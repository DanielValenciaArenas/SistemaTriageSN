package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.dto.request.ResumenIARequestDTO;
import co.edu.uniquindio.sistematriage.dto.request.SugerenciaIAInputDTO;
import co.edu.uniquindio.sistematriage.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.sistematriage.dto.response.SugerenciaIAOutputDTO;
import co.edu.uniquindio.sistematriage.services.IAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la integración con IA del Sistema de Triage.
 *
 * El try-catch garantiza que un fallo de IA retorna 503 (Service Unavailable)
 * en vez de 500. El frontend puede mostrar "Servicio de IA no disponible" y
 * el usuario puede clasificar manualmente — sin romper el flujo (RF-11).
 *
 */
@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
@Tag(name = "IA", description = "Sugerencias automáticas y resúmenes con inteligencia artificial")
public class IAController {

    private final IAService iaService;

    /**
     * RF-10: Sugiere TipoSolicitud y Prioridad a partir de la descripción ingresada.
     * Las sugerencias deben ser confirmadas o ajustadas por el usuario humano.
     */
    @PostMapping("/sugerencias/clasificacion")
    @Operation(summary = "Sugerir tipo de solicitud y prioridad a partir de la descripción (RF-10)")
    public ResponseEntity<SugerenciaIAOutputDTO> sugerirClasificacion(
            @Valid @RequestBody SugerenciaIAInputDTO request) {
        try {
            return ResponseEntity.ok(iaService.sugerirClasificacion(request.getDescripcion()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }

    /**
     * RF-09: Genera un resumen textual del estado e historial de una solicitud.
     */
    @PostMapping("/resumen")
    @Operation(summary = "Generar resumen de una solicitud con su historial (RF-09)")
    public ResponseEntity<ResumenIAResponseDTO> generarResumen(
            @Valid @RequestBody ResumenIARequestDTO request) {
        try {
            return ResponseEntity.ok(iaService.generarResumen(request.getSolicitudId()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }
}