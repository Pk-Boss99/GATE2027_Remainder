# GATE Wallpaper

Native Kotlin Android app that generates a motivational GATE countdown wallpaper and updates it daily.

## Build APK on GitHub

1. Push this project to a GitHub repository.
2. Open the repository on GitHub.
3. Go to `Actions > Build APK > Run workflow`.
4. After the workflow finishes, download `gate-wallpaper-debug-apk` from the workflow run artifacts.

The APK path inside the artifact is:

```text
app-debug.apk
```

## Push to GitHub

From this folder:

```powershell
git init
git add .
git commit -m "Initial GATE wallpaper Android app"
git branch -M main
git remote add origin https://github.com/Pk-Boss99/GATE2027_Remainder.git
git push -u origin main
```

Do not commit `local.properties`; it is ignored because it contains machine-specific Android SDK paths.

## Local Build

If you build locally, create `local.properties` with:

```properties
sdk.dir=your_android_sdk_path
```

Then run:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```
