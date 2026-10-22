package com.Floreria.controlador;

import com.Floreria.modelo.Usuario;
import com.Floreria.modelo.Rol;
import com.Floreria.repositorio.UsuarioRepository;
import com.Floreria.repositorio.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping
    public String mostrarFormularioRegistro() {
        return "registro";
    }

    @PostMapping("/guardar")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        // Validaciones básicas
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()
                || usuario.getPassword() == null || usuario.getPassword().length() < 4) {
            return "redirect:/registro?error=datos";
        }
        usuario.setUsername(usuario.getUsername().trim());
        if (usuarioRepo.findByUsername(usuario.getUsername()).isPresent()) {
            return "redirect:/registro?error=existe";
        }

        usuario.setPassword(encoder.encode(usuario.getPassword()));
        Rol rolCliente = rolRepo.findByNombre("ROLE_CLIENTE");
        
        if (rolCliente != null) {
            usuario.setRoles(Collections.singletonList(rolCliente));
        }

        usuarioRepo.save(usuario);
        return "redirect:/login?exito";
    }
}