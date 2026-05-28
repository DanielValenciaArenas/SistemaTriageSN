import { Component, computed, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SolicitudService } from '../../solicitud.service';
import { Solicitud } from '../../../core/models/solicitud.model';

@Component({
  selector: 'app-lista-solicitudes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lista-solicitudes.component.html',
  styleUrls: ['./lista-solicitudes.component.css']
})
export class ListaSolicitudesComponent implements OnInit {

  estadoFiltro = signal('');
  tipoFiltro = signal('');
  prioridadFiltro = signal('');

  solicitudes = signal<Solicitud[]>([]);

  constructor(private solicitudService: SolicitudService) {}

  ngOnInit(): void {
    this.solicitudService.listarMisSolicitudes().subscribe({
      next: (data) => {
        this.solicitudes.set(data);
      },
      error: (err: any) => {
        console.error(err);
      }
    });
  }

  solicitudesFiltradas = computed(() => {
    const estado = this.estadoFiltro();
    const tipo = this.tipoFiltro();
    const prioridad = this.prioridadFiltro();

    return this.solicitudes().filter(s => {
      const estadoOk = estado === '' || s.estado === estado;
      const tipoOk = tipo === '' || s.tipoSolicitud === tipo;
      const prioridadOk = prioridad === '' || s.prioridad === prioridad;

      return estadoOk && tipoOk && prioridadOk;
    });
  });

}
