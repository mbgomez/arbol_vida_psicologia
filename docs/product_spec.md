# Product Spec: Tree of Life Psychology App

## 1. Short Product Vision

Build a reflective Android app that translates the Kabbalah Tree of Life into a practical psychology experience. Users complete a structured questionnaire across the ten sephirot and receive a personal profile showing where each sephira appears balanced, deficient, or excessive, along with clear explanations and gentle practices for integration.

The product should feel insightful, grounded, and non-pathologizing. It is a self-reflection tool, not a diagnostic or clinical mental health product.

Canonical product name: `Numyah Mind`.

The app's educational content should be grounded in the project's own Kabbalah reference document in [Tree of life - overview - psychology.docx](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\Tree%20of%20life%20-%20overview%20-%20psychology.docx). Assessment and onboarding content should be adapted into app-friendly copy, while Learn content may stay much closer to the authored Kabbalistic teaching voice.

The app should launch with bilingual support for English and Spanish.

## 2. User Flow

### Primary Flow

1. User opens the app.
2. User sees short onboarding that explains the Tree of Life framework, reflection-focused positioning, privacy, and time commitment.
3. User starts a new assessment.
   - Home can still offer a direct start action.
   - `Assessments` is the dedicated top-level browsing surface for the growing assessment library.
   - The library should be shaped to support multiple future assessments, including later user-authored assessments, even if v1 still starts with the Tree of Life assessment.
4. Before the first questionnaire intro, the user sees a brief honesty notice that frames the reflection and can be dismissed permanently with a local preference.
5. Before each sephira section, the user sees a short intro that explains the sephira in practical psychological language.
6. User answers questionnaire sections for each sephira in sequence.
7. After each section, progress is saved locally.
8. When the final section is complete, the scoring engine evaluates each sephira.
9. User lands on the results overview screen with all ten sephirot and their states.
10. User taps any sephira to open a deeper detail screen.
11. User reviews psychological meaning, signs of deficiency or excess, strengths of balance, and suggested practices.
12. User can open an optional Learn/About area for deeper Kabbalah context and longer educational content.
13. User can save the result, retake the assessment later, or compare with a previous run in a future version.

### Secondary Flows

- Resume interrupted assessment from the last unanswered question.
- Distinguish on Home between:
  - starting a new assessment
  - resuming an in-progress assessment
  - opening the latest completed reflection
- If a user leaves an in-progress assessment for Home, the shell should return them to the existing Home surface when possible instead of rebuilding a new navigation stack unnecessarily.
- Retake assessment from the results screen.
- Review previous completed assessments from history.
- Open a sephira detail screen directly from a saved assessment.
- Open a Learn/About section without starting the assessment.
- Review graph-based trends across completed assessments in History in a future slice.

Locked history standard:

- The History tab should show completed assessments as a real saved-results surface, not as a placeholder or roadmap note.
- Each saved session should show enough context for calm re-entry:
  - completion date
  - how many sephirot were saved in that session
  - which sephira carried the most tension
  - which sephira looked most settled
- Selecting a history item should open the results overview for that exact saved session.

### UX Principles

- One question at a time or one short cluster at a time.
- Clear progress and section context.
- A short pre-assessment honesty prompt should frame the questionnaire as useful only when answered from the user's real current experience, not their ideal self.
- Language should be psychologically accessible, spiritually respectful, and free of deterministic claims.
- Results should emphasize tendencies, not labels.
- Result screens should provide plain-language interpretation, likely real-life expression, and a gentle next step, not only a pole label or raw scores.
- Onboarding should be a dedicated first-run experience rather than a reused in-app shell screen.
- Onboarding should be trust-building and substantial enough to orient the user, but still lighter than the deeper educational material in Learn/About.
- Onboarding copy must be written from the user's perspective and should never read like implementation notes, roadmap language, or builder-facing commentary.
- Deeper educational material should appear in context, especially before and after each sephira assessment.
- All core user-facing flows should be available in English and Spanish.
- The first-run experience should feel polished and product-grade, with intentional layout, strong hierarchy, and a professional visual treatment.
- The top-level shell should stay calm and uncluttered while still reflecting the product's real direction:
  - `Home` for fast entry, resume, and latest reflection value
  - `Assessments` for browsing the growing library
  - `History` for completed reflections and future trend graphs
- In the five-tab bottom shell, labels should remain readable on smaller phones. Prefer adaptive single-line label sizing and, when necessary, a shorter nav-only label rather than multi-line wrapping of the tab text.
- The visible screen title may stay fuller than the bottom-nav label. A tab can use a shortened nav label while the destination header keeps the full product-facing name.
- When a user tries to leave an in-progress assessment through the shell, the exit dialog should name the actual destination in both its body and confirm action.
- Refactored Compose screens should keep resource lookup and visual tokens close to the UI section that uses them, so extracted sub-composables remain self-contained and easier to maintain.
- Shared dimension tokens should be grouped by semantic role, not just by matching numeric value.
- Local execution workflow should stay explicit: Android Studio runs, Gradle builds, emulator checks, and manual testing are performed by the user, while implementation changes should include targeted verification steps for the user to run.

## 3. Screen List

### Launch / Setup

- Splash or startup routing screen
- Optional sign-in gate if the existing auth foundation is kept, though this should be disabled for v1 unless there is a real backend need
- Dedicated onboarding flow with its own visual treatment and without the standard in-app toolbar behavior

### Assessment

- Assessment library screen
- Assessment catalog tab destination
- Sephira intro screen for the current section
- Question screen
- Section completion micro-summary screen
- Resume assessment prompt

### Results

- Results overview screen with all ten sephirot
- Sephira detail screen
- Suggested practices screen or practices section inside detail
- Assessment history screen
- History graphs and trend summaries in a future slice

### Support Screens

- Learn / About the Tree of Life screen
- Privacy and methodology screen
- Settings screen

### Settings Scope Standard

The Settings screen should be a product-grade support surface, not a generic utility list. Its role is to help the user control reading comfort, language, trust-facing preferences, and onboarding behavior without introducing complexity that does not support the reflective core loop.

Locked v1 sections:

- `Language`
- `Appearance`
- `Assessment experience`
- `Privacy and data`
- `About`

Locked v1 controls:

- Language:
  - `System default`
  - `English`
  - `Spanish`
- Appearance:
  - `System default`
  - `Light`
  - `Dark`
- Assessment experience:
  - show or hide the pre-assessment honesty notice
- Onboarding behavior:
  - replay onboarding from the beginning

Locked behavior rules:

- Language and appearance should default to system behavior until the user explicitly selects an override.
- Theme and language are app-wide preferences and should apply across the full app shell.
- Settings must remain local-first and should not require sign-in, sync, or backend access.
- Replaying onboarding resets only the onboarding completion state and does not clear saved assessments, results, answers, or history.
- Hiding the honesty notice affects only the short pre-assessment framing notice and should not remove other trust or privacy copy elsewhere in the product.
- Privacy and data entries in Settings should explain the local-device storage model in plain user-facing language rather than technical implementation terms.
- About content in Settings should reinforce the non-diagnostic positioning, reflective purpose, and methodology framing.
- Settings copy should stay calm, practical, and psychologically respectful. It should not read like developer notes or device-configuration jargon.

## 4. Domain Model

The domain should center on a local-first assessment engine.

### Core Enums

- `SephiraId`
  - `KETER`
  - `CHOKHMAH`
  - `BINAH`
  - `CHESED`
  - `GEVURAH`
  - `TIFERET`
  - `NETZACH`
  - `HOD`
  - `YESOD`
  - `MALKUTH`
- `Pole`
  - `BALANCE`
  - `DEFICIENCY`
  - `EXCESS`
- `AssessmentStatus`
  - `NOT_STARTED`
  - `IN_PROGRESS`
  - `COMPLETED`
- `QuestionFormat`
  - `LIKERT_5`
  - `LIKERT_7`
  - `SINGLE_CHOICE`

### Core Entities

- `SephiraDefinition`
  - id
  - displayName
  - order
  - shortMeaning
  - balancedDescription
  - deficiencyDescription
  - excessDescription
  - practiceRecommendations
  - cautionNote

- `Questionnaire`
  - version
  - title
  - sephiraSections

- `SephiraSection`
  - sephiraId
  - title
  - introText
  - questions

- `Question`
  - id
  - sephiraId
  - prompt
  - format
  - options
  - reverseScored
  - weightsByPole

- `AnswerOption`
  - id
  - label
  - numericValue

- `AssessmentSession`
  - id
  - questionnaireVersion
  - startedAt
  - completedAt
  - status
  - currentSephira
  - currentQuestionIndex

Locked session-model standard:

- One `AssessmentSession` spans the full ten-sephirot assessment.
- Completing a sephira section saves progress and may persist that sephira's derived score, but does not complete the overall session.
- The overall session becomes `COMPLETED` only after the final sephira is finished and the Tree-wide results overview can be shown.
- Resume behavior should return the user to the current sephira and last unanswered question within that larger session.

- `Response`
  - sessionId
  - questionId
  - selectedOptionId
  - numericValue
  - answeredAt

- `SephiraScore`
  - sessionId
  - sephiraId
  - balanceScore
  - deficiencyScore
  - excessScore
  - dominantPole
  - confidence

- `AssessmentResult`
  - sessionId
  - overallSummary
  - sephiraScores
  - generatedAt

### Content Model

Each sephira should include:

- short intro copy shown before the questionnaire section
- psychological theme
- healthy expression
- typical deficiency expression
- typical excess expression
- reflective prompts
- grounding practices
- optional journaling prompts

### Content Source Strategy

The project's Kabbalah document should be adapted into four content layers:

- `Onboarding content`
  - a clear explanation of the Tree of Life as a reflection map
  - explanation of balance, deficiency, and excess
  - reflection-focused framing and non-diagnostic disclaimer
  - privacy and local-first expectations
  - a concise explanation of what the user will experience and what kind of results they will receive
- `Sephira intro content`
  - one short paragraph before each sephira question set
  - plain-language psychological framing
- `Sephira result detail content`
  - fuller explanation of the sephira
  - how balance, deficiency, and excess may show up
  - reflective examples and suggested practices
- `Learn/About content`
  - deeper material such as broader Kabbalah framing, structure of the Tree, and optional terminology

The document should be treated as the content source, but the app uses two different voice standards:

- assessment and onboarding copy should be edited toward clarity, brevity, and psychologically grounded product language
- Learn copy should preserve the authored Kabbalistic teaching voice much more closely, with only light cleanup for readability, structure, and mobile presentation

User-facing copy rules:

- Prefer direct user-oriented language such as "you will reflect on" or "your results may show" rather than builder-oriented language such as "the app will later include."
- Avoid copy that references implementation state, roadmap progress, or development placeholders in production-facing flows.
- Footer and supporting copy on production browsing surfaces should describe what the user can do there, not how the team is structuring or staging the feature internally.
- Keep onboarding understandable to a first-time user without requiring product or technical context.
- Production-grade screens should externalize stable strings and dimension values early so localized copy, polish passes, and layout tuning remain low-risk.
- When result confidence is low, use softened interpretation such as "leans toward balance" or "current tendency" rather than definitive labels.
- Result language should frame patterns as present-state tendencies, not fixed identity or diagnosis.

Locked workflow learning:

- Future sephira content should be seeded manually from the authored question set rather than generated from prior slices by default.
- Manual verification and project execution are user-owned. The implementation workflow should assume the user runs builds, tests, and interactive checks locally and reports the first failure or mismatch back into the collaboration loop.

### Locked Content Structure Standard

The Malkuth slice establishes the default content structure for future sephirot.

Each sephira questionnaire definition should include:

- versioned bilingual content
- `shortMeaning`
- `introText`
- `detailContent`
- `pages`
- `questions`

`detailContent` should include:

- `healthyExpression`
- `deficiencyPattern`
- `excessPattern`
- `suggestedPractices`

Each page should include:

- page id
- title
- description
- ordered `questionIds`

Each question should include:

- question id
- `sephiraId`
- `pageId`
- localized prompt
- `format`
- `targetPole`
- `weight`

This content structure is now the default standard unless a later review explicitly changes it in both docs and implementation.

### Locked Settings Implementation Standard

When implementing the Settings slice:

- Keep the screen under the existing `ui/screen` structure and expose state through a dedicated `StateFlow`-backed ViewModel.
- Keep app preference persistence isolated in the app preferences repository and expose changes through thin use cases when the feature follows the repository -> use case -> ViewModel path.
- Treat theme and language selections as durable app preferences rather than transient screen state.
- Keep destructive data actions out of the initial settings slice unless their behavior and confirmation model are explicitly locked first.
- Add repository, use case, and ViewModel tests for new settings behavior when those layers are introduced.
- Use `DataStore` as the default persistence layer for app-wide settings such as theme, language, onboarding flags, and other local preferences. Do not expand `SharedPreferences` further for new settings work.
- Treat runtime language override as an app-shell behavior, not a screen-local one. Locale application should be wired through the application and activity host so the full UI updates consistently.
- Nested settings detail destinations should use a dedicated detail-header pattern rather than reusing the exact top-level shell treatment.
- Header state for nested settings pages should transition in sync with route changes so the user does not briefly see the wrong top bar during forward or backward navigation.
- Settings entry surfaces should present one clear destination affordance per card. Avoid duplicate CTA labels within the same destination row.
- For scrollable settings screens, prefer explicit test-friendly semantics on the real actionable controls and add smaller focused UI tests when full-screen Compose tests become brittle after layout polish.

Locked scaling learning:

- The reusable sephira slice is:
  - intro content
  - paged questionnaire
  - saved section score
  - short section-complete interpretation
- This slice should be repeated consistently before introducing additional per-sephira UX variations.

### Localization Requirement

The product should support:

- English
- Spanish

Localization rules:

- All onboarding, questionnaire, results, practices, and Learn/About content should be available in both languages.
- Content architecture should separate copy from scoring logic.
- The source document may remain as the authoring base, but app-ready copy should be produced as localized English and Spanish strings.
- Question meaning should remain equivalent across languages, especially where scoring depends on nuanced wording.

Learn content architecture should follow the same local-first principle:

- seed Learn content from local JSON in a shape that can later be served by an endpoint
- organize Learn content as:
  - `version`
  - `courses`
  - per-course `sections`
- treat Learn as the place for the more direct Kabbalistic teaching voice rather than rewriting it into the same softer tone used in assessment content
- keep course metadata explicit:
  - stable `id`
  - localized title
  - localized subtitle
  - localized description
  - estimated reading time
  - total planned section count
- keep section metadata explicit:
  - stable `id`
  - localized title
  - localized summary
  - reading time
  - display order
  - localized body paragraphs
- the first seeded course should follow the source document closely, using one introduction section plus one section per sephira
- unfinished sephira sections should be allowed to arrive gradually without redesigning the Learn flow

Locked Learn delivery phases for the current course slice:

- Phase 1:
  - lock Learn voice to stay close to the authored Kabbalistic teaching style
  - seed the first available sections from the source document
- Phase 2:
  - evolve the section reader toward a book-like reading page
  - support chapter progression and calmer reading rhythm
- Phase 3:
  - add remaining missing tests for Learn behavior and persistence
  - capture Learn-specific implementation learnings and refinement standards in docs

## 5. Scoring Model

The goal is not to force a mystical interpretation, but to produce a stable and understandable psychological profile.

### Basic Structure

Each sephira has a dedicated question set, ideally 6 to 10 questions in v1.

Each question contributes weighted points to one or more poles:

- balance
- deficiency
- excess

Example:

- A statement like "I can set limits without feeling guilty" may contribute positively to `BALANCE` for `GEVURAH`.
- A statement like "I avoid conflict even when something matters deeply to me" may contribute to `DEFICIENCY` for `GEVURAH`.
- A statement like "I become rigid when others do not meet my standards" may contribute to `EXCESS` for `GEVURAH`.

### Recommended Response Format

Use a 5-point Likert scale:

- strongly disagree = 0
- disagree = 1
- neither = 2
- agree = 3
- strongly agree = 4

This keeps the questionnaire simple and mobile-friendly.

### Per-Question Weighting

Each question should define:

- target sephira
- target pole
- optional secondary pole contribution
- weight, default `1.0`

This allows some questions to reflect mixed patterns without overcomplicating the UI.

Locked decision:

- `weight` is a real scoring field and should remain in the authored content model.
- Until weighted scoring is fully applied in implementation, authored v1 content should continue using `1.0` by default so the content contract stays stable without implying tuned weighting that the engine does not yet honor.

### Per-Sephira Calculation

For each sephira:

1. Sum all weighted answer contributions into three buckets: `balance`, `deficiency`, and `excess`.
2. Normalize by the maximum possible score for that section.
3. Compute percentages for the three poles.
4. Choose the dominant pole using threshold rules.

The Malkuth slice establishes the default v1 scoring pattern:

- compute independent normalized scores for `balance`, `deficiency`, and `excess`
- determine a dominant pole deterministically
- compute explicit confidence
- preserve low-confidence handling internally even when the UI shows a softened dominant tendency
- save each sephira result as soon as that section is completed so progress and engagement do not depend on reaching the final Tree-wide overview

### Suggested Classification Rules

For each sephira:

- `BALANCED` if:
  - balance percentage is highest, and
  - balance percentage is at least 0.55, and
  - the gap between balance and the next highest pole is at least 0.10

- `DEFICIENCY` if:
  - deficiency percentage is highest, and
  - deficiency percentage is at least 0.45, and
  - deficiency exceeds balance by at least 0.08

- `EXCESS` if:
  - excess percentage is highest, and
  - excess percentage is at least 0.45, and
  - excess exceeds balance by at least 0.08

- `MIXED / LOW_CONFIDENCE` internal state if no rule is met
  - In UI, map this to the nearest dominant pole but show lower confidence and softer language such as "leans toward excess" rather than a hard label.

These rules are the locked v1 baseline for scaling content and tests. Threshold tuning remains allowed, but only as an explicit scoring revision rather than an ad hoc per-sephira change.

### Confidence Score

Confidence should help copy and UI tone.

Suggested confidence inputs:

- dominance gap between highest and second-highest pole
- completion rate
- variance consistency within the sephira section

Confidence bands:

- `HIGH`
- `MEDIUM`
- `LOW`

### Example Output Per Sephira

- `TIFERET`
  - balance: 0.62
  - deficiency: 0.21
  - excess: 0.17
  - result: balanced
  - confidence: high

## 6. Architecture Proposal For This Repo

## Current Repo Read

The repository already contains:

- a single `:app` module
- Compose UI
- Hilt DI
- Room database plumbing
- Retrofit networking
- Navigation Compose
- starter packages for `data`, `domain`, `ui`, `viewmodel`, and `di`

It also appears to contain starter-template content such as `PostRepository`, `MainScreen`, and `DetailScreen`, which should be treated as scaffolding rather than the real product architecture.

## Proposed Direction

For this project, keep one module first and standardize the active UI implementation inside the existing `ui` tree. That matches the current size of the repo, keeps the package structure easy to scan, and avoids mixing multiple competing UI conventions.

### Recommended Package Structure

`com.netah.hakkam.numyah.mind`

- `app`
  - `NumyahMindApplication`
  - app-wide setup
- `data`
  - `local`
  - `repository`
  - `mapper`
- `ui`
  - `components`
  - `nav`
  - `screen`
  - `theme`
- `viewmodel`
- `feature`
  - future domain-oriented expansion only when it materially improves maintainability

Recommended rule:

- user-facing Compose UI should live under `ui/*`
- ViewModels should live under `viewmodel/*`
- data, domain, and DI should stay outside the UI tree
- When a feature already uses repository and use case abstractions, prefer flow-based repositories and thin use cases rather than direct repository access from ViewModels
- ViewModels should expose semantic UI state rather than localized user-facing copy. Resolve display strings such as error text, result labels, and action wording in the UI layer from resources whenever possible.
- Small local preferences such as onboarding completion and "do not show again" notices should share one app preferences repository rather than multiplying narrow repositories by screen.
- Compose UI tests should prefer small explicit hooks such as stable tags for interactive controls when text alone would make assertions ambiguous or brittle.

### Layer Responsibilities

- UI layer
  - Compose screens, view state, events, navigation
- Domain layer
  - use cases for loading questionnaire, saving answers, resuming progress, scoring results
- Data layer
  - Room entities, DAOs, repositories, seed content sources, optional remote sync later
- Core scoring layer
  - deterministic scoring engine isolated from Android dependencies

### ViewModel Plan

Use one ViewModel per screen or tightly related flow, under the `viewmodel` package:

- `OnboardingViewModel`
- `AssessmentViewModel`
- `ResultsViewModel`
- `SephiraDetailViewModel`
- `HistoryViewModel`

Prefer `StateFlow`-based UI state rather than exposing mutable Compose state directly. This will make business logic easier to test.

For assessment slices, keep intro content, question progression, resume position, completion behavior, and result mapping inside the same ViewModel-driven state contract even if the UI later renders intro and questions as separate screens or phases.

For multi-phase assessment flows, prefer explicit UI states such as `Loading`, `Intro`, `Question`, `Completed`, and `Error` instead of one overloaded active-state object. Extract shared UI data such as progress and navigation into smaller submodels when that improves readability and testing.

When building Compose screens on top of those flows, keep the screen layer thin: map semantic ViewModel state to resources and visual sections, and avoid rebuilding questionnaire progression, result classification, or persistence logic in the UI layer.

### Storage Proposal

Use Room for v1 with the following tables:

- `questionnaires`
- `sephira_definitions`
- `questions`
- `answer_options`
- `assessment_sessions`
- `responses`
- `sephira_scores`

Questionnaire content can be seeded from local JSON on first launch or from code-based fixtures in v1. Local JSON is preferable because copy and question wording will change often.

Seeded content should be structured to support multilingual copy from the start.

For the assessment engine, use local JSON as the mock-endpoint content shape in v1, parse it through the content layer, and cache the normalized questionnaire into Room so the same flow supports offline reuse and future remote-content evolution.

ID standard:

- use `Long` for runtime-created database records such as assessment sessions
- use `String` for authored content identifiers such as questionnaire versions, question IDs, page IDs, and answer option IDs
- use enums for closed app vocabularies such as `SephiraId`, `Pole`, `QuestionFormat`, `AssessmentStatus`, and `ConfidenceLevel`

### Navigation Proposal

Navigation graph:

- onboarding
- assessment intro
- sephira intro
- assessment question flow
- assessment results
- sephira detail
- learn/about
- history
- settings

The current login flow should not be part of the v1 critical path unless there is a real product requirement for account sync.

### Networking Proposal

Retrofit is already present, but v1 does not need network dependency for the core product loop. Keep the networking stack available but do not make the questionnaire or scoring depend on it.

Possible future uses:

- syncing assessment history
- content updates
- remote editor-driven questionnaire versions

### Test Strategy

Prioritize tests for:

- scoring engine
- questionnaire progression logic
- repository persistence
- ViewModel result mapping

Suggested test split:

- unit tests for scoring rules and use cases
- Room integration tests for persistence
- Compose UI tests for core screens

For flows that use repository -> use case -> ViewModel layering, keep tests aligned across that stack:

- repository tests for emitted persistence values
- use case tests for delegation and flow output
- ViewModel tests for UI state mapping and flow-driven behavior

UI test prioritization guidance:

- Start by covering the screens and interactions that define the current product slice, such as onboarding, primary entry actions, and critical navigation behavior.
- Avoid investing heavily in UI tests for placeholder or transitional screens unless they represent a real user contract that should remain stable.
- Prefer Robolectric-backed local JVM tests for Room repository and database verification when the behavior does not require device-only execution. Avoid growing new instrumented persistence tests by default unless the test specifically depends on Android runtime behavior that Robolectric cannot cover well.
- For assessment flows, keep progression and persistence logic owned by the ViewModel/use case layer rather than duplicating that behavior in Compose UI code.
- For assessment UI tests, prioritize stable user contracts such as intro visibility, question progression, answer interaction, completion rendering, and retry behavior before testing visual details.
- For assessment result screens, test the presence of the interpretive sections and the primary completion action, not only the raw classification label.

## 7. Implementation Phases

### Phase 1: Product Foundation

- remove or quarantine starter-template post flows from the main navigation path
- define sephira taxonomy and copy guidelines
- adapt the Word document into onboarding, sephira intro, detail, and learn-content layers
- define English and Spanish content requirements and translation approach
- write v1 questionnaire content for all ten sephirot
- define Room schema and domain models
- define navigation routes and screen contracts

### Phase 2: Assessment Engine

- implement questionnaire loader
- build assessment session persistence
- implement answer capture and resume logic
- create deterministic scoring engine
- add unit tests for score calculation

### Phase 3: Core User Experience

- build onboarding
- build short sephira intro screens
- build question flow UI
- build progress tracking
- build a short section-complete result after each sephira
- build results overview
- build sephira detail screen with explanations and practices
- build optional Learn/About area if it fits the phase scope

### Phase 4: History And Polish

- add assessment history
- add retake flow
- improve copy and confidence messaging
- add empty, loading, and recovery states
- add accessibility and content review pass

### Phase 5: Optional Extensions

- compare current and previous assessments
- create personalized growth plans
- add reminders for reflective practices
- add account sync
- support remote content versioning

## Suggested v1 Content Shape

To keep the first release achievable:

- 10 sephirot
- 6 questions per sephira
- 60 total questions
- 5-point Likert answers
- 1 overall results dashboard
- 10 sephira detail pages

This is enough to feel meaningful without making the assessment exhausting.

## Risks And Product Notes

- Spiritual language can become vague if not paired with concrete psychology terms.
- Clinical-sounding labels should be avoided.
- Balance, deficiency, and excess should be framed as current tendencies, not fixed identity.
- The copywriting and question design will matter as much as the code.
- The scoring model should be transparent enough that future content edits do not break interpretability.
- Long-form educational content should not overload onboarding.
- The source document should be edited into app-ready copy rather than used verbatim.
- English and Spanish copy must stay semantically aligned so scoring interpretation remains consistent.

## Working Standard

Implementation and product decisions for this repository should be made with the combined judgment of:

- a senior UX designer
- a senior Android developer
- a senior project manager

In practice, that means:

- prioritize user trust and clarity over cleverness
- prefer polished, intentional flows over generic template behavior
- keep architecture simple but durable
- avoid shipping user-facing copy or UI that feels unfinished, internal, or prototype-like

Collaboration protocol for implementation:

- Lock major identity and scope decisions before broad implementation passes when possible.
- Distinguish between:
  - temporary experiments
  - standards that should propagate into code structure and documentation
- Run each feature slice in this order:
  - clarify the decisions being locked
  - implement the slice
  - review and refine with product feedback
- When feedback changes a project standard, update both the implementation and the requirement files so the change becomes durable.

Batch-planning protocol:

- Before implementing a new sephira batch, lock:
  - sephira order
  - question count per sephira
  - whether section-complete interpretation copy is included in the batch
  - the minimum repository, ViewModel, and Compose coverage expected for that batch

Compose screen construction guidance:

- A screen should primarily coordinate layout and state, not hold every visual section inline.
- Extract smaller composable sections when they represent distinct responsibilities such as top bars, hero media, body copy, progress indicators, cards, and action areas.
- Keep reusable visual building blocks easy to share across screens.
- Prefer decomposition that improves readability and reuse without fragmenting simple logic into unnecessary wrappers.
- For multi-step onboarding or intro flows, keep page definitions explicit so text, artwork, and behavior can evolve together without spreading page logic across the layout tree.

## Locked Refactor Direction

The repository also follows the locked refactor direction in:

- [Refactor roadmap](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\refactor_roadmap.md)

That roadmap is now the source of truth for refactor-specific standards such as:

- product naming cleanup
- template residue removal or quarantine
- package convergence under the current `ui`, `viewmodel`, `data`, and `domain` direction
- shared UI extraction thresholds
- design-token naming and reuse rules
- navigation-shell cleanup
- Room hardening expectations

Locked refactor standards to preserve during new work:

- legacy starter-template concepts such as posts, generic login scaffolding, and `Foundation` naming should not define future product architecture
- app-wide reused tokens should move toward semantic naming such as spacing, radius, size, stroke, and elevation instead of feature-local names
- screens above the current readability threshold should be decomposed before new complexity is added to them
- if the authored content model keeps `weight`, the scoring engine should eventually use it as real scoring input
- destructive Room migration fallback is not the long-term persistence standard for the product
- full Room migration/schema hardening is deferred until the first Google Play release-preparation pass
- deeper content/version hardening is deferred until the app is ready to add more sephirot beyond the current finalized content set

## Recommended Next Step After This Spec

Before coding, define the v1 content set in detail:

- final wording for each sephira definition
- 6 to 10 questions per sephira
- answer scale labels
- deficiency, excess, and balance interpretation copy
- 3 to 5 practices per sephira

Once that content exists, implementation can proceed cleanly through the architecture above.

## Current Locked Slice: Malkuth Questionnaire Engine

This section defines the current implementation slice that should be treated as the active project standard until reviewed and revised.

### Slice Goal

Build one production-shaped vertical slice of the assessment engine for `MALKUTH` only so the team can validate:

- questionnaire content loading
- local persistence
- resume behavior
- deterministic scoring
- confidence-aware result language
- the repository -> use case -> ViewModel -> UI flow

The goal of this slice is to prove the real core loop in a way that can later be extended to the other nine sephirot without redesigning the engine.

### In Scope

- one sephira only: `MALKUTH`
- `6` questions for the section
- `LIKERT_5` as the only response format in this slice
- local structured questionnaire content with a `version` field
- local JSON questionnaire content with a `version` field, cached into Room after load for offline reuse
- generic domain models that are ready for future multi-sephira expansion
- Room-backed persistence for:
  - one active assessment session
  - saved responses
  - current progress position
  - derived Malkuth score
- save after every answer
- resume from the last unanswered question
- deterministic scoring across:
  - `BALANCE`
  - `DEFICIENCY`
  - `EXCESS`
- internal support for mixed or low-confidence outcomes
- minimal user-facing flow:
  - Malkuth intro
  - one-question-at-a-time questionnaire
  - progress indicator
  - Malkuth result screen
- confidence-aware UI wording such as "leans toward deficiency" when a hard classification is not strongly supported
- repository, use case, ViewModel, and scoring tests for this slice
- bilingual-ready architecture from the start

### Out Of Scope

- the other nine sephirot
- cross-sephira Tree overview results
- full assessment history
- retake flows across prior completed sessions
- Learn/About full content system
- onboarding completion for the whole app
- multiple question formats
- adaptive or branching questionnaires
- backend, sync, or auth dependency for the slice
- compare-with-previous-results features
- overgeneralized content management architecture

### Locked Decisions For This Slice

- The engine should be built in steps, but each step must contribute to one usable vertical slice.
- The data and scoring model should be generic enough for all sephirot even though only Malkuth is seeded now.
- Persistence should be real, not mocked, because local resume behavior is part of the product contract.
- The slice should save progress after every answer.
- The slice should support only one active assessment session at a time.
- The slice should expose softened user-facing language when confidence is low rather than forcing a hard label.
- The architecture should be bilingual-ready now even if content expansion continues later.
- Use `Malkuth` as the in-app spelling standard for this slice.
- Group the six questions into two themed pages of three questions each.
- Use a local JSON questionnaire source for this slice and cache parsed content into Room so the content pipeline remains compatible with offline use and future remote updates.

Temporary slice shortcuts that are not project standards:

- The current Malkuth-specific routing inside the assessment flow is temporary.
- The current behavior where finishing the Malkuth slice also completes the entire assessment session is temporary and must be replaced before multi-sephira scaling.
- The current implementation may keep `weight = 1.0` across all authored questions, but the field itself is not temporary and should remain part of the stable content contract.

### Acceptance Criteria

This slice is complete when a user can:

1. open the Malkuth assessment
2. read a short Malkuth intro
3. answer `6` Likert questions one at a time
4. leave and return to the app and resume from the last unanswered question
5. complete the section and receive a deterministic Malkuth result with confidence-aware wording

Technical completion criteria:

- questionnaire content is versioned and loaded locally
- raw responses and derived scores are persisted locally
- scoring logic is isolated from Android UI concerns and unit-testable
- repository tests verify persistence and flow emissions
- use case tests verify delegation and returned flows
- ViewModel tests verify progression, resume state, and completion mapping

### Review Trigger Before Scaling

Do not scale this pattern to the full ten sephirot until the team has reviewed:

- whether the Malkuth wording feels psychologically grounded and spiritually respectful
- whether the scoring output feels interpretable and non-pathologizing
- whether resume behavior is reliable and calm from a user experience perspective
- whether the architecture remains simple enough to extend without duplication
