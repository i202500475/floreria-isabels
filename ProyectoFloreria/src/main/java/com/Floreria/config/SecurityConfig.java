package com.Floreria.config;

import com.Floreria.seguridad.TokenAuthFilter;
import com.Floreria.seguridad.TokenService;
import com.Floreria.servicio.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenService tokenService;

    // Orígenes permitidos para CORS, separados por coma
    @Value("${floreria.cors.origenes:http://localhost:4200}")
    private List<String> origenesCors;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // 1. RECURSOS ESTÁTICOS
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()


                .requestMatchers("/error").permitAll()

                .requestMatchers("/tienda", "/tienda/**").permitAll()

                // 2. API REST (consumida por Angular con "Authorization: Bearer <token>")
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/flores/**").permitAll()
                .requestMatchers("/api/flores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/pedidos/mis-pedidos").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasRole("ADMIN")
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "DELIVERY")
                .requestMatchers("/api/**").authenticated()

                // 3. PÚBLICO
                .requestMatchers("/", "/index", "/login", "/login?error", "/registro/**", "/flor/**").permitAll()

                // 4. PROTEGIDO POR ROLES
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/delivery/**").hasRole("DELIVERY")
                .requestMatchers("/pago/**", "/carrito/**", "/mis-pedidos/**", "/procesar-pago/**")
                    .hasAnyRole("CLIENTE", "ADMIN", "DELIVERY")

                // 5. TODO LO DEMÁS
                .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/**")))

            .addFilterBefore(new TokenAuthFilter(tokenService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults())   // Postman: Authorization > Basic Auth (admin / 1234)
            .formLogin(login -> login
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    boolean isDelivery = authentication.getAuthorities().stream()
                            .anyMatch(role -> role.getAuthority().equals("ROLE_DELIVERY"));
                    if (isDelivery) {
                        response.sendRedirect("/delivery/pedidos");
                    } else {
                        response.sendRedirect("/");
                    }
                })
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenesCors);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
