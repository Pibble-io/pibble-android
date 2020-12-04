package com.star.pibbledev.profile.activity.setting.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

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

public class AccountActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton switch_password_changed;
    CheckBox check_namechanged_off, check_namechanged_friend;

    private String requestType;
    boolean mCurrentFlag, isPasswordChanged;
    String mCurrentKey, mCurrentString, str_namechanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_account);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        switch_password_changed = (SwitchButton)findViewById(R.id.switch_password_changed);
        switch_password_changed.setOnCheckedChangeListener(this);
        check_namechanged_off = (CheckBox)findViewById(R.id.check_namechanged_off);
        check_namechanged_off.setOnCheckedChangeListener(this);
        check_namechanged_friend = (CheckBox)findViewById(R.id.check_namechanged_friend);
        check_namechanged_friend.setOnCheckedChangeListener(this);

        getNotificationSettings();
    }

    private void setNotificationState(boolean isOn, String string, String notification_key) {

        requestType = REQUEST_UPDATE_NOTIFICATION;
        mCurrentFlag = isOn;
        mCurrentString = string;
        mCurrentKey = notification_key;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateAccountSettings(this, this, token, isOn, string, notification_key);

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

    private void parsingSettingInfos(JSONObject jsonObject) {

        isPasswordChanged = jsonObject.optBoolean(Notification.ACCOUNT_PASSWORD_CHANGED);
        str_namechanged = jsonObject.optString(Notification.ACCOUNT_USERNAME_CHANGED);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            switch_password_changed.setChecked(isPasswordChanged);

            switch (str_namechanged) {

                case Notification.RESULT_OFF:
                    check_namechanged_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_namechanged_friend.setChecked(true);
                    break;
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton == check_namechanged_off) {

            if (b) {

                if (!str_namechanged.equals(Notification.RESULT_OFF)) {
                    str_namechanged = Notification.RESULT_OFF;
                    setNotificationState(false, str_namechanged, Notification.ACCOUNT_USERNAME_CHANGED);
                }
            }

            if (str_namechanged.equals(Notification.RESULT_OFF)) {
                check_namechanged_off.setChecked(true);
                check_namechanged_friend.setChecked(false);
            }

        } else if (compoundButton == check_namechanged_friend) {

            if (b) {

                if (!str_namechanged.equals(Notification.RESULT_FRIEND)) {
                    str_namechanged = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_namechanged, Notification.ACCOUNT_USERNAME_CHANGED);
                }
            }

            if (str_namechanged.equals(Notification.RESULT_FRIEND)) {
                check_namechanged_friend.setChecked(true);
                check_namechanged_off.setChecked(false);
            }

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_SETTINGS)) getNotificationSettings();
            else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) setNotificationState(mCurrentFlag, mCurrentString, mCurrentKey);

        } else {

            if (objResult != null) parsingSettingInfos(objResult);
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

        if (view == switch_password_changed) {

            if (isChecked) {
                if (!isPasswordChanged) setNotificationState(true, null, Notification.ACCOUNT_PASSWORD_CHANGED);
            } else {
                if (isPasswordChanged) setNotificationState(false, null, Notification.ACCOUNT_PASSWORD_CHANGED);
            }
        }

    }
}
