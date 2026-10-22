package com.Floreria.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // 1. Constructor vacío (Obligatorio para que Hibernate no de error)
    public Rol() {}

    // 2. ESTE ES EL QUE FALTA: Constructor para recibir el nombre
    public Rol(String nombre) {
        this.nombre = nombre;
    }

    // --- Tus Getters y Setters manuales que ya pusimos antes ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}