-- =====================================================
-- VetApp - Estructura de la base de datos
-- =====================================================

CREATE DATABASE IF NOT EXISTS veterinaria_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE veterinaria_db;

-- Se borran en orden inverso por las claves foráneas
DROP TABLE IF EXISTS consulta_tratamiento;
DROP TABLE IF EXISTS consultas;
DROP TABLE IF EXISTS animales;
DROP TABLE IF EXISTS tratamientos;
DROP TABLE IF EXISTS clientes;

-- -----------------------------------------------------
-- Clientes (dueños de los animales)
-- -----------------------------------------------------
CREATE TABLE clientes (
  id       INT          NOT NULL AUTO_INCREMENT,
  nombre   VARCHAR(100) NOT NULL,
  telefono VARCHAR(20)  DEFAULT NULL,
  email    VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY email (email)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tratamientos
-- -----------------------------------------------------
CREATE TABLE tratamientos (
  id            INT          NOT NULL AUTO_INCREMENT,
  nombre        VARCHAR(100) NOT NULL,
  descripcion   VARCHAR(255) DEFAULT NULL,
  duracion_dias INT          DEFAULT NULL,
  precio        DECIMAL(6,2) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Animales (cada uno pertenece a un cliente)
-- -----------------------------------------------------
CREATE TABLE animales (
  id_animal  INT          NOT NULL AUTO_INCREMENT,
  nombre     VARCHAR(50)  NOT NULL,
  especie    VARCHAR(50)  NOT NULL,
  raza       VARCHAR(50)  DEFAULT NULL,
  edad       INT          DEFAULT NULL,
  peso       DECIMAL(5,2) DEFAULT NULL,
  id_cliente INT          NOT NULL,
  imagen     LONGBLOB,
  PRIMARY KEY (id_animal),
  KEY id_cliente (id_cliente),
  CONSTRAINT animales_ibfk_1 FOREIGN KEY (id_cliente)
    REFERENCES clientes (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Consultas (cada una es de un animal)
-- -----------------------------------------------------
CREATE TABLE consultas (
  id        INT          NOT NULL AUTO_INCREMENT,
  fecha     DATE         NOT NULL,
  motivo    VARCHAR(255) NOT NULL,
  precio    DECIMAL(6,2) NOT NULL,
  id_animal INT          NOT NULL,
  PRIMARY KEY (id),
  KEY id_animal (id_animal),
  CONSTRAINT consultas_ibfk_1 FOREIGN KEY (id_animal)
    REFERENCES animales (id_animal) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Relación N:M entre consultas y tratamientos
-- -----------------------------------------------------
CREATE TABLE consulta_tratamiento (
  id             INT NOT NULL AUTO_INCREMENT,
  id_consulta    INT NOT NULL,
  id_tratamiento INT NOT NULL,
  PRIMARY KEY (id),
  KEY id_consulta (id_consulta),
  KEY id_tratamiento (id_tratamiento),
  CONSTRAINT consulta_tratamiento_ibfk_1 FOREIGN KEY (id_consulta)
    REFERENCES consultas (id) ON DELETE CASCADE,
  CONSTRAINT consulta_tratamiento_ibfk_2 FOREIGN KEY (id_tratamiento)
    REFERENCES tratamientos (id) ON DELETE CASCADE
) ENGINE=InnoDB;
