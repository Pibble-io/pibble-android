package com.star.pibbledev.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.abdularis.civ.AvatarImageView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.star.pibbledev.BaseFragment;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.dashboard.DashboardFragment;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.activity.PlayroomActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.SlidingAnimation;
import com.star.pibbledev.services.global.model.BrushRewardsModel;
import com.star.pibbledev.services.global.model.FriendsModel;
import com.star.pibbledev.services.global.model.GraphDataModel;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.global.model.InteractionStatusModel;
import com.star.pibbledev.services.global.model.LevelUpModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.services.global.model.UserStatsModel;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.network.BackendAPI;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.profile.photopicker.PickerActivity;
import com.star.pibbledev.profile.activity.CreateDescriptionActivity;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.profile.adaptar.ProfileFriendRequestAdaptar;
import com.star.pibbledev.profile.adaptar.ProfileFriendsListAdaptar;
import com.star.pibbledev.profile.adaptar.ProfilePostsGridAdaptar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileHomeFragment extends BaseFragment implements View.OnClickListener , RequestListener,
         ProfilePostsGridAdaptar.PostGridItemClickListener, ProfileFriendRequestAdaptar.ProfileFriendRequestListener, ProfileFriendsListAdaptar.ProfileFriendsListListener {

    public static final String IMAGE_COVER = "cover";
    public static final String IMAGE_USER = "user";

    public static final String PROFILE_USERNAME = "profile_username";
    public static final String PROFILE_description = "profile_description";

    private static final String REQUEST_GET_PROFILE = "getProfile";
    private static final String REQUEST_GET_USER_POSTS = "getPosts";
    private static final String REQUEST_GET_USER_BRASH_STATUS = "get_user_brash_status";

    private static final String REQUEST_FOLLOW = "selectFollow";
    public static final String REQUEST_POST_FOLLOW = "postFollow";
    public static final String SUCCESS_GET_USERS_UPVOTED = "success_get_users_upvoted";
    public static final String REQUEST_REFRESH = "getRefreshposts";
    public static final String REQUEST_GET_UPVOTE = "limitedUpvote";
    private static final String REQUEST_SEND_COMMENT = "sendingComment";
    public static final String REQUEST_SEND_FAVORITE = "selectFavorite";

    private static final String REQUEST_GET_FRIENDS = "allFriends";
    private static final String REQUEST_FRIENDSHIP = "sendFriendship";
    private static final String REQUEST_FRIENDSHIP_INBOUND = "sendFriendshipInbound";
    private static final String REQUEST_FRIENDSHIP_ACCEPT = "sendFriendshipAccept";
    private static final String REQUEST_FRIENDSHIP_DENY = "sendFriendshipDeny";

    public static final String TARGET_UPVOTE = "profile_post_upvote";

    public static final String TYPE_ACTION_EDIT = "edit";
    public static final String TYPE_ACTION_EDIT_TAG = "edit_tag";

    private String REQUEST_TYPE;

    private View view, view_subPopup;
    private Context mContext;

    private ImageView img_cover, img_aboutme, img_diamond;
    private AvatarImageView img_avatar;
    private TextView txt_userEmo, txt_pib, txt_prb, txt_pgb, txt_userstate, txt_level, txt_reward_title, txt_getpoints, txt_points, txt_points_progress, txt_name, txt_reward_prb, txt_reward_limit, txt_reward_progress;
    private TextView txt_followers, txt_won, txt_followings, txt_friends, txt_posts, txt_description, txt_following, txt_addfriend, txt_friendsCnt, txt_friends_requestCnt, txt_period, txt_website;
    private LinearLayout linear_addaction, linear_userbalances, linear_playroom, linear_details, linear_following, linear_addfriend, linear_friends_list, linear_friends_requests, linear_about_me, linear_graph, linear_graph_period;
    private LinearLayout btn_followers, btn_followings, btn_friends, btn_posts;
    private FrameLayout frame_points_progress, frame_reward_progress;
    private AppBarLayout mAppBarContainer;

    private GraphView mGraph;

    private ArrayList<GraphDataModel> ary_PGB;
    private ArrayList<GraphDataModel> ary_PRB;

    private RecyclerView recyclerview_collect, recyclerView_friends, recyclerview_friends_requests;
    private ProfilePostsGridAdaptar postsGridAdaptar;

    private ProfileFriendsListAdaptar profileFriendsListAdaptar;
    private ArrayList<FriendsModel> ary_friends;

    private ProfileFriendRequestAdaptar profileFriendRequestAdaptar;
    private ArrayList<FriendsModel> ary_friendRequests;

    private ImageView img_selected_grid, img_selected_favourit, img_selected_brush, img_selected_redbrush;

    private boolean isCalledRefreshToken, isDestroyView;

    private ImageLoader imageLoader;

    private String username, mServerURL, mPerod;
    private int mPosition;
    private int totalPagecount = 0, totalPages_friends;
    private int currentPage, currentPage_friend, currentPage_inbound;

    private String currentString;
    private int appBarLayoutOffset;

    private UserModel mUserModel;

    private boolean isSelectedProfile, isCalledGraph, isSelecteUpvotedPost;

    private ArrayList<PostsModel> ary_postsData;
    private ArrayList<Integer> ary_aniCommented = new ArrayList<>();
    public static ArrayList<PostsModel> ary_publicPostsData;

    private DashboardFragment dashboardFragment;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = (View) inflater.inflate(R.layout.fragment_profile_home, null);
        mContext = getActivity();

        dismissTouch(view.findViewById(R.id.layout_profile), getActivity());

        view.findViewById(R.id.layout_profile).setVisibility(View.GONE);

        img_cover = (ImageView)view.findViewById(R.id.img_cover);

        img_avatar = (AvatarImageView)view.findViewById(R.id.img_avatar);

        mAppBarContainer = (AppBarLayout)view.findViewById(R.id.mAppBarContainer);

        txt_userEmo = (TextView)view.findViewById(R.id.txt_userEmo);
        txt_pib = (TextView)view.findViewById(R.id.txt_pib);
        txt_prb = (TextView)view.findViewById(R.id.txt_prb);
        txt_pgb = (TextView)view.findViewById(R.id.txt_pgb);
        txt_userstate = (TextView)view.findViewById(R.id.txt_userstate);
        txt_level = (TextView)view.findViewById(R.id.txt_level);
        txt_reward_title = (TextView)view.findViewById(R.id.txt_reward_title);
        txt_getpoints = (TextView)view.findViewById(R.id.txt_getpoints);
        txt_points = (TextView)view.findViewById(R.id.txt_points);
        txt_points_progress = (TextView)view.findViewById(R.id.txt_points_progress);
        txt_name = (TextView)view.findViewById(R.id.txt_name);
        txt_won = (TextView)view.findViewById(R.id.txt_won);
        txt_reward_prb = (TextView)view.findViewById(R.id.txt_reward_prb);
        txt_reward_limit = (TextView)view.findViewById(R.id.txt_reward_limit);
        txt_reward_progress = (TextView)view.findViewById(R.id.txt_reward_progress);

        txt_followers = (TextView)view.findViewById(R.id.txt_followers);
        txt_followings = (TextView)view.findViewById(R.id.txt_followings);
        txt_friends = (TextView)view.findViewById(R.id.txt_friends);
        txt_posts = (TextView)view.findViewById(R.id.txt_posts);

        txt_website = (TextView)view.findViewById(R.id.txt_website);
        txt_website.setOnClickListener(this);

        txt_following = (TextView)view.findViewById(R.id.txt_following);
        txt_addfriend = (TextView)view.findViewById(R.id.txt_addfriend);
        txt_friendsCnt = (TextView)view.findViewById(R.id.txt_friendsCnt);
        txt_friendsCnt.setOnClickListener(this);
        txt_friends_requestCnt = (TextView)view.findViewById(R.id.txt_friends_requestCnt);

        txt_period = (TextView)view.findViewById(R.id.txt_period);
        txt_period.setText(mContext.getString(R.string.this_week));

        img_selected_grid = (ImageView)view.findViewById(R.id.img_selected_grid);
        img_selected_grid.setOnClickListener(this);
        img_selected_redbrush = (ImageView)view.findViewById(R.id.img_selected_redbrush);
        img_selected_redbrush.setOnClickListener(this);
        img_selected_favourit = (ImageView)view.findViewById(R.id.img_selected_favourit);
        img_selected_favourit.setOnClickListener(this);
        img_selected_brush = (ImageView)view.findViewById(R.id.img_selected_brush);
        img_selected_brush.setOnClickListener(this);

        txt_description = (TextView)view.findViewById(R.id.txt_description);

        frame_points_progress = (FrameLayout)view.findViewById(R.id.frame_points_progress);
        frame_reward_progress = (FrameLayout)view.findViewById(R.id.frame_reward_progress);

        linear_addaction = (LinearLayout)view.findViewById(R.id.linear_addaction);
        linear_addfriend = (LinearLayout)view.findViewById(R.id.linear_addfriend);
        linear_addfriend.setOnClickListener(this);
        linear_following = (LinearLayout)view.findViewById(R.id.linear_following);
        linear_following.setOnClickListener(this);

        linear_userbalances = (LinearLayout)view.findViewById(R.id.linear_userbalances);
        linear_playroom = (LinearLayout)view.findViewById(R.id.linear_playroom);
        linear_playroom.setOnClickListener(this);
        linear_details = (LinearLayout)view.findViewById(R.id.linear_details);

        linear_about_me = (LinearLayout)view.findViewById(R.id.linear_about_me);

        btn_followers = (LinearLayout)view.findViewById(R.id.btn_followers);
        btn_followers.setOnClickListener(this);
        btn_followings = (LinearLayout)view.findViewById(R.id.btn_followings);
        btn_followings.setOnClickListener(this);
        btn_friends = (LinearLayout)view.findViewById(R.id.btn_friends);
        btn_friends.setOnClickListener(this);
        btn_posts = (LinearLayout)view.findViewById(R.id.btn_posts);
        btn_posts.setOnClickListener(this);

        linear_graph = (LinearLayout)view.findViewById(R.id.linear_graph);
        linear_graph.setVisibility(View.GONE);

        linear_graph_period = (LinearLayout)view.findViewById(R.id.linear_graph_period);
        linear_graph_period.setOnClickListener(this);

        linear_friends_list = (LinearLayout)view.findViewById(R.id.linear_friends_list);
        linear_friends_requests = (LinearLayout)view.findViewById(R.id.linear_friends_requests);
        linear_friends_requests.setVisibility(View.GONE);

        img_aboutme = (ImageView)view.findViewById(R.id.img_aboutme);
        img_diamond = (ImageView)view.findViewById(R.id.img_diamond);

        mGraph = (GraphView)view.findViewById(R.id.mGraph);

        ary_postsData = new ArrayList<>();
        ary_publicPostsData = new ArrayList<>();

        username = getArguments().getString(PROFILE_USERNAME);

        boolean isCurrentUser = false;
        if (username != null) {
            if (username.equals(Utility.getReadPref(mContext).getStringValue("username"))) {
                username = null;
                isCurrentUser = true;
            }
        }

        if (username == null) {

//            linear_playroom.setVisibility(View.VISIBLE);

            isSelectedProfile = true;
            img_cover.setOnClickListener(this);
            img_avatar.setOnClickListener(this);
            linear_addaction.setVisibility(View.GONE);
            if (!isCurrentUser) linear_about_me.setOnClickListener(this);
            else img_aboutme.setVisibility(View.GONE);

            mServerURL = BackendAPI.get_user_posts;

        } else {

//            linear_playroom.setVisibility(View.GONE);

            isSelectedProfile = false;
            img_selected_favourit.setVisibility(View.GONE);
            img_aboutme.setVisibility(View.GONE);

            mServerURL = BackendAPI.get_selected_user_posts + username +"/posts";

        }

        isCalledRefreshToken = false;
        isDestroyView = false;
        isCalledGraph = false;

        mUserModel = new UserModel();

        currentPage = 1;
        currentPage_friend = 1;
        currentPage_inbound = 1;

        mPerod = "week";

        ary_friendRequests = new ArrayList<>();
        ary_friends = new ArrayList<>();

        ary_PGB = new ArrayList<>();
        ary_PRB = new ArrayList<>();

        getProfile();

        imageLoader = ImageLoader.getInstance();

        recyclerview_collect = (RecyclerView)view.findViewById(R.id.recyclerview_collect);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerview_collect.setLayoutManager(layoutManager);
        recyclerview_collect.setItemAnimator(new DefaultItemAnimator());
        recyclerview_collect.setHasFixedSize(false);
        recyclerview_collect.setItemViewCacheSize(20);
        recyclerview_collect.setDrawingCacheEnabled(true);
        recyclerview_collect.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        postsGridAdaptar = new ProfilePostsGridAdaptar(mContext, ary_postsData, imageLoader);
        postsGridAdaptar.setHasStableIds(true);
        postsGridAdaptar.setClickListener(this);
        recyclerview_collect.setAdapter(postsGridAdaptar);

        recyclerView_friends = (RecyclerView)view.findViewById(R.id.recyclerview_friends);
        GridLayoutManager layoutManager_friend = new GridLayoutManager(getContext(), 1);
        recyclerView_friends.setLayoutManager(layoutManager_friend);
        recyclerView_friends.setItemAnimator(new DefaultItemAnimator());
        recyclerView_friends.setHasFixedSize(true);
        recyclerView_friends.setItemViewCacheSize(20);
        recyclerView_friends.setDrawingCacheEnabled(true);
        recyclerView_friends.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        boolean usertype = false;
        if (username != null) usertype = true;

        profileFriendsListAdaptar = new ProfileFriendsListAdaptar(mContext, ary_friends, usertype);
        profileFriendsListAdaptar.setClickListener(this);
        recyclerView_friends.setAdapter(profileFriendsListAdaptar);

        if (username == null) {

            recyclerview_friends_requests = (RecyclerView)view.findViewById(R.id.recyclerview_friends_requests);
            GridLayoutManager layoutManager_friend_request = new GridLayoutManager(getContext(), 1);
            recyclerview_friends_requests.setLayoutManager(layoutManager_friend_request);
            recyclerview_friends_requests.setItemAnimator(new DefaultItemAnimator());
            recyclerview_friends_requests.setHasFixedSize(true);
            recyclerview_friends_requests.setItemViewCacheSize(20);
            recyclerview_friends_requests.setDrawingCacheEnabled(true);
            recyclerview_friends_requests.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            profileFriendRequestAdaptar = new ProfileFriendRequestAdaptar(mContext, ary_friendRequests);
            profileFriendRequestAdaptar.setClickListener(this);
            recyclerview_friends_requests.setAdapter(profileFriendRequestAdaptar);

        }

        if (getActivity().getClass().getSimpleName().equals(DashboardActivity.TAG)) {

            DashboardActivity dashboardActivity = (DashboardActivity)getActivity();
            dashboardFragment = ((DashboardFragment) dashboardActivity.getFragment(0));

        }

        recyclerview_collect.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerview_collect.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (isSelecteUpvotedPost) {
                    getUsersPosts(String.valueOf(page + 1), mServerURL, "desc");
                } else {
                    getUsersPosts(String.valueOf(page + 1), mServerURL, "");
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

        appBarLayoutOffset = 0;

        mAppBarContainer.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (appBarLayoutOffset - verticalOffset > 100) {

                    appBarLayoutOffset = verticalOffset;

                    if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(false);

                } else if (verticalOffset - appBarLayoutOffset > 100) {

                    appBarLayoutOffset = verticalOffset;

                    if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(true);

                }

            }
        });

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_selected);

        return view;
    }

    private void dismissTouch(View view, final Activity activity) {

        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Utility.dismissKeyboard(getActivity());
                    return false;
                }
            });

        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                dismissTouch(innerView, activity);
            }
        }
    }

    private void controllAppbarlayoutScrolling(boolean flag) {

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarContainer.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return flag;
                }
            });
        }

    }

    public void scrollToTop() {

        if (mAppBarContainer == null) return;
        mAppBarContainer.setExpanded(true);
        controllAppbarlayoutScrolling(true);
    }

    public void customDestroy() {

        isDestroyView = true;

        if (recyclerview_collect != null) recyclerview_collect.stopScroll();

        if (recyclerview_collect != null && recyclerview_collect.getAdapter() != null)
            recyclerview_collect.setAdapter(null);

        if (postsGridAdaptar != null) postsGridAdaptar = null;

        imageLoader = null;
//        imageLoader.clearDiskCache();
//        imageLoader.clearMemoryCache();
    }

    @Override
    public void onResume() {

        super.onResume();

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if (!(fragment instanceof ProfileFragment) && !(fragment instanceof ProfileHomeFragment)) return;

        if (fragment instanceof ProfileFragment) Utility.g_indexDashboardTab = 3;

        ary_publicPostsData.clear();
        ary_publicPostsData.addAll(ary_postsData);

        if (username == null && Utility.g_userInfoChanged) {

            Utility.g_userInfoChanged = false;

            if (!Utility.getReadPref(mContext).getStringValue("wall_cover").equals("null")) {
                Glide.with(mContext).load(Utility.getReadPref(mContext).getStringValue("wall_cover")).into(img_cover);
            }

            if (!Utility.getReadPref(mContext).getStringValue("avatar").equals("null")) {
                Glide.with(mContext).load(Utility.getReadPref(mContext).getStringValue("avatar")).into(img_avatar);
                img_avatar.setState(AvatarImageView.SHOW_IMAGE);
                txt_userEmo.setVisibility(View.INVISIBLE);
            }
        }

        long currentTime = System.currentTimeMillis() / 1000;

        String expires_in = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_EXPIRED_TIME);

        if (expires_in.equals("null") || expires_in.equals("")) expires_in = "0";

        if (Constants.isNetwork(mContext)) {

            if (Long.parseLong(expires_in) > currentTime) {
                isCalledRefreshToken = false;

            } else {

                isCalledRefreshToken = true;
                String refresh_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_REFRESH_TOKEN);
                ServerRequest.getSharedServerRequest().refreshToken(mContext, this, refresh_token);

            }

        } else {
            Utility.isNetworkoffline((AppCompatActivity) mContext);
        }

        if (Utility.g_isChanged) {

            Utility.g_isChanged = false;

            getProfile();
        }

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

    }

    private void getProfile() {

        REQUEST_TYPE = REQUEST_GET_PROFILE;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            if (isSelectedProfile) {
                ServerRequest.getSharedServerRequest().getProfile(this, mContext, access_token);
            } else {
                ServerRequest.getSharedServerRequest().getUserInfoFromUsername(this, mContext, access_token, username);
            }

        } else {
            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;
        }

    }

    private void getGraphData(String username, String period) {

        REQUEST_TYPE = REQUEST_GET_USER_BRASH_STATUS;

        currentString = username;
        mPerod = period;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserBrashStatus(this, mContext, access_token, username, period);

        } else {
            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;
        }

    }

    private void getFriends(String username, String page) {

        REQUEST_TYPE = REQUEST_GET_FRIENDS;

        currentString = username;
        currentPage = Integer.parseInt(page);

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserFriends(this, mContext, access_token, username, page, "15");

        } else {

            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;

        }

    }

    private void getFriendshipInbound(String page) {

        REQUEST_TYPE = REQUEST_FRIENDSHIP_INBOUND;

        currentPage = Integer.parseInt(page);

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getFriendshipInbound(this, mContext, access_token, page, "15");

        } else {

            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;

        }

    }

    private void getUsersPosts(String page, String serverURL, String order) {

        if (isDestroyView) return;

        REQUEST_TYPE = REQUEST_GET_USER_POSTS;

        currentPage = Integer.parseInt(page);

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserPosts(this, mContext, access_token, page, "30", order, serverURL);

        } else {

            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;

        }

    }

    private void initView() {

        if (!mUserModel.wall_cover.equals("null") && !mUserModel.wall_cover.equals("") && !mUserModel.is_banned) {
            Glide.with(mContext).load(mUserModel.wall_cover).into(img_cover);
        }

        if (!mUserModel.avatar.equals("null") && !mUserModel.avatar.equals("") && !mUserModel.is_banned) {

            Glide.with(mContext).load(mUserModel.avatar).into(img_avatar);
            img_avatar.setState(AvatarImageView.SHOW_IMAGE);
            txt_userEmo.setVisibility(View.INVISIBLE);

        } else {

            int value = 0;
            if (mUserModel.username.length() > 14) value = 14;
            else value = mUserModel.username.length();
            txt_userEmo.setVisibility(View.VISIBLE);
            img_avatar.setState(AvatarImageView.SHOW_INITIAL);
            img_avatar.setAvatarBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            txt_userEmo.setText(Utility.getUserEmoName(mUserModel.username));

        }

        if (mUserModel.is_banned) {

            linear_userbalances.setVisibility(View.INVISIBLE);
            linear_details.setVisibility(View.GONE);
            recyclerview_collect.setVisibility(View.GONE);
            img_diamond.setVisibility(View.GONE);

            img_avatar.setEnabled(false);
            img_cover.setEnabled(false);

            if (isSelectedProfile) {

                txt_userstate.setTextColor(mContext.getResources().getColor(R.color.red));
                txt_userstate.setText(R.string.account_blocked);

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_content);
                if (fragment instanceof ProfileFragment) ((ProfileFragment) fragment).changeMoreButton(true);

            } else {

                txt_userstate.setTextColor(mContext.getResources().getColor(R.color.line_background_color));
                txt_userstate.setText(R.string.account_locked);

                UserProfileActivity activity = (UserProfileActivity)getActivity();
                if (activity != null) activity.changeMoreButton(true);

            }

            return;

        } else {

            linear_userbalances.setVisibility(View.VISIBLE);
            linear_details.setVisibility(View.VISIBLE);
            recyclerview_collect.setVisibility(View.VISIBLE);
            img_diamond.setVisibility(View.VISIBLE);

            img_avatar.setEnabled(true);
            img_cover.setEnabled(true);

            Float val_rb = Float.parseFloat(mUserModel.available_PRB);
            Float val_gb = Float.parseFloat(mUserModel.available_PGB);
            @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%s R.B %d G.B %d", mUserModel.level, val_rb.intValue(), val_gb.intValue());

            txt_userstate.setText(str_level);
        }

        if (mUserModel.prizeAmount == 0) {
            img_diamond.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_diamond));
            txt_won.setVisibility(View.INVISIBLE);
        } else {
            img_diamond.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_diamond_gold));
            txt_won.setVisibility(View.VISIBLE);
            txt_won.setText(Utility.getConvertedValue(String.valueOf(mUserModel.prizeAmount), false));
        }

        if (username != null) {

            if (!mUserModel.interaction_status.is_following) {
                linear_following.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                txt_following.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                txt_following.setText(mContext.getString(R.string.follow));
            } else {
                linear_following.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                txt_following.setTextColor(mContext.getResources().getColor(R.color.black));
                txt_following.setText(mContext.getString(R.string.following));
            }

            if (mUserModel.interaction_status.is_friend) {
                linear_addfriend.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                txt_addfriend.setTextColor(mContext.getResources().getColor(R.color.black));
                txt_addfriend.setText(mContext.getString(R.string.friends));
            } else if (mUserModel.interaction_status.inbound_friend_requested) {
                linear_addfriend.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                txt_addfriend.setTextColor(mContext.getResources().getColor(R.color.black));
                txt_addfriend.setText(mContext.getString(R.string.received_friendship));
            } else if (mUserModel.interaction_status.outbound_friend_requested){
                linear_addfriend.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                txt_addfriend.setTextColor(mContext.getResources().getColor(R.color.black));
                txt_addfriend.setText(mContext.getString(R.string.friendship_requested));
            } else {
                linear_addfriend.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                txt_addfriend.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                txt_addfriend.setText(mContext.getString(R.string.add_friend));
            }

        }

        if (mUserModel.available_PIB == null) mUserModel.available_PIB = "0";
        if (mUserModel.available_PRB == null) mUserModel.available_PRB = "0";
        if (mUserModel.available_PGB == null) mUserModel.available_PGB = "0";

        txt_pib.setText(Utility.getConvertedValue(mUserModel.available_PIB, true));
        txt_prb.setText(Utility.getConvertedValue(mUserModel.available_PRB, false));
        txt_pgb.setText(Utility.getConvertedValue(mUserModel.available_PGB, false));

        txt_level.setText(String.format("%s (Lv.%s)", mContext.getString(R.string.level), mUserModel.level));
        txt_reward_title.setText(String.format("%s (24)", mContext.getString(R.string.profile_rewards)));

        if (mUserModel.first_name.equals("null") || mUserModel.last_name.equals("null") || mUserModel.first_name.equals("") || mUserModel.last_name.equals("")) {
            txt_name.setText(mUserModel.username);
        } else {
            String full_name = mUserModel.last_name + " " + mUserModel.first_name;
            txt_name.setText(full_name);
        }

        if (mUserModel.site_name.equals("null") || mUserModel.site_name.length() == 0) txt_website.setVisibility(View.GONE);
        else txt_website.setText(mUserModel.site_name);

        showProgressStatu(mUserModel.levelUpModel.available, mUserModel.levelUpModel.necessary, frame_points_progress);

        txt_points_progress.setText(String.format("%s%%", String.valueOf(getProgressValue(mUserModel.levelUpModel.available, mUserModel.levelUpModel.necessary))));

        txt_getpoints.setText(String.valueOf(mUserModel.levelUpModel.available));
        txt_points.setText(String.valueOf(mUserModel.levelUpModel.necessary));

        int reward_earn = mUserModel.brushRewardsModel.earn;
        int reward_limit = mUserModel.brushRewardsModel.limit;

        showProgressStatu(reward_earn, reward_limit, frame_reward_progress);

        txt_reward_prb.setText(String.valueOf(reward_earn));
        txt_reward_limit.setText(String.valueOf(reward_limit));

        txt_reward_progress.setText(String.format("%s%%", String.valueOf(getProgressValue(reward_earn, reward_limit))));

        txt_followers.setText(String.valueOf(mUserModel.stats.followers_count));
        txt_followings.setText(String.valueOf(mUserModel.stats.followings_count));
        txt_friends.setText(String.valueOf(mUserModel.stats.friends_count));
        txt_posts.setText(String.valueOf(mUserModel.stats.posts_count));

        if (!mUserModel.description.equals("null")) {
            txt_description.setText(Utility.getWithoutLine(mUserModel.description));
        } else {
            txt_description.setText("");
        }

    }

    public void showProgressStatu(int realVal, int maxVal, FrameLayout frameLayout) {

        float maxLenth = (Utility.g_deviceWidth - Utility.dpToPx(40)) * 0.45f;

        float realLenth = maxLenth * getProgressValue(realVal, maxVal) / 100;

        if (realLenth > maxLenth) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) realLenth, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(params);
        }


    }

    private void drwaingGraph() {

        mGraph.removeAllSeries();

        String[] ary_months_string = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] ary_week_string = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        int count = 0;

        if (mPerod.equals("week")) count = 7;
        else if (mPerod.equals("year")) count = 12;

        if (ary_PRB.size() == 0) {
            for (int i = 0; i < count; i++) {
                GraphDataModel graphDataModel = new GraphDataModel();
                graphDataModel.amount = 0;
                ary_PRB.add(graphDataModel);
            }
        }

        if (ary_PGB.size() == 0) {
            for (int i = 0; i < count; i++) {
                GraphDataModel graphDataModel = new GraphDataModel();
                graphDataModel.amount = 0;
                ary_PGB.add(graphDataModel);
            }
        }

        if (mPerod.equals("week")) {

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(1, ary_PRB.get(0).amount),
                    new DataPoint(2, ary_PRB.get(1).amount),
                    new DataPoint(3, ary_PRB.get(2).amount),
                    new DataPoint(4, ary_PRB.get(3).amount),
                    new DataPoint(5, ary_PRB.get(4).amount),
                    new DataPoint(6, ary_PRB.get(5).amount),
                    new DataPoint(7, ary_PRB.get(6).amount)
            });

            series.setColor(mContext.getResources().getColor(R.color.colorWalletPink));
//        series.setDrawDataPoints(true);
//        series.setDataPointsRadius(6);
            series.setThickness(4);

            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(1, ary_PGB.get(0).amount),
                    new DataPoint(2, ary_PGB.get(1).amount),
                    new DataPoint(3, ary_PGB.get(2).amount),
                    new DataPoint(4, ary_PGB.get(3).amount),
                    new DataPoint(5, ary_PGB.get(4).amount),
                    new DataPoint(6, ary_PGB.get(5).amount),
                    new DataPoint(7, ary_PGB.get(6).amount)
            });

            series1.setColor(mContext.getResources().getColor(R.color.colorWalletGreen));
//        series1.setDrawDataPoints(true);
//        series1.setDataPointsRadius(6);
            series1.setThickness(4);

            mGraph.addSeries(series);
            mGraph.addSeries(series1);

        } else {

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(1, ary_PRB.get(0).amount),
                    new DataPoint(2, ary_PRB.get(1).amount),
                    new DataPoint(3, ary_PRB.get(2).amount),
                    new DataPoint(4, ary_PRB.get(3).amount),
                    new DataPoint(5, ary_PRB.get(4).amount),
                    new DataPoint(6, ary_PRB.get(5).amount),
                    new DataPoint(7, ary_PRB.get(6).amount),
                    new DataPoint(8, ary_PRB.get(7).amount),
                    new DataPoint(9, ary_PRB.get(8).amount),
                    new DataPoint(10, ary_PRB.get(9).amount),
                    new DataPoint(11, ary_PRB.get(10).amount),
                    new DataPoint(12, ary_PRB.get(11).amount)
            });

            series.setColor(mContext.getResources().getColor(R.color.colorWalletPink));
//        series.setDrawDataPoints(true);
//        series.setDataPointsRadius(6);
            series.setThickness(4);

            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(1, ary_PGB.get(0).amount),
                    new DataPoint(2, ary_PGB.get(1).amount),
                    new DataPoint(3, ary_PGB.get(2).amount),
                    new DataPoint(4, ary_PGB.get(3).amount),
                    new DataPoint(5, ary_PGB.get(4).amount),
                    new DataPoint(6, ary_PGB.get(5).amount),
                    new DataPoint(7, ary_PGB.get(6).amount),
                    new DataPoint(8, ary_PGB.get(7).amount),
                    new DataPoint(9, ary_PGB.get(8).amount),
                    new DataPoint(10, ary_PGB.get(9).amount),
                    new DataPoint(11, ary_PGB.get(10).amount),
                    new DataPoint(12, ary_PGB.get(11).amount)
            });

            series1.setColor(mContext.getResources().getColor(R.color.colorWalletGreen));
//        series1.setDrawDataPoints(true);
//        series1.setDataPointsRadius(6);
            series1.setThickness(4);

            mGraph.addSeries(series);
            mGraph.addSeries(series1);
        }

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mGraph);

        if (mPerod.equals("week")) {
            staticLabelsFormatter.setHorizontalLabels(ary_week_string);
        } else {
            staticLabelsFormatter.setHorizontalLabels(ary_months_string);
        }

        mGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        mGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.LTGRAY);
        mGraph.getGridLabelRenderer().setHighlightZeroLines(true);
        mGraph.getGridLabelRenderer().setTextSize(25);
        mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.LTGRAY);

        if (!isCalledGraph) {
            Animation animation = new SlidingAnimation(linear_graph, 0, Utility.dpToPx(150));
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(300);
            linear_graph.setAnimation(animation);
            linear_graph.startAnimation(animation);
        }

        linear_graph.setVisibility(View.VISIBLE);

    }

    public int getProgressValue(int realVal, int maxVal) {
        return (int) (realVal * 100 / maxVal);
    }

    private void getProfiledata(JSONObject objResult) {

        try {

            mUserModel.id = objResult.optInt(Constants.ID);
            mUserModel.uuid = objResult.optString(Constants.UUID);
            mUserModel.username = objResult.optString(Constants.USERNAME);
            mUserModel.level = objResult.optInt(Constants.LEVEL);
            mUserModel.email = objResult.optString(Constants.EMAIL);
            mUserModel.avatar = objResult.optString(Constants.AVATAR);
            mUserModel.wall_cover = objResult.optString(Constants.WALL_COVER);
            mUserModel.description = objResult.optString(Constants.DESCRIPTION);
            mUserModel.first_name = objResult.optString(Constants.FIRST_NAME);
            mUserModel.last_name = objResult.optString(Constants.LAST_NAME);
            mUserModel.site_name = objResult.optString(Constants.SITE_NAME);
            mUserModel.is_banned = objResult.optBoolean(Constants.IS_BANNED);
            mUserModel.prizeAmount = objResult.optInt(Constants.PRIZE);

            JSONArray ary_balance = objResult.optJSONArray("balances");

            if (ary_balance != null && ary_balance.length() > 0) {

                for (int w = 0; w < ary_balance.length(); w++) {
                    JSONObject walObj = (JSONObject)ary_balance.get(w);
                    switch (walObj.optString("symbol")) {
                        case "ETH":
                            mUserModel.available_ETH = walObj.optString("available");
                            break;
                        case "PIB":
                            mUserModel.available_PIB = walObj.optString("available");
                            break;
                        case "PRB":
                            mUserModel.available_PRB = walObj.optString("available");
                            break;
                        case "PGB":
                            mUserModel.available_PGB = walObj.optString("available");
                            break;
                        case "BTC":
                            mUserModel.available_BTC = walObj.optString("available");
                            break;
                    }
                }
            }

            JSONObject obj_uppoints = objResult.optJSONObject("level_up_points");

            mUserModel.levelUpModel = new LevelUpModel();

            if (obj_uppoints != null) {
                mUserModel.levelUpModel.available = obj_uppoints.optInt("available");
                mUserModel.levelUpModel.necessary = obj_uppoints.optInt("necessary");
            }

            JSONObject obj_reward = objResult.optJSONObject("brush_rewards");

            if (obj_reward != null) {
                mUserModel.brushRewardsModel = new BrushRewardsModel();

                mUserModel.brushRewardsModel.earn = obj_reward.optInt("earn");
                mUserModel.brushRewardsModel.limit = obj_reward.optInt("limit");
            }

            JSONObject obj_stats = objResult.optJSONObject("stats");

            int cnt_followers = 0;
            int cnt_followings = 0;
            int cnt_friends = 0;
            int cnt_posts = 0;

            if (obj_stats != null) {

                cnt_followers = obj_stats.optInt("followers_count");
                cnt_followings = obj_stats.optInt("followings_count");
                cnt_friends = obj_stats.optInt("friends_count");
                cnt_posts = obj_stats.optInt("posts_count");

                mUserModel.stats = new UserStatsModel();

                mUserModel.stats.followers_count = cnt_followers;
                mUserModel.stats.followings_count = cnt_followings;
                mUserModel.stats.friends_count = cnt_friends;
                mUserModel.stats.posts_count = cnt_posts;
            }

            JSONObject obj_interaction = objResult.optJSONObject("interaction_status");

            if (obj_interaction != null) {
                mUserModel.interaction_status = new InteractionStatusModel();
                mUserModel.interaction_status.is_follower = obj_interaction.optBoolean("is_follower");
                mUserModel.interaction_status.is_following = obj_interaction.optBoolean("is_following");
                mUserModel.interaction_status.is_friend = obj_interaction.optBoolean("is_friend");
                mUserModel.interaction_status.inbound_friend_requested = obj_interaction.optBoolean("inbound_friend_requested");
                mUserModel.interaction_status.outbound_friend_requested = obj_interaction.optBoolean("outbound_friend_requested");
            }

            if (isSelectedProfile) {

                Utility.getSavedPref(mContext).saveInt(Constants.ID, objResult.optInt(Constants.ID));
                Utility.getSavedPref(mContext).saveString(Constants.UUID, objResult.optString(Constants.UUID));
                Utility.getSavedPref(mContext).saveString(Constants.USERNAME, objResult.optString(Constants.USERNAME));
                Utility.getSavedPref(mContext).saveString(Constants.LEVEL, String.valueOf(objResult.optInt(Constants.LEVEL)));
                Utility.getSavedPref(mContext).saveString(Constants.FIRST_NAME, objResult.optString(Constants.FIRST_NAME));
                Utility.getSavedPref(mContext).saveString(Constants.LAST_NAME, objResult.optString(Constants.LAST_NAME));
                Utility.getSavedPref(mContext).saveString(Constants.SITE_NAME, objResult.optString(Constants.SITE_NAME));
                Utility.getSavedPref(mContext).saveString(Constants.REFERRAL, objResult.optString(Constants.REFERRAL));
                Utility.getSavedPref(mContext).saveString(Constants.AVATAR, objResult.optString(Constants.AVATAR));
                Utility.getSavedPref(mContext).saveString(Constants.EMAIL, objResult.optString(Constants.EMAIL));
                Utility.getSavedPref(mContext).saveString(Constants.PHONE, objResult.optString(Constants.PHONE));
                Utility.getSavedPref(mContext).saveString(Constants.WALL_COVER, objResult.optString(Constants.WALL_COVER));
                Utility.getSavedPref(mContext).saveString(Constants.DESCRIPTION, objResult.optString(Constants.DESCRIPTION));

                JSONObject obj_country = objResult.optJSONObject("country");
                if (obj_country != null) {
                    Utility.getSavedPref(mContext).saveInt("country_id", obj_country.optInt("id"));
                    Utility.getSavedPref(mContext).saveString("phonecode", obj_country.optString("phonecode"));
                }

                JSONArray ary_wallets = objResult.optJSONArray("wallets");

                if (ary_wallets != null && ary_wallets.length() > 0) {

                    for (int w = 0; w < ary_wallets.length(); w++) {
                        JSONObject walObj = (JSONObject)ary_wallets.get(w);
                        switch (walObj.optString("symbol")) {

                            case "ETH":
                                Utility.getSavedPref(mContext).saveString("address_ETH", walObj.optString("address"));
                                break;
                            case "PIB":
                                Utility.getSavedPref(mContext).saveString("address_PIB", walObj.optString("address"));
                                break;
                            case "BTC":
                                Utility.getSavedPref(mContext).saveString("address_BTC", walObj.optString("address"));
                                break;
                            case "KLAY":
                                Utility.getSavedPref(mContext).saveString("address_KLAY", walObj.optString("address"));
                                break;
                            case "PIBK":
                                Utility.getSavedPref(mContext).saveString("address_PIBK", walObj.optString("address"));
                                break;

                        }
                    }

                }

                if (ary_balance != null && ary_balance.length() > 0) {

                    String str_eth = "";
                    String str_pib = "";
                    String str_prb = "";
                    String str_pgb = "";
                    String str_btc = "";

                    for (int w = 0; w < ary_balance.length(); w++) {
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
                        }
                    }

                    mUserModel.available_PIB = str_pib;
                    mUserModel.available_PRB = str_prb;
                    mUserModel.available_PGB = str_pgb;

                    Utility.getSavedPref(mContext).saveString("value_eth", str_eth);
                    Utility.getSavedPref(mContext).saveString("value_pib", str_pib);
                    Utility.getSavedPref(mContext).saveString("value_prb", str_prb);
                    Utility.getSavedPref(mContext).saveString("value_pgb", str_pgb);
                    Utility.getSavedPref(mContext).saveString("value_btc", str_btc);
                }

                if (obj_uppoints != null) {
                    Utility.getSavedPref(mContext).saveInt("level_available", obj_uppoints.optInt("available"));
                    Utility.getSavedPref(mContext).saveInt("level_necessary", obj_uppoints.optInt("necessary"));
                }

                if (obj_reward != null) {
                    Utility.getSavedPref(mContext).saveInt("reward_earn", obj_reward.optInt("earn"));
                    Utility.getSavedPref(mContext).saveInt("reward_limit", obj_reward.optInt("limit"));
                }

                Utility.getSavedPref(mContext).saveInt("followers_count", cnt_followers);
                Utility.getSavedPref(mContext).saveInt("followings_count", cnt_followings);
                Utility.getSavedPref(mContext).saveInt("friends_count", cnt_friends);
                Utility.getSavedPref(mContext).saveInt("posts_count", cnt_posts);

                String str_pinValue;
                if (objResult.optBoolean("pin_code_presence")) str_pinValue = "1";
                else str_pinValue = "0";

                Utility.getSavedPref(mContext).saveString("pin_code_presence", str_pinValue);

//                objResult.put(Constants.AUTH_REFRESH_TOKEN, Utility.getReadPref(mContext).getStringValue(Constants.AUTH_REFRESH_TOKEN));
//                Utility.writeToFile(objResult.toString());

            }

            initView();

            view.findViewById(R.id.layout_profile).setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFriendInfo(JSONObject jsonObject) {

        JSONArray items = jsonObject.optJSONArray("items");
        JSONObject pageObj = jsonObject.optJSONObject("pagination");

        if (pageObj == null) return;

        totalPages_friends = pageObj.optInt("pages");

        int totalCnt = pageObj.optInt("total");
        if (totalCnt == 0) {
            linear_friends_list.setVisibility(View.GONE);
        } else {
            txt_friendsCnt.setText(String.format("%s (%s)", mContext.getString(R.string.friends), String.valueOf(totalCnt)));

            if (totalCnt < 3) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utility.dpToPx(50 + 50 * totalCnt));
                linear_friends_list.setLayoutParams(layoutParams);

            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utility.dpToPx(200));
                linear_friends_list.setLayoutParams(layoutParams);
            }

        }

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

                    ary_friends.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            profileFriendsListAdaptar.notifyDataSetChanged();

        }
    }

    private void parseFriendshipInbound(JSONObject jsonObject) {

        JSONArray items = jsonObject.optJSONArray("items");
        JSONObject pageObj = jsonObject.optJSONObject("pagination");

        if (pageObj == null) return;

        int totalPages = pageObj.optInt("pages");

        int totalCnt = pageObj.optInt("total");
        if (totalCnt == 0) {
            linear_friends_requests.setVisibility(View.GONE);
        } else {
            linear_friends_requests.setVisibility(View.VISIBLE);
            txt_friends_requestCnt.setText(String.format("%s %s", String.valueOf(totalCnt), mContext.getString(R.string.profile_friends_request_arrived)));

            if (totalCnt < 3) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utility.dpToPx(50 + 50 * totalCnt));
                linear_friends_requests.setLayoutParams(layoutParams);

            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utility.dpToPx(200));
                linear_friends_requests.setLayoutParams(layoutParams);
            }

        }

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

                    ary_friendRequests.add(friendsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            profileFriendRequestAdaptar.notifyDataSetChanged();

        }

    }

    private void getMonthData(JSONArray jsonArray, ArrayList<GraphDataModel> arrayList) {

        arrayList.clear();

        int sum = 0;

        String[] ary_months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] ary_months_string = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String currentYear = (String) DateFormat.format(Constants.DATE_YEAR, Calendar.getInstance().getTime());

        int count_day = 0;
        int num_day = 0;

        for (int n = 0; n < 12; n++) {

            if (jsonArray != null && jsonArray.length() > 0) {

                String selected_month = "";

                for (int i = num_day; i < jsonArray.length(); i++) {

                    try {

                        JSONObject obj_pgb = (JSONObject)jsonArray.get(i);

                        String year = Utility.getTypeFromDate(obj_pgb.optString("date"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Constants.DATE_YEAR);

                        if (Integer.parseInt(year) != Integer.parseInt(currentYear)) continue;

                        GraphDataModel graphDataModel = new GraphDataModel();

                        int amount = (int) Double.parseDouble(obj_pgb.optString("amount"));
                        String month_name = Utility.getTypeFromDate(obj_pgb.optString("date"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Constants.DATE_MONTH_NUMBER);

                        if (month_name.equals(ary_months[n])) {

                            count_day++;
                            sum = sum + amount;
                            selected_month = ary_months_string[n];

                            if (i == jsonArray.length() - 1) {

                                graphDataModel.amount = sum;
                                graphDataModel.date = selected_month;
                                arrayList.add(graphDataModel);

                                selected_month = "";
                                count_day = 0;
                                num_day = 0;
                            }

                        } else {

                            if (selected_month.length() > 0) {

                                graphDataModel.amount = sum;
                                graphDataModel.date = selected_month;
                                arrayList.add(graphDataModel);
                                selected_month = "";
                                sum = 0;

                            } else {

                                sum = 0;
                                graphDataModel.amount = sum;
                                graphDataModel.date = ary_months_string[n];
                                arrayList.add(graphDataModel);

                            }

                            num_day = count_day;

                            break;

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                GraphDataModel graphDataModel = new GraphDataModel();
                graphDataModel.amount = 0;
                graphDataModel.date = ary_months_string[n];
                arrayList.add(graphDataModel);
            }

        }

    }

    private void getWeekData(JSONArray jsonArray, ArrayList<GraphDataModel> arrayList) {

        arrayList.clear();

        int sum = 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String mondayDate = (String) DateFormat.format(Constants.DATE_DATE, c.getTime());
        String mondayMonth = (String) DateFormat.format(Constants.DATE_MONTH_NUMBER, c.getTime());

        String[] ary_week = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] ary_week_ko = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

        int count_day = 0;
        int num_day = 0;

        for (int n = 0; n < 7; n++) {

            if (jsonArray != null && jsonArray.length() > 0) {

                String selected_week = "";

                for (int i = num_day; i < jsonArray.length(); i++) {

                    try {

                        JSONObject obj_pgb = (JSONObject)jsonArray.get(i);


                        GraphDataModel graphDataModel = new GraphDataModel();

                        int amount = (int) Double.parseDouble(obj_pgb.optString("amount"));
                        String week_name = Utility.getTypeFromDate(obj_pgb.optString("date"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Constants.DATE_WEEK);

                        String currentDate = Utility.getTypeFromDate(obj_pgb.optString("date"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Constants.DATE_DATE);
                        String currentMonth = Utility.getTypeFromDate(obj_pgb.optString("date"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Constants.DATE_MONTH_NUMBER);

//                        if (currentMonth.equals(mondayMonth)) {
//                            if (Integer.parseInt(Utility.getStringWithoutFirstZero(mondayDate)) > Integer.parseInt(Utility.getStringWithoutFirstZero(currentDate))) continue;
//                        }

                        if (week_name.equals(ary_week[n]) || week_name.equals(ary_week_ko[n])) {

                            count_day++;
                            sum = sum + amount;
                            selected_week = ary_week[n];

                            if (i == jsonArray.length() - 1) {

                                graphDataModel.amount = sum;
                                graphDataModel.date = ary_week[n];;
                                arrayList.add(graphDataModel);

                                selected_week = "";
                                count_day = 0;
                                num_day = 0;
                            }

                        } else {

                            if (selected_week.length() > 0) {

                                graphDataModel.amount = sum;
                                graphDataModel.date = selected_week;
                                arrayList.add(graphDataModel);
                                selected_week = "";
                                sum = 0;

                            } else {

                                sum = 0;
                                graphDataModel.amount = sum;
                                graphDataModel.date = ary_week[n];
                                arrayList.add(graphDataModel);

                            }
                            num_day = count_day;

                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                GraphDataModel graphDataModel = new GraphDataModel();
                graphDataModel.amount = 0;
                graphDataModel.date = ary_week[n];
                arrayList.add(graphDataModel);
            }
        }
    }

    private void getGraphDataParse(JSONObject jsonObject) {

        JSONArray jsonArray_pgb = jsonObject.optJSONArray("PGB");
        JSONArray jsonArray_prb = jsonObject.optJSONArray("PRB");

        if (mPerod.equals("week")) {
            getWeekData(jsonArray_pgb, ary_PGB);
            getWeekData(jsonArray_prb, ary_PRB);
        } else {
            getMonthData(jsonArray_pgb, ary_PGB);
            getMonthData(jsonArray_prb, ary_PRB);
        }

        drwaingGraph();
    }

    private void gettingPostsArray(JSONObject objResult) {

        if (isDestroyView) return;

        int cnt_all = ary_postsData.size();

        try {

            JSONArray posts = objResult.optJSONArray("items");

            JSONObject pageObj = objResult.optJSONObject("pagination");
            totalPagecount = pageObj.optInt("pages");

            if (posts == null || posts.length() == 0) return;

            int aryCnt = ary_postsData.size();

            for (int i = 0; i < posts.length(); i++) {

                JSONObject objPost = (JSONObject) posts.get(i);

                boolean isLoaded = false;
                for (int cnt = 0; cnt < cnt_all; cnt++) {
                    PostsModel postsModel = ary_postsData.get(cnt);
                    if (postsModel.id == objPost.optInt(Constants.ID)) {
                        isLoaded = true;
                    }
                }

                if (isLoaded) continue;

                final PostsModel postsModel = ParseUtility.postModel(objPost);

                if (postsModel.ary_media != null && postsModel.ary_media.size() > 0) {

                    if (imageLoader != null) {
                        imageLoader.loadImage(postsModel.ary_media.get(0).thumbnail, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }
                        });
                    }

                    for (int j = 0; j < postsModel.ary_media.size(); j++) {

                        final int finalJ = j;

                        if (finalJ == postsModel.ary_media.size() - 1) {
                            ary_postsData.add(postsModel);
                            ary_publicPostsData.add(postsModel);
                            ary_aniCommented.add(0);

                            //------ for comment animaiton-----

                            for (int k = 0; k < ary_postsData.size();k++) {
                                PostsModel temp = ary_postsData.get(k);
                                if (ary_aniCommented.get(k) == 1) {
                                    temp.isAnimated = true;
                                    ary_postsData.set(k, temp);
                                    ary_publicPostsData.set(k, temp);
                                }
                            }
                        }
                    }
                }
            }

            if (postsGridAdaptar != null) postsGridAdaptar.notifyItemRangeInserted(aryCnt, ary_postsData.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClickGrid() {

        ary_postsData.clear();
        ary_publicPostsData.clear();
        ary_aniCommented.clear();
        currentPage = 1;
        isSelecteUpvotedPost = false;

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

        if (isSelectedProfile) {
            mServerURL = BackendAPI.get_user_posts;
        } else {
            mServerURL = BackendAPI.get_selected_user_posts + username +"/posts";
        }

        getUsersPosts(String.valueOf(currentPage), mServerURL, "");

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_selected);
        img_selected_redbrush.setImageResource(R.drawable.icon_profile_redbrush_select);
        img_selected_favourit.setImageResource(R.drawable.icon_profile_favourit_unselect);
        img_selected_brush.setImageResource(R.drawable.icon_profile_greenbrush_select);

    }

    private void onClickFavourite(){

        ary_postsData.clear();
        ary_publicPostsData.clear();
        ary_aniCommented.clear();
        currentPage = 1;
        isSelecteUpvotedPost = false;

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

        mServerURL = BackendAPI.get_user_favourit_posts;

        getUsersPosts(String.valueOf(currentPage), mServerURL, "");

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_unselected);
        img_selected_redbrush.setImageResource(R.drawable.icon_profile_redbrush_select);
        img_selected_favourit.setImageResource(R.drawable.icon_profile_favourit_select);
        img_selected_brush.setImageResource(R.drawable.icon_profile_greenbrush_select);

    }

    private void onClickBrush(){

        ary_postsData.clear();
        ary_publicPostsData.clear();
        ary_aniCommented.clear();
        currentPage = 1;
        isSelecteUpvotedPost = true;

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

        if (isSelectedProfile) {
            mServerURL = BackendAPI.get_user_upvoted_posts;
        } else {
            mServerURL = BackendAPI.get_selected_user_posts + username +"/up-voted-posts";
        }

        getUsersPosts(String.valueOf(currentPage), mServerURL, "desc");

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_unselected);
        img_selected_redbrush.setImageResource(R.drawable.icon_profile_redbrush_select);
        img_selected_favourit.setImageResource(R.drawable.icon_profile_favourit_unselect);
        img_selected_brush.setImageResource(R.drawable.icon_profile_greenbrush_unselect);

    }

    private void onClickRedBrush() {

        ary_postsData.clear();
        ary_publicPostsData.clear();
        ary_aniCommented.clear();
        currentPage = 1;
        isSelecteUpvotedPost = true;

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

        if (isSelectedProfile) {
            mServerURL = BackendAPI.get_user_posts_upvoted("me");
        } else {
            mServerURL = BackendAPI.get_user_posts_upvoted(username);
        }

        getUsersPosts(String.valueOf(currentPage), mServerURL, "desc");

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_unselected);
        img_selected_redbrush.setImageResource(R.drawable.icon_profile_redbrush_unselect);
        img_selected_favourit.setImageResource(R.drawable.icon_profile_favourit_unselect);
        img_selected_brush.setImageResource(R.drawable.icon_profile_greenbrush_select);

    }

    private void onClickFollow() {

        showHUD();

        String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

        if (!mUserModel.interaction_status.is_following) {

            linear_following.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
            txt_following.setTextColor(mContext.getResources().getColor(R.color.black));
            txt_following.setText(mContext.getString(R.string.following));

            mUserModel.interaction_status.is_following = true;

            ServerRequest.getSharedServerRequest().selectFollow(this, mContext, username, access_token);

        } else {

            linear_following.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
            txt_following.setTextColor(mContext.getResources().getColor(R.color.colorMain));
            txt_following.setText(mContext.getString(R.string.follow));

            mUserModel.interaction_status.is_following = false;

            ServerRequest.getSharedServerRequest().selectUnFollow(this, mContext, username, access_token);

        }
    }

    private void requestDeny(int position) {

        showHUD();

        String username = ary_friendRequests.get(position).user_name;
        String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().sendFriendshipRequestAccept_Reject(this, mContext, token, BackendAPI.send_friendship_request_deny(username));

    }

    private void sendingComment(String string, int position) {

        PostsModel postsModel = ary_postsData.get(position);
        Utility.dismissKeyboard((Activity) mContext);
        String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().createComment(this, mContext, postsModel.id, string, access_token);

    }

    private void requestAccept(int position) {

        showHUD();

        String username = ary_friendRequests.get(position).user_name;
        String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().sendFriendshipRequestAccept_Reject(this, mContext, token, BackendAPI.send_friendship_request_accept(username));

    }

    @Override
    public void onClick(View view) {

        if (view == img_cover) {

            Intent intent = new Intent(mContext, PickerActivity.class);
            intent.putExtra(PickerActivity.IMAGE_TYPE, IMAGE_COVER);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (view == img_avatar) {

            Intent intent = new Intent(mContext, PickerActivity.class);
            intent.putExtra(PickerActivity.IMAGE_TYPE, IMAGE_USER);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (view == linear_playroom) {

            Intent intent = new Intent(getContext(), PlayroomActivity.class);

            if (username == null) {
                intent.putExtra(Constants.EMAIL, Utility.getReadPref(mContext).getStringValue(Constants.EMAIL));
            } else {
                intent.putExtra(Constants.EMAIL, mUserModel.email);
            }

            intent.putExtra(Constants.PLACE, 2);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_following) {

            REQUEST_TYPE = REQUEST_FOLLOW;

            if (Constants.isLifeToken(mContext)) {

                onClickFollow();

            } else {

                Constants.requestRefreshToken(mContext, this);
                isCalledRefreshToken = true;

            }

        } else if (view == linear_addfriend) {

            if (!mUserModel.interaction_status.is_friend && !mUserModel.interaction_status.inbound_friend_requested && !mUserModel.interaction_status.outbound_friend_requested) {

                showHUD();

                REQUEST_TYPE = REQUEST_FRIENDSHIP;

                if (Constants.isLifeToken(mContext)) {

                    String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                    ServerRequest.getSharedServerRequest().sendFriendshipRequest(this, mContext, access_token, username);

                } else {

                    Constants.requestRefreshToken(mContext, this);
                    isCalledRefreshToken = true;

                }

            }

        } else if (view == linear_about_me) {

            Intent intent = new Intent(getContext(), CreateDescriptionActivity.class);
            intent.putExtra(PROFILE_description, mUserModel.description);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == txt_website) {

            Utility.openBrowser(mContext, mUserModel.site_name);

        } else if (view == linear_graph_period) {

            isCalledGraph = true;

            if (txt_period.getText().equals(mContext.getString(R.string.this_week))) {

                txt_period.setText(R.string.this_year);
                mPerod = "year";

            } else {

                txt_period.setText(mContext.getString(R.string.this_week));
                mPerod = "week";
            }

            if (username == null) {
                username = "me";
            }

            getGraphData(username, mPerod);

        } else if (view == btn_followers) {

            Intent intent = new Intent(getContext(), UsersActivity.class);

            if (username != null) {
                intent.putExtra(UsersActivity.USER_NAME, username);
            } else {
                intent.putExtra(UsersActivity.USER_NAME, "me");
            }

            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_FOLLOWERS);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == btn_followings) {

            Intent intent = new Intent(getContext(), UsersActivity.class);

            if (username != null) {
                intent.putExtra(UsersActivity.USER_NAME, username);
            } else {
                intent.putExtra(UsersActivity.USER_NAME, "me");
            }

            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_FOLLOWINGS);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == btn_friends || view == txt_friendsCnt) {

            Intent intent = new Intent(getContext(), UsersActivity.class);

            if (username != null) {
                intent.putExtra(UsersActivity.USER_NAME, username);
            } else {
                intent.putExtra(UsersActivity.USER_NAME, "me");
            }

            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_FRIENDS);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == btn_posts) {

            Intent intent = new Intent(getContext(), UsersActivity.class);

            if (username != null) {
                intent.putExtra(UsersActivity.USER_NAME, username);
            } else {
                intent.putExtra(UsersActivity.USER_NAME, "me");
            }

            intent.putExtra(UsersActivity.SERVER_URL, mServerURL);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_POSTS);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }

        if (view == img_selected_grid) {

            onClickGrid();

        } else if (view == img_selected_favourit) {

            onClickFavourite();

        } else if (view == img_selected_brush) {

            onClickBrush();

        } else if (view == img_selected_redbrush) {

            onClickRedBrush();

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        hideHUD();

        if (isCalledRefreshToken) {

            Constants.saveRefreshToken(mContext, objResult);

            isCalledRefreshToken = false;

            switch (REQUEST_TYPE) {

                case REQUEST_GET_PROFILE:
                    getProfile();
                    break;
                case REQUEST_GET_USER_POSTS:

                    if (isSelecteUpvotedPost) {
                        getUsersPosts(String.valueOf(currentPage), mServerURL, "desc");
                    } else {
                        getUsersPosts(String.valueOf(currentPage), mServerURL, "");
                    }

                    break;
                case REQUEST_FOLLOW:
                    onClickFollow();
                    break;
                case REQUEST_FRIENDSHIP:
                    String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                    ServerRequest.getSharedServerRequest().sendFriendshipRequest(this, mContext, access_token, username);
                    break;
                case REQUEST_SEND_COMMENT:
                    sendingComment(currentString, mPosition);
                    break;
                case REQUEST_FRIENDSHIP_ACCEPT:
                    requestAccept(mPosition);
                    break;
                case REQUEST_FRIENDSHIP_DENY:
                    requestDeny(mPosition);
                    break;
                case REQUEST_GET_USER_BRASH_STATUS:
                    getGraphData(currentString, mPerod);
                    break;
                case REQUEST_GET_FRIENDS:
                    getFriends(currentString, String.valueOf(currentPage));
                    break;
                case REQUEST_FRIENDSHIP_INBOUND:
                    getFriendshipInbound(String.valueOf(currentPage));
                    break;
            }

        } else {

            switch (REQUEST_TYPE) {

                case REQUEST_GET_PROFILE:

                    getProfiledata(objResult);

                    if (objResult != null) {

                        boolean isBlocked = objResult.optBoolean(Constants.IS_BANNED);

                        if (!isBlocked) {

                            if (username != null) {
                                getFriends(username, "1");
                            } else {
                                getFriends("me", "1");
                            }
                        }
                    }

                    break;
                case REQUEST_GET_FRIENDS:

                    if (currentPage_friend == 1) {

                        currentPage_friend++;

                        if (username == null) {
                            getFriendshipInbound("1");
                        } else {
                            getGraphData(username, mPerod);
                        }

                        parseFriendInfo(objResult);
                    }

                    break;
                case REQUEST_FRIENDSHIP_INBOUND:

                    if (currentPage_inbound == 1) {

                        currentPage_inbound++;

                        getGraphData("me", mPerod);
                    }

                    parseFriendshipInbound(objResult);

                    break;
                case REQUEST_GET_USER_BRASH_STATUS:

                    getGraphDataParse(objResult);

                    if (!isCalledGraph) {

                        if (isSelecteUpvotedPost) {
                            getUsersPosts(String.valueOf("1"), mServerURL, "desc");
                        } else {
                            getUsersPosts(String.valueOf("1"), mServerURL, "");
                        }
                    }

                    break;
                case REQUEST_GET_USER_POSTS:

                    gettingPostsArray(objResult);

                    break;
                case REQUEST_FRIENDSHIP:

                    mUserModel.interaction_status.outbound_friend_requested = true;

                    linear_addfriend.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                    txt_addfriend.setTextColor(mContext.getResources().getColor(R.color.black));
                    txt_addfriend.setText(mContext.getString(R.string.friendship_requested));

                    break;

            }
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert((AppCompatActivity)mContext, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(mContext)) Utility.parseError((AppCompatActivity) mContext, strError);

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onGridItemClick(View view, int position) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
        Utility.saveImpressionToSql(mContext, impressionModel);

        Intent intent = new Intent(getContext(), UsersActivity.class);

        if (username != null) {
            intent.putExtra(UsersActivity.USER_NAME, username);
        } else {
            intent.putExtra(UsersActivity.USER_NAME, "me");
        }

        intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_POSTS);
        intent.putExtra(UsersActivity.IS_SCROLL, true);
        intent.putExtra(UsersActivity.SERVER_URL, mServerURL);
        intent.putExtra(UsersActivity.SCROLL_POSITION, position);

        startActivity(intent);
//        getActivity().finish();
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onFriendRequestClick(int position) {
        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, ary_friendRequests.get(position).user_name);
        intent.putExtra(UserProfileActivity.SELECT_USERID, ary_friendRequests.get(position).user_id);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onClickRequestAccept(int position) {

        REQUEST_TYPE = REQUEST_FRIENDSHIP_ACCEPT;

        mPosition = position;

        if (Constants.isLifeToken(mContext)) {

            requestAccept(position);

        } else {

            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;

        }

    }

    @Override
    public void onClickRequestDeny(int position) {

        REQUEST_TYPE = REQUEST_FRIENDSHIP_DENY;

        mPosition = position;

        if (Constants.isLifeToken(mContext)) {

            requestDeny(position);

        } else {

            Constants.requestRefreshToken(mContext, this);
            isCalledRefreshToken = true;

        }

    }

    @Override
    public void onClickFriend(int position) {
        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, ary_friends.get(position).user_name);
        intent.putExtra(UserProfileActivity.SELECT_USERID, ary_friends.get(position).user_id);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }
}
