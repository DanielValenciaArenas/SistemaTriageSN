package co.edu.uniquindio.sistematriage.domain.model;


import co.edu.uniquindio.sistematriage.domain.enums.Canal;
import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import co.edu.uniquindio.sistematriage.domain.enums.Prioridad;
import co.edu.uniquindio.sistematriage.domain.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Solicitud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue
    @Column(name = "idSolicitud", nullable = false, unique = true)
    private UUID idSolicitud;

    @Column(name = "nombre", nullable = false, length = 250)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoSolicitud")
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "canalOrigen", nullable = false, length = 20)
    private Canal canalOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitanteId", nullable = false)
    private Usuario solicitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", length = 20)
    private Prioridad prioridad;

    @Column(name = "justificacionPrioridad", length = 500)
    private String justificacionPrioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsableId")
    private Usuario responsable;

    @Column(name = "fechaRegistro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fechaActualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "fechaCierre")
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialSolicitud> historiales = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaActualizacion = ahora;
        if (this.estado == null) {
            this.estado = Estado.REGISTRADA;
        }
    }

}
