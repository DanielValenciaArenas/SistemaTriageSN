package co.edu.uniquindio.sistematriage.mapper;

import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de transformar entidades Usuario
 * en DTOs de respuesta para el cliente.
 */
@Component
public class UsuarioMapper {

    /*
     * Convierte una entidad Usuario en su representación HTTP.
     * No expone listas de solicitudes para evitar referencias circulares.
     * @param u entidad Usuario desde la base de datos
     * @return DTO con los datos listos para serializar como JSON
     */
    public UsuarioResponseDTO toResponse(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getIdUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .activo(u.isActivo())
                .fechaRegistro(u.getFechaRegistro())
                .build();
    }
}