This is a Kotlin Multiplatform project targeting Android, iOS, and Desktop (JVM).

* [/iosApp](./iosApp/iosApp) contains the iOS host application shell (SwiftUI entrypoint).
* [/androidApp](./androidApp) contains the Android host application shell.
* [/desktopApp](./desktopApp) contains the Compose Multiplatform Desktop host application shell (JVM).
* [/shared](./shared/src) contains shared Compose Multiplatform UI, business logic, and platform abstractions:
  - [commonMain](./shared/src/commonMain/kotlin) is common for all targets.
  - [androidMain](./shared/src/androidMain/kotlin) contains Android-specific implementations (Media3 ExoPlayer).
  - [desktopMain](./shared/src/desktopMain/kotlin) contains Desktop-specific implementations.
  - [iosMain](./shared/src/iosMain/kotlin) contains iOS-specific implementations.

### Running the apps

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app: `./gradlew :desktopApp:run`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

- Shared desktop tests: `./gradlew :shared:desktopTest`
- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…