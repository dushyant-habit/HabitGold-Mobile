# HabitGold Git Workflow

This file defines how we manage branches, commits, and documentation while rebuilding HabitGold in KMP.

The goal is simple:

- every branch name should clearly describe the task
- every commit should represent one logical unit of work
- every meaningful task should leave a documentation trail when needed

## Branch Naming

Use the following pattern for new work:

```text
codex/<type>/<short-kebab-description>
```

Examples:

- `codex/feature/auth-mvi-cleanup`
- `codex/feature/home-shell-sections`
- `codex/fix/session-expiry-routing`
- `codex/docs/migration-roadmap-update`
- `codex/refactor/network-error-mapper`
- `codex/test/auth-repository-coverage`

### Allowed Types

- `feature`
- `fix`
- `refactor`
- `test`
- `docs`
- `chore`

## Commit Standard

Use conventional-commit style messages:

```text
<type>(<scope>): <summary>
```

Examples:

- `feat(auth): add startup-aware auth shell routing`
- `fix(network): clear session on authenticated 401`
- `refactor(ui): extract shared design-system components`
- `test(session): add restore and logout coverage`
- `docs(roadmap): track localization and git workflow`
- `chore(build): add ktor mock client for common tests`

### Allowed Commit Types

- `feat`
- `fix`
- `refactor`
- `test`
- `docs`
- `chore`

### Commit Rules

- one commit should solve one logical problem
- do not mix docs, refactor, feature work, and unrelated fixes in one commit unless they are inseparable
- tests should be committed with the code they validate whenever practical
- if a change introduces or alters behavior, the commit should include or update tests unless truly blocked
- documentation updates should be included when architecture, standards, workflow, or progress tracking changes

## Commit Granularity

Preferred granularity:

- one task -> one commit
- one bug fix -> one commit
- one focused refactor -> one commit
- one documentation-only change -> one commit

Avoid:

- giant “phase” commits that hide many unrelated changes
- mixing feature work from different domains in one commit
- mixing formatting-only edits into behavior commits unless unavoidable

## Documentation Rules

Update docs when:

- a phase starts or completes
- a major architectural decision changes
- a new workflow rule is introduced
- a hidden subflow or migration risk is discovered
- localization, testing, navigation, security, or environment strategy changes

Primary docs:

- [KMP_PROJECT_GUIDE.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROJECT_GUIDE.md:1)
- [KMP_MIGRATION_ROADMAP.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_MIGRATION_ROADMAP.md:1)
- [KMP_PROGRESS_TRACKER.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PROGRESS_TRACKER.md:1)
- [KMP_PRE_MIGRATION_AUDIT.md](/Users/dushyantmainwal/AndroidStudioProjects/HabitGold_Mobile/KMP_PRE_MIGRATION_AUDIT.md:1)

## Working Agreement For This Repo

From this point forward:

- new work should start on a properly named branch
- commits should be made in logical slices, not as one giant checkpoint
- progress and standards docs should stay in sync with meaningful architecture/process changes
- if the working tree contains mixed unrelated changes, do not force fake “clean” commits retroactively without first choosing a safe split strategy

## Current Reality

The current working tree already contains multi-phase uncommitted work.

That means the safest next git step is one of:

1. create one baseline checkpoint commit for the current integrated state, then continue with granular commits from there
2. split the current tree into a few logical commits by area such as scaffold, session/network, app shell, and design system

Do not pretend this is already clean task-by-task history.
