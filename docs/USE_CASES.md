# RepoMind Use Cases (v0.1)

Target repo for analysis: Spring PetClinic (open source)

RepoMind’s primary purpose is to provide:
1) semantic search over the PetClinic codebase
2) context packs for coding agents (Codex/Copilot) so they can answer questions or propose changes with high-quality context

---

## A. Semantic Search Use Cases (PetClinic)

### A1. Locate implementation for a concept
- "Where is owner registration handled?"
- "Where is pet creation implemented?"
- "Where is visit scheduling handled?"
- "Where are vet specialties defined?"
- "Where is the clinic service wired?"

### A2. Trace data flow by keyword intent
- "Where does Owner get validated?"
- "Where is Visit saved?"
- "Where are pets loaded for an owner?"
- "Where is form binding for Owner handled?"
- "Where is PetType list provided?"

### A3. Find the 'entry point' class/method for a subsystem
- "What is the Spring Boot entrypoint?"
- "Where are web MVC controllers defined?"
- "Where is visit validation handled?"
- "Where is the owners repository implemented?"

### A4. Find configuration and defaults
- "Where is datasource configuration defined?"
- "Where are application properties loaded?"
- "Where is H2 profile configured?"

---

## B. Context Pack Use Cases (for Codex/Copilot)

RepoMind should generate a markdown context pack that includes:
- top relevant classes/methods
- file paths + line ranges
- short excerpts (only necessary parts)
- suggested "where to edit"

Examples:
- "Create a context pack for explaining how owner registration works end-to-end"
- "Create a context pack for adding validation to visit creation"
- "Create a context pack for refactoring a controller and updating tests"

---

## C. System/Tooling Use Cases

### C1. Indexing
- Index PetClinic repo locally (single command)
- Re-index after git pull
- Show stats: number of files, chunks, total time

### C2. Search UX
- `repomind search "owner address" --repo petclinic --limit 10`
- Print results with:
    - file path
    - symbol name
    - line range
    - short snippet preview

### C3. Context generation UX
- `repomind context "how are visits stored?" --repo petclinic --out context.md`
- Context pack should be under a size limit (configurable)

---

## Non-goals for v0.1
- Call graph
- Usages query (beyond simple grep)
- Knowledge graph
- Incremental indexing
- Perfect semantic understanding of all PetClinic internals (we optimize for retrieval quality)

---
