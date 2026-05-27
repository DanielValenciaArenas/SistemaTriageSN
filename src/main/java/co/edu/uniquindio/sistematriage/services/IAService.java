package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.sistematriage.dto.response.SugerenciaIAOutputDTO;

import java.util.UUID;

/**
 * Contrato del servicio de IA para el Sistema de Triage.
 * Tiene dos implementaciones:
 *   - {@link IAServiceFallbackImpl}: basada en reglas de palabras clave (sin red).
 *   - {@link IAServiceOpenAIImpl}: usa Spring AI + OpenAI (requiere API key).
 *
 * Cumple RF-10 (sugerencia automática de clasificación) y RF-09 (resumen de solicitud).
 * RF-11 garantizado: el fallback opera sin ninguna dependencia externa.
 */
public interface IAService {
    SugerenciaIAOutputDTO sugerirClasificacion(String descripcion);
    ResumenIAResponseDTO generarResumen(UUID solicitudId);
}