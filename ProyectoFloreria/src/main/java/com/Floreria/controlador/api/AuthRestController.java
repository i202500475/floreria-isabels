package com.Floreria.controlador.api;

import com.Floreria.dto.LoginRequest;
import com.Floreria.dto.LoginResponse;
import com.Floreria.dto.RegistroRequest;
import com.Floreria.modelo.Rol;
import com.Floreria.modelo.Usuario;
import com.Floreria.repositorio.RolRepository;
import com.Floreria.repositorio.UsuarioRepository;
import com.Floreria.seguridad.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio web REST de login.
 * El usuario y la contraseña se validan contra la tabla "usuarios" de MySQL.
 * La contraseña está cifrada con BCryptPasswordEncoder, por eso se compara con encoder.matches().
 * Si el login es correcto se devuelve un token que Angular envía en cada petición.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthRestController(UsuarioRepository usuarioRepo, RolRepository rolRepo,
                              BCryptPasswordEncoder encoder, TokenService tokenService) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    // POST http://localhost:8080/api/auth/login
    // Body JSON: { "username": "admin", "password": "1234" }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        Optional<Usuario> usuarioOpt = usuarioRepo.findByUsername(request.getUsername().trim());

        // Mismo mensaje para "no existe" y "clave incorrecta" (buena práctica de seguridad)
        if (usuarioOpt.isEmpty() || !encoder.matches(request.getPassword(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Usuario o contraseña incorrectos"));
        }

        Usuario usuario = usuarioOpt.get();
        List<String> roles = usuario.getRoles() == null ? List.of()
                : usuario.getRoles().stream().map(Rol::getNombre).toList();

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                roles,
                tokenService.crearToken(usuario.getUsername()),
                "Login correcto"));
    }

    // POST /api/auth/registro -> crea un cliente nuevo (contraseña cifrada con BCrypt)
    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@Valid @RequestBody RegistroRequest req) {
        String username = req.getUsername().trim();
        if (usuarioRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El usuario " + username + " ya está registrado"));
        }
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(encoder.encode(req.getPassword()));
        u.setNombreCompleto(req.getNombreCompleto().trim());
        u.setCorreo(req.getCorreo());
        u.setDireccion(req.getDireccion());
        Rol rolCliente = rolRepo.findByNombre("ROLE_CLIENTE");
        if (rolCliente != null) {
            u.setRoles(List.of(rolCliente));
        }
        usuarioRepo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario registrado correctamente", "username", username));
    }

    // POST /api/auth/logout -> invalida el token
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null && header.startsWith("Bearer ")) {
            tokenService.revocar(header.substring(7).trim());
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada"));
    }
}
