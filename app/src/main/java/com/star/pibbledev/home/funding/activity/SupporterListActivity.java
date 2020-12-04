package com.star.pibbledev.home.funding.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.funding.adaptar.SupportListAdaptar;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.model.FundingSupportersModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupporterListActivity extends BaseActivity implements View.OnClickListener, RequestListener, SupportListAdaptar.SupportListListener {

    ImageButton img_back;
    RecyclerView recyclerView;

    private int postID, currentPage;
    private ArrayList<FundingSupportersModel> aryDatas;
    private SupportListAdaptar adaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_funding_supporter_list);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        postID = getIntent().getIntExtra(Constants.ID, -1);

        aryDatas = new ArrayList<>();

        adaptar = new SupportListAdaptar(this, aryDatas);
        recyclerView.setAdapter(adaptar);
        adaptar.setClickListener(this);

        getFundingContributor(1);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getFundingContributor(page + 1);
            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });

    }

    // get funding contributor

    private void getFundingContributor(int page) {

        currentPage = page;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getFundingContributors(this, this, token, postID, page, 15);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void parsingContributors(JSONObject jsonObject) {

        JSONArray items = jsonObject.optJSONArray("items");

        if (items != null && items.length() > 0) {

            for (int i = 0; i < items.length(); i++) {

                try {

                    JSONObject supportor = (JSONObject) items.get(i);

                    if (supportor == null) continue;

                    FundingSupportersModel supportersModel = new FundingSupportersModel();
                    supportersModel.value = supportor.optInt(Constants.VALUE);
                    supportersModel.price = supportor.optString(Constants.PRICE);
                    supportersModel.created_at = supportor.optString(Constants.CREATED_AT);
                    supportersModel.userModel = ParseUtility.userModel(supportor.optJSONObject(Constants.USER));

                    aryDatas.add(supportersModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adaptar.notifyDataSetChanged();

        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (objResult != null) parsingContributors(objResult);

        } else {
            getFundingContributor(currentPage);
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onClickSupporter(int position) {

        FundingSupportersModel supportersModel = aryDatas.get(position);

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, supportersModel.userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, supportersModel.userModel.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }
}
