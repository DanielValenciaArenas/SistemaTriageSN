package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.sistematriage.dto.response.SugerenciaIAOutputDTO;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import java.util.UUID;

/**
 * Implementación del servicio de IA usando Spring AI + OpenAI.
 *
 * ChatClient es la abstracción de Spring AI para interactuar con LLMs.
 * Se configura automáticamente cuando spring-ai-openai-spring-boot-starter
 * está en el classpath y la API key está configurada (OPENAI_API_KEY).
 *
 * Si la llamada a OpenAI falla (timeout, clave inválida, rate limit),
 * se delega automáticamente al IAServiceFallbackImpl.
 *
 */
@Slf4j
@RequiredArgsConstructor
public class IAServiceOpenAIImpl implements IAService {

    private final ChatClient chatClient;
    private final SolicitudRepository solicitudRepository;
    private final IAServiceFallbackImpl fallback;

    /**
     * Prompt para sugerir TipoSolicitud y Prioridad a partir de la descripción.
     * Responde SOLO con JSON — sin markdown, sin texto adicional.
     */
    private static final String PROMPT_CLASIFICACION = """
            Eres un asistente académico del Programa de Ingeniería de Sistemas.
            
            Tipos de solicitud posibles: REGISTRO_ASIGNATURA, HOMOLOGACION, CANCELACION, CUPO, CONSULTA
            Prioridades posibles: BAJA, MEDIA, ALTA
            
            Analiza la siguiente descripción de solicitud académica y responde ÚNICAMENTE con JSON
            (sin markdown, sin comillas extra, sin texto adicional):
            {"tipoSugerido":"NOMBRE","prioridadSugerida":"NOMBRE","confianza":0.85,"justificacion":"..."}
            
            Descripción: %s
            """;

    /**
     * Prompt para generar un resumen ejecutivo de la solicitud con su historial.
     */
    private static final String PROMPT_RESUMEN = """
            Genera un resumen ejecutivo en español de esta solicitud académica universitaria:
            %s
            Incluye: estado actual, puntos clave del historial y próximos pasos recomendados.
            Sé conciso y directo. Máximo 150 palabras.
            """;

    @Override
    public SugerenciaIAOutputDTO sugerirClasificacion(String descripcion) {
        log.info("Sugerencia de clasificación con OpenAI (Spring AI)");
        try {
            String respuesta = chatClient.prompt()
                    .user(String.format(PROMPT_CLASIFICACION, descripcion))
                    .call()
                    .content();

            return parsearSugerencia(respuesta);
        } catch (Exception e) {
            log.warn("OpenAI falló, usando fallback: {}", e.getMessage());
            return fallback.sugerirClasificacion(descripcion);
        }
    }

    @Override
    public ResumenIAResponseDTO generarResumen(UUID solicitudId) {
        log.info("Generando resumen con OpenAI para solicitud: {}", solicitudId);
        try {
            Solicitud solicitud = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Solicitud no encontrada con ID: " + solicitudId));

            String datos = construirDatos(solicitud);
            String resumen = chatClient.prompt()
                    .user(String.format(PROMPT_RESUMEN, datos))
                    .call()
                    .content();

            return ResumenIAResponseDTO.builder()
                    .resumen(resumen)
                    .generadoPor("OpenAI (Spring AI)")
                    .build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("OpenAI falló para resumen, usando fallback: {}", e.getMessage());
            return fallback.generarResumen(solicitudId);
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private SugerenciaIAOutputDTO parsearSugerencia(String respuesta) {
        try {
            String json = respuesta.trim()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "");

            String tipo = extraerCampo(json, "tipoSugerido");
            String pri  = extraerCampo(json, "prioridadSugerida");
            String conf = extraerCampo(json, "confianza");
            String just = extraerCampo(json, "justificacion");

            TipoSolicitud tipoSolicitud;
            try { tipoSolicitud = TipoSolicitud.valueOf(tipo); }
            catch (Exception e) { tipoSolicitud = TipoSolicitud.CONSULTA; }

            Prioridad prioridad;
            try { prioridad = Prioridad.valueOf(pri); }
            catch (Exception e) { prioridad = Prioridad.MEDIA; }

            float confianza;
            try { confianza = Float.parseFloat(conf); }
            catch (Exception e) { confianza = 0.70f; }

            return SugerenciaIAOutputDTO.builder()
                    .tipoSugerido(tipoSolicitud)
                    .prioridadSugerida(prioridad)
                    .confianza(confianza)
                    .justificacion(just != null ? just : "Clasificación sugerida por IA")
                    .build();

        } catch (Exception e) {
            log.warn("Error parseando respuesta de OpenAI: {}", e.getMessage());
            return SugerenciaIAOutputDTO.builder()
                    .tipoSugerido(TipoSolicitud.CONSULTA)
                    .prioridadSugerida(Prioridad.MEDIA)
                    .confianza(0.50f)
                    .justificacion("No se pudo interpretar la respuesta del modelo.")
                    .build();
        }
    }

    private String extraerCampo(String json, String campo) {
        try {
            int idx = json.indexOf("\"" + campo + "\"");
            if (idx == -1) return null;
            int colon = json.indexOf(":", idx);
            if (colon == -1) return null;
            String after = json.substring(colon + 1).trim();
            if (after.startsWith("\"")) {
                int end = after.indexOf("\"", 1);
                return end > 0 ? after.substring(1, end) : null;
            } else {
                int end = after.indexOf(",");
                if (end == -1) end = after.indexOf("}");
                return end > 0 ? after.substring(0, end).trim() : after.trim();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String construirDatos(Solicitud solicitud) {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(solicitud.getNombre()).append("\n");
        sb.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");
        sb.append("Tipo: ").append(solicitud.getTipoSolicitud()).append("\n");
        sb.append("Estado: ").append(solicitud.getEstado()).append("\n");
        sb.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
        sb.append("Canal de origen: ").append(solicitud.getCanalOrigen()).append("\n");
        if (solicitud.getSolicitante() != null)
            sb.append("Solicitante: ").append(solicitud.getSolicitante().getNombre()).append("\n");
        if (solicitud.getResponsable() != null)
            sb.append("Responsable: ").append(solicitud.getResponsable().getNombre()).append("\n");
        sb.append("\nHistorial:\n");
        for (HistorialSolicitud h : solicitud.getHistoriales()) {
            sb.append("  - ").append(h.getFechaHoraAccion())
                    .append(" | ").append(h.getAccion());
            if (h.getEstadoAnterior() != null && h.getEstadoNuevo() != null) {
                sb.append(" [").append(h.getEstadoAnterior())
                        .append(" → ").append(h.getEstadoNuevo()).append("]");
            }
            if (h.getObservacion() != null) sb.append(" | ").append(h.getObservacion());
            sb.append("\n");
        }
        return sb.toString();
    }
}