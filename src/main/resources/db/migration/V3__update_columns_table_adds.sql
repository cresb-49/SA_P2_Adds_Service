-- Agregar columnas nuevas a la tabla adds
ALTER TABLE adds
    ADD COLUMN IF NOT EXISTS user_id UUID NOT NULL,
    ADD COLUMN IF NOT EXISTS add_expiration TIMESTAMP WITHOUT TIME ZONE NOT NULL;