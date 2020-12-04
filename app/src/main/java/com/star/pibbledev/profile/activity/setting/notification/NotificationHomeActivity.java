package com.star.pibbledev.profile.activity.setting.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Notification;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationHomeActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_settings";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton check_mute, check_email_sms;
    LinearLayout linear_mute, linear_post_comments, linear_follower_friend, linear_profile, linear_message_room, linear_promotion_funding, linear_wallet_activity, linear_account;

    ArrayList<LinearLayout> ary_linears = new ArrayList<>();
    String requestType;

    boolean mCurrentFlag, isNotification, isEmalAndSms;
    String mCurrentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_home);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        linear_mute = (LinearLayout)findViewById(R.id.linear_mute);

        check_mute = (SwitchButton)findViewById(R.id.check_mute);
        check_mute.setOnCheckedChangeListener(this);
        check_email_sms = (SwitchButton)findViewById(R.id.check_email_sms);
        check_email_sms.setOnCheckedChangeListener(this);

        linear_post_comments = (LinearLayout)findViewById(R.id.linear_post_comments);
        linear_post_comments.setOnClickListener(this);
        ary_linears.add(linear_post_comments);

        linear_follower_friend = (LinearLayout)findViewById(R.id.linear_follower_friend);
        linear_follower_friend.setOnClickListener(this);
        ary_linears.add(linear_follower_friend);

        linear_profile = (LinearLayout)findViewById(R.id.linear_profile);
        linear_profile.setOnClickListener(this);
        ary_linears.add(linear_profile);

        linear_message_room = (LinearLayout)findViewById(R.id.linear_message_room);
        linear_message_room.setOnClickListener(this);
        ary_linears.add(linear_message_room);

        linear_promotion_funding = (LinearLayout)findViewById(R.id.linear_promotion_funding);
        linear_promotion_funding.setOnClickListener(this);
        ary_linears.add(linear_promotion_funding);

        linear_wallet_activity = (LinearLayout)findViewById(R.id.linear_wallet_activity);
        linear_wallet_activity.setOnClickListener(this);
        ary_linears.add(linear_wallet_activity);

        linear_account = (LinearLayout)findViewById(R.id.linear_account);
        linear_account.setOnClickListener(this);
        ary_linears.add(linear_account);

        getNotificationSettings();

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
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == linear_post_comments) {

            changeColorLinearLayout(0);

            Intent intent = new Intent(this, PostAndCommentActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_follower_friend) {

            changeColorLinearLayout(1);

            Intent intent = new Intent(this, FollowingAndFriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_profile) {

            changeColorLinearLayout(2);

            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_message_room) {

            changeColorLinearLayout(3);

            Intent intent = new Intent(this, MessageRoomActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_promotion_funding) {

            changeColorLinearLayout(4);

            Intent intent = new Intent(this, PromotionAndFundingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_wallet_activity) {

            changeColorLinearLayout(5);

            Intent intent = new Intent(this, WalletActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_account) {

            changeColorLinearLayout(6);

            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    private void setNotificationMute(boolean isOn, String key) {

        requestType = REQUEST_UPDATE_NOTIFICATION;
        mCurrentFlag = isOn;
        mCurrentKey = key;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateAccountSettings(this, this, token, isOn, null, key);

        } else {

            Constants.requestRefreshToken(this, this);

        }
    }

    private void getNotificationSettings() {

        requestType = REQUEST_GET_SETTINGS;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getAccountSettings(this, this, token);

        } else {

            Constants.requestRefreshToken(this, this);

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_SETTINGS)) getNotificationSettings();
            else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) setNotificationMute(mCurrentFlag, mCurrentKey);

        } else {

            if (requestType.equals(REQUEST_GET_SETTINGS)) {

                if (objResult == null) return;

                isNotification = objResult.optBoolean(Notification.PUSH_NOTIFICATION);
                isEmalAndSms = objResult.optBoolean(Notification.EMAIL_AND_SMS);

                check_mute.setChecked(isNotification);
                check_email_sms.setChecked(isEmalAndSms);

            } else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) {

                if (objResult == null) return;

                isNotification = objResult.optBoolean(Notification.PUSH_NOTIFICATION);
                isEmalAndSms = objResult.optBoolean(Notification.EMAIL_AND_SMS);

            }
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

        if (view == check_mute) {

            if (isChecked) {
                if (!isNotification) setNotificationMute(true, Notification.PUSH_NOTIFICATION);
            } else {
                if (isNotification) setNotificationMute(false, Notification.PUSH_NOTIFICATION);
            }

        } else if (view == check_email_sms) {

            if (isChecked) {
                if (!isEmalAndSms) setNotificationMute(true, Notification.EMAIL_AND_SMS);
            } else {
                if (isEmalAndSms) setNotificationMute(false, Notification.EMAIL_AND_SMS);
            }
        }
    }
}
