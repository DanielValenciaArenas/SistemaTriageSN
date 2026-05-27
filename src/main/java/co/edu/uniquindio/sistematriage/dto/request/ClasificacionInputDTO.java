package co.edu.uniquindio.sistematriage.dto.request;

import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClasificacionInputDTO {
    @NotNull
    private TipoSolicitud tipo;

}
