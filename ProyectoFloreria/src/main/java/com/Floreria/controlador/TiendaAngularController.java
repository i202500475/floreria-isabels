package com.Floreria.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve la aplicación Angular publicada en /tienda/ (carpeta static/tienda del JAR).
 * Las rutas internas de Angular (por ejemplo /tienda/mis-pedidos) se reenvían a index.html
 * para que el router de Angular las resuelva. Las rutas con punto (archivos .js, .css) no
 * entran aquí: las sirve Spring Boot como recursos estáticos.
 */
@Controller
public class TiendaAngularController {

    @GetMapping({
        "/tienda",
        "/tienda/",
        "/tienda/{a:[^\\.]*}",
        "/tienda/{a:[^\\.]*}/{b:[^\\.]*}",
        "/tienda/{a:[^\\.]*}/{b:[^\\.]*}/{c:[^\\.]*}",
        "/tienda/{a:[^\\.]*}/{b:[^\\.]*}/{c:[^\\.]*}/{d:[^\\.]*}"
    })
    public String tienda() {
        return "forward:/tienda/index.html";
    }
}
