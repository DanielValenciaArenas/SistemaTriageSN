package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.sistematriage.mapper.UsuarioMapper;
import co.edu.uniquindio.sistematriage.services.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @Test
    @DisplayName("Debe registrar un usuario correctamente y retornar HTTP 201")
    void shouldCreateUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .idUsuario(id)
                .nombre("Karen")
                .correo("karen@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .build();

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Karen")
                .correo("karen@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        String body = """
                {
                  "nombre": "Karen",
                  "correo": "karen@uq.edu.co",
                  "rol": "ESTUDIANTE"
                }
                """;

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(dto);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nombre").value("Karen"))
                .andExpect(jsonPath("$.correo").value("karen@uq.edu.co"));
    }

    @Test
    @DisplayName("Debe listar todos los usuarios cuando no se envía filtro")
    void shouldReturnAllUsersWhenNoFilterProvided() throws Exception {
        UUID id = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .idUsuario(id)
                .nombre("Carlos")
                .correo("carlos@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .build();

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Carlos")
                .correo("carlos@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(dto);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Carlos"));
    }

    @Test
    @DisplayName("Debe filtrar usuarios por rol correctamente")
    void shouldFilterUsersByRole() throws Exception {
        UUID id = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .idUsuario(id)
                .nombre("Admin")
                .correo("admin@uq.edu.co")
                .rol(RolUsuario.ADMINISTRATIVO)
                .activo(true)
                .build();

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Admin")
                .correo("admin@uq.edu.co")
                .rol(RolUsuario.ADMINISTRATIVO)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioService.listarUsuariosPorRol(RolUsuario.ADMINISTRATIVO))
                .thenReturn(List.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(dto);

        mockMvc.perform(get("/usuarios").param("rol", "ADMINISTRATIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rol").value("ADMINISTRATIVO"));
    }

    @Test
    @DisplayName("Debe obtener un usuario existente por su ID")
    void shouldReturnUserById() throws Exception {
        UUID id = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .idUsuario(id)
                .nombre("Laura")
                .correo("laura@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .build();

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Laura")
                .correo("laura@uq.edu.co")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(dto);

        mockMvc.perform(get("/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura"));
    }

    @Test
    @DisplayName("Debe actualizar correctamente los datos de un usuario")
    void shouldUpdateUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        Usuario actualizado = Usuario.builder()
                .idUsuario(id)
                .nombre("Karen Lara")
                .correo("karen.lara@uq.edu.co")
                .rol(RolUsuario.ADMINISTRATIVO)
                .activo(true)
                .build();

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Karen Lara")
                .correo("karen.lara@uq.edu.co")
                .rol(RolUsuario.ADMINISTRATIVO)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        String body = """
                {
                  "nombre": "Karen Lara",
                  "correo": "karen.lara@uq.edu.co",
                  "rol": "ADMINISTRATIVO"
                }
                """;

        when(usuarioService.actualizarUsuario(eq(id), any(Usuario.class))).thenReturn(actualizado);
        when(usuarioMapper.toResponse(actualizado)).thenReturn(dto);

        mockMvc.perform(put("/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Karen Lara"));
    }

    @Test
    @DisplayName("Debe eliminar lógicamente un usuario y retornar HTTP 204")
    void shouldPerformLogicalDelete() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(usuarioService).eliminarUsuario(id);

        mockMvc.perform(delete("/usuarios/{id}", id))
                .andExpect(status().isNoContent());
    }
}