import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FlorService } from '../../core/flor.service';
import { Flor } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-flor-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './flor-form.component.html'
})
export class FlorFormComponent implements OnInit {
  private florService = inject(FlorService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  id: number | null = null;
  flor: Flor = { nombre: '', descripcion: '', precio: 0, imagenUrl: '', stock: 5 };
  error = signal('');
  guardando = signal(false);

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      this.id = Number(param);
      // GET /api/flores/{id}
      this.florService.buscar(this.id).subscribe({
        next: (f) => this.flor = f,
        error: (e) => this.error.set(mensajeError(e))
      });
    }
  }

  guardar(): void {
    this.error.set('');
    this.guardando.set(true);
    // PUT /api/flores/{id} si edita, POST /api/flores si es nueva
    const peticion = this.id
      ? this.florService.actualizar(this.id, this.flor)
      : this.florService.registrar(this.flor);

    peticion.subscribe({
      next: () => this.router.navigate(['/admin/flores']),
      error: (e) => { this.error.set(mensajeError(e)); this.guardando.set(false); }
    });
  }
}
