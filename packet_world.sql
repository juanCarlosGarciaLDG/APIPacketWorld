CREATE DATABASE IF NOT EXISTS packet_world;
USE packet_world;

DROP TABLE IF EXISTS sucursales;
-- sucursales (mínimo)
CREATE TABLE sucursales (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  ciudad VARCHAR(100),
  estatus ENUM('activa','inactiva') NOT NULL DEFAULT 'activa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS colaboradores;

CREATE TABLE colaboradores (
  id_colaborador INT PRIMARY KEY AUTO_INCREMENT,
  no_personal VARCHAR(20) NOT NULL UNIQUE,
  curp VARCHAR(18) NOT NULL UNIQUE,
  nombre VARCHAR(50) NOT NULL,
  apellido_paterno VARCHAR(50) NOT NULL,
  apellido_materno VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  rol ENUM('Administrador','Ejecutivo de Tienda','Conductor') NOT NULL,
  id_sucursal INT NOT NULL,
  licencia VARCHAR(20) DEFAULT NULL,
  foto LONGBLOB NOT NULL,
  foto_base64 LONGTEXT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  
  FOREIGN KEY (id_sucursal) REFERENCES sucursales(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS clientes;
-- clientes (lo principal para tu API)
CREATE TABLE clientes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  apellido VARCHAR(80),
  correo VARCHAR(100),
  telefono VARCHAR(30),
  calle VARCHAR(120),
  num_ext VARCHAR(20),
  colonia VARCHAR(100),
  cp VARCHAR(10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS envios;
-- envios (relacionado con cliente y sucursal origen)
CREATE TABLE envios (
  id INT PRIMARY KEY AUTO_INCREMENT,
  id_cliente INT NOT NULL,
  num_guia VARCHAR(40) UNIQUE,
  direccion_destino VARCHAR(255) NOT NULL,
  ciudad_destino VARCHAR(100),
  estado_destino VARCHAR(100),
  id_sucursal_origen INT,
  costo DECIMAL(10,2) DEFAULT 0.00,
  estatus ENUM('recibido','procesado','en_transito','entregado','cancelado') DEFAULT 'recibido',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE RESTRICT,
  FOREIGN KEY (id_sucursal_origen) REFERENCES sucursales(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS paquetes;
-- paquetes (vinculados a envios)
CREATE TABLE paquetes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  id_envio INT NOT NULL,
  descripcion VARCHAR(255),
  peso DECIMAL(6,2),
  FOREIGN KEY (id_envio) REFERENCES envios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- datos de ejemplo (para pruebas inmediatas)
INSERT INTO sucursales (nombre, ciudad) VALUES
('Sucursal Centro', 'Ciudad A'),
('Sucursal Norte', 'Ciudad B');

INSERT INTO colaboradores (
    no_personal, curp, nombre, apellido_paterno, apellido_materno, 
    email, contrasena, rol, id_sucursal, licencia, 
    foto, foto_base64, activo
) VALUES (
    'ADM1', 
    'GORA800101HDFRRA01', 
    'Ana', 'Gómez', 'Ruiz', 
    'ana.admin@empresa.com', 
    'ADM1',
    'Administrador', 
    1,
    NULL,
    UNHEX('89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000A49444154789C63000100000500010D0A2D600000000049454E44AE426082'), 
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg==', 
    TRUE
);

INSERT INTO colaboradores (
    no_personal, curp, nombre, apellido_paterno, apellido_materno, 
    email, contrasena, rol, id_sucursal, licencia, 
    foto, foto_base64, activo
) VALUES (
    'EJE1', 
    'PEML900505HDFRRB02', 
    'Luis', 'Pérez', 'Méndez', 
    'luis.ventas@empresa.com', 
    'EJE1', 
    'Ejecutivo de Tienda', 
    1, 
    NULL,
    UNHEX('89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000A49444154789C63000100000500010D0A2D600000000049454E44AE426082'), 
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg==', 
    TRUE
);

INSERT INTO colaboradores (
    no_personal, curp, nombre, apellido_paterno, apellido_materno, 
    email, contrasena, rol, id_sucursal, licencia, 
    foto, foto_base64, activo
) VALUES (
    'CON1', 
    'SARM850808HDFRRC03', 
    'Mario', 'Sánchez', 'Ramírez', 
    'mario.ruta@empresa.com', 
    'CON1', 
    'Conductor', 
    1, 
    'LIC-TYPE-A-998877',
    UNHEX('89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000A49444154789C63000100000500010D0A2D600000000049454E44AE426082'), 
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg==', 
    TRUE
);

INSERT INTO clientes (nombre, apellido, correo, telefono, calle, num_ext, colonia, cp) VALUES
('Juan','Pérez','juan@dominio.com','555-1234','Calle Falsa','123','Centro','01000'),
('María','López','maria@dominio.com','555-5678','Av. Siempre Viva','456','Norte','02000');

INSERT INTO envios (id_cliente, num_guia, direccion_destino, ciudad_destino, estado_destino, id_sucursal_origen, costo)
VALUES
(1,'GW-0001','Calle Destino 1 #12','Ciudad X','Estado Y',1,120.00),
(2,'GW-0002','Av. Otra 45','Ciudad Z','Estado W',2,80.50);

INSERT INTO paquetes (id_envio, descripcion, peso) VALUES
(1,'Caja electrónica',2.5),
(1,'Manual',0.3),
(2,'Ropa',1.2);