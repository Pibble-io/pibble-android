package com.star.pibbledev.profile.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.auth.SocialLoginActivity;
import com.star.pibbledev.gifticon.GiftHomeActivity;
import com.star.pibbledev.gifticon.GiftInviteActivity;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.profile.activity.WebviewActivity;
import com.star.pibbledev.profile.activity.setting.about.SettingAboutActivity;
import com.star.pibbledev.profile.activity.setting.account.SettingAccountHomeActivity;
import com.star.pibbledev.profile.activity.setting.commerce.SettingCommerceActivity;
import com.star.pibbledev.profile.activity.setting.friend.SettingCloseFriendActivity;
import com.star.pibbledev.profile.activity.setting.invite.SettingInviteFriendActivity;
import com.star.pibbledev.profile.activity.setting.notification.NotificationHomeActivity;
import com.star.pibbledev.profile.activity.setting.wallet.SettingWalletHomeActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettingHomeActicity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String TAG = "SettingHomeActicity";

    private static final String REQUEST_LOGOUT = "request_logout";

    ImageButton img_back;
    LinearLayout linear_invite_friends, linear_close_friend, linear_notification, linear_commerce, linear_promotions
            , linear_funding, linear_wallet, linear_account, linear_help, linear_about, linear_logout;

    ArrayList<LinearLayout> ary_linears = new ArrayList<>();

    public static boolean isRestarted;

    private String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_home);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        linear_invite_friends = (LinearLayout)findViewById(R.id.linear_invite_friends);
        linear_invite_friends.setOnClickListener(this);
        ary_linears.add(linear_invite_friends);

        linear_close_friend = (LinearLayout)findViewById(R.id.linear_close_friend);
        linear_close_friend.setOnClickListener(this);
        ary_linears.add(linear_close_friend);

        linear_notification = (LinearLayout)findViewById(R.id.linear_notification);
        linear_notification.setOnClickListener(this);
        ary_linears.add(linear_notification);

        linear_commerce = (LinearLayout)findViewById(R.id.linear_commerce);
        linear_commerce.setOnClickListener(this);
        ary_linears.add(linear_commerce);

        linear_promotions = (LinearLayout)findViewById(R.id.linear_promotions);
        linear_promotions.setOnClickListener(this);
        ary_linears.add(linear_promotions);

        linear_funding = (LinearLayout)findViewById(R.id.linear_funding);
        linear_funding.setOnClickListener(this);
        ary_linears.add(linear_funding);

        linear_wallet = (LinearLayout)findViewById(R.id.linear_wallet);
        linear_wallet.setOnClickListener(this);
        ary_linears.add(linear_wallet);

        linear_account = (LinearLayout)findViewById(R.id.linear_account);
        linear_account.setOnClickListener(this);
        ary_linears.add(linear_account);

        linear_help = (LinearLayout)findViewById(R.id.linear_help);
        linear_help.setOnClickListener(this);
        ary_linears.add(linear_help);

        linear_about = (LinearLayout)findViewById(R.id.linear_about);
        linear_about.setOnClickListener(this);
        ary_linears.add(linear_about);

        linear_logout = (LinearLayout)findViewById(R.id.linear_logout);
        linear_logout.setOnClickListener(this);
        ary_linears.add(linear_logout);

    }

    private void changeColorLinearLayout(int position) {

        if (ary_linears != null && ary_linears.size() > position) {

            for (int i = 0; i < ary_linears.size(); i++) {

                if (i == position) {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.line_background_color));
                } else {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SettingAccountHomeActivity.isRestarted) {

            SettingAccountHomeActivity.isRestarted = false;
            restartActivity();
        }

    }

    private void restartActivity() {

        isRestarted = true;

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void callLogout() {

        requestType = REQUEST_LOGOUT;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().authSignout(this, this, access_token);

        } else {
            Constants.requestRefreshToken(this, this);
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
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }

        if (v == linear_invite_friends) {

            changeColorLinearLayout(0);

            Intent intent = new Intent(this, GiftInviteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
//            Intent intent = new Intent(this, SettingInviteFriendActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_close_friend) {

            changeColorLinearLayout(1);

            Intent intent = new Intent(this, SettingCloseFriendActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_notification) {

            changeColorLinearLayout(2);

            Intent intent = new Intent(this, NotificationHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_commerce) {

            changeColorLinearLayout(3);

            Intent intent = new Intent(this, SettingCommerceActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_promotions) {

            changeColorLinearLayout(4);

            Intent intent = new Intent(this, UsersActivity.class);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_PROMOTIONS);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_funding) {

            changeColorLinearLayout(5);

            Intent intent = new Intent(this, UsersActivity.class);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_FUNDING);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_wallet) {

            changeColorLinearLayout(6);

            Intent intent = new Intent(this, SettingWalletHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_account) {

            changeColorLinearLayout(7);

            Intent intent = new Intent(this, SettingAccountHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_help) {

            changeColorLinearLayout(8);

            Intent intent = new Intent(this, WebviewActivity.class);

            String locale = Utility.getReadPref(this).getStringValue(Constants.LANGUAGE);

            if (locale.equals("ko")) {
                intent.putExtra(Constants.SITE_URL, Constants.help_ko);
            } else {
                intent.putExtra(Constants.SITE_URL, Constants.help_other);
            }

            intent.putExtra(WebviewActivity.TAG, TAG);

            intent.putExtra(Constants.TITLE, getString(R.string.help));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_about) {

            changeColorLinearLayout(9);

            Intent intent = new Intent(this, SettingAboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_logout) {

            changeColorLinearLayout(10);
            callLogout();

        }

    }

    // server

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (requestType.equals(REQUEST_LOGOUT)) {

                logout();

            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_LOGOUT)) {

                callLogout();

            }
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
