package co.edu.uniquindio.sistematriage.dto.response;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistorialAccionDTO {
    private LocalDateTime fecha;
    private String usuario;
    private String accion;
    private Estado estadoAnterior;
    private Estado estadoNuevo;
    private String observacion;

}
