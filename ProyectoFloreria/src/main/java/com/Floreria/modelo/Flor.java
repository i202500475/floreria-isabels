package com.Floreria.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "flores")
public class Flor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 
	private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    
   
    private int stock = 5;

    public Flor() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    // Lógica para la vista index
    public boolean isAgotado() {
        return this.stock <= 0;
    }
}