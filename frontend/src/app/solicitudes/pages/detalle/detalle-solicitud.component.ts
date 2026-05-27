import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitudService } from '../../solicitud.service';

/** PERSONA B — implementar */
@Component({
  selector: 'app-detalle-solicitud',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './detalle-solicitud.component.html',
  styleUrl: './detalle-solicitud.component.css'
})
export class DetalleSolicitudComponent {
  constructor(public solicitudService: SolicitudService) {}
}
