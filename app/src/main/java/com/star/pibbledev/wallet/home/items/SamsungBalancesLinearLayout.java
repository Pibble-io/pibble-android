package com.star.pibbledev.wallet.home.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.samsung.activity.SamsungWalletWithdrawActivity;
import com.star.pibbledev.wallet.send.SendPIBCreatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class SamsungBalancesLinearLayout extends LinearLayout implements View.OnClickListener , RequestListener {

    TextView val_PIB, val_ETH, val_BTC;
    ImageView img_balace_background1, img_balace_background2, img_balace_background3, img_balance_mark;
    LinearLayout linear_deposit, linear_withdraw;

    Context mContext;

    private String str_eth, str_pib;

    public SamsungBalancesLinearLayout(Context context) {
        super(context);

        mContext = context;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_wallet_home_samsung, this, true);

        val_PIB = (TextView)view.findViewById(R.id.val_PIB);
        val_ETH = (TextView)view.findViewById(R.id.val_ETH);
        val_BTC = (TextView)view.findViewById(R.id.val_BTC);

        img_balace_background1 = (ImageView)view.findViewById(R.id.img_balace_background1);
        img_balace_background2 = (ImageView)view.findViewById(R.id.img_balace_background2);
        img_balace_background3 = (ImageView)view.findViewById(R.id.img_balace_background3);
        img_balance_mark = (ImageView)view.findViewById(R.id.img_balance_mark);

        linear_deposit = (LinearLayout)view.findViewById(R.id.linear_deposit);
        linear_deposit.setOnClickListener(this);
        linear_withdraw = (LinearLayout)view.findViewById(R.id.linear_withdraw);
        linear_withdraw.setOnClickListener(this);

    }

    @SuppressLint("DefaultLocale")
    public void setValues(String address) {

        String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getSamsungWalletBalances(this, mContext, token, address);

    }

    @Override
    public void onClick(View v) {

        if (v == linear_deposit) {

            Intent intent = new Intent(mContext, SendPIBCreatActivity.class);
            intent.putExtra(Constants.TARGET, Constants.SAMSUNG_WALLET_DEPOSIT);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_withdraw) {

            Intent intent = new Intent(mContext, SamsungWalletWithdrawActivity.class);
            intent.putExtra(Constants.TARGET, Constants.SAMSUNG_WALLET_WITHDRAW);

            if (str_eth != null) intent.putExtra(SamsungWalletWithdrawActivity.ETH_VALUE, str_eth);
            if (str_pib != null) intent.putExtra(SamsungWalletWithdrawActivity.PIB_VALUE, str_pib);

            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void succeeded(JSONObject objResult) {

        if (objResult != null) {

            str_eth = objResult.optString("ETH");
            str_pib = objResult.optString("PIB");

            if (str_eth != null && !str_eth.equals("null")) val_ETH.setText(String.format("%.8f", Float.parseFloat(str_eth)));
            if (str_pib != null && !str_pib.equals("null")) val_PIB.setText(Utility.formatedNumberString(Float.parseFloat(str_pib)));

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        Log.d("ssss", strError);

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
