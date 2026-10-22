import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { RegistroRequest } from '../../core/models';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.component.html'
})
export class RegistroComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  datos: RegistroRequest = { username: '', password: '', nombreCompleto: '', correo: '', direccion: '' };
  error = signal('');
  enviando = signal(false);

  registrar(): void {
    this.error.set('');
    this.enviando.set(true);
    // POST /api/auth/registro
    this.auth.registro(this.datos).subscribe({
      next: () => this.router.navigate(['/login'], { queryParams: { registrado: 1 } }),
      error: (e) => { this.error.set(mensajeError(e)); this.enviando.set(false); }
    });
  }
}
