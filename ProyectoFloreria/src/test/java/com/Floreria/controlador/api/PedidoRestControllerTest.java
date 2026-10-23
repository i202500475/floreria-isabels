package com.Floreria.controlador.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Pruebas del flujo de compra: registrar pedido, ver mis pedidos, cambiar estado y eliminar. */
@SpringBootTest
@AutoConfigureMockMvc
class PedidoRestControllerTest {

    @Autowired
    private MockMvc mvc;

    private long crearFlor(String admin, int stock) throws Exception {
        String json = mvc.perform(post("/api/flores").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Girasoles\",\"precio\":20.0,\"stock\":" + stock + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ApiTestUtil.numero(json, "$.id");
    }

    private String pedido(long florId, int cantidad) {
        return "{\"florId\":" + florId + ",\"cantidad\":" + cantidad
                + ",\"direccion\":\"Av. Arequipa 123\",\"metodoPago\":\"Yape\"}";
    }

    @Test
    @DisplayName("Flujo completo: comprar descuenta stock, aparece en mis pedidos, delivery lo entrega")
    void flujoCompleto() throws Exception {
        String admin = ApiTestUtil.bearer(mvc, "admin", "1234");
        long florId = crearFlor(admin, 5);
        ApiTestUtil.registrarCliente(mvc, "compra1", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "compra1", "clave123");

        // 1. Comprar 2 unidades
        String json = mvc.perform(post("/api/pedidos").header("Authorization", cliente)
                        .contentType(MediaType.APPLICATION_JSON).content(pedido(florId, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(40.0))
                .andExpect(jsonPath("$.estado").value("PAGADO"))
                .andExpect(jsonPath("$.cliente").value("compra1"))
                .andReturn().getResponse().getContentAsString();
        long pedidoId = ApiTestUtil.numero(json, "$.id");

        // 2. El stock bajó de 5 a 3
        mvc.perform(get("/api/flores/" + florId)).andExpect(jsonPath("$.stock").value(3));

        // 3. El pedido aparece en "mis pedidos"
        mvc.perform(get("/api/pedidos/mis-pedidos").header("Authorization", cliente))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(pedidoId));

        // 4. El repartidor lo marca como entregado
        String delivery = ApiTestUtil.bearer(mvc, "Deli", "very");
        mvc.perform(put("/api/pedidos/" + pedidoId + "/estado").header("Authorization", delivery)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ENTREGADO\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("ENTREGADO"));

        // 5. Un pedido entregado ya no se puede modificar
        mvc.perform(put("/api/pedidos/" + pedidoId + "/estado").header("Authorization", delivery)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"CANCELADO\"}"))
            .andExpect(status().isConflict());

        // 6. El admin lo elimina
        mvc.perform(delete("/api/pedidos/" + pedidoId).header("Authorization", admin))
            .andExpect(status().isOk());
        mvc.perform(get("/api/pedidos/" + pedidoId).header("Authorization", admin))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Comprar más de lo que hay en stock responde 409")
    void stockInsuficiente() throws Exception {
        String admin = ApiTestUtil.bearer(mvc, "admin", "1234");
        long florId = crearFlor(admin, 1);
        ApiTestUtil.registrarCliente(mvc, "compra2", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "compra2", "clave123");

        mvc.perform(post("/api/pedidos").header("Authorization", cliente)
                .contentType(MediaType.APPLICATION_JSON).content(pedido(florId, 3)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Un CLIENTE no puede ver todos los pedidos (403) y sin token es 401")
    void permisos() throws Exception {
        ApiTestUtil.registrarCliente(mvc, "compra3", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "compra3", "clave123");
        mvc.perform(get("/api/pedidos").header("Authorization", cliente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/pedidos/mis-pedidos")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Pedido con método de pago inválido responde 400")
    void metodoPagoInvalido() throws Exception {
        ApiTestUtil.registrarCliente(mvc, "compra4", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "compra4", "clave123");
        mvc.perform(post("/api/pedidos").header("Authorization", cliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"florId\":1,\"cantidad\":1,\"direccion\":\"Av. X\",\"metodoPago\":\"Bitcoin\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores.metodoPago").exists());
    }
    
    @Test
    @DisplayName("Un CLIENTE no puede cambiar el estado de un pedido (403)")
    void clienteNoPuedeCambiarEstado() throws Exception {
        String admin = ApiTestUtil.bearer(mvc, "admin", "1234");
        long florId = crearFlor(admin, 2);

        ApiTestUtil.registrarCliente(mvc, "compra5", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "compra5", "clave123");

        String json = mvc.perform(post("/api/pedidos")
                        .header("Authorization", cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedido(florId, 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long pedidoId = ApiTestUtil.numero(json, "$.id");

        mvc.perform(put("/api/pedidos/" + pedidoId + "/estado")
                        .header("Authorization", cliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CANCELADO\"}"))
                .andExpect(status().isForbidden());
    }

}
