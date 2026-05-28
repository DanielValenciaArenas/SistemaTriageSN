import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IaService, SugerenciaIA, ResumenIA } from '../../ia.service';
import { SolicitudService } from '../../../solicitudes/solicitud.service';

@Component({
  selector: 'app-panel-ia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './panel-ia.component.html',
  styleUrl: './panel-ia.component.css'
})
export class PanelIaComponent implements OnInit {
  descripcion = '';
  cargandoSugerencia = false;
  sugerencia: SugerenciaIA | null = null;
  errorSugerencia = '';

  solicitudes: any[] = [];
  solicitudId = '';
  cargandoResumen = false;
  resumen: ResumenIA | null = null;
  errorResumen = '';

  constructor(private iaService: IaService, private solicitudService: SolicitudService) {}

  ngOnInit() {
    this.solicitudService.listarMisSolicitudes().subscribe({
      next: (data) => this.solicitudes = data,
      error: () => console.error('Error al cargar solicitudes para el panel de IA')
    });
  }

  obtenerSugerencia() {
    if (!this.descripcion.trim()) return;
    this.cargandoSugerencia = true;
    this.sugerencia = null;
    this.errorSugerencia = '';

    this.iaService.sugerir(this.descripcion).subscribe({
      next: (res) => {
        this.sugerencia = res;
        this.cargandoSugerencia = false;
      },
      error: () => {
        this.errorSugerencia = 'Ocurrió un error al obtener la sugerencia de la IA.';
        this.cargandoSugerencia = false;
      }
    });
  }

  obtenerResumen() {
    if (!this.solicitudId.trim()) return;
    this.cargandoResumen = true;
    this.resumen = null;
    this.errorResumen = '';

    this.iaService.resumir(this.solicitudId).subscribe({
      next: (res) => {
        this.resumen = res;
        this.cargandoResumen = false;
      },
      error: () => {
        this.errorResumen = 'Ocurrió un error al generar el resumen de la solicitud.';
        this.cargandoResumen = false;
      }
    });
  }
}
