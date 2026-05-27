package co.edu.uniquindio.sistematriage.domain.model;

import co.edu.uniquindio.sistematriage.domain.enums.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Historial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HistorialSolicitud {

    @Id
    @GeneratedValue
    @Column(name = "idHistorial", nullable = false, unique = true)
    private UUID idHistorial;

    @Column(name = "fechaHoraAccion", nullable = false)
    private LocalDateTime fechaHoraAccion;

    @Column(name = "accion", length = 50)
    private String accion;

    @Column(name = "observacion", length = 200)
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoAnterior", length = 20)
    private Estado estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoNuevo", length = 20)
    private Estado estadoNuevo;


    //--RELACIONES--
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSolicitud", nullable = false)
    private Solicitud solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist(){
        if (fechaHoraAccion == null){
            fechaHoraAccion = LocalDateTime.now();
        }
    }
}
