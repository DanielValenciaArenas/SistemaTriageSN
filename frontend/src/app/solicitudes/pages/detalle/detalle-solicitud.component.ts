import { Component, signal, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../../auth/auth.service';
import { SolicitudService } from '../../solicitud.service';
import { UsuarioService } from '../../../usuarios/usuario.service';

import { Solicitud, HistorialAccion, } from '../../../core/models/solicitud.model';
import { Usuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-detalle-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalle-solicitud.component.html',
  styleUrls: ['./detalle-solicitud.component.css']
})
export class DetalleSolicitudComponent implements OnInit {


  constructor(
    public authService: AuthService,
    private solicitudService: SolicitudService,
    private usuarioService: UsuarioService,
    private route: ActivatedRoute
  ) {}

  solicitud = signal<Solicitud | null>(null);
  historial = signal<HistorialAccion[]>([]);
  administrativos = signal<Usuario[]>([]);

  mostrarFormPriorizar = signal(false);
  mostrarFormAsignar = signal(false);
  mostrarFormEstado = signal(false);

  nuevaPrioridad = '';
  justificacion = '';

  responsableSeleccionado = '';

  nuevoEstado = '';
  observacionEstado = '';

  mensajeExito = signal<string | null>(null);
  mensajeError = signal<string | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.cargarSolicitud(id);
    this.cargarHistorial(id);

    if (this.authService.isAdmin()) {
      this.cargarAdministrativos();
    }
  }

  private cargarSolicitud(id: string): void {
    this.solicitudService.obtener(id).subscribe({
      next: (data) => {
        console.log('SOLICITUD DETALLE:', data);
        this.solicitud.set(data);
      },
      error: () => this.mostrarError('No se pudo cargar la solicitud.')
    });
  }
  private cargarHistorial(id: string): void {
    this.solicitudService.historial(id).subscribe({
      next: (data) => this.historial.set(data),
      error: () => {}
    });
  }

  private cargarAdministrativos(): void {
    this.usuarioService.listar('ADMINISTRATIVO').subscribe({
      next: (data) => this.administrativos.set(data),
      error: () => {}
    });
  }

  atender(): void {
    const s = this.solicitud();
    if (!s) return;

    const obs = prompt('Observación de atención:');
    if (!obs) return;

    this.solicitudService.atender(s.id, obs).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.cargarHistorial(s.id);
        this.mostrarExito('Atención registrada correctamente.');
      },
      error: () => this.mostrarError('Error al registrar la atención.')
    });
  }

  cerrar(): void {
    const s = this.solicitud();
    if (!s) return;

    const obs = prompt('Observación de cierre:');
    if (!obs) return;

    this.solicitudService.cerrar(s.id, obs).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.cargarHistorial(s.id);
        this.mostrarExito('Solicitud cerrada correctamente.');
      },
      error: () => this.mostrarError('Error al cerrar la solicitud.')
    });
  }

  toggleFormPriorizar(): void {
    this.mostrarFormPriorizar.update(v => !v);
    this.mostrarFormAsignar.set(false);
    this.mostrarFormEstado.set(false);
    this.limpiarMensajes();
  }

  confirmarPriorizar(): void {
    const s = this.solicitud();

    if (!s || !this.nuevaPrioridad || !this.justificacion.trim()) {
      this.mostrarError('Selecciona una prioridad e ingresa la justificación.');
      return;
    }

    this.solicitudService.priorizar(s.id, this.nuevaPrioridad as any, this.justificacion).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.cargarHistorial(s.id);
        this.mostrarFormPriorizar.set(false);
        this.nuevaPrioridad = '';
        this.justificacion = '';
        this.mostrarExito('Prioridad asignada correctamente.');
      },
      error: () => this.mostrarError('Error al priorizar la solicitud.')
    });
  }

  toggleFormAsignar(): void {
    this.mostrarFormAsignar.update(v => !v);
    this.mostrarFormPriorizar.set(false);
    this.mostrarFormEstado.set(false);
    this.limpiarMensajes();
  }

  confirmarAsignar(): void {
    const s = this.solicitud();

    if (!s || !this.responsableSeleccionado) {
      this.mostrarError('Selecciona un responsable.');
      return;
    }

    this.solicitudService.asignar(s.id, this.responsableSeleccionado).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.cargarHistorial(s.id);
        this.mostrarFormAsignar.set(false);
        this.responsableSeleccionado = '';
        this.mostrarExito('Responsable asignado correctamente.');
      },
      error: () => this.mostrarError('Error al asignar el responsable.')
    });
  }

  toggleFormEstado(): void {
    this.mostrarFormEstado.update(v => !v);
    this.mostrarFormPriorizar.set(false);
    this.mostrarFormAsignar.set(false);
    this.limpiarMensajes();
  }

  confirmarEstado(): void {
    const s = this.solicitud();

    if (!s || !this.nuevoEstado || !this.observacionEstado.trim()) {
      this.mostrarError('Selecciona un estado e ingresa una observación.');
      return;
    }

    if (this.nuevoEstado === 'EN_ATENCION' || this.nuevoEstado === 'ATENDIDA') {
      this.solicitudService.atender(s.id, this.observacionEstado).subscribe({
        next: (updated) => this.finalizarCambioEstado(updated, s.id, 'Estado actualizado correctamente.'),
        error: () => this.mostrarError('Error al actualizar el estado.')
      });
      return;
    }

    if (this.nuevoEstado === 'CERRADA') {
      this.solicitudService.cerrar(s.id, this.observacionEstado).subscribe({
        next: (updated) => this.finalizarCambioEstado(updated, s.id, 'Solicitud cerrada correctamente.'),
        error: () => this.mostrarError('Error al cerrar la solicitud. La solicitud debe estar ATENDIDA antes de cerrarse.')
      });
      return;
    }

    this.mostrarError('Estado no válido.');
  }


  private finalizarCambioEstado(updated: Solicitud, id: string, mensaje: string): void {
    this.solicitud.set(updated);
    this.cargarHistorial(id);
    this.mostrarFormEstado.set(false);
    this.nuevoEstado = '';
    this.observacionEstado = '';
    this.mostrarExito(mensaje);
  }

  nombreResponsable(): string {
    const s = this.solicitud();

    if (!s?.responsableId) {
      return 'Sin asignar';
    }

    const admin = this.administrativos().find(u => u.id === s.responsableId);

    return admin ? admin.nombre : s.responsableId;
  }

  private mostrarExito(msg: string): void {
    this.mensajeExito.set(msg);
    this.mensajeError.set(null);
    setTimeout(() => this.mensajeExito.set(null), 4000);
  }

  private mostrarError(msg: string): void {
    this.mensajeError.set(msg);
    this.mensajeExito.set(null);
    setTimeout(() => this.mensajeError.set(null), 4000);
  }

  private limpiarMensajes(): void {
    this.mensajeExito.set(null);
    this.mensajeError.set(null);
  }
}
