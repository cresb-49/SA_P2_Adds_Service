CREATE TABLE adds (
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
CONSTRAINT chk_adds_type
    CHECK (type IN ('TEXT_BANNER','MEDIA_HORIZONTAL','MEDIA_VERTICAL'))
);

-- Índices recomendados para filtros
CREATE INDEX idx_adds_cinema_id ON adds (cinema_id);
CREATE INDEX idx_adds_active    ON adds (active);
CREATE INDEX idx_adds_type      ON adds (type);
CREATE INDEX idx_adds_created_at ON adds (created_at DESC);