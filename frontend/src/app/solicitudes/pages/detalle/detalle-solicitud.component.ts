import { Component, signal } from '@angular/core';
import { AuthService } from '../../../auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-detalle-solicitud',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle-solicitud.component.html',
  styleUrls: ['./detalle-solicitud.component.css']
})
export class DetalleSolicitudComponent {

  constructor(public authService: AuthService) {}

  solicitud = signal({
    id: 1,
    nombre: 'Homologación de materias',
    descripcion: 'Solicitud realizada por el estudiante.',
    estado: 'REGISTRADA',
    prioridad: 'ALTA',
    tipoSolicitud: 'HOMOLOGACION',
    responsableId: 'Karen'
  });

  historial = signal([
    {
      accion: 'Solicitud registrada',
      observacion: 'La solicitud fue creada correctamente',
      actorNombre: 'Sistema',
      fechaAccion: new Date()
    }
  ]);

  clasificar() {
    this.actualizarEstado('CLASIFICADA', 'Solicitud clasificada');
  }

  priorizar() {
    this.actualizarEstado('EN_ATENCION', 'Solicitud priorizada');
  }

  asignar() {
    this.actualizarEstado('EN_ATENCION', 'Solicitud asignada');
  }

  atender() {
    this.actualizarEstado('ATENDIDA', 'Solicitud atendida');
  }

  cerrar() {
    this.actualizarEstado('CERRADA', 'Solicitud cerrada');
  }

  actualizarEstado(estado: string, accion: string) {
    this.solicitud.update(s => ({
      ...s,
      estado
    }));

    this.historial.update(h => [
      {
        accion,
        observacion: accion,
        actorNombre: this.authService.isAdmin() ? 'Administrativo' : 'Estudiante',
        fechaAccion: new Date()
      },
      ...h
    ]);
  }

}
