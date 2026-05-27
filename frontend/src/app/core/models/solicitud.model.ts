// ─── Enums — deben coincidir exactamente con el backend ───────────────────────

export type Estado =
  | 'REGISTRADA'
  | 'CLASIFICADA'
  | 'EN_ATENCION'
  | 'ATENDIDA'
  | 'CERRADA';

export type TipoSolicitud =
  | 'REGISTRO_ASIGNATURA'
  | 'HOMOLOGACION'
  | 'CANCELACION'
  | 'CUPO'
  | 'CONSULTA';

export type Prioridad = 'ALTA' | 'MEDIA' | 'BAJA';

export type Canal =
  | 'CSU'
  | 'CORREO'
  | 'SAC'
  | 'TELEFONICO'
  | 'PORTAL_WEB';

// ─── Interfaces ───────────────────────────────────────────────────────────────

/** Coincide con SolicitudResponseDTO del backend */
export interface Solicitud {
  id: string;
  nombre: string;
  descripcion: string;
  tipoSolicitud: TipoSolicitud | null;
  canalOrigen: Canal;
  solicitanteId: string;
  estado: Estado;
  prioridad: Prioridad | null;
  justificacionPrioridad: string | null;
  responsableId: string | null;
  fechaRegistro: string;
  fechaActualizacion: string | null;
  fechaCierre: string | null;
}

/** Coincide con HistorialAccionDTO del backend */
export interface HistorialAccion {
  fechaAccion: string;
  accion: string;
  observacion: string | null;
  actorId: string;
  actorNombre: string;
  estadoAnterior: Estado | null;
  estadoNuevo: Estado | null;
}

// ─── Etiquetas legibles para mostrar en la UI ─────────────────────────────────

export const ESTADO_LABEL: Record<Estado, string> = {
  REGISTRADA:  'Registrada',
  CLASIFICADA: 'Clasificada',
  EN_ATENCION: 'En atención',
  ATENDIDA:    'Atendida',
  CERRADA:     'Cerrada',
};

export const TIPO_LABEL: Record<TipoSolicitud, string> = {
  REGISTRO_ASIGNATURA: 'Registro de asignatura',
  HOMOLOGACION:        'Homologación',
  CANCELACION:         'Cancelación',
  CUPO:                'Solicitud de cupo',
  CONSULTA:            'Consulta académica',
};

export const PRIORIDAD_LABEL: Record<Prioridad, string> = {
  ALTA:  'Alta',
  MEDIA: 'Media',
  BAJA:  'Baja',
};

export const CANAL_LABEL: Record<Canal, string> = {
  CSU:        'CSU',
  CORREO:     'Correo electrónico',
  SAC:        'SAC',
  TELEFONICO: 'Telefónico',
  PORTAL_WEB: 'Portal web',
};
