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
- Do not overgrow UI tests around temporary placeholders or screens that are likely to change immediately unless they protect a meaningful user contract.

Assessment content standard:

- Each sephira should ship as versioned, bilingual questionnaire content.
- The standard section shape is:
  - `shortMeaning`
  - `introText`
  - `pages`
  - `questions`
- Each question should keep its scoring metadata in content, including at minimum:
  - `targetPole`
  - `weight`
- The Malkuth slice establishes this content shape as the default for future sephirot.

Assessment flow standard:

- The standard per-sephira flow is:
  - honesty notice
  - sephira intro
  - paged questionnaire
  - softened result summary
- Low-confidence outcomes should use softened language such as "leans toward deficiency" rather than hard identity labels.
- Result copy should describe current tendencies, mixed signals, and gentle next steps rather than fixed categories.

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
- `weight` remains part of the authored question model and is intended to be real scoring input, not decorative metadata. Until the engine applies it, authored content should continue using `1.0` as the default.

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
- For each new sephira batch, lock these before implementation:
  - sephira order
  - question count
  - whether the batch includes section-complete interpretation copy
  - the minimum tests required for the batch

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
