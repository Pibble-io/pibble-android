package com.star.pibbledev.profile.activity.setting.account;

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
import com.star.pibbledev.profile.adaptar.SettingMutedUserRecyclerAdaptar;
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

public class SettingAccountMutedUsersActivity extends BaseActivity implements View.OnClickListener , SettingMutedUserRecyclerAdaptar.listListener, RequestListener {

    private static final String REQUEST_GET_MUTED_USERS = "request_get_muted_users";
    private static final String REQUEST_SEND_UNMUTED = "request_send_unmuted";

    ImageButton img_back;
    RecyclerView recyclerView;

    ArrayList<FriendsModel> mUsers = new ArrayList<>();
    SettingMutedUserRecyclerAdaptar recyclerAdaptar;

    String requestType;
    int mCurrentPage, mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_muteduser);

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

        recyclerAdaptar = new SettingMutedUserRecyclerAdaptar(this, mUsers);
        recyclerView.setAdapter(recyclerAdaptar);
        recyclerAdaptar.setClickListener(this);

        getMutedUsers(1);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                getMutedUsers(page + 1);

            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });
    }

    private void getMutedUsers(int page) {

        requestType = REQUEST_GET_MUTED_USERS;
        mCurrentPage = page;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getMutedUsers(this, this, token, String.valueOf(page),"15", "desc");

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void sendUnMute(int position) {

        FriendsModel friendsModel = mUsers.get(position);

        requestType = REQUEST_SEND_UNMUTED;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().setting(this, this, token, friendsModel.user_id, false);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void parsingUsersInfo(JSONObject jsonObject) {

        JSONArray items = jsonObject.optJSONArray("items");

        int aryCnt = mUsers.size();

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

                    mUsers.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (recyclerAdaptar != null) recyclerAdaptar.notifyItemRangeInserted(aryCnt, mUsers.size());
        }

    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }

    }

//    SettingMutedUserRecyclerAdaptar listener

    @Override
    public void onClickUser(int position) {

        FriendsModel friendsModel = mUsers.get(position);

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, friendsModel.user_name);
        intent.putExtra(UserProfileActivity.SELECT_USERID, friendsModel.user_id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onUserUnmute(int position) {

        mPosition = position;
        sendUnMute(position);

    }

//    Server Requestlistener

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (requestType.equals(REQUEST_GET_MUTED_USERS)) {

                parsingUsersInfo(objResult);

            } else if (requestType.equals(REQUEST_SEND_UNMUTED)) {

                ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.unmuted));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        mUsers.remove(mPosition);
                        recyclerAdaptar.notifyDataSetChanged();

                    }
                }, 1000);

            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_MUTED_USERS)) {

                getMutedUsers(mCurrentPage);

            } else if (requestType.equals(REQUEST_SEND_UNMUTED)) {

                sendUnMute(mPosition);

            }

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
