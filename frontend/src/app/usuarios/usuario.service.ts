import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, RolUsuario } from '../core/models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly api = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient) {}

  listar(rol?: RolUsuario): Observable<Usuario[]> {
    let params = new HttpParams();
    if (rol) params = params.set('rol', rol);
    return this.http.get<Usuario[]>(this.api, { params });
  }

  obtener(id: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.api}/${id}`);
  }

  registrar(dto: {
    nombre: string;
    correo: string;
    rol: RolUsuario;
    password: string;
  }): Observable<Usuario> {
    return this.http.post<Usuario>(this.api, dto);
  }

  actualizar(id: string, dto: {
    nombre?: string;
    correo?: string;
    rol?: RolUsuario;
  }): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.api}/${id}`, dto);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
