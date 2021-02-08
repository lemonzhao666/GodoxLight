package com.godox.light;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zlm.base.BaseBackActivity;

public class PrivacyActivity extends BaseBackActivity {


    private WebView webView;

    @Override
    public int bindLayout() {
        return R.layout.activity_privacy;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        webView = findViewById(R.id.webview);
    }

    @Override
    public void doBusiness() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.egodox.com/statement/privacypolicy/zh/privacypolicy.html");
    }
    @Override
    public void initListener() {

    }
}
