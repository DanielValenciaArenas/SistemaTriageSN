package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID>,
        JpaSpecificationExecutor<Solicitud> {

    List<Solicitud> findByEstado(Estado estado);
    List<Solicitud> findByTipoSolicitud(TipoSolicitud tipoSolicitud);
    List<Solicitud> findByPrioridad(Prioridad prioridad);
    List<Solicitud> findByResponsable_IdUsuario(UUID idUsuario);

    @Query("""
        SELECT s FROM Solicitud s
        WHERE (:estado IS NULL OR s.estado = :estado)
          AND (:tipo IS NULL OR s.tipoSolicitud = :tipo)
          AND (:prioridad IS NULL OR s.prioridad = :prioridad)
          AND (:responsableId IS NULL OR s.responsable.idUsuario = :responsableId)
    """)

    List<Solicitud> buscarConFiltros(
            @Param("estado") Estado estado,
            @Param("tipo") TipoSolicitud tipo,
            @Param("prioridad") Prioridad prioridad,
            @Param("responsableId") UUID responsableId
    );

    List<Solicitud> findBySolicitante_IdUsuario(UUID idUsuario);
}