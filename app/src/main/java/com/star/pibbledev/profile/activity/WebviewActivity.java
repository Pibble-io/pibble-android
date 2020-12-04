package com.star.pibbledev.profile.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.activity.setting.about.SettingAboutActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

import java.net.URISyntaxException;

public class WebviewActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "WebviewActivity";

    ImageButton img_back;
    TextView txt_title;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_title = (TextView)findViewById(R.id.txt_title);
        webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new MyWebViewClient());

        String url = getIntent().getStringExtra(Constants.SITE_URL);
        String title = getIntent().getStringExtra(Constants.TITLE);
        String tag = getIntent().getStringExtra(TAG);

        if (title != null) txt_title.setText(title);

        if (Utility.checkWebsite(url)) {
            webView.loadUrl("http://" + url);
        } else {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("intent:") && url.contains("kakaolink")) {

                try {

                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {

                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);
                        }

                        return true;
                    }

                } catch (URISyntaxException ignored) {

                }
            }

            return !URLUtil.isNetworkUrl(url);

        }
    }
}
