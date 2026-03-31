Project: Arbol Vida Psicologia

Purpose:
Build an Android app in Kotlin that applies the Kabbalah Tree of Life to psychological self-reflection through a guided questionnaire and locally generated profile.

Primary source of truth:
- Product spec: `docs/product_spec.md`
- README summary: `README.md`
- Content source document: `docs/Tree of life - overview - psychology.docx`
- Production roadmap: `docs/production_readiness_roadmap.md`

Product framing:
- This is a self-reflection and personal growth app.
- It is not a diagnostic, psychiatric, or clinical mental health product.
- Language should be psychologically grounded, spiritually respectful, and non-pathologizing.
- Results describe current tendencies, not fixed identity.
- The app should support both English and Spanish.
- Canonical product name is `Numyah Mind`.

Core product loop:
1. User reads a short onboarding that explains the framework and privacy expectations.
2. User reads a short intro for the current sephira.
3. User completes a questionnaire for each of the ten sephirot.
4. The app scores each sephira into one of three states:
   - balanced
   - deficiency
   - excess
5. The app shows a full Tree of Life profile.
6. The user can open each sephira for interpretation and suggested practices.
7. The user can optionally explore deeper educational material in a Learn/About section.
8. The user can save progress locally and retake the assessment later.

Initial sephirot:
- Malkhut
- Yesod
- Hod
- Netzach
- Tiferet
- Gevurah
- Chesed
- Binah
- Chokhmah
- Keter

V1 scope:
- onboarding
- assessment intro
- sephira intro content
- questionnaire flow
- local progress saving
- local scoring engine
- results overview
- per-sephira detail screen
- optional learn/about content section
- assessment history
- retake assessment

Non-goals for v1:
- diagnosis or treatment claims
- backend dependency for the main assessment flow
- mandatory sign-in
- social features
- overengineered module splitting

Questionnaire guidance:
- Start with 10 sephirot.
- Target 6 to 10 questions per sephira in v1.
- Prefer a 5-point Likert scale for most questions.
- Questions should map to one or more poles:
  - balance
  - deficiency
  - excess
- Question wording should be concrete and behavior-oriented.
- Avoid vague spiritual abstraction without psychological meaning.

Content guidance:
- The Word document in `docs/Tree of life - overview - psychology.docx` is the main content source for educational copy.
- Do not put most of that document into onboarding.
- Keep onboarding focused on trust, framing, privacy expectations, and what the user will experience.
- Onboarding can be fuller than a simple splash screen, but it should remain lighter and more practical than the deeper Learn/About material.
- Use short sephira-specific intro copy before each questionnaire section.
- Use fuller adapted content on each sephira result detail screen.
- Put broader and denser conceptual material in an optional Learn/About area.
- Adapt the source document into app-friendly English for onboarding, assessment intros, and other product-facing flows.
- For Learn/About courses, preserve the authored Kabbalistic teaching voice much more closely, using only light cleanup for readability, sectioning, and mobile presentation unless a different Learn standard is explicitly locked.
- When in doubt, prefer clear psychological phrasing first and Kabbalah terminology second.
- Onboarding and other user-facing flows must be written from the user's perspective, not from a builder, roadmap, or implementation perspective.

Scoring guidance:
- Use deterministic scoring, not AI-generated interpretation.
- Score each sephira independently.
- Store raw responses and derived scores.
- Support low-confidence or mixed outcomes internally, even if the UI presents a softened dominant tendency.
- Treat authored question `weight` as real scoring input. Keep `1.0` as the default unless a sephira batch explicitly locks tuned weighting.
- Keep scoring logic isolated and unit-testable.

Architecture rules:
- Kotlin
- Jetpack Compose UI
- Material 3
- MVVM
- Hilt for dependency injection
- Room for local persistence
- Repository pattern
- Prefer `StateFlow`-based UI state for new screens
- Keep code modular and testable
- Prefer simple, production-ready architecture over overengineering

Repository direction:
- Keep a single `:app` module for now.
- Standardize active UI code under the existing `ui` tree.
- Recommended package direction:
  - `ui/components`
  - `ui/nav`
  - `ui/screen`
  - `ui/theme`
  - `viewmodel`
  - `data/local`
  - `data/repository`
  - `app`
  - `domain`

Current repo note:
- Existing starter-template code such as posts, generic detail screens, and login scaffolding should not define the product architecture.
- Reuse the technical foundation where useful, but prioritize the Tree of Life assessment flow over preserving template concepts.
- Do not split active UI across competing schemas like `feature/*`, `core/designsystem`, and `ui/*` at the same time. Prefer one consistent UI home under `ui/*`.
- The top-level shell is now intended to grow to:
  - `Home`
  - `Assessments`
  - `History`
  - `Learn`
  - `Settings`

Data and content rules:
- The app should work offline for the core v1 experience.
- Questionnaire content should be versioned.
- Prefer local JSON or structured seed content for questions and sephira definitions in v1.
- The default seeded sephira shape should include:
  - `shortMeaning`
  - `introText`
  - `completionContent`
  - `detailContent`
  - `pages`
  - `questions`
- `completionContent` should be treated as part of the same authored content contract as intros and detail screens so the section-complete assessment reflection does not depend on screen-level or Malkuth-shaped fallback copy.
- `completionContent` should stay assessment-agnostic enough that future assessments or additional energy views for a sephira can reuse the same model instead of inventing UI-specific interpretation text.
- The locked `completionContent` shape is:
  - `sectionSummary`
  - `balanced`
    - `reflection`
    - `practice`
  - `deficiency`
    - `reflection`
    - `practice`
  - `excess`
    - `reflection`
    - `practice`
- Repository/domain/ViewModel layers should choose the active completion pole content before the screen renders it.
- Seed DTO fields that may legitimately be absent while content is still arriving little by little must stay backward-safe at parse time. Prefer optional/defaulted JSON seed fields for authored copy such as `completionContent`, its nested pole fields, and `suggestedPractices` so Moshi parsing does not fail before repository fallbacks can run.
- When the seeded assessment content contract changes, update fixture/test seed constructors in the same pass so local repository/session/scoring tests do not silently keep enforcing an older required shape.
- When questionnaire seed content or cached content schema changes, treat the corresponding content/database version bump plus manual fresh-install or clear-data verification as part of the same slice.
- `detailContent` should include:
  - `healthyExpression`
  - `deficiencyPattern`
  - `excessPattern`
  - `suggestedPractices`
- Prefer local JSON or structured seed content for Learn courses and section reading content in v1, using a shape that can later map cleanly to an endpoint.
- Persist assessment sessions, answers, and sephira results locally with Room.
- Plan content and UI strings for English and Spanish from the start.
- Keep localized copy separate from scoring rules and domain logic.
- When the architecture uses repositories plus use cases, prefer repository methods that return `Flow` and thin use cases that expose those flows to ViewModels.
- The remaining sephirot should be added little by little as their questions and definitions are finalized. Do not assume all remaining sephirot are ready for one bulk implementation pass.
- Learn course sections should also be allowed to arrive little by little as authored content is finalized. Do not assume the full course body is ready in one pass.
- Deeper questionnaire/content version hardening should be treated as a later expansion task when new sephirot batches are ready to enter implementation.
- Full Room hardening work such as schema export, explicit migrations, and migration tests should be treated as a release-preparation task for the first Google Play version rather than an immediate refactor requirement.
- Debug/demo data tools should prefer source switching over database mutation. If the app needs mock completed-history data for QA or product review, keep the real saved Room history untouched and switch eligible completed-history consumers to a debug-only mock source instead.

Testing priorities:
- scoring engine correctness
- questionnaire progression and resume behavior
- repository persistence
- ViewModel state mapping
- critical Compose screen flows
- When a feature uses repository -> use case -> ViewModel layering, add unit tests across that stack instead of testing only one layer.
- Repository tests should verify flow emissions and persistence side effects.
- Use case tests should verify delegation and returned flow values.
- ViewModel tests should verify state transitions, progression logic, and callback/completion behavior.
- Compose UI tests should start with the stable, user-critical screens in the current slice.
- For scoring-engine tests, prefer fixtures with a clear margin away from classification and confidence thresholds unless the test is explicitly about threshold behavior.
- Avoid spending test effort on placeholder screens unless they protect behavior we explicitly want to keep stable.
- For long scrollable Compose screens such as History and Settings, prefer assertions that scroll to the target content or rely on stable test tags rather than assuming later sections are immediately visible in the initial viewport.
- When connected Compose tests fail broadly with `No compose hierarchies found in the app`, first check the Android test-device state before treating it as a feature regression. In this project that failure can come from Developer Options such as `Don't keep activities`.

Locked history standard:
- The History tab is a production user surface for completed assessments, not a placeholder destination.
- Each saved history card should communicate:
  - completion date
  - saved sephira count for that session
  - the most tense saved sephira
  - the most settled saved sephira
- Opening a history item should route to that saved session's results overview rather than always defaulting to the latest completed result.
- History is also the future home for graph-based trend views across saved sessions. Graphs should extend the saved-results surface rather than replace the session list.
- The first locked trend metrics for History are:
  - highest tension by saved session, using the session's highest imbalance score
  - most settled by saved session, using the session's lowest-imbalance strongest-balance score
- Trend visuals in History should stay lightweight, calm, and chart-ready while preserving the saved-session list as the clearly primary surface.
- The lightweight trend section in History remains the entry point for trend exploration.
- Deeper graph exploration should open on a dedicated secondary screen from History rather than replacing the main History surface.
- The first dedicated deeper-trends screen should support two explicit exploration modes:
  - by sephira, showing balance, deficiency, and excess across saved completed sessions for one selected sephira
  - by score type, showing all sephirot across saved completed sessions for one selected score type
- Deeper trend state and models should stay chart-ready and stable so later visual upgrades do not require repository-contract churn.
- In the deeper `by score type` view, per-sephira visibility toggles are allowed as screen-local UI state so the graph can update immediately without changing repository or ViewModel contracts.
- Dynamic graph surfaces should expose stable test tags for chart controls and saved-point rows when visibility/filtering can change what is rendered.

UX guidance:
- Keep the assessment calm, clear, and mobile-friendly.
- Show progress through the ten sephirot.
- Favor soft interpretive language such as "leans toward deficiency" when confidence is low.
- Onboarding should feel like a dedicated first-run experience, not a reused standard app shell screen.
- Onboarding should look polished and intentional, with stable layout, strong hierarchy, and professional visual treatment.
- Richer explanation should appear in-context before each sephira and on result detail screens.
- Explanations should include:
  - healthy expression
  - deficiency pattern
  - excess pattern
  - suggested practices
- Home should clearly distinguish between:
  - starting a new assessment
  - resuming an in-progress assessment
  - viewing the latest completed reflection
- Starting a new assessment while another one is still in progress should require explicit confirmation before the unfinished session is replaced.
- Replacing an unfinished in-progress session should not erase completed saved reflections from History.
- Results and Assessments should follow the same fresh-start rule as Home so the user never encounters inconsistent replacement behavior across entry surfaces.
- `Assessments` is now a justified top-level tab because the product intends to grow into a multi-assessment surface, including future user-authored assessments.
- Home should keep the current flow of starting or resuming an assessment, while `Assessments` becomes the dedicated browsing surface for the expanding assessment library.
- In a five-tab bottom navigation shell, tab labels should stay readable on narrower phones. Prefer adaptive single-line label sizing or a dedicated shorter nav label resource over awkward wrapping.
- The bottom-nav label may be shorter than the destination header/title when needed for fit, but the destination itself should keep the fuller product-facing name.
- Assessment exit confirmation should be destination-aware. The dialog body and confirm action should name the actual tab being opened, not always Home.
- Leaving an in-progress assessment for Home should preserve the fast-entry route behavior. Prefer popping back to an existing Home destination when possible before falling back to generic top-level navigation.
- When a product concept offers both a persisted visibility toggle and a manual reopen/view action, keep them as clearly separate controls. Toggling visibility must not also trigger the reopen action.
- For top-level tab navigation, prefer popping back to an existing destination before using generic save/restore navigation so deep-stack re-entry does not bounce through the wrong tab.
- Footer/supporting copy on production surfaces such as Assessments, Learn, and History should speak to the user's experience, not to internal product structure or developer intent.

Compose screen construction guidance:
- Keep each screen file centered on orchestration and state mapping, not one large block of inline UI.
- Extract smaller composables for clearly distinct sections such as headers, hero areas, content blocks, progress indicators, and action areas.
- Prefer reusable UI sections when the same visual pattern may appear in more than one screen.
- Decompose screens enough to improve readability and maintenance, but avoid creating meaningless wrapper composables.
- For onboarding or other multi-state screens, keep page/state definitions explicit so images, copy, and behavior stay aligned in one place.
- Resolve dimension and string resources as close as practical to the composable that consumes them. Avoid keeping child-only resource tokens in the parent screen scope after extraction.
- When extracting UI sections, move their layout tokens and related visual rules with them so refactors reduce scope leakage instead of creating parameter churn.
- Group `dimen` resources when they share the same semantic role, but do not collapse spacing, elevation, and shape into one token just because the raw value matches.

When making implementation decisions:
- Prefer choices that support clear interpretation, local reliability, and testability.
- If the codebase and the spec conflict, follow `docs/product_spec.md`.
- Apply the judgment of a senior UX designer, a senior Android developer, and a senior project manager. Balance user trust, visual quality, maintainable implementation, and realistic scope.

Working-together protocol:
- Treat identity-level decisions such as naming, package direction, tone, and scope boundaries as lock-first decisions whenever possible.
- Treat `Phase 0` as the workflow-foundation phase for Codex itself: keep the human docs as the source of truth, keep task prompts bounded, and use lightweight task-specific mini-agents instead of repeatedly loading the full planning stack when that extra context is not needed.
- Structure each feature slice in three phases:
  - locked decisions
  - implementation
  - review and refinement
- For the current Learn slice, treat phase 3 as the pass for missing tests plus Learn-specific learnings and refinement standards after the reader experience is in place.
- Be explicit about whether a choice is a temporary experiment or a new project standard.
- When a standard changes, update both the implementation and the requirement files in the same pass.
- Prefer reducing assumption churn over rushing a larger implementation on unstable foundations.
- Follow `docs/production_readiness_roadmap.md` for the current phase order toward a production-ready release.
- Use `.codex/project_map.md` as the low-token execution map for future Codex threads after reading this file.
- Read only the task-specific mini-agent files under `.codex/agents/` that are relevant to the current request. Do not load every mini-agent on every thread.
- Prefer one bounded slice per thread. Do not combine repo-wide discovery, implementation, broad roadmap redesign, and verification strategy changes in one prompt unless the task explicitly requires it.
- Do not read every config file by default in every thread. Load the smallest sufficient context for the task:
  - always start with `AGENTS.md` and `.codex/project_map.md`
  - add `docs/assessment_task_status.toml` for current phase and current-state guidance
  - add `docs/production_readiness_roadmap.md` when phase boundaries or release ordering matter
  - add `docs/product_spec.md` when product behavior, UX contract, or content rules are directly in play
  - add `docs/refactor_roadmap.md` when the slice may affect decomposition, shared UI, package direction, cleanup order, or other refactor standards
  - add `README.md` only when a lighter product summary or broader re-orientation is useful
- Do not read every config file by default in every thread. Load the smallest sufficient context for the task:
  - always start with `AGENTS.md` and `.codex/project_map.md`
  - add `docs/assessment_task_status.toml` for current phase and current-state guidance
  - add `docs/production_readiness_roadmap.md` when phase boundaries or release ordering matter
  - add `docs/product_spec.md` when product behavior, UX contract, or content rules are directly in play
  - add `docs/refactor_roadmap.md` when the slice may affect decomposition, shared UI, package direction, cleanup order, or other refactor standards
  - add `README.md` only when a lighter product summary or broader re-orientation is useful
- Treat finalized sephira-content enrichment as an approved side mission that can be added one sephira at a time during the main roadmap, unless a thread explicitly locks a feature that depends on all related sephirot being finalized first.
- If the roadmap introduces a tester-distribution and observability phase, keep crash reporting and analytics minimal, product-relevant, and aligned with the app's local-first trust model.
- During tester-distribution work, keep observability behind an app-owned interface and explicit build-time enablement so Firebase or Play testing setup does not become a hidden always-on dependency of normal local work.
- Privacy-facing copy for tester builds must stay explicit that telemetry is limited to crashes, recoverable failures, and a small approved flow taxonomy, and must not include answer content, interpretation copy, or saved score details.

Codex workflow layer:
- The mini-agents in `.codex/agents/` are prompt guides, not replacements for this file and not a separate product source of truth.
- Default task routing:
  - `.codex/agents/01-inspect-slice.md` for bounded discovery
  - `.codex/agents/02-implement-minimal.md` for implementation
  - `.codex/agents/03-content-contract.md` for authored content/model changes
  - `.codex/agents/04-ui-state-boundary.md` for keeping UI state-driven
  - `.codex/agents/05-tests-focused.md` for targeted test updates
  - `.codex/agents/06-verify-manual.md` for exact user-run verification steps
  - `.codex/agents/07-capture-learnings.md` for durable learnings and final commit copy after verification
  - `.codex/agents/08-next-slice-prompts.md` for generating the next bounded follow-up prompts
- Prefer `.codex/prompts/codex_prompt_cheatsheet.md` and `.codex/prompts/08-next-slice-prompts.md` as reusable prompt helpers instead of rewriting large prompt blocks from scratch each thread.
- Keep prompt context cheap:
  - read this file first
  - then read `.codex/project_map.md`
  - then read only the mini-agents needed for the task
  - then read only the minimum relevant code and docs
- Prefer task-driven doc selection over habitual full-doc loading. More context is not automatically better if the same constraints are already locked in a cheaper source.
- Prefer task-driven doc selection over habitual full-doc loading. More context is not automatically better if the same constraints are already locked in a cheaper source.
- The user still owns all Gradle, test, emulator, adb, Firebase, and Play Console execution. End implementation work with exact commands and focused manual checks.

Locked refactor direction:
- Follow `docs/refactor_roadmap.md` as the source of truth for refactor standards and cleanup order.
- Do not let starter-template code such as posts, login scaffolding, generic template screens, or `Foundation` naming define new product work.
- Prefer converging on one package and UI pattern over preserving multiple competing approaches.
- Treat shared UI patterns, semantic design tokens, Room hardening, and bilingual cleanup as project standards rather than optional polish.
- Before adding complexity to large files such as assessment, settings, results, or navigation shell code, prefer decomposition and standardization first.

Refactor lessons to preserve:
- Remove or quarantine non-product template residue early so it does not keep shaping new implementation.
- Prefer route-driven shell behavior over duplicated local navigation state.
- Extract shared UI only when it reflects real repeated product patterns, then make it the default path for new screens.
- Keep onboarding-specific visual tokens scoped to onboarding unless they are truly app-wide.
- Use documentation updates in the same pass whenever a refactor decision becomes a project standard.
- Treat manual smoke testing as part of refactor verification for navigation, shell, and visual regressions.
- Five-tab bottom bars need explicit fit decisions early. Letting long labels wrap produces low-quality navigation quickly, especially on smaller phones.
- Shell confirmation copy should be derived from the tapped destination so the dialog and resulting navigation stay trustworthy.
- Debug-only QA tools are safer when they switch the read source for completed-history surfaces instead of seeding or overwriting real saved history.
- Compose tests for polished, scrollable screens should be written to survive legitimate section growth without treating below-the-fold content as a regression.
- Broad `No compose hierarchies found` failures in androidTest can be caused by device settings like `Don't keep activities`, even when production code is fine. Check the test environment before rewriting app code.
- For dynamic graph screens, prefer filtering chart-ready UI models in the screen layer for temporary visibility controls rather than pushing transient chip-selection state into data or repository contracts.
- Replacing an unfinished assessment is a trust-sensitive action. Keep the confirmation copy explicit about what will be replaced and what will remain saved.
- Fresh-start behavior is stronger when it is implemented in the session/repository contract rather than as screen-local reset logic, so every entry surface shares the same rule.
- Section-complete assessment interpretation belongs to the authored content contract. If it is still expressed through shared screen strings or one sephira's copy pattern, treat that as a Phase 1 correction before layering more UX polish on top.
- Backward-safe seed DTO defaults are part of the content contract. If parse-time required fields can fail before repository fallbacks run, the fallback strategy is incomplete.
