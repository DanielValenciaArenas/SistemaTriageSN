package co.edu.uniquindio.sistematriage.mapper;

import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.dto.response.HistorialAccionDTO;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de transformar entidades {HistorialSolicitud}
 * en DTOs de respuesta para el cliente.
 */
@Component
public class HistorialMapper {

    /*
     * Convierte un registro de historial en su representación HTTP.
     * Si el usuario asociado a la acción es nulo (acción del sistema),
     * se asigna "Sistema" como nombre por defecto.
     * @param h entidad HistorialSolicitud desde la base de datos
     * @return DTO con fecha, actor, acción y estados de la transición
     */
    public HistorialAccionDTO toDTO(HistorialSolicitud h) {
        return HistorialAccionDTO.builder()
                .fecha(h.getFechaHoraAccion())
                .usuario(h.getUsuario() != null ? h.getUsuario().getNombre() : "Sistema")
                .accion(h.getAccion())
                .estadoAnterior(h.getEstadoAnterior())
                .estadoNuevo(h.getEstadoNuevo())
                .observacion(h.getObservacion())
                .build();
    }

}
