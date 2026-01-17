# Task Playbook (Codex)

Use these patterns:

## Pattern A: Add a CLI command
1. Add command skeleton in `cli/`
2. Implement service function in `repomind/`
3. Add tests in `tests/`
4. Update README with usage

## Pattern B: Add an indexer feature
1. Update chunker / scanner in `indexer/`
2. Ensure metadata stability (repo, file, symbol, line range)
3. Add fixture repo under `tests/fixtures/`
4. Add integration test that runs index -> search

## Pattern C: Improve context generation
1. Update prompt template in `prompts/`
2. Add a test showing the context pack is stable and within size
3. Add one example in README
