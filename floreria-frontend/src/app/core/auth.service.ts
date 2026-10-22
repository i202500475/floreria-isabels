import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { RegistroRequest, Sesion } from './models';

const CLAVE = 'floreria_sesion';

/** Consume /api/auth (login, registro y logout) y guarda la sesión en el navegador. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/auth`;

  readonly sesion = signal<Sesion | null>(this.leerSesion());
  readonly logueado = computed(() => this.sesion() !== null);
  readonly esAdmin = computed(() => this.tieneRol('ROLE_ADMIN'));
  readonly esDelivery = computed(() => this.tieneRol('ROLE_DELIVERY'));

  login(username: string, password: string): Observable<Sesion> {
    return this.http.post<Sesion>(`${this.url}/login`, { username, password }).pipe(
      tap((s: Sesion) => this.guardarSesion(s))
    );
  }

  registro(datos: RegistroRequest): Observable<{ mensaje: string }> {
    return this.http.post<{ mensaje: string }>(`${this.url}/registro`, datos);
  }

  logout(): void {
    if (this.sesion()) {
      this.http.post(`${this.url}/logout`, {}).subscribe({ error: () => {} });
    }
    this.limpiar();
  }

  /** Borra la sesión local (también se usa cuando el token ya no es válido). */
  limpiar(): void {
    try { localStorage.removeItem(CLAVE); } catch { /* sin almacenamiento */ }
    this.sesion.set(null);
  }

  token(): string | null {
    return this.sesion()?.token ?? null;
  }

  tieneRol(...roles: string[]): boolean {
    const s = this.sesion();
    return !!s && s.roles.some((r) => roles.includes(r));
  }

  private guardarSesion(s: Sesion): void {
    try { localStorage.setItem(CLAVE, JSON.stringify(s)); } catch { /* sin almacenamiento */ }
    this.sesion.set(s);
  }

  private leerSesion(): Sesion | null {
    try {
      const raw = localStorage.getItem(CLAVE);
      return raw ? (JSON.parse(raw) as Sesion) : null;
    } catch {
      return null;
    }
  }
}
