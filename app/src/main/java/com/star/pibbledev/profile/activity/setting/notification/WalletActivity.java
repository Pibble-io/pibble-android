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

public class WalletActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton switch_pincode_changed, switch_deposit, switch_withdrawal;

    private String requestType;
    boolean mCurrentFlag, isPincodeChanged, isDeposit, isWithdrawal;
    String mCurrentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_wallet);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        switch_pincode_changed = (SwitchButton)findViewById(R.id.switch_pincode_changed);
        switch_pincode_changed.setOnCheckedChangeListener(this);
        switch_deposit = (SwitchButton)findViewById(R.id.switch_deposit);
        switch_deposit.setOnCheckedChangeListener(this);
        switch_withdrawal = (SwitchButton)findViewById(R.id.switch_withdrawal);
        switch_withdrawal.setOnCheckedChangeListener(this);

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

        isPincodeChanged = jsonObject.optBoolean(Notification.WALLET_PINCODE_CHANGED);
        isDeposit = jsonObject.optBoolean(Notification.WALLET_DEPOSIT);
        isWithdrawal = jsonObject.optBoolean(Notification.WALLET_WITHDRAW);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            switch_pincode_changed.setChecked(isPincodeChanged);
            switch_deposit.setChecked(isDeposit);
            switch_withdrawal.setChecked(isWithdrawal);
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

        if (view == switch_pincode_changed) {

            if (isChecked) {
                if (!isPincodeChanged) setNotificationState(true, Notification.WALLET_PINCODE_CHANGED);
            } else {
                if (isPincodeChanged) setNotificationState(false, Notification.WALLET_PINCODE_CHANGED);
            }
        } else if (view == switch_deposit) {

            if (isChecked) {
                if (!isDeposit) setNotificationState(true, Notification.WALLET_DEPOSIT);
            } else {
                if (isDeposit) setNotificationState(false, Notification.WALLET_DEPOSIT);
            }
        } else if (view == switch_withdrawal) {

            if (isChecked) {
                if (!isWithdrawal) setNotificationState(true, Notification.WALLET_WITHDRAW);
            } else {
                if (isWithdrawal) setNotificationState(false, Notification.WALLET_WITHDRAW);
            }
        }
    }
}
