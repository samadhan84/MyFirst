# Dhruvil Games

Two small games for Android phones (they also work as websites).

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

## Project layout

- `web/tic/`, `web/racer/`: the games (HTML/CSS/JS)
- `app/`: the Dhruvil Tic Android app (`com.dhruvil.tic`)
- `racer/`: the Dhruvil Racer Android app (`com.dhruvil.racer`)
- `.github/workflows/android.yml`: builds both APKs on every push

Each Android app is a full-screen WebView that loads its game from the app's assets.

## Get the APKs

**From GitHub (easiest):** push to GitHub and open the **Actions** tab. Open the latest
"Build Android APKs" run and download **DhruvilTic-apk** or **DhruvilRacer-apk** (each is a
zip with the `.apk` inside). Copy the APK to the phone, open it, and allow "install unknown
apps" when asked.

**With Android Studio:** open this folder, wait for Gradle sync, pick `app` or `racer`, and press Run.

**From the command line** (needs the Android SDK and JDK 17+):

    ./gradlew assembleDebug
    # -> app/build/outputs/apk/debug/app-debug.apk
    # -> racer/build/outputs/apk/debug/racer-debug.apk

## Play in a browser

    cd web && python3 -m http.server
    # open http://localhost:8000/tic/ or http://localhost:8000/racer/
