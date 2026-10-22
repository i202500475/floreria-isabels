package com.Floreria.dto;

import com.Floreria.modelo.Pedido;
import java.time.LocalDateTime;

/**
 * Datos de un pedido que devuelve la API.
 * Se usa un DTO para NO exponer la entidad Usuario (que contiene la contraseña cifrada).
 */
public class PedidoDTO {
    private Long id;
    private String codigo;
    private String cliente;
    private String direccionEntrega;
    private String descripcion;
    private Double total;
    private String estado;
    private String metodoPago;
    private LocalDateTime fechaCreacion;

    public static PedidoDTO desde(Pedido p) {
        PedidoDTO d = new PedidoDTO();
        d.id = p.getId();
        d.codigo = "ORD-" + p.getId();
        d.cliente = p.getCliente() != null ? p.getCliente().getUsername() : "Invitado";
        d.direccionEntrega = p.getDireccionEntrega();
        d.descripcion = p.getDescripcionProductos();
        d.total = p.getTotal();
        d.estado = p.getEstado();
        d.metodoPago = p.getMetodoPago();
        d.fechaCreacion = p.getFechaCreacion();
        return d;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public String getDescripcion() { return descripcion; }
    public Double getTotal() { return total; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
