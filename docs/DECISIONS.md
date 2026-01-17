# RepoMind Decisions (ADR-lite)

## ADR-0001: Target repo for validation is Spring PetClinic
PetClinic is Java-based and small enough for fast local iteration while still representing a real app.

## ADR-0002: Java-first indexing (v0.1)
We support Java indexing first.
Other languages (Scala, Python, etc.) are deferred.

## ADR-0003: File-level chunking (v0.1)
We chunk by file only.
Each file is a single chunk with stable metadata (file path + line ranges).

## ADR-0004: Vector store is Postgres + pgvector
Reason:
- operational simplicity
- good enough performance for v0.1
- easy local dev using Docker

## ADR-0005: Knowledge Graph is deferred (post-v0.1)
We will focus on semantic search MVP first.
KG will be added later for:
- call graph queries
- endpoint->storage flows
- structural traversal

## ADR-0006: CLI is Picocli
Reason:
- mature, easy subcommands
- Java-native

## ADR-0007: Embeddings provider strategy
- v0.1 uses sentence-transformers `code-bert-tiny-code-search` locally
- tests use a deterministic stub embedding implementation

---
