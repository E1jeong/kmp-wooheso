# shared Module Guide

## Scope

The `shared` module owns **all business logic, UI, data access, and navigation** for the 우회소 platform. Platform targets (`androidApp/`, `desktopApp/`, `iosApp/`) are thin shells that call `WoohesoApp()`.

This module is the only Gradle source set containing domain models, Supabase repositories, Compose screens, ViewModels, and the navigation graph.

## Orient First

### Wiki prerequisites
- [[technical/kmp-masterplan]] — production architecture, tech stack, and development phases
- [[data/data-model-draft]] — Supabase schema (companies, products, saved_products), RLS policies
- [[features/product-brand-exhibition]] — feature specifications for feed, detail, company profile

### Core source entrypoints

| Package | Key file | Responsibility |
| --- | --- | --- |
| `core/supabase/` | `SupabaseClientProvider.kt` | Supabase client singleton (Auth, Postgrest, Storage) |
| `core/designsystem/` | `Theme.kt`, `AppColors.kt`, `Glassmorphism.kt` | Material theme, color palette, frosted glass effect |
| `core/media/` | `VideoPlayer.kt` (expect) | Native video player — Media3 (Android), AVPlayer (iOS), placeholder (Desktop) |
| `core/util/` | `PlatformLauncher.kt` (expect) | Platform URL/share bridges (`rememberUrlLauncher`, `rememberShareLauncher`) |
| `data/model/supabase/` | `CompanyDto.kt`, `ProductDto.kt`, `SavedProductDto.kt` | `@Serializable` Supabase wire format |
| `data/model/` | `ProductCardModel.kt`, `CompanyModel.kt`, `UserModel.kt` | Domain models (UI-facing) |
| `data/repository/` | `SupabaseProductRepository.kt`, `SupabaseCompanyRepository.kt`, `SavedProductRepository.kt`, `SupabaseStorageRepository.kt`, `ConversionTracker.kt` | Supabase Postgrest CRUD, media upload, conversion tracking |
| `data/mock/` | `MockFeedData.kt` | Sample product data for offline preview |
| `features/feed/presentation/` | `FeedScreen.kt`, `widgets/ProductFeedCard.kt`, `widgets/CategorySelector.kt` | Vertical pager shorts feed + media card + category filter |
| `features/product/presentation/` | `ProductDetailScreen.kt` | Product showcase + inquiry bar |
| `features/product/registration/` | `ProductRegistrationScreen.kt` | Product creation form + media upload |
| `features/company/presentation/` | `CompanyProfileScreen.kt` | Brand header + product list |
| `features/company/registration/` | `CompanyRegistrationScreen.kt` | Supplier onboarding form |
| `features/saved/` | `SavedListScreen.kt` | Private saved product list |
| `navigation/` | `Routes.kt`, `WoohesoNavHost.kt` | `WoohesoRoute` sealed interface + `AnimatedContent` transitions |

## Boundary & Architecture Constraints

### Data flow
- **Input:** User interactions → sealed `Intent` → ViewModel
- **Output:** `StateFlow<UiState>` → Compose UI; `SharedFlow<SideEffect>` → one-shot effects (navigation, toasts)
- **Backend:** All data access goes through `data/repository/` → Supabase Postgrest/Storage. No direct Supabase calls from screens or ViewModels.

### Architectural invariants
- **MVI pattern is mandatory.** Every feature ViewModel must use `StateFlow<UiState>` + `SharedFlow<SideEffect>` + sealed interface `Intent`. No LiveData, no mutable state in Composables.
- **`expect`/`actual` for platform APIs.** Video player, URL launcher, share launcher. No runtime `Platform.name` checks or `#ifdef`-style branching.
- **Coil 3 for images.** All async image loading uses Coil `AsyncImage`. No other image library.
- **DTO ↔ Model separation.** `data/model/supabase/` holds `@Serializable` wire types matching Supabase columns. `data/model/` holds UI-facing domain types. Map between them in repositories.
- **Navigation is centralized.** All route definitions live in `WoohesoNavHost.kt`. Feature screens receive callbacks; they do not hold `NavController` references.

### Module boundaries
- Platform modules (`androidApp`, `desktopApp`, `iosApp`) must **not** contain business logic, repositories, or ViewModels.
- `core/` packages must **not** depend on `features/` or `navigation/`.
- `data/` must **not** depend on `features/` or `navigation/`.
- `features/` screens must **not** depend on each other directly — communication is through navigation only.

## Change Gates

- **Do not add Firebase or any non-Supabase backend dependency.**
- **Do not put `@Composable` functions in `data/` or `core/supabase/`.**
- **Do not create feature-to-feature imports** (e.g., feed importing company registration). Use navigation routes.
- **Do not remove `expect`/`actual` and replace with runtime platform checks.**
- **Do not modify `SupabaseClientProvider` credentials without confirming against the Supabase dashboard.**
- **Do not bypass DTO → Model mapping by using DTOs (`data/model/supabase/`) directly in Composables.**

## Verify

```bash
# Shared module tests
./gradlew :shared:test

# Full build including shared
./gradlew assembleDebug

# All checks
./gradlew check
```
