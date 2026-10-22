package com.Floreria.repositorio;

import com.Floreria.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Si este método no está aquí, Java no sabe cómo filtrar por estado
    List<Pedido> findByEstado(String estado);

    // Último pedido de un cliente (para "Mis pedidos")
    Optional<Pedido> findTopByClienteUsernameOrderByIdDesc(String username);

    // Todos los pedidos de un cliente, del más reciente al más antiguo (API REST)
    List<Pedido> findByClienteUsernameOrderByIdDesc(String username);

    // Todos los pedidos, del más reciente al más antiguo (admin / delivery)
    List<Pedido> findAllByOrderByIdDesc();
}