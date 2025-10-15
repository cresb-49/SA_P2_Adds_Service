CREATE TABLE IF NOT EXISTS prices (
  id                    UUID PRIMARY KEY,
  cinema_id             UUID NOT NULL,
  amount_text_banner        NUMERIC(10,2) NOT NULL,
  amount_media_vertical     NUMERIC(10,2) NOT NULL,
  amount_media_horizontal   NUMERIC(10,2) NOT NULL,
  created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,

  CONSTRAINT uq_prices_cinema_type UNIQUE (cinema_id)
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_prices_cinema_id ON prices (cinema_id);
CREATE INDEX IF NOT EXISTS idx_prices_created_at ON prices (created_at DESC);