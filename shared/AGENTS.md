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

Keep Supabase access in repositories and map wire DTOs to UI-facing models there. Features communicate through centralized navigation rather than direct feature imports. ViewModels expose state and one-shot effects; composables render state and emit intents. Use `expect`/`actual` for platform APIs.

## Change Gates

- Do not call Supabase directly from screens or ViewModels.
- Do not use Supabase DTOs directly in composables.
- Do not put business logic, repositories, or ViewModels in platform host modules.
- Do not introduce feature-to-feature imports; route cross-feature transitions through navigation.
- Do not replace `expect`/`actual` abstractions with runtime platform checks.

## Verify

```powershell
.\gradlew.bat :shared:test
.\gradlew.bat assembleDebug
.\gradlew.bat check
```

Use `./gradlew` with the same tasks on Linux, WSL, or macOS.
