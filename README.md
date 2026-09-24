# first-kotlin-app

A first Android app in Kotlin, built up from a plain "Hello World" into a small
app with navigation and a network-backed CRUD screen.

## Features

- **Navigation drawer (sidebar)** with Home, Posts, Profile, and placeholder
  Settings/About entries.
- **Bottom navigation** with three tabs (Home, Posts, Profile), kept in sync
  with the drawer through a single navigation function in `MainActivity`.
- **Posts screen**: fetches posts from the [JSONPlaceholder](https://jsonplaceholder.typicode.com/)
  REST API via Retrofit + OkHttp, with create/edit/delete support, a
  connectivity check before each request, request timeouts, and distinct
  loading/empty/error (with retry) states instead of crashing on network
  failure.

## Project structure

```
app/src/main/java/com/example/helloworld/
├── MainActivity.kt          # drawer + bottom nav wiring, fragment switching
├── data/
│   ├── ApiClient.kt         # Retrofit/OkHttp setup
│   ├── NetworkUtils.kt      # connectivity check
│   ├── Post.kt              # Post model
│   └── PostsApi.kt          # Retrofit endpoint definitions
└── ui/
    ├── HomeFragment.kt
    ├── PostsFragment.kt      # list + create/edit/delete
    ├── PostsAdapter.kt
    └── ProfileFragment.kt
```

## Requirements

- JDK 17
- Android SDK with `compileSdk 33` / `platform-tools` installed
- A device or emulator running Android 5.0 (API 21) or newer

## Build

```bash
./gradlew assembleDebug
```

The output APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Install

**From a release:** download the latest APK from the
[Releases page](https://github.com/prahaladbelavadi/first-kotlin-app/releases),
transfer it to your phone, and open it (you'll need to allow "install from
unknown sources" for the app you use to open it).

**Via adb**, with the device connected over USB with USB debugging enabled:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## License

No license specified yet — all rights reserved by default until one is added.
