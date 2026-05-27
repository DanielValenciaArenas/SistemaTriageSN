package co.edu.uniquindio.sistematriage.dto.request;

import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriorizacionInputDTO {

    @NotNull
    private Prioridad prioridad;

    @NotBlank
    private String justificacion;
}