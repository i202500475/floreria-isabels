export interface Flor {
  id?: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  imagenUrl?: string;
  stock: number;
  agotado?: boolean;
}

export interface Sesion {
  id: number;
  username: string;
  nombreCompleto: string;
  roles: string[];
  token: string;
}

export interface RegistroRequest {
  username: string;
  password: string;
  nombreCompleto: string;
  correo: string;
  direccion: string;
}

export interface Pedido {
  id: number;
  codigo: string;
  cliente: string;
  direccionEntrega: string;
  descripcion: string;
  total: number;
  estado: string;
  metodoPago: string;
  fechaCreacion: string;
}

export interface PedidoRequest {
  florId: number;
  cantidad: number;
  direccion: string;
  metodoPago: string;
}

export const ESTADOS: string[] = ['PAGADO', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO'];
export const METODOS_PAGO: string[] = ['Yape', 'Plin', 'Tarjeta', 'Efectivo'];
