package com.star.pibbledev.wallet.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequestCreateActivity extends BaseActivity implements  View.OnClickListener , RequestListener {

    public static Activity requestCreateActivity;

    EditText txt_edit;
    ImageButton img_back;
    ImageView img_valueChange, img_unitChange;
    TextView txt_changedValue,txt_changedunit, txt_unit;
    Button btn_next;

    int unit_type;
    String str_unit, str_won;
    boolean isConvert;

    float exchangeRate;
    float changedValue, mMaxValue;

    TextWatcher tt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_request_amount);

        requestCreateActivity = this;

        setLightStatusBar();

        txt_edit = (EditText)findViewById(R.id.txt_edit);
        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_unitChange = (ImageView)findViewById(R.id.img_unitChange);
        img_unitChange.setOnClickListener(this);
        img_valueChange = (ImageView)findViewById(R.id.img_valueChange);
        img_valueChange.setOnClickListener(this);
        txt_unit = (TextView)findViewById(R.id.txt_unit);
        txt_changedValue = (TextView)findViewById(R.id.txt_changedValue);
        txt_changedunit = (TextView)findViewById(R.id.txt_changedunit);

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        unit_type = 1;
        str_unit = "PIB";
        isConvert = false;
        str_won = Utility.getReadPref(this).getStringValue(Constants.CURRENCY);

        mMaxValue = Constants.WALLET_REQUEST_MAX_PIB;

        txt_unit.setText(str_unit);
        txt_changedunit.setText(str_won);

        btn_next.setEnabled(false);
        btn_next.setAlpha(0.5f);

        txt_edit.setFilters(new InputFilter[]{
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
//                    int beforeDecimal = 6, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        String temp = txt_edit.getText() + source.toString();

                        if (temp.length() > 9) return "";

                        if (temp.equals(".")) {
                            return "0.";
                        }

//                        else if (temp.toString().indexOf(".") == -1) {
//                            // no decimal point placed yet
//                            if (temp.length() > beforeDecimal) {
//                                return "";
//                            }
//                        } else {
//                            temp = temp.substring(temp.indexOf(".") + 1);
//                            if (temp.length() > afterDecimal) {
//                                return "";
//                            }
//                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });

        tt = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {

                    float beforeValue = Float.parseFloat(s.toString());

                    if (beforeValue <= 0 || beforeValue > mMaxValue) {

                        txt_changedValue.setText("");

                        btn_next.setEnabled(false);
                        btn_next.setAlpha(0.5f);

                    } else {

                        changedValue = exchangeRate * beforeValue;
                        txt_changedValue.setText(Utility.formatedNumberString(changedValue));

                        btn_next.setEnabled(true);
                        btn_next.setAlpha(1.0f);

                    }

                } else {
                    txt_changedValue.setText("");

                    btn_next.setEnabled(false);
                    btn_next.setAlpha(0.5f);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        txt_edit.addTextChangedListener(tt);

        getExchangeRate(str_unit, str_won);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (Constants.isLifeToken(this)) {

            Utility.showKeyboard(this);

        } else {

            Utility.dismissKeyboard(this);

            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

    }


    private void getExchangeRate(String from, String to) {

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getExchangeRate(this, this, access_token, from, to);

        } else {

            Utility.dismissKeyboard(this);
            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void unitConvert(boolean action) {
        if (action) {
            txt_unit.setText(str_won);
            txt_changedunit.setText(str_unit);
        } else {
            txt_unit.setText(str_unit);
            txt_changedunit.setText(str_won);
        }

        mMaxValue = mMaxValue * exchangeRate;

        exchangeRate = 1/exchangeRate;

        txt_edit.getText().clear();
        txt_changedValue.setText("");
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            Utility.dismissKeyboard(this);
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_next) {

            Utility.dismissKeyboard(this);

            Intent intent = new Intent(this, RequestSelectActivity.class);
            intent.putExtra("unit", str_unit);
            if (str_unit.equals(txt_unit.getText().toString())) {
                intent.putExtra("unit_value", txt_edit.getText().toString());
                intent.putExtra("krw_value", String.valueOf(changedValue));
            } else {
                intent.putExtra("unit_value", String.valueOf(changedValue));
                intent.putExtra("krw_value", txt_edit.getText().toString());
            }

            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == img_unitChange) {

            isConvert = false;

            if (unit_type == 1) {
                unit_type = 2;
                str_unit = "ETH";

                mMaxValue = Constants.WALLET_REQUEST_MAX_ETH;

            } else if (unit_type == 2) {
                unit_type = 3;
                str_unit = "BTC";

                mMaxValue = Constants.WALLET_REQUEST_MAX_BTC;

            } else if (unit_type == 3) {

//                unit_type = 1;
//                str_unit = "PIB";
//
//                mMaxValue = Constants.WALLET_REQUEST_MAX_PIB;
                unit_type = 4;
                str_unit = "KLAY";

                mMaxValue = Constants.WALLET_REQUEST_MAX_ETH;

            } else if (unit_type == 4) {
                unit_type = 1;
                str_unit = "PIB";

                mMaxValue = Constants.WALLET_REQUEST_MAX_PIB;
            }

            txt_unit.setText(str_unit);
            txt_changedunit.setText(str_won);
            txt_edit.getText().clear();
            txt_changedValue.setText("");

            getExchangeRate(str_unit, str_won);

        } else if (view == img_valueChange) {
            if (isConvert) {
                isConvert = false;
            } else {
                isConvert = true;
            }

            unitConvert(isConvert);
        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        String value = objResult.optString("rate");
        exchangeRate = Float.parseFloat(value);
    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Constants.isLifeToken(this)) {

            Utility.parseError(this, strError);

        } else {

            Utility.dismissKeyboard(this);
            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
