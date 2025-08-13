CREATE TABLE topicos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    autor VARCHAR(255) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_titulo_mensaje (titulo, mensaje(255))
);