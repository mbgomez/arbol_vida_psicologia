# Tester Distribution And Observability

This document locks the first production-shaped `Phase 1.5` implementation standard for `Numyah Mind`.

## Purpose

Use internal or closed testing to learn from real tester usage without weakening the app's local-first trust model.

## Current Implementation Standard

- Observability is gated by Gradle properties instead of being always on.
- Core app code depends on an app-owned telemetry interface, not directly on Firebase SDK calls.
- Debug builds still log telemetry locally through Timber so event wiring can be reviewed without sending remote data.
- When Firebase dependencies and plugins are enabled, the same app-owned telemetry layer forwards the approved event set and non-fatal reports to Firebase.
- Privacy-facing copy must explain that tester builds may send limited crash and flow signals.

## Gradle Flags

- `numyah.enableTesterObservability=true`
  - enables the app's tester-observability code path
- `numyah.enableFirebase=true`
  - applies the Google Services and Crashlytics Gradle plugins
  - adds Firebase Analytics and Crashlytics dependencies

Recommended tester build usage:

- use both flags together for internal or closed testing
- leave both flags off for normal local work unless you are validating observability

## Event Taxonomy

The current locked event set is:

- `onboarding_completed`
  - `completion_method`: `finish` or `skip`
- `assessment_entry`
  - `source`: `home`, `assessments`, or `results`
  - `mode`: `start`, `resume`, or `start_fresh`
- `assessment_start_fresh_confirmed`
  - `source`: `home`, `assessments`, or `results`
- `assessment_completed`
  - `completed_sephirot_count`
- `results_detail_opened`
  - `sephira_id`
  - `session_scope`: `latest` or `saved`
- `history_session_opened`
- `learn_section_opened`
  - `course_id`
  - `section_id`
  - `section_order`
- `settings_changed`
  - `setting_key`: `language`, `theme`, or `honesty_notice`
  - `setting_value`
- `onboarding_replayed`

## Non-Fatal Reporting Scope

Recoverable failures are currently reported for:

- assessment load
- answer save
- assessment continue
- assessment go-back
- results load
- sephira detail load
- history load
- learn catalog load
- learn course load
- learn section load
- learn section completion

## Privacy Boundary

The approved telemetry boundary for this slice is:

- send crash and recoverable-failure signals
- send the small flow event set above
- do not send answer selections
- do not send written reflection copy
- do not send saved pole scores or result interpretations
- do not expand into broad behavioral tracking without a new explicit product decision

## Manual Firebase Setup

1. Create or open a Firebase project for the Android package `com.netah.hakkam.numyah.mind`.
2. Add an Android app with that exact package name.
3. Enable Analytics.
4. Enable Crashlytics.
5. Download `google-services.json`.
6. Place it at `app/google-services.json`.
7. Build with both Gradle flags enabled so the Firebase plugins and dependencies are applied.

## Manual Play Testing Setup

1. Create a signed release or release-candidate build with both Gradle flags enabled.
2. Upload the app bundle to a Play internal testing or closed testing track.
3. Add tester accounts or groups.
4. Publish the testing release.
5. Verify that the Play listing or tester notes mention limited crash and product-flow telemetry for tester builds.

## Verification Checklist

Manual verification should confirm:

- onboarding completion produces a local telemetry log entry
- start, resume, and fresh-start flows produce telemetry log entries
- results detail opens log a `results_detail_opened` event
- opening a saved history session logs `history_session_opened`
- opening a Learn section logs `learn_section_opened`
- language, theme, and honesty-notice changes log `settings_changed`
- a forced recoverable failure appears as a non-fatal record when Firebase is enabled
- Firebase DebugView shows the approved event names only
- Crashlytics receives a test crash and a recoverable non-fatal report
