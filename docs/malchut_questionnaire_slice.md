# Malchut Questionnaire Engine Slice

## Purpose

This document defines the current working agreement for the first assessment-engine implementation slice.

The slice is intentionally narrow: ship one complete, production-shaped Malchut flow before expanding to the other sephirot.

This is not the full v1 scope. It is the foundation slice used to validate architecture, scoring, persistence, and user experience.

## Why This Slice Exists

The repository is still being reshaped from starter-template structure into the actual product architecture for `Numyah Mind`.

Building all ten sephirot at once would combine too many moving parts:

- new content structure
- scoring rules
- Room persistence
- resume behavior
- bilingual content handling
- new assessment UI flow

This slice reduces risk by proving the real product loop for one sephira in a way that is small enough to review well.

## In Scope

- one active sephira: `MALKUTH`
- `6` questions
- `LIKERT_5` response scale only
- local structured seed content with questionnaire versioning
- local JSON questionnaire content parsed through the app content layer and cached into Room for offline reuse
- generic domain contracts prepared for future multi-sephira expansion
- one active assessment session at a time
- save progress after every answer
- resume from the last unanswered question
- deterministic scoring for:
  - `BALANCE`
  - `DEFICIENCY`
  - `EXCESS`
- internal mixed or low-confidence support
- minimal end-to-end UI:
  - Malchut intro
  - question flow
  - progress state
  - result screen
- confidence-aware user-facing wording
- tests across repository, use case, ViewModel, and scoring layers

## Out Of Scope

- all other sephirot
- final ten-sephirot results dashboard
- full onboarding experience
- Learn/About full content system
- assessment history
- retake from completed historical sessions
- multiple question formats
- adaptive questionnaire logic
- backend sync
- required sign-in
- comparison between assessment runs

## Locked Decisions

- Build this work in steps, but keep each step moving toward one coherent vertical slice.
- Use generic domain models now so Malchut is an implementation slice, not a dead-end prototype.
- Use real Room persistence now because resume behavior is part of the user contract.
- Keep scoring deterministic and isolated from Android dependencies.
- Use softened result language when the result is low-confidence.
- Keep copy and localization separate from scoring logic.
- Treat bilingual readiness as a standard even if content rollout is phased.
- Use a mock-endpoint style local JSON questionnaire source and save the parsed content into Room so the same slice can support offline reads and future remote-content evolution.
- Use `Long` for runtime-created records such as sessions, `String` for authored content identifiers such as questions and pages, and enums for closed questionnaire vocabularies.
- Use `Malkuth` as the in-app spelling standard for this project slice.
- Group the questions into two themed pages of three questions each.
- Keep questions authored in thematic order rather than randomized for this slice.

## Expected User Outcome

At the end of this slice, a user should be able to:

1. enter the Malchut assessment flow
2. read a short intro to Malchut
3. answer `6` questions one at a time
4. close and reopen the app and continue where they left off
5. finish and see a Malchut result such as:
   - balanced
   - leans toward deficiency
   - leans toward excess

## Locked Step 1 Questionnaire Contract

### Working Definition

`Malkuth` represents your relationship with the material world. In psychological terms, it reflects how you relate to the body, daily life, possessions, and practical reality.

### Response Format

This slice uses one response format only: `LIKERT_5`.

Answer labels:

- `Strongly disagree`
- `Disagree`
- `Neither agree nor disagree`
- `Agree`
- `Strongly agree`

### Page Structure

The questionnaire is grouped into two pages with three questions each:

- page 1: money, riches, and possessions
- page 2: body, health, and diet

Questions should remain grouped by theme so users can notice patterns in their own responses. Scoring should still happen per question rather than per page.

### Question Set

#### Page 1: Money, Riches, And Possessions

1. `I spend a great deal of my time and energy trying to accumulate money or possessions.`
Pole: `EXCESS`

2. `I avoid taking care of my money or possessions because I feel other parts of life matter more.`
Pole: `DEFICIENCY`

3. `I appreciate what I have while still looking for practical ways to improve my material stability.`
Pole: `BALANCE`

#### Page 2: Body, Health, And Diet

4. `I am very strict with my diet, exercise, or health routines because I feel my body must be carefully controlled.`
Pole: `EXCESS`

5. `I focus more on enjoying the moment than on keeping track of my diet, exercise, or physical health.`
Pole: `DEFICIENCY`

6. `I try to care for my body through balanced habits and a realistic weekly routine.`
Pole: `BALANCE`

### Current Scoring Assumption

For this first slice, each question maps primarily to one pole only:

- one balance-oriented item per page
- one deficiency-oriented item per page
- one excess-oriented item per page

All six questions should be equally weighted unless a later review explicitly changes that rule.

## Suggested Implementation Steps

1. Lock the engine contract.
Define the Malchut question count, answer scale, scoring thresholds, persistence expectations, and completion/resume rules.

2. Build the domain and content model.
Create the generic assessment models and seed only Malchut content for this slice.

3. Implement persistence and scoring.
Add Room entities, DAO, repository behavior, scoring logic, and tests.

4. Add use cases and ViewModel state.
Expose flow-based loading, answer saving, progress resumption, and section completion.

5. Build the minimal UI flow.
Implement the intro, questionnaire, progress, and result screens needed to exercise the engine.

6. Review before scaling.
Validate language, score feel, reliability, and extension readiness before adding more sephirot.

## Acceptance Criteria

Product acceptance:

- the flow feels calm, clear, and mobile-friendly
- the result language feels grounded and non-pathologizing
- the user can resume without confusion

Technical acceptance:

- questionnaire content is versioned and local
- questionnaire content is parsed from the local JSON source and cached into Room
- every answer is saved locally
- the active session restores reliably
- score calculation is deterministic and unit-tested
- repository, use case, and ViewModel coverage exists for the slice
- Room-backed repository verification should prefer local Robolectric tests unless device-only behavior is required

## Review Questions

Use these questions before expanding the pattern:

- Does Malchut feel like the real product rather than a technical demo?
- Does the scoring output feel understandable and fair?
- Is the language soft enough when confidence is low?
- Is the implementation easy to extend to the next sephira without rework?
