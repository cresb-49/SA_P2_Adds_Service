CREATE TABLE IF NOT EXISTS durations (
  id          UUID PRIMARY KEY,
  days        INTEGER NOT NULL UNIQUE,
  created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT chk_durations_days_positive CHECK (days > 0)
);

-- Índice opcional si vas a ordenar o filtrar frecuentemente por fecha de creación
CREATE INDEX IF NOT EXISTS idx_durations_created_at ON durations (created_at DESC);

-- Generar UUIDs en BD (si no estaba habilitado)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Insertar duraciones permitidas
INSERT INTO durations (id, days, created_at) VALUES
  (gen_random_uuid(), 1,  NOW()),
  (gen_random_uuid(), 3,  NOW()),
  (gen_random_uuid(), 7,  NOW()),
  (gen_random_uuid(), 14, NOW())
ON CONFLICT (days) DO NOTHING;  -- evita duplicados si ya existen