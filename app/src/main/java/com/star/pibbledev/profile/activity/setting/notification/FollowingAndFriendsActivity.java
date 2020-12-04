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

public class FollowingAndFriendsActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener{

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton switch_newfollowers, switch_request_arrived, switch_request_accepted;

    private String requestType;

    boolean mCurrentFlag, isNewFollowers, isRequestArrived, isRequestAccepted;
    String mCurrentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_following_friends);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        switch_newfollowers = (SwitchButton)findViewById(R.id.switch_newfollowers);
        switch_newfollowers.setOnCheckedChangeListener(this);
        switch_request_arrived = (SwitchButton)findViewById(R.id.switch_request_arrived);
        switch_request_arrived.setOnCheckedChangeListener(this);
        switch_request_accepted = (SwitchButton)findViewById(R.id.switch_request_accepted);
        switch_request_accepted.setOnCheckedChangeListener(this);

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

    private void parsingSettingInfos(JSONObject jsonObject) {

        isNewFollowers = jsonObject.optBoolean(Notification.NEW_FOLLOWER);
        isRequestArrived = jsonObject.optBoolean(Notification.FRIEND_REQUEST_ARRIVED);
        isRequestAccepted = jsonObject.optBoolean(Notification.FRIEND_REQUEST_ACCEPTED);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            switch_newfollowers.setChecked(isNewFollowers);
            switch_request_arrived.setChecked(isRequestArrived);
            switch_request_accepted.setChecked(isRequestAccepted);

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

            if (requestType.equals(REQUEST_GET_SETTINGS)) {

                getNotificationSettings();

            } else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) {

                setNotificationState(mCurrentFlag, mCurrentKey);

            }

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

        if (view == switch_newfollowers) {

            if (isChecked) {
                if (!isNewFollowers) setNotificationState(true, Notification.NEW_FOLLOWER);
            } else {
                if (isNewFollowers) setNotificationState(false, Notification.NEW_FOLLOWER);
            }

        } else if (view == switch_request_arrived) {

            if (isChecked) {
                if (!isRequestArrived) setNotificationState(true, Notification.FRIEND_REQUEST_ARRIVED);
            } else {
                if (isRequestArrived) setNotificationState(false, Notification.FRIEND_REQUEST_ARRIVED);
            }

        } else if (view == switch_request_accepted) {

            if (isChecked) {
                if (!isRequestAccepted) setNotificationState(true, Notification.FRIEND_REQUEST_ACCEPTED);
            } else {
                if (isRequestAccepted) setNotificationState(false, Notification.FRIEND_REQUEST_ACCEPTED);
            }
        }
    }
}
