package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void saveAndQueryByCorreoAndRolActivo() {
        Usuario activo = Usuario.builder()
                .nombre("María Pérez")
                .correo("maria.perez@example.com")
                .activo(true)
                .rol(RolUsuario.ADMINISTRATIVO)
                .build();

        Usuario inactivo = Usuario.builder()
                .nombre("Juan Gómez")
                .correo("juan.gomez@example.com")
                .activo(false)
                .rol(RolUsuario.ADMINISTRATIVO)
                .build();

        usuarioRepository.save(activo);
        usuarioRepository.save(inactivo);

        assertThat(usuarioRepository.existsByCorreo("maria.perez@example.com")).isTrue();
        assertThat(usuarioRepository.findByCorreo("maria.perez@example.com")).contains(activo);

        List<Usuario> administradoresActivos = usuarioRepository.findByRolAndActivoTrue(RolUsuario.ADMINISTRATIVO);
        assertThat(administradoresActivos).containsExactly(activo);

        List<Usuario> activos = usuarioRepository.findByActivoTrue();
        assertThat(activos).hasSize(1).contains(activo);

        List<Usuario> administradores = usuarioRepository.findByRol(RolUsuario.ADMINISTRATIVO);
        assertThat(administradores).hasSize(2).containsExactlyInAnyOrder(activo, inactivo);
    }
}
