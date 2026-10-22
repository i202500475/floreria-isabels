# Proyecto Florería Isabel's – Entrega final (100 %)

## Contenido de la carpeta
| Carpeta / archivo | Qué es |
|---|---|
| `ProyectoFloreria/` | Backend Spring Boot 3 (Spring MVC, Spring Data JPA, Spring Security, MySQL) |
| `floreria-frontend/` | Frontend Angular 18 que consume la API REST |
| `floreria.sql` | Script de la base de datos MySQL |
| `Floreria_API.postman_collection.json` | Pruebas de todos los servicios REST en Postman |

## 1. Backend (Eclipse / STS)
1. Encender MySQL en XAMPP y crear/importar la base `floreria` (`floreria.sql`).
2. Importar `ProyectoFloreria` como proyecto Maven > clic derecho > **Maven > Update Project**.
3. **Run As > Spring Boot App** → queda en http://localhost:8080.
   Al iniciar se crean los usuarios `admin / 1234` (ADMIN) y `Deli / very` (DELIVERY).
4. Pruebas: clic derecho en `src/test/java` > **Run As > JUnit Test** (usan H2 en memoria, no necesitan XAMPP).

## 2. Frontend Angular
Requiere Node.js 18.19+ (recomendado 20 LTS).
```bash
cd floreria-frontend
npm install
npm start      # abre http://localhost:4200
```

## 3. Servicios REST
| Método | Ruta | Quién | Pantalla Angular |
|---|---|---|---|
| POST | /api/auth/login | público | Iniciar sesión |
| POST | /api/auth/registro | público | Crear cuenta |
| POST | /api/auth/logout | logueado | Salir |
| GET | /api/flores, /api/flores/{id} | público | Catálogo, Comprar |
| POST | /api/flores | ADMIN | Nueva flor |
| PUT | /api/flores/{id} | ADMIN | Editar flor, Reponer stock |
| DELETE | /api/flores/{id} | ADMIN | Eliminar flor |
| POST | /api/pedidos | CLIENTE/ADMIN | Pagar pedido |
| GET | /api/pedidos/mis-pedidos | logueado | Mis pedidos |
| GET | /api/pedidos | ADMIN/DELIVERY | Pedidos / Entregas |
| PUT | /api/pedidos/{id}/estado | ADMIN/DELIVERY | En camino / Entregado / Cancelar |
| DELETE | /api/pedidos/{id} | ADMIN | Eliminar pedido |

Autenticación: el login devuelve un `token`; Angular lo envía en la cabecera `Authorization: Bearer <token>`.
En Postman se puede usar el token o Basic Auth (`admin / 1234`).

## 4. Pruebas automatizadas (JUnit)
- `FlorRepositoryTest` – insertar, actualizar, eliminar, listar (capa de datos)
- `UsuarioRepositoryTest` – contraseña cifrada con BCrypt
- `AuthRestControllerTest` – login (200/401/400), registro (201/409/400)
- `FlorRestControllerTest` – GET, POST, PUT, DELETE, 401 sin token, 403 cliente, 400 validaciones
- `PedidoRestControllerTest` – compra, stock, mis pedidos, cambio de estado, eliminación, permisos

## 5. Problemas comunes
- **Angular muestra "No se pudo conectar con el servidor"**: Spring Boot no está corriendo en el puerto 8080.
- **Error CORS**: Angular debe abrirse en http://localhost:4200 (es el origen permitido en `SecurityConfig`).
- **Tras reiniciar Spring Boot pide login de nuevo**: es normal, los tokens se guardan en memoria.
- **No se puede eliminar una flor (409)**: la flor ya tiene pedidos asociados.

## 6. Publicar en Azure (Azure for Students)

**Publicado en:** https://floreria-isabels-c6495e.azurewebsites.net/tienda/ (región Chile Central).

**Forma rápida (usada):** en Azure Cloud Shell (Bash) ejecutar
```bash
curl -sLo desplegar.sh https://raw.githubusercontent.com/i202500475/floreria-isabels/main/azure/desplegar.sh
LOC_OVERRIDE=chilecentral bash desplegar.sh
```
El script crea MySQL servidor flexible B1ms, App Service B1 (Java 17), configura las variables, compila Angular + JAR y publica. Al final muestra la URL y la clave del usuario admin.

> En la suscripción de estudiante solo están permitidas eastus, brazilsouth, canadacentral, westus y chilecentral, y MySQL B1ms solo estaba disponible en **chilecentral**.

**Para borrar todo después de la sustentación** (deja de consumir crédito): `az group delete -n floreria-rg --yes`

### Forma manual (portal)

La tienda se publica como **una sola aplicación**: el JAR de Spring Boot incluye Angular en `/tienda/`.
La URL final queda así: `https://<nombre-app>.azurewebsites.net/tienda/`

### 6.1 Base de datos: Azure Database for MySQL – Servidor flexible
1. Portal de Azure → **Crear un recurso** → *Azure Database for MySQL – Servidor flexible*.
2. Tipo de carga: **Para proyectos de desarrollo o pasatiempos** (Burstable **B1ms**, incluido en la capa gratuita de estudiantes).
3. Región: una permitida por tu suscripción de estudiante (por ejemplo *East US 2* o *Brazil South*).
4. Usuario administrador y contraseña: anótalos.
5. **Redes** → Acceso público → marcar **"Permitir acceso público desde cualquier servicio de Azure"**.
6. No hace falta crear la base `floreria`: la aplicación la crea sola (`createDatabaseIfNotExist=true`).

### 6.2 Aplicación: App Service
1. **Crear un recurso** → *Aplicación web*.
2. Publicar: **Código** · Pila: **Java 17** · Servidor: **Java SE (servidor web integrado)** · SO: **Linux**.
3. Plan: **Gratis F1** (lento al iniciar) o **Básico B1** (usa crédito de estudiante, más estable).
4. Misma región que la base de datos.
5. Al crear: **Configuración → Configuración general → Credenciales de publicación básicas de SCM: Activado** (necesario para el perfil de publicación).

### 6.3 Variables de entorno del App Service
App Service → **Configuración → Variables de entorno** → agregar:

| Nombre | Valor |
|---|---|
| `DB_URL` | `jdbc:mysql://<servidor>.mysql.database.azure.com:3306/floreria?createDatabaseIfNotExist=true&sslMode=REQUIRED&serverTimezone=UTC` |
| `DB_USERNAME` | usuario administrador de MySQL |
| `DB_PASSWORD` | contraseña de MySQL |
| `ADMIN_PASSWORD` | contraseña para el usuario `admin` de la tienda (no uses 1234 en internet) |
| `SERVER_PORT` | `80` |

### 6.4 Publicar desde GitHub
1. App Service → **Información general → Descargar perfil de publicación**.
2. GitHub → repositorio → **Settings → Secrets and variables → Actions**:
   - *Secrets* → `AZURE_WEBAPP_PUBLISH_PROFILE` = contenido completo del archivo descargado.
   - *Variables* → `AZURE_WEBAPP_NAME` = nombre del App Service.
3. GitHub → **Actions → Publicar en Azure → Run workflow**.
4. Abrir `https://<nombre-app>.azurewebsites.net/tienda/` (el primer arranque puede tardar 1–2 minutos).
