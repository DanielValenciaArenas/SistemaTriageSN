package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    List<Usuario> findByRol(RolUsuario rolUsuario);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByRolAndActivoTrue(RolUsuario rolUsuario);
    boolean existsByCorreo(String correo);
    Optional<Usuario> findByCorreo(String correo);
}
