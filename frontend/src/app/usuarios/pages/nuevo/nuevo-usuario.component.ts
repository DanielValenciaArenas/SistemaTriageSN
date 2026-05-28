import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { UsuarioService } from '../../usuario.service';
import { RolUsuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-nuevo-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './nuevo-usuario.component.html',
  styleUrl: './nuevo-usuario.component.css'
})
export class NuevoUsuarioComponent implements OnInit {
  form: FormGroup;
  cargando = signal(false);
  cargandoDatos = signal(false);
  error = signal('');
  modoEdicion = false;
  usuarioId = '';

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      correo: ['', [Validators.required, Validators.email]],
      rol:    ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit() {
    this.usuarioId = this.route.snapshot.paramMap.get('id') || '';
    if (this.usuarioId) {
      this.modoEdicion = true;
      this.form.get('password')?.clearValidators();
      this.form.get('password')?.updateValueAndValidity();
      this.cargarUsuario();
    }
  }

  cargarUsuario() {
    this.cargandoDatos.set(true);
    this.error.set('');
    this.usuarioService.obtener(this.usuarioId).subscribe({
      next: (user) => {
        this.form.patchValue({
          nombre: user.nombre,
          correo: user.correo,
          rol:    user.rol
        });
        this.cargandoDatos.set(false);
      },
      error: (err) => {
        console.error('Error al cargar usuario:', err);
        if (err.status === 403) {
          this.error.set('No tienes permisos para consultar este usuario.');
        } else {
          this.error.set('No se pudo cargar la información del usuario.');
        }
        this.cargandoDatos.set(false);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set('');

    const payload = {
      nombre: this.form.value.nombre,
      correo: this.form.value.correo,
      rol:    this.form.value.rol as RolUsuario
    };

    if (this.modoEdicion) {
      this.usuarioService.actualizar(this.usuarioId, payload).subscribe({
        next:  () => this.router.navigate(['/usuarios']),
        error: () => {
          this.cargando.set(false);
          this.error.set('Error al actualizar el usuario.');
        }
      });
    } else {
      const payloadCrear = { ...payload, password: this.form.value.password };
      this.usuarioService.registrar(payloadCrear).subscribe({
        next:  () => this.router.navigate(['/usuarios']),
        error: () => {
          this.cargando.set(false);
          this.error.set('Error al registrar el usuario.');
        }
      });
    }
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
