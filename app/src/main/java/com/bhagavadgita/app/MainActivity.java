package com.bhagavadgita.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleNavigation(request.getUrl());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleNavigation(Uri.parse(url));
            }
        });
        webView.loadUrl("file:///android_asset/www/index.html");
    }

    private boolean handleNavigation(Uri uri) {
        String url = uri.toString();
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();

        // Keep local website pages and assets inside the app.
        if ("file".equals(scheme) && url.startsWith("file:///android_asset/")) {
            return false;
        }

        Intent intent;
        if ("mailto".equals(scheme)) {
            intent = new Intent(Intent.ACTION_SENDTO, uri);
        } else if ("tel".equals(scheme)) {
            // Opens the phone dialer; the user confirms before placing a call.
            intent = new Intent(Intent.ACTION_DIAL, uri);
        } else if ("https".equals(scheme) && (
                "wa.me".equalsIgnoreCase(uri.getHost()) ||
                "api.whatsapp.com".equalsIgnoreCase(uri.getHost()) ||
                "mail.google.com".equalsIgnoreCase(uri.getHost()))) {
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else if ("http".equals(scheme) || "https".equals(scheme)) {
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, uri);
        }

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // No compatible app installed. Avoid crashing; Gmail links include a web fallback.
        } catch (Exception ex) {
            // Invalid or unsupported external link; keep the app running.
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
