import { HttpErrorResponse } from '@angular/common/http';

/** Convierte la respuesta de error del backend en un texto para mostrar al usuario. */
export function mensajeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'No se pudo conectar con el servidor. Verifica que Spring Boot esté corriendo en el puerto 8080.';
    }
    const body = err.error as { mensaje?: string; errores?: Record<string, string> } | null;
    if (body?.errores) {
      return Object.values(body.errores).join(' · ');
    }
    if (body?.mensaje) {
      return body.mensaje;
    }
    if (err.status === 403) {
      return 'No tienes permiso para realizar esta acción.';
    }
  }
  return 'Ocurrió un error inesperado. Intenta nuevamente.';
}
