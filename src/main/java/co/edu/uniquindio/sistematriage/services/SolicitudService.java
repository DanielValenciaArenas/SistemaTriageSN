package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.repository.SolicitudRepository;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final HistorialSolicitudService historialSolicitudService;
    private final UsuarioRepository usuarioRepository;

    /*
     * Constructor de la clase que Spring Boot utiliza para la "Inyección de Dependencias".
     * La anotación @Autowired le dice a Spring que, al crear el SolicitudService,
     * debe entregarle automáticamente las herramientas que necesita para funcionar
     * (los repositorios y el servicio de historial).
     */
    @Autowired
    public SolicitudService(SolicitudRepository solicitudRepository,
            HistorialSolicitudService historialSolicitudService,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.historialSolicitudService = historialSolicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    /*
     * Registra una nueva solicitud en el sistema.
     * Regla de negocio: "Al registrar: estado inicial REGISTRADA".
     * Se asignan fechas y se guarda en el historial automático.
     */
    public Solicitud registrarSolicitud(Solicitud solicitud) {
        if (solicitud.getSolicitante() == null) {
            throw new BusinessRuleException("La solicitud debe tener un solicitante asignado");
        }

        solicitud.setEstado(Estado.REGISTRADA);

        Solicitud saved = solicitudRepository.save(solicitud);

        historialSolicitudService.registrarCambio(saved, solicitud.getSolicitante(),
                "REGISTRO", "Solicitud registrada", null, Estado.REGISTRADA);

        return saved;
    }

    /*
     * Obtiene una lista con todas las solicitudes registradas en el sistema segun los parametros.
     * La anotación @Transactional(readOnly = true) le avisa a Spring Boot que
     * solo queremos "leer" datos de la base, lo que optimiza la memoria y velocidad.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> listarSolicitudes(Estado estado, TipoSolicitud tipo,
                                             Prioridad prioridad, UUID responsableId) {
        return solicitudRepository.buscarConFiltros(estado, tipo, prioridad, responsableId);
    }

    /*
     * Busca una solicitud específica mediante su identificador único (UUID).
     * Si no encuentra nada, lanza automáticamente un "ResourceNotFoundException"
     * (error de recurso no encontrado).
     * También utiliza "readOnly = true" porque es una operación de pura lectura.
     */
    @Transactional(readOnly = true)
    public Solicitud obtenerSolicitudPorId(UUID id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
    }

    /*
     * Aplica la clasificación a la solicitud.
     * Regla de negocio:
     * "Al clasificar: cambiar tipo y pasar a CLASIFICADA si aplica".
     * Solo cambia a CLASIFICADA si actualmente está REGISTRADA.
     */
    public Solicitud clasificarSolicitud(UUID id, TipoSolicitud nuevoTipo, Usuario actor) {
        Solicitud solicitud = obtenerSolicitudPorId(id);
        Estado estadoAnterior = solicitud.getEstado();

        solicitud.setTipoSolicitud(nuevoTipo);
        if (solicitud.getEstado() == Estado.REGISTRADA) {
            solicitud.setEstado(Estado.CLASIFICADA);
        }
        solicitud.setFechaActualizacion(LocalDateTime.now());

        Solicitud saved = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarCambio(saved, actor,
                "CLASIFICACION", "Cambio de tipo de solicitud a " + nuevoTipo,
                estadoAnterior, saved.getEstado());

        return saved;
    }

    /*
     * Asigna una prioridad a la solicitud.
     * Regla de negocio: "Al priorizar: guardar prioridad y justificación".
     * También registra el evento en el historial automático.
     */
    public Solicitud priorizarSolicitud(UUID id, Prioridad prioridad, String justificacion, Usuario actor) {
        Solicitud solicitud = obtenerSolicitudPorId(id);

        if (solicitud.getEstado() == Estado.CERRADA) {
            throw new BusinessRuleException("No se puede priorizar una solicitud cerrada");
        }

        Estado estadoActual = solicitud.getEstado();
        solicitud.setPrioridad(prioridad);
        solicitud.setJustificacionPrioridad(justificacion);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        Solicitud saved = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarCambio(saved, actor,
                "PRIORIZACION", "Priorizada como " + prioridad + ". " + justificacion,
                estadoActual, estadoActual);

        return saved;
    }

    /*
     * Asigna un usuario como responsable de la solicitud.
     * Regla de negocio: "Al asignar: validar usuario activo y existente".
     * Falla y lanza negocio excpetion si el usuario no existe o no tiene el estado
     * de 'activo'.
     */
    public Solicitud asignarResponsable(UUID id, UUID idResponsable, Usuario actor) {
        Solicitud solicitud = obtenerSolicitudPorId(id);

        if (solicitud.getEstado() == Estado.CERRADA) {
            throw new BusinessRuleException("No se puede asignar responsable a una solicitud cerrada");
        }

        Usuario responsable = usuarioRepository.findById(idResponsable)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado"));

        if (!responsable.isActivo()) {
            throw new BusinessRuleException("El usuario responsable debe estar activo para ser asignado");
        }

        Estado estadoActual = solicitud.getEstado();
        solicitud.setResponsable(responsable);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        Solicitud saved = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarCambio(saved, actor,
                "ASIGNACION", "Asignada a responsable: " + responsable.getNombre(),
                estadoActual, estadoActual);

        return saved;
    }

    /*
     * Gestiona el progreso de atención de la solicitud.
     * Regla de negocio: "Al atender: permitir solo cambios válidos".
     * Transiciones válidas aplicadas:
     * - REGISTRADA/CLASIFICADA -> EN_ATENCION
     * - EN_ATENCION -> ATENDIDA
     * - CERRADA -> (Invalido, arroja excepción BusinessRuleException)
     */
    public Solicitud registrarAtencion(UUID id, String observacion, Usuario actor) {
        Solicitud solicitud = obtenerSolicitudPorId(id);
        Estado estadoAnterior = solicitud.getEstado();

        if (estadoAnterior == Estado.CERRADA) {
            throw new BusinessRuleException("No se puede atender una solicitud ya cerrada");
        }

        if (estadoAnterior == Estado.ATENDIDA) {
            throw new BusinessRuleException("La solicitud ya fue atendida. Para finalizarla, proceda a cerrarla");
        }

        if (estadoAnterior == Estado.REGISTRADA || estadoAnterior == Estado.CLASIFICADA) {
            solicitud.setEstado(Estado.EN_ATENCION);
        } else if (estadoAnterior == Estado.EN_ATENCION) {
            solicitud.setEstado(Estado.ATENDIDA);
        }

        solicitud.setFechaActualizacion(LocalDateTime.now());
        Solicitud saved = solicitudRepository.save(solicitud);

        historialSolicitudService.registrarCambio(saved, actor,
                "ATENCION", observacion, estadoAnterior, saved.getEstado());

        return saved;
    }

    /*
     * Procede con el cierre de la solicitud.
     * Regla de negocio: "Al cerrar: solo cerrar si está en estado correcto".
     * Valida que la solicitud haya pasado por atención y este ATENDIDA.
     */
    public Solicitud cerrarSolicitud(UUID id, String observacion, Usuario actor) {
        Solicitud solicitud = obtenerSolicitudPorId(id);
        Estado estadoAnterior = solicitud.getEstado();

        if (estadoAnterior != Estado.ATENDIDA) {
            throw new BusinessRuleException(
                    "La solicitud solo puede cerrarse si está en estado ATENDIDA. Estado actual: " + estadoAnterior);
        }

        solicitud.setEstado(Estado.CERRADA);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        Solicitud saved = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarCambio(saved, actor,
                "CIERRE", observacion, estadoAnterior, Estado.CERRADA);

        return saved;
    }

    /*
     * Consulta el historial automático de todos los cambios asociados a esta
     * solicitud.
     * Utiliza Hibernate.initialize de forma limpia y explícita para cargar
     * la lista perezosa (Lazy Loading) antes de cerrar la sesión de base de datos.
     */
    @Transactional(readOnly = true)
    public List<HistorialSolicitud> consultarHistorial(UUID id) {
        Solicitud solicitud = obtenerSolicitudPorId(id);
        List<HistorialSolicitud> historiales = solicitud.getHistoriales();
        Hibernate.initialize(historiales);
        return historiales;
    }
}
