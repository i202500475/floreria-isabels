package com.Floreria.controlador.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Pruebas de los servicios REST GET, POST, PUT y DELETE de /api/flores. */
@SpringBootTest
@AutoConfigureMockMvc
class FlorRestControllerTest {

    @Autowired
    private MockMvc mvc;

    private String admin;

    private static final String FLOR = "{\"nombre\":\"Orquídea blanca\",\"descripcion\":\"Maceta\",\"precio\":75.5,\"imagenUrl\":\"\",\"stock\":4}";

    @BeforeEach
    void login() throws Exception {
        admin = ApiTestUtil.bearer(mvc, "admin", "1234");
    }

    private long crearFlor() throws Exception {
        String json = mvc.perform(post("/api/flores").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content(FLOR))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ApiTestUtil.numero(json, "$.id");
    }

    @Test
    @DisplayName("GET /api/flores es público y devuelve una lista")
    void listarPublico() throws Exception {
        crearFlor();
        mvc.perform(get("/api/flores"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST crea la flor y GET por id la devuelve")
    void crearYBuscar() throws Exception {
        long id = crearFlor();
        mvc.perform(get("/api/flores/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Orquídea blanca"))
            .andExpect(jsonPath("$.stock").value(4));
    }

    @Test
    @DisplayName("PUT actualiza precio y stock")
    void actualizar() throws Exception {
        long id = crearFlor();
        mvc.perform(put("/api/flores/" + id).header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Orquídea blanca\",\"precio\":80.0,\"stock\":10}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.precio").value(80.0))
            .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    @DisplayName("DELETE elimina la flor y luego GET responde 404")
    void eliminar() throws Exception {
        long id = crearFlor();
        mvc.perform(delete("/api/flores/" + id).header("Authorization", admin))
            .andExpect(status().isOk());
        mvc.perform(get("/api/flores/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST sin token responde 401")
    void crearSinToken() throws Exception {
        mvc.perform(post("/api/flores").contentType(MediaType.APPLICATION_JSON).content(FLOR))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un CLIENTE no puede crear flores (403)")
    void clienteNoPuedeCrear() throws Exception {
        ApiTestUtil.registrarCliente(mvc, "cliflor", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "cliflor", "clave123");
        mvc.perform(post("/api/flores").header("Authorization", cliente)
                .contentType(MediaType.APPLICATION_JSON).content(FLOR))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST con datos inválidos responde 400 con el detalle")
    void datosInvalidos() throws Exception {
        mvc.perform(post("/api/flores").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\",\"precio\":-5,\"stock\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores.nombre").exists())
            .andExpect(jsonPath("$.errores.precio").exists())
            .andExpect(jsonPath("$.errores.stock").exists());
    }
    
    @Test
    @DisplayName("Un CLIENTE no puede actualizar flores (403)")
    void clienteNoPuedeActualizarFlor() throws Exception {
        long id = crearFlor();

        ApiTestUtil.registrarCliente(mvc, "cliactualiza", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "cliactualiza", "clave123");

        mvc.perform(put("/api/flores/" + id)
                .header("Authorization", cliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Flor modificada\",\"precio\":80.0,\"stock\":10}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un CLIENTE no puede eliminar flores (403)")
    void clienteNoPuedeEliminarFlor() throws Exception {
        long id = crearFlor();

        ApiTestUtil.registrarCliente(mvc, "clielimina", "clave123");
        String cliente = ApiTestUtil.bearer(mvc, "clielimina", "clave123");

        mvc.perform(delete("/api/flores/" + id)
                .header("Authorization", cliente))
            .andExpect(status().isForbidden());
    }
}
