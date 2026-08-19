# 우회소 (wooheso)

## 프로젝트

- 우회소는 회사의 제품·브랜드·서비스를 전시형 콘텐츠로 소개하고 관심 고객을 문의·상담·미팅까지 연결하는 온라인 쇼룸 플랫폼이다.
- 한국어 표시명은 `우회소`, 저장소·패키지·Firebase 등 기술 식별자는 `wooheso` (`kmp-wooheso`)를 사용한다.
- 기획 기준은 `우회소 기획서 v2.4`이며, 상세 문서는 Obsidian 위키 `Dev/Project/Personal/wooheso/`에 있다.

## 현재 범위

- Phase 1 온라인 쇼룸 MVP를 Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) 기반으로 개발 중이다.
- UI/UX 프로토타입은 `flutter-wooheso`에 구현되어 있으며, 이를 CMP 클린 아키텍처로 포팅한다.
- Phase 1에는 Google 로그인, 회사/브랜드 프로필, 제품 등록·이미지/동영상 업로드, 제품 카드·피드·상세, 저장/공유, 외부 링크 문의를 포함한다.
- 피드 UX 규칙: 피드는 가격/부가설명 없이 숏폼 동영상/미디어 중심으로 무거운 텍스트를 배제하며, 프로스테드 글래스모피즘 헤더와 스마트 비디오 생명주기(`isCurrentPage` 재생/일시정지 제어)를 유지한다.
- 실시간 위치 공유, ETA, Google Calendar, NFC, 추천, 광고, 결제는 Phase 1 범위가 아니다.

## 데이터·인증 결정 (2026-08-09 확정)

- 인증은 Google 로그인 단독이다. Apple 로그인은 iOS 릴리스 시점까지 추가하지 않는다(Android 우선).
- `users`에 `role` 필드를 두지 않는다. 회사 문서 소유자가 곧 공급사이므로 권한은 `companies.ownerUid == request.auth.uid`로 판별한다.
- 제품 가격은 `priceType`(`fixed`/`inquiry`)으로 다루고 `price`는 `fixed`일 때만 존재한다. 가격은 상세 화면에서만 노출한다.
- 저장만 제공하고 공개 좋아요 카운터는 두지 않는다. 저장은 `users/{uid}/saved_products`에 비공개로 기록한다.
- 회사 프로필 필수 입력은 회사명·한 줄 소개·업종이며 로고를 포함한 나머지는 선택이다.

## 기술 스택

- Kotlin Multiplatform (KMP) & Compose Multiplatform (CMP) (Android 우선, iOS 대응)
- Orbit MVI (또는 Decompose)
- Navigation Compose (Multiplatform)
- Firebase Auth, Firestore, Storage (GitLive Firebase Kotlin SDK / Multiplatform)
- Video Player: Media3 (Android) / AVPlayer (iOS) Compose Wrapper

## 검증 명령

- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew check`
