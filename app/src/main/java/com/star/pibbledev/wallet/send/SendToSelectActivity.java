package com.star.pibbledev.wallet.send;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.samsung.android.sdk.coldwallet.ICWWallet;
import com.samsung.android.sdk.coldwallet.ScwService;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.global.model.FriendsModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.HomeWalletActivity;
import com.star.pibbledev.wallet.home.RegisterPinActivity;
import com.star.pibbledev.wallet.home.samsung.activity.SamsungWalletWithdrawActivity;
import com.star.pibbledev.wallet.home.samsung.util.ETHUtil;
import com.star.pibbledev.wallet.home.samsung.util.HexUtil;
import com.star.pibbledev.wallet.home.samsung.util.SamsungConstant;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.Arrays;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SendToSelectActivity extends BaseActivity implements  View.OnClickListener, RequestListener, FriendsListAdaptar.FriendsListListener {

    public static Activity sendToSelectActivity;

    private boolean mIsBound = false;
    protected int mCurrentCurrencyType = 60;
    protected int mIndex = 0;
    protected String mAddress;
    protected byte[] signedEthTx;
    protected byte[] signedMessage;
    protected byte[] mCurrentPubX;
    protected byte[] mCurrentPub;
    private int mNonceValue;
    private Long mGasPrice;

    public static final String TYPE_SEND_TO_ADDRESS = "sendToAddress";
    public static final String TYPE_SEND_TO_UUID = "sendToUUID";

    private static final String REQUEST_GET_FRIENDS = "request_get_friends";
    private static final String REQUEST_GET_RECENT_USERS = "request_get_recent_user";
    private static final String REQUEST_GET_GAS_PRICE = "request_get_gas_price";
    private static final String REQUEST_GET_NONCE = "request_get_nonce";
    private static final String REQUEST_SEND_SIGN_TRANSACTION = "request_send_sign_transaction";

    ImageButton img_back;
    TextView txt_value, txt_changedValue, txt_unit, txt_send, txt_wallet, txt_title, txt_actualamount, txt_transaction_fee, txt_fee_name;
    ImageView img_friend, img_address, img_qrcode, img_cancel, img_samsung;
    LinearLayout linear_friend, linear_address, linear_samsung_wallet, linear_friend_tab, linear_address_tab, linear_qr_tab, linear_samsung, linear_actual_name;
    EditText txt_address;
    LinearLayout linear_send, linear_card;
    NavigationTabStrip tab_bar;

    AppBarLayout mAppBarContainer;

    RecyclerView recyclerview;

    FriendsListAdaptar friendsListAdaptar;

    String str_amount, str_krw, str_unit;

    ArrayList<FriendsModel> ary_friends;
    String mPage;

    int mSelectedFriend = -1;
    String activityType, requestType, mSendRasString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_sendto);

        sendToSelectActivity = this;

        setLightStatusBar();

//        Utility.disKeyboardTouch(findViewById(R.id.linear_location), this);

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_value = (TextView)findViewById(R.id.txt_value);
        txt_changedValue = (TextView)findViewById(R.id.txt_changedValue);
        txt_unit = (TextView)findViewById(R.id.txt_unit);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_actualamount = (TextView)findViewById(R.id.txt_actualamount);
        txt_transaction_fee = (TextView)findViewById(R.id.txt_transaction_fee);
        txt_fee_name = (TextView)findViewById(R.id.txt_fee_name);

        img_friend = (ImageView)findViewById(R.id.img_friend);
        img_friend.setOnClickListener(this);
        img_address = (ImageView)findViewById(R.id.img_address);
        img_address.setOnClickListener(this);
        img_qrcode = (ImageView)findViewById(R.id.img_qrcode);
        img_qrcode.setOnClickListener(this);
        img_cancel = (ImageView)findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(this);
        img_samsung = (ImageView)findViewById(R.id.img_samsung);
        img_samsung.setOnClickListener(this);

        linear_actual_name = (LinearLayout) findViewById(R.id.linear_actual_name);

        linear_send = (LinearLayout) findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        txt_send = (TextView)findViewById(R.id.txt_send);

        linear_card = (LinearLayout)findViewById(R.id.linear_card);
        txt_wallet = (TextView)findViewById(R.id.txt_wallet);
        linear_samsung = (LinearLayout)findViewById(R.id.linear_samsung);
        linear_friend_tab = (LinearLayout)findViewById(R.id.linear_friend_tab);
        linear_address_tab = (LinearLayout)findViewById(R.id.linear_address_tab);
        linear_qr_tab = (LinearLayout)findViewById(R.id.linear_qr_tab);

        linear_friend = (LinearLayout)findViewById(R.id.linear_friend);
        linear_friend.setVisibility(View.INVISIBLE);
        linear_address = (LinearLayout)findViewById(R.id.linear_address);
        linear_address.setVisibility(View.INVISIBLE);
        linear_samsung_wallet = (LinearLayout)findViewById(R.id.linear_samsung_wallet);
        linear_samsung_wallet.setVisibility(View.INVISIBLE);

        mAppBarContainer = (AppBarLayout)findViewById(R.id.mAppBarContainer);

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        ary_friends = new ArrayList<>();

        friendsListAdaptar = new FriendsListAdaptar(this, ary_friends);
        friendsListAdaptar.setClickListener(this);
        recyclerview.setAdapter(friendsListAdaptar);

        tab_bar = (NavigationTabStrip)findViewById(R.id.tab_bar);
        tab_bar.setTitles(getString(R.string.friends), getString(R.string.recent));
        tab_bar.setTabIndex(0, true);

        tab_bar.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                Log.e("xxx", title);
            }

            @Override
            public void onEndTabSelected(String title, int index) {

//                ary_friends.clear();
                friendsListAdaptar.mSelectedCell = -1;
                friendsListAdaptar.clear();
                mSelectedFriend = -1;

                linear_send.setEnabled(false);

                if (index == 0) {
                    getFriends("1");
                } else if (index == 1) {
                    getFriendsRecentlyFundsent("1");
                }

            }
        });


        getFriends("1");

        recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerview.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (tab_bar.getTabIndex() == 0) {
                    getFriends(String.valueOf(page + 1));
                } else {
                    getFriendsRecentlyFundsent(String.valueOf(page + 1));
                }
            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });

        txt_address = (EditText)findViewById(R.id.txt_address);
        Utility.g_QRcodeScanResult = "";

        linear_send.setEnabled(false);
        txt_send.setTextColor(this.getResources().getColor(R.color.light_gray));

        txt_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    linear_send.setEnabled(true);
                    txt_send.setTextColor(SendToSelectActivity.this.getResources().getColor(R.color.colorMain));
                } else {
                    linear_send.setEnabled(false);
                    txt_send.setTextColor(SendToSelectActivity.this.getResources().getColor(R.color.light_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        str_amount = getIntent().getStringExtra("unit_value");
        str_krw = getIntent().getStringExtra("krw_value");
        str_unit = getIntent().getStringExtra("unit");

        String currency = Utility.getReadPref(this).getStringValue(Constants.CURRENCY);

        txt_unit.setText(str_unit);
        txt_value.setText(str_amount);

        if (str_unit.equals("PIB")) {

            float actualamount = Float.parseFloat(str_amount) - Float.parseFloat(SamsungConstant.TRANSACTION_FEE_PIB);

            txt_actualamount.setText(String.format("%s %s", String.valueOf((int) actualamount), str_unit));
            txt_transaction_fee.setText(String.format("%s %s", SamsungConstant.TRANSACTION_FEE_PIB, str_unit));

        } else if (str_unit.equals("ETH")) {

            float actualamount = Float.parseFloat(str_amount) - Float.parseFloat(SamsungConstant.TRANSACTION_FEE_ETH);

            txt_actualamount.setText(String.format("%.4f", actualamount) + " " + str_unit);
            txt_transaction_fee.setText(String.format("%s %s", SamsungConstant.TRANSACTION_FEE_ETH, str_unit));

        }

        txt_changedValue.setText(String.format("%s %s", Utility.formatedNumberString(Float.parseFloat(str_krw)), currency));

        activityType = getIntent().getStringExtra(Constants.TARGET);

        switch (activityType) {

            case Constants.PIBBLE_WALLET:

                sendBtnStatue();

                linear_card.setBackground(getResources().getDrawable(R.drawable.wallet_background));
                linear_samsung.setVisibility(View.GONE);
                txt_title.setText(R.string.send_to);

                linear_actual_name.setVisibility(View.VISIBLE);
                txt_fee_name.setText(R.string.transaction_fee);

                break;

            case Constants.SAMSUNG_WALLET_DEPOSIT:

                linear_friend_tab.setVisibility(View.GONE);
                linear_address_tab.setVisibility(View.GONE);
                linear_qr_tab.setVisibility(View.GONE);

                linear_card.setBackground(getResources().getDrawable(R.drawable.wallet_background));
                linear_samsung.setVisibility(View.VISIBLE);
                img_samsung.setImageResource(R.drawable.wallet_samsung2);
                txt_wallet.setText(R.string.samsung_wallet);
                txt_title.setText(R.string.samsung_deposit_lower);

                linear_actual_name.setVisibility(View.VISIBLE);
                txt_fee_name.setText(R.string.transaction_fee);

                break;

            case Constants.SAMSUNG_WALLET_WITHDRAW:

                linear_card.setBackground(getResources().getDrawable(R.drawable.samsung_wallet_background));
                linear_samsung.setVisibility(View.VISIBLE);
                img_samsung.setImageResource(R.drawable.wallet_pibble2);
                txt_wallet.setText(R.string.pibble_wallet);
                txt_title.setText(R.string.samsung_withdraw_lower);

                linear_actual_name.setVisibility(View.GONE);
                txt_fee_name.setText(R.string.transaction_fee_estimated);

                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent();
        intent.setAction(ICWWallet.class.getName());
        intent.setClassName(SamsungConstant.CWS_PKG_NAME, SamsungConstant.CWS_CLASS_NAME);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mIsBound) {
            mIsBound = false;
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (Constants.isLifeToken(this)) {

            if (Utility.g_QRcodeScanResult.length() > 0) {

                txt_address.setText(Utility.g_QRcodeScanResult);
                linear_address.setVisibility(View.VISIBLE);
                linear_friend.setVisibility(View.INVISIBLE);
                linear_samsung_wallet.setVisibility(View.INVISIBLE);
                img_friend.setImageResource(R.drawable.wallet_friend2);
                img_address.setImageResource(R.drawable.wallet_address1);

                if (activityType.equals(Constants.SAMSUNG_WALLET_DEPOSIT)) {
                    img_samsung.setImageResource(R.drawable.wallet_samsung2);
                } else {
                    img_samsung.setImageResource(R.drawable.wallet_pibble2);
                }

                linear_send.setEnabled(true);
                txt_send.setTextColor(this.getResources().getColor(R.color.colorMain));

                Utility.g_QRcodeScanResult = "";

            }

            if (Utility.g_isChanged) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                        getString(R.string.sent_successfully),
                        null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                    @Override
                    public void onClickButton(int position) {

                        if (position == 1) {

                            if (HomeWalletActivity.homeWalletActivity != null) HomeWalletActivity.homeWalletActivity.finish();
                            if (SendPIBCreatActivity.sendPIBCreatActivity != null) SendPIBCreatActivity.sendPIBCreatActivity.finish();
                            if (SamsungWalletWithdrawActivity.samsungWalletWithdrawActivity != null) SamsungWalletWithdrawActivity.samsungWalletWithdrawActivity.finish();

                            Intent intent = new Intent(SendToSelectActivity.this, HomeWalletActivity.class);
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
            }

        } else {

            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void sendBtnStatue() {

//        txt_send.setText(R.string.send);

        if (str_unit.equals("PIB")) {

            txt_send.setText(R.string.next);

        } else {

            txt_send.setText(R.string.send);
        }
    }

    private void getFriends(String page) {

        requestType = REQUEST_GET_FRIENDS;

        mPage = page;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserFriends(this, this, access_token, "me", page, "15");

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void getFriendsRecentlyFundsent(String page) {

        requestType = REQUEST_GET_RECENT_USERS;

        mPage = page;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getFriendsRecentlyFundsent(this, this, access_token, page, "15");

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void getGasPrice() {

        requestType = REQUEST_GET_GAS_PRICE;

        if (Constants.isLifeToken(this)) {

            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getGasPrice(this, this, accessToken);

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void getNonceValue(String address) {

        requestType = REQUEST_GET_NONCE;

        if (Constants.isLifeToken(this)) {

            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getNonceValue(this, this, accessToken, address);

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void sendRawTransaction(String transaction_data) {

        requestType = REQUEST_SEND_SIGN_TRANSACTION;

        String hexString = transaction_data.substring(2, transaction_data.length());

        if (Constants.isLifeToken(this)) {

            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().sendRawTransaction(this, this, accessToken, hexString);

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void parsingFriendsInfo(JSONObject objResult) {

        JSONArray items = objResult.optJSONArray("items");
        JSONObject pageObj = objResult.optJSONObject("pagination");
        if (pageObj == null) return;
//        totalPagecount = pageObj.optInt("pages");

        if (items != null && items.length() > 0) {

            for (int i = 0; i < items.length(); i++) {

                try {
                    JSONObject jsonObject = (JSONObject) items.get(i);

                    FriendsModel friendsModel = new FriendsModel();

                    friendsModel.user_avatar = jsonObject.optString("avatar");
                    friendsModel.user_name = jsonObject.optString("username");
                    friendsModel.user_uuid = jsonObject.optString("uuid");

                    JSONArray ary_wallets = jsonObject.optJSONArray("wallets");

                    if (ary_wallets != null && ary_wallets.length() > 0) {
                        for (int w = 0; w < ary_wallets.length(); w++) {
                            JSONObject walObj = (JSONObject)ary_wallets.get(w);
                            switch (walObj.optString("symbol")) {

                                case "ETH":
                                    friendsModel.address_ETH = walObj.optString("address");
                                    break;
                                case "PIB":
                                    friendsModel.address_PIB = walObj.optString("address");
                                    break;
                                case "BTC":
                                    friendsModel.address_BTC = walObj.optString("address");
                                    break;

                            }
                        }
                    }

                    ary_friends.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (friendsListAdaptar != null) friendsListAdaptar.notifyDataSetChanged();

        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            Utility.dismissKeyboard(this);
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == img_friend) {

            txt_address.clearFocus();
            Utility.dismissKeyboard(this);

            linear_send.setEnabled(false);
            txt_send.setTextColor(getResources().getColor(R.color.light_gray));

            img_friend.setImageResource(R.drawable.wallet_friend1);
            img_address.setImageResource(R.drawable.wallet_address2);

            if (activityType.equals(Constants.SAMSUNG_WALLET_DEPOSIT)) {
                img_samsung.setImageResource(R.drawable.wallet_samsung2);
            } else {
                img_samsung.setImageResource(R.drawable.wallet_pibble2);
            }

            linear_friend.setVisibility(View.VISIBLE);
            linear_address.setVisibility(View.INVISIBLE);
            linear_samsung_wallet.setVisibility(View.INVISIBLE);

        } else if (view == img_address) {

            mSelectedFriend = -1;

            Utility.showKeyboard(this);
            txt_address.requestFocus();

            linear_send.setEnabled(false);
            txt_send.setTextColor(getResources().getColor(R.color.light_gray));

            img_friend.setImageResource(R.drawable.wallet_friend2);
            img_address.setImageResource(R.drawable.wallet_address1);
            if (activityType.equals(Constants.SAMSUNG_WALLET_DEPOSIT)) {
                img_samsung.setImageResource(R.drawable.wallet_samsung2);
            } else {
                img_samsung.setImageResource(R.drawable.wallet_pibble2);
            }

            linear_friend.setVisibility(View.INVISIBLE);
            linear_address.setVisibility(View.VISIBLE);
            linear_samsung_wallet.setVisibility(View.INVISIBLE);

            mAppBarContainer.setExpanded(true);

        } else if (view == img_qrcode) {

            mSelectedFriend = -1;
            linear_send.setEnabled(false);
            txt_send.setTextColor(getResources().getColor(R.color.light_gray));

            mAppBarContainer.setExpanded(true);

            Utility.dismissKeyboard(this);
            txt_address.clearFocus();

            Intent intent = new Intent(this, QRcodeScan.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == img_samsung) {

            Utility.dismissKeyboard(this);
            txt_address.clearFocus();

            linear_send.setEnabled(true);
            txt_send.setTextColor(getResources().getColor(R.color.colorMain));

            mAppBarContainer.setExpanded(true);

            img_friend.setImageResource(R.drawable.wallet_friend2);
            img_address.setImageResource(R.drawable.wallet_address2);

            if (activityType.equals(Constants.SAMSUNG_WALLET_DEPOSIT)) {

                img_samsung.setImageResource(R.drawable.wallet_samsung1);

            } else if (activityType.equals(Constants.SAMSUNG_WALLET_WITHDRAW)){

                img_samsung.setImageResource(R.drawable.wallet_pibble1);

            }

            linear_friend.setVisibility(View.INVISIBLE);
            linear_address.setVisibility(View.INVISIBLE);
            linear_samsung_wallet.setVisibility(View.VISIBLE);

        } else if (view == img_cancel) {

            txt_address.getText().clear();

        } else if (view == linear_send) {

            Utility.dismissKeyboard(this);

            switch (activityType) {

                case Constants.PIBBLE_WALLET:

                    if (str_unit.equals("PIB") && mSelectedFriend == -1) {

                        Intent intent = new Intent(this, SelectPIBTypeActivity.class);
                        intent.putExtra("value", str_amount);
                        intent.putExtra("address", Utility.getStringWithoutFirstSpace(Utility.getWithoutLastSpace(txt_address.getText().toString())));

                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                    } else {

                        Utility.g_isChanged = false;

                        linear_send.setEnabled(false);

                        Intent intent = new Intent(this, RegisterPinActivity.class);

                        if (mSelectedFriend == -1) {
                            intent.putExtra(RegisterPinActivity.TYPE_ACTION, TYPE_SEND_TO_ADDRESS + "," + Utility.getStringWithoutFirstSpace(Utility.getWithoutLastSpace(txt_address.getText().toString())) + "," + str_amount + "," + str_unit);
                        } else {
                            intent.putExtra(RegisterPinActivity.TYPE_ACTION, TYPE_SEND_TO_UUID + "," + ary_friends.get(mSelectedFriend).user_uuid + "," + str_amount + "," + str_unit);
                        }

                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    break;

                case Constants.SAMSUNG_WALLET_DEPOSIT:

                    Intent intent = new Intent(this, RegisterPinActivity.class);

                    intent.putExtra(RegisterPinActivity.TYPE_ACTION, TYPE_SEND_TO_ADDRESS + "," + mAddress + "," + str_amount + "," + str_unit);

                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    break;

                case Constants.SAMSUNG_WALLET_WITHDRAW:

                    mSelectedFriend = -3;

                    if (mAddress != null && mAddress.length() > 0) getNonceValue(mAddress);

                    break;
            }

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            switch (requestType) {

                case REQUEST_GET_FRIENDS:
                case REQUEST_GET_RECENT_USERS:

                    parsingFriendsInfo(objResult);

                    if (mGasPrice == null) getGasPrice();

                    break;

                case REQUEST_GET_GAS_PRICE:

                    if (objResult == null) return;

                    mGasPrice = objResult.optLong("gas_price");

                    updateMaximumFee(String.valueOf(mGasPrice));

                    break;

                case REQUEST_GET_NONCE:

                    if (objResult == null) return;

                    try {

                        mNonceValue = objResult.getInt("nonce");

                        String address = null;

                        if (mSelectedFriend == -1) {

                            address = txt_address.getText().toString();

                        } else if (mSelectedFriend == -3) {

                            if (str_unit.equals("PIB"))
                                address = Utility.getReadPref(this).getStringValue(Constants.ADDRESS_PIB);
                            else if (str_unit.equals("ETH"))
                                address = Utility.getReadPref(this).getStringValue(Constants.ADDRESS_ETH);

                        } else if (mSelectedFriend >= 0) {

                            if (str_unit.equals("PIB"))
                                address = ary_friends.get(mSelectedFriend).address_PIB;
                            else if (str_unit.equals("ETH"))
                                address = ary_friends.get(mSelectedFriend).address_ETH;
                        }

                        if (mAddress != null && mAddress.length() > 0) {

                            if (mGasPrice != null) signETH(address, mGasPrice);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case REQUEST_SEND_SIGN_TRANSACTION:

                    AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                            getString(R.string.sent_successfully),
                            null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                        @Override
                        public void onClickButton(int position) {

                            if (position == 1) {

                                if (HomeWalletActivity.homeWalletActivity != null) HomeWalletActivity.homeWalletActivity.finish();
                                if (SendPIBCreatActivity.sendPIBCreatActivity != null) SendPIBCreatActivity.sendPIBCreatActivity.finish();
                                if (SamsungWalletWithdrawActivity.samsungWalletWithdrawActivity != null) SamsungWalletWithdrawActivity.samsungWalletWithdrawActivity.finish();

                                Intent intent = new Intent(SendToSelectActivity.this, HomeWalletActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
                            }

                            dismiss();
                        }

                    };
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    break;
            }

        } else {
            Constants.saveRefreshToken(this, objResult);

            switch (requestType) {
                case REQUEST_GET_FRIENDS:
                    getFriends(mPage);
                    break;
                case REQUEST_GET_RECENT_USERS:
                    getFriendsRecentlyFundsent(mPage);
                    break;
                case REQUEST_GET_GAS_PRICE:
                    getGasPrice();
                    break;
                case REQUEST_GET_NONCE:

                    if (mAddress != null && mAddress.length() > 0) getNonceValue(mAddress);

                    break;
                case REQUEST_SEND_SIGN_TRANSACTION:

                    sendRawTransaction(mSendRasString);

                    break;

            }
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

    }

    @Override
    public void onItemClick(View view, int position) {
        mSelectedFriend = position;
        linear_send.setEnabled(true);
        txt_send.setTextColor(SendToSelectActivity.this.getResources().getColor(R.color.colorMain));
    }

    //-------- samsung wallet -------------

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mIsBound = true;

            if (str_unit.equals("ETH") || str_unit.equals("PIB")) {
                mCurrentCurrencyType = 60;
            } else if (str_unit.equals("BTC")){
                mCurrentCurrencyType = 0;
            }

            getXPubKeyWallet();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void getXPubKeyWallet() {

        ScwService.ScwGetExtendedPublicKeyListCallback callback =
                new ScwService.ScwGetExtendedPublicKeyListCallback() {
                    @Override
                    public void onSuccess(List<byte[]> extendedPublicKeyList) {
                        updatePubKey(extendedPublicKeyList.get(0));
                    }

                    @Override
                    public void onFailure(int errorCode) {

                        Toast.makeText(SendToSelectActivity.this, String.format(Locale.ENGLISH, "onResponse error = %d", errorCode), Toast.LENGTH_LONG).show();

                    }
                };


        String hdpath;
        if(mCurrentCurrencyType == 0){ // ETH default
            hdpath = SamsungConstant.HD_PATH_BTC + mIndex;
        } else {
            hdpath = SamsungConstant.HD_PATH_ETH + mIndex;
        }

        ArrayList<String> hdPathList = new ArrayList<>();
        hdPathList.add(hdpath);

        ScwService.getInstance().getExtendedPublicKeyList(callback, hdPathList);
    }

    private void updatePubKey(byte[] pubx) {

        mCurrentPubX = pubx;

        byte[] pub = Arrays.copyOfRange(pubx, 0, 33);
        byte[] chainCode = Arrays.copyOfRange(pubx, 33, 65);
        mCurrentPub = pub;

        byte[] decompKey = ETHUtil.decompressCompressedKey(pub);
        if (decompKey != null) {
            byte[] ethAddress = Keys.getAddress(decompKey);
            final String ethAddrStr = HexUtil.toHexString(ethAddress);
            mAddress = ethAddrStr;

            ECKey pubkey = ECKey.fromPublicOnly(pub);

//            Log.d("ssss_ethAddrStr", ethAddrStr);
//            Log.d("ssss_pub", HexUtil.toHexString(pub));
//            Log.d("ssss_chainCode", HexUtil.toHexString(chainCode));
        }
    }

    private void generateBTCPubKey(byte[] pubx) {

        byte[] pub = Arrays.copyOfRange(pubx, 0, 33);

        try {

            MessageDigest sha = MessageDigest.getInstance("SHA-256");

            byte[]s1 = sha.digest(pub);

//            MessageDigest rmd = MessageDigest.getInstance("RIPEMD160", "BC");
            MessageDigest rmd = new  RIPEMD160.Digest();
            byte[]r1 = rmd.digest(s1);

            byte[]r2 = new byte[r1.length + 1];
            r2[0] = 0;
            System.arraycopy(r1, 0, r2, 1, r1.length);

            byte[]s2 = sha.digest(r2);
            byte[]s3 = sha.digest(s2);

            byte[]a1 = new byte[25];
            System.arraycopy(r2, 0, a1, 0, r2.length);
            System.arraycopy(s3, 0, a1, 21, 4);

            mAddress = Base58.encode(a1);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void signETH(String address, Long gasPrice) {

        ScwService.ScwSignEthTransactionCallback callback =
                new ScwService.ScwSignEthTransactionCallback() {
                    @Override
                    public void onSuccess(byte[] signedEthTransaction) {
                        handlingSignedETHTx(signedEthTransaction);
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        Toast.makeText(SendToSelectActivity.this, String.format(Locale.ENGLISH, "onResponse error = %d", errorCode), Toast.LENGTH_LONG).show();

                    }
                };

        String hdpath = SamsungConstant.HD_PATH_ETH + mIndex;

        byte[] unsignedEthTx = getUnsignedEthTx(address, gasPrice);

        if (str_unit.equals("PIB")) {
            unsignedEthTx = getUnsignedEthTxWithGeneratedData(address, gasPrice);
        }

        ScwService.getInstance().signEthTransaction(callback, unsignedEthTx, hdpath);
    }

    public byte[] getUnsignedEthTx(String address, Long gas_Price) {

        try {
//            BigInteger nonce = BigInteger.ZERO;
            BigInteger nonce =  new BigInteger(String.valueOf(mNonceValue));
            double ethValue = Double.valueOf(str_amount);
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(String.valueOf(gas_Price));

            String gaslimet = String.valueOf(SamsungConstant.GAS_LIMIT_ETH);

            BigInteger gasLimit = new BigInteger(gaslimet);
            RawTransaction rawTrx = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, address, weiValue.toBigInteger());

            return TransactionEncoder.encode(rawTrx);

        } catch (Exception e) {
            return new byte[0];
        }

    }

    public byte[] getUnsignedEthTxWithGeneratedData(String address, Long gas_Price){

        try {
            BigInteger nonce_wei = BigInteger.ZERO;
            BigInteger nonce =  new BigInteger(String.valueOf(mNonceValue));
            double ethValue = Double.valueOf(str_amount);
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(String.valueOf(gas_Price));
            BigInteger gasLimit = new BigInteger(String.valueOf(SamsungConstant.GAS_LIMIT_PIB));

            BigInteger tokenAmount = BigDecimal.valueOf(Double.valueOf(str_amount)).multiply(BigDecimal.valueOf(10).pow(18)).toBigInteger();

            Function function = ETHUtil.createEthTransferData(address, tokenAmount);
            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction rawTrx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, SamsungConstant.SMART_CONTRACT_ADDRESS, nonce_wei, encodedFunction);

            return TransactionEncoder.encode(rawTrx);

        } catch (Exception e) {
            return new byte[0];
        }
    }

    private void handlingSignedETHTx(byte[] signedEthTx) {

        if(signedEthTx != null && signedEthTx.length > 0) {
            runOnUiThread(() -> {

                mSendRasString = HexUtil.toHexString(signedEthTx);

                sendRawTransaction(mSendRasString);

            });
        }
    }

    private void updateMaximumFee(String gasPriceStr) {

        String gasLimitStr = null;

        if (str_unit.equals("ETH")) gasLimitStr = String.valueOf(SamsungConstant.GAS_LIMIT_ETH);
        else if (str_unit.equals("PIB")) gasLimitStr = String.valueOf(SamsungConstant.GAS_LIMIT_PIB);

        if (gasPriceStr.isEmpty()) {
            gasPriceStr = "0";
        }

        if (gasLimitStr == null) {
            gasLimitStr = "0";
        }

        BigInteger gasPrice = new BigInteger(gasPriceStr);
        BigInteger gasLimit = new BigInteger(gasLimitStr);
        BigInteger maxFee = gasPrice.multiply(gasLimit);

        BigDecimal weiValue = new BigDecimal(maxFee).multiply(BigDecimal.valueOf(1, 18));
        String maxFeeStr = weiValue.toPlainString();

        if (activityType.equals(Constants.SAMSUNG_WALLET_WITHDRAW)) txt_transaction_fee.setText(String.format("%s ETH", Utility.getRoundString(maxFeeStr)));
    }
}
