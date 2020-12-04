package com.star.pibbledev.profile.activity.setting.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

public class SettingWalletHomeActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back;
    LinearLayout linear_currency;
    TextView txt_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_wallet_home);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        txt_currency = (TextView)findViewById(R.id.txt_currency);

        linear_currency = (LinearLayout)findViewById(R.id.linear_currency);
        linear_currency.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        txt_currency.setText(Utility.getReadPref(this).getStringValue(Constants.CURRENCY));
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_currency) {

            linear_currency.setBackgroundColor(getResources().getColor(R.color.line_background_color));

            Intent intent = new Intent(this, SettingWalletDetailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }
}
