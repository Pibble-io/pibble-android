package com.star.pibbledev.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.auth.SocialLoginActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class SettingActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    ImageButton btn_cancel;
    LinearLayout linear_all, linear_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_cancel = (ImageButton)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        linear_all = (LinearLayout)findViewById(R.id.linear_all);
        linear_logout = (LinearLayout)findViewById(R.id.linear_logout);
        linear_logout.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linear_all.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.layout_scale);
                animation.setDuration(400);
                linear_all.setAnimation(animation);
                linear_all.animate();
                animation.start();
            }
        }, 700);

    }

    private void callLogout() {

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().authSignout(this, this, access_token);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            logout();

        } else {

            Constants.saveRefreshToken(this, objResult);
            callLogout();

        }

    }

    private void logout() {

        Utility.getSavedPref(this).saveString(Constants.AUTH_ACCESS_TOKEN, "");
        Utility.getSavedPref(this).saveString(Constants.SIGN_UP_TOKEN, "");
        Utility.getSavedPref(this).saveString(Constants.AUTH_EXPIRED_TIME, "");
        Utility.getSavedPref(this).saveString(Constants.AUTH_REFRESH_TOKEN, "");
        Utility.getSavedPref(this).saveBoolean(Constants.EMAIL_VERIFIED, false);
        Utility.getSavedPref(this).saveBoolean(Constants.PHONE_VERIFIED, false);

        Utility.saveUUIDToStorage(this,"");

        Intent intent = new Intent(this, SocialLoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);
    }

    @SuppressLint("TimberArgCount")
    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));
        }

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onClick(View view) {

        if (view == btn_cancel) {
            finish();
            overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);
        } else if (view == linear_logout) {

            callLogout();

        }

    }
}
