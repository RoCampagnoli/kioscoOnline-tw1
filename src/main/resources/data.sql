SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

ALTER DATABASE tallerwebi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE Producto CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE CategoriaProductos CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE Usuario CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE Hijo CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO Usuario
(id,dni,nombre,apellido,celular, email, password, rol, activo,fotoPerfil)
VALUES(null,1234567890, 'Pepe','Sujeto',1112341234,'test@unlam.edu.ar', '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', 'CLIENTE', true,'https://res.cloudinary.com/dqrka5zry/image/upload/v1780520000/istockphoto-1447126543-612x612_ek0kjw.jpg'),
    (null,1122334455,'Rocio','Campa',1122334455,'rociov.campagnoli@gmail.com','$2a$10$VBLw8Bn5sJnHlUMkkiKxiOjMVxxCUlSDy2SPrc0q7z1glRoHfNQYW','CLIENTE',true,null);

INSERT INTO Hijo (id,curso, fechaNac, fotoPerfil, nombre, idPadre,dni,apellido,aliasRetiro)
VALUES (null,'TERCERO_C','2020-06-18','https://res.cloudinary.com/dqrka5zry/image/upload/v1784000669/images_iwunyg.jpg','Santiago',1,12345,'Sujeto','ROJO.BARCO.PIZZA'),
         (null,'CUARTO_C','2022-07-28','https://res.cloudinary.com/dqrka5zry/image/upload/v1784000673/images_wj0ghd.jpg','Ariana',1,223345,'Sujeto','VERDE.MANZANA.PASTO'),
    (null,'PRIMERO_A','2020-06-18','https://res.cloudinary.com/dqrka5zry/image/upload/v1784000700/portrait-happy-child-girl-arms-260nw-2616158577_w0ykwc.webp','Martina',2,22334455,'Campagnoli','AZUL.GATO.COMETA');



INSERT INTO Usuario
(id, dni, nombre, apellido, celular, email, password, rol, activo, fotoPerfil)
VALUES(
          null,
          12345678,
          'Señor',
          'Kiosquero',
          1122334456,
          'kionettallerwebi@gmail.com',
          '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', -- HASH CORREGIDO (60 chars)
          'KIOSQUERO',
          true,
          'https://res.cloudinary.com/dqrka5zry/image/upload/v1783038194/KionetTWI/img_hijos/nwcrw3qbtfwhtylnrdbr.jpg'
      );

-- Insert de Categorías
INSERT INTO CategoriaProductos (id, nombreCategoria)
VALUES (1, 'Golosina');

INSERT INTO CategoriaProductos (id, nombreCategoria)
VALUES (2, 'Bebidas');

INSERT INTO CategoriaProductos (id, nombreCategoria)
VALUES (3, 'Librería');

INSERT INTO CategoriaProductos (id, nombreCategoria)
VALUES (4, 'Buffet');

INSERT INTO CategoriaProductos (id, nombreCategoria)
VALUES (5, 'Varios');

-- Insert de productos
INSERT INTO Producto (id, nombre, descripcion, precio,categoria_id,imagen,cantidad)
VALUES (null, 'Rollo Mogul', 'Gomitas de frutas surtidas', 600.00,1,
        'https://res.cloudinary.com/dqrka5zry/image/upload/v1780514334/GOMITAS-MOGUL-ROLLO-FRUTALES-X35GR-1-145_cpjdhr.jpg',5);

INSERT INTO Producto (id, nombre, descripcion, precio,categoria_id,imagen,cantidad)
VALUES (null, 'Alfajor Jorgito Negro', 'Alfajor de chocolate relleno con dulce de leche', 1200.00,1,
        'https://res.cloudinary.com/dqrka5zry/image/upload/v1780514266/alf-jorgito-negro_bf2cig.jpg',5);

INSERT INTO Producto (id, nombre, descripcion, precio,categoria_id,imagen,cantidad)
VALUES (null, 'Chupetín Pico Dulce', 'Chupetín duro sabor frutal', 500.00,1,
        'https://res.cloudinary.com/dqrka5zry/image/upload/v1780514267/pico-dulce_r18ww8.jpg',5);



INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id,imagen,cantidad)
VALUES (null, 'Jugo Baggio 200ml Multifruta', 'Jugo de fruta listo para tomar de 200ml', 800.00, 2,
        'https://res.cloudinary.com/dqrka5zry/image/upload/v1780514267/jugo-baggio-multi_zk6pjh.jpg',5);


INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id,imagen,cantidad)
VALUES (null, 'Cuaderno Éxito N°1', 'Cuaderno tapa dura de 48 hojas rayadas', 8000.00, 3,
        'https://res.cloudinary.com/dqrka5zry/image/upload/v1780514267/cuad-exito-n1_sra7m3.jpg',5);

-- ==========================================
-- CATEGORÍA 4: BUFFET (Sándwiches, pebetes, etc.)
-- ==========================================
INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id, imagen, cantidad)
VALUES
    (null, 'Pebete de Jamón y Queso', 'Sándwich de jamón y queso en pan pebete',
     2500.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783997738/pebete-jq.webp', 10),
    (null, 'Sándwich de Miga de Jamón y Queso', 'Triple de miga clásico (2 unidades)',
     1800.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783997892/1630525398155_ncavxd.webp', 10),
    (null, 'Medialunas de Jamón y Queso', 'Medialunas de Jamón y Queso (3 unidades)',
     4500.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783998110/7b66bf92a53fdbc7350300eba85ea3cc_cjysq0.jpg', 10),
    (null, 'Empanada de Carne', 'Empanada de carne al horno', 1200.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783998222/Empanada-arg-beef-1000-877x992_nj3x6a.png', 10),
    (null, 'Empanada de Jamón y Queso', 'Empanada de jamón y queso al horno', 1200.00, 4,
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1783998324/recetas_kvuk6g.webp', 10),
    (null, 'Super Pancho', 'Salchicha de primera marca con aderezos a elección',
     1500.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783998437/perro-caliente-clasico-ketchup-salsa-mostaza-aislado-sobre-fondo-blanco_123827-29747_vzjhaf.avif', 10),
    (null, 'Pizzeta de Muzzarella', 'Pizzeta individual con salsa de tomate y muzzarella',
     2000.00, 4, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783998563/images_sthuj8.jpg', 10);

-- ==========================================
-- CATEGORÍA 5: VARIOS (Cosas escolares que no entran en librería ni buffet)
-- ==========================================
INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id, imagen, cantidad)
VALUES
    (null, 'Pañuelos descartables Elite', 'Paquete de pañuelos descartables pocket x 10 unidades',
     400.00, 5, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999031/001467_1-29fe35bc56c312189016753663322386-640-0_yrzesj.webp', 10),
    (null, 'Alcohol en gel pocket', 'Sanitizante de manos portátil para la mochila', 1200.00,
     5, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999100/algabo-ultra-x30-ml-alcohol-en-gel-holder-7791274003608-531a9d08c4006157b817392016268235-640-0_ixvgtg.webp', 10),
    (null, 'Bandas elásticas x10u', 'Banda elástica de repuesto para carpetas x10u',
     300.00, 5, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999171/images_rckzag.jpg', 10);
-- ==========================================
-- CATEGORÍA 2: BEBIDAS (Gaseosas y jugos adicionales)
-- ==========================================
INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id, imagen, cantidad)
VALUES
    (null, 'Coca Cola 500ml', 'Gaseosa sabor original 500ml', 1500.00, 2,
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999323/images_q0suhx.jpg', 10),
    (null, 'Coca Cola Zero 500ml', 'Gaseosa sin azúcares sabor original 500ml', 1500.00,
     2, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999340/thumb_72207_default_medium_c79vcg.jpg', 0),
    (null, '7Up 500ml', 'Gaseosa lima-limón 500ml', 1400.00, 2,
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999392/123767_gaseosa-7-up-botella-500-ml-_1_dpkizx.png', 10),
    (null, 'Pepsi 500ml', 'Gaseosa sabor original 500ml', 1400.00, 2,
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999446/pepsi-500-01_eepn2t.jpg', 10),
    (null, 'Jugo Baggio 200ml Manzana', 'Jugo de manzana listo para tomar de 200ml',
     800.00, 2, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999569/D_NQ_NP_618249-MLU76633380329_052024-O_nbdete.webp', 10),
    (null, 'Jugo Baggio 200ml Naranja', 'Jugo de naranja listo para tomar de 200ml',
     800.00, 2, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999534/D_NQ_NP_830353-MLU77820614326_072024-O_hbzbqh.webp', 10),
    (null, 'Agua Mineral Villavicencio 500ml', 'Agua mineral sin gas 500ml',
     1000.00, 2, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999610/Agua-Sin-Gas-Villavicencio-500-Ml-_1_pikczh.webp', 10);

-- ==========================================
-- CATEGORÍA 3: LIBRERÍA (Útiles escolares)
-- ==========================================
INSERT INTO Producto (id, nombre, descripcion, precio, categoria_id, imagen, cantidad)
VALUES
    (null, 'Lapicera Borrable Azul Simball', 'Bolígrafo de tinta borrable color azul', 2500.00,
     3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999673/102-0971-c2aabff87044e94bd315865664973515-1024-1024_eehu9a.webp', 10),
    (null, 'Tijera Escolar Maped', 'Tijera escolar de punta redonda 13cm', 1800.00,
     3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999776/01280152_1_lrjzwc.webp', 10),
    (null, 'Voligoma chica 30g', 'Adhesivo sintético líquido transparente', 1100.00,
     3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999923/product_picture_f7c1a98034234741873eb1b869ca686d_637887072592501471_0_m_dbb2bf.jpg', 10),
    (null, 'Lápiz Negro Faber-Castell HB', 'Lápiz de grafito clásico para escribir y dibujar',
     500.00, 3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999971/fotos-para-tienda-nube-2431-9a6738e6c169dd3dba16510737766193-1024-1024_jx6ye2.webp', 10),
    (null, 'Goma de borrar Dos Banderas', 'Goma de borrar para lápiz y tinta', 400.00,
     3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000011/goma-2-banderas-roja-azul-x1-art-08361-bam-6d28dfd867fc0a445d17182196369306-1024-1024_cm7uf0.webp', 10),
    (null, 'Sacapuntas metálico Maped', 'Sacapuntas metálico de un solo orificio, alta duración',
     800.00, 3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000062/images_rgsifp.jpg', 10),
    (null, 'Regla plástica 20cm Maped', 'Regla de acrílico transparente y resistente', 700.00,
     3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000126/1663014355_Mjg5Mw_iujyko.webp', 10),
    (null, 'Resaltador Filgo Celeste', 'Resaltador chato punta biselada color celeste pastel',
     900.00, 3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000159/images_vpiyxw.jpg', 10),
    (null, 'Folios plásticos N°3', 'Pack de 10 folios plásticos transparentes',
    800.00, 3, 'https://res.cloudinary.com/dqrka5zry/image/upload/v1783999210/D_NQ_NP_694290-MLA102684397697_122025-O_vuof8a.webp', 10);

UPDATE Producto SET cantidad = cantidad + 50;

INSERT INTO Pedido(id,estado,fecha,subtotal,hijo_id,usuario_id,fecha_retiro)
VALUES (1,'PAGO_PENDIENTE','2026-06-30 02:05:04.896000',600,1,1,'2026-07-16'),
    (2,'PAGADO','2026-06-30 02:05:04.957000',1200,2,1,'2026-07-17'),
     (3,'PAGO_PENDIENTE','2026-06-30 02:08:47.750000',600,3,2,'2026-07-17');

INSERT INTO ItemPedido(id,cantidad,precioUnitario,pedido_id,producto_id)
VALUES (1,1,600,1,1),
    (2,1,1200,2,2),
    (3,1,600,3,1);


INSERT INTO Usuario (id, dni, nombre, apellido, celular, email, password, rol, activo, fotoPerfil)
VALUES
    (3, 20333444, 'Carlos', 'Gómez', 1133334444, 'test1@unlam.edu.ar', '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', 'CLIENTE', true, null),
    (4, 20444555, 'Laura', 'Rodríguez', 1144445555, 'test2@unlam.edu.ar', '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', 'CLIENTE', true, null),
    (5, 20555666, 'Mariano', 'López', 1155556666, 'test3@unlam.edu.ar', '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', 'CLIENTE', true, null),
    (6, 20666777, 'Patricia', 'Fernández', 1166667777, 'test4@unlam.edu.ar', '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy', 'CLIENTE', true, null);

INSERT INTO Hijo (id, curso, fechaNac, fotoPerfil, nombre, idPadre, dni, apellido, aliasRetiro)
VALUES
    (4, 'SEGUNDO_A', '2019-03-12', null, 'Mateo', 3, 50111222, 'Gómez', 'SOL.LUNA.ESTRELLA'),
    (5, 'TERCERO_B', '2018-08-24', null, 'Juana', 4, 50222333, 'Rodríguez', 'NUBE.TRUENO.VIENTO'),
    (6, 'CUARTO_A', '2017-05-05', null, 'Lucas', 5, 50333444, 'López', 'FUEGO.TIERRA.AGUA'),
    (7, 'QUINTO_B', '2016-11-30', null, 'Sofía', 6, 50444555, 'Fernández', 'LAPIZ.GOMA.REGLA');

-- ==========================================
-- PEDIDOS PARA "HOY" (16 de Julio de 2026)
-- ==========================================

-- Pedido para hoy: PAGADO (Usuario 3, Hijo 4) - Subtotal: 3300.00
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (4, 'PAGADO', '2026-07-16 08:30:00', 3300.00, 4, 3, '2026-07-16');

INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES
    (4, 1, 1500.00, 4, 11),  -- Coca Cola 500ml (ID: 11)
    (5, 1, 1800.00, 4, 7);   -- Sándwich de Miga (ID: 7)


-- Pedido para hoy: PAGO_PENDIENTE (Usuario 4, Hijo 5) - Subtotal: 2500.00
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (5, 'PAGO_PENDIENTE', '2026-07-16 09:15:00', 2500.00, 5, 4, '2026-07-16');

INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES
    (6, 1, 2500.00, 5, 6);   -- Pebete de Jamón y Queso (ID: 6)


-- Pedido para hoy: PEDIDO_ARMADO (Usuario 5, Hijo 6) - Subtotal: 2000.00
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (6, 'PEDIDO_ARMADO', '2026-07-16 10:00:00', 2000.00, 6, 5, '2026-07-16');

INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES
    (7, 1, 2000.00, 6, 12);  -- Pizzeta de Muzzarella (ID: 12)


-- ==========================================
-- HISTORIAL DE PEDIDOS (Otros días hábiles de Julio 2026)
-- ==========================================

-- Miércoles 1 de Julio: PAGADO (Usuario 3)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (7, 'PAGADO', '2026-07-01 12:30:00', 1400.00, 4, 3, '2026-07-01');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (8, 1, 1400.00, 7, 14); -- Pepsi 500ml

-- Viernes 3 de Julio: PAGADO (Usuario 4)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (8, 'PAGADO', '2026-07-03 10:10:00', 1200.00, 5, 4, '2026-07-03');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (9, 1, 1200.00, 8, 2); -- Alfajor Jorgito

-- Lunes 6 de Julio: PAGADO (Usuario 5)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (9, 'PAGADO', '2026-07-06 14:20:00', 1500.00, 6, 5, '2026-07-06');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (10, 1, 1500.00, 9, 11); -- Coca Cola

-- Martes 7 de Julio: PAGADO (Usuario 6)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (10, 'PAGADO', '2026-07-07 09:05:00', 2500.00, 7, 6, '2026-07-07');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (11, 1, 2500.00, 10, 18); -- Lapicera Borrable Simball

-- Miércoles 8 de Julio: PAGADO (Usuario 3)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (11, 'PAGADO', '2026-07-08 11:40:00', 1800.00, 4, 3, '2026-07-08');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (12, 1, 1800.00, 11, 19); -- Tijera Escolar Maped

-- [Se salta el Jueves 9 de Julio por ser Feriado de la Independencia]

-- Viernes 10 de Julio: PAGADO (Usuario 4)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (12, 'PAGADO', '2026-07-10 08:50:00', 800.00, 5, 4, '2026-07-10');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (13, 1, 800.00, 12, 4); -- Jugo Baggio

-- Lunes 13 de Julio: PAGADO (Usuario 5)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (13, 'PAGADO', '2026-07-13 15:00:00', 1200.00, 6, 5, '2026-07-13');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (14, 1, 1200.00, 13, 10); -- Empanada de J&Q

-- Martes 14 de Julio: PAGADO (Usuario 6)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (14, 'PAGADO', '2026-07-14 10:30:00', 1200.00, 7, 6, '2026-07-14');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (15, 1, 1200.00, 14, 14); -- Alcohol en Gel pocket

-- Miércoles 15 de Julio: PAGADO (Usuario 3)
INSERT INTO Pedido(id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES (15, 'PAGADO', '2026-07-15 11:15:00', 2000.00, 4, 3, '2026-07-15');
INSERT INTO ItemPedido(id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES (16, 1, 2000.00, 15, 12); -- Pizzeta