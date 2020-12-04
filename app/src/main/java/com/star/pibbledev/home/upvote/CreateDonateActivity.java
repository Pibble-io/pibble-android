package com.star.pibbledev.home.upvote;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateDonateActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    private static final String SUCCESS_BALANCE = "balance";
    private static final String SUCCESS_UPVOTE = "upvote";
    private static final String SUCCESS_EXCHANGE = "exchange";

    public static final String POST_ID = "post_id";
    public static final String PROMO_ID = "promo_id";
    public static final String CURRENT_PRICE = "current_price";

    private static String VALUE_PIB;
    private static String VALUE_PRB;
    private static String VALUE_PGB;

    Button btn_cancel, btn_upvote;
    LinearLayout linear_min, linear_max, linear_edittext;
    SeekBar seekbar;
    TextView txt_unit;
    EditText txt_brush;
    ImageButton img_change_wallet;

    String str_unit;
    int post_id, promo_id, type_balance, current_price;

    private String requestType;
    private boolean isManual;

    String fromValue, toValue;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_charity_donate);

        setLightStatusBar();

        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        btn_upvote = (Button)findViewById(R.id.btn_upvote);
        btn_upvote.setOnClickListener(this);

        linear_min = (LinearLayout)findViewById(R.id.linear_min);
        linear_min.setOnClickListener(this);
        linear_max = (LinearLayout)findViewById(R.id.linear_max);
        linear_max.setOnClickListener(this);
        linear_edittext = (LinearLayout)findViewById(R.id.linear_edittext);
        linear_edittext.setOnClickListener(this);
        img_change_wallet = (ImageButton)findViewById(R.id.img_change_wallet);
        img_change_wallet.setOnClickListener(this);
        img_change_wallet.setVisibility(View.GONE);

        txt_brush = (EditText)findViewById(R.id.txt_brush);
        txt_brush.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    txt_brush.setBackgroundResource(R.drawable.icon_edittext_background);
                    txt_brush.setCursorVisible(true);
                    Utility.showKeyboard(CreateDonateActivity.this);
                    seekbar.setVisibility(View.INVISIBLE);

                    isManual = true;
                }

                return false;
            }
        });
        txt_brush.setBackgroundResource(R.color.transparent);
        txt_brush.setCursorVisible(false);

        txt_unit = (TextView)findViewById(R.id.txt_unit);

        post_id = getIntent().getIntExtra(POST_ID, -1);
        promo_id = getIntent().getIntExtra(PROMO_ID, -1);
        current_price = getIntent().getIntExtra(CURRENT_PRICE, -1);

        if (current_price != -1) {
            linear_edittext.setEnabled(false);
        }

        seekbar = (SeekBar)findViewById(R.id.upvote_seekbar);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if (current_price != -1) {

                    if (progress < current_price) setAction(false);
                    else setAction(true);

                    progress = progress / current_price;
                    progress = progress * current_price;
                }

                txt_brush.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        type_balance = 0;

        str_unit = "pib";
        txt_unit.setText(str_unit.toUpperCase());
        txt_unit.setTextColor(this.getResources().getColor(R.color.colorMain));

        getBalanceData();
    }

    private void setAction(boolean isEnable) {

        btn_upvote.setEnabled(isEnable);

        if (isEnable) {
            btn_upvote.setTextColor(getResources().getColor(R.color.colorMain));
        } else {
            btn_upvote.setTextColor(getResources().getColor(R.color.grey_464646));
        }
    }

    private void getBalanceData() {

        requestType = SUCCESS_BALANCE;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getBalances(this, this, token);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void sendUpvote() {

        requestType = SUCCESS_UPVOTE;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            String amount = txt_brush.getText().toString();
            ServerRequest.getSharedServerRequest().sendingDonate(this,this, access_token, post_id, current_price, Integer.parseInt(amount), str_unit.toUpperCase());

        } else {

            Constants.requestRefreshToken(this, this);

        }

    }

    private void getExchangeRate(String from, String to) {

        requestType = SUCCESS_EXCHANGE;

        fromValue = from;
        toValue = to;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getExchangeRate(this, this, access_token, from, to);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void exchangedValue(JSONObject jsonObject) {

        if (jsonObject != null) {
            String value = jsonObject.optString("rate");

            String oldValue = txt_brush.getText().toString();
            float newValue = Float.parseFloat(value) * Float.parseFloat(oldValue);
            txt_brush.setText(String.valueOf((int) newValue));
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btn_cancel) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (view == linear_min) {

            final int[] value = {seekbar.getProgress()};
            ValueAnimator anim = ValueAnimator.ofInt(value[0], 0);
            anim.setDuration(1000);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(
                        ValueAnimator animation) {
                    value[0] = (Integer) animation
                            .getAnimatedValue();
                    seekbar.setProgress(value[0]);
                }
            });
            anim.setInterpolator(new BounceInterpolator());
            anim.start();

        } else if (view == linear_max) {

            final int[] value = {seekbar.getProgress()};
            ValueAnimator anim = ValueAnimator.ofInt(value[0], seekbar.getMax());
            anim.setDuration(1000);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(
                        ValueAnimator animation) {
                    value[0] = (Integer) animation
                            .getAnimatedValue();
                    seekbar.setProgress(value[0]);
                }
            });
            anim.setInterpolator(new BounceInterpolator());
            anim.start();

        } else if (view == btn_upvote) {

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, post_id, promo_id, 10, Constants.METRICS_UP_VOTE);
            Utility.saveImpressionToSql(this, impressionModel);

            sendUpvote();

        } else if (view == img_change_wallet) {

            if (type_balance == 0) {

                type_balance = 1;
                str_unit = "prb";
                txt_unit.setText(str_unit.toUpperCase());
                txt_unit.setTextColor(this.getResources().getColor(R.color.colorWalletPink));

                int prbVal = (int)(Float.parseFloat(VALUE_PRB));
                seekbar.setMax(prbVal);

                getExchangeRate("pib", "prb");

            } else if (type_balance == 1) {
                type_balance = 2;
                str_unit = "pgb";
                txt_unit.setText(str_unit.toUpperCase());
                txt_unit.setTextColor(this.getResources().getColor(R.color.colorWalletGreen));

                int prbVal = (int)(Float.parseFloat(VALUE_PGB));
                seekbar.setMax(prbVal);

                getExchangeRate("prb", "pgb");

            } else if (type_balance == 2) {
                type_balance = 0;
                str_unit = "pib";
                txt_unit.setText(str_unit.toUpperCase());
                txt_unit.setTextColor(this.getResources().getColor(R.color.colorMain));

                int prbVal = (int)(Float.parseFloat(VALUE_PIB));
                seekbar.setMax(prbVal);

                getExchangeRate("pgb", "pib");

            }
        } else if (view == linear_edittext) {

            if (isManual) {

                txt_brush.setBackgroundResource(R.color.transparent);
                txt_brush.setCursorVisible(false);
                Utility.dismissKeyboard(this);
                seekbar.setVisibility(View.VISIBLE);

                isManual = false;

            } else {

                txt_brush.setBackgroundResource(R.drawable.icon_edittext_background);
                txt_brush.requestFocus();
                txt_brush.setSelection(txt_brush.length());
                txt_brush.setCursorVisible(true);
                Utility.showKeyboard(this);
                seekbar.setVisibility(View.INVISIBLE);

                isManual = true;
            }
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (requestType.equals(SUCCESS_EXCHANGE)) {
                exchangedValue(objResult);
            } else {

                Utility.g_isChanged = true;
                Utility.g_isChangedFundingStatus = true;

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            switch (requestType) {

                case SUCCESS_BALANCE:
                    getBalanceData();
                    break;
                case SUCCESS_UPVOTE:
                    sendUpvote();
                    break;
                case SUCCESS_EXCHANGE:
                    getExchangeRate(fromValue, toValue);
                    break;
            }
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

        try {

            VALUE_PIB = ((JSONObject) objResult.get(1)).optString("available");
            VALUE_PRB = ((JSONObject) objResult.get(2)).optString("available");
            VALUE_PGB = ((JSONObject) objResult.get(3)).optString("available");

            int prbVal = (int)(Float.parseFloat(VALUE_PIB));

            seekbar.setMax(prbVal);

            if (current_price != -1) {

                if (current_price <= prbVal) {
                    seekbar.setProgress(current_price);
                    setAction(true);
                } else {
                    setAction(false);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
