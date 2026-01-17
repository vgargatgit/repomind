# RepoMind — Agent Operating Guide (AGENTS.md)

RepoMind builds semantic search and context packs for repositories and generates
high-signal “context packs” for coding agents (Codex / Copilot CLI / Claude Code).

This repo is Java-first.
Target repo for v0.1 validation: Spring PetClinic.

---
## Coding guidelines
1. Follow TDD - Write tests first wherever possible
2. Follow SOLID and good Object-Oriented Design

## Goals

1. Index source code into:
    - Vector index for semantic search (pgvector)
2. Provide a CLI:
    - `repomind index ...`
    - `repomind search ...`
    - `repomind context ...`
    - `repomind doctor`
3. Keep results deterministic, stable, and safe.

---

## Non-goals

- Full IDE replacement
- Runtime instrumentation/profiling
- Auto-refactoring without explicit user intent
- Call graph extraction
- Usages query (beyond simple grep)
- Knowledge graph extraction
- Incremental indexing

---

## Key Design Principles

- **File-level chunking (v0.1)**: one chunk per file, deterministic ordering.
- **Stable identifiers**: chunks must have stable IDs for a given git commit.
- **Safe defaults**: no destructive DB operations unless explicit flag is provided.
- **Copy/paste output**: CLI output is meant to be pasted into agent prompts.
- **Local embeddings**: sentence-transformers `code-bert-tiny-code-search` (no API keys).

---

## Repository Modules

- `repomind-core`: domain models + query ranking + context pack generation
- `repomind-indexer`: scanner + file-level chunker, emits chunks
- `repomind-storage`: DB access (pgvector), migrations helpers
- `repomind-migrations`: Flyway migrations
- `repomind-cli`: Picocli-based CLI wrapper

---

## Definition of Done

- Adds tests (JUnit5; integration uses Testcontainers where relevant)
- Updates docs if behavior changes
- Adds CLI examples in README for user-facing changes
- No secrets in code; config from env + YAML

---

## Security & Secrets

- Use `.env` locally; do not commit secrets
- All credentials via env vars
- No telemetry by default

---

## Expected Commands

- `./scripts/dev-up.sh`
- `mvn -q -DskipTests=false test`
- `java -jar repomind-cli/target/repomind.jar doctor`
- `java -jar repomind-cli/target/repomind.jar index ../spring-petclinic --repo petclinic`
- `java -jar repomind-cli/target/repomind.jar search "owner address" --repo petclinic --limit 10`
- `java -jar repomind-cli/target/repomind.jar context "add visit validation" --repo petclinic --out context.md`

---
