package com.Floreria.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Floreria.modelo.Usuario;
import java.util.Optional; // Importante

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Al usar Optional, evitamos errores de NullPointerException
    Optional<Usuario> findByUsername(String username);
}