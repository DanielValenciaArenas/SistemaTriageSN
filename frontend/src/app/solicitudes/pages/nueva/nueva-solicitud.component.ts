import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SolicitudService } from '../../solicitud.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-nueva-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nueva-solicitud.component.html',
  styleUrls: ['./nueva-solicitud.component.css']
})
export class NuevaSolicitudComponent {

  constructor(
    private solicitudService: SolicitudService,
    private router: Router
  ) {}

  request = {
    nombre: '',
    descripcion: '',
    tipoSolicitud: 'HOMOLOGACION',
    canalOrigen: 'PORTAL_WEB'
  };

  guardar() {
    this.solicitudService.crearSolicitud(this.request).subscribe({
      next: () => {
        alert('Solicitud registrada correctamente');
        this.router.navigate(['/solicitudes']);
      },
      error: (err) => {
        console.error(err);
        alert('Error al registrar la solicitud');
      }
    });
  }
}
