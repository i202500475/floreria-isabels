package com.Floreria.controlador.api;

import com.Floreria.dto.EstadoRequest;
import com.Floreria.dto.PedidoDTO;
import com.Floreria.dto.PedidoRequest;
import com.Floreria.servicio.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoService service;

    public PedidoRestController(PedidoService service) {
        this.service = service;
    }

    @GetMapping("/mis-pedidos")
    public List<PedidoDTO> misPedidos(Principal principal) {
        return service.listarPorCliente(principal.getName()).stream().map(PedidoDTO::desde).toList();
    }

    @GetMapping
    public List<PedidoDTO> listar() {
        return service.listarTodos().stream().map(PedidoDTO::desde).toList();
    }

    @GetMapping("/{id}")
    public PedidoDTO buscar(@PathVariable Long id) {
        return PedidoDTO.desde(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> registrar(@Valid @RequestBody PedidoRequest req, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PedidoDTO.desde(service.crear(principal.getName(), req)));
    }

    @PutMapping("/{id}/estado")
    public PedidoDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest req) {
        return PedidoDTO.desde(service.cambiarEstado(id, req.getEstado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado correctamente"));
    }
}
