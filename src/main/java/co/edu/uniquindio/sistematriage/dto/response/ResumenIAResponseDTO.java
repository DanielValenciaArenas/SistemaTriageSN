package co.edu.uniquindio.sistematriage.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIAResponseDTO {
    private String resumen;
    private String generadoPor;
}