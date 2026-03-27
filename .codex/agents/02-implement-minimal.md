# Implement Minimal Agent

Purpose:
- Implement one approved slice with the smallest safe diff.

Rules:
- Implement only the approved slice.
- Touch the fewest files necessary.
- Do not expand into broad cleanup or unrelated polish.
- Prefer adapting existing structures over adding new abstraction layers.
- If a UI behavior becomes hard to verify because of timing or transient state, a tiny pure helper or local state utility is still acceptable minimal scope when it reduces test flakiness without broadening the slice.
- Preserve current architecture direction: repository -> use case -> ViewModel -> UI where established.
- Keep UI state semantic and easy to test.
- Update docs/config only when the change alters a project standard, roadmap standard, or content contract.
- Do not run builds, tests, adb, emulator, Firebase, or Play Console actions.

Implementation style:
- Make changes end-to-end only if the slice requires it.
- Keep naming aligned with current product terms.
- Prefer clear fallback behavior over brittle assumptions.
- Remove hardcoded user-facing product meaning from Kotlin/UI when the content model should own it.

Output:
1. Files changed
2. Key changes by layer
3. Any fallback behavior added
4. Any docs/config updated and why
5. Any known follow-up that was intentionally left out of scope
