package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Canal;
import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.exception.ResourceNotFoundException;
import co.edu.uniquindio.sistematriage.repository.SolicitudRepository;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private HistorialSolicitudService historialSolicitudService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitudService solicitudService;

    private Usuario solicitante;
    private Usuario responsable;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitante = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .nombre("Sofía Álvarez")
                .correo("sofia@example.com")
                .activo(true)
                .build();

        responsable = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .nombre("Carlos Ramírez")
                .correo("carlos@example.com")
                .activo(true)
                .build();

        solicitud = Solicitud.builder()
                .idSolicitud(UUID.randomUUID())
                .nombre("Solicitud de cupo")
                .descripcion("Petición de cupo adicional")
                .canalOrigen(Canal.TELEFONICO)
                .solicitante(solicitante)
                .estado(Estado.REGISTRADA)
                .tipoSolicitud(TipoSolicitud.CUPO)
                .prioridad(Prioridad.MEDIA)
                .build();

        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registrarSolicitudAsignaEstadoRegistradaYRegistraHistorial() {
        when(historialSolicitudService.registrarCambio(any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        Solicitud guardada = solicitudService.registrarSolicitud(solicitud);

        assertThat(guardada.getEstado()).isEqualTo(Estado.REGISTRADA);
    }

    @Test
    void registrarSolicitudSinSolicitanteLanzaExcepcion() {
        solicitud.setSolicitante(null);

        assertThatThrownBy(() -> solicitudService.registrarSolicitud(solicitud))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("solicitante asignado");
    }

    @Test
    void asignarResponsableActivoGuardaResponsable() {
        when(solicitudRepository.findById(solicitud.getIdSolicitud())).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(responsable.getIdUsuario())).thenReturn(Optional.of(responsable));

        Solicitud guardada = solicitudService.asignarResponsable(solicitud.getIdSolicitud(), responsable.getIdUsuario(), solicitante);

        assertThat(guardada.getResponsable()).isEqualTo(responsable);
    }

    @Test
    void asignarResponsableInactivoLanzaExcepcion() {
        Usuario inactivo = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .nombre("No activo")
                .correo("noactivo@example.com")
                .activo(false)
                .build();

        when(solicitudRepository.findById(solicitud.getIdSolicitud())).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(inactivo.getIdUsuario())).thenReturn(Optional.of(inactivo));

        assertThatThrownBy(() -> solicitudService.asignarResponsable(solicitud.getIdSolicitud(), inactivo.getIdUsuario(), solicitante))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("activo");
    }

    @Test
    void cerrarSolicitudSoloSiEstaAtendida() {
        solicitud.setEstado(Estado.ATENDIDA);
        when(solicitudRepository.findById(solicitud.getIdSolicitud())).thenReturn(Optional.of(solicitud));

        Solicitud cerrada = solicitudService.cerrarSolicitud(solicitud.getIdSolicitud(), "Cerrada correctamente", solicitante);

        assertThat(cerrada.getEstado()).isEqualTo(Estado.CERRADA);
        assertThat(cerrada.getFechaCierre()).isNotNull();
    }

    @Test
    void cerrarSolicitudNoAtendidaLanzaExcepcion() {
        solicitud.setEstado(Estado.REGISTRADA);
        when(solicitudRepository.findById(solicitud.getIdSolicitud())).thenReturn(Optional.of(solicitud));

        assertThatThrownBy(() -> solicitudService.cerrarSolicitud(solicitud.getIdSolicitud(), "No puede cerrar", solicitante))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("solo puede cerrarse");
    }

    @Test
    void obtenerSolicitudPorIdNoEncontradaLanzaExcepcion() {
        when(solicitudRepository.findById(solicitud.getIdSolicitud())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitudService.obtenerSolicitudPorId(solicitud.getIdSolicitud()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Solicitud no encontrada");
    }
}
