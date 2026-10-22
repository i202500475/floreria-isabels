package com.Floreria.repositorio;
import com.Floreria.modelo.Flor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlorRepository extends JpaRepository<Flor, Long> { }