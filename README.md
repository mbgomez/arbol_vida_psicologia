# Arbol Vida Psicologia

Android app concept for exploring the Kabbalah Tree of Life through a psychological self-reflection journey.

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

- short onboarding for framing and expectations
- short sephira intros before each questionnaire section
- richer per-sephira explanations on the result detail screens
- optional deeper reading in a Learn/About area

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

Keep the repo as a single `:app` module for now, but move from the current generic starter structure toward feature-based packages:

- `feature/onboarding`
- `feature/assessment`
- `feature/results`
- `feature/sephira_detail`
- `core/designsystem`
- `core/navigation`
- `core/database`
- `core/scoring`

This preserves the current stack while making the codebase easier to grow.
