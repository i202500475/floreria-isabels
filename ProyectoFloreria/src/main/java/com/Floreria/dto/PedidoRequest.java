package com.Floreria.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para registrar una compra: POST /api/pedidos */
public class PedidoRequest {

    @NotNull(message = "Debe indicar la flor")
    private Long florId;

    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 10, message = "La cantidad máxima por pedido es 10")
    private int cantidad = 1;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 200, message = "La dirección no debe superar 200 caracteres")
    private String direccion;

    @NotBlank(message = "El método de pago es obligatorio")
    @Pattern(regexp = "Yape|Plin|Tarjeta|Efectivo", message = "Método de pago no válido (Yape, Plin, Tarjeta o Efectivo)")
    private String metodoPago;

    public Long getFlorId() { return florId; }
    public void setFlorId(Long florId) { this.florId = florId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
