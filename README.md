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

## Delivery Standard

Future work in this repository should be approached with the judgment of:

- a senior UX designer
- a senior Android developer
- a senior project manager

That means implementation decisions should balance product clarity, user trust, visual quality, technical maintainability, and realistic scope. When tradeoffs appear, prefer the option that produces a cleaner user experience and a more durable foundation for the next feature slice.
