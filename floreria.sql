-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 04-05-2026 a las 21:05:20
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `floreria`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `flores`
--

CREATE TABLE `flores` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `flores`
--

INSERT INTO `flores` (`id`, `descripcion`, `imagen_url`, `nombre`, `precio`, `stock`) VALUES
(1, 'ramitos de rosas para regalar a tu pareja <3', 'https://i.pinimg.com/1200x/4b/0a/d0/4b0ad00e307edbed46cfe121cfcfcd6e.jpg', 'Ramo de Rosas', 15, 0),
(2, 'Al igual que el girasol, mi corazón siempre gira hacia ti, sin importar dónde esté', 'https://i.pinimg.com/736x/5a/03/51/5a0351d6308356d22ff42d9dd8d1849d.jpg', 'Girasoles', 22, 4),
(3, 'Flores pequeñas para momentos bonitos\r\n', 'https://i.pinimg.com/736x/65/ff/b1/65ffb12bfdf5c8e92ca55256b58802e7.jpg', 'bouquet', 8, 1),
(4, 'Especial para cumpleaños', 'https://i.pinimg.com/736x/6e/6e/c2/6e6ec260acb53ed3f5e1aa66f590d952.jpg', 'Flores HB', 30, 5),
(5, 'Hermosas Orquidias Para Sorprender', 'https://i.pinimg.com/736x/25/ec/48/25ec487e5a740a85522e29e17ebe670a.jpg', 'Orquideas', 15, 5),
(6, 'Bellas y accesibles ', 'https://i.pinimg.com/736x/26/50/70/2650708295f9ffc442cfa34d8d42fdf8.jpg', 'Tulipanes', 25, 4),
(7, 'Flores con muchos petalos', 'https://i.pinimg.com/1200x/bf/4a/19/bf4a19bfe632f0732278707b81e75212.jpg', 'dahlia, lavender', 15, 3),
(8, 'Conocida por su forma de trompeta y aroma intenso', 'https://i.pinimg.com/736x/6a/d5/94/6ad594d9f00354a6db3b5a7470cb2a61.jpg', 'Lirios', 25, 5),
(9, 'Reconocida por su aroma relajante y color violeta', 'https://i.pinimg.com/736x/76/9c/7b/769c7bee6e14b631def60f8bbde14c71.jpg', 'Lavanda', 35, 1),
(10, 'Simple pero bonito', 'https://i.pinimg.com/736x/da/a2/fc/daa2fc6e333aedb5b1c37ac37567b2f3.jpg', 'claveles <3', 10, 3),
(11, 'Una Bella luna entre flores', 'https://i.pinimg.com/736x/c8/c5/d5/c8c5d5d2cf72bec112d2005ba4b8f592.jpg', 'Rosas Eternas con Luna', 40, 3),
(12, 'Peluche + rosas y un globo decorativo', 'https://i.pinimg.com/736x/97/de/1a/97de1a6c1f0d1c4ef8acc5739f395cf3.jpg', 'Peluche + Flor', 55, 2),
(13, 'Pedida de compromiso', 'https://i.pinimg.com/1200x/13/90/18/139018a8d608b14023cea761f621e4a0.jpg', 'Detalle de declaracion', 25, 1),
(14, 'Peluche de oso + flores decorativos y dulces :)', 'https://i.pinimg.com/736x/98/bf/ca/98bfcaa134d7003c49134f9c062b0aa4.jpg', 'Regalo Completo', 65, 2),
(15, 'globo para regalar a alguien en su cumpleaños', 'https://i.pinimg.com/736x/3b/4a/37/3b4a373cbed53d3fdfe43b9f777735b9.jpg', 'Globo decorado', 15, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` bigint(20) NOT NULL,
  `direccion_entrega` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `total` double DEFAULT NULL,
  `cliente_id` bigint(20) DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  `descripcion_productos` varchar(500) DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id`, `direccion_entrega`, `estado`, `total`, `cliente_id`, `fecha_creacion`, `descripcion_productos`, `metodo_pago`) VALUES
(16, 'estacion San', 'ENTREGADO', 25, NULL, '2026-05-04 13:29:09', '1x Tulipanes', 'yape'),
(17, 'estacion piramides del sol', 'PAGADO', 15, NULL, '2026-05-04 13:44:48', '1x Ramo de Rosas', 'yape'),
(18, 'estacion del tren santa rosa', 'PAGADO', 8, NULL, '2026-05-04 13:53:25', '1x bouquet', 'yape'),
(19, 'estacion de tren ayacucho', 'PAGADO', 22, NULL, '2026-05-04 14:03:05', '1x Girasoles', 'tarjeta');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos_productos`
--

CREATE TABLE `pedidos_productos` (
  `pedido_id` bigint(20) NOT NULL,
  `productos_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`) VALUES
(4, 'ROLE_ADMIN'),
(5, 'ROLE_CLIENTE'),
(6, 'ROLE_DELIVERY');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `nombre_completo` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `correo`, `direccion`, `nombre_completo`, `password`, `username`) VALUES
(5, NULL, NULL, 'Admin de IsabelS', '$2a$10$LMlLrblRh9Kv4tUtdhmWm.LHi3QmfFjPyedcmCS9519TG00dLU65S', 'admin'),
(6, NULL, NULL, 'Repartidor IsabelS', '$2a$10$apERGMwOzHT9IsK/adQApeSprs4oK.6ZwpbCrZQDUj1QC5qWHVtlK', 'Deli'),
(7, 'leonardoArango@gmail.com', 'estacion bayobar sjl', 'leonardo Arango', '$2a$10$fQwqGtNSlW/bW.j0PZsqBueZPrjIZclpAUSU3NjsD2FpaYp4iZaz2', 'arango'),
(8, 'GenenMeji@gmail.com', 'estacion bayobar', 'Genesis M', '$2a$10$PZJzOBPxdr7uDgWg.6n5o.ojnZVZ0OW8hgRgjmqFpj65lYfaokywa', 'genesis'),
(9, 'JesusLujanCarreon@Gmail.com', 'estacion los postes ', 'Jesus Lujan', '$2a$10$FZnu6ie1Oz3hobiTN1tICuAXl6fwIvXc5D1RITmW/5acPkjyTlGG6', 'sideral'),
(10, 'leonardoArango@gmail.com', 'proceres de la independencia', 'Isabel Ramos', '$2a$10$sVXYjdgORO9mBP0MEhmYz.xasKKlfBhhGTjl3q97zMShwmNbDzYLO', 'isabelita');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios_roles`
--

CREATE TABLE `usuarios_roles` (
  `usuario_id` bigint(20) NOT NULL,
  `rol_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios_roles`
--

INSERT INTO `usuarios_roles` (`usuario_id`, `rol_id`) VALUES
(9, 5),
(5, 4),
(6, 6),
(7, 5),
(8, 5),
(9, 5),
(10, 5);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `flores`
--
ALTER TABLE `flores`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK6dtctern9votxnaydg7g2uifb` (`cliente_id`);

--
-- Indices de la tabla `pedidos_productos`
--
ALTER TABLE `pedidos_productos`
  ADD KEY `FKstfo4339khrpwchepk47ewbvb` (`productos_id`),
  ADD KEY `FKb75m80xdtjupgast9vikicv9v` (`pedido_id`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_m2dvbwfge291euvmk6vkkocao` (`username`);

--
-- Indices de la tabla `usuarios_roles`
--
ALTER TABLE `usuarios_roles`
  ADD KEY `FK5338ehgluufgc8bpj08nrq970` (`rol_id`),
  ADD KEY `FKqcxu02bqipxpr7cjyj9dmhwec` (`usuario_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `flores`
--
ALTER TABLE `flores`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `FK6dtctern9votxnaydg7g2uifb` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `pedidos_productos`
--
ALTER TABLE `pedidos_productos`
  ADD CONSTRAINT `FKb75m80xdtjupgast9vikicv9v` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`),
  ADD CONSTRAINT `FKstfo4339khrpwchepk47ewbvb` FOREIGN KEY (`productos_id`) REFERENCES `flores` (`id`);

--
-- Filtros para la tabla `usuarios_roles`
--
ALTER TABLE `usuarios_roles`
  ADD CONSTRAINT `FK5338ehgluufgc8bpj08nrq970` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`),
  ADD CONSTRAINT `FKqcxu02bqipxpr7cjyj9dmhwec` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
