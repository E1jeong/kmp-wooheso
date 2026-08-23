# 우회소 (wooheso) AI Guide

## Start Here

This guide is a **navigation aid and safety guard**, not a knowledge archive. For background, architecture rationale, and decision history, read the Obsidian wiki.

- **Wiki SSOT:** `Dev/Project/Personal/wooheso/` (vault `C:\Users\sumas\OneDrive\Desktop\dev\10.obsidian`)
- **Session read order:**
  1. `README.md` (this repo)
  2. Wiki [`handoff.md`](file:///C:/Users/sumas/OneDrive/Desktop/dev/10.obsidian/Dev/Project/Personal/wooheso/handoff.md) — last session state, next starting point
  3. Wiki [`issues/needs-verification.md`](file:///C:/Users/sumas/OneDrive/Desktop/dev/10.obsidian/Dev/Project/Personal/wooheso/issues/needs-verification.md) — open questions and unresolved items
- **Report language:** English for code and commit messages; Korean for user-facing UI strings.
- **Production repo:** `https://github.com/E1jeong/kmp-wooheso` (`main` branch)
- **Tech identifier:** `wooheso` / `kmp-wooheso`; display name: 우회소

## Product and Runtime Map

```
┌──────────────────────────────────────────────────────────────────┐
│                        User Device                               │
│                                                                  │
│  ┌─────────────┐  ┌─────────────────────────────────────────┐   │
│  │ androidApp  │  │              shared (KMP)               │   │
│  │ (thin shell)│──│  navigation/ ──▶ features/              │   │
│  └─────────────┘  │                  ├── feed/    (피드)     │   │
│  ┌─────────────┐  │                  ├── product/ (상세/등록)│   │
│  │ desktopApp  │──│                  ├── company/ (프로필)   │   │
│  │ (thin shell)│  │                  ├── auth/    (로그인)   │   │
│  └─────────────┘  │                  └── saved/   (저장목록) │   │
│  ┌─────────────┐  │  core/                                   │   │
│  │   iosApp    │──│  ├── supabase/  (Auth, Postgrest, Storage)│  │
│  │ (Xcode)     │  │  ├── designsystem/ (Theme, Glassmorphism)│  │
│  └─────────────┘  │  ├── media/     (VideoPlayer expect/act) │  │
│                   │  └── util/      (URL/Share launchers)    │  │
│                   │  data/                                    │  │
│                   │  ├── model/supabase/ (Supabase DTOs)       │  │
│                   │  ├── model/     (Domain models + mock)    │  │
│                   │  └── repository/(Postgrest CRUD)          │  │
│                   └──────────────────────────────────────────┘   │
│                              │                                   │
│                              ▼                                   │
│                   ┌──────────────────┐                           │
│                   │    Supabase      │                           │
│                   │  (Seoul region)  │                           │
│                   │  Auth · Postgrest│                           │
│                   │  Storage (S3)    │                           │
│                   └──────────────────┘                           │
└──────────────────────────────────────────────────────────────────┘
```

## Module/Domain Map and First Reads

| Module | Ownership | First entrypoint | Wiki topic |
| --- | --- | --- | --- |
| `shared/` | All business logic, UI, and data | [`App.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/App.kt) | [[technical/kmp-masterplan]] |
| `shared/.../core/supabase/` | Supabase client singleton, auth | [`SupabaseClientProvider.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/core/supabase/SupabaseClientProvider.kt) | [[data/data-model-draft]] |
| `shared/.../core/media/` | Video player expect/actual | [`VideoPlayer.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/core/media/VideoPlayer.kt) | [[technical/kmp-masterplan#5.1]] |
| `shared/.../core/designsystem/` | Theme, colors, glassmorphism | [`Theme.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/core/designsystem/Theme.kt) | [[features/product-brand-exhibition]] |
| `shared/.../data/` | DTOs, domain models, repositories | [`SupabaseProductRepository.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/data/repository/SupabaseProductRepository.kt) | [[data/data-model-draft]] |
| `shared/.../features/` | MVI screens (presentation/) | [`FeedScreen.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/features/feed/presentation/FeedScreen.kt) | [[technical/kmp-masterplan#6]] |
| `shared/.../navigation/` | Type-safe NavHost + routes | [`WoohesoNavHost.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/shared/src/commonMain/kotlin/com/sumas/wooheso/navigation/WoohesoNavHost.kt) | [[technical/kmp-masterplan#3]] |
| `androidApp/` | Thin shell — `MainActivity` only | [`MainActivity.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/androidApp/src/main/java/com/sumas/wooheso/MainActivity.kt) | — |
| `desktopApp/` | Thin shell — `Main.kt` only | [`Main.kt`](file:///C:/Users/sumas/OneDrive/Desktop/dev/6.project/kmp-wooheso/desktopApp/src/main/kotlin/com/sumas/wooheso/Main.kt) | — |
| `iosApp/` | Xcode SwiftUI wrapper (no Gradle module) | `ContentView.swift` | — |

## Task Router

| Request intent | Read first (wiki) | Primary source entrypoint | Trace path |
| --- | --- | --- | --- |
| Feed UX / video player | [[technical/kmp-masterplan#5.1]] | `features/feed/presentation/FeedScreen.kt` | → `ProductFeedCard` → `core/media/VideoPlayer` |
| Product detail / inquiry | [[features/product-brand-exhibition]] | `features/product/presentation/ProductDetailScreen.kt` | → `core/util/PlatformLauncher` (URL) |
| Company profile | [[features/product-brand-exhibition]] | `features/company/presentation/CompanyProfileScreen.kt` | → `SupabaseCompanyRepository` |
| Product registration / upload | [[technical/kmp-masterplan#Phase 3]] | `features/product/registration/ProductRegistrationScreen.kt` | → `SupabaseProductRepository` → `SupabaseStorageRepository` |
| Company onboarding | [[technical/kmp-masterplan#Phase 3]] | `features/company/registration/CompanyRegistrationScreen.kt` | → `SupabaseCompanyRepository` |
| Google Sign-In / auth | [[data/data-model-draft]] | `features/auth/` | → `SupabaseClientProvider` → Credential Manager (Android) |
| Saved products | [[data/data-model-draft]] | `features/saved/SavedListScreen.kt` | → `SavedProductRepository` |
| Navigation / routing | [[technical/kmp-masterplan#3]] | `navigation/Routes.kt` + `WoohesoNavHost.kt` | → `WoohesoRoute` sealed interface |
| Theme / design system | [[features/product-brand-exhibition]] | `core/designsystem/Theme.kt` | → `AppColors` → `Glassmorphism` |
| Supabase config / RLS | [[data/data-model-draft]] | `core/supabase/SupabaseClientProvider.kt` | → Supabase Dashboard |
| Inquiry conversion tracking | [[technical/kmp-masterplan#Phase 2]] | `data/repository/ConversionTracker.kt` | → `SupabaseProductRepository.incrementInquiryClick` |

## Immutable Boundaries and Change Gates

These rules are **non-negotiable**. Do not override, remove, or work around them without explicit user approval.

### Authentication & Authorization
- **Google Sign-In only.** Do not add Apple, email, or any other auth provider. Apple login is deferred to iOS release.
- **No `role` field in `users`.** Supplier identity is determined solely by `companies.owner_id == auth.uid()` RLS. Never add a role/type column.
- **Guest mode default.** Feed browsing, product detail, and 1-second inquiry must work without login. Only save and product registration require authentication.

### Data & Pricing
- **`priceType` enum is `fixed` or `inquiry` only.** `price` column exists only when `priceType = fixed`. Do not add `free`, `negotiable`, or other types.
- **No public like counter.** Saves are private (`saved_products.user_id = auth.uid()`). Never expose save counts or add a public likes table.
- **Inquiry via external URL only.** No in-app chat or inquiry database. Each product's `inquiryUrl` opens externally. Log clicks via `inquiry_click_count` increment.

### Feed UX
- **No prices on feed.** Prices appear only on `ProductDetailScreen`. Never show price badges, labels, or ranges on feed cards.
- **No heavy text on feed.** Feed is media-first (video/image). No descriptions, bullet points, or long text on feed cards.
- **Video lifecycle.** Only the `isCurrentPage` card plays video. All others must be paused. Do not auto-play multiple videos.
- **Frosted glassmorphism header.** Feed header uses `frostedGlass()` modifier. Do not replace with opaque or transparent app bar.

### Infrastructure
- **Supabase only.** Do not add Firebase, AWS Amplify, or any other backend. The Supabase instance is deployed in Seoul (`ap-northeast-2`).
- **Supabase credentials are in `SupabaseClientProvider.kt`.** Do not move them to `local.properties` or environment variables without migration plan.
- **RLS enforced.** All Supabase tables have Row Level Security. Do not create tables without RLS policies.
- **Media constraints.** Video max 30 seconds / 30 MB. Thumbnail auto-captured on upload. Do not change limits without cost analysis.

### Architecture
- **All business logic in `shared/`.** Platform targets (`androidApp/`, `desktopApp/`, `iosApp/`) are thin shells calling `WoohesoApp()`. Do not put business logic in platform modules.
- **MVI pattern.** Each feature ViewModel uses `StateFlow<UiState>` + `SharedFlow<SideEffect>` + sealed `Intent`. Do not mix MVC, MVVM-with-LiveData, or other patterns.
- **`expect`/`actual` for platform APIs.** Video, URL launcher, share launcher use expect/actual. Do not use `#ifdef`-style conditionals or runtime platform checks.

### Scope Exclusions (Phase 1)
- Do not implement: real-time location sharing, ETA, Google Calendar integration, NFC, recommendation algorithms, ads, or payments.

## Build and Verification

```bash
# Full build (Android)
./gradlew assembleDebug

# Unit tests
./gradlew test

# Full check (lint + tests)
./gradlew check
```

## Tech Stack Reference

| Area | Technology | Version |
| --- | --- | --- |
| Framework | KMP + Compose Multiplatform | Kotlin 2.4.10, CMP 1.11.1 |
| Navigation | Navigation Compose (Type-Safe) | 2.8.0-alpha10 |
| State Management | AndroidX ViewModel + MVI | Lifecycle 2.8.4 |
| Image Loading | Coil 3 Multiplatform | 3.1.0 |
| Backend & DB | Supabase Kotlin SDK | 3.7.0 |
| HTTP Client | Ktor | 3.1.1 |
| Serialization | Kotlinx Serialization JSON | 1.8.0 |
| Video (Android) | Media3 ExoPlayer | 1.5.1 |
| Video (iOS) | AVPlayer (native) | — |
| UI Components | Material3 | 1.11.0-alpha07 |
| Android | AGP 9.0.1, minSdk 24, targetSdk 36 | — |
