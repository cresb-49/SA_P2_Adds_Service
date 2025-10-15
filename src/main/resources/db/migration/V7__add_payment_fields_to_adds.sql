-- 1) Agregar columnas (inicialmente permisivas)
ALTER TABLE adds
  ADD COLUMN IF NOT EXISTS payment_state VARCHAR(32),                 -- enum como texto
  ADD COLUMN IF NOT EXISTS paid_at       TIMESTAMP WITHOUT TIME ZONE, -- puede ser NULL
  ADD COLUMN IF NOT EXISTS price         NUMERIC(10,2);               -- dinero

-- 2) Backfill seguro para filas existentes
UPDATE adds SET payment_state = 'PENDING' WHERE payment_state IS NULL;
UPDATE adds SET price = 0 WHERE price IS NULL;

-- 3) Endurecer las restricciones
ALTER TABLE adds
  ALTER COLUMN payment_state SET NOT NULL,
  ALTER COLUMN price SET NOT NULL;