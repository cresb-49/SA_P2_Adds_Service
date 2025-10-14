-- Agregar columnas nuevas a la tabla adds
ALTER TABLE adds
    ADD COLUMN IF NOT EXISTS content_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS external_media BOOLEAN NOT NULL DEFAULT FALSE;