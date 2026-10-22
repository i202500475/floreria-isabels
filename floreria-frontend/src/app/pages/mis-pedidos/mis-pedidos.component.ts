import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PedidoService } from '../../core/pedido.service';
import { Pedido } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-mis-pedidos',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './mis-pedidos.component.html'
})
export class MisPedidosComponent implements OnInit {
  private pedidoService = inject(PedidoService);

  pedidos = signal<Pedido[]>([]);
  cargando = signal(true);
  error = signal('');

  ngOnInit(): void {
    // GET /api/pedidos/mis-pedidos
    this.pedidoService.misPedidos().subscribe({
      next: (lista) => { this.pedidos.set(lista); this.cargando.set(false); },
      error: (e) => { this.error.set(mensajeError(e)); this.cargando.set(false); }
    });
  }

  texto(estado: string): string {
    return estado.replace('_', ' ');
  }
}
