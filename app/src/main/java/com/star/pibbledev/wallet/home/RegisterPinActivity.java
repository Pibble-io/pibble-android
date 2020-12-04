package com.star.pibbledev.wallet.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.exchange.ExchangeActivity;
import com.star.pibbledev.wallet.forgot.ConfirmCodeActivity;
import com.star.pibbledev.wallet.send.SendToSelectActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class RegisterPinActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    private static final String REQUEST_CHECK_PIN = "request_check_pin";
    private static final String REQUEST_WALLET_ACTION = "request_wallet_action";

    public static final String TYPE_ACTION = "typeActivity";
    public static final String IS_EXPIRED = "is_expired";

    RelativeLayout relative_1, relative_2, relative_3, relative_4, relative_5, relative_6, relative_7, relative_8, relative_9, relative_0, relative_cancel, relative_back;
    ImageView img_1, img_2, img_3, img_4;
    LinearLayout linear_pinview;
    TextView txt_content;
    Button btn_forgotpin;

    String str_pin, str_pinConfirm;

    boolean mflag_confirm, flag_expireToken;

    String str_ActivityType;
    private String requestType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_passcode);

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
        btn_forgotpin = (Button)findViewById(R.id.btn_forgotpin);
        btn_forgotpin.setOnClickListener(this);

        str_pin = "";
        str_pinConfirm = "";
        mflag_confirm = true;

        flag_expireToken = false;

        String str_pin_code = Utility.getReadPref(this).getStringValue("pin_code_presence");
        if (str_pin_code.equals("1")) {
            txt_content.setText(R.string.wallet_passcode_uplock);
        } else {
            txt_content.setText(R.string.wallet_passcode_register);
        }

        str_ActivityType = getIntent().getStringExtra(TYPE_ACTION);

    }

    @Override
    public void onResume() {

        super.onResume();

        long currentTime = System.currentTimeMillis() / 1000;

        String expires_in =  Utility.getReadPref(this).getStringValue(Constants.AUTH_EXPIRED_TIME);

        if (expires_in.equals("null") || expires_in.equals("")) expires_in = "0";

        if (Long.parseLong(expires_in) > currentTime) {
            flag_expireToken = true;

        } else {
            flag_expireToken = false;
            String refresh_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_REFRESH_TOKEN);
            ServerRequest.getSharedServerRequest().refreshToken(this, this, refresh_token);
        }

    }

    private void getPinAction(String string) {

        if (string.length() == 0) {
            img_1.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_2.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_3.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_4.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
        } else if (string.length() == 1) {
            img_1.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_2.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_3.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_4.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
        } else if (string.length() == 2) {
            img_1.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_2.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_3.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
            img_4.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
        } else if (string.length() == 3) {
            img_1.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_2.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_3.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_4.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_diselected));
        } else if (string.length() == 4) {
            img_1.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_2.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_3.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
            img_4.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_pin_selected));
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

        if (v == relative_back) {

            str_pin = getBactString(str_pin);
            getPinAction(str_pin);

        } else if (v == relative_cancel) {

            if (Constants.isLifeToken(this)) {

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            } else {

                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }


        } else if (v == btn_forgotpin) {

            Intent intent = new Intent(this, ConfirmCodeActivity.class);

            intent.putExtra(ConfirmCodeActivity.RESET_PIN_TYPE, ConfirmCodeActivity.RESET_PIN_EMAIL);
            intent.putExtra(ConfirmCodeActivity.RESET_PIN_EMAIL, Utility.getReadPref(this).getStringValue("email"));

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {

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

            getPinAction(str_pin);

            if (str_pin.length() == 4) {

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                String str_pin_code = Utility.getReadPref(this).getStringValue("pin_code_presence");

                if (str_pin_code.equals("1")) {

                    str_pinConfirm = str_pin;
                    str_pin = "";

                    showHUD();

                    requestType = REQUEST_CHECK_PIN;

                    if (Constants.isLifeToken(this)) {
                        ServerRequest.getSharedServerRequest().checkPincode(this, this, str_pinConfirm, access_token);
                    } else {
                        Constants.requestRefreshToken(this, this);
                        flag_expireToken = false;
                    }

                } else {

                    if (mflag_confirm) {

                        txt_content.setText(R.string.wallet_passcode_confirm);
                        str_pinConfirm = str_pin;
                        str_pin = "";
                        mflag_confirm = false;

                    } else {

                        if (str_pin.equals(str_pinConfirm)) {
                            showHUD();
                            ServerRequest.getSharedServerRequest().createPinCode(this, this, str_pin, access_token);
                        } else {
                            pinAnimation();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (flag_expireToken) {

            if (str_ActivityType == null || Utility.getReadPref(this).getStringValue("pin_code_presence").equals("0")) {

                hideHUD();

                boolean isExpired = getIntent().getBooleanExtra(IS_EXPIRED, false);

                if (isExpired) {

                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else {

                    Utility.getSavedPref(this).saveString("pin_code_presence", "1");

                    if (str_ActivityType != null) {

                        txt_content.setText(R.string.wallet_passcode_uplock);
                        str_pin = "";
                        getPinAction(str_pin);

                    } else {
                        Intent intent = new Intent(this, HomeWalletActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                }

            } else if (str_ActivityType != null && Utility.getReadPref(this).getStringValue("pin_code_presence").equals("1")){

                List<String> params = Arrays.asList(str_ActivityType.split(","));

                if (requestType.equals(REQUEST_CHECK_PIN)) {

                    requestType = REQUEST_WALLET_ACTION;

                    String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                    switch (params.get(0)) {

                        case ExchangeActivity.TYPE_EXCHANGE:

                            ServerRequest.getSharedServerRequest().requestExchange(this, this, token, params.get(1), params.get(2), params.get(3));

                            break;
                        case SendToSelectActivity.TYPE_SEND_TO_ADDRESS:

                            ServerRequest.getSharedServerRequest().sendToAddress(this, this, token, params.get(1), Float.parseFloat(params.get(2)), params.get(3), str_pinConfirm);

                            break;
                        case SendToSelectActivity.TYPE_SEND_TO_UUID:

                            ServerRequest.getSharedServerRequest().sendToFriendUUID(this, this, token, params.get(1), Float.parseFloat(params.get(2)), params.get(3), str_pinConfirm);

                            break;
                        case Constants.REQUEST_ACCEPT_WALLET_INVOICE:

                            ServerRequest.getSharedServerRequest().acceptRequestWallet(this, this, token, (int) (Float.parseFloat(params.get(1))), str_pinConfirm);

                            break;
                        case Constants.REQUEST_ACCEPT_INVOICE:

                            ServerRequest.getSharedServerRequest().acceptInvoiceBuyItem(this, this, token, (int) (Float.parseFloat(params.get(1))), str_pinConfirm);

                            break;
                        case Constants.REQUEST_CREATE_PROMOTION:

                            ServerRequest.getSharedServerRequest().createPromotion(this, this, token, Utility.mainFeedParams, str_pinConfirm);

                            break;
                        case Constants.REQUEST_CREATE_GOODS_ORDER:

                            ServerRequest.getSharedServerRequest().createGoodsOrder(this, this, token, params.get(2), params.get(3), params.get(1), str_pinConfirm);
                            break;
                        case Constants.REQUEST_CREATE_ASKING_HELP:

                            ServerRequest.getSharedServerRequest().createHelp(this, this, token, Integer.parseInt(params.get(1)), params.get(3), Integer.valueOf(params.get(2)), str_pinConfirm);
                            break;
                    }

                } else if (requestType.equals(REQUEST_WALLET_ACTION)){

                    hideHUD();

                    Utility.g_isChanged = true;

                    if (params.get(0).equals(Constants.REQUEST_CREATE_GOODS_ORDER)) Utility.g_isChangedType = Constants.GOODS_ORDER_CREATED;

                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            }

        } else {

            Constants.saveRefreshToken(this, objResult);
            flag_expireToken = true;
            if (requestType.equals(REQUEST_CHECK_PIN)) {
                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().checkPincode(this, this, str_pinConfirm, access_token);
            }
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

        if (str_ActivityType == null) {
            str_pin = "";
            getPinAction(str_pin);
            pinAnimation();

            if (Constants.isLifeToken(this)) Utility.parseError(this, strError);

        } else {

            if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
        }

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}