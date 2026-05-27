package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.request.ClasificacionInputDTO;
import co.edu.uniquindio.sistematriage.dto.response.HistorialAccionDTO;
import co.edu.uniquindio.sistematriage.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.sistematriage.mapper.HistorialMapper;
import co.edu.uniquindio.sistematriage.mapper.SolicitudMapper;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import co.edu.uniquindio.sistematriage.services.SolicitudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SolicitudController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private SolicitudService solicitudService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private SolicitudMapper solicitudMapper;

    @MockBean
    private HistorialMapper historialMapper;

    @Test
    @DisplayName("Debe registrar una solicitud correctamente")
    void shouldRegisterSolicitud() throws Exception {

        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .idUsuario(usuarioId)
                .build();

        Solicitud solicitud = Solicitud.builder()
                .nombre("Solicitud prueba")
                .descripcion("Descripción")
                .build();

        SolicitudResponseDTO dto = SolicitudResponseDTO.builder()
                .nombre("Solicitud prueba")
                .descripcion("Descripción")
                .build();

        String body = """
    {
      "nombre": "Solicitud prueba",
      "descripcion": "Descripción",
      "canalOrigen": "PORTAL_WEB",
      "idSolicitante": "%s"
    }
    """.formatted(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(solicitudService.registrarSolicitud(any(Solicitud.class))).thenReturn(solicitud);
        when(solicitudMapper.toResponse(solicitud)).thenReturn(dto);

        mockMvc.perform(post("/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Solicitud prueba"));
    }

    @Test
    @DisplayName("Debe listar solicitudes correctamente")
    void shouldListSolicitudes() throws Exception {

        Solicitud solicitud = Solicitud.builder().nombre("Solicitud 1").build();

        SolicitudResponseDTO dto = SolicitudResponseDTO.builder()
                .nombre("Solicitud 1")
                .build();

        when(solicitudService.listarSolicitudes(null, null, null, null))
                .thenReturn(List.of(solicitud));
        when(solicitudMapper.toResponse(solicitud)).thenReturn(dto);

        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Solicitud 1"));
    }

    @Test
    @DisplayName("Debe obtener solicitud por ID")
    void shouldGetSolicitudById() throws Exception {

        UUID id = UUID.randomUUID();

        Solicitud solicitud = Solicitud.builder().nombre("Solicitud X").build();

        SolicitudResponseDTO dto = SolicitudResponseDTO.builder()
                .nombre("Solicitud X")
                .build();

        when(solicitudService.obtenerSolicitudPorId(id)).thenReturn(solicitud);
        when(solicitudMapper.toResponse(solicitud)).thenReturn(dto);

        mockMvc.perform(get("/solicitudes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Solicitud X"));
    }

    @Test
    @DisplayName("Debe clasificar una solicitud correctamente")
    void shouldClasificarSolicitud() throws Exception {

        UUID solicitudId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();

        Usuario actor = Usuario.builder()
                .idUsuario(actorId)
                .build();

        Solicitud solicitud = Solicitud.builder().build();
        SolicitudResponseDTO dto = SolicitudResponseDTO.builder().build();

        ClasificacionInputDTO input = new ClasificacionInputDTO();
        input.setTipo(TipoSolicitud.HOMOLOGACION);

        when(usuarioRepository.findById(actorId)).thenReturn(Optional.of(actor));
        when(solicitudService.clasificarSolicitud(any(), any(), any()))
                .thenReturn(solicitud);
        when(solicitudMapper.toResponse(solicitud)).thenReturn(dto);

        mockMvc.perform(patch("/solicitudes/{id}/clasificar", solicitudId)
                        .param("actorId", actorId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe obtener historial de solicitud")
    void shouldGetHistorial() throws Exception {

        UUID solicitudId = UUID.randomUUID();

        when(solicitudService.consultarHistorial(solicitudId))
                .thenReturn(List.of());

        mockMvc.perform(get("/solicitudes/{id}/historial", solicitudId))
                .andExpect(status().isOk());
    }
}