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

## Launcher icon sizing (original image unchanged)

This project intentionally uses the supplied original logo image unchanged in the five
`mipmap-*` folders. It does not define `mipmap-anydpi-v26/ic_launcher.xml` or
`ic_launcher_round.xml`, because those adaptive-icon XML files can cause Android
launchers to fit the entire logo inside the adaptive icon safe zone, making it look
smaller than expected.

If you are updating an existing GitHub repository, check for these two files:
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`

If either exists in your repository, delete it and commit the deletion, then rebuild.
Do not edit or crop the original PNG. The launcher may still apply the phone's own
shape mask, so the exact appearance varies by Android launcher.
