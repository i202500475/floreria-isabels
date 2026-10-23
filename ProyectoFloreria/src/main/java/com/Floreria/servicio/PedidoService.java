package com.Floreria.servicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Floreria.dto.PedidoRequest;
import com.Floreria.excepcion.RecursoNoEncontradoException;
import com.Floreria.excepcion.ReglaNegocioException;
import com.Floreria.modelo.Flor;
import com.Floreria.modelo.Pedido;
import com.Floreria.modelo.Usuario;
import com.Floreria.repositorio.FlorRepository;
import com.Floreria.repositorio.PedidoRepository;
import com.Floreria.repositorio.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private FlorRepository florRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Transactional
    public void procesarCompra(Usuario cliente, String direccion, Double total, String descripcion) {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        nuevoPedido.setDireccionEntrega(direccion);
        nuevoPedido.setTotal(total);
        nuevoPedido.setEstado("PENDIENTE");
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setDescripcionProductos(descripcion);
        pedidoRepo.save(nuevoPedido);
    }

    // Devuelve el último pedido del usuario logueado (o null si aún no compra)
    public Pedido obtenerUltimo(String username) {
        return pedidoRepo.findTopByClienteUsernameOrderByIdDesc(username).orElse(null);
    }

    // ===================== Métodos usados por la API REST =====================

    /** Registra una compra: valida stock, lo descuenta y guarda el pedido (todo en una transacción). */
    @Transactional
    public Pedido crear(String username, PedidoRequest req) {
        Usuario cliente = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
        Flor flor = florRepo.findById(req.getFlorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una flor con id " + req.getFlorId()));

        if (flor.getStock() < req.getCantidad()) {
            throw new ReglaNegocioException("Stock insuficiente para " + flor.getNombre()
                    + " (disponible: " + flor.getStock() + ")");
        }
        flor.setStock(flor.getStock() - req.getCantidad());
        florRepo.save(flor);

        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setDireccionEntrega(req.getDireccion().trim());
        p.setMetodoPago(req.getMetodoPago());
        p.setTotal(Math.round(flor.getPrecio() * req.getCantidad() * 100.0) / 100.0);
        p.setEstado("PAGADO");
        p.setFechaCreacion(LocalDateTime.now());
        p.setDescripcionProductos(req.getCantidad() + "x " + flor.getNombre());
        p.setProductos(new ArrayList<>(List.of(flor)));
        return pedidoRepo.save(p);
    }

    public List<Pedido> listarPorCliente(String username) {
        return pedidoRepo.findByClienteUsernameOrderByIdDesc(username);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepo.findAllByOrderByIdDesc();
    }

    public Pedido buscar(Long id) {
        return pedidoRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido " + id));
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String estado) {
        Pedido p = buscar(id);
        if ("ENTREGADO".equals(p.getEstado()) || "CANCELADO".equals(p.getEstado())) {
            throw new ReglaNegocioException("El pedido ya está " + p.getEstado() + " y no se puede modificar");
        }
        p.setEstado(estado);
        return pedidoRepo.save(p);
    }

    @Transactional
    public void eliminar(Long id) {
        pedidoRepo.delete(buscar(id));
    }
}
