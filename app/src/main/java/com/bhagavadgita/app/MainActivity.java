package com.bhagavadgita.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;
    private View noInternetOverlay;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean wasOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep the screen awake for as long as the app is open.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        FrameLayout root = new FrameLayout(this);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        noInternetOverlay = buildNoInternetOverlay();
        noInternetOverlay.setVisibility(View.GONE);
        root.addView(noInternetOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

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

        updateInternetState();
        registerNetworkCallback();
    }

    // ---- Internet connectivity handling ----

    private boolean isInternetAvailable() {
        if (connectivityManager == null) return true;
        try {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
            return caps != null
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            return true; // Don't block the user if the check itself fails.
        }
    }

    private void updateInternetState() {
        final boolean online = isInternetAvailable();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (noInternetOverlay == null) return;
                noInternetOverlay.setVisibility(online ? View.GONE : View.VISIBLE);
                if (online && wasOffline && webView != null) {
                    // Reload so anything that failed to load offline (fonts, CDN scripts) recovers.
                    webView.reload();
                }
                wasOffline = !online;
            }
        });
    }

    private void registerNetworkCallback() {
        if (connectivityManager == null) return;
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override public void onAvailable(Network network) { updateInternetState(); }
            @Override public void onLost(Network network) { updateInternetState(); }
            @Override public void onCapabilitiesChanged(Network network, NetworkCapabilities caps) { updateInternetState(); }
        };
        try {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        } catch (Exception e) {
            networkCallback = null;
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private View buildNoInternetOverlay() {
        FrameLayout scrim = new FrameLayout(this);
        scrim.setBackgroundColor(Color.parseColor("#B3000000"));
        scrim.setClickable(true); // swallow touches so the page underneath can't be used

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(24), dp(28), dp(24), dp(28));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#FFF8EC"));
        bg.setCornerRadius(dp(20));
        card.setBackground(bg);

        TextView icon = new TextView(this);
        icon.setText("\uD83D\uDCF6");
        icon.setTextSize(40);
        icon.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("No Internet Connection");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#6B160D"));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(8));

        TextView msg = new TextView(this);
        msg.setText("Please check your Wi-Fi or mobile data. This message will close automatically once you're back online.");
        msg.setTextSize(15);
        msg.setTextColor(Color.parseColor("#4A3A33"));
        msg.setGravity(Gravity.CENTER);

        card.addView(icon);
        card.addView(title);
        card.addView(msg);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        lp.setMargins(dp(32), 0, dp(32), 0);
        scrim.addView(card, lp);
        return scrim;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInternetState();
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
        if (connectivityManager != null && networkCallback != null) {
            try { connectivityManager.unregisterNetworkCallback(networkCallback); } catch (Exception ignored) {}
            networkCallback = null;
        }
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
