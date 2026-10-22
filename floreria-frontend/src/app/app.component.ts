import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html'
})
export class AppComponent {
  auth = inject(AuthService);
  private router = inject(Router);
  menuAbierto = false;

  salir(): void {
    this.auth.logout();
    this.menuAbierto = false;
    this.router.navigate(['/']);
  }
}
