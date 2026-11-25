# DOOMX Floating Controller

A free Android floating overlay practice controller.

## What it does
- Displays a draggable floating key above other apps.
- Holding the key starts a visible practice counter (120 ms interval, maximum 25 counts).
- Releasing stops the counter immediately.
- It does **not** inject taps, automate Free Fire, or control other apps.

## Build an APK on GitHub
1. Create a new empty GitHub repository.
2. Upload all files and folders from this project.
3. Open **Actions** and select **Build DOOMX APK** (or push to `main`).
4. Open the completed workflow run.
5. Download the `DOOMX-Floating-Controller-APK` artifact.

The workflow uses the installed Gradle command if a Gradle wrapper is not included. For a reproducible build, generate and commit the Gradle wrapper from Android Studio (`gradle wrapper`) before pushing.

## Install
Install the downloaded debug APK. On first launch, tap **ENABLE FLOATING KEY** and grant the Android "Display over other apps" permission.
