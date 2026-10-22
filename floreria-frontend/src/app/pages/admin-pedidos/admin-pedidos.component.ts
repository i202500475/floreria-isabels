import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { PedidoService } from '../../core/pedido.service';
import { ESTADOS, Pedido } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-admin-pedidos',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './admin-pedidos.component.html'
})
export class AdminPedidosComponent implements OnInit {
  private pedidoService = inject(PedidoService);
  auth = inject(AuthService);

  estados = ESTADOS;
  pedidos = signal<Pedido[]>([]);
  filtro = signal('TODOS');
  mensaje = signal('');
  error = signal('');

  filtrados = computed(() =>
    this.filtro() === 'TODOS' ? this.pedidos() : this.pedidos().filter((p) => p.estado === this.filtro()));

  totalVendido = computed(() =>
    this.pedidos().filter((p) => p.estado !== 'CANCELADO').reduce((suma, p) => suma + (p.total ?? 0), 0));

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    // GET /api/pedidos
    this.pedidoService.listar().subscribe({
      next: (lista) => this.pedidos.set(lista),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  cambiarEstado(p: Pedido, estado: string): void {
    this.mensaje.set('');
    this.error.set('');
    // PUT /api/pedidos/{id}/estado
    this.pedidoService.cambiarEstado(p.id, estado).subscribe({
      next: (act) => { this.mensaje.set(`${act.codigo} ahora está ${act.estado.replace('_', ' ')}`); this.cargar(); },
      error: (e) => { this.error.set(mensajeError(e)); this.cargar(); }
    });
  }

  eliminar(p: Pedido): void {
    if (!confirm(`¿Eliminar el pedido ${p.codigo}?`)) { return; }
    // DELETE /api/pedidos/{id}
    this.pedidoService.eliminar(p.id).subscribe({
      next: (r) => { this.mensaje.set(r.mensaje); this.cargar(); },
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  cerrado(p: Pedido): boolean {
    return p.estado === 'ENTREGADO' || p.estado === 'CANCELADO';
  }

  texto(estado: string): string {
    return estado.replace('_', ' ');
  }
}
