
# RepoMind Conventions

This document defines RepoMind’s engineering conventions and expected patterns.
RepoMind is a Java-first tool that builds semantic search + context packs for repositories
(e.g., Spring PetClinic) and is designed to work well with coding agents (Codex/Copilot).

---

## 1) Guiding Principles

### 1.1 Deterministic outputs
For a given git commit and configuration:
- chunk IDs must be stable
- chunk boundaries should be stable (line ranges)
- context pack ordering should be stable

### 1.2 Safe by default
- No destructive DB operations unless explicitly requested
- No external network calls in unit/integration tests
- No secrets in repo

### 1.3 Copy/paste friendly
CLI output is meant to be pasted into:
- Codex prompts
- Copilot CLI context
- Slack/Jira discussions

---

## 2) Project Layout

RepoMind is a Maven multi-module project.

- `repomind-core`
  - domain models (CodeChunk, SearchQuery, SearchResult)
  - context pack generation (markdown rendering, dedupe, ranking)
  - config model

- `repomind-indexer`
  - repository scanner
  - file-level chunker
  - indexing pipeline orchestration

- `repomind-storage`
  - Postgres + pgvector repository implementations
  - DB connectivity (HikariCP)
  - Flyway runner utilities

- `repomind-migrations`
  - Flyway migration scripts in `src/main/resources/db/migration`

- `repomind-cli`
  - Picocli commands: doctor/index/search/context
  - output formatting for CLI

- `docs/`
  - architecture, roadmap, decisions

- `.codex/`
  - Codex-specific workflow instructions

---

## 3) Naming Conventions

### 3.1 Commands
- Root command: `repomind`
- Subcommands:
  - `repomind doctor`
  - `repomind index`
  - `repomind search`
  - `repomind context`

### 3.2 Packages
Base package: `com.repomind`

Suggested packages:
- `com.repomind.core.*`
- `com.repomind.indexer.*`
- `com.repomind.storage.*`
- `com.repomind.cli.*`

### 3.3 Classes
Use explicit names over generic ones.

Examples:
- `RepoScanner`
- `FileChunker`
- `EmbeddingProvider`
- `SentenceTransformersEmbeddingProvider`
- `StubEmbeddingProvider`
- `PgVectorCodeChunkRepository`
- `ContextPackRenderer`

---

## 4) IDs and Metadata Conventions

### 4.1 Stable chunk IDs
Chunks must have stable IDs to support upserts and re-indexing.

Required fields:
- repo
- file_path
- symbol_kind
- symbol (file name)
- start_line
- end_line

Chunk ID format (string):
```
repo:file_path:symbol_kind:symbol:start_line:end_line
```
For v0.1 file-level chunking:
- `symbol_kind` is `FILE`
- `symbol` is the file name
- `start_line` is 1
- `end_line` is the last line in the file

### 4.2 Line numbers
- Line numbers are 1-based
- Always store `start_line` and `end_line`
- Always print them in CLI output

---

## 5) Chunking Conventions

### 5.1 Default chunking strategy (v0.1)
- File-level only (one chunk per file)

### 5.2 Chunk size limits
- Enforce max chunk chars (config)
- If a file exceeds max size:
  - truncate the excerpt
  - keep line range accurate for the full file

### 5.3 Parse failures
If reading a file fails:
- log a warning with file path
- continue indexing other files
- do not crash the whole indexing job

---

## 6) Embeddings Conventions

### 6.1 Providers
v0.1 supports:
- sentence-transformers `code-bert-tiny-code-search` (local)
- Stub embeddings (tests/offline)

### 6.2 No network calls in tests
- unit tests: always stub embeddings
- integration tests: stub embeddings + Testcontainers DB

### 6.3 Embedding dimension
Embedding dimension must be consistent with DB schema.
Use the model output dimension (configurable).

---

## 7) Storage Conventions (pgvector)

### 7.1 Upserts
- Upsert by `chunk_id`
- Update `updated_at` on conflict

### 7.2 Search
- Search always filtered by repo name (required)
- `topK` is capped to prevent accidental huge outputs

### 7.3 Migration ownership
- Flyway migrations live in `repomind-migrations`
- No “manual schema changes” outside migrations

---

## 8) CLI Output Conventions

### 8.1 Search output format
Must include:
- rank number
- score/distance (optional but recommended)
- file path
- symbol
- line range
- snippet preview (short)

Example:

```

#1  (score=0.12)
clients/src/main/java/.../ConsumerCoordinator.java:120-220
symbol: onJoinPrepare()
preview: ...

```

### 8.2 Context pack format
Markdown output must include:
- query
- top results (file:line)
- code excerpts (short)
- suggested edit points

Context packs must be stable and deterministic in ordering.

---

## 9) Logging Conventions

- Use SLF4J everywhere
- Default log level: INFO
- Prefer structured messages:
  - include repo name, file path, counts

---

## 10) Error Handling Conventions

- Prefer actionable errors:
  - missing config
  - missing DB connection
  - missing embeddings model or failed model load
- Do not print stack traces by default for expected errors
- Add `--verbose` for debug stack traces (future enhancement)

---
