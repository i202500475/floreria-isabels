package com.Floreria.excepcion;

/** Se lanza cuando no existe el registro buscado (la API responde 404). */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
