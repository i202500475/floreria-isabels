# Frontend Angular – Florería Isabel's

Aplicación Angular 18 (componentes standalone) que consume la API REST del backend Spring Boot.

## Requisitos
- Node.js 18.19 o superior (recomendado 20 LTS)
- Backend Spring Boot corriendo en http://localhost:8080 (con MySQL encendido)

## Cómo ejecutar
```bash
npm install
npm start          # abre http://localhost:4200
```

## Usuarios de prueba
| Usuario | Clave | Rol |
|---|---|---|
| admin | 1234 | ROLE_ADMIN (gestiona flores y pedidos) |
| Deli | very | ROLE_DELIVERY (marca entregas) |
| (crear en "Crear cuenta") | | ROLE_CLIENTE (compra) |

## Servicios REST que consume
| Pantalla | Servicio |
|---|---|
| Iniciar sesión | POST /api/auth/login |
| Crear cuenta | POST /api/auth/registro |
| Salir | POST /api/auth/logout |
| Catálogo | GET /api/flores |
| Comprar | GET /api/flores/{id}, POST /api/pedidos |
| Mis pedidos | GET /api/pedidos/mis-pedidos |
| Gestionar flores | GET, POST, PUT, DELETE /api/flores |
| Pedidos / Entregas | GET /api/pedidos, PUT /api/pedidos/{id}/estado, DELETE /api/pedidos/{id} |

## Estructura
```
src/app/
  core/       servicios HTTP, modelos, interceptor del token y guards
  pages/      una carpeta por pantalla
```
