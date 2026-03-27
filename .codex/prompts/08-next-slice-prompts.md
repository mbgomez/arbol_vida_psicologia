# Next Slice Prompt Templates

Use these after a slice is complete and the current docs have been updated.

## Inspect Prompt
Continue from the current workspace state for Numyah Mind.

Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/01-inspect-slice.md`
- `.codex/agents/08-next-slice-prompts.md`
- `docs/assessment_task_status.toml`

Task:
Inspect the current repo state and propose the best one bounded next slice for the current roadmap phase.
Do not implement anything yet.
Do not broaden scope beyond one realistic thread.
Do not suggest work that conflicts with the currently locked standards.

Output:
1. Recommended next slice
2. Why this is the best next chunk now
3. Files likely in scope
4. Risks or dependencies
5. What should stay out of scope

## Implement Prompt
Continue from the current workspace state for Numyah Mind.

Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/02-implement-minimal.md`
- `.codex/agents/05-tests-focused.md`
- `docs/assessment_task_status.toml`

Optional if relevant:
- `.codex/agents/03-content-contract.md`
- `.codex/agents/04-ui-state-boundary.md`

Task:
Implement this one approved next slice only:
[PASTE APPROVED SLICE]

Important workflow note:
- I will run all Gradle builds, unit tests, instrumented tests, emulator checks, adb commands, Firebase checks, and Play Console steps myself.
- Do not run builds or tests for me.

Output:
1. Files changed
2. Key changes
3. Tests updated
4. Exact commands and manual checks for me to run

## Tests Prompt
Continue from the current workspace state for Numyah Mind.

Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/05-tests-focused.md`

Task:
Update only the tests required for this changed behavior:
[PASTE CHANGED BEHAVIOR]

Do not run tests.

## Verify Prompt
Continue from the current workspace state for Numyah Mind.

Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/06-verify-manual.md`

Task:
Give me the exact Gradle commands, targeted test commands, emulator checks, and manual verification steps for this slice:
[PASTE SLICE]

## Capture Learnings Prompt
Continue from the current workspace state for Numyah Mind.

Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/07-capture-learnings.md`
- `docs/assessment_task_status.toml`

Task:
Based on this verified slice:
[PASTE VERIFIED SLICE]

Capture the durable learnings, suggest any minimal config/doc update, and generate a copy-paste-ready commit message.
