package com.star.pibbledev.home.asking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.blurry.Blurry;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetRewardAmountActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String POST_ID = "post_id";
    public static final String PROMO_ID = "promo_id";

    private static String VALUE_PIB;
    private static String VALUE_PRB;
    private static String VALUE_PGB;

    ImageView img_background;
    Button btn_cancel, btn_confirm;
    LinearLayout linear_min, linear_max;
    EditText edit_value;
    TextView txt_balance;

    int post_id, promo_id, type_balance;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_setreward_amount);

        setLightStatusBar();

        img_background = (ImageView)findViewById(R.id.img_background);
//        Blurry.with(this)
//                .radius(10)
//                .sampling(8)
//                .async()
//                .capture(findViewById(R.id.img_background))
//                .into(findViewById(R.id.img_background));

//        Blurry.with(this)
//                .radius(25)
//                .sampling(1)
//                .color(Color.argb(66, 0, 255, 255))
//                .async()
//                .capture(findViewById(R.id.img_background))
//                .into(findViewById(R.id.img_background));


        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);

        linear_min = (LinearLayout)findViewById(R.id.linear_min);
        linear_min.setOnClickListener(this);
        linear_max = (LinearLayout)findViewById(R.id.linear_max);
        linear_max.setOnClickListener(this);
        txt_balance = (TextView)findViewById(R.id.txt_balance);
        edit_value = (EditText)findViewById(R.id.edit_value);
        edit_value.requestFocus();

        post_id = getIntent().getIntExtra(POST_ID, -1);
        promo_id = getIntent().getIntExtra(PROMO_ID, -1);

        type_balance = 0;

        edit_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String str_value = edit_value.getText().toString();

                if (str_value.length() > 0) {

                    int currentValue = Integer.parseInt(edit_value.getText().toString());
                    if (currentValue < 100 || currentValue > 100000) {
                        setAction(false);
                    } else {
                        setAction(true);
                    }

                } else {

                    setAction(false);

                }
            }
        });

        setAction(true);

        getBalanceData();
    }

    private void setAction(boolean isEnable) {

        btn_confirm.setEnabled(isEnable);

        if (isEnable) {
            btn_confirm.setTextColor(getResources().getColor(R.color.colorMain));
        } else {
            btn_confirm.setTextColor(getResources().getColor(R.color.grey_464646));
        }
    }

    private void getBalanceData() {

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getBalances(this, this, token);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    @Override
    public void onClick(View view) {

        if (view == btn_cancel) {

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (view == linear_min) {

            edit_value.setText("100");

        } else if (view == linear_max) {

            if (VALUE_PIB != null) {

                int pibVal = (int)(Float.parseFloat(VALUE_PIB));
                edit_value.setText(String.valueOf(pibVal));
            }

        } else if (view == btn_confirm) {

            Utility.g_askingHelpRewardAmount = Integer.valueOf(edit_value.getText().toString());

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

        } else {

            Constants.saveRefreshToken(this, objResult);

            getBalanceData();

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

            int pibVal = (int)(Float.parseFloat(VALUE_PIB));

            String balace = Utility.formatedNumberString(pibVal) + getString(R.string.pib);

            txt_balance.setText(balace);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
