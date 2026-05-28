import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SugerenciaIA {
  tipoSugerido: string;
  prioridadSugerida: string;
  confianza?: number;
  justificacion: string;
}

export interface ResumenIA {
  resumen: string;
}

@Injectable({ providedIn: 'root' })
export class IaService {
  private readonly api = 'http://localhost:8080/ia';

  constructor(private http: HttpClient) {}

  /** RF-10: Sugerir tipo y prioridad a partir de la descripción */
  sugerir(descripcion: string): Observable<SugerenciaIA> {
    return this.http.post<SugerenciaIA>(`${this.api}/sugerencias/clasificacion`, { descripcion });
  }

  /** RF-09: Generar resumen del historial de una solicitud */
  resumir(solicitudId: string): Observable<ResumenIA> {
    return this.http.post<ResumenIA>(`${this.api}/resumen`, { solicitudId });
  }
}
