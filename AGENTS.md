# RepoMind — Agent Operating Guide (AGENTS.md)

RepoMind builds semantic search + (optional) code knowledge graph for repositories and generates
high-signal “context packs” for coding agents (Codex / Copilot CLI / Claude Code).

This repo is Java-first.

---
## Coding guidelines
1. Follow TDD - Write tests first wherever possible
2. Follow SOLID and good Object-Oriented Design

## Goals

1. Index source code into:
    - Vector index for semantic search (pgvector)
    - Optional knowledge graph facts (call graph, endpoints, tables)
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

---

## Key Design Principles

- **Symbol-aware chunking**: prefer class/method-level chunks, not arbitrary splits.
- **Stable identifiers**: chunks must have stable IDs for a given git commit.
- **Safe defaults**: no destructive DB operations unless explicit flag is provided.
- **Copy/paste output**: CLI output is meant to be pasted into agent prompts.

---

## Repository Modules

- `repomind-core`: domain models + query ranking + context pack generation
- `repomind-indexer`: scanner + chunker (JavaParser for Java), emits chunks
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
- `java -jar repomind-cli/target/repomind.jar index ../tests-fixtures/sample-repo --repo sample`
- `java -jar repomind-cli/target/repomind.jar search "refund eligibility" --repo sample`
- `java -jar repomind-cli/target/repomind.jar context "add negative amount validation" --repo sample --out context.md`

---
