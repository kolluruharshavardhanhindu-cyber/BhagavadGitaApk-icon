# BhagavadGita Android WebView App

This Android project packages the contents of the supplied `htdocs` website ZIP inside an Android WebView app. The website files are in `app/src/main/assets/www/`.

## Build APK using GitHub Actions
1. Upload the contents of this project folder to your GitHub repository (keep the `.github/workflows/build-apk.yml` file).
2. Open the repository's **Actions** tab.
3. Select **Build BhagavadGita APK** and click **Run workflow** (or push to `main`/`master`).
4. Open the completed workflow run and download the **BhagavadGita-APK** artifact.
5. Extract the downloaded artifact to get `BhagavadGita.apk`.

The workflow uses Java 17 and builds a debug APK. A debug APK is suitable for direct testing/installing, but is not a signed release for Play Store publication.
