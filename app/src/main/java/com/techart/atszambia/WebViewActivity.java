package com.techart.atszambia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.techart.atszambia.constants.Constants;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        String postTitle = getIntent().getStringExtra(Constants.POST_TITLE);
        webView.loadUrl(postTitle);

    }
}
