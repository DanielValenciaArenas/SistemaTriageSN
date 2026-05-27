package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.repository.HistorialSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class HistorialSolicitudService {

    private final HistorialSolicitudRepository historialSolicitudRepository;

    /*
     * Constructor utilizado para la "Inyección de Dependencias".
     * La anotación @Autowired instruye a Spring Boot para que provea automáticamente
     * la instancia de HistorialSolicitudRepository cuando se crea este servicio.
     * Esto permite interactuar con la base de datos sin crear el repositorio manualmente.
     */
    @Autowired
    public HistorialSolicitudService(HistorialSolicitudRepository historialSolicitudRepository) {
        this.historialSolicitudRepository = historialSolicitudRepository;
    }

    /*
     * Registra cada cambio importante en el ciclo de vida de la solicitud.
     * Regla de negocio: "Cada cambio debe dejar historial" y registrar "fecha,
     * usuario, acción, estado anterior, estado nuevo, observación".
     */
    @Transactional
    public HistorialSolicitud registrarCambio(Solicitud solicitud, Usuario usuario, String accion, String observacion,
            Estado estadoAnterior, Estado estadoNuevo) {
        HistorialSolicitud historial = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .usuario(usuario)
                .accion(accion)
                .observacion(observacion)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .fechaHoraAccion(LocalDateTime.now())
                .build();

        return historialSolicitudRepository.save(historial);
    }
}
