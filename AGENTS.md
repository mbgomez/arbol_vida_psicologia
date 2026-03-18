Project: Arbol Vida Psicologia

Purpose:
Build an Android app in Kotlin that applies the Kabbalah Tree of Life to psychological self-reflection through a guided questionnaire and locally generated profile.

Primary source of truth:
- Product spec: `docs/product_spec.md`
- README summary: `README.md`
- Content source document: `docs/Tree of life - overview - psychology.docx`

Product framing:
- This is a self-reflection and personal growth app.
- It is not a diagnostic, psychiatric, or clinical mental health product.
- Language should be psychologically grounded, spiritually respectful, and non-pathologizing.
- Results describe current tendencies, not fixed identity.
- The app should support both English and Spanish.

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
- Keep onboarding brief and focused on trust, framing, and expectations.
- Use short sephira-specific intro copy before each questionnaire section.
- Use fuller adapted content on each sephira result detail screen.
- Put broader and denser conceptual material in an optional Learn/About area.
- Adapt the source document into app-friendly English rather than copying it verbatim.
- When in doubt, prefer clear psychological phrasing first and Kabbalah terminology second.

Scoring guidance:
- Use deterministic scoring, not AI-generated interpretation.
- Score each sephira independently.
- Store raw responses and derived scores.
- Support low-confidence or mixed outcomes internally, even if the UI presents a softened dominant tendency.
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
- Evolve the current starter structure toward feature-first packages.
- Recommended package direction:
  - `feature/onboarding`
  - `feature/assessment`
  - `feature/results`
  - `feature/history`
  - `feature/settings`
  - `core/designsystem`
  - `core/navigation`
  - `core/scoring`
  - `data/local`
  - `data/repository`

Current repo note:
- Existing starter-template code such as posts, generic detail screens, and login scaffolding should not define the product architecture.
- Reuse the technical foundation where useful, but prioritize the Tree of Life assessment flow over preserving template concepts.

Data and content rules:
- The app should work offline for the core v1 experience.
- Questionnaire content should be versioned.
- Prefer local JSON or structured seed content for questions and sephira definitions in v1.
- Persist assessment sessions, answers, and sephira results locally with Room.
- Plan content and UI strings for English and Spanish from the start.
- Keep localized copy separate from scoring rules and domain logic.

Testing priorities:
- scoring engine correctness
- questionnaire progression and resume behavior
- repository persistence
- ViewModel state mapping
- critical Compose screen flows

UX guidance:
- Keep the assessment calm, clear, and mobile-friendly.
- Show progress through the ten sephirot.
- Favor soft interpretive language such as "leans toward deficiency" when confidence is low.
- Onboarding should be short.
- Richer explanation should appear in-context before each sephira and on result detail screens.
- Explanations should include:
  - healthy expression
  - deficiency pattern
  - excess pattern
  - suggested practices

When making implementation decisions:
- Prefer choices that support clear interpretation, local reliability, and testability.
- If the codebase and the spec conflict, follow `docs/product_spec.md`.
