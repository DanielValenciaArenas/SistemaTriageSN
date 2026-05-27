import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Solicitud, HistorialAccion,
  Estado, TipoSolicitud, Prioridad
} from '../core/models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly api = 'http://localhost:8080/solicitudes';

  constructor(private http: HttpClient) {}

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
    return this.http.get<Solicitud[]>(this.api, { params });
  }

  obtener(id: string): Observable<Solicitud> {
    return this.http.get<Solicitud>(`${this.api}/${id}`);
  }

  registrar(dto: {
    nombre: string;
    descripcion: string;
    canalOrigen: string;
  }): Observable<Solicitud> {
    return this.http.post<Solicitud>(this.api, dto);
  }

  clasificar(id: string, tipo: TipoSolicitud): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.api}/${id}/clasificar`, { tipo });
  }

  priorizar(id: string, prioridad: Prioridad, justificacion: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.api}/${id}/priorizar`, { prioridad, justificacion });
  }

  asignar(id: string, responsableId: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.api}/${id}/asignar`, { responsableId });
  }

  atender(id: string, observacion: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.api}/${id}/atender`, { observacion });
  }

  cerrar(id: string, observacionCierre: string): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.api}/${id}/cerrar`, { observacionCierre });
  }

  historial(id: string): Observable<HistorialAccion[]> {
    return this.http.get<HistorialAccion[]>(`${this.api}/${id}/historial`);
  }
}
