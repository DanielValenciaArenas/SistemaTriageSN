package co.edu.uniquindio.sistematriage.dto.request;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRegistroDTO {

    @NotBlank
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank
    @Email
    private String correo;

    @NotNull
    private RolUsuario rol;
}