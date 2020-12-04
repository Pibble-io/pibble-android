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

public class ProfileActivity extends BaseActivity implements View.OnClickListener, RequestListener, CompoundButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    CheckBox check_profile_updated_off, check_profile_updated_friend, check_level_up_off, check_level_up_friend;

    private String requestType;
    String mCurrentString, mCurrentKey, str_profile_update, str_level_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_profile);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        check_profile_updated_off = (CheckBox)findViewById(R.id.check_profile_updated_off);
        check_profile_updated_off.setOnCheckedChangeListener(this);
        check_profile_updated_friend = (CheckBox)findViewById(R.id.check_profile_updated_friend);
        check_profile_updated_friend.setOnCheckedChangeListener(this);
        check_level_up_off = (CheckBox)findViewById(R.id.check_level_up_off);
        check_level_up_off.setOnCheckedChangeListener(this);
        check_level_up_friend = (CheckBox)findViewById(R.id.check_level_up_friend);
        check_level_up_friend.setOnCheckedChangeListener(this);

        getNotificationSettings();
    }

    private void setNotificationState(String string, String notification_key) {

        requestType = REQUEST_UPDATE_NOTIFICATION;
        mCurrentString = string;
        mCurrentKey = notification_key;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateAccountSettings(this, this, token, false, string, notification_key);

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

        str_profile_update = jsonObject.optString(Notification.PROFILE_UPDATED);
        str_level_up = jsonObject.optString(Notification.PROFILE_LEVEL_UP);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            if (str_profile_update.equals(Notification.RESULT_OFF)) check_profile_updated_off.setChecked(true);
            else if (str_profile_update.equals(Notification.RESULT_FRIEND)) check_profile_updated_friend.setChecked(true);

            if (str_level_up.equals(Notification.RESULT_OFF)) check_level_up_off.setChecked(true);
            else if (str_level_up.equals(Notification.RESULT_FRIEND)) check_level_up_friend.setChecked(true);
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
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_SETTINGS)) getNotificationSettings();
            else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) setNotificationState( mCurrentString, mCurrentKey);

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
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton == check_profile_updated_off) {

            if (b) {

                if (!str_profile_update.equals(Notification.RESULT_OFF)) {
                    str_profile_update = Notification.RESULT_OFF;
                    setNotificationState(str_profile_update, Notification.PROFILE_UPDATED);
                }
            }

            if (str_profile_update.equals(Notification.RESULT_OFF)) {
                check_profile_updated_off.setChecked(true);
                check_profile_updated_friend.setChecked(false);
            }

        } else if (compoundButton == check_profile_updated_friend) {

            if (b) {

                if (!str_profile_update.equals(Notification.RESULT_FRIEND)) {
                    str_profile_update = Notification.RESULT_FRIEND;
                    setNotificationState(str_profile_update, Notification.PROFILE_UPDATED);
                }
            }

            if (str_profile_update.equals(Notification.RESULT_FRIEND)) {
                check_profile_updated_off.setChecked(false);
                check_profile_updated_friend.setChecked(true);
            }

        } else if (compoundButton == check_level_up_off) {

            if (b) {

                if (!str_level_up.equals(Notification.RESULT_OFF)) {
                    str_level_up = Notification.RESULT_OFF;
                    setNotificationState(str_level_up, Notification.PROFILE_LEVEL_UP);
                }
            }

            if (str_level_up.equals(Notification.RESULT_OFF)) {
                check_level_up_off.setChecked(true);
                check_level_up_friend.setChecked(false);
            }

        } else if (compoundButton == check_level_up_friend) {

            if (b) {

                if (!str_level_up.equals(Notification.RESULT_FRIEND)) {
                    str_level_up = Notification.RESULT_FRIEND;
                    setNotificationState(str_level_up, Notification.PROFILE_LEVEL_UP);
                }
            }

            if (str_level_up.equals(Notification.RESULT_FRIEND)) {
                check_level_up_off.setChecked(false);
                check_level_up_friend.setChecked(true);
            }
        }
    }
}
