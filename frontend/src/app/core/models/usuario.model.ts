// ─── Enums ────────────────────────────────────────────────────────────────────

export type RolUsuario = 'ESTUDIANTE' | 'ADMINISTRATIVO';

// ─── Interfaces ───────────────────────────────────────────────────────────────

/** Coincide con UsuarioResponseDTO del backend */
export interface Usuario {
  id: string;
  nombre: string;
  correo: string;
  rol: RolUsuario;
  activo: boolean;
  fechaRegistro: string;
}

// ─── Etiquetas ────────────────────────────────────────────────────────────────

export const ROL_LABEL: Record<RolUsuario, string> = {
  ESTUDIANTE:     'Estudiante',
  ADMINISTRATIVO: 'Administrativo',
};
