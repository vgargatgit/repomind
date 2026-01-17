# Codex Working Notes for RepoMind

Codex should treat RepoMind as a **tooling** project:
- correctness and determinism matter more than cleverness
- outputs must be concise and copy/paste friendly

## Suggested workflow
1) Run `repomind doctor`
2) Run `repomind index ...` on a small sample repo under `tests/fixtures/sample-repo`
3) Verify `repomind search` returns correct results
4) Build `repomind context` packs and ensure they are high signal

## What "good" looks like
- Search results show the right file and symbol names
- Context packs are under the configured size limit
- Graph queries (if enabled) produce stable and explainable output
