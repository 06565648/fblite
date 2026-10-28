# IPTV Player

A simple, modern **IPTV / M3U player** for Android phones and **Android TV**, built with
Kotlin, Jetpack Compose and Media3 (ExoPlayer).

You bring your own playlist (a `.m3u` / `.m3u8` URL or local file); the app parses it,
shows your channels as a lean-back, TV-style grid, and plays the stream you pick.

## Features

- Load a playlist from a **URL** or a **local file**
- Parses extended M3U (`#EXTINF`) with `tvg-logo` and `group-title` attributes
- **Category filter** chips (from `group-title`)
- Full-screen player with standard transport controls
- Plays **HLS** (`.m3u8`), **DASH** (`.mpd`) and progressive / TS streams
- Dark, TV-friendly UI; installs on **Android TV** (Leanback launcher), D-pad navigable
- Remembers your last playlist URL and reloads it on launch

## Build & run

Requires **Android Studio** (or the Android SDK + JDK 17). No Android SDK is bundled here,
so open the project in Android Studio to build.

```bash
# from this directory, once the Android SDK is configured:
./gradlew assembleDebug        # build a debug APK
./gradlew installDebug         # build + install on a connected device/emulator
```

The APK lands in `app/build/outputs/apk/debug/`.

> On first open, Android Studio may ask to create `local.properties` pointing at your
> Android SDK — let it, or add `sdk.dir=/path/to/Android/sdk` yourself.

## Project layout

```
app/src/main/java/com/example/iptvplayer/
├── MainActivity.kt          # entry point, file picker, screen switching
├── data/
│   ├── Channel.kt           # playlist entry model
│   ├── M3uParser.kt         # extended-M3U parser
│   └── PlaylistRepository.kt# load from URL / file, remember last URL
├── player/
│   └── PlayerScreen.kt      # Media3 ExoPlayer full-screen player
└── ui/
    ├── IptvViewModel.kt     # state + actions
    ├── HomeScreen.kt        # TV-style channel grid + source bar + filters
    └── theme/Theme.kt       # dark Material 3 theme
```

## A note on content

This app ships **no channels or streams**. It only plays playlists *you* provide. Please
use it with content you are authorized to access.

## Sample playlist

`sample-playlist.m3u` contains a couple of public test streams (Apple's HLS sample,
Google's DASH sample) so you can verify playback end to end.
