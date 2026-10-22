package com.Floreria.controlador;

import com.Floreria.modelo.Flor;
import com.Floreria.repositorio.FlorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FlorRepository florRepo;

    // 1. Ver el Panel Principal (panel.html)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Flor> listaFlores = florRepo.findAll();
        model.addAttribute("flores", listaFlores);
        return "admin/panel"; // Nombre exacto de tu archivo panel.html
    }

    // 2. Mostrar Formulario para Nueva Flor
    @GetMapping("/nuevo")
    public String nuevaFlor(Model model) {
        model.addAttribute("flor", new Flor());
        return "admin/formulario"; // Nombre de tu archivo formulario-html
    }

    // 3. Guardar o Actualizar Flor
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("flor") Flor flor) {
        florRepo.save(flor);
        return "redirect:/admin/dashboard";
    }

    // 4. Editar Flor Existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Flor flor = florRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("flor", flor);
        return "admin/formulario";
    }

    // 5. Eliminar Flor
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        florRepo.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // 6. Botón rápido para reponer stock (+5)
    @PostMapping("/reponer/{id}")
    public String reponer(@PathVariable("id") Long id) {
        Flor flor = florRepo.findById(id).get();
        flor.setStock(flor.getStock() + 5);
        florRepo.save(flor);
        return "redirect:/admin/dashboard";
    }
}