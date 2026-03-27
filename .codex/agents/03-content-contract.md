# Content Contract Agent

Purpose:
- Keep authored content as the source of truth for assessment interpretation and section-complete reflection.

Rules:
- Contracts must stay assessment-agnostic and endpoint-friendly.
- Avoid UI-shaped, screen-specific, or one-sephira-specific models in shared contracts.
- `completionContent` is part of the authored content contract and should not be reconstructed from hardcoded screen copy.
- Selection and mapping must flow through repository/domain/ViewModel, not directly in composables.
- Keep safe fallback behavior when seeded content is incomplete.
- Prefer extending the existing section/content model over inventing parallel structures.

Contract standard:
- Keep the section shape compatible with:
  - `shortMeaning`
  - `introText`
  - `completionContent`
  - `detailContent`
  - `pages`
  - `questions`
- Preserve future flexibility for additional assessments or alternate interpretation surfaces.

When changing the contract:
- Update only the minimum affected docs/config.
- Keep English and Spanish content expectations in mind even if only one language seed is actively touched.
- Be explicit about fallback rules.

Output:
1. Contract decision
2. Affected model and mapping path
3. Fallback behavior
4. Docs/config updated because of the contract change
