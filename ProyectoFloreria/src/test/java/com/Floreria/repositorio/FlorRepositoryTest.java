package com.Floreria.repositorio;

import com.Floreria.modelo.Flor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la capa de acceso a datos (FlorRepository).
 * Cubre las 4 operaciones pedidas en la rúbrica: insertar, actualizar, eliminar y listar.
 * Cada prueba corre en una transacción que se revierte al terminar (base H2 en memoria).
 */
@DataJpaTest
class FlorRepositoryTest {

    @Autowired
    private FlorRepository florRepo;

    private Flor rosa;

    @BeforeEach
    void setUp() {
        florRepo.deleteAll();
        rosa = crearFlor("Ramo de rosas rojas", "12 rosas rojas con lazo", 89.90, 10);
    }

    private Flor crearFlor(String nombre, String descripcion, double precio, int stock) {
        Flor f = new Flor();
        f.setNombre(nombre);
        f.setDescripcion(descripcion);
        f.setPrecio(precio);
        f.setImagenUrl("https://ejemplo.com/" + nombre.replace(' ', '-') + ".jpg");
        f.setStock(stock);
        return f;
    }

    @Test
    @DisplayName("Insertar: guarda una flor y le asigna un ID")
    void insertarFlor() {
        Flor guardada = florRepo.save(rosa);

        assertNotNull(guardada.getId(), "La BD debe generar el ID");
        Optional<Flor> encontrada = florRepo.findById(guardada.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("Ramo de rosas rojas", encontrada.get().getNombre());
        assertEquals(89.90, encontrada.get().getPrecio());
        assertEquals(10, encontrada.get().getStock());
    }

    @Test
    @DisplayName("Actualizar: modifica precio y stock de una flor existente")
    void actualizarFlor() {
        Flor guardada = florRepo.save(rosa);

        guardada.setPrecio(99.50);
        guardada.setStock(3);
        florRepo.save(guardada);

        Flor actualizada = florRepo.findById(guardada.getId()).orElseThrow();
        assertEquals(99.50, actualizada.getPrecio());
        assertEquals(3, actualizada.getStock());
        assertEquals(1, florRepo.count(), "Actualizar no debe crear un registro nuevo");
    }

    @Test
    @DisplayName("Eliminar: borra la flor de la BD")
    void eliminarFlor() {
        Flor guardada = florRepo.save(rosa);
        Long id = guardada.getId();

        florRepo.deleteById(id);

        assertFalse(florRepo.findById(id).isPresent());
        assertEquals(0, florRepo.count());
    }

    @Test
    @DisplayName("Listar: devuelve todas las flores registradas")
    void listarFlores() {
        florRepo.save(rosa);
        florRepo.save(crearFlor("Girasoles", "Arreglo de 6 girasoles", 65.00, 5));
        florRepo.save(crearFlor("Tulipanes", "Caja de 10 tulipanes", 120.00, 0));

        List<Flor> lista = florRepo.findAll();

        assertEquals(3, lista.size());
        assertTrue(lista.stream().anyMatch(f -> f.getNombre().equals("Girasoles")));
        assertTrue(lista.stream().anyMatch(Flor::isAgotado), "Tulipanes tiene stock 0, debe figurar agotado");
    }
}
