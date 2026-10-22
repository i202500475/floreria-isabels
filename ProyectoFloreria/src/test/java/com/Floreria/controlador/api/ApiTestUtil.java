package com.Floreria.controlador.api;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Utilidades comunes para las pruebas de la API. */
final class ApiTestUtil {

    private ApiTestUtil() {}

    /** Hace login por la API y devuelve la cabecera "Bearer <token>". */
    static String bearer(MockMvc mvc, String username, String password) throws Exception {
        String json = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(json, "$.token");
    }

    /** Registra un cliente nuevo por la API. */
    static void registrarCliente(MockMvc mvc, String username, String password) throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password
                        + "\",\"nombreCompleto\":\"Cliente " + username + "\"}"))
                .andExpect(status().isCreated());
    }

    /** Extrae un número (id, stock...) de una respuesta JSON. */
    static long numero(String json, String path) {
        return ((Number) JsonPath.read(json, path)).longValue();
    }
}
