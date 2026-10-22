package com.Floreria.controlador.api;

import com.Floreria.dto.FlorDTO;
import com.Floreria.modelo.Flor;
import com.Floreria.servicio.FlorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Servicios REST del catálogo de flores (GET, POST, PUT y DELETE).
 * Todas las operaciones persisten en la base de datos MySQL "floreria".
 * Estos servicios serán consumidos por el frontend en Angular.
 */
@RestController
@RequestMapping("/api/flores")
public class FlorRestController {

    private final FlorService service;

    public FlorRestController(FlorService service) {
        this.service = service;
    }

    // GET /api/flores  -> listar todas
    @GetMapping
    public List<Flor> listar() {
        return service.listar();
    }

    // GET /api/flores/{id} -> buscar una
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Flor flor = service.buscar(id);
        if (flor == null) {
            return noEncontrada(id);
        }
        return ResponseEntity.ok(flor);
    }

    // POST /api/flores -> registrar
    @PostMapping
    public ResponseEntity<Flor> registrar(@Valid @RequestBody FlorDTO dto) {
        Flor flor = new Flor();
        copiar(dto, flor);
        service.guardar(flor);
        return ResponseEntity.status(HttpStatus.CREATED).body(flor);
    }

    // PUT /api/flores/{id} -> actualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody FlorDTO dto) {
        Flor flor = service.buscar(id);
        if (flor == null) {
            return noEncontrada(id);
        }
        copiar(dto, flor);
        service.guardar(flor);
        return ResponseEntity.ok(flor);
    }

    // DELETE /api/flores/{id} -> eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (service.buscar(id) == null) {
            return noEncontrada(id);
        }
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Flor eliminada correctamente"));
    }

    private void copiar(FlorDTO dto, Flor flor) {
        flor.setNombre(dto.getNombre().trim());
        flor.setDescripcion(dto.getDescripcion());
        flor.setPrecio(dto.getPrecio());
        flor.setImagenUrl(dto.getImagenUrl());
        flor.setStock(dto.getStock());
    }

    private ResponseEntity<Map<String, String>> noEncontrada(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "No existe una flor con id " + id));
    }
}
