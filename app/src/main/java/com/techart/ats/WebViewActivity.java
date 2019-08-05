package com.techart.ats;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.techart.ats.constants.Constants;

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
