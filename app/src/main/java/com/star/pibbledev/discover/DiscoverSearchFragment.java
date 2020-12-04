package com.star.pibbledev.discover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.dashboard.DashboardFragment;
import com.star.pibbledev.R;
import com.star.pibbledev.discover.activities.DiscoverItemDetailActivity;
import com.star.pibbledev.discover.adaptars.CustomLayoutManagerRecyclerAdaptar;
import com.star.pibbledev.discover.adaptars.DiscoverRecycleItemAdaptar;
import com.star.pibbledev.discover.listeners.DiscoverLoadmoreListener;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.DiscoverItemModel;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.global.model.PlaceModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.services.sqlite.History;
import com.star.pibbledev.services.sqlite.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class DiscoverSearchFragment extends Fragment implements View.OnClickListener, RequestListener, DiscoverLoadmoreListener, DiscoverRecycleItemAdaptar.DiscoverRecycleItemListener, CustomLayoutManagerRecyclerAdaptar.CustomLayoutItemListener {

    public static final String TAG = "DiscoverSearchFragment";

    private static final String REQUEST_GET_POSTS = "get_all_posts";
    private static final String REQUEST_GET_TOP_DATA = "get_top_datas";
    private static final String REQUEST_GET_USER = "get_users";
    private static final String REQUEST_GET_TAG = "get_tags";
    private static final String REQUEST_GET_PLACE = "get_place";

    private Context mContext;

    private EditText txt_search;
    private ImageButton btn_cancel;
    private LinearLayout linear_search_result, linear_top, linear_people, linear_tags, linear_place;
    private TextView txt_top, txt_people, txt_tags, txt_place;
    private ImageView img_history;

    private RecyclerView mTvRecyclerView;
    private RecyclerView discover_recycler_view;
    private ImageLoader imageLoader;

    public static ArrayList<PostsModel> ary_postsData;
    private ArrayList<DiscoverItemModel> ary_discoverDatas = new ArrayList<>();

    private DiscoverRecycleItemAdaptar discoverRecycleItemAdaptar;

    private String requesType = "";
    private int indexCategory = 1;
    private boolean isSelectedHistory = false;
    private boolean isCalledLoadMore = false;
    private int currentPage;
    private String currentSearch;

    private DatabaseHelper db;
    private DashboardFragment dashboardFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment_home_search_discover, null);
        mContext = getActivity();

        txt_search = (EditText) view.findViewById(R.id.txt_search);
        txt_top = (TextView) view.findViewById(R.id.txt_top);
        txt_people = (TextView) view.findViewById(R.id.txt_people);
        txt_tags = (TextView) view.findViewById(R.id.txt_tags);
        txt_place = (TextView) view.findViewById(R.id.txt_place);
        btn_cancel = (ImageButton) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        linear_search_result = (LinearLayout) view.findViewById(R.id.linear_search_result);
        linear_top = (LinearLayout) view.findViewById(R.id.linear_top);
        linear_top.setOnClickListener(this);
        linear_people = (LinearLayout) view.findViewById(R.id.linear_people);
        linear_people.setOnClickListener(this);
        linear_tags = (LinearLayout) view.findViewById(R.id.linear_tags);
        linear_tags.setOnClickListener(this);
        linear_place = (LinearLayout) view.findViewById(R.id.linear_place);
        linear_place.setOnClickListener(this);
        img_history = (ImageView) view.findViewById(R.id.img_history);
        img_history.setOnClickListener(this);

        ary_postsData = new ArrayList<>();

        mTvRecyclerView = (RecyclerView) view.findViewById(R.id.tv_recycler_view);
        mTvRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTvRecyclerView.setHasFixedSize(false);
        mTvRecyclerView.setItemViewCacheSize(20);
        mTvRecyclerView.setDrawingCacheEnabled(true);
        mTvRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        discover_recycler_view = (RecyclerView) view.findViewById(R.id.discover_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        discover_recycler_view.setLayoutManager(layoutManager);
        discover_recycler_view.setItemAnimator(new DefaultItemAnimator());
        discover_recycler_view.setHasFixedSize(true);
        discover_recycler_view.setItemViewCacheSize(20);
        discover_recycler_view.setDrawingCacheEnabled(true);
        discover_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mTvRecyclerView.setVisibility(View.VISIBLE);
        linear_search_result.setVisibility(View.INVISIBLE);

        DashboardActivity dashboardActivity = (DashboardActivity)getActivity();
        dashboardFragment = ((DashboardFragment) dashboardActivity.getFragment(0));

        mTvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < -80) {

                    if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(true);

                } else if (dy > 0) {

                    if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(false);

                }
            }
        });

        initEditText();

        imageLoader = ImageLoader.getInstance();
        db = new DatabaseHelper(mContext);

        discoverRecycleItemAdaptar = new DiscoverRecycleItemAdaptar(mContext, ary_discoverDatas, imageLoader);
        discoverRecycleItemAdaptar.setClickListener(this);
        discover_recycler_view.setAdapter(discoverRecycleItemAdaptar);

        getAllPosts(1);
        onClickCategory(indexCategory);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {

                    isCalledLoadMore = true;

                    switch (indexCategory) {
                        case 1:
                            getTopDatas(editable.toString());
                            break;
                        case 2:
                            getPeoples(editable.toString(), "1");
                            break;
                        case 3:
                            getTags(editable.toString());
                            break;
                        case 4:
                            getPlaces(editable.toString());
                            break;
                    }

                    discover_recycler_view.clearOnScrollListeners();
                    discover_recycler_view.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) discover_recycler_view.getLayoutManager()) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {

                            isCalledLoadMore = false;

                            switch (indexCategory) {
                                case 2:
                                    getPeoples(editable.toString(), String.valueOf(page + 1));
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    break;
                            }
                        }

                        @Override
                        public void onScrolledUp() {
                            if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(true);
                        }

                        @Override
                        public void onScrolledDown() {
                            if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(false);
                        }
                    });

                }
            }
        });

        txt_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (isSelectedHistory) {
                        isSelectedHistory = false;
                        indexCategory = 1;
                        changeActionStatus(true);
                        onClickCategory(indexCategory);
                    }

                    mTvRecyclerView.setVisibility(View.INVISIBLE);
                    linear_search_result.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });
    }

    public void autoScrollToTop() {

        if (mTvRecyclerView != null && mTvRecyclerView.getVisibility() == View.VISIBLE) {

            GridLayoutManager layoutManager = (GridLayoutManager)mTvRecyclerView.getLayoutManager();

            if (layoutManager == null) return;

            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
                @Override protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };

            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);
        }

    }

    public void customDestory() {

        if (discover_recycler_view != null && discover_recycler_view.getAdapter() != null) discover_recycler_view.setAdapter(null);
        if (mTvRecyclerView != null && mTvRecyclerView.getAdapter() != null) mTvRecyclerView.setAdapter(null);

        imageLoader = null;
    }

    private void onClickCategory(int index) {

        if (txt_search.getText().toString().length() == 0) {
            ary_discoverDatas.clear();
            discoverRecycleItemAdaptar.notifyDataSetChanged();
        }

        switch (index) {

            case 0:
                txt_top.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_people.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_tags.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_place.setTextColor(mContext.getResources().getColor(R.color.gray));

                ary_discoverDatas.addAll(getAllHistoryItems());
                discoverRecycleItemAdaptar.notifyDataSetChanged();

                break;

            case 1:
                txt_top.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                txt_people.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_tags.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_place.setTextColor(mContext.getResources().getColor(R.color.gray));

                if (txt_search.getText().toString().length() > 0) {
                    getTopDatas(txt_search.getText().toString());
                }

                break;
            case 2:
                txt_top.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_people.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                txt_tags.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_place.setTextColor(mContext.getResources().getColor(R.color.gray));

                if (txt_search.getText().toString().length() > 0) {
                    getPeoples(txt_search.getText().toString(), "1");
                }

                break;
            case 3:
                txt_top.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_people.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_tags.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                txt_place.setTextColor(mContext.getResources().getColor(R.color.gray));

                if (txt_search.getText().toString().length() > 0) {
                    getTags(txt_search.getText().toString());
                }

                break;
            case 4:
                txt_top.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_people.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_tags.setTextColor(mContext.getResources().getColor(R.color.gray));
                txt_place.setTextColor(mContext.getResources().getColor(R.color.colorMain));

                if (txt_search.getText().toString().length() > 0) {
                    getPlaces(txt_search.getText().toString());
                }

                break;
        }

    }

    private void changeActionStatus(boolean isEnable) {

        linear_top.setEnabled(isEnable);
        linear_people.setEnabled(isEnable);
        linear_place.setEnabled(isEnable);
        linear_tags.setEnabled(isEnable);

        if (isEnable) {
            img_history.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_discover_history_gray));
        } else {
            img_history.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_discover_history_blue));
        }
    }

    private void clearSearchList() {

        txt_search.clearFocus();
        txt_search.setText("");
        indexCategory = 1;
        onClickCategory(indexCategory);
        mTvRecyclerView.setVisibility(View.VISIBLE);
        linear_search_result.setVisibility(View.INVISIBLE);

        isSelectedHistory = false;
        img_history.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_discover_history_gray));

        ary_discoverDatas.clear();
        discoverRecycleItemAdaptar.notifyDataSetChanged();

        Utility.dismissKeyboard(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if (!(fragment instanceof DiscoverSearchFragment)) return;

        Utility.g_indexDashboardTab = 2;

        if (Utility.g_isChanged) {

            CustomLayoutManagerRecyclerAdaptar adaptar = (CustomLayoutManagerRecyclerAdaptar)mTvRecyclerView.getAdapter();

            if (adaptar != null) {
                adaptar.refreshData(ary_postsData);
            } else {
                CustomLayoutManagerRecyclerAdaptar newAdaptar = new CustomLayoutManagerRecyclerAdaptar(mContext, this, ary_postsData, imageLoader);
                mTvRecyclerView.setAdapter(newAdaptar);
                newAdaptar.setCustomLayoutItemOnclickListener(this);
            }
        }
    }

    private void getAllPosts(int pageNumber) {

        currentPage = pageNumber;

        if (!Constants.isNetwork(mContext)) {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
            return;
        }

        requesType = REQUEST_GET_POSTS;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getDiscovers( this, mContext, access_token, String.valueOf(pageNumber), "15");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void getTopDatas(String search) {

        if (!Constants.isNetwork(mContext)) {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
            return;
        }

        currentSearch = search;

        requesType = REQUEST_GET_TOP_DATA;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getTopDatas( this, mContext, access_token, search);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void getPeoples(String search, String page) {

        if (!Constants.isNetwork(mContext)) {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
            return;
        }

        currentSearch = search;
        currentPage = Integer.parseInt(page);

        requesType = REQUEST_GET_USER;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPeoples( this, mContext, access_token, search, page, "15");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void getTags(String search) {

        if (!Constants.isNetwork(mContext)) {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
            return;
        }

        currentSearch = search;

        requesType = REQUEST_GET_TAG;

        if (Constants.isLifeToken(mContext)) {

            ServerRequest.getSharedServerRequest().getTagSearchInfo( this, mContext, search);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void getPlaces(String search) {

        if (!Constants.isNetwork(mContext)) {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
            return;
        }

        currentSearch = search;

        requesType = REQUEST_GET_PLACE;

        if (Constants.isLifeToken(mContext)) {

            ServerRequest.getSharedServerRequest().getPlace( this, mContext, search);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }


    private void parsingPostsArray(JSONObject objResult) {

        int cnt_all = ary_postsData.size();

        try {

            JSONArray posts = objResult.optJSONArray("items");

            for (int i = 0; i < posts.length(); i++) {

                JSONObject objPost = (JSONObject) posts.get(i);

                boolean isLoaded = false;
                for (int cnt = 0; cnt < cnt_all; cnt++) {
                    PostsModel postsModel = ary_postsData.get(cnt);
                    if (postsModel.id == objPost.optInt("id")) {
                        isLoaded = true;
                    }
                }

                if (isLoaded) continue;

                PostsModel postsModel = ParseUtility.postModel(objPost);

                if (postsModel.ary_media != null && postsModel.ary_media.size() > 0) {

                    ary_postsData.add(postsModel);

                    for (int j = 0; j < postsModel.ary_media.size(); j++) {

                        String img_url = "";

                        if (postsModel.ary_media.get(j).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                            img_url = postsModel.ary_media.get(j).poster;
                        } else {
                            img_url = postsModel.ary_media.get(j).url;
                        }

                        imageLoader.loadImage(img_url, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);

                            }
                        });
                    }
                }
            }

            CustomLayoutManagerRecyclerAdaptar adaptar = (CustomLayoutManagerRecyclerAdaptar)mTvRecyclerView.getAdapter();

            if (adaptar != null) {
                adaptar.updateData(ary_postsData, cnt_all);
            } else {

                CustomLayoutManagerRecyclerAdaptar newAdaptar = new CustomLayoutManagerRecyclerAdaptar(mContext, this, ary_postsData, imageLoader);
                mTvRecyclerView.setAdapter(newAdaptar);
                newAdaptar.setCustomLayoutItemOnclickListener(this);
            }

            requesType = "";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsingTopData(JSONObject jsonObject) {

        ary_discoverDatas.clear();

        if (jsonObject != null) {

            JSONArray userAry = jsonObject.optJSONArray("users");
            if (userAry != null && userAry.length() > 0) {

                for (int i = 0; i < userAry.length(); i++) {
                    try {
                        JSONObject userObj = (JSONObject) userAry.get(i);

                        if (userObj == null) continue;

                        DiscoverItemModel discoverItemModel = new DiscoverItemModel();
                        discoverItemModel.itemType = Constants.DISCOVER_USER;
                        discoverItemModel.itemCategory = Constants.DISCOVER_TOP;
                        discoverItemModel.itemTitle = userObj.optString("username");
                        discoverItemModel.itemID = userObj.optInt("id");

                        JSONArray ary_balances = userObj.optJSONArray("balances");
                        if (ary_balances != null && ary_balances.length() == 3) {

                            String rb = ((JSONObject) ary_balances.get(1)).optString("available");
                            String gb = ((JSONObject) ary_balances.get(2)).optString("available");

                            Float val_rb = Float.parseFloat(rb);
                            Float val_gb = Float.parseFloat(gb);

                            @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%d R.B %d G.B %d", userObj.optInt("level"), val_rb.intValue(), val_gb.intValue());
                            discoverItemModel.itmeDetails = str_level;
                        }

                        discoverItemModel.itemAvatar = userObj.optString("avatar");

                        if (discoverItemModel.itemAvatar.equals("null")) {
                            if (discoverItemModel.itemTitle.length() > 14) {
                                discoverItemModel.itemAvatarTemp = 14;
                            } else {
                                discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                            }

                        } else {

                            if (imageLoader != null) {

                                imageLoader.loadImage(discoverItemModel.itemAvatar, new ImageSize(Utility.dpToPx(34), Utility.dpToPx(34)),new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                    }
                                });
                            }

                        }

                        discoverItemModel.isShowingCopy = isSelectedHistory;

                        ary_discoverDatas.add(discoverItemModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            JSONArray placeAry = jsonObject.optJSONArray("places");
            if (placeAry != null && placeAry.length() > 0) {

                for (int i = 0; i < placeAry.length(); i++) {

                    try {

                        JSONObject placeObj = (JSONObject)placeAry.get(i);
                        if (placeObj == null) continue;

                        DiscoverItemModel discoverItemModel = new DiscoverItemModel();

                        discoverItemModel.itemType = Constants.DISCOVER_PLACE;
                        discoverItemModel.itemCategory = Constants.DISCOVER_TOP;
                        discoverItemModel.itemTitle =  placeObj.optString("description");
                        discoverItemModel.itmeDetails = String.valueOf(placeObj.optInt("posted"));
                        discoverItemModel.itemID = placeObj.optInt("id");

                        discoverItemModel.placeModel = new PlaceModel();
                        discoverItemModel.placeModel.lng = placeObj.optString("lng");
                        discoverItemModel.placeModel.lat = placeObj.optString("lat");

                        if (discoverItemModel.itemTitle.length() > 14) {
                            discoverItemModel.itemAvatarTemp = new Random().nextInt(15);
                        } else {
                            discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                        }

                        discoverItemModel.isShowingCopy = isSelectedHistory;

                        ary_discoverDatas.add(discoverItemModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            JSONArray tagAry = jsonObject.optJSONArray("tags");
            if (tagAry != null && tagAry.length() > 0) {

                for (int i = 0; i < tagAry.length(); i++) {

                    try {

                        JSONObject tagObj = (JSONObject)tagAry.get(i);
                        if (tagObj == null) continue;

                        DiscoverItemModel discoverItemModel = new DiscoverItemModel();

                        discoverItemModel.itemType = Constants.DISCOVER_TAG;
                        discoverItemModel.itemCategory = Constants.DISCOVER_TOP;
                        discoverItemModel.itemTitle = tagObj.optString("tag");
                        discoverItemModel.itmeDetails = String.valueOf(tagObj.optInt("posted"));
                        discoverItemModel.itemID = tagObj.optInt("id");

                        if (discoverItemModel.itemTitle.length() > 14) {
                            discoverItemModel.itemAvatarTemp = new Random().nextInt(15);
                        } else {
                            discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                        }

                        discoverItemModel.isShowingCopy = isSelectedHistory;

                        ary_discoverDatas.add(discoverItemModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

        discoverRecycleItemAdaptar.notifyDataSetChanged();
        requesType = "";

    }

    private void parsingPeopleResult(JSONObject jsonObject) {

        if (isCalledLoadMore) ary_discoverDatas.clear();

        JSONArray peopleAry = jsonObject.optJSONArray("items");

        if (peopleAry != null && peopleAry.length() > 0) {

            for (int i = 0; i < peopleAry.length(); i++) {

                try {

                    JSONObject peopleObj = (JSONObject) peopleAry.get(i);

                    if (peopleObj == null) continue;

                    DiscoverItemModel discoverItemModel = new DiscoverItemModel();
                    discoverItemModel.itemType = Constants.DISCOVER_USER;
                    discoverItemModel.itemCategory = Constants.DISCOVER_USER;
                    discoverItemModel.itemTitle = peopleObj.optString("username");
                    discoverItemModel.itemID = peopleObj.optInt("id");

                    JSONArray ary_balances = peopleObj.optJSONArray("balances");
                    if (ary_balances != null && ary_balances.length() == 3) {

                        String rb = ((JSONObject) ary_balances.get(1)).optString("available");
                        String gb = ((JSONObject) ary_balances.get(2)).optString("available");

                        Float val_rb = Float.parseFloat(rb);
                        Float val_gb = Float.parseFloat(gb);

                        @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%d R.B %d G.B %d", peopleObj.optInt("level"), val_rb.intValue(), val_gb.intValue());
                        discoverItemModel.itmeDetails = str_level;
                    }

                    discoverItemModel.itemAvatar = peopleObj.optString("avatar");

                    if (discoverItemModel.itemAvatar.equals("null")) {
                        if (discoverItemModel.itemTitle.length() > 14) {
                            discoverItemModel.itemAvatarTemp = 14;
                        } else {
                            discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                        }

                    } else {

                        if (imageLoader != null) {

                            imageLoader.loadImage(discoverItemModel.itemAvatar, new ImageSize(Utility.dpToPx(34), Utility.dpToPx(34)),new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                }
                            });
                        }

                    }

                    discoverItemModel.isShowingCopy = isSelectedHistory;

                    ary_discoverDatas.add(discoverItemModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        discoverRecycleItemAdaptar.notifyDataSetChanged();

    }

    private void parsingTagResult(JSONObject jsonObject) {

        ary_discoverDatas.clear();

        JSONArray tagAry = jsonObject.optJSONArray("items");
        if (tagAry != null && tagAry.length() > 0) {

            for (int i = 0; i < tagAry.length(); i++) {

                try {

                    JSONObject tagObj = (JSONObject) tagAry.get(i);
                    if (tagObj == null) continue;

                    DiscoverItemModel discoverItemModel = new DiscoverItemModel();

                    discoverItemModel.itemType = Constants.DISCOVER_TAG;
                    discoverItemModel.itemCategory = Constants.DISCOVER_TAG;
                    discoverItemModel.itemTitle = tagObj.optString("tag");
                    discoverItemModel.itmeDetails = String.valueOf(tagObj.optInt("posted"));
                    discoverItemModel.itemID = tagObj.optInt("id");

                    discoverItemModel.itemAvatar = "";

                    if (discoverItemModel.itemTitle.length() > 14) {
                        discoverItemModel.itemAvatarTemp = new Random().nextInt(15);
                    } else {
                        discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                    }

                    discoverItemModel.isShowingCopy = isSelectedHistory;

                    ary_discoverDatas.add(discoverItemModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        discoverRecycleItemAdaptar.notifyDataSetChanged();
    }

    private void parsingPlaceResult(JSONObject jsonObject) {

        ary_discoverDatas.clear();

        JSONArray placeAry = jsonObject.optJSONArray("items");
        if (placeAry != null && placeAry.length() > 0) {

            for (int i = 0; i < placeAry.length(); i++) {

                try {

                    JSONObject placeObj = (JSONObject) placeAry.get(i);
                    if (placeObj == null) continue;

                    DiscoverItemModel discoverItemModel = new DiscoverItemModel();

                    discoverItemModel.itemType = Constants.DISCOVER_PLACE;
                    discoverItemModel.itemCategory = Constants.DISCOVER_PLACE;
                    discoverItemModel.itemTitle =  placeObj.optString("description");
                    discoverItemModel.itmeDetails = String.valueOf(placeObj.optInt("posted"));
                    discoverItemModel.itemID = placeObj.optInt("id");

                    discoverItemModel.itemAvatar = "";

                    discoverItemModel.placeModel = new PlaceModel();
                    discoverItemModel.placeModel.lng = placeObj.optString("lng");
                    discoverItemModel.placeModel.lat = placeObj.optString("lat");

                    if (discoverItemModel.itemTitle.length() > 14) {
                        discoverItemModel.itemAvatarTemp = new Random().nextInt(15);
                    } else {
                        discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                    }

                    discoverItemModel.isShowingCopy = isSelectedHistory;

                    ary_discoverDatas.add(discoverItemModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        discoverRecycleItemAdaptar.notifyDataSetChanged();
    }

    private void saveHistoryToSQL(DiscoverItemModel discoverItemModel, String search) {
        long id = db.insertHistory(discoverItemModel, search);
        if (id != 0) {
            // success
        }
    }

    private ArrayList<DiscoverItemModel> getAllHistoryItems() {

        ArrayList<DiscoverItemModel> arylist = new ArrayList<>();

        if (db.getHistoriesCount() == 0) return arylist;

        for (int i = 0; i < db.getHistoriesCount(); i++) {
            History history = db.getAllHistories().get(i);

            DiscoverItemModel discoverItemModel = new DiscoverItemModel();
            discoverItemModel.itemID = Integer.parseInt(history.getItemId());
            discoverItemModel.itemType = history.getType();
            discoverItemModel.itemCategory = history.getCategory();
            discoverItemModel.itemTitle = history.getTitle();
            discoverItemModel.itmeDetails = history.getDetail();
            discoverItemModel.itemAvatar = history.getAvatar();
            discoverItemModel.searchText = history.getSearch();

            if (discoverItemModel.itemType.equals(Constants.DISCOVER_TAG) || discoverItemModel.itemType.equals(Constants.DISCOVER_PLACE)) {

                if (discoverItemModel.itemTitle.length() > 14) {
                    discoverItemModel.itemAvatarTemp = new Random().nextInt(15);
                } else {
                    discoverItemModel.itemAvatarTemp = discoverItemModel.itemTitle.length();
                }
            }

            discoverItemModel.isShowingCopy = true;

            arylist.add(discoverItemModel);
        }

        return arylist;
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(mContext)) {

            switch (requesType) {
                case REQUEST_GET_POSTS:
                    parsingPostsArray(objResult);
                    break;
                case REQUEST_GET_TOP_DATA:
                    parsingTopData(objResult);
                    break;
                case REQUEST_GET_USER:
                    parsingPeopleResult(objResult);
                    break;
                case REQUEST_GET_TAG:
                    parsingTagResult(objResult);
                    break;
                case REQUEST_GET_PLACE:
                    parsingPlaceResult(objResult);
                    break;
            }

        } else {

            Constants.saveRefreshToken(mContext, objResult);

            switch (requesType) {

                case REQUEST_GET_POSTS:
                    getAllPosts(currentPage);
                    break;
                case REQUEST_GET_TOP_DATA:
                    getTopDatas(currentSearch);
                    break;
                case REQUEST_GET_USER:
                    getPeoples(currentSearch, String.valueOf(currentPage));
                    break;
                case REQUEST_GET_TAG:
                    getTags(currentSearch);
                    break;
                case REQUEST_GET_PLACE:
                    getPlaces(currentSearch);
                    break;
            }
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert((AppCompatActivity)mContext, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }
//        Utility.parseError((AppCompatActivity) mContext, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void LoadmoreDatas(int page) {
        getAllPosts(page);
    }

    @Override
    public void onClick(View view) {

        if (view == btn_cancel) {
            clearSearchList();
        } else if (view == img_history) {

            txt_search.setText("");

            if (isSelectedHistory) {

                isSelectedHistory = false;
                indexCategory = 1;

                changeActionStatus(true);

            } else {

                isSelectedHistory = true;
                indexCategory = 0;

                changeActionStatus(false);
                if (mTvRecyclerView.getVisibility() == View.VISIBLE) {
                    mTvRecyclerView.setVisibility(View.INVISIBLE);
                    linear_search_result.setVisibility(View.VISIBLE);
                }
            }

            onClickCategory(indexCategory);

        } else {

            isCalledLoadMore = true;

            if (view == linear_top) {
                indexCategory = 1;
            } else if (view == linear_people) {
                indexCategory = 2;
            } else if (view == linear_tags) {
                indexCategory = 3;
            } else if (view == linear_place) {
                indexCategory = 4;
            }
            onClickCategory(indexCategory);
        }

    }

    @Override
    public void onClickItem(int position) {

        if (isSelectedHistory) return;

        DiscoverItemModel discoverItemModel = ary_discoverDatas.get(position);
        String str_type = discoverItemModel.itemType;

        saveHistoryToSQL(discoverItemModel, txt_search.getText().toString());

        switch (str_type) {

            case Constants.DISCOVER_USER: {

                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.SELECT_USERNAME, discoverItemModel.itemTitle);
                intent.putExtra(UserProfileActivity.SELECT_USERID, discoverItemModel.itemID);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                break;
            }
            case Constants.DISCOVER_PLACE: {

                Intent intent = new Intent(mContext, DiscoverItemDetailActivity.class);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_ID, discoverItemModel.itemID);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, discoverItemModel.itemTitle);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, discoverItemModel.itmeDetails);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, discoverItemModel.itemType);
                intent.putExtra(DiscoverItemDetailActivity.PLACE_LAT, discoverItemModel.placeModel.lat);
                intent.putExtra(DiscoverItemDetailActivity.PLACE_LNG, discoverItemModel.placeModel.lng);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                break;
            }
            case Constants.DISCOVER_TAG: {

                Intent intent = new Intent(mContext, DiscoverItemDetailActivity.class);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_ID, discoverItemModel.itemID);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, discoverItemModel.itemTitle);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, discoverItemModel.itmeDetails);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, discoverItemModel.itemType);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                break;
            }
        }
    }

    @Override
    public void CustomLayoutItemOnClick(int id) {

        int position = -1;

        for (int i = 0; i < ary_postsData.size(); i++) {
            PostsModel postsModel = ary_postsData.get(i);
            if (id == postsModel.id) position = i;
        }

        if (position != -1) {

            PostsModel postsModel = ary_postsData.get(position);
            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
            Utility.saveImpressionToSql(mContext, impressionModel);

            Intent intent = new Intent(mContext, UsersActivity.class);

            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_DISCOVER_HOME);
            intent.putExtra(UsersActivity.IS_SCROLL, true);
            intent.putExtra(UsersActivity.SCROLL_POSITION, position);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }

    }

    @Override
    public void onClickCopyAction(int position) {

        isSelectedHistory = false;

        DiscoverItemModel discoverItemModel = ary_discoverDatas.get(position);

        txt_search.setText(discoverItemModel.searchText);
        changeActionStatus(true);

        if (discoverItemModel.itemCategory.equals(Constants.DISCOVER_TOP)) {
            indexCategory = 1;
        } else if (discoverItemModel.itemCategory.equals(Constants.DISCOVER_USER)) {
            indexCategory = 2;
        } else if (discoverItemModel.itemCategory.equals(Constants.DISCOVER_TAG)) {
            indexCategory = 3;
        } else {
            indexCategory = 4;
        }

        onClickCategory(indexCategory);
    }
}