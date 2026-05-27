import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitudService } from '../../solicitud.service';

/** PERSONA B — implementar */
@Component({
  selector: 'app-nueva-solicitud',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nueva-solicitud.component.html',
  styleUrl: './nueva-solicitud.component.css'
})
export class NuevaSolicitudComponent {
  constructor(public solicitudService: SolicitudService) {}
}
