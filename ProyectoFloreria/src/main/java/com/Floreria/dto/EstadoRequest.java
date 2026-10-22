package com.Floreria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Nuevo estado de un pedido: PUT /api/pedidos/{id}/estado */
public class EstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PAGADO|EN_CAMINO|ENTREGADO|CANCELADO",
             message = "Estado no válido (PAGADO, EN_CAMINO, ENTREGADO o CANCELADO)")
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
