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

import java.util.Map;
import java.util.UUID;

/**
 * Implementación de IA basada en reglas de palabras clave.
 * Funciona sin conexión a internet ni API key.
 *
 * Garantiza RF-11: el sistema opera correctamente sin la integración de LLMs.
 * Es además el fallback automático cuando OpenAI falla (timeout, cuota, etc.).
 */
@Slf4j
@RequiredArgsConstructor
public class IAServiceFallbackImpl implements IAService {

    private final SolicitudRepository solicitudRepository;

    // Palabras clave para detectar TipoSolicitud
    private static final Map<String, TipoSolicitud> KEYWORDS_TIPO = Map.of(
            "registro", TipoSolicitud.REGISTRO_ASIGNATURA,
            "inscripcion", TipoSolicitud.REGISTRO_ASIGNATURA,
            "inscripción", TipoSolicitud.REGISTRO_ASIGNATURA,
            "asignatura", TipoSolicitud.REGISTRO_ASIGNATURA,
            "homologacion", TipoSolicitud.HOMOLOGACION,
            "homologación", TipoSolicitud.HOMOLOGACION,
            "equivalencia", TipoSolicitud.HOMOLOGACION,
            "cancelacion", TipoSolicitud.CANCELACION,
            "cancelación", TipoSolicitud.CANCELACION,
            "retirar", TipoSolicitud.CANCELACION
    );

    private static final Map<String, TipoSolicitud> KEYWORDS_TIPO_2 = Map.of(
            "cupo", TipoSolicitud.CUPO,
            "cupos", TipoSolicitud.CUPO,
            "consulta", TipoSolicitud.CONSULTA,
            "informacion", TipoSolicitud.CONSULTA,
            "información", TipoSolicitud.CONSULTA,
            "pregunta", TipoSolicitud.CONSULTA
    );

    // Palabras clave para detectar Prioridad
    private static final Map<String, Prioridad> KEYWORDS_PRIORIDAD = Map.of(
            "urgente", Prioridad.ALTA,
            "urgencia", Prioridad.ALTA,
            "critico", Prioridad.ALTA,
            "crítico", Prioridad.ALTA,
            "grado", Prioridad.ALTA,
            "graduacion", Prioridad.ALTA,
            "graduación", Prioridad.ALTA,
            "plazo", Prioridad.ALTA,
            "vence", Prioridad.ALTA,
            "sancion", Prioridad.ALTA
    );

    @Override
    public SugerenciaIAOutputDTO sugerirClasificacion(String descripcion) {
        log.info("Sugerencia de clasificación por REGLAS (fallback)");
        String texto = descripcion.toLowerCase().trim();

        TipoSolicitud tipo = null;
        Prioridad prioridad = Prioridad.MEDIA;
        float confianza = 0.0f;
        StringBuilder justificacion = new StringBuilder();

        // Detectar tipo por keywords (mapa 1)
        for (Map.Entry<String, TipoSolicitud> entry : KEYWORDS_TIPO.entrySet()) {
            if (texto.contains(entry.getKey())) {
                tipo = entry.getValue();
                confianza = 0.70f;
                justificacion.append("Palabra clave '").append(entry.getKey())
                        .append("' sugiere ").append(entry.getValue()).append(". ");
                break;
            }
        }
        // Detectar tipo por keywords (mapa 2)
        if (tipo == null) {
            for (Map.Entry<String, TipoSolicitud> entry : KEYWORDS_TIPO_2.entrySet()) {
                if (texto.contains(entry.getKey())) {
                    tipo = entry.getValue();
                    confianza = 0.65f;
                    justificacion.append("Palabra clave '").append(entry.getKey())
                            .append("' sugiere ").append(entry.getValue()).append(". ");
                    break;
                }
            }
        }

        // Detectar prioridad
        for (Map.Entry<String, Prioridad> entry : KEYWORDS_PRIORIDAD.entrySet()) {
            if (texto.contains(entry.getKey())) {
                prioridad = entry.getValue();
                confianza = Math.min(confianza + 0.15f, 1.0f);
                justificacion.append("Palabra '").append(entry.getKey())
                        .append("' sugiere prioridad ").append(entry.getValue()).append(". ");
                break;
            }
        }

        // Default si no se detectó nada
        if (tipo == null) {
            tipo = TipoSolicitud.CONSULTA;
            confianza = 0.30f;
            justificacion.append("Sin palabras clave reconocidas. Se sugiere CONSULTA por defecto.");
        }

        return SugerenciaIAOutputDTO.builder()
                .tipoSugerido(tipo)
                .prioridadSugerida(prioridad)
                .confianza(confianza)
                .justificacion("[Reglas] " + justificacion.toString().trim())
                .build();
    }

    @Override
    public ResumenIAResponseDTO generarResumen(UUID solicitudId) {
        log.info("Resumen por REGLAS (fallback) para solicitud: {}", solicitudId);

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud no encontrada con ID: " + solicitudId));

        StringBuilder resumen = new StringBuilder();
        resumen.append("=== Resumen de Solicitud Académica ===\n");
        resumen.append("Nombre: ").append(solicitud.getNombre()).append("\n");
        resumen.append("Tipo: ").append(solicitud.getTipoSolicitud() != null
                ? solicitud.getTipoSolicitud() : "Sin clasificar").append("\n");
        resumen.append("Estado actual: ").append(solicitud.getEstado()).append("\n");
        resumen.append("Prioridad: ").append(solicitud.getPrioridad() != null
                ? solicitud.getPrioridad() : "Sin asignar").append("\n");
        resumen.append("Canal de origen: ").append(solicitud.getCanalOrigen()).append("\n");
        resumen.append("Solicitante: ").append(
                solicitud.getSolicitante() != null ? solicitud.getSolicitante().getNombre() : "N/A"
        ).append("\n");
        if (solicitud.getResponsable() != null) {
            resumen.append("Responsable asignado: ").append(solicitud.getResponsable().getNombre()).append("\n");
        }
        resumen.append("Fecha de registro: ").append(solicitud.getFechaRegistro()).append("\n");

        if (solicitud.getDescripcion() != null) {
            resumen.append("\nDescripción: ").append(solicitud.getDescripcion()).append("\n");
        }

        if (solicitud.getJustificacionPrioridad() != null) {
            resumen.append("Justificación de prioridad: ").append(solicitud.getJustificacionPrioridad()).append("\n");
        }

        resumen.append("\nHistorial (").append(solicitud.getHistoriales().size()).append(" eventos):\n");
        for (HistorialSolicitud h : solicitud.getHistoriales()) {
            resumen.append("  - ").append(h.getFechaHoraAccion())
                    .append(" | ").append(h.getAccion());
            if (h.getEstadoAnterior() != null && h.getEstadoNuevo() != null) {
                resumen.append(" [").append(h.getEstadoAnterior())
                        .append(" → ").append(h.getEstadoNuevo()).append("]");
            }
            if (h.getObservacion() != null) {
                resumen.append(" | ").append(h.getObservacion());
            }
            resumen.append("\n");
        }

        return ResumenIAResponseDTO.builder()
                .resumen(resumen.toString())
                .generadoPor("Fallback (reglas)")
                .build();
    }
}