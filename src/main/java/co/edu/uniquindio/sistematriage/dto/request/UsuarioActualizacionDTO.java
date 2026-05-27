package co.edu.uniquindio.sistematriage.dto.request;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioActualizacionDTO {

    private String nombre;

    @Email
    private String correo;

    private RolUsuario rol;
}