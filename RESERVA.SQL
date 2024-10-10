CREATE DATABASE ReservaDB
GO

USE ReservaDB
GO

-- Tabla Departamento
CREATE TABLE departamento (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(50) NOT NULL,
    usuario_creacion VARCHAR(20),
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1')
);
GO

-- Tabla Provincia
CREATE TABLE provincia (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(50) NOT NULL,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
    departamento_id INT NOT NULL,
    FOREIGN KEY (departamento_id) REFERENCES departamento(id)
);
GO

-- Tabla Distrito
CREATE TABLE distrito (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(150) NOT NULL,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
    provincia_id INT NOT NULL,
    FOREIGN KEY (provincia_id) REFERENCES provincia(id)
);
GO


-- Tabla Persona
CREATE TABLE persona (
    id INT PRIMARY KEY IDENTITY(1,1),
    dni CHAR(8) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    ape_paterno VARCHAR(50) NOT NULL,
    ape_materno VARCHAR(50) NOT NULL,
    celular CHAR(9) NULL,
    correo VARCHAR(250) NULL,
    fecha_nac DATE,
    genero CHAR(1),
    direccion VARCHAR(150),
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
    distrito_id INT NOT NULL,
    FOREIGN KEY (distrito_id) REFERENCES distrito(id)
);
GO

-- Tabla Empresa
CREATE TABLE empresa (
    id INT PRIMARY KEY IDENTITY(1,1),
    ruc CHAR(11) UNIQUE NOT NULL,
    razon_social VARCHAR(100) NOT NULL,
    telefono CHAR(11) NOT NULL,
    direccion VARCHAR(150),
    usuario_creacion VARCHAR(20)NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
    distrito_id INT NOT NULL,
    FOREIGN KEY (distrito_id) REFERENCES distrito(id)
);
GO

-- Tabla Cliente
CREATE TABLE cliente (
    id INT PRIMARY KEY IDENTITY(1,1),
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
    persona_id INT,
    empresa_id INT,
    FOREIGN KEY (persona_id) REFERENCES persona(id),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);
GO

-- Tabla cargo
CREATE TABLE cargo(
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre_cargo VARCHAR(50),
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
)
GO

    -- Tabla empleado
    CREATE TABLE empleado (
        id INT PRIMARY KEY IDENTITY(1,1),
        fecha_ingreso DATE NOT NULL,
        fecha_salida DATE NOT NULL,
        sueldo DECIMAL(6,2),
        usuario_creacion VARCHAR(20) NULL,
        fecha_creacion DATETIME DEFAULT GETDATE(),
        usuario_modificacion VARCHAR(20),
        fecha_modificacion DATETIME DEFAULT GETDATE(),
        estado CHAR(1) DEFAULT('1'),
        cargo_id INT,
        persona_id INT,
        empresa_id INT,
        FOREIGN KEY (persona_id) REFERENCES persona(id),
        FOREIGN KEY (empresa_id) REFERENCES empresa(id),
        FOREIGN KEY (cargo_id) REFERENCES cargo(id)
    );
    GO

CREATE TABLE imagen (
    id int IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(max) NOT NULL,
    image_id VARCHAR(max) NOT NULL,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion datetime2(6) DEFAULT GETDATE(), 
    usuario_modificacion VARCHAR(20) NULL,
    fecha_modificacion datetime2(6) DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT '1' 
)
go
-- Tabla tipo_usuario
CREATE TABLE rol(
    id INT PRIMARY KEY IDENTITY(1,1),
    nomtipo VARCHAR(50),
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
)
go

CREATE TABLE compania (
    id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
	concepto VARCHAR(80)NULL,	
	correo	VARCHAR(30)	NULL,
	pagWeb	VARCHAR(50)NULL	,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE() NULL,
    pc_creacion VARCHAR(20) NULL,
    usuario_modificacion VARCHAR(20) NULL,
    fecha_modificacion DATETIME NOT NULL,
    pc_modificacion VARCHAR(20) NOT NULL,
    estado CHAR(1),
	imagen_id INT NOT NULL
	idpersona
    FOREIGN KEY (imagen_id) REFERENCES imagen(id)
);
GO
-- Tabla para Sucursal
CREATE TABLE sucursal (
    id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE() NULL,
    pc_creacion VARCHAR(20) NULL,
    usuario_modificacion VARCHAR(20) NULL,
    fecha_modificacion DATETIME NOT NULL,
    pc_modificacion VARCHAR(20) NOT NULL,
    estado CHAR(1),
    compania_id INT NOT NULL,
    FOREIGN KEY (compania_id) REFERENCES compania(id)
);
GO

-- Tabla para Sede
CREATE TABLE sede (
    id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    sucursal_id INT NOT NULL,
    usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE() NULL,
    usuario_modificacion VARCHAR(20) NULL,
    fecha_modificacion DATETIME NOT NULL,
    estado CHAR(1),
    FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);
GO


-- Tabla usuario
CREATE TABLE usuario (
    id INT PRIMARY KEY IDENTITY(1,1),
    logeo VARCHAR(50) NOT NULL,                
    clave VARCHAR(50) NULL,                    
    email VARCHAR(100) UNIQUE NOT NULL,       
    google_id VARCHAR(100) UNIQUE,            
    usuario_creacion VARCHAR(20) NULL,        
    fecha_creacion DATETIME DEFAULT GETDATE(),  
    usuario_modificacion VARCHAR(20),           
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),               
    empleado_id INT NULL,                       
    rol_id INT NOT NULL,                
    sede_id INT NULL,                           
    cliente_id INT NULL,                       
    FOREIGN KEY (empleado_id) REFERENCES empleado(id),
    FOREIGN KEY (rol_id) REFERENCES rol(id),
    FOREIGN KEY (sede_id) REFERENCES sede(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) 
);





-- Tabla campo
CREATE TABLE campo(
	id INT PRIMARY KEY IDENTITY(1,1),
	nombre CHAR(12) NOT NULL,
	precio DECIMAL (10,2) NOT NULL,
    descripcion VARCHAR(100),
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	imagen_id INT NOT NULL
    FOREIGN KEY (imagen_id) REFERENCES imagen(id)
)
GO

-- Tabla metodo_pago
CREATE TABLE metodo_pago(
	id INT PRIMARY KEY IDENTITY(1,1),
	nombre VARCHAR(20),
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1')
)
GO

CREATE TABLE horario(
	id INT PRIMARY KEY IDENTITY(1,1),
	hora_inicio TIME NOT NULL,
	hora_final TIME NOT NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1')
)
GO

-- Tabla reserva
CREATE TABLE reserva(
	id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	fecha DATETIME NOT NULL,
	descuento DECIMAL(10,2) NOT NULL,
	igv DECIMAL(10,2) NOT NULL,
	total DECIMAL(10,2) NOT NULL,
	total_descuento DECIMAL(10,2) NOT NULL,
	subtotal DECIMAL(10,2) NOT NULL,
	tipo_comprobante CHAR(1) NOT NULL,
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	cliente_id INT NOT NULL,
	usuario_id INT NOT NULL,
	metodo_pago_id INT NOT NULL,
    horario_id INT NOT NULL,
	FOREIGN KEY(cliente_id) REFERENCES cliente(id),
	FOREIGN KEY(usuario_id) REFERENCES usuario(id),
	FOREIGN KEY(metodo_pago_id) REFERENCES metodo_pago(id),
	FOREIGN KEY(horario_id) REFERENCES horario(id)
)
GO

CREATE TABLE detalle_venta(
	id INT PRIMARY KEY,
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	venta_id INT NOT NULL,
	campo_id INT NOT NULL,
	usuario_id INT NOT NULL
	FOREIGN KEY(venta_id) REFERENCES reserva(id),
	FOREIGN KEY(campo_id) REFERENCES campo(id),
	FOREIGN KEY(usuario_id) REFERENCES usuario(id)
)
GO

CREATE TABLE factura(
	id INT PRIMARY KEY IDENTITY,
	serie CHAR(5) NOT NULL,
	numero CHAR (10) NOT NULL,
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	reserva_id INT NOT NULL,
	FOREIGN KEY(reserva_id) REFERENCES reserva(id)
)
GO

CREATE TABLE boleta(
	id INT PRIMARY KEY IDENTITY,
	serie CHAR(5) NOT NULL,
	numero CHAR (10) NOT NULL,
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	reserva_id INT NOT NULL,
	FOREIGN KEY(reserva_id) REFERENCES reserva(id)
)
GO

CREATE TABLE ticket(
	id INT PRIMARY KEY IDENTITY,
	serie CHAR(5) NOT NULL,
	numero CHAR (10) NOT NULL,
	usuario_creacion VARCHAR(20) NULL,
    fecha_creacion DATETIME DEFAULT GETDATE(),
    usuario_modificacion VARCHAR(20),
    fecha_modificacion DATETIME DEFAULT GETDATE(), 
    estado CHAR(1) DEFAULT('1'),
	reserva_id INT NOT NULL,
	FOREIGN KEY(reserva_id) REFERENCES reserva(id)
)
GO

