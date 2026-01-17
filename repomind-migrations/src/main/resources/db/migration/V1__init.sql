-- Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS code_chunks (
  id            BIGSERIAL PRIMARY KEY,
  repo          TEXT NOT NULL,
  file_path     TEXT NOT NULL,
  language      TEXT NOT NULL,
  symbol        TEXT,
  symbol_kind   TEXT,
  start_line    INT,
  end_line      INT,
  code          TEXT NOT NULL,
  summary       TEXT,
  embedding     vector(1536) NOT NULL,
  chunk_id      TEXT NOT NULL, -- stable id for upsert, e.g. repo:file:symbol:start:end
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_code_chunks_chunk_id ON code_chunks(chunk_id);

-- Optional: vector index (tune lists after some data volume)
-- CREATE INDEX IF NOT EXISTS ix_code_chunks_embedding ON code_chunks USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

