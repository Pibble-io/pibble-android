package com.star.pibbledev.wallet.forgot;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.PinView;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfirmCodeActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String RESET_PIN_EMAIL = "email";
    public static final String RESET_PIN_PHONE = "sms";
    public static final String RESET_PIN_TYPE = "type";

    ImageButton img_back;
    TextView txt_content;
    PinView firstPinView;
    Button btn_resend;

    boolean flag_reset_type, isResend;
    int cnt_sendcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_forgot_confirm_code);

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_content = (TextView)findViewById(R.id.txt_content);
        firstPinView = (PinView)findViewById(R.id.firstPinView);
        btn_resend = (Button)findViewById(R.id.btn_resend);
        btn_resend.setOnClickListener(this);

        firstPinView = (PinView)findViewById(R.id.firstPinView);
        firstPinView.setAnimationEnable(true);
        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {

                    if (cnt_sendcode > 4) {

                        Utility.getSavedPref(ConfirmCodeActivity.this).saveString(Constants.RESET_PINCODE_FAILED_LIMIT, Utility.getCurrentDate());

                        AlertVerticalDialog dialog = new AlertVerticalDialog(ConfirmCodeActivity.this, getString(R.string.oh_snap),
                                getString(R.string.error_failed_5times_reset_pincode),
                                null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                            @Override
                            public void onClickButton(int position) {

                                if (position == 1) {

                                    Utility.dismissKeyboard(ConfirmCodeActivity.this);

                                    Intent intent = new Intent(ConfirmCodeActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                }

                                dismiss();

                            }

                        };
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                    } else {

                        Utility.getSavedPref(ConfirmCodeActivity.this).saveString(Constants.RESET_PINCODE_FAILED_LIMIT, "");

                        cnt_sendcode++;

                        showHUD();

                        isResend = true;

                        if (flag_reset_type) {
                            ServerRequest.getSharedServerRequest().verifyConfirmSMS(ConfirmCodeActivity.this, ConfirmCodeActivity.this, s.toString());
                        } else {
                            ServerRequest.getSharedServerRequest().verifyConfrimEmail(ConfirmCodeActivity.this, ConfirmCodeActivity.this, s.toString());
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendPincode();

        if (getIntent().getStringExtra(RESET_PIN_TYPE).equals(RESET_PIN_EMAIL)) {
            txt_content.setText(getIntent().getStringExtra(RESET_PIN_EMAIL));
            flag_reset_type = false;
        } else {
            txt_content.setText(getIntent().getStringExtra(RESET_PIN_PHONE));
            flag_reset_type = true;
        }

        isResend = false;

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                btn_resend.setText(getString(R.string.resend_code_after) + " " + millisUntilFinished / 1000);
                btn_resend.setEnabled(false);
            }

            public void onFinish() {
                btn_resend.setText(getString(R.string.button_resend_code));
                btn_resend.setEnabled(true);
            }

        }.start();

    }

    private void sendPincode() {

        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        String email = Utility.getReadPref(this).getStringValue("email");
        ServerRequest.getSharedServerRequest().sendVerifyCodeViaEmail(this, this, email, "pincode_reset", access_token);
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_resend){

            isResend = false;

            String type = getIntent().getStringExtra(RESET_PIN_TYPE);

            if (type.equals(RESET_PIN_EMAIL)) {

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().sendVerifyCodeViaEmail(this, this, getIntent().getStringExtra(RESET_PIN_EMAIL), "pincode_reset", access_token);

            } else if (type.equals(RESET_PIN_PHONE)) {

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                String str_phone = Utility.getReadPref(this).getStringValue("phone");
                int country_id = Utility.getReadPref(this).getIntValue("country_id");

                ServerRequest.getSharedServerRequest().confirmSendsms(this, this, str_phone, String.valueOf(country_id), "pincode_reset", access_token, true);

            }

            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    btn_resend.setText(getString(R.string.resend_code_after) + " " + millisUntilFinished / 1000);
                    btn_resend.setEnabled(false);
                }

                public void onFinish() {
                    btn_resend.setText(getString(R.string.button_resend_code));
                    btn_resend.setEnabled(true);
                }

            }.start();

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        hideHUD();

        if (isResend) {

            String token = objResult.optString("token");

            Intent intent = new Intent(this, ResetPinCodeActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        Utility.showKeyboard(this);

        firstPinView.setText("");

        Utility.parseError(this, strError);
        hideHUD();
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
