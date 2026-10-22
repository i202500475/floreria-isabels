import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { FlorService } from '../../core/flor.service';
import { PedidoService } from '../../core/pedido.service';
import { Flor, METODOS_PAGO, Pedido } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-comprar',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink],
  templateUrl: './comprar.component.html'
})
export class ComprarComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private florService = inject(FlorService);
  private pedidoService = inject(PedidoService);
  auth = inject(AuthService);

  metodos = METODOS_PAGO;
  flor = signal<Flor | null>(null);
  error = signal('');
  enviando = signal(false);
  confirmado = signal<Pedido | null>(null);

  cantidad = 1;
  direccion = '';
  metodoPago = 'Yape';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    // GET /api/flores/{id}
    this.florService.buscar(id).subscribe({
      next: (f) => this.flor.set(f),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  total(): number {
    return (this.flor()?.precio ?? 0) * (this.cantidad || 0);
  }

  comprar(): void {
    const f = this.flor();
    if (!f?.id) { return; }
    this.error.set('');
    this.enviando.set(true);
    // POST /api/pedidos
    this.pedidoService.registrar({
      florId: f.id, cantidad: this.cantidad, direccion: this.direccion, metodoPago: this.metodoPago
    }).subscribe({
      next: (p) => { this.confirmado.set(p); this.enviando.set(false); },
      error: (e) => { this.error.set(mensajeError(e)); this.enviando.set(false); }
    });
  }
}
