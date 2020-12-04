package com.star.pibbledev.wallet.home;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.samsung.android.sdk.coldwallet.ICWWallet;
import com.samsung.android.sdk.coldwallet.ScwService;
import com.samsung.android.sdk.coldwallet.ScwDeepLink;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.SlidingAnimation;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.items.PibbleBalancesLinearLayout;
import com.star.pibbledev.wallet.home.items.SamsungBalancesLinearLayout;
import com.star.pibbledev.wallet.home.samsung.util.AppExecutors;
import com.star.pibbledev.wallet.home.samsung.util.ETHUtil;
import com.star.pibbledev.wallet.home.samsung.util.HexUtil;
import com.star.pibbledev.wallet.home.samsung.util.ScwGlobal;

import org.bitcoinj.core.ECKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.Arrays;
import org.web3j.crypto.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeWalletActivity extends BaseActivity implements  View.OnClickListener, RequestListener {

    public static Activity homeWalletActivity;

    private static final String KEY_USER_DATA_SAMPLE = "user_data_sample";
    private static final String CWS_PKG_NAME = "com.samsung.android.coldwalletservice";
    public static final String CWS_CLASS_NAME = CWS_PKG_NAME + ".core.CWWalletService";
    public static final String CWHOME_CLASS_NAME = CWS_PKG_NAME + ".ui.CWHomeActivity";
    private boolean mIsBound = false;
//    private CWServiceAdapter mServiceAdapter;

    private final String mApplicationId = "pm7ufRYJ0b";
    protected int mCurrentCurrencyType = 60;
    public static final String HD_PATH_ETH = "m/44'/60'/0'/0/";
    public static final String HD_PATH_BTC = "m/44'/0'/0'/0/";
    protected int mIndex = 0;
    private static final int TOKEN_GET_XPUB_KEY_FOR_ETH = 10001;
    private static final int TOKEN_GET_XPUB_KEY_LIST = 10003;
    private static final int TOKEN_SIGN_ETH = 10004;
    private static final int TOKEN_CHECK_APP_VERSION = 10005;
    private static final int TOKEN_SIGN_MESSAGE = 10006;

    protected String mEthAddress;
    protected byte[] signedEthTx;
    protected byte[] signedMessage;
    protected byte[] mCurrentPubX;
    protected byte[] mCurrentPub;

    protected AppExecutors mExecutors;

    ImageButton img_back;
    LinearLayout linear_wallet_detail, linear_add_samsung_wallet, linear_balances, linear_dots;
    TextView txt_userEmo, txt_username, txt_pib, txt_prb, txt_pgb;
    ImageView img_user, img_dot1, img_dot2;
    HorizontalScrollView scroll_balance;

    PibbleBalancesLinearLayout pibbleBalancesLinearLayout;
    SamsungBalancesLinearLayout samsungBalances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_home);

        homeWalletActivity = this;

        setLightStatusBar();

        mExecutors = AppExecutors.getInstance();

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        scroll_balance = findViewById(R.id.scroll_balance);
        linear_balances = findViewById(R.id.linear_balances);

        linear_add_samsung_wallet = findViewById(R.id.linear_add_samsung_wallet);
        linear_add_samsung_wallet.setOnClickListener(this);
        linear_dots = findViewById(R.id.linear_dots);

        linear_add_samsung_wallet.setVisibility(View.GONE);
        linear_dots.setVisibility(View.GONE);

        linear_wallet_detail = findViewById(R.id.linear_wallet_detail);

        img_user = findViewById(R.id.img_user);
        txt_userEmo = findViewById(R.id.txt_userEmo);
        txt_username = findViewById(R.id.txt_username);
        txt_pib = findViewById(R.id.txt_pib);
        txt_prb = findViewById(R.id.txt_prb);
        txt_pgb = findViewById(R.id.txt_pgb);

        img_dot1 = findViewById(R.id.img_dot1);
        img_dot2 = findViewById(R.id.img_dot2);

        String avatar = Utility.getReadPref(this).getStringValue("avatar");
        String username = Utility.getReadPref(this).getStringValue("username");

        if (!avatar.equals("null")) {
            ImageLoader.getInstance().displayImage(avatar, img_user);
            txt_userEmo.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (username.length() > 14) value = 14;
            else value = username.length();
            txt_userEmo.setVisibility(View.VISIBLE);
            img_user.setBackgroundColor(getResources().getColor(Utility.g_aryColors[value]));
            txt_userEmo.setText(Utility.getUserEmoName(username));
        }

        txt_username.setText(username);

        pibbleBalancesLinearLayout = new PibbleBalancesLinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utility.g_deviceWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        pibbleBalancesLinearLayout.setLayoutParams(params);
        linear_balances.addView(pibbleBalancesLinearLayout);

        scroll_balance.setOnTouchListener(new View.OnTouchListener() {

            int down_scrollX, up_scrollX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    down_scrollX = scroll_balance.getScrollX();

                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    up_scrollX = scroll_balance.getScrollX();
                    int itemWidth = scroll_balance.getMeasuredWidth();
                    int activeItem = ((up_scrollX + itemWidth / 2) / itemWidth);

                    if (up_scrollX - down_scrollX > 0) {
                        activeItem = activeItem + 1;
                    } else if (up_scrollX -down_scrollX < 0) {
                        activeItem = activeItem - 1;
                    }

                    int scrollTo = activeItem * itemWidth;

                    ObjectAnimator animator=ObjectAnimator.ofInt(scroll_balance, "scrollX",scrollTo );
                    animator.setDuration(300);
                    animator.start();

                    if (activeItem == -1) activeItem = 0;
                    else if (activeItem == 2) activeItem = activeItem - 1;

                    animationScrollPager(activeItem);

                    return true;
                }

                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent();
        intent.setAction(ICWWallet.class.getName());
        intent.setClassName(CWS_PKG_NAME, CWS_CLASS_NAME);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mIsBound) {
            mIsBound = false;
//            mServiceAdapter = null;
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Constants.isLifeToken(this)) {

            //----top animation----

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            linear_wallet_detail.setLayoutParams(params);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = new SlidingAnimation(linear_wallet_detail, 0, Utility.dpToPx(50));
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(300);
                    linear_wallet_detail.setAnimation(animation);
                    linear_wallet_detail.startAnimation(animation);
                }
            }, 500);

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserInfoFromUsername(this, this, token, "me");

        } else {

            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

    private void initView(JSONObject objResult) {

        if (objResult == null) return;

        JSONArray ary_wallets = objResult.optJSONArray("wallets");

        if (ary_wallets != null && ary_wallets.length() > 0) {
            for (int w = 0; w < ary_wallets.length(); w++) {

                try {

                    JSONObject walObj = (JSONObject)ary_wallets.get(w);

                    switch (walObj.optString("symbol")) {

                        case "ETH":
                            Utility.getSavedPref(this).saveString(Constants.ADDRESS_ETH, walObj.optString("address"));
                            break;
                        case "PIB":
                            Utility.getSavedPref(this).saveString(Constants.ADDRESS_PIB, walObj.optString("address"));
                            break;
                        case "BTC":
                            Utility.getSavedPref(this).saveString(Constants.ADDRESS_BTC, walObj.optString("address"));
                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String str_eth = "0";
        String str_pib = "0";
        String str_prb = "0";
        String str_pgb = "0";
        String str_btc = "0";
        String str_klay = "0";

        JSONArray ary_balance = objResult.optJSONArray("balances");

        if (ary_balance != null && ary_balance.length() > 0) {

            for (int w = 0; w < ary_balance.length(); w++) {

                try {
                    JSONObject walObj = (JSONObject)ary_balance.get(w);

                    switch (walObj.optString("symbol")) {
                        case "ETH":
                            str_eth = walObj.optString("available");
                            break;
                        case "PIB":
                            str_pib = walObj.optString("available");
                            break;
                        case "PRB":
                            str_prb = walObj.optString("available");
                            break;
                        case "PGB":
                            str_pgb = walObj.optString("available");
                            break;
                        case "BTC":
                            str_btc = walObj.optString("available");
                            break;
                        case "KLAY":
                            str_klay = walObj.optString("available");
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            txt_pib.setText(Utility.getConvertedValue(str_pib, false));
            txt_prb.setText(Utility.getConvertedValue(str_prb, false));
            txt_pgb.setText(Utility.getConvertedValue(str_pgb, false));

            pibbleBalancesLinearLayout.setValues(str_pib, str_eth, str_btc, str_klay);

        }

        int active_invoice_count = objResult.optInt("active_invoice_count");

        pibbleBalancesLinearLayout.setBadgeNumber(active_invoice_count);

    }

    private void animationScrollPager(int index) {

        switch (index) {
            case 0:
                img_dot1.setImageDrawable(getResources().getDrawable(R.drawable.icon_dot_selected));
                img_dot2.setImageDrawable(getResources().getDrawable(R.drawable.icon_dot_unselected));
                break;
            case 1:
                img_dot1.setImageDrawable(getResources().getDrawable(R.drawable.icon_dot_unselected));
                img_dot2.setImageDrawable(getResources().getDrawable(R.drawable.icon_dot_selected));
                break;
        }

    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_add_samsung_wallet) {

//            Intent intent = new Intent();
//            intent.setClassName(CWS_PKG_NAME, CWHOME_CLASS_NAME);
//            startActivity(intent);
            Uri uri = Uri.parse(ScwDeepLink.MAIN);
            Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
            displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (!Utility.g_isCalledRefreshToken) initView(objResult);

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

    //-------- samsung wallet -------------

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

//            mServiceAdapter = new CWServiceAdapter(service, mApplicationId, CWBuildType.RELEASE);

            mIsBound = true;

            checkSamsungWallet();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("ssss", "ssss");
        }
    };

    private void checkSamsungWallet() {

        ScwService scwService = ScwService.getInstance();

        if (scwService == null) {

            linear_add_samsung_wallet.setVisibility(View.GONE);
            linear_dots.setVisibility(View.GONE);

        } else {

            if (ScwGlobal.getSeedHash()) {

                ScwService.ScwCheckForMandatoryAppUpdateCallback callback =

                        new ScwService.ScwCheckForMandatoryAppUpdateCallback() {
                            @Override
                            public void onMandatoryAppUpdateNeeded(boolean needed) {

                                if(needed){

                                    Uri uri = Uri.parse(ScwDeepLink.GALAXY_STORE);
                                    Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
                                    displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {

                                    linear_add_samsung_wallet.setVisibility(View.GONE);

                                    if (linear_dots.getVisibility() == View.GONE) {

                                        runOnUiThread(() -> {

                                            linear_dots.setVisibility(View.VISIBLE);

                                            samsungBalances = new SamsungBalancesLinearLayout(HomeWalletActivity.this);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utility.g_deviceWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            samsungBalances.setLayoutParams(params);
                                            linear_balances.addView(samsungBalances);
                                        });
                                    }

                                    getXPubKeyWallet();
                                }
                            }
                        };

                scwService.checkForMandatoryAppUpdate(callback);

            } else {
                linear_add_samsung_wallet.setVisibility(View.VISIBLE);
                linear_dots.setVisibility(View.GONE);
            }

        }

//        Bundle userData = new Bundle();
//        userData.putString(KEY_USER_DATA_SAMPLE, "check wallet");
//        try {
//            boolean initialized = mServiceAdapter.checkWallet();
//            String walletKey = mServiceAdapter.getWalletKey();
//
//            if (initialized) {
//
//                linear_add_samsung_wallet.setVisibility(View.GONE);
//
//                getXPubKeyWallet();
//
//                if (linear_dots.getVisibility() == View.GONE) {
//
//                    linear_dots.setVisibility(View.VISIBLE);
//
//                    samsungBalances = new SamsungBalancesLinearLayout(HomeWalletActivity.this);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utility.g_deviceWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    samsungBalances.setLayoutParams(params);
//                    linear_balances.addView(samsungBalances);
//
//                }
//
//            } else {
//
//                linear_add_samsung_wallet.setVisibility(View.VISIBLE);
//                linear_dots.setVisibility(View.GONE);
//
//            }
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }

    private void getXPubKeyWallet() {

        ScwService.ScwGetExtendedPublicKeyListCallback callback =
                new ScwService.ScwGetExtendedPublicKeyListCallback() {
                    @Override
                    public void onSuccess(List<byte[]> extendedPublicKeyList) {
                        updatePubKey(extendedPublicKeyList.get(0));
                    }

                    @Override
                    public void onFailure(int errorCode) {

                        Toast.makeText(HomeWalletActivity.this, String.format(Locale.ENGLISH, "onResponse error = %d", errorCode), Toast.LENGTH_LONG).show();

                    }
                };


        String hdpath;
        if(mCurrentCurrencyType == 0){ // ETH default
            hdpath = HD_PATH_BTC + mIndex;
        } else {
            hdpath = HD_PATH_ETH + mIndex;
        }

        ArrayList<String> hdPathList = new ArrayList<>();
        hdPathList.add(hdpath);

        ScwService.getInstance().getExtendedPublicKeyList(callback, hdPathList);

//        Bundle userData = new Bundle(); userData.putString(KEY_USER_DATA_SAMPLE, "get public key");
//        try {
//
//            String hdpath;
//            if(mCurrentCurrencyType == 0){ // ETH default
//                hdpath = HD_PATH_BTC + mIndex;
//            } else {
//                hdpath = HD_PATH_ETH + mIndex;
//            }
//
//            ArrayList<String> hdPathList = new ArrayList<>();
//            hdPathList.add(hdpath);
//            mServiceAdapter.getXPublicKeyList(mCallback, TOKEN_GET_XPUB_KEY_LIST, hdPathList, userData);
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }
    //
//    private CWServiceCallback mCallback = new CWServiceCallback() {
//
//        @Override
//        public void onResponse(int reqeustId, int token, int errorCode, Bundle userData, Bundle result) throws RemoteException {
//
//            if (token == TOKEN_CHECK_APP_VERSION) {
//
//                if (errorCode == CWErrorCode.OK) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(HomeWalletActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d latest version", token, errorCode), Toast.LENGTH_LONG).show();
//                    });
//                } else {
//                    runOnUiThread(() -> {
//                        Toast.makeText(HomeWalletActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d error=%d", token, errorCode), Toast.LENGTH_LONG).show();
//                    });
//                }
//                return;
//            }
//
//            if (errorCode < 0) {
//                runOnUiThread(() -> {
//                    Toast.makeText(HomeWalletActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d error=%d", token, errorCode), Toast.LENGTH_LONG).show();
//                });
//                return;
//            }
//
//            switch (token) {
//
//                case TOKEN_GET_XPUB_KEY_FOR_ETH:
//
//                    byte[] ethXPubKey = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_PUBKEY);
//
//                    if (ethXPubKey != null) {
//                        updatePubKey(ethXPubKey);
//                    }
//
//                    break;
//
//                case TOKEN_GET_XPUB_KEY_LIST:
//
//                    ArrayList<String> resultStringArrayList = result.getStringArrayList(CWResult.EXTRAS_KEY_RESULT_PUBKEY_LIST);
//                    if (resultStringArrayList != null && !resultStringArrayList.isEmpty()) {
//                        byte[] sample = HexUtil.toBytes(resultStringArrayList.get(0));
//                        updatePubKey(sample);
//                    }
//
//                    break;
//
//                case TOKEN_SIGN_ETH:
//
//                    signedEthTx = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_SIGNED_TX);
////                    if (signedEthTx != null) {
////                        handlingSignedETHTx(signedEthTx);
////                        Log.d("ssss", "onResponse, signedTx=" + HexUtil.toHexString(signedEthTx));
////                    }
//                    break;
//
//                case TOKEN_SIGN_MESSAGE:
//
//                    signedMessage = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_SIGNED_TX);
////                    if (signedMessage != null) {
////                        handlingSignedMessage(signedMessage);
////                        Log.d("ssss", "onResponse, signedTx=" + HexUtil.toHexString(signedEthTx));
////                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    private void updatePubKey(byte[] pubx) {

        mCurrentPubX = pubx;

        byte[] pub = Arrays.copyOfRange(pubx, 0, 33);
        byte[] chainCode = Arrays.copyOfRange(pubx, 33, 65);
        mCurrentPub = pub;

        byte[] decompKey = ETHUtil.decompressCompressedKey(pub);
        if (decompKey != null) {

            byte[] ethAddress = Keys.getAddress(decompKey);
            final String ethAddrStr = HexUtil.toHexString(ethAddress);
            mEthAddress = ethAddrStr;

            ECKey pubkey = ECKey.fromPublicOnly(pub);

            runOnUiThread(() -> {

                if (samsungBalances != null) {
                    samsungBalances.setValues(mEthAddress);
                }
            });

            if (mCurrentCurrencyType == 60) {

                Utility.getSavedPref(this).saveString(Constants.SAMSUNG_ETH, mEthAddress);

            } else if (mCurrentCurrencyType == 0) {

                Utility.getSavedPref(this).saveString(Constants.SAMSUNG_BTC, mEthAddress);

            }

        }
    }

}


