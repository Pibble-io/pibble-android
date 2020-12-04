package com.star.pibbledev.wallet.exchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.HomeWalletActivity;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExchangeActivity extends BaseActivity implements  View.OnClickListener, RequestListener {

    public static final String TYPE_EXCHANGE = "exchange";
    private String VALUE_PIB, VALUE_PRB, VALUE_PGB;

    EditText txt_edit;
    ImageButton img_back;
    ImageView img_valueChange, img_unitChange;
    TextView txt_changedValue,txt_changedunit, txt_unit, txt_pib_to_prb;
    Button btn_next, btn_max;

    ImageView img_user;
    TextView txt_userEmo, txt_username, txt_pib, txt_prb, txt_pgb;

    int unit_type;
    String str_unit, str_won;
    boolean isConvert;
    float exchangeRate, mMaxValue;

    TextWatcher tt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_exchange);

        setLightStatusBar();

        txt_edit = (EditText)findViewById(R.id.txt_edit);
        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_unitChange = (ImageView)findViewById(R.id.img_unitChange);
        img_unitChange.setOnClickListener(this);
        img_valueChange = (ImageView)findViewById(R.id.img_valueChange);
        img_valueChange.setOnClickListener(this);
        img_valueChange.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_wallet_exchange_pink));
        txt_unit = (TextView)findViewById(R.id.txt_unit);
        txt_changedValue = (TextView)findViewById(R.id.txt_changedValue);
        txt_changedunit = (TextView)findViewById(R.id.txt_changedunit);

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_max = (Button)findViewById(R.id.btn_max);
        btn_max.setOnClickListener(this);

        img_user = (ImageView)findViewById(R.id.img_user);
        txt_username = (TextView)findViewById(R.id.txt_username);
        txt_userEmo = (TextView)findViewById(R.id.txt_userEmo);
        txt_pib = (TextView)findViewById(R.id.txt_pib);
        txt_prb = (TextView)findViewById(R.id.txt_prb);
        txt_pgb = (TextView)findViewById(R.id.txt_pgb);
        txt_pib_to_prb = (TextView)findViewById(R.id.txt_pib_to_prb);

        String avatar = Utility.getReadPref(this).getStringValue("avatar");
        String username = Utility.getReadPref(this).getStringValue("username");

        if (!avatar.equals("null")) {
            ImageLoader.getInstance().displayImage(avatar, img_user);
            txt_userEmo.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (username.length() > 14) value = 14;
            else value = username.length();

            img_user.setBackgroundColor(this.getResources().getColor(Utility.g_aryColors[value]));
            txt_userEmo.setText(Utility.getUserEmoName(username));
        }

        txt_username.setText(username);

        unit_type = 1;
        str_unit = "PIB";
        str_won = "PRB";
        isConvert = false;
        exchangeRate = 1.0f;

        txt_pib_to_prb.setText(getTitleString(str_unit, str_won));

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

                if (s.length() > 0 ) {

                    float beforeValue = Float.parseFloat(s.toString());

                    if (beforeValue <= 0 || beforeValue > mMaxValue) {

                        txt_changedValue.setText("0");

                        btn_next.setEnabled(false);
                        btn_next.setAlpha(0.5f);

                    } else {

                        float changedValue = exchangeRate * beforeValue;
                        txt_changedValue.setText(String.valueOf(changedValue));

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

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!btn_next.isEnabled()) btn_next.setEnabled(true);

        if (Constants.isLifeToken(this)) {

            if (Utility.g_isChanged) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                        getString(R.string.successfully_changed),
                        null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                    @Override
                    public void onClickButton(int position) {

                        if (position == 1) {

                            if (HomeWalletActivity.homeWalletActivity != null) HomeWalletActivity.homeWalletActivity.finish();

                            Utility.dismissKeyboard(ExchangeActivity.this);

                            Intent intent = new Intent(ExchangeActivity.this, HomeWalletActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
                        }

                        dismiss();

                    }

                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Utility.g_isChanged = false;

            } else {

                Utility.showKeyboard(this);
            }

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getBalances(this, this, token);

        } else {

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
            txt_pib_to_prb.setText(getTitleString(str_won, str_unit));
        } else {
            txt_unit.setText(str_unit);
            txt_changedunit.setText(str_won);
            txt_pib_to_prb.setText(getTitleString(str_unit, str_won));
        }

        exchangeRate = 1/exchangeRate;

        txt_edit.getText().clear();
        txt_changedValue.setText("");

        switch (txt_unit.getText().toString()) {

            case "PIB":

                if (VALUE_PIB != null) mMaxValue = Float.parseFloat(VALUE_PIB);

                break;
            case "PRB":

                if (VALUE_PRB != null) mMaxValue = Float.parseFloat(VALUE_PRB);

                break;
            case "PGB":

                if (VALUE_PGB != null) mMaxValue = Float.parseFloat(VALUE_PGB);

                break;
        }
    }

    private String getTitleString(String unit, String won) {

        String string = "";

        String unitVal = "";

        if (unit.equals("PRB")) unitVal = getString(R.string.red_brush);
        else if (unit.equals("PGB")) unitVal = getString(R.string.green_brush);
        else unitVal = unit;

        String wonVal = "";

        if (won.equals("PRB")) wonVal = getString(R.string.red_brush);
        else if (won.equals("PGB")) wonVal = getString(R.string.green_brush);
        else wonVal = won;

        string = getString(R.string.request_exchange) + " " + unitVal + " " + getString(R.string.to) + " " + wonVal;

        return string;

    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            Utility.dismissKeyboard(this);
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_next) {

            Utility.dismissKeyboard(this);

            if (str_unit.equals(getString(R.string.pib)) && str_won.equals(getString(R.string.pgb))) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(this, getString(R.string.confirm), getString(R.string.exchange_description), getString(R.string.continue_to_exchange), getString(R.string.cancel), R.color.colorMain, R.color.black) {
                    @Override
                    public void onClickButton(int position) {

                        if (position == 0) {

                            Utility.g_isChanged = false;

                            btn_next.setEnabled(false);

                            Intent intent = new Intent(ExchangeActivity.this, RegisterPinActivity.class);
                            intent.putExtra(RegisterPinActivity.TYPE_ACTION, TYPE_EXCHANGE + "," + txt_unit.getText().toString() + "," + txt_changedunit.getText().toString() + "," + txt_edit.getText().toString());
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        } else if (position == 1) {

                            Utility.showKeyboard(ExchangeActivity.this);
                        }

                        dismiss();

                    }

                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            } else {

                Utility.g_isChanged = false;

                btn_next.setEnabled(false);

                Intent intent = new Intent(ExchangeActivity.this, RegisterPinActivity.class);
                intent.putExtra(RegisterPinActivity.TYPE_ACTION, TYPE_EXCHANGE + "," + txt_unit.getText().toString() + "," + txt_changedunit.getText().toString() + "," + txt_edit.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

        } else if (view == img_unitChange) {

            if (unit_type == 1) {
                unit_type = 2;
                str_won = "PGB";
                str_unit = "PIB";

                img_valueChange.setEnabled(false);
                img_valueChange.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_wallet_exchange_green));

            } else if (unit_type == 2) {
                unit_type = 1;
                str_unit = "PIB";
                str_won = "PRB";

                img_valueChange.setEnabled(true);
                img_valueChange.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_wallet_exchange_pink));

            }

            txt_unit.setText(str_unit);
            txt_changedunit.setText(str_won);
            txt_edit.getText().clear();
            txt_changedValue.setText("");
            txt_pib_to_prb.setText(getTitleString(str_unit, str_won));

            try {

                mMaxValue = Float.parseFloat(VALUE_PIB);

            } catch (NumberFormatException e) {
                System.out.println("Not a valid float number");
            }


            getExchangeRate(str_unit, str_won);

        } else if (view == img_valueChange) {
            if (isConvert) {
                isConvert = false;
            } else {
                isConvert = true;
            }

            unitConvert(isConvert);

        } else if (view == btn_max) {

            switch (txt_unit.getText().toString()) {

                case "PIB":

                    if (VALUE_PIB != null) mMaxValue = Float.parseFloat(VALUE_PIB);

                    break;
                case "PRB":

                    if (VALUE_PRB != null) mMaxValue = Float.parseFloat(VALUE_PRB);

                    break;
                case "PGB":

                    if (VALUE_PGB != null) mMaxValue = Float.parseFloat(VALUE_PGB);

                    break;
            }

            txt_edit.setText(String.valueOf(mMaxValue));
            float changedValue = exchangeRate * mMaxValue;
            txt_changedValue.setText(String.valueOf(changedValue));

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (objResult != null) {
            String value = objResult.optString("rate");
            exchangeRate = Float.parseFloat(value);
        }
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

        getExchangeRate(str_unit, str_won);

        if (objResult != null && objResult.length() > 0) {

            String str_pib = "0";
            String str_prb = "0";
            String str_pgb = "0";

            for (int w = 0; w < objResult.length(); w++) {

                try {
                    JSONObject walObj = (JSONObject)objResult.get(w);

                    switch (walObj.optString("symbol")) {

                        case "PIB":
                            str_pib = walObj.optString("available");
                            VALUE_PIB = str_pib;
                            break;
                        case "PRB":
                            str_prb = walObj.optString("available");
                            VALUE_PRB = str_prb;
                            break;
                        case "PGB":
                            str_pgb = walObj.optString("available");
                            VALUE_PGB = str_pgb;
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (VALUE_PIB != null) mMaxValue = Float.parseFloat(VALUE_PIB);

            txt_pib.setText(Utility.getConvertedValue(str_pib, false));
            txt_prb.setText(Utility.getConvertedValue(str_prb, false));
            txt_pgb.setText(Utility.getConvertedValue(str_pgb, false));
        }
    }
}
