package com.star.pibbledev.profile.activity.setting.friend;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.profile.adaptar.SettingCloseFriendRecyclerAdaptar;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.ActionStatusDialog;
import com.star.pibbledev.services.global.model.FriendsModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettingCloseFriendActivity extends BaseActivity implements View.OnClickListener, SettingCloseFriendRecyclerAdaptar.listListener, RequestListener {

    private static final String REQUEST_GET_FRIENDS = "request_get_friend";
    private static final String REQUEST_CANCEL_FRIENDSHIP = "request_cancel_friendship";

    ImageButton img_back;
    RecyclerView recyclerView;

    SettingCloseFriendRecyclerAdaptar recyclerAdaptar;
    ArrayList<FriendsModel> mFriends = new ArrayList<>();

    String requestType;

    int mCurrentPage, mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_closefriend);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        recyclerAdaptar = new SettingCloseFriendRecyclerAdaptar(this, mFriends);
        recyclerView.setAdapter(recyclerAdaptar);
        recyclerAdaptar.setClickListener(this);

        getFriends("1");

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                getFriends(String.valueOf(page + 1));

            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }
    }

    private void getFriends(String page) {

        requestType = REQUEST_GET_FRIENDS;

        mCurrentPage = Integer.parseInt(page);

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserFriends(this, this, access_token, "me", page, "15");

        } else {

            Constants.requestRefreshToken(this, this);
        }

    }

    private void cancelFriendShip(int position) {

        FriendsModel friendsModel = mFriends.get(position);

        requestType = REQUEST_CANCEL_FRIENDSHIP;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().cancelFriendship(this, this, token, friendsModel.user_name);

        } else {
            Constants.requestRefreshToken(this, this);
        }


    }

    private void parseFriendInfo(JSONObject jsonObject) {

        JSONArray items = jsonObject.optJSONArray("items");

        int aryCnt = mFriends.size();

        if (items != null && items.length() > 0) {

            for (int i = 0; i < items.length(); i++) {

                try {
                    JSONObject object = (JSONObject) items.get(i);

                    FriendsModel friendsModel = new FriendsModel();

                    friendsModel.user_id = object.optInt(Constants.ID);
                    friendsModel.user_avatar = object.optString("avatar");
                    friendsModel.user_name = object.optString("username");

                    friendsModel.level = object.optInt("level");

                    JSONArray ary_balance = object.optJSONArray("balances");

                    if (ary_balance != null && ary_balance.length() > 0) {

                        for (int w = 0; w < ary_balance.length(); w++) {
                            JSONObject walObj = (JSONObject)ary_balance.get(w);
                            switch (walObj.optString("symbol")) {
                                case "ETH":
                                    break;
                                case "PIB":
                                    friendsModel.available_PIB = walObj.optString("available");
                                    break;
                                case "PRB":
                                    friendsModel.available_PRB = walObj.optString("available");
                                    break;
                                case "PGB":
                                    friendsModel.available_PGB = walObj.optString("available");
                                    break;
                                case "BTC":
                                    break;
                            }
                        }
                    }

                    mFriends.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (recyclerAdaptar != null) recyclerAdaptar.notifyItemRangeInserted(aryCnt, mFriends.size());
        }
    }

    // recyclerview Adatar

    @Override
    public void onClickUser(int position) {

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, mFriends.get(position).user_name);
        intent.putExtra(UserProfileActivity.SELECT_USERID, mFriends.get(position).user_id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onUserClose(int position) {

        mPosition = position;

        cancelFriendShip(position);

    }

    // server request listener

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (requestType.equals(REQUEST_GET_FRIENDS)) {

                parseFriendInfo(objResult);

            } else if (requestType.equals(REQUEST_CANCEL_FRIENDSHIP)) {

                ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.closed));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        mFriends.remove(mPosition);

                        if (recyclerAdaptar != null) recyclerAdaptar.notifyDataSetChanged();
                    }
                }, 1000);

            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_FRIENDS)) {

                getFriends(String.valueOf(mCurrentPage));

            } else if (requestType.equals(REQUEST_CANCEL_FRIENDSHIP)) {

                cancelFriendShip(mPosition);

            }
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
