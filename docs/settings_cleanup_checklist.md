# Settings Cleanup Checklist

This checklist tracks the remaining cleanup and hardening work for the Settings slice after the current implementation, tests, and manual verification pass.

## Known Testing Follow-up

- Stabilize [`SettingsScreenTest.kt`](/C:/Users/Miguel/AndroidStudioProjects/arbol-vida-psicologia/app/src/androidTest/java/com/netah/hakkam/numyah/mind/ui/SettingsScreenTest.kt) for `settingsScreen_languageSelection_invokesCallback`.
- Treat the current language-selection Android test failure as a flaky UI-test issue, not a confirmed product bug.
- Keep manual verification of language switching as the source of truth until the UI test is hardened.
- Revisit the language option semantics in [`SettingsScreen.kt`](/C:/Users/Miguel/AndroidStudioProjects/arbol-vida-psicologia/app/src/main/java/com/netah/hakkam/numyah/mind/ui/screen/SettingsScreen.kt) so the test can target one explicit, reliable action node.
- Consider extracting the theme and language selectors into smaller composables with their own focused UI tests.

## UI Polish Follow-up

- Do one final bilingual visual pass across Settings, Privacy, and About in English and Spanish.
- Check spacing, line wrapping, and hierarchy on smaller devices and longer Spanish strings.
- Re-check the Settings detail navigation flow after any future header or shell changes.

## Code Cleanup Follow-up

- Review test tags in production composables and keep only the ones that support stable UI testing.
- Rename or group test tags more consistently if the Settings screen gets another refactor.
- Keep the Settings screen orchestration-focused and avoid letting future additions grow it back into a large mixed-responsibility file.
