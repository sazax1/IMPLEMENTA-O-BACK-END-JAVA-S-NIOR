-- V1__create_tables.sql
-- Criação das tabelas do sistema

-- Tabela de usuários
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de artistas
CREATE TABLE artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabela de álbuns
CREATE TABLE albums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    release_year INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabela de relacionamento N:N entre artistas e álbuns
CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_artist_album_artist FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE,
    CONSTRAINT fk_artist_album_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

-- Tabela de capas de álbuns
CREATE TABLE album_covers (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    file_key VARCHAR(500) NOT NULL,
    original_name VARCHAR(255),
    content_type VARCHAR(100),
    file_size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_album_covers_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

-- Tabela de regionais
CREATE TABLE regionais (
    id BIGSERIAL PRIMARY KEY,
    external_id INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Índices para otimização
CREATE INDEX idx_artists_name ON artists(name);
CREATE INDEX idx_artists_type ON artists(type);
CREATE INDEX idx_albums_title ON albums(title);
CREATE INDEX idx_artist_album_artist_id ON artist_album(artist_id);
CREATE INDEX idx_artist_album_album_id ON artist_album(album_id);
CREATE INDEX idx_album_covers_album_id ON album_covers(album_id);
CREATE INDEX idx_regionais_external_id ON regionais(external_id);
CREATE INDEX idx_regionais_ativo ON regionais(ativo);
