import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  correo   = '';
  password = '';
  error    = '';
  loading  = false;

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    if (!this.correo || !this.password) {
      this.error = 'Por favor completa todos los campos.';
      return;
    }
    this.loading = true;
    this.error   = '';

    this.auth.login(this.correo, this.password).subscribe({
      next: () => this.router.navigate(['/home']),
      error: () => { this.error = 'Correo o contraseña incorrectos.'; this.loading = false; }
    });
  }
}
