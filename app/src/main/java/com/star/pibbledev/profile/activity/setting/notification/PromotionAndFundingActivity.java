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

public class PromotionAndFundingActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    CheckBox check_new_promotion_off, check_new_promotion_friend, check_new_promotion_every, check_new_funding_off, check_new_funding_friend, check_new_funding_every;
    SwitchButton switch_funding;

    private String requestType;

    boolean mCurrentFlag, isFunding;
    String mCurrentString, mCurrentKey, str_new_promotion, str_funding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_promotion_funding);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        check_new_promotion_off = (CheckBox)findViewById(R.id.check_new_promotion_off);
        check_new_promotion_off.setOnCheckedChangeListener(this);
        check_new_promotion_friend = (CheckBox)findViewById(R.id.check_new_promotion_friend);
        check_new_promotion_friend.setOnCheckedChangeListener(this);
        check_new_promotion_every = (CheckBox)findViewById(R.id.check_new_promotion_every);
        check_new_promotion_every.setOnCheckedChangeListener(this);
        check_new_funding_off = (CheckBox)findViewById(R.id.check_new_funding_off);
        check_new_funding_off.setOnCheckedChangeListener(this);
        check_new_funding_friend = (CheckBox)findViewById(R.id.check_new_funding_friend);
        check_new_funding_friend.setOnCheckedChangeListener(this);
        check_new_funding_every = (CheckBox)findViewById(R.id.check_new_funding_every);
        check_new_funding_every.setOnCheckedChangeListener(this);

        switch_funding = (SwitchButton)findViewById(R.id.switch_funding);
        switch_funding.setOnCheckedChangeListener(this);

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

        str_new_promotion = jsonObject.optString(Notification.NEW_PROMOTION);
        str_funding = jsonObject.optString(Notification.NEW_FUNDING);
        isFunding = jsonObject.optBoolean(Notification.FUNDING_CONTRIBUTED);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            switch_funding.setChecked(isFunding);

            switch (str_new_promotion) {

                case Notification.RESULT_OFF:
                    check_new_promotion_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_new_promotion_friend.setChecked(true);
                    break;
                case Notification.RESULT_EVERYONE:
                    check_new_promotion_every.setChecked(true);
                    break;
            }

            switch (str_funding) {

                case Notification.RESULT_OFF:
                    check_new_funding_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_new_funding_friend.setChecked(true);
                    break;
                case Notification.RESULT_EVERYONE:
                    check_new_funding_every.setChecked(true);
                    break;
            }
        }
    }

    private void changedNewPromotionCheckbox() {

        switch (str_new_promotion) {

            case Notification.RESULT_OFF:
                check_new_promotion_off.setChecked(true);
                check_new_promotion_friend.setChecked(false);
                check_new_promotion_every.setChecked(false);
                break;
            case Notification.RESULT_FRIEND:
                check_new_promotion_off.setChecked(false);
                check_new_promotion_friend.setChecked(true);
                check_new_promotion_every.setChecked(false);
                break;
            case Notification.RESULT_EVERYONE:
                check_new_promotion_off.setChecked(false);
                check_new_promotion_friend.setChecked(false);
                check_new_promotion_every.setChecked(true);
                break;
        }
    }

    private void changedNewFundingCheckbox() {

        switch (str_funding) {

            case Notification.RESULT_OFF:
                check_new_funding_off.setChecked(true);
                check_new_funding_friend.setChecked(false);
                check_new_funding_every.setChecked(false);
                break;
            case Notification.RESULT_FRIEND:
                check_new_funding_off.setChecked(false);
                check_new_funding_friend.setChecked(true);
                check_new_funding_every.setChecked(false);
                break;
            case Notification.RESULT_EVERYONE:
                check_new_funding_off.setChecked(false);
                check_new_funding_friend.setChecked(false);
                check_new_funding_every.setChecked(true);
                break;
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

        if (compoundButton == check_new_promotion_off) {

            if (b) {

                if (!str_new_promotion.equals(Notification.RESULT_OFF)) {
                    str_new_promotion = Notification.RESULT_OFF;
                    setNotificationState(false, str_new_promotion, Notification.NEW_PROMOTION);
                }
            }

            changedNewPromotionCheckbox();

        } else if (compoundButton == check_new_promotion_friend) {

            if (b) {

                if (!str_new_promotion.equals(Notification.RESULT_FRIEND)) {
                    str_new_promotion = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_new_promotion, Notification.NEW_PROMOTION);
                }
            }

            changedNewPromotionCheckbox();

        } else if (compoundButton == check_new_promotion_every) {

            if (b) {

                if (!str_new_promotion.equals(Notification.RESULT_EVERYONE)) {
                    str_new_promotion = Notification.RESULT_EVERYONE;
                    setNotificationState(false, str_new_promotion, Notification.NEW_PROMOTION);
                }
            }

            changedNewPromotionCheckbox();

        } else if (compoundButton == check_new_funding_off) {

            if (b) {

                if (!str_funding.equals(Notification.RESULT_OFF)) {
                    str_funding = Notification.RESULT_OFF;
                    setNotificationState(false, str_funding, Notification.NEW_FUNDING);
                }
            }

            changedNewFundingCheckbox();

        } else if (compoundButton == check_new_funding_friend) {

            if (b) {

                if (!str_funding.equals(Notification.RESULT_FRIEND)) {
                    str_funding = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_funding, Notification.NEW_FUNDING);
                }
            }

            changedNewFundingCheckbox();

        } else if (compoundButton == check_new_funding_every) {

            if (b) {

                if (!str_funding.equals(Notification.RESULT_EVERYONE)) {
                    str_funding = Notification.RESULT_EVERYONE;
                    setNotificationState(false, str_funding, Notification.NEW_FUNDING);
                }
            }

            changedNewFundingCheckbox();
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

        if (isChecked) {
            if (!isFunding) setNotificationState(true, null, Notification.FUNDING_CONTRIBUTED);
        } else {
            if (isFunding) setNotificationState(false, null, Notification.FUNDING_CONTRIBUTED);
        }
    }
}
