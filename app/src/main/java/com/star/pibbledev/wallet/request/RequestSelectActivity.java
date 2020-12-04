package com.star.pibbledev.wallet.request;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
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
import com.star.pibbledev.wallet.receive.ReceiveQraddressActivity;
import com.star.pibbledev.wallet.send.FriendsListAdaptar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class RequestSelectActivity extends BaseActivity implements  View.OnClickListener, RequestListener , FriendsListAdaptar.FriendsListListener {

    public static String REQUEST_TYPE;
    public static final String REQUEST_GET_FRIENDS = "get_friends";
    public static final String REQUEST_CREATE_INVOICE = "create_invoice";

    ImageButton img_back;
    TextView txt_value, txt_changedValue, txt_unit, txt_send;
    EditText txt_message;
    LinearLayout linear_send;
    NavigationTabStrip tab_bar;
    RecyclerView recyclerview;

    FriendsListAdaptar friendsListAdaptar;

    String str_amount, str_krw, str_unit;

    int mSelectedFriend = -1;

    ArrayList<FriendsModel> ary_friends;
    int totalPagecount, currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_request_to);

        setLightStatusBar();

//        Utility.disKeyboardTouch(findViewById(R.id.linear_all), this);

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        txt_send = (TextView)findViewById(R.id.txt_send);

        txt_value = (TextView)findViewById(R.id.txt_value);
        txt_changedValue = (TextView)findViewById(R.id.txt_changedValue);
        txt_unit = (TextView)findViewById(R.id.txt_unit);

        str_amount = getIntent().getStringExtra("unit_value");
        str_krw = getIntent().getStringExtra("krw_value");
        str_unit = getIntent().getStringExtra("unit");

        txt_unit.setText(str_unit);
        txt_value.setText(Utility.formatedNumberString(Float.parseFloat(str_amount)));

        String currency = Utility.getReadPref(this).getStringValue(Constants.CURRENCY);

        txt_changedValue.setText(String.format("%s %s", Utility.formatedNumberString(Float.parseFloat(str_krw)), currency));

        txt_message = (EditText)findViewById(R.id.txt_message);

        linear_send = (LinearLayout)findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        ary_friends = new ArrayList<>();
        currentPage = 1;
        friendsListAdaptar = new FriendsListAdaptar(this, ary_friends);
        friendsListAdaptar.setClickListener(this);
        recyclerview.setAdapter(friendsListAdaptar);

        tab_bar = (NavigationTabStrip)findViewById(R.id.tab_bar);
        tab_bar.setTitles(getString(R.string.friends), getString(R.string.recent));
        tab_bar.setTabIndex(0, true);
        tab_bar.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
//                Log.e("xxx", title);
            }

            @Override
            public void onEndTabSelected(String title, int index) {

                currentPage = 1;
//                ary_friends.clear();
                friendsListAdaptar.mSelectedCell = -1;
                friendsListAdaptar.clear();
                mSelectedFriend = -1;

                txt_send.setTextColor(getResources().getColor(R.color.light_gray));
                linear_send.setEnabled(false);

                if (index == 0) {
                    getFriends("1");
                } else if (index == 1) {
                    getFriendsRecentlyFundsent("1");
                }
            }
        });

        getFriends("1");

        recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) Objects.requireNonNull(recyclerview.getLayoutManager())) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (tab_bar.getTabIndex() == 0) {

                    getFriends(String.valueOf(page + 1));

                } else if (tab_bar.getTabIndex() == 1) {

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
    }

    private void getFriends(String page) {

        if (Constants.isLifeToken(this)) {

            REQUEST_TYPE = REQUEST_GET_FRIENDS;
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserFriends(this, this, access_token, "me", page, "15");

        } else {

            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
//            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void getFriendsRecentlyFundsent(String page) {

        if (Constants.isLifeToken(this)) {

            REQUEST_TYPE = REQUEST_GET_FRIENDS;
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getFriendsRecentlyFundsent(this, this, access_token, page, "15");

        } else {
            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
//            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void parseFriendInfo(JSONObject objResult) {

        JSONArray items = objResult.optJSONArray("items");
        JSONObject pageObj = objResult.optJSONObject("pagination");
        if (pageObj == null) return;
        totalPagecount = pageObj.optInt("pages");

        if (items != null && items.length() > 0) {

            for (int i = 0; i < items.length(); i++) {

                try {
                    JSONObject jsonObject = (JSONObject) items.get(i);

                    FriendsModel friendsModel = new FriendsModel();

                    friendsModel.user_avatar = jsonObject.optString("avatar");
                    friendsModel.user_name = jsonObject.optString("username");
                    friendsModel.user_uuid = jsonObject.optString("uuid");

                    ary_friends.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            friendsListAdaptar.notifyDataSetChanged();

        }

//        if (totalPagecount > 1 && currentPage == 1) {
//
//            LoadMoreWrapper.with(friendsListAdaptar)
//                    .setShowNoMoreEnabled(true)
//                    .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
//                        @Override
//                        public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
//
//                            currentPage++;
//
//                            getFriends(String.valueOf(currentPage));
//
//                            if (currentPage == totalPagecount) {
//                                enabled.setLoadMoreEnabled(false);
//                            }
//                        }
//                    })
//                    .into(recyclerview);
//        }
    }

    private void createInvoice() {

        if (Constants.isLifeToken(this)) {

            REQUEST_TYPE = REQUEST_CREATE_INVOICE;
            showHUD();
            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            String description = txt_message.getText().toString();
            ServerRequest.getSharedServerRequest().createWalletInvoice(this, this, accessToken, ary_friends.get(mSelectedFriend).user_uuid, (int) Float.parseFloat(str_amount), str_unit, description);

        } else {

            Intent intent = new Intent(this, RegisterPinActivity.class);
            intent.putExtra(RegisterPinActivity.IS_EXPIRED, true);
            startActivity(intent);
//            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            Utility.dismissKeyboard(this);
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == linear_send) {

            if (mSelectedFriend != -1) {
                createInvoice();
            }

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (REQUEST_TYPE.equals(REQUEST_GET_FRIENDS)) {

            parseFriendInfo(objResult);

        } else if (REQUEST_TYPE.equals(REQUEST_CREATE_INVOICE)) {

            hideHUD();

            AlertVerticalDialog dialog = new AlertVerticalDialog(this, null,
                    getString(R.string.successfully_requested),
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {

                    if (position == 1) {

                        if (HomeWalletActivity.homeWalletActivity != null) HomeWalletActivity.homeWalletActivity.finish();
                        if (ReceiveQraddressActivity.receiveQraddressActivity != null) ReceiveQraddressActivity.receiveQraddressActivity.finish();
                        if (RequestCreateActivity.requestCreateActivity != null) RequestCreateActivity.requestCreateActivity.finish();

                        Intent intent = new Intent(RequestSelectActivity.this, HomeWalletActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
                    }

                    dismiss();

                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

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
        txt_send.setTextColor(getResources().getColor(R.color.colorMain));
        linear_send.setEnabled(true);
        mSelectedFriend = position;
    }
}
