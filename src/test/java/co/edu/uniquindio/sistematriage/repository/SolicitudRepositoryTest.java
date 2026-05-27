package co.edu.uniquindio.sistematriage.repository;

import co.edu.uniquindio.sistematriage.domain.enums.*;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SolicitudRepositoryTest {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void buscarConFiltrosYConsultasEspecialesFuncionan() {
        Usuario solicitante = Usuario.builder()
                .nombre("Alejandro Ruiz")
                .correo("alejandro@example.com")
                .activo(true)
                .rol(RolUsuario.ESTUDIANTE)
                .build();
        usuarioRepository.save(solicitante);

        Usuario responsable = Usuario.builder()
                .nombre("Lorena Vega")
                .correo("lorena@example.com")
                .activo(true)
                .rol(RolUsuario.ADMINISTRATIVO)
                .build();
        usuarioRepository.save(responsable);

        Solicitud clasificada = Solicitud.builder()
                .nombre("Homologación")
                .descripcion("Homologar asignatura de otro programa")
                .tipoSolicitud(TipoSolicitud.HOMOLOGACION)
                .canalOrigen(co.edu.uniquindio.sistematriage.domain.enums.Canal.CORREO)
                .solicitante(solicitante)
                .responsable(responsable)
                .estado(Estado.CLASIFICADA)
                .prioridad(Prioridad.ALTA)
                .build();

        Solicitud registrada = Solicitud.builder()
                .nombre("Solicitud de cupo")
                .descripcion("Cupo para materia cerrada")
                .tipoSolicitud(TipoSolicitud.CUPO)
                .canalOrigen(Canal.TELEFONICO)
                .solicitante(solicitante)
                .estado(Estado.REGISTRADA)
                .prioridad(Prioridad.MEDIA)
                .build();

        solicitudRepository.save(clasificada);
        solicitudRepository.save(registrada);

        assertThat(solicitudRepository.findByEstado(Estado.CLASIFICADA)).containsExactly(clasificada);
        assertThat(solicitudRepository.findByTipoSolicitud(TipoSolicitud.CUPO)).containsExactly(registrada);
        assertThat(solicitudRepository.findByPrioridad(Prioridad.ALTA)).containsExactly(clasificada);
        assertThat(solicitudRepository.findByResponsable_IdUsuario(responsable.getIdUsuario())).containsExactly(clasificada);
        assertThat(solicitudRepository.findBySolicitante_IdUsuario(solicitante.getIdUsuario()))
                .hasSize(2)
                .containsExactlyInAnyOrder(clasificada, registrada);

        List<Solicitud> filtradas = solicitudRepository.buscarConFiltros(Estado.REGISTRADA, TipoSolicitud.CUPO,
                Prioridad.MEDIA, null);
        assertThat(filtradas).containsExactly(registrada);
    }
}
