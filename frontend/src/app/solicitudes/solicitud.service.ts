import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import {
  Solicitud, HistorialAccion,
  Estado, TipoSolicitud, Prioridad
} from '../core/models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly apiUrl = 'http://localhost:8080/solicitudes';

  constructor(private http: HttpClient,  private authService: AuthService) {}

  listar(filtros?: {
    estado?: Estado;
    tipo?: TipoSolicitud;
    prioridad?: Prioridad;
    responsableId?: string;
  }): Observable<Solicitud[]> {
    let params = new HttpParams();
    if (filtros?.estado)        params = params.set('estado',        filtros.estado);
    if (filtros?.tipo)          params = params.set('tipo',          filtros.tipo);
    if (filtros?.prioridad)     params = params.set('prioridad',     filtros.prioridad);
    if (filtros?.responsableId) params = params.set('responsableId', filtros.responsableId);
    return this.http.get<Solicitud[]>(this.apiUrl, { params });
  }

  obtener(id: string): Observable<Solicitud> {
    return this.http.get<Solicitud>(`${this.apiUrl}/${id}`);
  }

  registrar(dto: {
    nombre: string;
    descripcion: string;
    canalOrigen: string;
    idSolicitante: string | null;
  }): Observable<Solicitud> {
    return this.http.post<Solicitud>(this.apiUrl, dto);
  }

  clasificar(id: string, tipo: TipoSolicitud): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/clasificar`, { tipo });
  }

  priorizar(id: string, prioridad: Prioridad, justificacion: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/priorizar`, { prioridad, justificacion });
  }

  asignar(id: string, responsableId: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/asignar`, { responsableId });
  }

  atender(id: string, observacion: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/atender`, { observacion });
  }

  cerrar(id: string, observacionCierre: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/cerrar`, { observacionCierre });
  }

  historial(id: string): Observable<HistorialAccion[]> {
    return this.http.get<HistorialAccion[]>(`${this.apiUrl}/${id}/historial`);
  }

  listarMisSolicitudes(): Observable<Solicitud[]> {
    return this.listar();
  }

  crearSolicitud(request: any): Observable<Solicitud> {
    return this.registrar({
      nombre: request.nombre,
      descripcion: request.descripcion,
      canalOrigen: request.canalOrigen,
      idSolicitante: this.authService.getUserId()
    } as any);
  }
}
