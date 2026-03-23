# Production Readiness Roadmap

This document defines the working roadmap from the current repository state to a production-ready `Numyah Mind` release.

It is meant to keep future threads aligned around:

- what should happen next
- what can happen in parallel
- what is release-blocking
- what can continue as an ongoing side mission

If this roadmap changes in a way that affects project standards, update:

- `AGENTS.md`
- `README.md`
- `docs/product_spec.md`
- `docs/refactor_roadmap.md`
- `docs/assessment_task_status.toml`

## Roadmap Structure

Work now moves in two coordinated tracks:

- `Mainline release track`
  - the work required to ship a polished, reliable, production-ready app
- `Sephira content side mission`
  - authored sephira enrichment that can continue in parallel as content is finalized

The side mission must not unnecessarily block the mainline release track unless a specific feature explicitly requires finalized content for all affected sephirot.

## Mainline Release Track

### Phase 1: Product Completion

Goal:

- make the core app functionally complete for the release target

Focus:

- finish core Home, Assessments, History, Results, Learn, and Settings behavior
- tighten retake, resume, and saved-session re-entry flows
- close remaining production-surface placeholders
- ensure the app feels complete even where some authored sephira content still uses approved fallback copy
- treat trust-sensitive continuation and replacement moments as product work, not only navigation work, so users always understand when an unfinished session will be replaced and when completed history will remain untouched

Release gate:

- every production surface should feel intentional, trustworthy, and usable end-to-end

### Phase 1.5: Tester Distribution And Observability

Goal:

- make the app learnable in real tester hands before deeper polish and release hardening

Focus:

- prepare Google Play internal testing or closed testing distribution
- add crash reporting for release-candidate and tester builds
- add lightweight product analytics for the core reflective journey
- define a small, stable event taxonomy for onboarding, start, resume, start-fresh confirmation, completion, results detail, history re-entry, learn reading, and key settings changes
- keep telemetry aligned with the product's local-first trust model and explain it clearly in privacy-facing copy when enabled
- prefer minimal, product-relevant analytics over broad behavioral tracking
- add non-fatal reporting for important recoverable failures that testers may hit during real usage

Current implementation standard for this phase:

- gate tester observability behind explicit Gradle properties instead of making Firebase mandatory for every build
- keep telemetry calls behind an app-owned interface so the product event contract stays stable if the backend changes
- allow local Timber logging for observability verification when Firebase is not enabled
- restrict the approved event taxonomy to the small journey set documented in [tester_distribution_and_observability.md](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\tester_distribution_and_observability.md)
- keep privacy-facing copy explicit that crash and flow telemetry in tester builds does not include answers, interpretation copy, or saved score details

Release gate:

- tester builds should surface actionable crash and product-flow signals without violating the app's calm, privacy-respectful positioning

### Phase 2: UX And Enjoyment Polish

Goal:

- make the app enjoyable, calm, and product-grade for real users

Focus:

- visual polish across hierarchy, spacing, and readability
- richer empty, loading, and recovery states
- calmer chart and trend refinement inside History
- copy consistency review for psychological tone, trust, and non-pathologizing language
- smoother transitions between top-level surfaces and detail screens

Release gate:

- the experience should feel coherent and emotionally trustworthy, not only technically complete

### Phase 3: Refactor And Architecture Hardening

Goal:

- reduce technical risk before release pressure makes changes expensive

Focus:

- decompose large screens and ViewModels that are still above the project readability threshold
- standardize shared UI where real product repetition is now visible
- keep route-driven shell behavior clean
- remove or quarantine leftover starter-template residue
- preserve chart-ready and testable contracts as graph polish continues

Release gate:

- active product code should be maintainable enough that fixes and last-mile polish remain low-risk

### Phase 4: Reliability And Coverage Expansion

Goal:

- harden critical flows and reduce regression risk

Focus:

- expand ViewModel and Compose coverage for user-critical flows
- continue repository/use case tests where behavior meaningfully grows
- strengthen History, Results, resume, and assessment progression coverage
- keep tests stable by relying on scroll-aware assertions and explicit test tags on dynamic surfaces
- keep androidTest verification grounded in a known-good device setup so environment issues like `Don't keep activities` do not masquerade as broad Compose regressions

Release gate:

- core product flows should be well-covered and resilient to polish-driven UI growth

### Phase 5: Accessibility And Localization Hardening

Goal:

- make the app ready for real-world usage across languages and devices

Focus:

- perform a true Spanish quality pass
- review accessibility semantics, contrast, type scaling, and touch targets
- verify long-content behavior on smaller phones
- verify chart readability and control discoverability in both supported languages

Release gate:

- the app should remain understandable, navigable, and polished in English and Spanish under realistic device conditions

### Phase 6: Release Hardening

Goal:

- close the technical work required before submission

Focus:

- Room schema export
- explicit migrations
- migration tests
- manifest and dependency cleanup
- crash and edge-case cleanup
- offline and persistence verification
- release configuration and versioning

Release gate:

- the app should be safe to update in users' hands and technically ready for store submission

### Phase 7: Final QA And Submission Prep

Goal:

- treat release as a product launch, not only a build milestone

Focus:

- full manual smoke pass across all primary flows
- verify empty, partial, and rich saved-data scenarios
- verify install and upgrade behavior
- prepare release notes, store copy, and submission-facing material

Release gate:

- no known release-blocking UX, persistence, localization, or navigation issues remain

## Sephira Content Side Mission

This side mission continues in parallel with the mainline release track.

### Purpose

- let authored sephira content arrive gradually without freezing release progress

### Scope

- sephira intro copy
- `detailContent`
  - `healthyExpression`
  - `deficiencyPattern`
  - `excessPattern`
  - `suggestedPractices`
- question refinements
- translation alignment for that sephira

### Working Rule

- content can be added one sephira at a time, in focused passes, whenever finalized material is available
- future threads may inject finalized sephira content in the middle of the main roadmap without reopening unrelated architecture decisions
- if a future feature depends on all affected sephirot having finalized authored content, that dependency must be locked explicitly before implementation

### Side-Mission Standard

- use the existing seeded content shape
- preserve scoring contracts unless a scoring revision is explicitly locked
- keep copy user-facing and product-grade
- do not let temporary fallback copy become the unreviewed long-term standard

## Priority Order

The default near-term order is:

1. `Phase 1: Product Completion`
2. `Phase 1.5: Tester Distribution And Observability`
3. `Phase 2: UX And Enjoyment Polish`
4. `Phase 3: Refactor And Architecture Hardening`
5. `Phase 4: Reliability And Coverage Expansion`
6. `Phase 5: Accessibility And Localization Hardening`
7. `Phase 6: Release Hardening`
8. `Phase 7: Final QA And Submission Prep`

The `Sephira content side mission` continues throughout all of those phases.

## Release-Blocking Vs Non-Blocking

Release-blocking by default:

- broken primary navigation or resume behavior
- unstable persistence or migration risk
- major missing production surfaces
- unresolved accessibility or localization failures on core flows
- unresolved crash or data-loss risks
- lack of crash visibility or tester-distribution readiness once the team has chosen to run real Play testing

Not automatically release-blocking:

- unfinished authored detail content for sephirot that already have approved fallback behavior
- optional visual refinements that do not affect trust or readability
- future graph enhancements beyond the currently locked trend contracts

## Threading Guidance

Future threads should usually identify themselves as one of:

- `mainline roadmap thread`
- `sephira content side-mission thread`
- `release hardening thread`

If a thread changes a roadmap standard, update this file and the linked requirement files in the same pass.
