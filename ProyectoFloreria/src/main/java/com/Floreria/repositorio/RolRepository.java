package com.Floreria.repositorio;

import com.Floreria.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Añade esta línea exactamente así:
    Rol findByNombre(String nombre);
}