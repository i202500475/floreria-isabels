package com.Floreria.repositorio;

import com.Floreria.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(String estado);


    Optional<Pedido> findTopByClienteUsernameOrderByIdDesc(String username);


    List<Pedido> findByClienteUsernameOrderByIdDesc(String username);


    List<Pedido> findAllByOrderByIdDesc();
}