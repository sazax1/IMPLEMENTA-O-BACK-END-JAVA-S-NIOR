-- V2__seed_data.sql
-- Carga inicial de dados

-- Usuário admin (senha: admin123 - BCrypt hash)
INSERT INTO app_users (username, password, role, enabled) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/3OT.eOomQFAV6x7b.yHYS', 'ADMIN', true),
('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/3OT.eOomQFAV6x7b.yHYS', 'USER', true);

-- Artistas
INSERT INTO artists (name, type, created_at, updated_at) VALUES
('Serj Tankian', 'SOLO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mike Shinoda', 'SOLO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Michel Teló', 'SOLO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Guns N'' Roses', 'BAND', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Álbuns do Serj Tankian
INSERT INTO albums (title, release_year, created_at, updated_at) VALUES
('Harakiri', 2012, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Black Blooms', 2019, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Rough Dog', 2021, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Álbuns do Mike Shinoda
INSERT INTO albums (title, release_year, created_at, updated_at) VALUES
('The Rising Tied', 2005, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Post Traumatic', 2018, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Post Traumatic EP', 2018, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Where''d You Go', 2022, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Álbuns do Michel Teló
INSERT INTO albums (title, release_year, created_at, updated_at) VALUES
('Bem Sertanejo', 2014, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bem Sertanejo - O Show (Ao Vivo)', 2015, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bem Sertanejo - (1ª Temporada) - EP', 2016, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Álbuns do Guns N' Roses
INSERT INTO albums (title, release_year, created_at, updated_at) VALUES
('Use Your Illusion I', 1991, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Use Your Illusion II', 1991, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Greatest Hits', 2004, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Relacionamentos Artist-Album
-- Serj Tankian (id=1) -> Harakiri, Black Blooms, The Rough Dog (ids 1,2,3)
INSERT INTO artist_album (artist_id, album_id) VALUES
(1, 1), (1, 2), (1, 3);

-- Mike Shinoda (id=2) -> The Rising Tied, Post Traumatic, Post Traumatic EP, Where'd You Go (ids 4,5,6,7)
INSERT INTO artist_album (artist_id, album_id) VALUES
(2, 4), (2, 5), (2, 6), (2, 7);

-- Michel Teló (id=3) -> Bem Sertanejo albums (ids 8,9,10)
INSERT INTO artist_album (artist_id, album_id) VALUES
(3, 8), (3, 9), (3, 10);

-- Guns N' Roses (id=4) -> Use Your Illusion I, II, Greatest Hits (ids 11,12,13)
INSERT INTO artist_album (artist_id, album_id) VALUES
(4, 11), (4, 12), (4, 13);
