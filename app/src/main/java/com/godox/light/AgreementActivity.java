package com.godox.light;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.zlm.base.BaseBackActivity;

import java.util.Locale;

import butterknife.Bind;

public class AgreementActivity extends BaseBackActivity {
    @Bind(R.id.webview)
    WebView webview;

    @Override
    public int bindLayout() {
        return R.layout.activity_agrement;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        String uri = isZh() ? "http://www.egodox.com/android/privacy/privacy_policy.html" : "http://www.egodox.com/android/privacy/privacy_policy_en.html";
        webview.loadUrl(uri);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {

    }

    public boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
