package com.Floreria.servicio;

import com.Floreria.modelo.Usuario;
import com.Floreria.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Mapeamos los roles asegurando que tengan el prefijo ROLE_
        List<GrantedAuthority> authorities = usuario.getRoles().stream()
            .map(rol -> {
                String nombreRol = rol.getNombre().toUpperCase();
                // Si el rol en la BD no tiene ROLE_, se lo ponemos nosotros
                if (!nombreRol.startsWith("ROLE_")) {
                    nombreRol = "ROLE_" + nombreRol;
                }
                return new SimpleGrantedAuthority(nombreRol);
            })
            .collect(Collectors.toList());

        return new User(
            usuario.getUsername(), 
            usuario.getPassword(),
            authorities
        );
    }
}