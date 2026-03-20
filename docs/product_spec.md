# Product Spec: Tree of Life Psychology App

## 1. Short Product Vision

Build a reflective Android app that translates the Kabbalah Tree of Life into a practical psychology experience. Users complete a structured questionnaire across the ten sephirot and receive a personal profile showing where each sephira appears balanced, deficient, or excessive, along with clear explanations and gentle practices for integration.

The product should feel insightful, grounded, and non-pathologizing. It is a self-reflection tool, not a diagnostic or clinical mental health product.

Canonical product name: `Numyah Mind`.

The app's educational content should be grounded in the project's own Kabbalah reference document in [Tree of life - overview - psychology.docx](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\Tree%20of%20life%20-%20overview%20-%20psychology.docx), adapted into app-friendly copy rather than pasted directly.

The app should launch with bilingual support for English and Spanish.

## 2. User Flow

### Primary Flow

1. User opens the app.
2. User sees short onboarding that explains the Tree of Life framework, reflection-focused positioning, privacy, and time commitment.
3. User starts a new assessment.
4. Before each sephira section, the user sees a short intro that explains the sephira in practical psychological language.
5. User answers questionnaire sections for each sephira in sequence.
6. After each section, progress is saved locally.
7. When the final section is complete, the scoring engine evaluates each sephira.
8. User lands on the results overview screen with all ten sephirot and their states.
9. User taps any sephira to open a deeper detail screen.
10. User reviews psychological meaning, signs of deficiency or excess, strengths of balance, and suggested practices.
11. User can open an optional Learn/About area for deeper Kabbalah context and longer educational content.
12. User can save the result, retake the assessment later, or compare with a previous run in a future version.

### Secondary Flows

- Resume interrupted assessment from the last unanswered question.
- Retake assessment from the results screen.
- Review previous completed assessments from history.
- Open a sephira detail screen directly from a saved assessment.
- Open a Learn/About section without starting the assessment.

### UX Principles

- One question at a time or one short cluster at a time.
- Clear progress and section context.
- Language should be psychologically accessible, spiritually respectful, and free of deterministic claims.
- Results should emphasize tendencies, not labels.
- Onboarding should be a dedicated first-run experience rather than a reused in-app shell screen.
- Onboarding should be trust-building and substantial enough to orient the user, but still lighter than the deeper educational material in Learn/About.
- Onboarding copy must be written from the user's perspective and should never read like implementation notes, roadmap language, or builder-facing commentary.
- Deeper educational material should appear in context, especially before and after each sephira assessment.
- All core user-facing flows should be available in English and Spanish.
- The first-run experience should feel polished and product-grade, with intentional layout, strong hierarchy, and a professional visual treatment.
- Refactored Compose screens should keep resource lookup and visual tokens close to the UI section that uses them, so extracted sub-composables remain self-contained and easier to maintain.
- Shared dimension tokens should be grouped by semantic role, not just by matching numeric value.

## 3. Screen List

### Launch / Setup

- Splash or startup routing screen
- Optional sign-in gate if the existing auth foundation is kept, though this should be disabled for v1 unless there is a real backend need
- Dedicated onboarding flow with its own visual treatment and without the standard in-app toolbar behavior

### Assessment

- Assessment home screen
- Sephira intro screen for the current section
- Question screen
- Section completion micro-summary screen
- Resume assessment prompt

### Results

- Results overview screen with all ten sephirot
- Sephira detail screen
- Suggested practices screen or practices section inside detail
- Assessment history screen

### Support Screens

- Learn / About the Tree of Life screen
- Privacy and methodology screen
- Settings screen

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
  - `MALKHUT`
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

The document should be treated as the content source, but the app copy should be edited for clarity, brevity, and consistency.

User-facing copy rules:

- Prefer direct user-oriented language such as "you will reflect on" or "your results may show" rather than builder-oriented language such as "the app will later include."
- Avoid copy that references implementation state, roadmap progress, or development placeholders in production-facing flows.
- Keep onboarding understandable to a first-time user without requiring product or technical context.
- Production-grade screens should externalize stable strings and dimension values early so localized copy, polish passes, and layout tuning remain low-risk.

### Localization Requirement

The product should support:

- English
- Spanish

Localization rules:

- All onboarding, questionnaire, results, practices, and Learn/About content should be available in both languages.
- Content architecture should separate copy from scoring logic.
- The source document may remain as the authoring base, but app-ready copy should be produced as localized English and Spanish strings.
- Question meaning should remain equivalent across languages, especially where scoring depends on nuanced wording.

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

### Per-Sephira Calculation

For each sephira:

1. Sum all weighted answer contributions into three buckets: `balance`, `deficiency`, and `excess`.
2. Normalize by the maximum possible score for that section.
3. Compute percentages for the three poles.
4. Choose the dominant pole using threshold rules.

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

Compose screen construction guidance:

- A screen should primarily coordinate layout and state, not hold every visual section inline.
- Extract smaller composable sections when they represent distinct responsibilities such as top bars, hero media, body copy, progress indicators, cards, and action areas.
- Keep reusable visual building blocks easy to share across screens.
- Prefer decomposition that improves readability and reuse without fragmenting simple logic into unnecessary wrappers.
- For multi-step onboarding or intro flows, keep page definitions explicit so text, artwork, and behavior can evolve together without spreading page logic across the layout tree.

## Recommended Next Step After This Spec

Before coding, define the v1 content set in detail:

- final wording for each sephira definition
- 6 to 10 questions per sephira
- answer scale labels
- deficiency, excess, and balance interpretation copy
- 3 to 5 practices per sephira

Once that content exists, implementation can proceed cleanly through the architecture above.

## Current Locked Slice: Malchut Questionnaire Engine

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
