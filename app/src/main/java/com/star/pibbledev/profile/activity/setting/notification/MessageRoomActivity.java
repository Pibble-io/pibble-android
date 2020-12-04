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

public class MessageRoomActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton switch_arrived;

    private String requestType;
    boolean mCurrentFlag, isArrived;
    String mCurrentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_message_room);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        switch_arrived = (SwitchButton)findViewById(R.id.switch_arrived);
        switch_arrived.setOnCheckedChangeListener(this);

        getNotificationSettings();

    }

    private void setNotificationState(boolean isOn, String notification_key) {

        requestType = REQUEST_UPDATE_NOTIFICATION;
        mCurrentFlag = isOn;
        mCurrentKey = notification_key;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateAccountSettings(this, this, token, isOn, null, notification_key);

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

        isArrived = jsonObject.optBoolean(Notification.CHAT_NEW_MESSAGE);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {
            switch_arrived.setChecked(isArrived);
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
            else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) setNotificationState(mCurrentFlag, mCurrentKey);

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

        if (isChecked) {
            if (!isArrived) setNotificationState(true, Notification.CHAT_NEW_MESSAGE);
        } else {
            if (isArrived) setNotificationState(false, Notification.CHAT_NEW_MESSAGE);
        }
    }
}
