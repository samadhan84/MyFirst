# Dhruvil Games

Small games for Android phones and Android TV (they also work as websites).

## Dhruvil Tic

Tic-tac-toe.
- **vs Computer**: a smart AI that slips up now and then, so you can still win
- **2 Players**: pass the phone back and forth
- Keeps score, switches who goes first each game, and buzzes when you tap

## Dhruvil Racer

A 3D car racing game, drawn with [three.js](https://threejs.org) (MIT, bundled in `web/racer/three.min.js`
so the game works offline).
- Tap the left or right side of the screen (or use the arrow keys) to change lanes
- Dodge the traffic, grab the **D** coins (+10 each), and go as far as you can
- The race gets faster over time, and the game remembers your best score

## Bruno's Big Adventure (Android TV)

An original 3D platformer for Android TV, played with a gamepad. You play Bruno the bear, hop across
floating islands, punch slimes, collect coins and reach the flag.

| Button | Action |
|---|---|
| Stick / D-pad | Move |
| **A** | Jump |
| **B** | Punch |
| **A** then **B**, or **B** then **A** (within ~0.2 s) | Very long jump (about 2x the distance) |

On a plain TV remote, OK acts as A (there is no B, so use a gamepad to punch). On a computer:
arrows/WASD to move, Space or Z = A, X = B.

This is a standalone game with its own characters and levels. It is **not** connected to the
Super Bear Adventure servers (that game and its network protocol belong to its developer).

## Project layout

- `web/bear/`, `bear/`: Bruno's Big Adventure and its Android TV app (`com.dhruvil.bear`)

- `web/tic/`, `web/racer/`: the games (HTML/CSS/JS)
- `app/`: the Dhruvil Tic Android app (`com.dhruvil.tic`)
- `racer/`: the Dhruvil Racer Android app (`com.dhruvil.racer`)
- `.github/workflows/android.yml`: builds all APKs on every push

Each Android app is a full-screen WebView that loads its game from the app's assets.

## Get the APKs

**From GitHub (easiest):** push to GitHub and open the **Actions** tab. Open the latest
"Build Android APKs" run and download **DhruvilTic-apk** or **DhruvilRacer-apk** or **BrunoBigAdventure-TV-apk** (each is a
zip with the `.apk` inside). Copy the APK to the phone, open it, and allow "install unknown
apps" when asked.

**With Android Studio:** open this folder, wait for Gradle sync, pick `app` or `racer`, and press Run.

**From the command line** (needs the Android SDK and JDK 17+):

    ./gradlew assembleDebug
    # -> app/build/outputs/apk/debug/app-debug.apk
    # -> racer/build/outputs/apk/debug/racer-debug.apk
    # -> bear/build/outputs/apk/debug/bear-debug.apk

## Play in a browser

    cd web && python3 -m http.server
    # open http://localhost:8000/tic/ or http://localhost:8000/racer/ or http://localhost:8000/bear/
