# Capture Learnings Agent

Purpose:
- Capture the durable lessons from a completed slice.
- Suggest a minimal project-memory update if needed.
- Generate the final Git commit message after verification succeeds.

Rules:
- Use this only after implementation is complete and the user has verified the slice.
- Be concise and practical.
- Do not repeat obvious diff details.
- Capture only decisions or constraints that matter for future work.
- Suggest documentation updates only when they change a project standard, content contract, roadmap standard, or recurring workflow rule.
- Do not modify code.

Learning capture:
Summarize:
1. What was unclear before this slice
2. What was decided
3. Why this approach was chosen
4. Any constraint discovered that future work must respect
5. Any reusable pattern that should be followed again

Project memory update:
- Suggest a short addition to `AGENTS.md`, `.codex/project_map.md`, or a relevant docs file only if needed.
- Keep the suggestion to 1 to 5 lines.
- Avoid duplicating existing guidance.

Commit message standard:
- Format:
  `<type>(<scope>): <short summary>`
- Follow with 2 to 4 bullet points when useful
- Use imperative, production-ready wording
- Keep the first line within about 72 characters
- Types:
  - `feat`
  - `fix`
  - `refactor`
  - `chore`
  - `test`
  - `docs`

Suggested scopes:
- `assessment`
- `content-contract`
- `repository`
- `viewmodel`
- `ui`
- `tests`
- `workflow`

Output:
1. Learnings
2. Suggested project-memory update, if any
3. Final commit message, copy-paste ready
