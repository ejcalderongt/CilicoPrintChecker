package com.olc.printchecker;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.Locale;

public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setDisplayZoomControls(false);
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            webview.loadUrl("file:///android_asset/help.html");
        else
            webview.loadUrl("file:///android_asset/help_en.html");

    }
}
