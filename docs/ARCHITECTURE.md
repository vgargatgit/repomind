
# RepoMind Architecture

RepoMind is a Java-first tool that builds semantic search and context packs for codebases
(e.g., Spring PetClinic). It is designed to “inject the right context” into coding agents like Codex
and Copilot CLI.

---

## 1) System Overview

RepoMind has two major flows:

1. **Indexing Flow**
   - scan repo files
   - chunk source code into file-level units
   - generate embeddings
   - store in Postgres (pgvector)

2. **Query Flow**
   - embed query text
   - vector search for relevant code chunks
   - render results (search output or context pack)

Optional future flow (post-v0.1):
- Knowledge Graph extraction and traversal

---

## 2) Components

### 2.1 Repo Scanner
**Responsibility**
- discover files to index using include/exclude patterns

**Input**
- repo root path
- include/exclude patterns from config

**Output**
- list of file paths

**Key requirements**
- deterministic ordering
- safe traversal (avoid symlink loops)

---

### 2.2 Chunker (file-level)
**Responsibility**
- read Java source files
- emit one chunk per file

**Chunk types (v0.1)**
- FILE chunks only

**Chunk metadata**
- repo
- file_path
- language
- symbol_kind (FILE)
- symbol (file name)
- start_line / end_line
- code excerpt

**Output**
- list of `CodeChunk`

---

### 2.3 Embedding Provider
**Responsibility**
- convert chunk text into embedding vectors

**Implementations**
- `SentenceTransformersEmbeddingProvider` using `code-bert-tiny-code-search` locally
- `StubEmbeddingProvider` (tests/offline)

**Key requirements**
- batching
- local model load and reuse
- deterministic output (stub)

---

### 2.4 Vector Store (Postgres + pgvector)
**Responsibility**
- store embeddings and metadata
- perform similarity search

**Table**
- `code_chunks`

**Operations**
- upsert by `chunk_id`
- search by cosine distance

---

### 2.5 Search Service
**Responsibility**
- take a natural language query
- embed it
- retrieve topK chunks from vector store
- return ranked results

---

### 2.6 Context Pack Generator
**Responsibility**
- convert top search results into a markdown pack suitable for coding agents

**Context pack rules**
- must include file paths + line ranges
- must include short excerpts
- must dedupe similar chunks
- must respect size limits

---

### 2.7 CLI (Picocli)
**Commands**
- `repomind doctor`
  - validates config + DB connectivity
- `repomind index`
  - runs indexing pipeline
- `repomind search`
  - semantic search
- `repomind context`
  - outputs markdown context pack

---

## 3) Data Model

### 3.1 CodeChunk
Represents an indexed unit of code.

Fields (v0.1):
- `repo` (string)
- `filePath` (string)
- `language` (string)
- `symbolKind` (string: FILE)
- `symbol` (string: file name)
- `startLine` (int)
- `endLine` (int)
- `code` (string)
- `summary` (string, optional)
- `chunkId` (string, stable id)
- `embedding` (float[])

---

## 4) Indexing Pipeline (v0.1)

### 4.1 Steps
1. Load config
2. Scan repository files
3. Chunk Java files into `CodeChunk` (file-level)
4. Generate embeddings for chunks
5. Upsert into `code_chunks` table

### 4.2 Determinism requirements
- Scanner outputs stable sorted file list
- Chunker outputs stable ordering within a file
- chunk_id generation is stable

---

## 5) Query Pipeline (v0.1)

### 5.1 Steps
1. Embed the query text
2. Search vector store for topK chunks
3. Render results:
   - CLI output (search)
   - Markdown pack (context)

### 5.2 Ranking & dedupe
- Ranking is based on vector similarity
- Context pack performs dedupe by:
  - same file + overlapping line ranges
  - identical chunk_id

---

## 6) Storage Architecture

### 6.1 Postgres schema
Primary table:
- `code_chunks`

Key indexes:
- unique index on `chunk_id`
- vector index (optional) on `embedding`

---

## 7) Deployment Model (v0.1)

RepoMind is expected to run locally:
- Postgres via Docker compose
- RepoMind CLI jar on developer machine

Future:
- indexing service in CI
- shared index per team

---

## 8) Future Architecture (post-v0.1)

### 8.1 Knowledge Graph
Add a graph extractor to emit:
- nodes: Class, Method, File, Package
- edges: DECLARES, CALLS, IMPORTS

Use cases:
- call graph traversal
- path finding (controller → storage)
- structural narrowing before semantic ranking

---

## 9) Failure Modes and Handling

### 9.1 Parsing failures
- log warning and continue indexing

### 9.2 Embedding failures
- retry transient errors
- fail fast for invalid credentials
- allow stub mode for offline

### 9.3 DB failures
- actionable errors for connection/auth
- do not partially commit within a batch if avoidable

---

## 10) Security Considerations

- Do not store secrets in DB
- Avoid indexing sensitive files by default
- Configurable include/exclude patterns
- No telemetry by default

---
