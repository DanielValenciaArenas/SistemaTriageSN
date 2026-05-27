package co.edu.uniquindio.sistematriage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIARequestDTO {

    @NotNull(message = "El ID de la solicitud es obligatorio")
    private UUID solicitudId;
}