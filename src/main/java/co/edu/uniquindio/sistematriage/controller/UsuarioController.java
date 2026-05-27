package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.request.UsuarioRegistroDTO;
import co.edu.uniquindio.sistematriage.dto.request.UsuarioActualizacionDTO;
import co.edu.uniquindio.sistematriage.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.mapper.UsuarioMapper;
import co.edu.uniquindio.sistematriage.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST que expone los endpoints de gestión de usuarios.
 * Permite registrar, consultar, actualizar y desactivar usuarios del sistema.
 *
 * Nota: En el Hito 3 la autenticación será gestionada mediante JWT.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /*
     * POST /usuarios
     * Registra un nuevo usuario en el sistema.
     * @param dto datos del usuario a registrar
     * @return usuario creado con HTTP 201
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody UsuarioRegistroDTO dto) {

        Usuario nuevo = Usuario.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .rol(dto.getRol())
                .password(dto.getPassword())
                .build();

        Usuario guardado = usuarioService.registrarUsuario(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioMapper.toResponse(guardado));
    }

    /*
     * GET /usuarios
     * Lista todos los usuarios, con filtro opcional por rol.
     * @param rol filtro opcional por rol (ESTUDIANTE o ADMINISTRATIVO)
     * @return lista de usuarios con HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar(
            @RequestParam(required = false) RolUsuario rol) {

        List<Usuario> usuarios = (rol != null)
                ? usuarioService.listarUsuariosPorRol(rol)
                : usuarioService.listarUsuarios();

        return ResponseEntity.ok(
                usuarios.stream().map(usuarioMapper::toResponse).toList()
        );
    }

    /*
     * GET /usuarios/{id}
     * Obtiene un usuario específico por su UUID.
     * @return usuario encontrado con HTTP 200, o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(
                usuarioMapper.toResponse(usuarioService.obtenerUsuarioPorId(id))
        );
    }

    /*
     * PUT /usuarios/{id}
     * Actualiza los datos de un usuario existente.
     * @return usuario actualizado con HTTP 200
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioActualizacionDTO dto) {

        Usuario datosActualizados = Usuario.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .rol(dto.getRol())
                .build();

        return ResponseEntity.ok(
                usuarioMapper.toResponse(usuarioService.actualizarUsuario(id, datosActualizados))
        );
    }

    /*
     * DELETE /usuarios/{id}
     * Realiza una eliminación lógica (soft delete) del usuario.
     * El usuario queda inactivo pero no se borra de la base de datos.
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}