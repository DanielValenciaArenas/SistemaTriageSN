import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './auth/auth.guard';
import { LoginComponent } from './auth/pages/login/login.component';
import { RegisterComponent } from './auth/pages/register/register.component';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  { path: 'login',    component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },

      { path: 'home', component: HomeComponent },

      // ── Persona B ──────────────────────────────────────
      {
        path: 'solicitudes',
        loadComponent: () =>
          import('./solicitudes/pages/lista/lista-solicitudes.component')
            .then(m => m.ListaSolicitudesComponent)
      },
      {
        path: 'solicitudes/nueva',
        loadComponent: () =>
          import('./solicitudes/pages/nueva/nueva-solicitud.component')
            .then(m => m.NuevaSolicitudComponent)
      },
      {
        path: 'solicitudes/:id',
        loadComponent: () =>
          import('./solicitudes/pages/detalle/detalle-solicitud.component')
            .then(m => m.DetalleSolicitudComponent)
      },

      // ── Persona C ──────────────────────────────────────
      {
        path: 'usuarios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./usuarios/pages/lista/lista-usuarios.component')
            .then(m => m.ListaUsuariosComponent)
      },
      {
        path: 'usuarios/nuevo',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./usuarios/pages/nuevo/nuevo-usuario.component')
            .then(m => m.NuevoUsuarioComponent)
      },
      {
        path: 'ia',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./ia/pages/panel/panel-ia.component')
            .then(m => m.PanelIaComponent)
      },
    ]
  },

  { path: '**', redirectTo: 'login' }
];
