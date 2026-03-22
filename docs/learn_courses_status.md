# Learn Courses Status

This document captures the current state of the Learn/Courses feature, the decisions that were made, what has already been built, what is still left, and the recommended next steps.

## What Was Built

The Learn feature is now a real product slice with:

- local JSON-backed course content
- bilingual course loading
- route flow for:
  - course list
  - course overview
  - section reader
- gated section progression using local app preferences
- a more editorial, book-like section reader
- stronger course and chapter hierarchy polish across list, detail, and reader surfaces
- route behavior that returns section navigation to the course flow instead of stacking every visited chapter
- explicit return-to-course actions in the reader, including the end of the available chapter sequence
- stable Compose test hooks for Learn user-critical interactions
- Compose UI coverage for the core Learn screens and progression controls
- Kabbalistic voice locked for Learn content

## Main Decisions Made

### Content Voice

- Learn is the place for the authored Kabbalistic teaching voice.
- Assessment and onboarding remain in the softer, more product-shaped psychological voice.
- Learn content can receive light cleanup for readability and mobile structure, but should not be rewritten into the same tone as assessment content.
- Course descriptions and section summaries should describe the reading material itself, not implementation state or future build plans.
- Seeded section bodies should stay as close as practical to the source document, with only minor cleanup for spelling, structure, and bilingual readability.

### Course Structure

- Learn content stays local-first in JSON for now.
- The first course is structured as:
  - one introduction section
  - one section per sephira
- Current available sections:
  - Introduction
  - Malkuth
  - Yesod
- Total planned sections in the first course:
  - 11

### Progression Rule

- Section 1 starts unlocked.
- Each later section unlocks only after the immediately previous section is marked finished.
- Completion is currently stored locally in app preferences.

### Reader Direction

- The section reader moved away from stacked cards.
- The current standard is a more book-like reading experience:
  - chapter-style header
  - continuous reading page
  - previous and next chapter controls
  - footer actions separated from the reading body
- Chapter-to-chapter navigation should replace the current reader entry instead of creating a long back stack of visited sections.
- The reader should always provide a clear route back to the course overview, and the end of the available sequence should provide an explicit return-to-course action.

## Files That Matter Most

### Learn Content

- [tree_of_life_courses.json](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\assets\courses\tree_of_life_courses.json)

### Learn Data / Domain

- [LearningContent.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\domain\model\LearningContent.kt)
- [JsonLearningContentDataSource.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\data\local\content\JsonLearningContentDataSource.kt)
- [LocalLearningContentRepository.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\data\repository\LocalLearningContentRepository.kt)
- [LearningContentUseCases.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\domain\usecase\LearningContentUseCases.kt)

### Learn Progress

- [LocalAppPreferencesRepository.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\data\repository\LocalAppPreferencesRepository.kt)
- [AppPreferencesUseCases.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\domain\usecase\AppPreferencesUseCases.kt)

### Learn UI / Navigation

- [LearnViewModel.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\viewmodel\LearnViewModel.kt)
- [LearnScreen.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\ui\screen\LearnScreen.kt)
- [MainNav.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\ui\nav\MainNav.kt)
- [Routes.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\java\com\netah\hakkam\numyah\mind\ui\nav\route\Routes.kt)

### Strings / Standards

- [strings.xml](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\res\values\strings.xml)
- [strings.xml](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\main\res\values-es\strings.xml)
- [README.md](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\README.md)
- [product_spec.md](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\docs\product_spec.md)
- [AGENTS.md](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\AGENTS.md)

## Tests Already Added

- [LocalLearningContentRepositoryTests.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\test\java\com\netah\hakkam\numyah\mind\data\repository\LocalLearningContentRepositoryTests.kt)
- [LearningContentUseCaseTests.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\test\java\com\netah\hakkam\numyah\mind\domain\usecase\LearningContentUseCaseTests.kt)
- [LearnViewModelTests.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\test\java\com\netah\hakkam\numyah\mind\viewmodel\LearnViewModelTests.kt)
- [LocalAppPreferencesRepositoryTests.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\test\java\com\netah\hakkam\numyah\mind\data\repository\LocalAppPreferencesRepositoryTests.kt)
- [AppPreferencesUseCaseTests.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\test\java\com\netah\hakkam\numyah\mind\domain\usecase\AppPreferencesUseCaseTests.kt)
- [LearnScreenTest.kt](C:\Users\Miguel\AndroidStudioProjects\arbol-vida-psicologia\app\src\androidTest\java\com\netah\hakkam\numyah\mind\ui\LearnScreenTest.kt)

## What Is Left

### Missing Tests

The main missing Learn Compose coverage has now been added for:

- course list seeded entry rendering
- course screen available and locked section states
- section reader title and body rendering
- unfinished section completion action
- completed section next-chapter action
- locked section surface

Any further UI tests should stay focused on newly locked behavior rather than broad polished-layout snapshots.

### Manual Review

The reader still needs manual review on device for:

- chapter header feel
- long paragraph rhythm
- previous and next controls after the route-stack fix
- locked and unlocked flow
- dark and light theme readability
- catalog and course-card density on smaller devices

### Future Enhancement Layer

Not part of the current pass, but already identified:

- optional hero and inline image support
- more book-like visual polish if needed after manual review

## Suggestions For The Next Thread

Recommended order:

1. Run the Learn-related tests first.
2. Manually review the Learn reader and course flow on device.
3. Fix any remaining UI issues discovered during review.
4. Add any missing assertions for newly reviewed behavior.
5. If the Learn standard changes again, update docs in the same pass.

## Good Prompt To Continue

"Continue the Learn/Courses feature using `docs/learn_courses_status.md` as the source of truth. Start with the missing Learn Compose UI tests, then review and polish the current book-like reader."
