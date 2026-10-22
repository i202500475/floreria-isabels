import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Solo deja pasar si el usuario inició sesión. */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  return auth.logueado() ? true : inject(Router).createUrlTree(['/login'], { queryParams: { volver: state.url } });
};

/** Solo deja pasar si el usuario tiene alguno de los roles indicados. */
export function roleGuard(...roles: string[]): CanActivateFn {
  return (_route, state) => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.logueado()) {
      return router.createUrlTree(['/login'], { queryParams: { volver: state.url } });
    }
    return auth.tieneRol(...roles) ? true : router.createUrlTree(['/']);
  };
}
