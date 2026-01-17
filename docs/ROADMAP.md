# RepoMind Roadmap

## Project: REPOMIND

**Target Release:** v0.1
**Target Repo:** Apache Kafka
**Theme:** Semantic Search + Context Packs for Codex/Copilot

---

# EPIC: REPOMIND-EPIC-1 — Bootstrap & Developer Experience

### REPOMIND-1 — Initialize Maven multi-module structure

**Type:** Story
**Priority:** High

**Description**
Create a Maven multi-module project with the modules:

* repomind-core
* repomind-indexer
* repomind-storage
* repomind-migrations
* repomind-cli

**Acceptance Criteria**

* `mvn -q test` succeeds
* `mvn -q package` succeeds
* CLI jar builds as `repomind-cli/target/repomind.jar`

**Implementation Notes**

* Use Java 21 (or Java 17 if you prefer, but lock it)
* Use Picocli for CLI

**Test Plan**

* CI: run `mvn test`
* Manual: run CLI `--help`

**Observability**

* Basic logback logging enabled

---

### REPOMIND-2 — Add config loading (YAML + env overrides)

**Type:** Story
**Priority:** High

**Description**
Implement config loading from:

1. `repomind.config.yaml`
2. env overrides
3. CLI overrides (later)

**Acceptance Criteria**

* Config loads successfully with defaults
* Missing config results in actionable error message
* `repomind doctor` prints resolved config (safe values only)

**Implementation Notes**

* SnakeYAML for YAML parsing
* Provide a config model: `RepoMindConfig`

**Test Plan**

* Unit tests for YAML parsing
* Unit tests for env override resolution

**Observability**

* Log config file path and loaded sections

---

### REPOMIND-3 — Add Docker compose for Postgres + pgvector

**Type:** Story
**Priority:** Medium

**Description**
Add `docker/docker-compose.yml` for local Postgres.

**Acceptance Criteria**

* `docker compose up -d` starts Postgres
* `.env.example` contains DB vars
* README includes setup steps

**Test Plan**

* Manual: connect via `psql` or app startup

**Observability**

* N/A

---

# EPIC: REPOMIND-EPIC-2 — Storage Layer (Postgres + pgvector)

### REPOMIND-4 — Flyway migration: enable pgvector + create code_chunks table

**Type:** Story
**Priority:** High

**Description**
Create Flyway migration to:

* `CREATE EXTENSION vector`
* create `code_chunks` table
* unique index on `chunk_id`

**Acceptance Criteria**

* Migration runs on fresh DB without manual steps
* Table schema includes:

    * repo, file_path, symbol, symbol_kind, start_line, end_line
    * code, summary
    * embedding vector
    * chunk_id unique

**Implementation Notes**

* Keep embedding dimension configurable (default 1536)

**Test Plan**

* Integration test using Testcontainers Postgres:

    * run Flyway
    * verify table exists

**Observability**

* Log migration version on startup

---

### REPOMIND-5 — Implement DB connectivity (HikariCP)

**Type:** Story
**Priority:** High

**Description**
Implement a DB factory to create a pooled datasource from env/config.

**Acceptance Criteria**

* Can connect using `.env`
* Failure modes are clear:

    * host unreachable
    * auth failure
    * database missing

**Implementation Notes**

* Use `HikariDataSource`
* Use timeouts (connectionTimeout, validationTimeout)

**Test Plan**

* Unit test: config validation
* Integration test: connect to Testcontainers Postgres

**Observability**

* Log pool init success + DB host/port (no password)

---

### REPOMIND-6 — Implement CodeChunkRepository (upsert + vector search)

**Type:** Story
**Priority:** High

**Description**
Implement repository methods:

* `upsertChunks(List<CodeChunk>)`
* `search(repo, queryEmbedding, topK)`

**Acceptance Criteria**

* Upsert is idempotent (chunk_id unique)
* Search returns topK ordered by similarity
* Supports repo filtering

**Implementation Notes**

* SQL should use pgvector cosine distance operator
* Store `updated_at` on upsert

**Test Plan**

* Integration test:

    * insert 10 fake chunks with deterministic embeddings
    * search returns expected chunk in top 3

**Observability**

* Log query latency for search

---

# EPIC: REPOMIND-EPIC-3 — Indexer (Scan + Chunk Java)

### REPOMIND-7 — Implement repo scanner (include/exclude)

**Type:** Story
**Priority:** High

**Description**
Scan a repo and return list of files to index.

**Acceptance Criteria**

* Includes patterns from config (e.g. `**/*.java`)
* Excludes `.git`, `target`, `build`, `node_modules`
* Handles symlinks safely (no infinite loops)

**Implementation Notes**

* Use `java.nio.file.Files.walkFileTree`
* Add max file size guard (configurable)

**Test Plan**

* Unit tests on fixture directory tree

**Observability**

* Log number of files discovered and excluded

---

### REPOMIND-8 — JavaParser chunker: extract class/method chunks

**Type:** Story
**Priority:** High

**Description**
Use JavaParser to extract symbol-aware chunks:

* methods as primary chunks
* optionally class-level chunks

**Acceptance Criteria**

* For a Java file:

    * chunks include symbol name and line ranges
    * extracted code is non-empty
* Chunk IDs are stable:

    * `repo:file:symbol:start:end`

**Implementation Notes**

* Prefer method chunks first (best search signal)
* Keep excerpt length bounded (config max chars)

**Test Plan**

* Unit test: given sample Java file, expected chunk count and names
* Ensure line numbers match actual source

**Observability**

* Log parse failures but continue indexing

---

### REPOMIND-9 — Index command: scan + chunk + persist

**Type:** Story
**Priority:** High

**Description**
Implement CLI command:

* `repomind index <repoPath> --repo kafka`

**Acceptance Criteria**

* Creates chunks from repo
* Embeds chunks (stub or real)
* Writes to DB via repository
* Prints stats:

    * scanned files
    * chunks
    * duration

**Implementation Notes**

* Build as pipeline:

    * scan → parse → chunk → embed → upsert

**Test Plan**

* Integration test indexing fixture repo

**Observability**

* Log indexing duration and chunk throughput

---

# EPIC: REPOMIND-EPIC-4 — Embeddings

### REPOMIND-10 — Embeddings client (OpenAI) + batching

**Type:** Story
**Priority:** High

**Description**
Implement an embeddings client:

* batch requests
* retries on transient failures
* configurable model name

**Acceptance Criteria**

* Can embed list of texts
* Handles rate limiting (backoff)
* Timeout is enforced

**Implementation Notes**

* Keep HTTP client reusable
* Support future provider swapping

**Test Plan**

* Unit tests using mocked HTTP server (no real calls)
* Validate JSON serialization/deserialization

**Observability**

* Log rate-limit events and retry count

---

### REPOMIND-11 — Deterministic stub embeddings for tests

**Type:** Story
**Priority:** High

**Description**
Provide a deterministic embedding provider for tests and offline runs.

**Acceptance Criteria**

* Same input text → same embedding vector always
* No network calls
* Dimension matches configured dimension

**Implementation Notes**

* Use hashing (e.g., SHA-256) to generate a stable float vector

**Test Plan**

* Unit test: repeated calls return identical vectors

**Observability**

* Log provider selection (openai vs stub)

---

# EPIC: REPOMIND-EPIC-5 — Search CLI

### REPOMIND-12 — Implement `repomind search`

**Type:** Story
**Priority:** High

**Description**
Search indexed chunks by semantic similarity.

**Acceptance Criteria**

* `repomind search "leader election" --repo kafka --limit 10`
* Output includes:

    * rank
    * file path
    * symbol
    * line range
    * snippet preview

**Implementation Notes**

* Keep output compact and readable
* Optionally add `--format json`

**Test Plan**

* Integration test with stub embeddings + known data

**Observability**

* Log query latency and result count

---

### REPOMIND-13 — Add filters to search

**Type:** Story
**Priority:** Medium

**Description**
Add optional filters:

* `--file-pattern`
* `--language`
* `--symbol-kind`

**Acceptance Criteria**

* Filtered results reflect constraints

**Test Plan**

* Unit tests for filter logic
* Integration test for file-pattern filter

---

# EPIC: REPOMIND-EPIC-6 — Context Packs for Codex/Copilot

### REPOMIND-14 — Implement `repomind context`

**Type:** Story
**Priority:** High

**Description**
Generate a markdown context pack from semantic search results.

**Acceptance Criteria**

* `repomind context "how does FetchRequest work?" --repo kafka --out context.md`
* Output includes:

    * summary header
    * top relevant symbols
    * file paths + line ranges
    * short code excerpts
* Dedup repeated chunks
* Enforce size limit (default 60k chars)

**Implementation Notes**

* Context pack format should be stable (deterministic ordering)

**Test Plan**

* Integration test:

    * known indexed chunks
    * verify output contains expected file paths

**Observability**

* Log pack size and chunk count

---

### REPOMIND-15 — Add “Suggested edit points” section

**Type:** Story
**Priority:** Medium

**Description**
Add final section listing top 3–5 edit targets.

**Acceptance Criteria**

* Context includes a list like:

    * `File: ... Symbol: ...`

**Test Plan**

* Unit test on context rendering

---

### REPOMIND-16 — Support output formats and piping

**Type:** Story
**Priority:** Medium

**Description**
Support:

* `--format md|text`
* `--out <file>`
* default stdout

**Acceptance Criteria**

* `repomind context ...` prints to stdout by default
* Works in pipelines (for Codex/Copilot)

**Test Plan**

* CLI test capturing stdout

---

# EPIC: REPOMIND-EPIC-7 — Kafka Validation & Benchmarking

### REPOMIND-17 — Add script: clone + index Kafka + run queries

**Type:** Story
**Priority:** High

**Description**
Add a script to validate RepoMind on Kafka.

**Acceptance Criteria**

* Script can:

    * clone kafka (if missing)
    * index it
    * run a fixed list of queries
    * print top results

**Test Plan**

* Manual run (Kafka is too large for CI by default)

**Observability**

* Prints timing for each stage

---

### REPOMIND-18 — Retrieval quality evaluation checklist

**Type:** Story
**Priority:** Medium

**Description**
Create a checklist to manually verify:

* 8/10 use cases show relevant result in top 5

**Acceptance Criteria**

* docs include a table:

    * query → expected subsystem → top results found?

---

# Ticket Template (copy/paste)

Use this for new tickets:

```md
## Description
...

## Acceptance Criteria
- [ ] ...
- [ ] ...

## Implementation Notes
- ...

## Test Plan
- Unit:
- Integration:

## Observability
- Logs:
- Metrics (optional):
```

