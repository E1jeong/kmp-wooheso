# Wooheso AI Guide

## Context

This is a code-navigation and safety guide, not project history. Product context, planning, data-model detail, and decisions live in the vault-relative `Dev/Project/Personal/wooheso` wiki; resolve it through `_meta/routing-tables.md` or `obsidian-wiki-sync`, then follow the vault root `AGENTS.md`.

Report plans and results in Korean. Use the `shared/` guide before changing shared code.

## Code Map

| Module | Responsibility | Orient first | Local guide |
| --- | --- | --- | --- |
| `shared/` | Shared Compose UI, feature logic, navigation, repositories, and platform abstractions | `shared/src/commonMain/kotlin/com/sumas/wooheso/` | `shared/AGENTS.md` |
| `androidApp/` | Android application shell | `androidApp/src/main/` | None |
| `desktopApp/` | Desktop application shell | `desktopApp/src/desktopMain/` | None |
| `iosApp/` | iOS SwiftUI host application | `iosApp/iosApp/` | None |

## Change Gates

- Keep business logic, repositories, and shared UI in `shared/`; platform applications remain thin hosts.
- Use Supabase as the backend and retain Row Level Security for all persisted data.
- Never commit, expose, or change Supabase credentials without confirmed configuration and an explicit migration plan.
- Preserve authentication and private-data authorization boundaries; do not bypass ownership checks from client code.
- Keep platform-specific APIs behind `expect`/`actual` abstractions rather than runtime platform branching.

## Verify

Run the narrowest relevant command:

```powershell
.\gradlew.bat :shared:test
.\gradlew.bat assembleDebug
.\gradlew.bat check
```

Use `./gradlew` with the same tasks on Linux, WSL, or macOS.
