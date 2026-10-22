package com.Floreria.controlador.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del servicio REST de login (POST /api/auth/login).
 * El usuario admin / 1234 lo crea DataInitializer al iniciar la aplicación.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String URL = "/api/auth/login";

    @Test
    @DisplayName("Login correcto devuelve 200 y los roles del usuario")
    void loginCorrecto() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"1234\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Registro de cliente nuevo devuelve 201 y luego puede iniciar sesión")
    void registroYLogin() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nuevo1\",\"password\":\"clave123\",\"nombreCompleto\":\"Cliente Nuevo\",\"correo\":\"nuevo@mail.com\"}"))
            .andExpect(status().isCreated());

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nuevo1\",\"password\":\"clave123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENTE"));
    }

    @Test
    @DisplayName("Registrar un usuario que ya existe devuelve 409")
    void registroDuplicado() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"clave123\",\"nombreCompleto\":\"Otro\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Registro con correo inválido devuelve 400")
    void registroCorreoInvalido() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nuevo2\",\"password\":\"clave123\",\"nombreCompleto\":\"X\",\"correo\":\"no-es-correo\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores.correo").exists());
    }

    @Test
    @DisplayName("Contraseña incorrecta devuelve 401")
    void passwordIncorrecta() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"malaclave\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Usuario inexistente devuelve 401")
    void usuarioInexistente() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"fantasma\",\"password\":\"1234\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Campos vacíos devuelven 400 con el detalle de la validación")
    void camposVacios() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"password\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores.username").exists())
            .andExpect(jsonPath("$.errores.password").exists());
    }

    @Test
    @DisplayName("JSON mal formado devuelve 400")
    void jsonInvalido() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{username:"))
            .andExpect(status().isBadRequest());
    }
}
