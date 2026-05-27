package co.edu.uniquindio.sistematriage.mapper;

import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.dto.response.SolicitudResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de transformar entidades del dominio en DTOs de respuesta.
 */
@Component
public class SolicitudMapper {

    /*
     * Convierte una entidad {Solicitud} en su representación de respuesta HTTP.
     * Extrae únicamente los campos necesarios para el cliente, incluyendo
     * los IDs de solicitante y responsable sin exponer los objetos {Usuario} completos.
     * @param s entidad Solicitud obtenida desde la base de datos
     * @return DTO con los datos listos para serializar como JSON
     */
    public SolicitudResponseDTO toResponse(Solicitud s){
        return SolicitudResponseDTO.builder()
                .id(s.getIdSolicitud())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .tipoSolicitud(s.getTipoSolicitud())
                .canalOrigen(s.getCanalOrigen())
                .solicitanteId(s.getSolicitante() != null
                        ? s.getSolicitante().getIdUsuario().toString() : null)
                .estado(s.getEstado())
                .prioridad(s.getPrioridad())
                .justificacionPrioridad((s.getJustificacionPrioridad()))
                .responsableId(s.getResponsable() != null
                        ? s.getResponsable().getIdUsuario().toString() : null)
                .fechaRegistro(s.getFechaRegistro())
                .fechaActualizacion(s.getFechaActualizacion())
                .fechaCierre(s.getFechaCierre())
                .build();
    }
}
