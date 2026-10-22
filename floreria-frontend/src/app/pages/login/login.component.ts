import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { mensajeError } from '../../core/errores';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  username = '';
  password = '';
  error = signal('');
  enviando = signal(false);
  aviso = this.route.snapshot.queryParamMap.has('registrado')
    ? 'Cuenta creada. Ya puedes iniciar sesión.'
    : this.route.snapshot.queryParamMap.has('expirada') ? 'Tu sesión expiró. Vuelve a iniciar sesión.' : '';

  ingresar(): void {
    this.error.set('');
    this.enviando.set(true);
    // POST /api/auth/login
    this.auth.login(this.username, this.password).subscribe({
      next: (s) => {
        const volver = this.route.snapshot.queryParamMap.get('volver');
        if (volver) {
          this.router.navigateByUrl(volver);
        } else if (s.roles.includes('ROLE_DELIVERY')) {
          this.router.navigate(['/admin/pedidos']);
        } else if (s.roles.includes('ROLE_ADMIN')) {
          this.router.navigate(['/admin/flores']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (e) => { this.error.set(mensajeError(e)); this.enviando.set(false); }
    });
  }
}
