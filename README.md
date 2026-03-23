# Arbol Vida Psicologia

Android app concept for exploring the Kabbalah Tree of Life through a psychological self-reflection journey.

Canonical product name: `Numyah Mind`.

This repository currently contains a single Android app module built with Kotlin, Jetpack Compose, Hilt, Room, Retrofit, and Navigation. The existing codebase looks like a starter foundation, so the first product step is to reshape it around a questionnaire-driven assessment for the ten sephirot.

The app should support both English and Spanish.

## Product Direction

The app begins with a guided assessment. For each sephira, the user answers a short set of questions and receives one of three states:

- balanced
- deficiency
- excess

The result is a Tree of Life profile that explains each sephira in psychological terms and offers grounded reflection practices.

The product content should be based on the project's own Kabbalah source document:

- [Tree of life overview](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\Tree%20of%20life%20-%20overview%20-%20psychology.docx)

That content should not all live in onboarding. The app should use it in layers:

- dedicated onboarding for trust, orientation, privacy expectations, and clear user framing
- short sephira intros before each questionnaire section
- richer per-sephira explanations on the result detail screens
- optional deeper reading in a Learn/About area

Onboarding should be more substantial than a splash screen, but it should still stay lighter and more practical than the deeper educational material in Learn/About.

All onboarding copy should be written from the user's perspective. It should explain what the user is about to experience, what kind of reflection the app supports, and what the results mean. It should not sound like roadmap text, implementation notes, or builder-facing commentary.

All user-facing content should be planned for English and Spanish from the beginning.

## Spec

The full product plan is documented here:

- [Product spec](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\product_spec.md)
- [Production readiness roadmap](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\production_readiness_roadmap.md)
- [Tester distribution and observability](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\tester_distribution_and_observability.md)

## Scope For First Build

- onboarding and framing
- questionnaire by sephira
- per-sephira educational intro copy
- local scoring engine
- results overview across all ten sephirot
- per-sephira detail view
- optional learn/about content area
- saved local assessment history
- retake assessment flow
- settings for language, appearance, onboarding replay, and assessment preferences

## Architectural Direction

Keep the repo as a single `:app` module for now, and standardize UI code under the existing `ui` package tree so the project has one consistent visual structure:

- `ui/components`
- `ui/nav`
- `ui/screen`
- `ui/theme`
- `viewmodel`

Non-UI code should continue to live in supporting layers such as `app`, `data`, `domain`, `di`, and related technical packages.

This preserves the current stack while keeping the active UI implementation aligned to one clear schema instead of splitting visual code across multiple competing package conventions.

State and use case standard:

- Repository interfaces should expose `Flow` when the surrounding feature already follows the repository -> use case -> ViewModel flow pattern.
- Use cases should stay thin and delegate to repositories, following the same style used in the posts example.
- ViewModels should collect use case flows in `viewModelScope` rather than reaching into repositories directly when that architectural path already exists for the feature area.

Testing standard:

- When a feature follows the repository -> use case -> ViewModel pattern, add tests at all three levels when the feature is user-critical.
- Repository tests should verify persistence contracts and emitted flow values.
- Use case tests should verify delegation and emitted values.
- ViewModel tests should verify state mapping, progression rules, and completion behavior.
- Compose UI tests should focus first on stable, user-facing flows that define the current slice of work.
- Scoring tests should avoid sitting exactly on confidence or dominant-pole thresholds unless the goal is to verify threshold behavior itself.
- Do not overgrow UI tests around temporary placeholders or screens that are likely to change immediately unless they protect a meaningful user contract.
- Local builds, Gradle test runs, emulator checks, and manual verification are run by the user. Implementation work should include clear run instructions and focused checks for the user to execute.
- For long scrollable screens, prefer Compose assertions that explicitly scroll to the target content or use stable test tags so layout growth does not create false failures.
- If connected Compose tests fail broadly with `No compose hierarchies found in the app`, check the device or emulator state first. In this project that failure can come from Android Developer Options such as `Don't keep activities` being enabled.

Assessment content standard:

- Each sephira should ship as versioned, bilingual questionnaire content.
- The standard section shape is:
  - `shortMeaning`
  - `introText`
  - `detailContent`
  - `pages`
  - `questions`
- `detailContent` should keep:
  - `healthyExpression`
  - `deficiencyPattern`
  - `excessPattern`
  - `suggestedPractices`
- Each question should keep its scoring metadata in content, including at minimum:
  - `targetPole`
  - `weight`
- The Malkuth slice establishes this content shape as the default for future sephirot.

Learn content standard:

- Learn/About content should also stay local-first and bilingual in v1.
- Learn/About should preserve the authored Kabbalistic teaching voice much more closely than assessment content.
- Learn implementation phases for the current course slice are:
  - Phase 1: lock Kabbalistic voice and authored content direction
  - Phase 2: shape the section reader into a book-like reading experience
  - Phase 3: close missing tests and capture Learn-specific learnings and polish from the finished reader
- The default learning seed shape is:
  - `version`
  - `courses`
  - per-course `sections`
- Each course should keep:
  - a stable `id`
  - localized title, subtitle, and description
  - estimated reading time
  - total planned section count
  - currently available sections
- Each section should keep:
  - a stable `id`
  - localized title and summary
  - reading time
  - order
  - localized body paragraphs
- Learn text may receive light cleanup for readability and mobile structure, but it should not be rewritten into the softer psychological product voice used by onboarding and assessment flows.
- The first seeded course should follow `docs/Tree of life - overview - psychology.docx` closely, using one introduction section plus one section per sephira, while allowing unfinished sections to be added gradually later without changing the reading flow.

Assessment flow standard:

- The standard per-sephira flow is:
  - honesty notice
  - sephira intro
  - paged questionnaire
  - softened result summary
- Low-confidence outcomes should use softened language such as "leans toward deficiency" rather than hard identity labels.
- Result copy should describe current tendencies, mixed signals, and gentle next steps rather than fixed categories.

Locked history standard:

- The History tab is a production surface for completed assessments, not a placeholder destination.
- History cards should show completed-session context at a glance:
  - completion date
  - session progress across seeded sephirot
  - the most tense saved sephira
  - the most settled saved sephira
- Opening a history item should route to the saved results overview for that specific completed session rather than always redirecting to the latest result.
- History is also the planned surface for future trend graphs across saved assessments.
- The first locked History trend metrics are:
  - highest tension by saved session, using the session's top imbalance score
  - most settled by saved session, using the session's lowest-imbalance strongest-balance score
- Trend UI should stay as a calm extension above the saved-session list, not a dashboard replacement for the list itself.
- The lightweight trend section inside History is the entry point into trend exploration.
- Deeper trend exploration should open from History on a dedicated secondary screen instead of replacing the saved-session list.
- The first deeper-trends screen should support:
  - by sephira exploration, with balance, deficiency, and excess over time for one selected sephira
  - by score type exploration, with all sephirot over time for one selected score type
- In the score-type exploration mode, temporary per-sephira visibility filters should stay in the screen layer so graph interactions can remain fast without changing the underlying trend data contract.
- Debug/demo tools for History and related completed-result surfaces should be implemented as debug-only source switches. They must not overwrite or clear the user's real saved Room history.

Scoring standard:

- Scoring should remain deterministic and per-sephira.
- Persist both raw responses and derived pole scores.
- Keep confidence and low-confidence handling explicit so the UI can soften interpretation without hiding the underlying score state.
- Keep scoring logic isolated and unit-testable.

Current temporary decisions:

- The current Malkuth-specific ViewModel routing is an implementation shortcut, not the long-term feature standard.
- The current behavior where one sephira completion also completes the whole assessment session is temporary and must be replaced before scaling to all ten sephirot.
- The current threshold values used for dominant-pole classification are the v1 baseline, but should still be treated as tunable until validated across more than Malkuth.
- Placeholder or builder-facing copy outside the production assessment flow should not be copied forward as product voice.

Locked foundation decisions before scaling:

- Canonical spelling is `Malkuth` in user-facing copy and `MALKUTH` in code identifiers and content ids.
- One assessment session must span the full ten-sephirot assessment. Completing one sephira should save section progress and score data, but should not mark the whole assessment session complete.
- `weight` remains part of the authored question model and is real scoring input, not decorative metadata. Keep `1.0` as the default unless a sephira batch explicitly locks tuned weighting.

Learnings from the Malkuth slice:

- The reusable product unit is:
  - sephira intro
  - paged questions
  - scored interpretation
- Shared UI surfaces should stay generic. The active sephira name, intro meaning, and interpretive language should come from authored content rather than hardcoded shared strings.
- Content seeding for future sephirot should stay author-driven. The engine is generic, but questionnaire content should be added intentionally rather than inferred.
- Saved data should distinguish between:
  - per-sephira scores
  - the latest completed assessment
  - the eventual full Tree of Life overview
- Scrollable Compose screens should be tested against real semantics structure. Do not assume every visible phrase is exposed as its own standalone text node.
- The preferred engagement model is:
  - save a result after each sephira
  - show a short section-complete reflection
  - reserve the larger synthesis for the end of the assessment
  - let Home remain the fast entry point while `Assessments` becomes the dedicated library surface as the catalog expands
- For each new sephira batch, lock these before implementation:
  - sephira order
  - question count
  - whether the batch includes section-complete interpretation copy
  - the minimum tests required for the batch

Locked settings foundation:

- The first production settings slice should include only:
  - language
  - appearance
  - assessment experience
  - privacy and data
  - about
- Language options should be:
  - system default
  - English
  - Spanish
- Appearance options should be:
  - system default
  - light
  - dark
- The default behavior for both language and appearance should follow the device until the user chooses an override.
- Settings are local-only and should not depend on sign-in, sync, or backend state.
- Replaying onboarding should reset only the onboarding-completed preference. It should not erase assessment sessions, answers, scores, or history.
- The assessment honesty notice should remain user-controllable from Settings and should affect only that pre-assessment notice.
- Privacy and data content in Settings should explain the local-first model in clear user language and avoid technical or legalistic wording.
- About content in Settings should reinforce the reflection-focused, non-diagnostic framing and can include app version information.
- Theme and language preferences should be treated as app-wide settings and should apply across the full shell, not only inside the Settings screen.

Learnings from the Settings slice:

- App-wide preferences have now crossed the threshold where `DataStore` is the default standard for settings persistence. `SharedPreferences` should not be expanded further for new app settings.
- Runtime language override should be treated as an app-shell concern. In this app, locale changes rely on the AppCompat activity host plus app-wide preference observation rather than screen-local refresh logic.
- Nested Settings destinations such as Privacy and About should use a detail-screen header pattern that is distinct from the top-level app shell. The shell header state should transition in sync with route changes to avoid visible header lag during navigation.
- Settings entry cards should communicate one clear destination action. Avoid repeating the same CTA label in multiple places inside the same card.
- Scrollable Compose screens with polished layouts can invalidate brittle UI tests quickly. For settings-style screens, prefer explicit stable semantics and focused component tests over relying only on large screen-level interaction tests.
- When a settings feature uses repository -> use case -> ViewModel layering, the preferred verification stack remains:
  - repository persistence and flow tests
  - use case delegation tests
  - ViewModel state tests
  - focused Compose tests for stable user-critical controls
- Manual verification remains part of the release standard for language, theme, and nested Settings navigation even when automated tests exist, because those areas depend on app-shell behavior as well as local screen behavior.
- Debug-only product review tools in Settings should explain their purpose clearly and should prefer switching data sources over mutating persisted user data.

Production roadmap standard:

- Use `docs/production_readiness_roadmap.md` as the current phase guide toward a production-ready release.
- Keep the mainline release track moving even while deeper authored sephira content is still arriving.
- Treat finalized sephira-content enrichment as an ongoing side mission that can be added one sephira at a time during the main roadmap.
- The roadmap now includes a dedicated tester-distribution and observability phase before deeper polish and release hardening.
- Telemetry during that phase should stay minimal and product-relevant, focusing on crashes, key journey events, and tester learning rather than broad user tracking.
- The current tester-observability implementation is gated by Gradle flags and routes through an app-owned telemetry layer so Firebase can be enabled for tester builds without making observability a permanent always-on assumption.

Locked assessment entry direction:

- Home should clearly separate:
  - starting a new assessment
  - resuming an in-progress assessment
  - viewing the latest completed reflection
- `Assessments` is now a top-level product surface.
- Home should still keep direct actions for starting a new assessment and resuming the current one.
- Starting a new assessment while another one is still in progress should require explicit confirmation before replacing the unfinished session.
- Replacing an unfinished in-progress session should not erase completed saved reflections from History.
- The same replacement rule should apply consistently from `Home`, `Assessments`, and `Results` so retake behavior stays trustworthy across the app.
- `Assessments` should own the expanding library and future multi-assessment browsing, including future user-authored assessments when that capability enters scope.
- `History` remains the completed-results surface and is the planned home for graph-based trends across saved sessions.
- In the five-tab bottom shell, nav labels should remain single-line and readable on smaller phones. It is acceptable to use a shorter nav-only label resource while keeping the fuller screen title unchanged.
- Assessment-exit confirmation from the questionnaire should be destination-aware in both button text and body copy, and returning to Home should preserve the Home-first navigation path when possible.

## Delivery Standard

Future work in this repository should be approached with the judgment of:

- a senior UX designer
- a senior Android developer
- a senior project manager

That means implementation decisions should balance product clarity, user trust, visual quality, technical maintainability, and realistic scope. When tradeoffs appear, prefer the option that produces a cleaner user experience and a more durable foundation for the next feature slice.

Screen construction standard:

- Keep each screen composable as an orchestrator rather than a large monolithic block.
- Extract meaningful sub-composables for reusable or clearly separable sections such as headers, hero areas, body content, progress indicators, action areas, and background treatments.
- Keep screen content models or page definitions separate from layout when that improves readability.
- Prefer small, composable UI pieces that are easy to preview, reuse, and adjust without rewriting the full screen.
- When a screen has per-page or per-state media, prefer explicit page models that map content and artwork together instead of hardcoding visual branching inline.
- Keep resource ownership close to the composable that consumes it. If a spacing, size, or visual token is only used inside one extracted child composable, resolve it there instead of leaking parent-only values downward.
- When refactoring a large screen into smaller composables, move the dependent UI resources and layout rules with the extracted section so the parent remains an orchestrator and not a parameter hub.
- Group equal `dimen` values only when they represent the same design role. Shared spacing tokens are good; spacing, elevation, and shape values should stay separate even if the numbers match.

## Collaboration Protocol

To work effectively on future slices:

- Lock identity-level decisions early when possible, especially product name, package direction, user-facing tone, and scope boundaries.
- Separate each slice into three steps:
  - decisions we are locking
  - implementation pass
  - review and refinement
- Treat reversible experiments differently from project standards. When a choice becomes a standard, reflect it in code and docs in the same pass.
- Prefer reducing rework over maximizing speed when assumptions are still unstable.
- Use product judgment plus structured execution together: strong critique on user experience, followed by concrete implementation and verification.
- Assume the user owns local execution. Manual testing, Android Studio runs, and build or test commands should be requested from the user when verification is needed, rather than assumed to be run by the coding agent.

## Locked Refactor Standard

The repository now also follows a locked refactor standard:

- [Refactor roadmap](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\refactor_roadmap.md)

This roadmap defines the current project-standard direction for:

- naming cleanup
- package direction
- starter-template removal or quarantine
- Compose screen decomposition
- shared UI standardization
- design token standardization
- localization cleanup
- persistence hardening
- navigation and shell refactor order

Current timing notes:

- full Room hardening is intentionally deferred until first-release preparation for Google Play
- deeper content/version hardening is intentionally deferred until additional sephirot are ready to be added in later batches

Refactor lessons learned:

- remove or quarantine non-product starter-template residue early
- let real routes drive shell behavior instead of mirrored local navigation state
- extract shared UI from repeated product patterns, then reuse it consistently
- keep onboarding-specific tokens scoped to onboarding unless they truly become app-wide
- update docs in the same pass whenever a refactor decision becomes a standard
- rely on manual smoke testing to catch navigation and visual regressions during refactor-heavy passes
- treat bottom-nav label fit as a real UX requirement once the shell reaches five top-level tabs
- keep footer and supporting copy user-facing; avoid explaining product architecture or roadmap inside production cards

Refactor rule:

- when implementation changes one of those standards, update the implementation and the requirement files in the same pass
- do not let temporary cleanup patterns become permanent by accident
- prefer converging on one clear product architecture instead of preserving multiple competing patterns
