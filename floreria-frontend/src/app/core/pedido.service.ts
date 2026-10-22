import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Pedido, PedidoRequest } from './models';

/** Consume los servicios REST de /api/pedidos. */
@Injectable({ providedIn: 'root' })
export class PedidoService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/pedidos`;

  misPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.url}/mis-pedidos`);
  }

  listar(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.url);
  }

  registrar(req: PedidoRequest): Observable<Pedido> {
    return this.http.post<Pedido>(this.url, req);
  }

  cambiarEstado(id: number, estado: string): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.url}/${id}/estado`, { estado });
  }

  eliminar(id: number): Observable<{ mensaje: string }> {
    return this.http.delete<{ mensaje: string }>(`${this.url}/${id}`);
  }
}
