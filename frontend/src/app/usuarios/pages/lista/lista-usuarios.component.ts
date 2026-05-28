import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UsuarioService } from '../../usuario.service';
import { Usuario, RolUsuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-usuarios.component.html',
  styleUrl: './lista-usuarios.component.css'
})
export class ListaUsuariosComponent implements OnInit {
  usuarios = signal<Usuario[]>([]);
  cargando = signal(true);
  error = signal('');
  filtroRol: RolUsuario | '' = '';

  constructor(public usuarioService: UsuarioService) {}

  ngOnInit() {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.cargando.set(true);
    this.error.set('');
    this.usuarioService.listar(this.filtroRol || undefined).subscribe({
      next: (data) => {
        if (data && !Array.isArray(data) && (data as any).content) {
          this.usuarios.set((data as any).content);
        } else {
          this.usuarios.set(data || []);
        }
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);
        if (err.status === 403) {
          this.error.set('No tienes permisos de Administrador para ver esta lista.');
        } else {
          this.error.set('Error de conexión al cargar los usuarios.');
        }
        this.usuarios.set([]);
        this.cargando.set(false);
      }
    });
  }

  onFiltroChange(event: any) {
    this.filtroRol = event.target.value as RolUsuario | '';
    this.cargarUsuarios();
  }

  eliminar(id: string) {
    if (confirm('¿Estás seguro de eliminar este usuario?')) {
      this.usuarioService.eliminar(id).subscribe({
        next: () => {
          this.usuarios.update(users => users.filter(u => u.id !== id));
        },
        error: () => {
          alert('Error al eliminar usuario');
        }
      });
    }
  }

  getIniciales(nombre: string): string {
    if (!nombre) return '?';
    return nombre.substring(0, 2).toUpperCase();
  }
}
