package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.*;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class HistoriaSolicitudRepositoryTest {

    @Autowired
    private HistorialSolicitudRepository historialSolicitudRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void findBySolicitudIdReturnsOrderedHistory() {
        Usuario solicitante = Usuario.builder()
                .nombre("Gabriel García")
                .correo("gabriel@example.com")
                .activo(true)
                .rol(RolUsuario.ESTUDIANTE)
                .build();
        usuarioRepository.save(solicitante);

        Solicitud solicitud = Solicitud.builder()
                .nombre("Homologación de asignatura")
                .descripcion("Solicitud de homologación de materia de otro programa")
                .canalOrigen(Canal.CORREO)
                .solicitante(solicitante)
                .estado(Estado.REGISTRADA)
                .prioridad(Prioridad.MEDIA)
                .tipoSolicitud(TipoSolicitud.HOMOLOGACION)
                .build();
        solicitudRepository.save(solicitud);

        HistorialSolicitud primer = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .usuario(solicitante)
                .accion("REGISTRO")
                .observacion("Solicitud registrada")
                .estadoAnterior(null)
                .estadoNuevo(Estado.REGISTRADA)
                .fechaHoraAccion(LocalDateTime.now().minusMinutes(2))
                .build();

        HistorialSolicitud segundo = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .usuario(solicitante)
                .accion("CLASIFICACION")
                .observacion("Solicitud clasificada")
                .estadoAnterior(Estado.REGISTRADA)
                .estadoNuevo(Estado.CLASIFICADA)
                .fechaHoraAccion(LocalDateTime.now().minusMinutes(1))
                .build();

        historialSolicitudRepository.save(primer);
        historialSolicitudRepository.save(segundo);

        List<HistorialSolicitud> resultados = historialSolicitudRepository
                .findBySolicitud_IdSolicitudOrderByFechaHoraAccionAsc(solicitud.getIdSolicitud());

        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getAccion()).isEqualTo("REGISTRO");
        assertThat(resultados.get(1).getAccion()).isEqualTo("CLASIFICACION");
    }
}
