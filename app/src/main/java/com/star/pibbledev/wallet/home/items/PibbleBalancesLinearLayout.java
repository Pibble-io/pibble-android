package com.star.pibbledev.wallet.home.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.wallet.activity.ActivityWalletActivity;
import com.star.pibbledev.wallet.exchange.ExchangeActivity;
import com.star.pibbledev.wallet.paybil.PayBillActivity;
import com.star.pibbledev.wallet.receive.ReceiveQraddressActivity;
import com.star.pibbledev.wallet.send.SendPIBCreatActivity;

public class PibbleBalancesLinearLayout extends LinearLayout implements View.OnClickListener {

    TextView val_PIB, val_ETH, val_BTC, val_KLAY;
    ImageView img_balace_background1, img_balace_background2, img_balace_background3, img_balance_mark;
    LinearLayout linear_receive, linear_send, linear_paybill, linear_exchange, linear_activity, linear_market;
    FrameLayout frame_badge;
    TextView txt_badgeNum;

    Context mContext;

    public PibbleBalancesLinearLayout(Context context) {

        super(context);

        mContext = context;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_wallet_home_pibble, this, true);

        val_PIB = (TextView)view.findViewById(R.id.val_PIB);
        val_ETH = (TextView)view.findViewById(R.id.val_ETH);
        val_BTC = (TextView)view.findViewById(R.id.val_BTC);
        val_KLAY = (TextView)view.findViewById(R.id.val_KLAY);

        img_balace_background1 = (ImageView)view.findViewById(R.id.img_balace_background1);
        img_balace_background2 = (ImageView)view.findViewById(R.id.img_balace_background2);
        img_balace_background3 = (ImageView)view.findViewById(R.id.img_balace_background3);
        img_balance_mark = (ImageView)view.findViewById(R.id.img_balance_mark);

        linear_paybill = (LinearLayout)view.findViewById(R.id.linear_paybill);
        linear_paybill.setOnClickListener(this);
        linear_send = (LinearLayout)view.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        linear_receive = (LinearLayout)view.findViewById(R.id.linear_receive);
        linear_receive.setOnClickListener(this);
        linear_exchange = (LinearLayout)view.findViewById(R.id.linear_exchange);
        linear_exchange.setOnClickListener(this);
        linear_activity = (LinearLayout)view.findViewById(R.id.linear_activity);
        linear_activity.setOnClickListener(this);
        linear_market = (LinearLayout)view.findViewById(R.id.linear_market);
        linear_market.setOnClickListener(this);

        txt_badgeNum = (TextView)view.findViewById(R.id.txt_badgeNum);
        frame_badge = (FrameLayout)view.findViewById(R.id.frame_badge);
        frame_badge.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    public void setValues(String pib, String eth, String btc, String klay) {
        val_PIB.setText(Utility.formatedNumberString(Float.parseFloat(pib)));
        val_ETH.setText(String.format("%.8f", Float.parseFloat(eth)));
        val_BTC.setText(String.format("%.8f", Float.parseFloat(btc)));
        val_KLAY.setText(String.format("%.8f", Float.parseFloat(klay)));
    }

    public void setBadgeNumber(int invoiceCount) {
        if (invoiceCount > 0) {
            frame_badge.setVisibility(View.VISIBLE);
            txt_badgeNum.setText(String.valueOf(invoiceCount));
        } else {
            frame_badge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == linear_paybill) {

            Intent intent = new Intent(mContext, PayBillActivity.class);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_send) {

            Intent intent = new Intent(mContext, SendPIBCreatActivity.class);
            intent.putExtra(Constants.TARGET, Constants.PIBBLE_WALLET);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_receive) {

            Intent intent = new Intent(mContext, ReceiveQraddressActivity.class);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_exchange) {

            Intent intent = new Intent(mContext, ExchangeActivity.class);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_activity) {

            Intent intent = new Intent(mContext, ActivityWalletActivity.class);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_market) {

            Utility.openBrowser(mContext, Constants.market_url);

        }
    }
}
