package com.Floreria.controlador;

import com.Floreria.modelo.Flor;
import com.Floreria.modelo.Pedido;
import com.Floreria.modelo.Usuario;
import com.Floreria.servicio.FlorService;
import com.Floreria.servicio.PedidoService;

import jakarta.servlet.http.HttpSession;

import com.Floreria.repositorio.PedidoRepository;
import com.Floreria.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AppController {
	@Autowired
	private PedidoRepository pedidoRepo;
    
    @Autowired 
    private FlorService service;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // --- RUTAS PÚBLICAS  ---

    @GetMapping("/")
    public String index(Model m) {
        List<Flor> listaReal = service.listar();
        m.addAttribute("flores", listaReal);
        return "index";
    }

    @GetMapping("/flor/{id}")
    public String verDetalles(@PathVariable Long id, Model m) {
        Flor flor = service.buscar(id);
        m.addAttribute("flor", flor);
        return "detalle";
    }

    // --- RUTAS DE CLIENTES ---

    @GetMapping("/pago/{id}")
    public String verPago(@PathVariable Long id, Model m) {
        Flor flor = service.buscar(id);
        m.addAttribute("flor", flor);
        return "pago"; 
    }

    // La vista clientes/carrito aún no existe: por ahora se regresa al catálogo
    @GetMapping("/carrito")
    public String verCarrito() {
        return "redirect:/";
    }

    
    @GetMapping("/rastrear-delivery")
    public String verApartadoDelivery(Model m) {

        return "delivery/detalle";
    }
    
    
    
    @GetMapping("/mis-pedidos")
    public String verMisPedidos(Model model, Principal principal) {

        Pedido ultimoPedido = pedidoService.obtenerUltimo(principal.getName());

        if (ultimoPedido != null) {
            model.addAttribute("pedido", ultimoPedido);
            model.addAttribute("descripcion", ultimoPedido.getDescripcionProductos());
            model.addAttribute("codigoPedido", "ORD-" + ultimoPedido.getId());
            model.addAttribute("direccion", ultimoPedido.getDireccionEntrega());
            model.addAttribute("tiempoEstimado", 10);
        }

        // Nombre real del usuario logueado
        String nombre = usuarioRepo.findByUsername(principal.getName())
                .map(u -> u.getNombreCompleto() != null ? u.getNombreCompleto() : u.getUsername())
                .orElse(principal.getName());
        model.addAttribute("nombreUsuario", nombre);
        
        return "clientes/mis-pedidos"; 
    }
    
    
    
    
    

    @PostMapping("/procesar-pago")
    public String procesarPago(@RequestParam Long florId, 
                               @RequestParam String direccion,
                               @RequestParam(required = false, defaultValue = "Yape") String metodo,
                               Model m, Principal principal) {
        
        Flor flor = service.buscar(florId);
        
        if (flor == null) {
            return "redirect:/";
        }

        // 1. Validar y Descontar Stock
        if (flor.getStock() > 0) {
            flor.setStock(flor.getStock() - 1);
            service.guardar(flor); 
        } else {
            return "redirect:/?error=sin_stock";
        }

        // 2. GUARDAR EL PEDIDO REAL EN LA BASE DE DATOS
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setDireccionEntrega(direccion);
        // Asociamos el pedido al cliente que inició sesión
        usuarioRepo.findByUsername(principal.getName()).ifPresent(nuevoPedido::setCliente);
        nuevoPedido.setTotal(flor.getPrecio());
        nuevoPedido.setMetodoPago(metodo);
        nuevoPedido.setEstado("PAGADO"); // Aquí podrías usar "PENDIENTE" o "PAGADO"
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setDescripcionProductos("1x " + flor.getNombre());
        
        // Guardamos usando tu repositorio
        pedidoRepo.save(nuevoPedido); 

        // 3. Datos para la vista de éxito
        m.addAttribute("flor", flor);
        m.addAttribute("direccion", direccion);
        m.addAttribute("metodo", metodo);
        m.addAttribute("codigoPedido", "ORD-" + nuevoPedido.getId()); // Usamos el ID real de la BD
        m.addAttribute("tiempoEstimado", (int)(Math.random() * 11) + 5);

        return "exito"; 
    }
    
}