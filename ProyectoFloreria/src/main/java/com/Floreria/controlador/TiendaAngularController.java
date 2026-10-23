package com.Floreria.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


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
