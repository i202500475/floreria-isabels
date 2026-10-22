import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FlorService } from '../../core/flor.service';
import { Flor } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-admin-flores',
  standalone: true,
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './admin-flores.component.html'
})
export class AdminFloresComponent implements OnInit {
  private florService = inject(FlorService);

  flores = signal<Flor[]>([]);
  mensaje = signal('');
  error = signal('');
  porEliminar = signal<Flor | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    // GET /api/flores
    this.florService.listar().subscribe({
      next: (lista) => this.flores.set(lista),
      error: (e) => this.error.set(mensajeError(e))
    });
  }

  confirmarEliminar(): void {
    const f = this.porEliminar();
    if (!f?.id) { return; }
    this.error.set('');
    // DELETE /api/flores/{id}
    this.florService.eliminar(f.id).subscribe({
      next: (r) => { this.mensaje.set(r.mensaje); this.porEliminar.set(null); this.cargar(); },
      error: (e) => { this.error.set(mensajeError(e)); this.porEliminar.set(null); }
    });
  }

  reponer(f: Flor): void {
    if (!f.id) { return; }
    // PUT /api/flores/{id} (stock + 5)
    this.florService.actualizar(f.id, { ...f, stock: f.stock + 5 }).subscribe({
      next: () => { this.mensaje.set(`Se repuso el stock de ${f.nombre}`); this.cargar(); },
      error: (e) => this.error.set(mensajeError(e))
    });
  }
}
