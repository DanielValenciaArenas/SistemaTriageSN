package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, UUID> {

    List<HistorialSolicitud> findBySolicitud_IdSolicitudOrderByFechaHoraAccionAsc (UUID idSolicitud);
}
