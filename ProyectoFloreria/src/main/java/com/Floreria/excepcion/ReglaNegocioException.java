package com.Floreria.excepcion;

/** Se lanza cuando la operación no cumple una regla del negocio, por ejemplo stock insuficiente (la API responde 409). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
