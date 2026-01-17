# RepoMind

RepoMind builds semantic search and context packs for code repositories, optimized for coding agents.

Target repo for validation: Spring PetClinic.

## Scope (v0.1)

- Java-first indexing with file-level chunks
- Vector search in Postgres (pgvector)
- Local embeddings via sentence-transformers `code-bert-tiny-code-search`
- CLI: doctor, index, search, context
- Deterministic outputs and safe defaults

Out of scope (v0.1):

- call graph
- usages query (beyond simple grep)
- knowledge graph
- incremental indexing

## Requirements

- Java 21
- Maven
- Docker (for local Postgres + embeddings server)

## Installation

### Postgres (pgvector)

Docker (recommended for local dev):

```bash
./scripts/dev-up.sh
```

Local Postgres:

- Install Postgres 16 and the pgvector extension (package name varies by OS).
- Create a database and user that match `.env` or `repomind.config.yaml`.
- Flyway will create the schema and enable the extension during migrations.

### Embeddings server (local HTTP)

Docker (recommended):

```bash
./scripts/dev-up.sh
```

Manual (Python 3.11+):

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r embedding-server/requirements.txt
uvicorn embed_server:app --host 0.0.0.0 --port 8088
```

### .env setup

Create a local `.env` file for secrets and overrides:

```bash
cp .env.example .env
```

Update values as needed (DB host/port/user/password and optional embeddings model path).
Set `REPOMIND_EMBEDDINGS_LOCAL_HTTP_URL` if the embeddings server is on a different host/port.

## Quickstart

Start Postgres:

```bash
./scripts/dev-up.sh
```

The same command also starts the local embeddings server.

Run tests:

```bash
mvn -q -DskipTests=false test
```

Build the CLI jar:

```bash
mvn -q package
```

Example CLI usage:

```bash
java -jar repomind-cli/target/repomind.jar doctor
java -jar repomind-cli/target/repomind.jar index ../spring-petclinic --repo petclinic
java -jar repomind-cli/target/repomind.jar search "owner address" --repo petclinic --limit 10
java -jar repomind-cli/target/repomind.jar context "add visit validation" --repo petclinic --out context.md
```

When `embeddings.provider=local-http`, `repomind doctor` also checks that the
embedding server is reachable.

## Configuration

- `repomind.config.yaml` for config
- `.env` for secrets and overrides (copy from `.env.example`)
- No API keys required for embeddings (local model)

Example embeddings config:

```yaml
embeddings:
  provider: local-http
  model: sentence-transformers/code-bert-tiny-code-search
  modelPath: /path/to/local/model
  local_http:
    url: http://localhost:8088
```

The model is downloaded on first use and cached by sentence-transformers (typically under
`~/.cache/torch/sentence_transformers` or `~/.cache/huggingface`). Set `modelPath` or
`REPOMIND_EMBEDDINGS_MODEL_PATH` to use a local checkout.

## Repository Modules

- `repomind-core`: domain models, ranking, context pack generation
- `repomind-indexer`: scanner, chunker, indexing pipeline
- `repomind-storage`: DB access, migrations helpers
- `repomind-migrations`: Flyway migrations
- `repomind-cli`: Picocli CLI wrapper

## Docs

- `docs/ARCHITECTURE.md`
- `docs/CONVENTIONS.md`
- `docs/DECISIONS.md`
- `docs/ROADMAP.md`
- `docs/USE_CASES.md`
