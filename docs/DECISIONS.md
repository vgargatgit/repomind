# RepoMind Decisions (ADR-lite)

## ADR-0001: Target repo for validation is Apache Kafka
Kafka is large, Java-heavy, and provides realistic complexity to validate indexing and search quality.

## ADR-0002: Java-first indexing (v0.1)
We support Java indexing first.
Other languages (Scala, Python, etc.) are deferred.

## ADR-0003: Symbol-aware chunking using JavaParser
We chunk by:
- class / interface
- method
  with stable metadata (file path + line ranges).

## ADR-0004: Vector store is Postgres + pgvector
Reason:
- operational simplicity
- good enough performance for v0.1
- easy local dev using Docker

## ADR-0005: Knowledge Graph is deferred (v0.2)
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
- v0.1 supports OpenAI embeddings in real usage
- tests use a deterministic stub embedding implementation

---
