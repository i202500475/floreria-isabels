package com.Floreria.seguridad;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Genera y valida los tokens de acceso que usa el frontend Angular.
 * Al hacer login se crea un token aleatorio (UUID) asociado al usuario;
 * Angular lo envía en cada petición en la cabecera: Authorization: Bearer <token>.
 * Los tokens se guardan en memoria y se pierden al reiniciar el servidor.
 */
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
