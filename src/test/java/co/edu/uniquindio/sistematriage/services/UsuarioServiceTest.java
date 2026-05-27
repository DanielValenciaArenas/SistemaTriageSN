package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .nombre("Natalia López")
                .correo("natalia@example.com")
                .rol(RolUsuario.ESTUDIANTE)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarUsuarioExitoso() {
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario guardado = usuarioService.registrarUsuario(usuario);

        assertThat(guardado.getCorreo()).isEqualTo(usuario.getCorreo());
        assertThat(guardado.isActivo()).isTrue();
    }

    @Test
    void registrarUsuarioCorreoDuplicadoLanzaExcepcion() {
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.registrarUsuario(usuario))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("correo ya está registrado");
    }

    @Test
    void actualizarUsuarioCambiaNombreYCambiaCorreo() {
        Usuario actualizacion = Usuario.builder()
                .nombre("Natalia M.")
                .correo("natalia.nuevo@example.com")
                .rol(RolUsuario.ADMINISTRATIVO)
                .build();

        when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCorreo(actualizacion.getCorreo())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.actualizarUsuario(usuario.getIdUsuario(), actualizacion);

        assertThat(actualizado.getNombre()).isEqualTo("Natalia M.");
        assertThat(actualizado.getCorreo()).isEqualTo("natalia.nuevo@example.com");
        assertThat(actualizado.getRol()).isEqualTo(RolUsuario.ADMINISTRATIVO);
    }

    @Test
    void actualizarUsuarioCorreoDuplicadoLanzaExcepcion() {
        Usuario otro = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .nombre("Otro Usuario")
                .correo("nuevo-correo@example.com")
                .activo(true)
                .rol(RolUsuario.ESTUDIANTE)
                .build();

        Usuario cambios = Usuario.builder().correo(otro.getCorreo()).build();

        when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCorreo(otro.getCorreo())).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> usuarioService.actualizarUsuario(usuario.getIdUsuario(), cambios))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("correo ya está registrado por otro usuario");
    }

    @Test
    void eliminarUsuarioMarcaComoInactivo() {
        when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.eliminarUsuario(usuario.getIdUsuario());

        assertThat(usuario.isActivo()).isFalse();
    }

    @Test
    void listarUsuariosPorRolDevuelveActivos() {
        when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.ESTUDIANTE))
                .thenReturn(List.of(usuario));

        List<Usuario> resultados = usuarioService.listarUsuariosPorRol(RolUsuario.ESTUDIANTE);

        assertThat(resultados).containsExactly(usuario);
    }

    @Test
    void obtenerUsuarioPorIdNoEncontradoLanzaExcepcion() {
        when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerUsuarioPorId(usuario.getIdUsuario()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
