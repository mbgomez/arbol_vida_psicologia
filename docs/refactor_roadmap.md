# Refactor Roadmap

This document locks the current refactor direction for `Numyah Mind`.

For overall release-phase ordering, also follow [production_readiness_roadmap.md](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\production_readiness_roadmap.md).

It exists to keep future implementation work aligned with one product-grade standard instead of letting the codebase drift between:

- starter-template residue
- temporary experiments
- product standards

If implementation changes any standard in this document, update this file, `README.md`, `docs/product_spec.md`, and `AGENTS.md` in the same pass.

## Refactor Goal

Refactor the current single-module Android app into a cleaner, more professional, and more scalable production foundation without changing the v1 product scope.

The purpose of this refactor is to improve:

- product identity consistency
- code readability
- UI reuse
- token standardization
- persistence safety
- localization readiness
- future sephirot scaling
- testability

This is not a rewrite and not a modularization project.

## Locked Refactor Standards

### 1. Identity And Naming

- The canonical product name remains `Numyah Mind`.
- Product-facing code, resources, and themes should use `Numyah Mind` naming rather than legacy `Foundation` naming.
- Starter-template concepts such as posts, generic login scaffolding, and unrelated remote-feed architecture are not part of the product standard.
- If legacy template code is kept temporarily, it must be clearly marked as deprecated scaffolding and must not shape new feature architecture.

### 2. Package Direction

The repo remains a single `:app` module.

The active package direction is:

- `app`
- `data/local`
- `data/repository`
- `domain`
- `ui/components`
- `ui/nav`
- `ui/screen`
- `ui/theme`
- `viewmodel`

Additional package rules:

- New product UI should not be added under parallel schemas such as `feature/*`, `core/designsystem/*`, and `ui/*` at the same time.
- Room entities, DAOs, and local content sources should move toward `data/local/*` naming instead of broad `datasource` naming over time.
- Legacy `mapper` and `model` packages from the template should not grow unless they are explicitly kept as product standards.

### 3. Template Cleanup Standard

The following are considered cleanup candidates and should not drive future implementation:

- post feed repository and API stack
- generic login scaffolding
- template `MainScreen`
- template database naming
- placeholder or builder-facing strings in user-facing flows

Cleanup rule:

- Remove them if they are unused.
- Quarantine them if temporary retention is necessary.
- Do not extend them for new product work.

### 4. Compose Screen Standard

Each screen should act primarily as an orchestrator.

That means:

- keep route wiring and state mapping at the screen level
- extract meaningful sub-composables for hero, section, progress, content, and action areas
- avoid giant screen files that mix layout, copy selection, state branching, and repeated visual primitives
- keep resource lookup close to the composable that consumes it

Screen decomposition thresholds:

- any screen above roughly `300` to `350` lines should be reviewed for extraction
- any screen above `450` lines should be treated as an active refactor target

Current priority screen refactor targets:

- `AssessmentScreen`
- `AssessmentLibraryScreen`
- `HistoryScreen`
- `SettingsScreen`
- `ResultsScreen`
- `MainNav`

### 5. ViewModel Standard

ViewModels should expose semantic `StateFlow` UI state and keep UI logic testable.

Rules:

- ViewModels should not carry large amounts of display-label branching that can live in UI/resource mappers
- complex progression and branching logic should move into focused helpers or pure domain/UI-state mapper functions
- screen state should prefer explicit phases such as loading, intro, question, completed, and error

Current priority ViewModel refactor target:

- `AssessmentViewModel`

### 6. Shared UI Standard

Repeated UI patterns should become reusable components when they appear in two or more places or clearly represent a product-wide visual pattern.

Current standardization candidates:

- hero/header cards
- section cards
- detail headers
- progress bars
- score meters
- status badges
- selection rows
- empty and loading states
- saved-history cards

The goal is not abstraction for its own sake.

Only promote a shared component when it improves:

- consistency
- readability
- testability
- reuse across real screens

### 7. Design Token Standard

Dimension and visual tokens should be semantic and global when reused across the app.

Token rules:

- use semantic groups such as `spacing_*`, `radius_*`, `stroke_*`, `size_*`, `elevation_*`
- do not use `onboarding_*` names for app-wide tokens
- keep equal numeric values separate if they represent different semantic roles
- reduce hardcoded `dp` and `sp` values in production screens

Current priority token cleanup targets:

- `SettingsScreen`
- `HomeScreen`
- `MainNav`
- `ArbolVidaApp`

### 8. String And Copy Standard

All production-facing copy must sound user-facing and product-grade.

Do not ship strings that sound like:

- roadmap text
- builder notes
- internal placeholders
- technical implementation commentary

String rules:

- stable UI copy belongs in string resources
- hardcoded user-facing strings in Kotlin should be removed
- English and Spanish copy should remain semantically aligned
- footer and supporting copy on production surfaces should stay user-facing and should not explain internal architecture, staged rollout structure, or developer intent

Current copy cleanup targets:

- home shell copy that references placeholders
- settings copy that describes what the app "should" do instead of what it does
- placeholder titles and bodies presented in real navigation surfaces
- hardcoded sign-in label if auth remains in the repo

### 9. Content Standard

Questionnaire content remains local-first and versioned.

Rules:

- content should live in structured local content sources
- authored copy stays separate from scoring logic
- all ten sephirot must follow one shared content shape
- content validation should be easy to add as sephirot scale up

Locked delivery note:

- the remaining sephirot will be added little by little, not in one bulk implementation pass
- question writing and sephira-definition work are still active authoring tasks and should be treated as lock-first content work before each implementation batch
- implementation should scale sephirot in controlled slices that match the currently finalized content
- deeper content/version hardening should be deferred until the app is ready to add additional sephirot beyond the current finalized set

The current content shape remains:

- `version`
- `title`
- `responseScale`
- `sections`
- `shortMeaning`
- `introText`
- `detailContent`
- `pages`
- `questions`

`detailContent` should now travel with each seeded sephira section and include:

- `healthyExpression`
- `deficiencyPattern`
- `excessPattern`
- `suggestedPractices`

### 10. Scoring Standard

Scoring remains deterministic and isolated.

Locked scoring refactor rule:

- if `weight` remains in the authored model, the scoring engine should use it as real input

Until that refactor is complete:

- treat the current implementation as incomplete relative to the content contract
- do not introduce non-default weights in authored content without engine support

### 11. Persistence Standard

The app stores real user progress and should not rely on destructive migration as a long-term default.

Locked persistence standards:

- enable Room schema export
- add explicit migrations
- add migration tests before treating the persistence layer as hardened
- do not keep `fallbackToDestructiveMigration()` as the long-term user-data policy

Locked delivery timing:

- full Room hardening is intentionally deferred until the first production version is ready for Google Play submission
- until that release-preparation phase begins, refactor work may improve package structure and readability around persistence but should not expand into the full migration/schema hardening project

### 12. Dependency And Manifest Standard

Dependencies and permissions should reflect the actual v1 product.

Rules:

- remove or quarantine dependencies that only support deprecated starter-template flows
- keep the offline-first assessment flow free from unnecessary backend coupling
- prefer a leaner manifest and dependency graph when auth/network features are not in active scope

### 13. Localization Standard

Localization is part of the product foundation, not a later polish task.

Rules:

- all stable production strings must exist in English and Spanish
- Spanish copy should receive a product-quality pass, not only direct literal translation
- app-wide language switching should remain an app-shell concern
- locale support should align with Android app-language best practices

### 14. Testing Standard

Refactor work should make testing easier, not harder.

Refactor acceptance should improve:

- ViewModel tests for state transitions
- repository tests for persistence contracts
- migration and schema tests
- focused Compose tests on stable controls and components

Avoid:

- growing brittle end-to-end UI tests around unstable polished layouts
- leaving reused logic duplicated across screens without a single testable source of truth
- coupling future analytics or crash-reporting instrumentation too tightly to screen-local UI code when the same product event can be emitted from a more stable ViewModel or navigation boundary
- wiring Firebase or Play-testing observability directly into feature UI code when the same event can be emitted through an app-owned telemetry layer

Additional guidance:

- For long scrollable production screens, prefer stable semantics and explicit `performScrollTo()`-style assertions over viewport-dependent visibility checks.

### 15. Assessment Entry Standard

- The top-level shell is now intended to include `Home`, `Assessments`, `History`, `Learn`, and `Settings`.
- `Home` should keep fast actions for:
  - start new assessment
  - resume current assessment
  - open latest completed reflection
- Starting a new assessment while another one is in progress should require a confirmation dialog before the unfinished session is replaced.
- Replacing the unfinished session should leave completed saved-history entries untouched.
- The same fresh-start contract should be reused by `Home`, `Assessments`, and `Results` instead of each screen inventing separate replacement behavior.
- `Assessments` should own the browsing surface for the growing assessment library.
- The assessment library should be shaped to support both future first-party assessments and future user-authored assessments without reshaping the shell again.
- In the five-tab bottom shell, tab labels should remain single-line on smaller phones. Prefer adaptive label sizing or a shorter nav-specific label over wrapped text.
- The bottom-nav label may be shorter than the destination title when needed for fit, but the destination title itself should remain the fuller product-facing wording.
- Assessment-exit confirmation should be destination-aware in both the button label and supporting body text.
- Returning from an in-progress assessment to Home should prefer popping back to the existing Home surface when that route is already in the stack.

### 16. History Trend Standard

- `History` remains the saved-results surface for completed sessions.
- Future graph and trend work should live inside `History`, not in `Home` or `Assessments`.
- Trend views should extend the calm session-history experience rather than turning History into a dashboard-first screen.
- The first locked trend metrics are:
  - highest tension by session, based on the session's top imbalance score
  - most settled by session, based on the session's lowest-imbalance strongest-balance score
- New graph UI should consume chart-ready state models for those metrics so later visual refinement does not require changing repository contracts or replacing the saved-session list.
- The lightweight trend section inside `History` remains the entry point for trend exploration.
- Deeper trend exploration should open from `History` on a dedicated secondary screen instead of expanding the main `History` surface into a dashboard.
- Dedicated deeper-trends controls should use explicit modes and stable semantics/test tags so graph refinement can continue without brittle screen contracts.
- Temporary graph-filter selections such as per-sephira visibility chips in deeper trends should stay in screen-local state when they do not affect saved data or cross-screen behavior.
- Any QA/demo support for completed-history surfaces should be implemented as a debug-only source switch that leaves the real Room history untouched.

## Prioritized Refactor Phases

### Phase 1: Lock Standards And Remove Residue

- lock naming and package standards
- document deprecated starter-template areas
- remove or quarantine post/login/template flows from the product path
- rename legacy `Foundation` naming where it affects active product code

### Phase 2: Shared UI And Token Foundation

- create app-wide semantic dimension tokens
- standardize shared cards, headers, meters, badges, and state surfaces
- refactor the shell, home, and settings screens onto those primitives

### Phase 3: Assessment Flow Refactor

- split `AssessmentScreen`
- split `AssessmentViewModel`
- extract assessment progression helpers and result-label mappers
- unify result presentation across assessment and results screens

### Phase 4: Navigation And Shell Refactor

- reduce route-specific shell hacks
- make headers and nested settings behavior route-driven
- simplify `MainNav` into smaller navigation and shell units

### Phase 5: Data Hardening

- export Room schema
- replace destructive migration fallback with real migrations
- add migration tests
- align local storage naming with product architecture

### Phase 6: Content And Localization Hardening

- finish ten-sephirot content seeding
- remove builder-facing strings
- polish English and Spanish production copy
- validate that authored content and scoring contracts still match

## Current Highest-Priority Refactor Targets

- `app/src/main/java/com/netah/hakkam/numyah/mind/ui/screen/AssessmentScreen.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/viewmodel/AssessmentViewModel.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/ui/screen/SettingsScreen.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/ui/nav/MainNav.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/data/datasource/FoundationDatabase.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/data/repository/PostRepository.kt`
- `app/src/main/java/com/netah/hakkam/numyah/mind/ui/screen/LoginScreen.kt`
- `app/src/main/res/values/dimen.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`

## Definition Of Done For Refactor Work

A refactor pass is considered complete only when:

- the new standard is explicit
- the implementation follows it
- the old competing pattern is removed or deprecated
- requirement files are updated in the same pass
- tests still reflect the current architecture

## Refactor Lessons Learned

- Starter-template residue creates real architecture drag. If a flow is not part of the product, it should be removed or clearly quarantined early.
- Shared UI extraction pays off most when done after the product visual direction is stable enough to see true repetition, not before.
- Semantic tokens reduce churn only when they replace app-wide reuse. Onboarding-specific tokens should stay onboarding-specific.
- Large screens improve fastest when the screen becomes an orchestrator and the repeated visual sections move into owned components.
- Navigation state should follow real routes whenever possible. Mirrored local shell state becomes fragile and causes unnecessary coupling.
- Five-tab bottom navigation reaches a readability threshold quickly on narrow devices. Label fit and destination wording need to be locked as part of shell design, not as afterthought polish.
- Destination-aware confirmation copy is part of navigation trust, not optional polish.
- When a refactor changes a project standard, updating the docs in the same pass prevents the next thread from rebuilding old patterns.
- Manual smoke testing remains the fastest way to catch UI regressions during refactor work, especially for shell behavior, nested navigation, and visual elevation issues.
- Persistence and content hardening should be timed intentionally. Not every long-term standard needs to be implemented immediately if the delivery milestone is different.
- Debug/testing shortcuts that mutate user data create avoidable risk. For product review flows, switching read sources is often the safer architecture.
- Compose tests on polished screens need to tolerate legitimate growth in above-the-fold content or they turn refactors into false regressions.
- Dynamic chart screens are safer to test when detail rows and filter chips expose explicit test tags instead of relying on repeated visible text that may also appear in legends, chips, or headings.
- Refactor work should support the mainline release track from the production roadmap rather than delaying product completion for cleanup that is not yet release-relevant.
- Trust-sensitive actions such as replacing an unfinished assessment should be implemented once in the data/session contract and reused upward through navigation and UI, rather than reset separately at each screen.

## Immediate Next Step

The next implementation phase after this document is:

- Phase 1 cleanup of starter-template residue and naming

That should begin with:

- product naming cleanup
- deprecated flow quarantine or removal
- token strategy setup
- shell/navigation simplification plan
