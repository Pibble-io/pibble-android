package com.star.pibbledev.profile.activity.setting.invite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.adaptar.ItemReferralUser;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.ActionStatusDialog;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SettingInviteFriendActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    private static final String REQUEST_GET_INVITE_MSG = "request_get_invite_msg";
    private static final String REQUEST_REGISTER_REFERRAL = "request_register_referral";
    private static final String REQUEST_REFERRAL_USERS = "request_referral_users";
    private static final String REQUEST_OWNER_USED_REFERRAL = "request_owner_used_referral";

    ImageButton img_back;
    TextView txt_invitecode, txt_register, txt_get_prb;
    LinearLayout linear_copy, linear_invite, linear_register, linear_edittext, linear_referral_users;
    EditText edit_friendid;

    private String requestType;
    private ArrayList<UserModel> ary_referrals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_invite);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_invitecode = (TextView)findViewById(R.id.txt_invitecode);
        txt_register = (TextView)findViewById(R.id.txt_register);
        txt_get_prb = (TextView)findViewById(R.id.txt_get_prb);
        edit_friendid = (EditText)findViewById(R.id.edit_friendid);
        linear_copy = (LinearLayout)findViewById(R.id.linear_copy);
        linear_copy.setOnClickListener(this);
        linear_invite = (LinearLayout)findViewById(R.id.linear_invite);
        linear_invite.setOnClickListener(this);
        linear_register = (LinearLayout)findViewById(R.id.linear_register);
        linear_register.setOnClickListener(this);
        linear_edittext = (LinearLayout)findViewById(R.id.linear_edittext);
        linear_referral_users = (LinearLayout)findViewById(R.id.linear_referral_users);

        txt_invitecode.setText(Utility.getReadPref(this).getStringValue(Constants.REFERRAL));

        getReferralUsers();

    }

    private void getInviteMessage() {

        requestType = REQUEST_GET_INVITE_MSG;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getInviteMessage(this, this, access_token);

        } else {

            Constants.requestRefreshToken(this, this);

        }

    }

    private void registerReferral() {

        requestType = REQUEST_REGISTER_REFERRAL;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().registerReferral(this, this, access_token, edit_friendid.getText().toString());

        } else {

            Constants.requestRefreshToken(this, this);

        }

    }

    private void getReferralUsers() {

        requestType = REQUEST_REFERRAL_USERS;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getReferralUsers(this, this, access_token);

        } else {

            Constants.requestRefreshToken(this, this);

        }
    }

    private void getOwnerUsername(){

        requestType = REQUEST_OWNER_USED_REFERRAL;

        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getOwnerUsedReferral(this, this, access_token);
    }

    private void sendingInvite(JSONObject jsonObject) {

        if (jsonObject == null) return;

        String msg = jsonObject.optString("message");

        Utility.shareTextAction(this, msg, getString(R.string.pibble), getString(R.string.share_message));

    }

    private void changeButtonAfterRegistered() {
        linear_edittext.setBackground(getResources().getDrawable(R.drawable.linear_corner6_gray_background_lightgray));
        linear_register.setEnabled(false);
        txt_register.setTextColor(getResources().getColor(R.color.line_background_color));
        edit_friendid.setEnabled(false);
        edit_friendid.clearFocus();
    }

    private void referralUsersView(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.optJSONArray("items");
        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {

                    JSONObject object = (JSONObject)jsonArray.get(i);

                    if (object == null) continue;

                    JSONObject userObj = object.optJSONObject("user");

                    if (userObj == null) continue;

                    UserModel userModel = new UserModel();
                    userModel.id = userObj.optInt(Constants.ID);
                    userModel.username = userObj.optString(Constants.USERNAME);
                    userModel.avatar = userObj.optString(Constants.AVATAR);
                    if (userModel.avatar == null || userModel.avatar.equals("null") || userModel.avatar.length() == 0) {

                        if (userModel.username.length() > 14) userModel.avatar_temp = 14;
                        else userModel.avatar_temp = userModel.username.length();

                    }

                    ary_referrals.add(userModel);

                    ItemReferralUser referralUser = new ItemReferralUser(this);
                    referralUser.setImage(userModel);

                    linear_referral_users.addView(referralUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        for (int i = 0; i < 10 - ary_referrals.size(); i++) {

            ItemReferralUser referralUser = new ItemReferralUser(this);
            referralUser.setImage(null);

            linear_referral_users.addView(referralUser);

        }

        int getPRB = ary_referrals.size() * Constants.PRB_REFERRAL;
        String str_gotPRB = String.valueOf(getPRB) + getString(R.string.prb);

        txt_get_prb.setText(str_gotPRB);
    }

    @Override
    public void onClick(View v) {

        if (v == linear_copy) {

            ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("inviteCode", txt_invitecode.getText().toString());
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);

            ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.copied));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);

        } else if (v == linear_register) {

            registerReferral();

        } else if (v == linear_invite) {

            getInviteMessage();

        } else if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            switch (requestType) {

                case REQUEST_GET_INVITE_MSG:

                    getInviteMessage();

                    break;
                case REQUEST_REGISTER_REFERRAL:

                    registerReferral();

                    break;
                case REQUEST_REFERRAL_USERS:

                    getReferralUsers();

                    break;
            }

        } else {

            switch (requestType) {

                case REQUEST_GET_INVITE_MSG:

                    sendingInvite(objResult);

                    break;
                case REQUEST_REGISTER_REFERRAL:

                    AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                            getString(R.string.invite_regiser_success),
                            null, getString(R.string.okay), R.color.black, R.color.black) {

                        @Override
                        public void onClickButton(int position) {

                            dismiss();

                            changeButtonAfterRegistered();
                        }

                    };
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    break;
                case REQUEST_REFERRAL_USERS:

                    referralUsersView(objResult);

                    getOwnerUsername();

                    break;
                case REQUEST_OWNER_USED_REFERRAL:

                    if (objResult != null) {

                        String ownername = objResult.optString("referral");
                        if (ownername != null && !ownername.equals("null")) {
                            changeButtonAfterRegistered();
                            edit_friendid.setText(ownername);
                        }
                    }

                    break;
            }

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (!Utility.g_isCalledRefreshToken) Utility.parseError(this, strError);

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
