# shared Module Guide

## Scope

Shared Compose UI, feature logic, navigation, repositories, domain models, and platform abstractions for Android, desktop, and iOS hosts.

## Orient First

- `shared/src/commonMain/kotlin/com/sumas/wooheso/App.kt`
- `shared/src/commonMain/kotlin/com/sumas/wooheso/core/`
- `shared/src/commonMain/kotlin/com/sumas/wooheso/data/`
- `shared/src/commonMain/kotlin/com/sumas/wooheso/features/`
- `shared/src/commonMain/kotlin/com/sumas/wooheso/navigation/`

## Boundary & Architecture Constraints

Keep Supabase access in repositories and map wire DTOs to UI-facing models there. Features communicate through centralized navigation rather than direct feature imports. ViewModels expose state and one-shot effects; composables render state and emit intents.

## Change Gates

- Do not call Supabase directly from screens or ViewModels.
- Do not use Supabase DTOs directly in composables.
- Do not introduce feature-to-feature imports; route cross-feature transitions through navigation.

## Verify

```powershell
.\gradlew.bat :shared:desktopTest
```

Use `./gradlew` with the same task on Linux, WSL, or macOS. For Android integration or repository-wide changes, follow the root guide's broader verification.
