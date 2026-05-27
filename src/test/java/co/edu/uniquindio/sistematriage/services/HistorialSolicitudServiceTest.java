package co.edu.uniquindio.sistematriage.services;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.sistematriage.domain.model.Solicitud;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.repository.HistorialSolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistorialSolicitudServiceTest {

    @Mock
    private HistorialSolicitudRepository historialSolicitudRepository;

    @InjectMocks
    private HistorialSolicitudService historialSolicitudService;

    @Test
    void registrarCambioGuardaRegistroDeHistorial() {
        Solicitud solicitud = Solicitud.builder().idSolicitud(UUID.randomUUID()).build();
        Usuario usuario = Usuario.builder().idUsuario(UUID.randomUUID()).nombre("Test").build();
        HistorialSolicitud esperado = HistorialSolicitud.builder()
                .idHistorial(UUID.randomUUID())
                .solicitud(solicitud)
                .usuario(usuario)
                .accion("REGISTRO")
                .observacion("Se registró la solicitud")
                .estadoAnterior(null)
                .estadoNuevo(Estado.REGISTRADA)
                .build();

        when(historialSolicitudRepository.save(any(HistorialSolicitud.class))).thenReturn(esperado);

        HistorialSolicitud resultado = historialSolicitudService.registrarCambio(solicitud, usuario,
                "REGISTRO", "Se registró la solicitud", null, Estado.REGISTRADA);

        assertThat(resultado).isSameAs(esperado);
        assertThat(resultado.getAccion()).isEqualTo("REGISTRO");
        assertThat(resultado.getEstadoNuevo()).isEqualTo(Estado.REGISTRADA);
    }
}
