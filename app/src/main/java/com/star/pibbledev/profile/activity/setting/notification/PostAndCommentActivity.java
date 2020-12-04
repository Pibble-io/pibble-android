package com.star.pibbledev.profile.activity.setting.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class PostAndCommentActivity extends BaseActivity implements View.OnClickListener, RequestListener, SwitchButton.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final String REQUEST_GET_SETTINGS = "request_get_setting";
    private static final String REQUEST_UPDATE_NOTIFICATION = "request_update_notification";

    ImageButton img_back;
    SwitchButton switch_upvote, switch_comment_upvote, switch_comments, switch_collect;
    CheckBox check_post_photo_off, check_post_photo_friend, check_first_post_off, check_first_post_friend, check_first_post_every, check_post_goods_off, check_post_goods_friend, check_post_goods_every,
            check_post_shop_off, check_post_shop_friend, check_post_shop_every;

    private String requestType;

    boolean isUpvote, isCommentUpvote, isComments, isCollect, mCurrentFlag;
    String str_post_photo, str_first_post, str_post_goods, str_post_shop, mCurrentString, mCurrentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_notification_post_comment);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        switch_upvote = (SwitchButton)findViewById(R.id.switch_upvote);
        switch_upvote.setOnCheckedChangeListener(this);
        switch_comment_upvote = (SwitchButton)findViewById(R.id.switch_comment_upvote);
        switch_comment_upvote.setOnCheckedChangeListener(this);
        switch_comments = (SwitchButton)findViewById(R.id.switch_comments);
        switch_comments.setOnCheckedChangeListener(this);
        switch_collect = (SwitchButton)findViewById(R.id.switch_collect);
        switch_collect.setOnCheckedChangeListener(this);

        check_post_photo_off = (CheckBox)findViewById(R.id.check_post_photo_off);
        check_post_photo_off.setOnCheckedChangeListener(this);
        check_post_photo_friend = (CheckBox)findViewById(R.id.check_post_photo_friend);
        check_post_photo_friend.setOnCheckedChangeListener(this);
        check_first_post_off = (CheckBox)findViewById(R.id.check_first_post_off);
        check_first_post_off.setOnCheckedChangeListener(this);
        check_first_post_friend = (CheckBox)findViewById(R.id.check_first_post_friend);
        check_first_post_friend.setOnCheckedChangeListener(this);
        check_first_post_every = (CheckBox)findViewById(R.id.check_first_post_every);
        check_first_post_every.setOnCheckedChangeListener(this);
        check_post_goods_off = (CheckBox)findViewById(R.id.check_post_goods_off);
        check_post_goods_off.setOnCheckedChangeListener(this);
        check_post_goods_friend = (CheckBox)findViewById(R.id.check_post_goods_friend);
        check_post_goods_friend.setOnCheckedChangeListener(this);
        check_post_goods_every = (CheckBox)findViewById(R.id.check_post_goods_every);
        check_post_goods_every.setOnCheckedChangeListener(this);
        check_post_shop_off = (CheckBox)findViewById(R.id.check_post_shop_off);
        check_post_shop_off.setOnCheckedChangeListener(this);
        check_post_shop_friend = (CheckBox)findViewById(R.id.check_post_shop_friend);
        check_post_shop_friend.setOnCheckedChangeListener(this);
        check_post_shop_every = (CheckBox)findViewById(R.id.check_post_shop_every);
        check_post_shop_every.setOnCheckedChangeListener(this);

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

    private void parsingNotificationSettingInfo(JSONObject jsonObject) {

        isUpvote = jsonObject.optBoolean(Notification.UPVOTED_POST);
        isCommentUpvote = jsonObject.optBoolean(Notification.UPVOTED_COMMENT);
        isComments = jsonObject.optBoolean(Notification.COMMENTED);
        isCollect = jsonObject.optBoolean(Notification.FOLLOWED);

        str_post_photo = jsonObject.optString(Notification.CREATE_POST);
        str_first_post = jsonObject.optString(Notification.CREATED_FIRST_POST);
        str_post_goods = jsonObject.optString(Notification.CREATED_DIGITAL_POST);
        str_post_shop = jsonObject.optString(Notification.CREATED_SHOP_POST);

        if (requestType.equals(REQUEST_GET_SETTINGS)) {

            switch_upvote.setChecked(isUpvote);
            switch_comment_upvote.setChecked(isCommentUpvote);
            switch_comments.setChecked(isComments);
            switch_collect.setChecked(isCollect);

            if (str_post_photo.equals(Notification.RESULT_OFF)) check_post_photo_off.setChecked(true);
            else if (str_post_photo.equals(Notification.RESULT_FRIEND)) check_post_photo_friend.setChecked(true);

            switch (str_first_post) {

                case Notification.RESULT_OFF:
                    check_first_post_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_first_post_friend.setChecked(true);
                    break;
                case Notification.RESULT_EVERYONE:
                    check_first_post_every.setChecked(true);
                    break;
            }

            switch (str_post_goods) {

                case Notification.RESULT_OFF:
                    check_post_goods_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_post_goods_friend.setChecked(true);
                    break;
                case Notification.RESULT_EVERYONE:
                    check_post_goods_every.setChecked(true);
                    break;
            }

            switch (str_post_shop) {

                case Notification.RESULT_OFF:
                    check_post_shop_off.setChecked(true);
                    break;
                case Notification.RESULT_FRIEND:
                    check_post_shop_friend.setChecked(true);
                    break;
                case Notification.RESULT_EVERYONE:
                    check_post_shop_every.setChecked(true);
                    break;
            }

        }
    }

    private void changedFirstPostCheckbox() {

        switch (str_first_post) {

            case Notification.RESULT_OFF:
                check_first_post_off.setChecked(true);
                check_first_post_friend.setChecked(false);
                check_first_post_every.setChecked(false);
                break;
            case Notification.RESULT_FRIEND:
                check_first_post_off.setChecked(false);
                check_first_post_friend.setChecked(true);
                check_first_post_every.setChecked(false);
                break;
            case Notification.RESULT_EVERYONE:
                check_first_post_off.setChecked(false);
                check_first_post_friend.setChecked(false);
                check_first_post_every.setChecked(true);
                break;
        }

    }

    private void changedPostGoodsCheckbox() {

        switch (str_post_goods) {

            case Notification.RESULT_OFF:
                check_post_goods_off.setChecked(true);
                check_post_goods_friend.setChecked(false);
                check_post_goods_every.setChecked(false);
                break;
            case Notification.RESULT_FRIEND:
                check_post_goods_off.setChecked(false);
                check_post_goods_friend.setChecked(true);
                check_post_goods_every.setChecked(false);
                break;
            case Notification.RESULT_EVERYONE:
                check_post_goods_off.setChecked(false);
                check_post_goods_friend.setChecked(false);
                check_post_goods_every.setChecked(true);
                break;
        }

    }

    private void changedPostShopCheckbox() {

        switch (str_post_shop) {

            case Notification.RESULT_OFF:
                check_post_shop_off.setChecked(true);
                check_post_shop_friend.setChecked(false);
                check_post_shop_every.setChecked(false);
                break;
            case Notification.RESULT_FRIEND:
                check_post_shop_off.setChecked(false);
                check_post_shop_friend.setChecked(true);
                check_post_shop_every.setChecked(false);
                break;
            case Notification.RESULT_EVERYONE:
                check_post_shop_off.setChecked(false);
                check_post_shop_friend.setChecked(false);
                check_post_shop_every.setChecked(true);
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
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_SETTINGS)) {

                getNotificationSettings();

            } else if (requestType.equals(REQUEST_UPDATE_NOTIFICATION)) {

                setNotificationState(mCurrentFlag, mCurrentString, mCurrentKey);

            }

        } else {

            if (objResult != null) parsingNotificationSettingInfo(objResult);

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

        if (view == switch_upvote) {

            if (isChecked) {
                if (!isUpvote) setNotificationState(true, null, Notification.UPVOTED_POST);
            } else {
                if (isUpvote) setNotificationState(false, null, Notification.UPVOTED_POST);
            }

        } else if (view == switch_comment_upvote) {

            if (isChecked) {
                if (!isCommentUpvote) setNotificationState(true, null, Notification.UPVOTED_COMMENT);
            } else {
                if (isCommentUpvote) setNotificationState(false, null, Notification.UPVOTED_COMMENT);
            }

        } else if (view == switch_comments) {

            if (isChecked) {
                if (!isComments) setNotificationState(true, null, Notification.COMMENTED);
            } else {
                if (isComments) setNotificationState(false, null, Notification.COMMENTED);
            }

        } else if (view == switch_collect) {

            if (isChecked) {
                if (!isCollect) setNotificationState(true, null, Notification.FOLLOWED);
            } else {
                if (isCollect) setNotificationState(false, null, Notification.FOLLOWED);
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton == check_post_photo_off) {

            if (b) {

                if (!str_post_photo.equals(Notification.RESULT_OFF)) {
                    str_post_photo = Notification.RESULT_OFF;
                    setNotificationState(false, str_post_photo, Notification.CREATE_POST);
                }
            }

            if (str_post_photo.equals(Notification.RESULT_OFF)) {
                check_post_photo_off.setChecked(true);
                check_post_photo_friend.setChecked(false);
            }

        } else if (compoundButton == check_post_photo_friend) {

            if (b) {

                if (!str_post_photo.equals(Notification.RESULT_FRIEND)) {
                    str_post_photo = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_post_photo, Notification.CREATE_POST);
                }
            }

            if (str_post_photo.equals(Notification.RESULT_FRIEND)) {
                check_post_photo_friend.setChecked(true);
                check_post_photo_off.setChecked(false);
            }

        } else if (compoundButton == check_first_post_off) {

            if (b) {

                if (!str_first_post.equals(Notification.RESULT_OFF)) {
                    str_first_post = Notification.RESULT_OFF;
                    setNotificationState(false, str_first_post, Notification.CREATED_FIRST_POST);
                }
            }

            changedFirstPostCheckbox();

        } else if (compoundButton == check_first_post_friend) {

            if (b) {

                if (!str_first_post.equals(Notification.RESULT_FRIEND)) {
                    str_first_post = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_first_post, Notification.CREATED_FIRST_POST);
                }
            }

            changedFirstPostCheckbox();

        } else if (compoundButton == check_first_post_every) {

            if (b) {

                if (!str_first_post.equals(Notification.RESULT_EVERYONE)) {
                    str_first_post = Notification.RESULT_EVERYONE;
                    setNotificationState(false, str_first_post, Notification.CREATED_FIRST_POST);
                }
            }

            changedFirstPostCheckbox();

        } else if (compoundButton == check_post_goods_off) {

            if (b) {

                if (!str_post_goods.equals(Notification.RESULT_OFF)) {
                    str_post_goods = Notification.RESULT_OFF;
                    setNotificationState(false, str_post_goods, Notification.CREATED_DIGITAL_POST);
                }
            }

            changedPostGoodsCheckbox();

        } else if (compoundButton == check_post_goods_friend) {

            if (b) {

                if (!str_post_goods.equals(Notification.RESULT_FRIEND)) {
                    str_post_goods = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_post_goods, Notification.CREATED_DIGITAL_POST);
                }
            }

            changedPostGoodsCheckbox();

        } else if (compoundButton == check_post_goods_every) {

            if (b) {

                if (!str_post_goods.equals(Notification.RESULT_EVERYONE)) {
                    str_post_goods = Notification.RESULT_EVERYONE;
                    setNotificationState(false, str_post_goods, Notification.CREATED_DIGITAL_POST);
                }
            }

            changedPostGoodsCheckbox();

        } else if (compoundButton == check_post_shop_off) {

            if (b) {

                if (!str_post_shop.equals(Notification.RESULT_OFF)) {
                    str_post_shop = Notification.RESULT_OFF;
                    setNotificationState(false, str_post_shop, Notification.CREATED_SHOP_POST);
                }
            }

            changedPostShopCheckbox();

        } else if (compoundButton == check_post_shop_friend) {

            if (b) {

                if (!str_post_shop.equals(Notification.RESULT_FRIEND)) {
                    str_post_shop = Notification.RESULT_FRIEND;
                    setNotificationState(false, str_post_shop, Notification.CREATED_SHOP_POST);
                }
            }

            changedPostShopCheckbox();

        } else if (compoundButton == check_post_shop_every) {

            if (b) {

                if (!str_post_shop.equals(Notification.RESULT_EVERYONE)) {
                    str_post_shop = Notification.RESULT_EVERYONE;
                    setNotificationState(false, str_post_shop, Notification.CREATED_SHOP_POST);
                }
            }

            changedPostShopCheckbox();

        }

    }
}
