package co.edu.uniquindio.sistematriage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String rol;
    private String userId;
    private String nombre;
}