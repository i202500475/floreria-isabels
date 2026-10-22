package com.Floreria.controlador;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Floreria.modelo.Pedido;
import com.Floreria.repositorio.PedidoRepository;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private PedidoRepository pedidoRepo;
    
    
    

    // 1. Dashboard Principal: Muestra pedidos PENDIENTES o EN_PREPARACION
    @GetMapping("/pedidos")
    public String verPedidos(Model model) {
        // Asumiendo que tienes este método en tu repositorio, si no, usa findAll() 
        // y filtra por lógica, pero aquí te lo dejo funcional:
        List<Pedido> lista = pedidoRepo.findAll(); 
        model.addAttribute("pedidos", lista);
        return "delivery/pedidos"; 
    }

    
    
    
    // 2. NUEVO: Vista de Detalle (La Hoja de Ruta con el Mapa)
    
    
    @GetMapping("/detalle")
    public String verDetallePedido(@RequestParam("id") Long id, Model model) {
        Pedido pedido = pedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
        
        // Cambiamos el estado solo si es PENDIENTE
        if ("PENDIENTE".equals(pedido.getEstado())) {
            pedido.setEstado("EN_CAMINO");
            pedidoRepo.save(pedido);
        }
        
        // Agregamos una validación extra para evitar el error 500 en la vista
        model.addAttribute("pedido", pedido);
        
        // Si el cliente es nulo, podrías imprimir un log para saber cuál pedido falla
        if (pedido.getCliente() == null) {
            System.out.println("OJO: El pedido #" + id + " no tiene un cliente asignado.");
        }
        
        return "delivery/detalle"; 
    }
    
    

    // 3. Confirmar entrega (Botón verde en la Hoja de Ruta)
    @PostMapping("/completar/{id}")
    public String completarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepo.findById(id).orElseThrow();
        pedido.setEstado("ENTREGADO");
        pedidoRepo.save(pedido);
        // Redirigimos al panel principal con un mensaje de éxito
        return "redirect:/delivery/pedidos?entregado=true";
    }
    
    
    

    // --- Métodos de limpieza (Opcionales si ya no usas las rutas antiguas) ---

    @PostMapping("/notificar/{id}")
    public String enviarVoucher(@PathVariable Long id) {
        Pedido pedido = pedidoRepo.findById(id).orElseThrow();
        pedido.setEstado("EN_CAMINO");
        pedidoRepo.save(pedido);
        return "redirect:/delivery/pedidos?notificado=true";
    }
    
}