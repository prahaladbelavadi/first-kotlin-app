# A swap space

An Android app for pushing content back and forth with a companion web
interface — upload something on one side, it shows up on the other. This
branch is a clean skeleton: one page and a bare navigation drawer, ready to
build the sync feature on top of.

## Features

- **Navigation drawer (sidebar)** with a single Home entry, wired up and
  ready for more.
- **Home screen**: placeholder single page.

## Project structure

```
app/src/main/java/com/example/helloworld/
├── MainActivity.kt          # drawer wiring, fragment switching
└── ui/
    └── HomeFragment.kt
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
