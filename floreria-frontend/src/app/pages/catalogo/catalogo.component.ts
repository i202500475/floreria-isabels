import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FlorService } from '../../core/flor.service';
import { Flor } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink],
  templateUrl: './catalogo.component.html'
})
export class CatalogoComponent implements OnInit {
  private florService = inject(FlorService);

  flores = signal<Flor[]>([]);
  busqueda = signal('');
  cargando = signal(true);
  error = signal('');

  filtradas = computed(() => {
    const texto = this.busqueda().trim().toLowerCase();
    return this.flores().filter((f) =>
      !texto || f.nombre.toLowerCase().includes(texto) || (f.descripcion ?? '').toLowerCase().includes(texto));
  });

  ngOnInit(): void {
    // GET /api/flores
    this.florService.listar().subscribe({
      next: (lista) => { this.flores.set(lista); this.cargando.set(false); },
      error: (e) => { this.error.set(mensajeError(e)); this.cargando.set(false); }
    });
  }
}
