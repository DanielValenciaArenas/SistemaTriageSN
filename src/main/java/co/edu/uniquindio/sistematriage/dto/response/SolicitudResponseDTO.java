package co.edu.uniquindio.sistematriage.dto.response;

import co.edu.uniquindio.sistematriage.domain.enums.Canal;
import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SolicitudResponseDTO {
    private UUID id;
    private String nombre;
    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private Canal canalOrigen;
    private String solicitanteId;
    private Estado estado;
    private Prioridad prioridad;
    private String justificacionPrioridad;
    private String responsableId;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCierre;

}
