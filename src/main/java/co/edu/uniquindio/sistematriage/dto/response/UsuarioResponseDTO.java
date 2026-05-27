package co.edu.uniquindio.sistematriage.dto.response;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UsuarioResponseDTO {
    private UUID id;
    private String nombre;
    private String correo;
    private RolUsuario rol;
    private boolean activo;
    private LocalDateTime fechaRegistro;
}