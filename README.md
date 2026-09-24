# Dhruvil Tic

A small tic-tac-toe game for Android (it also works as a website).

- **vs Computer**: a smart AI that slips up now and then, so you can still win
- **2 Players**: pass the phone back and forth
- Keeps score, switches who goes first each game, and buzzes when you tap

## Project layout

- `web/`: the game (HTML/CSS/JS). The Android app loads it from its assets.
- `app/`: the Android app, a full-screen WebView wrapper (`com.dhruvil.tic`)
- `.github/workflows/android.yml`: builds the APK on every push

## Get the APK

**From GitHub (easiest):** push to GitHub and open the **Actions** tab. Open the latest
"Build Android APK" run and download the **DhruvilTic-apk** artifact (a zip containing
`app-debug.apk`). Copy it to the phone, open it, and allow "install unknown apps" when asked.

**With Android Studio:** open this folder, wait for Gradle sync, then press Run, or use
*Build → Build APK(s)*.

**From the command line** (needs the Android SDK and JDK 17+):

    ./gradlew assembleDebug
    # -> app/build/outputs/apk/debug/app-debug.apk

## Play in a browser

    cd web && python3 -m http.server
    # open http://localhost:8000
