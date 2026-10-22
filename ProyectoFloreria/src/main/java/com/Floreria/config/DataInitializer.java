package com.Floreria.config;

import com.Floreria.modelo.Flor;
import com.Floreria.modelo.Rol;
import com.Floreria.modelo.Usuario;
import com.Floreria.repositorio.FlorRepository;
import com.Floreria.repositorio.RolRepository;
import com.Floreria.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;

/**
 * Datos iniciales: roles, usuarios admin y repartidor, y (si la tabla está vacía)
 * el catálogo de ejemplo de la florería. Se ejecuta al iniciar la aplicación.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private FlorRepository florRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Value("${floreria.admin.password:1234}")
    private String adminPassword;

    @Value("${floreria.seed-catalogo:true}")
    private boolean seedCatalogo;

    @Override
    public void run(String... args) throws Exception {
        // 1. Roles con el prefijo ROLE_
        crearSiNoExiste("ROLE_ADMIN");
        crearSiNoExiste("ROLE_CLIENTE");
        crearSiNoExiste("ROLE_DELIVERY");

        // 2. Usuario administrador (contraseña configurable con ADMIN_PASSWORD)
        if (usuarioRepo.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode(adminPassword));
            admin.setNombreCompleto("Admin Principal");
            admin.setRoles(Collections.singletonList(rolRepo.findByNombre("ROLE_ADMIN")));
            usuarioRepo.save(admin);
            System.out.println("--> Usuario ROLE_ADMIN creado con éxito");
        }

        // 3. Usuario repartidor
        if (usuarioRepo.findByUsername("Deli").isEmpty()) {
            Usuario deli = new Usuario();
            deli.setUsername("Deli");
            deli.setPassword(encoder.encode("very"));
            deli.setNombreCompleto("Repartidor");
            deli.setRoles(Collections.singletonList(rolRepo.findByNombre("ROLE_DELIVERY")));
            usuarioRepo.save(deli);
            System.out.println("--> Usuario ROLE_DELIVERY creado con éxito");
        }

        // 4. Catálogo de ejemplo (mismos productos de floreria.sql)
        if (seedCatalogo && florRepo.count() == 0) {
            flor("Ramo de Rosas", "Ramito de rosas para regalar a tu pareja.", 15, "https://i.pinimg.com/1200x/4b/0a/d0/4b0ad00e307edbed46cfe121cfcfcd6e.jpg");
            flor("Girasoles", "Al igual que el girasol, mi corazón siempre gira hacia ti.", 22, "https://i.pinimg.com/736x/5a/03/51/5a0351d6308356d22ff42d9dd8d1849d.jpg");
            flor("Bouquet", "Flores pequeñas para momentos bonitos.", 8, "https://i.pinimg.com/736x/65/ff/b1/65ffb12bfdf5c8e92ca55256b58802e7.jpg");
            flor("Flores HB", "Especial para cumpleaños.", 30, "https://i.pinimg.com/736x/6e/6e/c2/6e6ec260acb53ed3f5e1aa66f590d952.jpg");
            flor("Orquídeas", "Hermosas orquídeas para sorprender.", 15, "https://i.pinimg.com/736x/25/ec/48/25ec487e5a740a85522e29e17ebe670a.jpg");
            flor("Tulipanes", "Bellos y accesibles.", 25, "https://i.pinimg.com/736x/26/50/70/2650708295f9ffc442cfa34d8d42fdf8.jpg");
            flor("Dalias y lavanda", "Flores con muchos pétalos.", 15, "https://i.pinimg.com/1200x/bf/4a/19/bf4a19bfe632f0732278707b81e75212.jpg");
            flor("Lirios", "Conocidos por su forma de trompeta y su aroma intenso.", 25, "https://i.pinimg.com/736x/6a/d5/94/6ad594d9f00354a6db3b5a7470cb2a61.jpg");
            flor("Lavanda", "Reconocida por su aroma relajante y su color violeta.", 35, "https://i.pinimg.com/736x/76/9c/7b/769c7bee6e14b631def60f8bbde14c71.jpg");
            flor("Claveles", "Simple pero bonito.", 10, "https://i.pinimg.com/736x/da/a2/fc/daa2fc6e333aedb5b1c37ac37567b2f3.jpg");
            flor("Rosas eternas con luna", "Una bella luna entre flores.", 40, "https://i.pinimg.com/736x/c8/c5/d5/c8c5d5d2cf72bec112d2005ba4b8f592.jpg");
            flor("Peluche + flor", "Peluche con rosas y un globo decorativo.", 55, "https://i.pinimg.com/736x/97/de/1a/97de1a6c1f0d1c4ef8acc5739f395cf3.jpg");
            flor("Detalle de declaración", "Ideal para una pedida de compromiso.", 25, "https://i.pinimg.com/1200x/13/90/18/139018a8d608b14023cea761f621e4a0.jpg");
            flor("Regalo completo", "Peluche de oso, flores decorativas y dulces.", 65, "https://i.pinimg.com/736x/98/bf/ca/98bfcaa134d7003c49134f9c062b0aa4.jpg");
            flor("Globo decorado", "Globo para regalar a alguien en su cumpleaños.", 15, "https://i.pinimg.com/736x/3b/4a/37/3b4a373cbed53d3fdfe43b9f777735b9.jpg");
            System.out.println("--> Catálogo de ejemplo cargado (15 productos)");
        }
    }

    private void flor(String nombre, String descripcion, double precio, String imagen) {
        Flor f = new Flor();
        f.setNombre(nombre);
        f.setDescripcion(descripcion);
        f.setPrecio(precio);
        f.setImagenUrl(imagen);
        f.setStock(10);
        florRepo.save(f);
    }

    private void crearSiNoExiste(String nombre) {
        if (rolRepo.findByNombre(nombre) == null) {
            rolRepo.save(new Rol(nombre));
            System.out.println("Creando rol: " + nombre);
        }
    }
}
