package com.star.pibbledev.profile.activity.setting.wallet;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.adaptar.SettingCurrenciesRecyclerAdaptar;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class SettingWalletDetailActivity extends BaseActivity implements View.OnClickListener , RequestListener, SettingCurrenciesRecyclerAdaptar.listListener{

    ImageButton img_back;
    RecyclerView recyclerview;

    String currencies[] = {"KRW", "USD"};
    String str_currency;
    SettingCurrenciesRecyclerAdaptar adaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting_wallet_detail);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        String countries[] = {"Korean Won", "United States Dollar"};

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adaptar = new SettingCurrenciesRecyclerAdaptar(this, countries, currencies);
        recyclerview.setAdapter(adaptar);
        adaptar.setClickListener(this);
    }

    private void updateCurrency(String currency) {

        str_currency = currency;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().updateCurrency(this, this, token, currency);

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }
    }

    @Override
    public void onClickCell(int position) {

        updateCurrency(currencies[position]);

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            Utility.getSavedPref(this).saveString(Constants.CURRENCY, str_currency);

            String msg = objResult.optString("message");

            AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                    msg,
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {

                    dismiss();

                    adaptar.notifyDataSetChanged();

                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else {

            Constants.saveRefreshToken(this, objResult);

            updateCurrency(str_currency);

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
