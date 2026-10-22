import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards';
import { CatalogoComponent } from './pages/catalogo/catalogo.component';
import { ComprarComponent } from './pages/comprar/comprar.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroComponent } from './pages/registro/registro.component';
import { MisPedidosComponent } from './pages/mis-pedidos/mis-pedidos.component';
import { AdminFloresComponent } from './pages/admin-flores/admin-flores.component';
import { FlorFormComponent } from './pages/flor-form/flor-form.component';
import { AdminPedidosComponent } from './pages/admin-pedidos/admin-pedidos.component';

export const routes: Routes = [
  { path: '', component: CatalogoComponent, title: "Isabel's | Catálogo" },
  { path: 'flor/:id', component: ComprarComponent, title: "Isabel's | Comprar" },
  { path: 'login', component: LoginComponent, title: "Isabel's | Iniciar sesión" },
  { path: 'registro', component: RegistroComponent, title: "Isabel's | Registro" },
  { path: 'mis-pedidos', component: MisPedidosComponent, canActivate: [authGuard], title: "Isabel's | Mis pedidos" },
  { path: 'admin/flores', component: AdminFloresComponent, canActivate: [roleGuard('ROLE_ADMIN')], title: 'Admin | Flores' },
  { path: 'admin/flores/nueva', component: FlorFormComponent, canActivate: [roleGuard('ROLE_ADMIN')], title: 'Admin | Nueva flor' },
  { path: 'admin/flores/:id/editar', component: FlorFormComponent, canActivate: [roleGuard('ROLE_ADMIN')], title: 'Admin | Editar flor' },
  { path: 'admin/pedidos', component: AdminPedidosComponent, canActivate: [roleGuard('ROLE_ADMIN', 'ROLE_DELIVERY')], title: 'Pedidos' },
  { path: '**', redirectTo: '' }
];
