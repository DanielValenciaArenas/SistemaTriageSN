package co.edu.uniquindio.sistematriage.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email
    @NotBlank
    private String correo;

    @NotBlank
    private String password;
}