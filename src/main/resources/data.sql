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


INSERT INTO Pedido(id,estado,fecha,subtotal,hijo_id,usuario_id,fecha_retiro)
VALUES (1,'PAGO_PENDIENTE','2026-06-30 02:05:04.896000',600,1,1,'2026-07-16'),
    (2,'PAGADO','2026-06-30 02:05:04.957000',1200,2,1,'2026-07-17'),
     (3,'PAGO_PENDIENTE','2026-06-30 02:08:47.750000',600,3,2,'2026-07-17');

INSERT INTO ItemPedido(id,cantidad,precioUnitario,pedido_id,producto_id)
VALUES (1,1,600,1,1),
    (2,1,1200,2,2),
    (3,1,600,3,1);

-- ==========================================
-- USUARIO NUEVO + 2 HIJOS
-- ==========================================
INSERT INTO Usuario
(id, dni, nombre, apellido, celular, email, password, rol, activo, fotoPerfil)
VALUES(null, 30456789, 'Marcos', 'Fernández', 1155667788,
       'marcos.fernandez@gmail.com',
       '$2a$10$2ll9SsWXF8a0HWPdz3iKKuBQ9S37sQxnfCFlghyUp7jKJFzlKhazy',
       'CLIENTE', true,
       'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000700/portrait-happy-child-girl-arms-260nw-2616158577_w0ykwc.webp');

INSERT INTO Hijo (id, curso, fechaNac, fotoPerfil, nombre, idPadre, dni, apellido, aliasRetiro)
VALUES
    (null, 'SEGUNDO_B', '2021-03-10',
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000669/images_iwunyg.jpg',
     'Tomás', 4, 33445566, 'Fernández', 'AMARILLO.SOL.NUBE'),
    (null, 'QUINTO_A', '2018-11-22',
     'https://res.cloudinary.com/dqrka5zry/image/upload/v1784000673/images_wj0ghd.jpg',
     'Valentina', 4, 33445567, 'Fernández', 'NARANJA.LUNA.RIO');

-- ==========================================
-- PEDIDOS CON RETIRO HOY (2026-07-16)
-- Creados ayer o antesdeayer, 1 por usuario cliente
-- ==========================================
INSERT INTO Pedido (id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES
    (4, 'PAGADO', '2026-07-15 10:15:00.000000', 1200, 1, 1, '2026-07-16'),          -- Pepe / Santiago
    (5, 'PAGO_PENDIENTE', '2026-07-14 16:40:00.000000', 2500, 3, 2, '2026-07-16'),  -- Rocío / Martina
    (6, 'PAGADO', '2026-07-15 09:00:00.000000', 1500, 4, 4, '2026-07-16');          -- Marcos / Tomás

INSERT INTO ItemPedido (id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES
    (4, 1, 1200, 4, 2),   -- Alfajor Jorgito Negro
    (5, 1, 2500, 5, 6),   -- Pebete de Jamón y Queso
    (6, 1, 1500, 6, 11);  -- Super Pancho

-- ==========================================
-- PEDIDOS VIEJOS DE PEPE SUJETO (ENTREGADO)
-- ==========================================
INSERT INTO Pedido (id, estado, fecha, subtotal, hijo_id, usuario_id, fecha_retiro)
VALUES
    (7, 'ENTREGADO', '2026-06-18 11:00:00.000000', 800,  1, 1, '2026-06-19'),
    (8, 'ENTREGADO', '2026-06-03 14:30:00.000000', 1200, 2, 1, '2026-06-04'),
    (9, 'ENTREGADO', '2026-06-10 09:45:00.000000', 4500, 1, 1, '2026-06-11');

INSERT INTO ItemPedido (id, cantidad, precioUnitario, pedido_id, producto_id)
VALUES
    (7, 1, 800,  7, 4),   -- Jugo Baggio 200ml Multifruta
    (8, 1, 1200, 8, 2),   -- Alfajor Jorgito Negro
    (9, 1, 4500, 9, 8);   -- Medialunas de Jamón y Queso