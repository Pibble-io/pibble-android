package com.star.pibbledev.wallet.forgot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.HomeWalletActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResetPinCodeActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    RelativeLayout relative_1, relative_2, relative_3, relative_4, relative_5, relative_6, relative_7, relative_8, relative_9, relative_0, relative_cancel, relative_back;
    ImageView img_1, img_2, img_3, img_4;
    LinearLayout linear_pinview;
    TextView txt_content;

    String str_pin, str_pinConfirm;

    boolean mflag_confirm, flag_expireToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_resetpin);

        relative_1 = (RelativeLayout)findViewById(R.id.relative_1);
        relative_1.setOnClickListener(this);
        relative_2 = (RelativeLayout)findViewById(R.id.relative_2);
        relative_2.setOnClickListener(this);
        relative_3 = (RelativeLayout)findViewById(R.id.relative_3);
        relative_3.setOnClickListener(this);
        relative_4 = (RelativeLayout)findViewById(R.id.relative_4);
        relative_4.setOnClickListener(this);
        relative_5 = (RelativeLayout)findViewById(R.id.relative_5);
        relative_5.setOnClickListener(this);
        relative_6 = (RelativeLayout)findViewById(R.id.relative_6);
        relative_6.setOnClickListener(this);
        relative_7 = (RelativeLayout)findViewById(R.id.relative_7);
        relative_7.setOnClickListener(this);
        relative_8 = (RelativeLayout)findViewById(R.id.relative_8);
        relative_8.setOnClickListener(this);
        relative_9 = (RelativeLayout)findViewById(R.id.relative_9);
        relative_9.setOnClickListener(this);
        relative_0 = (RelativeLayout)findViewById(R.id.relative_0);
        relative_0.setOnClickListener(this);
        relative_cancel = (RelativeLayout)findViewById(R.id.relative_cancel);
        relative_cancel.setOnClickListener(this);
        relative_back = (RelativeLayout)findViewById(R.id.relative_back);
        relative_back.setOnClickListener(this);

        linear_pinview = (LinearLayout)findViewById(R.id.linear_pinview);

        img_1 = (ImageView)findViewById(R.id.img_1);
        img_2 = (ImageView)findViewById(R.id.img_2);
        img_3 = (ImageView)findViewById(R.id.img_3);
        img_4 = (ImageView)findViewById(R.id.img_4);

        txt_content = (TextView)findViewById(R.id.txt_content);
        txt_content.setText(R.string.reset_pin);

        str_pin = "";
        str_pinConfirm = "";
        mflag_confirm = true;

        flag_expireToken = false;

    }

    private void getPinAction(String string) {

        if (string.length() == 0) {
            img_1.setImageResource(R.drawable.icon_pin_diselected);
            img_2.setImageResource(R.drawable.icon_pin_diselected);
            img_3.setImageResource(R.drawable.icon_pin_diselected);
            img_4.setImageResource(R.drawable.icon_pin_diselected);
        } else if (string.length() == 1) {
            img_1.setImageResource(R.drawable.icon_pin_selected);
            img_2.setImageResource(R.drawable.icon_pin_diselected);
            img_3.setImageResource(R.drawable.icon_pin_diselected);
            img_4.setImageResource(R.drawable.icon_pin_diselected);
        } else if (string.length() == 2) {
            img_1.setImageResource(R.drawable.icon_pin_selected);
            img_2.setImageResource(R.drawable.icon_pin_selected);
            img_3.setImageResource(R.drawable.icon_pin_diselected);
            img_4.setImageResource(R.drawable.icon_pin_diselected);
        } else if (string.length() == 3) {
            img_1.setImageResource(R.drawable.icon_pin_selected);
            img_2.setImageResource(R.drawable.icon_pin_selected);
            img_3.setImageResource(R.drawable.icon_pin_selected);
            img_4.setImageResource(R.drawable.icon_pin_diselected);
        } else if (string.length() == 4) {
            img_1.setImageResource(R.drawable.icon_pin_selected);
            img_2.setImageResource(R.drawable.icon_pin_selected);
            img_3.setImageResource(R.drawable.icon_pin_selected);
            img_4.setImageResource(R.drawable.icon_pin_selected);
        }
    }

    private String getBactString(String str) {

        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void pinAnimation() {
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vibrate_animation);
        linear_pinview.startAnimation(shake);
    }

    @Override
    public void onClick(View v) {

        Utility.scaleView(v, 0.8f, 1.0f, 300);

        if (str_pin.length() < 4) {

            if (v == relative_0) {
                str_pin = str_pin + "0";
            } else if (v == relative_1) {
                str_pin = str_pin + "1";
            } else if (v == relative_2) {
                str_pin = str_pin + "2";
            } else if (v == relative_3) {
                str_pin = str_pin + "3";
            } else if (v == relative_4) {
                str_pin = str_pin + "4";
            } else if (v == relative_5) {
                str_pin = str_pin + "5";
            } else if (v == relative_6) {
                str_pin = str_pin + "6";
            } else if (v == relative_7) {
                str_pin = str_pin + "7";
            } else if (v == relative_8) {
                str_pin = str_pin + "8";
            } else if (v == relative_9) {
                str_pin = str_pin + "9";
            }
        }

        if (v == relative_back) {

            str_pin = getBactString(str_pin);

        } else if (v == relative_cancel) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
            return;

        }

        getPinAction(str_pin);

        if (str_pin.length() == 4) {

            if (mflag_confirm) {

                txt_content.setText(R.string.wallet_passcode_confirm);
                str_pinConfirm = str_pin;
                str_pin = "";
                mflag_confirm = false;

            } else {

                if (str_pin.equals(str_pinConfirm)) {
                    showHUD();
                    String token = getIntent().getStringExtra("token");
                    String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                    ServerRequest.getSharedServerRequest().restorePinCode(this, this, str_pin, token, access_token);
                } else {
                    pinAnimation();
                    Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.confrim_msg), getString(R.string.ok));
                }
            }
        }

    }

    @Override
    public void succeeded(JSONObject objResult) {
        hideHUD();

        new AlertView(getString(R.string.success), getString(R.string.pincode_changed_msg), getString(R.string.ok), null,
                new String[]{},
                this, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){

                Intent intent = new Intent(ResetPinCodeActivity.this, HomeWalletActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }

        }).show();
    }

    @Override
    public void failed(String strError, int errorCode) {
        hideHUD();
        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}