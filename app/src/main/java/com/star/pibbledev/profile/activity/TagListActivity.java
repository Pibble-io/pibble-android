package com.star.pibbledev.profile.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.discover.activities.DiscoverItemDetailActivity;
import com.star.pibbledev.profile.adaptar.TaglistAdaptar;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.TagModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagListActivity extends BaseActivity implements View.OnClickListener, RequestListener, TaglistAdaptar.TaglistListener {

    public static final String USER_NAME = "username";
    private static final String REQUEST_GET_TAG = "get_tags";

    ImageButton img_back;
    RecyclerView recyclerview;
    TextView txt_title;

    private String requestType;
    private String mUsername;
    private String mCurrentPage;
    private ArrayList<TagModel> ary_tags = new ArrayList<>();

    private TaglistAdaptar taglistAdaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_taglist_followed);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_title = (TextView)findViewById(R.id.txt_title);

        mUsername = getIntent().getStringExtra(USER_NAME);

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        taglistAdaptar = new TaglistAdaptar(this, ary_tags);
        taglistAdaptar.setClickListener(this);
        recyclerview.setAdapter(taglistAdaptar);

        getFollowedTags(mUsername, "1");

        recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerview.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                getFollowedTags(mUsername, String.valueOf(page + 1));

            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });
    }

    private void getFollowedTags(String username, String page) {

        requestType = REQUEST_GET_TAG;

        mCurrentPage = page;

        if (Constants.isLifeToken(this)) {
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getTagFollowings(this, this, access_token, username, page, "15");
        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void parseTagInfo(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.optJSONArray("items");

        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {

                    JSONObject tagobj = (JSONObject)jsonArray.get(i);

                    TagModel tagModel = new TagModel();

                    tagModel.id = tagobj.optInt("id");
                    tagModel.tagID = tagobj.optInt("tag_id");
                    tagModel.tag = tagobj.optString("name");
                    tagModel.posted = tagobj.optInt("posted");

                    tagModel.isfollowed = true;

                    ary_tags.add(tagModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (taglistAdaptar != null) taglistAdaptar.notifyDataSetChanged();

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

            if (requestType.equals(REQUEST_GET_TAG)) {
                parseTagInfo(objResult);
            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(REQUEST_GET_TAG)) {
                getFollowedTags(mUsername, mCurrentPage);
            }
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onClickTag(int position) {

        Intent intent = new Intent(this, DiscoverItemDetailActivity.class);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, ary_tags.get(position).tag);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, "");
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, Constants.DISCOVER_TAG_TEXT);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onClickFollow(int position, boolean flag) {

    }
}
