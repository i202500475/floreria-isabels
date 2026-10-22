import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Flor } from './models';

/** Consume los servicios REST GET, POST, PUT y DELETE de /api/flores. */
@Injectable({ providedIn: 'root' })
export class FlorService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/flores`;

  listar(): Observable<Flor[]> {
    return this.http.get<Flor[]>(this.url);
  }

  buscar(id: number): Observable<Flor> {
    return this.http.get<Flor>(`${this.url}/${id}`);
  }

  registrar(flor: Flor): Observable<Flor> {
    return this.http.post<Flor>(this.url, flor);
  }

  actualizar(id: number, flor: Flor): Observable<Flor> {
    return this.http.put<Flor>(`${this.url}/${id}`, flor);
  }

  eliminar(id: number): Observable<{ mensaje: string }> {
    return this.http.delete<{ mensaje: string }>(`${this.url}/${id}`);
  }
}
