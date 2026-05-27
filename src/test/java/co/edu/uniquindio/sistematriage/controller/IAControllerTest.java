package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.dto.request.ResumenIARequestDTO;
import co.edu.uniquindio.sistematriage.dto.request.SugerenciaIAInputDTO;
import co.edu.uniquindio.sistematriage.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.sistematriage.dto.response.SugerenciaIAOutputDTO;
import co.edu.uniquindio.sistematriage.services.IAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IAController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAService iaService;

    @Test
    @DisplayName("Debe sugerir clasificación correctamente")
    void shouldSuggestClasificacionSuccessfully() throws Exception {
        SugerenciaIAInputDTO input = new SugerenciaIAInputDTO();
        input.setDescripcion("Necesito homologar una materia urgentemente");

        SugerenciaIAOutputDTO output = new SugerenciaIAOutputDTO(
                TipoSolicitud.HOMOLOGACION,
                Prioridad.ALTA,
                0.95f,
                "La descripción contiene palabras asociadas a homologación y urgencia"
        );

        when(iaService.sugerirClasificacion(input.getDescripcion())).thenReturn(output);

        mockMvc.perform(post("/ia/sugerencias/clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoSugerido").value("HOMOLOGACION"))
                .andExpect(jsonPath("$.prioridadSugerida").value("ALTA"))
                .andExpect(jsonPath("$.justificacion")
                        .value("La descripción contiene palabras asociadas a homologación y urgencia"));
    }

    @Test
    @DisplayName("Debe retornar 503 cuando falle sugerencia de clasificación")
    void shouldReturn503WhenClasificacionFails() throws Exception {
        SugerenciaIAInputDTO input = new SugerenciaIAInputDTO();
        input.setDescripcion("Necesito ayuda con una solicitud");

        when(iaService.sugerirClasificacion(input.getDescripcion()))
                .thenThrow(new RuntimeException("Servicio IA no disponible"));

        mockMvc.perform(post("/ia/sugerencias/clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Debe generar resumen correctamente")
    void shouldGenerateResumenSuccessfully() throws Exception {
        UUID solicitudId = UUID.randomUUID();

        ResumenIARequestDTO input = new ResumenIARequestDTO();
        input.setSolicitudId(solicitudId);

        ResumenIAResponseDTO output = new ResumenIAResponseDTO(
                "La solicitud fue registrada, clasificada y priorizada correctamente.",
                "OpenAI"
        );

        when(iaService.generarResumen(solicitudId)).thenReturn(output);

        mockMvc.perform(post("/ia/resumen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen")
                        .value("La solicitud fue registrada, clasificada y priorizada correctamente."))
                .andExpect(jsonPath("$.generadoPor").value("OpenAI"));
    }

    @Test
    @DisplayName("Debe retornar 503 cuando falle la generación de resumen")
    void shouldReturn503WhenResumenFails() throws Exception {
        UUID solicitudId = UUID.randomUUID();

        ResumenIARequestDTO input = new ResumenIARequestDTO();
        input.setSolicitudId(solicitudId);

        when(iaService.generarResumen(solicitudId))
                .thenThrow(new RuntimeException("Proveedor IA caído"));

        mockMvc.perform(post("/ia/resumen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(""));
    }
}