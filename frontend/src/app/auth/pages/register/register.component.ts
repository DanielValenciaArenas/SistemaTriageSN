import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  nombre    = '';
  correo    = '';
  password  = '';
  confirmar = '';
  rol       = 'ESTUDIANTE';
  error     = '';
  loading   = false;

  constructor(private auth: AuthService, private router: Router) {}

  register(): void {
    if (!this.nombre || !this.correo || !this.password || !this.confirmar) {
      this.error = 'Por favor completa todos los campos.';
      return;
    }
    if (this.password !== this.confirmar) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }
    if (this.password.length < 6) {
      this.error = 'La contraseña debe tener al menos 6 caracteres.';
      return;
    }
    this.loading = true;
    this.error   = '';

    this.auth.register(this.nombre, this.correo, this.password, this.rol).subscribe({
      next: () => this.router.navigate(['/home']),
      error: (err) => {
        this.error = err?.error?.message ?? 'Error al registrar. Intenta de nuevo.';
        this.loading = false;
      }
    });
  }
}
