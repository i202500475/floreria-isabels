package com.Floreria.dto;

import java.util.List;


public class LoginResponse {
    private Long id;
    private String username;
    private String nombreCompleto;
    private List<String> roles;
    private String token;
    private String mensaje;

    public LoginResponse(Long id, String username, String nombreCompleto, List<String> roles, String token, String mensaje) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.roles = roles;
        this.token = token;
        this.mensaje = mensaje;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public List<String> getRoles() { return roles; }
    public String getToken() { return token; }
    public String getMensaje() { return mensaje; }
}
