# Codex Prompt Cheat Sheet - Numyah Mind

Use the current human docs as the source of truth and the `.codex` files as the low-token execution layer.

## Universal Base Prompt
Continue from the current workspace state for Numyah Mind.

Important constraints:
- Do not run builds, tests, adb, emulator, Firebase, or Play Console actions
- I will run all verification myself
- Prefer minimal diffs
- Do not expand scope beyond the requested slice
- Do not modify unrelated production code
- Keep the current project direction and architecture intact
- Read `AGENTS.md`, `.codex/project_map.md`, and only the mini-agents needed for this task

## 1. Inspect Slice
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/01-inspect-slice.md`

Task:
Inspect only the minimum relevant docs and code needed for this request:
[PASTE TASK]

Do not implement anything yet.

Output:
1. One bounded slice only
2. Files in scope
3. Why this slice
4. Risks
5. What is explicitly out of scope

## 2. Implement Minimal
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/02-implement-minimal.md`

Optional:
- `.codex/agents/03-content-contract.md`
- `.codex/agents/04-ui-state-boundary.md`
- `.codex/agents/05-tests-focused.md`

Task:
Implement only this approved slice:
[PASTE SLICE]

Constraints:
- Minimal file changes
- No unrelated cleanup
- No broad refactors

Output:
1. Files changed
2. Key changes
3. Fallback behavior
4. Docs/config updated and why

## 3. Content Contract
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/03-content-contract.md`

Task:
Define or refine the content contract for:
[PASTE TASK]

Requirements:
- Assessment-agnostic
- Endpoint-friendly
- No UI hardcoding

Output:
1. Contract definition
2. Mapping flow
3. Fallback behavior

## 4. UI State Boundary
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/04-ui-state-boundary.md`

Task:
Ensure UI remains state-driven for:
[PASTE TASK]

Output:
1. UI files changed
2. State usage
3. Boundary fixes

## 5. Tests Focused
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/05-tests-focused.md`

Task:
Update tests only for:
[PASTE TASK]

Output:
1. Test files changed
2. Scenarios covered

## 6. Verify Manual
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/06-verify-manual.md`

Task:
Provide verification steps for:
[PASTE TASK]

Output:
1. Gradle commands
2. Unit tests
3. Instrumented tests
4. Manual checks

## 7. Capture Learnings
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/07-capture-learnings.md`

Task:
Capture learnings and generate commit message for:
[PASTE TASK]

Output:
1. Learnings
2. Suggested doc update
3. Commit message

## 8. Generate The Next Slice
Read:
- `AGENTS.md`
- `.codex/project_map.md`
- `.codex/agents/08-next-slice-prompts.md`

Task:
Based on the current workspace state and phase, propose the next best bounded slice and generate exact follow-up prompts.

Output:
1. Recommended next slice
2. Why now
3. Files likely in scope
4. Copy-paste prompts
5. Risks or dependencies

## Recommended Flows

### Content-contract slice
Inspect -> Content contract -> Implement -> Tests -> Verify -> Learnings

### UI or flow slice
Inspect -> UI state boundary -> Implement -> Tests -> Verify -> Learnings

### Refactor slice
Inspect -> Implement -> Tests -> Verify -> Learnings

### Planning-only next slice
Inspect -> Next slice prompts
