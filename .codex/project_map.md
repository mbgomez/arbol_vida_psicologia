# Numyah Mind Codex Project Map

Purpose:
- Give Codex a cheap, stable orientation layer before task-specific work.
- Reduce repeated scanning across README, roadmap, and product docs.
- Point Codex at the right sources of truth for the current slice.

Canonical product name:
- `Numyah Mind`

Product framing:
- Self-reflection and personal growth app grounded in the Kabbalah Tree of Life.
- Not a diagnostic, psychiatric, or clinical mental health product.
- Language must stay psychologically grounded, spiritually respectful, and non-pathologizing.
- Results describe current tendencies, not fixed identity.
- English and Spanish are first-class product languages.

Primary human-facing sources of truth:
1. `AGENTS.md`
2. `docs/product_spec.md`
3. `README.md`
4. `docs/refactor_roadmap.md`
5. `docs/production_readiness_roadmap.md`
6. `docs/assessment_task_status.toml`

Current workflow phase:
- `Phase 0` is the Codex workflow foundation:
  - keep prompts bounded
  - read only the minimum relevant files
  - use task-specific mini-agents instead of loading every planning doc every time
  - keep the human docs as the source of truth

Current roadmap state:
- The bounded Phase 1 return slice for section-complete `completionContent` is implemented.
- The top-level Home and Assessments state-surface polish slice is now implemented and manually verified.
- The bounded Settings polish slice is now implemented and verified, including calmer bilingual readability tuning and a hidden debug-tools reveal path that keeps the default Settings surface cleaner.
- The final bounded Phase 2 assessment-flow polish slice is now implemented and manually verified.
- The bounded post-Phase-2 trust/readability return pass is now implemented and manually verified, covering the startup legal note, the assessment-exit opt-out, and the final top-level nav readability fix.
- `Phase 2: UX And Enjoyment Polish` is now complete.
- The project is now in `Phase 3: Refactor And Architecture Hardening`.
- The bounded information-balance correction for section-complete and final Results surfaces is now implemented.
- The remaining Phase 3 work is intentionally grouped into three bounded clusters: Results, Assessment state, and Shell/navigation.
- The user runs all builds, tests, emulator checks, adb commands, Firebase checks, and Play Console steps manually.

Current app direction:
- Single `:app` module
- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Hilt
- Room
- Repository -> use case -> ViewModel pattern where established
- Prefer `StateFlow`-driven UI state

Active package direction:
- `app`
- `data/local`
- `data/repository`
- `domain`
- `ui/components`
- `ui/nav`
- `ui/screen`
- `ui/theme`
- `viewmodel`

Assessment content contract direction:
- The standard section shape is:
  - `shortMeaning`
  - `introText`
  - `completionContent`
  - `detailContent`
  - `pages`
  - `questions`
- `completionContent` is authored content, not UI-owned copy.
- Section-complete interpretation should stay assessment-agnostic and endpoint-friendly.
- Mapping should flow through repository/domain/ViewModel, not through hardcoded screen logic.
- Incomplete seeded content must have safe fallback behavior.
- Assessment resume behavior depends on repository snapshots preserving saved sephira scores; a refactor that restores section-complete UI state is incomplete if the persistence path drops those scores during resume.
- Reflective output layering is now locked more clearly:
  - end-of-sephira assessment result = rewarding local pause with tendency, context, score support, and one next practice
  - final Results overview = richer whole-tree orientation surface
  - Sephira detail = deepest interpretation surface
- For section-complete meaning surfaces, reuse authored per-sephira `detailContent` through state mapping instead of reviving generic screen-level interpretation strings.
- Remaining Phase 3 execution map:
  - Results cluster = Results overview readability, ResultsScreen decomposition, Results/detail boundary polish, related Compose tests
  - Assessment state cluster = remaining AssessmentViewModel thinning, UI-state/helper cleanup, related unit tests
  - Shell/navigation cluster = MainNav and route-driven shell cleanup, focused navigation verification

Codex operating rules:
- Read only the files needed for the current slice.
- Avoid broad repo scans unless the task explicitly requires discovery.
- Prefer one bounded slice over broad multi-phase work.
- Prefer minimal diffs over new abstractions.
- Do not drift into later polish or refactor work unless directly required by the slice.
- Update docs/config only when a project standard, roadmap standard, or content contract changes.
- If a settings surface offers both a visibility toggle and a manual reopen action for the same note or dialog, keep them as separate controls and test both paths independently.

Mini-agent routing:
- `01-inspect-slice.md` for exploration and scope selection
- `02-implement-minimal.md` for bounded implementation
- `03-content-contract.md` when changing seeded content or interpretation contracts
- `04-ui-state-boundary.md` when UI/domain boundaries are at risk
- `05-tests-focused.md` when behavior changes require test updates
- `06-verify-manual.md` for final run instructions and manual checks
- `07-capture-learnings.md` only after user verification succeeds
- `08-next-slice-prompts.md` for generating the next bounded follow-up prompts

When starting a new thread:
1. Read `AGENTS.md`
2. Read this file
3. Read only the mini-agents needed for the task
4. Read only the minimum relevant code and docs

Do not treat starter-template residue as product architecture.
Do not let legacy placeholder concepts shape new product work.
