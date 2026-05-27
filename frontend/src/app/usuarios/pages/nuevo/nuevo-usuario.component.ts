import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UsuarioService } from '../../usuario.service';

/** PERSONA C — implementar */
@Component({
  selector: 'app-nuevo-usuario',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nuevo-usuario.component.html',
  styleUrl: './nuevo-usuario.component.css'
})
export class NuevoUsuarioComponent {
  constructor(public usuarioService: UsuarioService) {}
}
