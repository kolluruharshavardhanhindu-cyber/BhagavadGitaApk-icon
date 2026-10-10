# BhagavadGita Android app

This Android WebView project packages the website from the supplied `V6-glass.zip`.

## Build without Android Studio

1. Upload the contents of this project ZIP to the root of a GitHub repository.
2. Open **Actions**.
3. Select **Build BhagavadGita APK** and click **Run workflow**.
4. After the green check mark, download the `BhagavadGita-APK` artifact.
5. Extract the artifact ZIP to get `BhagavadGita.apk`.

## Contact actions

- Gmail button uses `mailto:` and opens an installed mail app. The website also provides a Gmail web fallback.
- The phone number opens the phone dialer.
- The WhatsApp button opens a chat with +91 6302394175 and a prefilled message.

## App identity

- App label: `BhagavadGita`
- APK filename: `BhagavadGita.apk`
- Launcher artwork: `Bhagavad Gita_ Krishna and Arjuna.png` supplied by the user.

Some website resources (such as online fonts or remote scripts) require internet access.

## Launcher icon

Android 8+ launchers only treat an icon as full-size if the app ships an *adaptive icon*.
Without `mipmap-anydpi-v26/ic_launcher.xml`, the launcher shrinks the plain PNG and puts it
on a backdrop, which looks small. This project now ships:

- `mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml` (adaptive icons)
- `drawable-nodpi/ic_launcher_foreground.png` (artwork, trimmed of white margin, filling the 72dp safe zone)
- `values/ic_launcher_background.xml` (gold background colour for the area outside the safe zone)
- trimmed `mipmap-*/ic_launcher*.png` for Android 7 and below

Do not delete the `mipmap-anydpi-v26` folder.
