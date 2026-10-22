# Florería Isabel's – E-commerce (DAWII, Cibertec 2026-II)

Tienda virtual con **Spring Boot 3** (API REST, Spring Security + BCrypt, Spring Data JPA, MySQL) y **Angular 18**.

**En línea (Azure):** https://floreria-isabels-c6495e.azurewebsites.net/tienda/

| Usuario | Rol |
|---|---|
| `admin` (clave entregada al docente) | Administrador |
| `Deli` / `very` | Repartidor |
| Crear cuenta en la tienda | Cliente |

## Verificación automática (GitHub Actions)
- **CI – pruebas y compilación:** 26 pruebas JUnit y compilación de Angular en cada cambio.
- **Evidencias – prueba integral:** levanta la app con MySQL 8, ejecuta la colección de Postman con newman (24/24) y recorre la tienda en un navegador real (Playwright). Capturas y resultados en la rama [`evidencias`](../../tree/evidencias).

## Estructura
- `ProyectoFloreria/` – backend Spring Boot
- `floreria-frontend/` – frontend Angular
- `azure/desplegar.sh` – despliegue en Azure desde Cloud Shell
- `INSTRUCCIONES.md` – instalación local y publicación en Azure
