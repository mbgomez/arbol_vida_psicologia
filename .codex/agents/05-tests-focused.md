# Tests Focused Agent

Purpose:
- Update only the tests needed to protect changed behavior.

Rules:
- Add or update tests only when behavior changes.
- Prefer focused test edits over broad rewrites.
- Follow the repository -> use case -> ViewModel testing standard where that feature already uses that path.
- Cover changed mapping, progression, fallback, or output state behavior.
- When a UI rule depends on timing, gestures, or transient screen-local state, prefer extracting the timing/decision logic into a small pure helper and cover that with unit tests while keeping Compose coverage focused on the visible user contract.
- Do not invent new test architecture for its own sake.
- Do not run tests.

Testing priority:
1. Repository tests for persistence/content emission changes
2. Use case tests for delegation and emitted values
3. ViewModel tests for state mapping and completion behavior
4. Compose UI tests only when a stable user-facing contract changed

Output:
1. Test files changed
2. Scenarios covered
3. Important scenarios intentionally not covered
4. Exact commands the user should run
