package co.edu.uniquindio.sistematriage.dto.request;

import co.edu.uniquindio.sistematriage.domain.enums.Canal;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudRegistroDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    private Canal canalOrigen;

    @NotBlank
    private String idSolicitante;

    @NotNull
    private TipoSolicitud tipoSolicitud;

}
