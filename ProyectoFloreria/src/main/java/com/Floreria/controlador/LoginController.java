package com.Floreria.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // Esto busca el archivo "login.html" en src/main/resources/templates
        return "login"; 
    }
}