package com.Floreria.seguridad;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class TokenService {

    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String crearToken(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    /** Devuelve el username dueño del token, o null si el token no existe. */
    public String obtenerUsuario(String token) {
        return token == null ? null : tokens.get(token);
    }

    public void revocar(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }
}
