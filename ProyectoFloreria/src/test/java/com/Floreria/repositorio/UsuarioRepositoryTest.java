package com.Floreria.repositorio;

import com.Floreria.modelo.Rol;
import com.Floreria.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica que el usuario se guarda con la contraseña cifrada (BCrypt) y se encuentra por username. */
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("La contraseña se guarda cifrada y el usuario se encuentra por username")
    void guardarUsuarioConPasswordCifrada() {
        Rol rol = rolRepo.save(new Rol("ROLE_CLIENTE"));

        Usuario u = new Usuario();
        u.setUsername("cliente1");
        u.setPassword(encoder.encode("clave123"));
        u.setNombreCompleto("Cliente de Prueba");
        u.setRoles(List.of(rol));
        usuarioRepo.save(u);

        Optional<Usuario> encontrado = usuarioRepo.findByUsername("cliente1");
        assertTrue(encontrado.isPresent());
        assertNotEquals("clave123", encontrado.get().getPassword(), "No debe guardarse en texto plano");
        assertTrue(encontrado.get().getPassword().startsWith("$2"), "Debe ser un hash BCrypt");
        assertTrue(encoder.matches("clave123", encontrado.get().getPassword()));
        assertEquals(1, encontrado.get().getRoles().size());
    }

    @Test
    @DisplayName("Buscar un username inexistente devuelve vacío")
    void buscarUsuarioInexistente() {
        assertTrue(usuarioRepo.findByUsername("noexiste").isEmpty());
    }
}
