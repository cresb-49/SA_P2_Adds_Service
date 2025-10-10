CREATE TABLE add (
  id           UUID PRIMARY KEY,
  content      VARCHAR(255) NOT NULL,
  type         VARCHAR(32)  NOT NULL,
  url_content  TEXT         NOT NULL,
  active       BOOLEAN      NOT NULL,
  description  VARCHAR(255) NOT NULL,
  cinema_id    UUID         NOT NULL,
  created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,

  -- Valida los 4 valores del enum AddType
CONSTRAINT chk_add_type
    CHECK (type IN ('TEXT_BANNER','MEDIA_HORIZONTAL','MEDIA_VERTICAL'))
);

-- Índices recomendados para filtros
CREATE INDEX idx_add_cinema_id ON add (cinema_id);
CREATE INDEX idx_add_active    ON add (active);
CREATE INDEX idx_add_type      ON add (type);
CREATE INDEX idx_add_created_at ON add (created_at DESC);