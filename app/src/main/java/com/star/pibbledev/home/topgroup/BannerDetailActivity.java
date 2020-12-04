package com.star.pibbledev.home.topgroup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertHorizentalDialog;

import java.net.URISyntaxException;

public class BannerDetailActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back, img_cancel;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topbanner_webview);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_cancel = (ImageButton)findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(this);
        webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new MyWebViewClient());

        String email = Utility.getReadPref(this).getStringValue(Constants.EMAIL);
        String username = Utility.getReadPref(this).getStringValue(Constants.USERNAME);

        String homeUrl = String.format("%s?email=%s&username=%s&isnative=1", Constants.banner_detail_url, email, username);

        if (Utility.checkWebsite(homeUrl)) {
            webView.loadUrl("http://" + homeUrl);
        } else {
            webView.loadUrl(homeUrl);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_cancel) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == img_back) {

            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            if (keyCode == KeyEvent.KEYCODE_BACK) {

                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                    overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
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
