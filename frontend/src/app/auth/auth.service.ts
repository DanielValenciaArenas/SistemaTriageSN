import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

export interface LoginResponse {
  token: string;
  rol: string;
  userId: string;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/auth';
  currentUser = signal<LoginResponse | null>(this.loadFromStorage());

  constructor(private http: HttpClient, private router: Router) {}

  login(correo: string, password: string) {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, { correo, password })
      .pipe(tap(res => this.saveSession(res)));
  }

  register(nombre: string, correo: string, password: string, rol: string) {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/register`, { nombre, correo, password, rol })
      .pipe(tap(res => this.saveSession(res)));
  }

  logout(): void {
    localStorage.removeItem('auth');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private saveSession(res: LoginResponse): void {
    localStorage.setItem('auth', JSON.stringify(res));
    this.currentUser.set(res);
  }

  getToken(): string | null  { return this.currentUser()?.token  ?? null; }
  getRol(): string | null    { return this.currentUser()?.rol    ?? null; }
  getUserId(): string | null { return this.currentUser()?.userId ?? null; }
  getNombre(): string | null { return this.currentUser()?.nombre ?? null; }
  isLoggedIn(): boolean      { return this.currentUser() !== null; }
  isAdmin(): boolean         { return this.getRol() === 'ADMINISTRATIVO'; }

  private loadFromStorage(): LoginResponse | null {
    try {
      const raw = localStorage.getItem('auth');
      return raw ? JSON.parse(raw) : null;
    } catch { return null; }
  }
}
