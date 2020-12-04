package com.star.pibbledev.profile.activity.setting.account;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class SettingAccountUsernameActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    ImageButton img_back;
    LinearLayout linear_done;
    EditText edt_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_account_username);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        linear_done = (LinearLayout)findViewById(R.id.linear_done);
        linear_done.setOnClickListener(this);
        edt_username = (EditText)findViewById(R.id.edt_username);

        edt_username.setText(Utility.getReadPref(this).getStringValue(Constants.USERNAME));
//        edt_username.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                if (s.toString().length() > 0) {
//
//                }
//
//            }
//        });

    }

    private void updateUsername() {

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateUsername(this, this, token, edt_username.getText().toString());

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_done) {

            updateUsername();
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            Utility.getSavedPref(this).saveString(Constants.USERNAME, edt_username.getText().toString());

            String msg = objResult.optString("message");

            AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                    msg,
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {
                    dismiss();
                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else {

            Constants.saveRefreshToken(this, objResult);

            updateUsername();

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
