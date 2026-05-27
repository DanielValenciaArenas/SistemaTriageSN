package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Constructor utilizado para la "Inyección de Dependencias".
     * La anotación @Autowired le indica a Spring Boot que debe proporcionar
     * automáticamente la instancia y conexión requerida de UsuarioRepository
     * cuando se inicializa este servicio.
     */
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Registra un nuevo usuario en el sistema.
     * Valida como regla de negocio que el correo no esté ya registrado.
     * Asigna automáticamente la fecha de registro actual y lo marca como 'activo'.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new BusinessRuleException("El correo ya está registrado");
        }

        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    /*
     * Obtiene una lista completa con todos los usuarios registrados,
     * sin importar su rol o si están activos o inactivos.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    /*
     * Consulta un usuario específico por su identificador único (UUID).
     * Si el ID no existe en la base de datos, arroja un error
     * (ResourceNotFoundException).
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    /*
     * Actualiza la información de un usuario existente.
     * Realiza una validación importante: si se intenta cambiar el correo,
     * verifica que el nuevo correo no pertenezca ya a otra persona.
     */
    public Usuario actualizarUsuario(UUID id, Usuario datosActualizados) {
        Usuario usuario = obtenerUsuarioPorId(id);

        if (datosActualizados.getNombre() != null) {
            usuario.setNombre(datosActualizados.getNombre());
        }
        if (datosActualizados.getCorreo() != null) {
            if (!usuario.getCorreo().equals(datosActualizados.getCorreo()) &&
                    usuarioRepository.findByCorreo(datosActualizados.getCorreo()).isPresent()) {
                throw new BusinessRuleException("El correo ya está registrado por otro usuario");
            }
            usuario.setCorreo(datosActualizados.getCorreo());
        }
        if (datosActualizados.getRol() != null) {
            usuario.setRol(datosActualizados.getRol());
        }

        return usuarioRepository.save(usuario);
    }

    /*
     * Realiza una "eliminación lógica" (Soft Delete) del usuario.
     * En lugar de borrar el registro físicamente de la base de datos,
     * simplemente cambia su bandera de 'activo' a falso.
     */
    public void eliminarUsuario(UUID id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /*
     * Busca y devuelve una lista de usuarios filtrados por su rol
     * (solo ESTUDIANTE o ADMINISTRATIVO) y asegura que solo
     * traiga a los que están activos actualmente.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorRol(RolUsuario rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol);
    }
}