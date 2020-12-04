package com.star.pibbledev.discover.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.auth.PhoneVerificationActivity;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.chatroom.activity.ChatRoomActivity;
import com.star.pibbledev.chatroom.activity.RoomsInGroupActivity;
import com.star.pibbledev.home.HomepageFragment;
import com.star.pibbledev.home.MainFeedListener;
import com.star.pibbledev.home.asking.AskingHelpListActivity;
import com.star.pibbledev.home.comments.CommentActivity;
import com.star.pibbledev.home.MainFeedParams;
import com.star.pibbledev.home.createmedia.tags.MainFeedTagsActivity;
import com.star.pibbledev.home.imagedetail.DisplayingImageActivity;
import com.star.pibbledev.home.editmedia.MediaCaptionEditActivity;
import com.star.pibbledev.home.report.ReportReasonSelectActivity;
import com.star.pibbledev.home.upvote.AddUpvoteActivity;
import com.star.pibbledev.home.upvote.UpvoteListener;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.ActionStatusDialog;
import com.star.pibbledev.services.global.customview.alertview.AlertHorizentalDialog;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.global.customview.alertview.UpvotedDoneDialog;
import com.star.pibbledev.services.global.customview.bottomsheetdialog.AskingHelpBottomSheetDialog;
import com.star.pibbledev.services.global.customview.bottomsheetdialog.FundingDetailBottomSheetDialog;
import com.star.pibbledev.services.global.customview.bottomsheetdialog.UpvotedUsersBottomSheetDialog;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.global.model.PlaceModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.services.global.model.UpvotedUsersModel;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.profile.adaptar.ProfilePostsGridAdaptar;
import com.star.pibbledev.profile.adaptar.ProfilePostsListAdaptar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DiscoverItemDetailActivity extends BaseActivity implements View.OnClickListener, RequestListener, ProfilePostsGridAdaptar.PostGridItemClickListener , MainFeedListener, OnMapReadyCallback , UpvoteListener {

    public static final String TAG = "DiscoverItemDetailActivity";

    public static final String ITEM_ID = "item_id";
    public static final String ITEM_POST_ID = "item_post_id";
    public static final String ITEM_TITLE = "item_title";
    public static final String ITEM_POSTS = "item_posts_count";
    public static final String ITEM_TYPE = "item_posts_type";
    public static final String PLACE_LAT = "lat";
    public static final String PLACE_LNG = "lng";

    public static final String REQUEST_GET_POST_TAG = "get_post_from_tag";
    public static final String REQUEST_GET_POST_TAG_TEXT = "get_post_from_tag_text";
    public static final String REQUEST_GET_POST_PLACE = "get_post_from_place";
    public static final String REQUEST_POST_FOLLOW = "post_follow_tag";

    ImageButton img_back;
    ImageView img_selected_grid, img_selected_list, img_user;
    TextView txt_title, txt_postscnt, txt_emo, txt_following;
    LinearLayout linear_tag, linear_map, linear_following, linear_related_scroll, linear_related;
    FrameLayout frame_map;
    RecyclerView recyclerview;
    RelativeLayout relative_upvoted_done;
    LinearLayout.LayoutParams mUpvotedDoneDialogParams;

    private int mitemID;
    private int mSelectPostID;
    private String mitemType;
    private String mitemTitle;
    private String requesType = "";
    private boolean isTapedGridView = true;
    private boolean isFollowing = false;
    private int currentPage;
    private int mPosition = 0;
    private boolean isImageLongClicked;

    ImageLoader imageLoader;

    ProfilePostsGridAdaptar postsGridAdaptar;
    ProfilePostsListAdaptar postsListAdaptar;

    ArrayList<PostsModel> ary_postsData;

    public static ArrayList<PostsModel> ary_publicPostsData;

    ArrayList<Integer> ary_aniCommented = new ArrayList<>();

    private PlaceModel mPlaceModel;

    ArrayList<UpvotedUsersModel> ary_usersUpvoted = new ArrayList<>();
    UpvotedUsersBottomSheetDialog upvotedUserPopupview;
    View view_subPopup;
    AlertView mAlertView;
    FundingDetailBottomSheetDialog fundingDetailBottomSheetDialog;
    AskingHelpBottomSheetDialog askingHelpBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_item_detail);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_selected_list = (ImageView)findViewById(R.id.img_selected_list);
        img_selected_list.setOnClickListener(this);
        img_selected_grid = (ImageView)findViewById(R.id.img_selected_grid);
        img_selected_grid.setOnClickListener(this);
        img_user = (ImageView)findViewById(R.id.img_user);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_postscnt = (TextView)findViewById(R.id.txt_postscnt);
        txt_emo = (TextView)findViewById(R.id.txt_emo);
        txt_following = (TextView)findViewById(R.id.txt_following);
        linear_tag = (LinearLayout)findViewById(R.id.linear_tag);
        linear_map = (LinearLayout)findViewById(R.id.linear_map);
        linear_following = (LinearLayout)findViewById(R.id.linear_following);
        linear_following.setOnClickListener(this);
        linear_related_scroll = (LinearLayout)findViewById(R.id.linear_related_scroll);
        linear_related = (LinearLayout)findViewById(R.id.linear_related);
        frame_map = (FrameLayout)findViewById(R.id.frame_map);
        frame_map.setOnClickListener(this);

        imageLoader = ImageLoader.getInstance();

        ary_postsData = new ArrayList<>();
        ary_publicPostsData = new ArrayList<>();

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(false);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mitemID = getIntent().getIntExtra(ITEM_ID, 0);
        mitemType = getIntent().getStringExtra(ITEM_TYPE);
        mitemTitle = getIntent().getStringExtra(ITEM_TITLE);
        mSelectPostID = getIntent().getIntExtra(ITEM_POST_ID, -1);

        if (mitemType.equals(Constants.DISCOVER_TAG)) {
            linear_map.setVisibility(View.GONE);
            linear_tag.setVisibility(View.GONE);
        } else if (mitemType.equals(Constants.DISCOVER_PLACE)) {
            linear_tag.setVisibility(View.GONE);
            linear_map.setVisibility(View.VISIBLE);
        } else if (mitemType.equals(Constants.DISCOVER_TAG_TEXT)) {
            linear_map.setVisibility(View.GONE);
            linear_tag.setVisibility(View.GONE);
        }

        onClickGrid();
    }

    private void loadData() {

        ary_postsData.clear();
        ary_aniCommented.clear();
        ary_publicPostsData.clear();

        if (mitemType.equals(Constants.DISCOVER_TAG)) {
            getTagDatas(mitemID, 1);
        } else if (mitemType.equals(Constants.DISCOVER_PLACE)) {
            getPlaceDatas(mitemID, 1);
        } else if (mitemType.equals(Constants.DISCOVER_TAG_TEXT)) {
            getTagDatasFromText(mitemTitle, 1);
        }

        recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerview.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (mitemType.equals(Constants.DISCOVER_TAG)) {
                    getTagDatas(mitemID, page + 1);
                } else if (mitemType.equals(Constants.DISCOVER_PLACE)) {
                    getPlaceDatas(mitemID, page + 1);
                } else if (mitemType.equals(Constants.DISCOVER_TAG_TEXT)) {
                    getTagDatasFromText(mitemTitle, page + 1);
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

    @Override
    protected void onResume() {
        super.onResume();

        isImageLongClicked = true;

        ary_publicPostsData.clear();
        ary_publicPostsData.addAll(ary_postsData);

        if (postsGridAdaptar != null) postsGridAdaptar.notifyDataSetChanged();

        if (askingHelpBottomSheetDialog != null) askingHelpBottomSheetDialog.dismiss();

        if (ary_postsData != null && ary_postsData.size() > mPosition) {
            getPostsFromID(ary_postsData.get(mPosition).id);
        }

        if (Utility.g_isChangedType != null && Utility.g_isChangedType.equals(Constants.REPORTED) && Utility.g_isChanged) {

            ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.reported));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    ary_postsData.remove(mPosition);
                    ary_aniCommented.remove(mPosition);
                    ary_publicPostsData.remove(mPosition);
                    if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();
                }
            }, 1300);

        } else if (Utility.g_isChangedType != null && Utility.g_isChangedType.equals(Constants.GOODS_ORDER_CREATED) && Utility.g_isChanged) {

            AlertVerticalDialog dialog = new AlertVerticalDialog(this, getString(R.string.thank_you),
                    getString(R.string.alert_payment_successfull),
                    null, getString(R.string.ok), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {

                    if (ary_postsData != null && ary_postsData.size() > mPosition) {
                        getPostsFromID(ary_postsData.get(mPosition).id);
                    }

                    dismiss();
                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            Utility.g_isChangedType = null;

        }

        if (Utility.g_upvotedUserReupvoteAmount != -1) {

            if (upvotedUserPopupview != null) upvotedUserPopupview.updateData(Utility.g_upvotedUserReupvoteAmount);

            Utility.g_upvotedUserReupvoteAmount = -1;

        }

        if (Utility.g_askingHelpRewardAmount != -1) {

            if (askingHelpBottomSheetDialog != null) askingHelpBottomSheetDialog.rewardSetData(Utility.g_askingHelpRewardAmount);
            Utility.g_askingHelpRewardAmount = -1;

        }
    }

    @Override
    public void onBackPressed() {

        if (mAlertView != null && mAlertView.isShowing()) mAlertView.dismiss();
        else super.onBackPressed();

    }

    private void initView(String str_tag, String cnt_posted) {

        if (mitemType.equals(Constants.DISCOVER_TAG) || mitemType.equals(Constants.DISCOVER_TAG_TEXT)) {

            txt_title.setText(String.format("#%s", Utility.truncate(str_tag, 20)));
            txt_postscnt.setText(cnt_posted);

            int value = 0;
            if (str_tag.length() > 14) value = 14;
            else value = str_tag.length();

            if (ary_postsData.size() == 0) {
                img_user.setImageDrawable(null);
                img_user.setBackgroundColor(this.getResources().getColor(Utility.g_aryColors[value]));
                txt_emo.setText(Utility.getUserEmoName(str_tag));
            }

        } else {
            txt_title.setText(Utility.truncate(str_tag, 20));

            mPlaceModel = new PlaceModel();
            mPlaceModel.lat = getIntent().getStringExtra(PLACE_LAT);
            mPlaceModel.lng = getIntent().getStringExtra(PLACE_LNG);
            mPlaceModel.description = str_tag;

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

    }

    private void getTagDatas(int tagID, int page) {

        currentPage = page;

        if (!Constants.isNetwork(this)) {
            Utility.isNetworkoffline((AppCompatActivity) this);
            return;
        }

        requesType = REQUEST_GET_POST_TAG;

        if (Constants.isLifeToken(this)) {
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPostsFromTag( this, this, access_token, String.valueOf(tagID), String.valueOf(page), "15");
        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void getTagDatasFromText(String tag, int page) {

        if (tag.length() == 0 || tag.equals(" ")) return;

        currentPage = page;

        if (!Constants.isNetwork(this)) {
            Utility.isNetworkoffline((AppCompatActivity) this);
            return;
        }

        requesType = REQUEST_GET_POST_TAG_TEXT;

        if (Constants.isLifeToken(this)) {
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPostsFromTagString( this, this, access_token, tag, String.valueOf(page), "15");
        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void getPlaceDatas(int placeID, int page) {

        currentPage = page;

        if (!Constants.isNetwork(this)) {
            Utility.isNetworkoffline((AppCompatActivity) this);
            return;
        }

        requesType = REQUEST_GET_POST_PLACE;

        if (Constants.isLifeToken(this)) {

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPostsFromPlace( this, this, access_token, String.valueOf(placeID), String.valueOf(page), "15");

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void getPostsFromID (int id) {

        requesType = Constants.REQUEST_GET_POST_FROM_ID;
        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getPostFromid(this, this, access_token, id);

    }

    private void sendUpvote(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        requesType = Constants.REQUEST_SEND_UPVOTE;

        if (Constants.isLifeToken(this)) {

            String balancesPGB = Utility.getReadPref(this).getStringValue("value_pgb");

            if (relative_upvoted_done != null && mUpvotedDoneDialogParams != null && balancesPGB != null && (int) Float.parseFloat(balancesPGB) > 10 && !postsModel.up_voted) {

                String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().sendingUpvote(this, this, postsModel.id, 10, token);

                int oldAmount = postsModel.up_votes_amount;
                postsModel.up_voted = true;
                postsModel.up_votes_amount = oldAmount + 10;

                ary_postsData.set(mPosition, postsModel);
                if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();

                UpvotedDoneDialog dialog = new UpvotedDoneDialog(this);
                dialog.setLayoutParams(mUpvotedDoneDialogParams);
                relative_upvoted_done.addView(dialog);

                Utility.scaleView(dialog, 0.0f, 0.6f, 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Utility.scaleView(dialog, 0.6f, 0.0f, 500);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                relative_upvoted_done.removeView(dialog);
                            }

                        }, 500);

                    }
                }, 800);
            }

        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void checkPhoneVerifyStatus() {

        AlertHorizentalDialog dialog = new AlertHorizentalDialog(this, null,
                getString(R.string.msg_not_verified_phone),
                getString(R.string.cancel), getString(R.string.ok), R.color.colorMain, R.color.colorMain) {

            @Override
            public void onClickButton(int position) {

                if (position == 1) {

                    Intent intent = new Intent(DiscoverItemDetailActivity.this, PhoneVerificationActivity.class);
                    intent.putExtra(PhoneVerificationActivity.ACTIVITY_TYPE, Constants.TYPE_CHECK_PHONEVERIFY);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                }

                dismiss();
            }

        };
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //---------user mute----------

    private void userMute(int position) {

        requesType = Constants.REQUEST_USER_MUTE;
        mPosition = position;

        if (Constants.isLifeToken(this)) {

            int userid = ary_postsData.get(position).user.id;

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().setting(this, this, token, userid, true);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void mutedUserPostRemove() {

        int userid = ary_postsData.get(mPosition).user.id;

        ActionStatusDialog dialog = new ActionStatusDialog(this, "Muted");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                int itemCount = ary_postsData.size();

                for (int i = itemCount - 1; i >=0; i--) {

                    if (ary_postsData.get(i).user.id == userid) {
                        ary_postsData.remove(i);
                        ary_publicPostsData.remove(i);
                        ary_aniCommented.remove(i);
                    }

                }

                if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();

            }
        }, 1000);
    }

    //----------delete media-----------

    private void deleteMedia() {

        requesType = Constants.REQUEST_DELETE_POST;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().deletingMedia(this, this, ary_postsData.get(mPosition).id, token);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    //--------block post----------

    private void blockFeed(int position) {

        PostsModel postsModel = ary_postsData.get(position);

        requesType = Constants.REQUEST_BLOCK_POST;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().blockFeed(this, this, token, postsModel.id);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void changedSelectedPost(JSONObject objPost) {

        if (objPost == null) return;

        final PostsModel postsModel = ParseUtility.postModel(objPost);

        if (ary_aniCommented.get(mPosition) == 1) postsModel.isAnimated = true;
        else postsModel.isAnimated = false;

        ary_postsData.set(mPosition, postsModel);

        if (postsListAdaptar != null) postsListAdaptar.notifyItemChanged(mPosition);

        if (Utility.g_isChangedFundingStatus && fundingDetailBottomSheetDialog != null) {

            fundingDetailBottomSheetDialog.setData(postsModel, false);

            Utility.g_isChangedFundingStatus = false;
        }
    }

    private void parsingTagResult(JSONObject jsonObject) {

        if (jsonObject != null) {

            JSONObject tagObj = jsonObject.optJSONObject("tag");

            String str_tag = tagObj.optString("tag");
            int cnt_posted = tagObj.optInt("posted");
            isFollowing = tagObj.optBoolean("isFollowing");
            mitemID = tagObj.optInt(Constants.ID);

            JSONArray relatedAry = tagObj.optJSONArray("related");

            if (relatedAry != null && relatedAry.length() > 0) {

                linear_related_scroll.removeAllViews();

                linear_related.setVisibility(View.VISIBLE);

                for (int i = 0; i < relatedAry.length(); i++) {

                    try {

                        JSONObject relatedObj = (JSONObject)relatedAry.get(i);

                        String tag_item = relatedObj.optString("tag");

                        linear_related_scroll.addView(createRelatedUI("#" + tag_item));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            } else {
                linear_related.setVisibility(View.GONE);
            }

            initView(str_tag, String.valueOf(cnt_posted));

            if (isFollowing) {
                linear_following.setBackground(getResources().getDrawable(R.drawable.linear_corner5_gray));
                txt_following.setText("#" + getString(R.string.following));
                txt_following.setTextColor(this.getResources().getColor(R.color.black));
            } else {
                linear_following.setBackground(getResources().getDrawable(R.drawable.btn_follow_background));
                txt_following.setText("#" + getString(R.string.follow));
                txt_following.setTextColor(this.getResources().getColor(R.color.white));
            }

            if (linear_tag.getVisibility() == View.GONE) {
                linear_tag.setVisibility(View.VISIBLE);
                Utility.scaleView(linear_tag, 0, 1, 300);
            }

            gettingPostsArray(jsonObject);

        }

    }

    private TextView createRelatedUI(String text) {

        TextView textView = new TextView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, Utility.dpToPx(10), 0);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(Utility.dpToPx(10), Utility.dpToPx(6), Utility.dpToPx(10), Utility.dpToPx(6));
        textView.setText(text);
        textView.setTextColor(this.getResources().getColor(R.color.gray));
        textView.setBackground(this.getResources().getDrawable(R.drawable.linear_corner5_gray));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscoverItemDetailActivity.this, DiscoverItemDetailActivity.class);
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, Utility.getStringWithoutFirstCharater(text));
                intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, "");
                intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, Constants.DISCOVER_TAG_TEXT);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }
        });

        return textView;
    }

    private void parsingPlaceResult(JSONObject jsonObject) {

        if (jsonObject != null) {

            JSONObject placeObj = jsonObject.optJSONObject("place");

            String str_description = placeObj.optString("description");
            String posted = placeObj.optString("posted");
            mitemID = placeObj.optInt(Constants.ID);

            initView(str_description, posted);

            gettingPostsArray(jsonObject);
        }
    }

    private void gettingPostsArray(JSONObject objResult) {

        try {

            JSONArray posts = objResult.optJSONArray("items");

            int aryCnt = ary_postsData.size();

            if (posts != null && posts.length() > 0) {

                for (int i = 0; i < posts.length(); i++) {

                    JSONObject objPost = (JSONObject) posts.get(i);

                    final PostsModel postsModel = ParseUtility.postModel(objPost);

                    postsModel.isAnimated = false;

                    if (postsModel.ary_media != null && postsModel.ary_media.size() > 0) {

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

                            if (!isTapedGridView) {

                                String img_url = "";

                                if (postsModel.ary_media.get(j).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                                    img_url = postsModel.ary_media.get(j).poster;
                                } else {
                                    img_url = postsModel.ary_media.get(j).url;
                                }

                                if (imageLoader != null) {

                                    imageLoader.loadImage(img_url, new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view, loadedImage);

                                        }
                                    });

                                }
                            }
                        }
                    }
                }

                if (isTapedGridView) {
                    if (postsGridAdaptar != null) postsGridAdaptar.notifyItemRangeInserted(aryCnt, ary_postsData.size());
                } else {
                    if (postsListAdaptar != null) postsListAdaptar.notifyItemRangeInserted(aryCnt, ary_postsData.size());
                }

                if (ary_postsData.size() > 0) {
                    if (imageLoader != null) imageLoader.displayImage(ary_postsData.get(0).ary_media.get(0).thumbnail, img_user);
                    txt_emo.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUsersUpvoted(JSONObject jsonObject) {

        ary_usersUpvoted.clear();

        if (jsonObject == null) return;

        JSONArray users = jsonObject.optJSONArray("items");
        JSONObject pageObj = jsonObject.optJSONObject("pagination");

        if (pageObj != null) {

            int total = pageObj.optInt("total");
            int pages = pageObj.optInt("pages");

            if (pages > 1) {

                int postid = ary_postsData.get(mPosition).id;

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().getUsersUpvoted(this, this, access_token, String.valueOf(postid),1, total);
                requesType = Constants.REQUEST_GET_USERS_UPVOTED;

                return;
            }
        }

        if (users != null && users.length() > 0) {

            try {

                for (int i = 0; i < users.length(); i++) {

                    UpvotedUsersModel upvotedUsersModel = new UpvotedUsersModel();
                    JSONObject userObj = users.getJSONObject(i);
                    upvotedUsersModel.amount = userObj.optInt("amount");
                    upvotedUsersModel.re_upvoteAmount = 0;

                    if (userObj.optJSONObject(Constants.USER) != null) {
                        upvotedUsersModel.userModel = ParseUtility.userModel(userObj.optJSONObject(Constants.USER));
                        ary_usersUpvoted.add(upvotedUsersModel);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ary_usersUpvoted.size() > 0) {

            boolean ismyPost = false;
            if (ary_postsData.get(mPosition).user.username.equals(Utility.getReadPref(this).getStringValue(Constants.USERNAME))) ismyPost = true;

            upvotedUserPopupview = UpvotedUsersBottomSheetDialog.getInstance(this);
            upvotedUserPopupview.setCanceledOnTouchOutside(true);
            upvotedUserPopupview.setData(ary_usersUpvoted, ismyPost);
            upvotedUserPopupview.show();
//            upvotedUserPopupview = new UpvotedUserPopupview(view_subPopup.getContext(), ary_usersUpvoted, this);
//            upvotedUserPopupview.setWidth(Utility.g_deviceWidth - Utility.dpToPx(40));
//            upvotedUserPopupview.setHeight(Utility.dpToPx(90));
//            upvotedUserPopupview.showOnAnchor(view_subPopup, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.CENTER, false);
        }

    }

    private void onClickGrid() {

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerview.setLayoutManager(layoutManager);

        if (postsGridAdaptar == null) {
            postsGridAdaptar = new ProfilePostsGridAdaptar(this, ary_postsData, imageLoader);
            postsGridAdaptar.setHasStableIds(true);
            postsGridAdaptar.setClickListener(this);
        }

        if (recyclerview.getAdapter() != null) recyclerview.setAdapter(null);

        recyclerview.setAdapter(postsGridAdaptar);

        loadData();

        isTapedGridView = true;

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_selected);
        img_selected_list.setImageResource(R.drawable.icon_profile_list_unselected);

    }

    private void onClickList() {

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);

        if (postsListAdaptar == null) {
            postsListAdaptar = new ProfilePostsListAdaptar(this, ary_postsData, ary_aniCommented, this, imageLoader, false, false);
            postsListAdaptar.setHasStableIds(true);
        }
        if (recyclerview.getAdapter() != null) recyclerview.setAdapter(null);

        recyclerview.setAdapter(postsListAdaptar);

        loadData();

        isTapedGridView = false;

        img_selected_grid.setImageResource(R.drawable.icon_profile_grid_unselected);
        img_selected_list.setImageResource(R.drawable.icon_profile_list_selected);

    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        } else if (view == img_selected_list) {
            onClickList();
        } else if (view == img_selected_grid) {
            onClickGrid();
        } else if (view == frame_map) {

            if (mPlaceModel.lng.equals("") || mPlaceModel.lng.equals("null")) return;
            if (mPlaceModel.lat.equals("") || mPlaceModel.lat.equals("null")) return;

            Intent intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra(PlaceDetailsActivity.PLACE_TITLE, mPlaceModel.description);
            intent.putExtra(PlaceDetailsActivity.PLACE_LAT, mPlaceModel.lat);
            intent.putExtra(PlaceDetailsActivity.PLACE_LNG, mPlaceModel.lng);

            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_following) {

            if (Constants.isLifeToken(this)) {

                String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                requesType = REQUEST_POST_FOLLOW;

                if (isFollowing) {
                    linear_following.setBackground(getResources().getDrawable(R.drawable.btn_follow_background));
                    txt_following.setText("# " + getString(R.string.follow));
                    txt_following.setTextColor(this.getResources().getColor(R.color.white));

                    isFollowing = false;

                    ServerRequest.getSharedServerRequest().unfollowTag(this, this, token, String.valueOf(mitemID));

                } else {
                    linear_following.setBackground(getResources().getDrawable(R.drawable.linear_corner5_gray));
                    txt_following.setText("# " +  getString(R.string.following));
                    txt_following.setTextColor(this.getResources().getColor(R.color.black));

                    isFollowing = true;

                    ServerRequest.getSharedServerRequest().followTag(this, this, token, String.valueOf(mitemID), mSelectPostID);
                }

            } else {
                Constants.requestRefreshToken(this, this);
            }

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            switch (requesType) {

                case REQUEST_GET_POST_TAG:
                case REQUEST_GET_POST_TAG_TEXT:

                    parsingTagResult(objResult);

                    break;
                case REQUEST_GET_POST_PLACE:

                    parsingPlaceResult(objResult);

                    break;
                case Constants.REQUEST_GET_USERS_UPVOTED:

                    getUsersUpvoted(objResult);

                    break;
                case Constants.REQUEST_SEND_COMMENT:
                case Constants.REQUEST_SEND_FAVOURITE:
                case Constants.REQUEST_SEND_FOLLOW:

                    getPostsFromID(ary_postsData.get(mPosition).id);

                    break;
                case Constants.REQUEST_GET_POST_FROM_ID:

                    changedSelectedPost(objResult);

                    break;
                case Constants.REQUEST_SEND_UPVOTE:

                    getPostsFromID(ary_postsData.get(mPosition).id);

                    if (relative_upvoted_done != null && mUpvotedDoneDialogParams != null) {

                        UpvotedDoneDialog dialog = new UpvotedDoneDialog(this);
                        dialog.setLayoutParams(mUpvotedDoneDialogParams);
                        relative_upvoted_done.addView(dialog);

                        Utility.scaleView(dialog, 0.0f, 0.5f, 500);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Utility.scaleView(dialog, 0.5f, 0.0f, 500);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        relative_upvoted_done.removeView(dialog);
                                    }
                                }, 500);

                            }
                        }, 800);
                    }

                    break;
                case Constants.REQUEST_DELETE_POST: {

                    ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.deleted));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ary_postsData.remove(mPosition);
                            ary_aniCommented.remove(mPosition);
                            ary_publicPostsData.remove(mPosition);
                            if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();
                        }
                    }, 1000);

                    break;
                }
                case Constants.REQUEST_USER_MUTE:

                    mutedUserPostRemove();

                    break;
                case Constants.REQUEST_BLOCK_POST: {

                    ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.blocked));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ary_postsData.remove(mPosition);
                            ary_aniCommented.remove(mPosition);
                            ary_publicPostsData.remove(mPosition);
                            if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();
                        }
                    }, 1000);

                    break;
                }
            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            switch (requesType) {

                case REQUEST_GET_POST_TAG:

                    getTagDatas(mitemID, currentPage);

                    break;
                case REQUEST_GET_POST_PLACE:

                    getPlaceDatas(mitemID, currentPage);

                    break;
                case REQUEST_GET_POST_TAG_TEXT:

                    getTagDatasFromText(mitemTitle, currentPage);

                    break;
                case Constants.REQUEST_GET_USERS_UPVOTED:

                    getUpvotedUsers(mPosition);

                    break;
                case Constants.REQUEST_SEND_UPVOTE:

                    sendUpvote(mPosition);

                    break;
                case Constants.REQUEST_DELETE_POST:

                    deleteMedia();

                    break;
                case Constants.REQUEST_USER_MUTE:

                    userMute(mPosition);

                    break;
                case Constants.REQUEST_BLOCK_POST:

                    blockFeed(mPosition);

                    break;
            }

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        } else {

            if (errorCode == 403) {

                checkPhoneVerifyStatus();

            } else {

                Utility.parseError(this, strError);

                if (requesType.equals(Constants.REQUEST_SEND_UPVOTE)) {

                    PostsModel postsModel = ary_postsData.get(mPosition);

                    int oldAmount = postsModel.up_votes_amount;
                    postsModel.up_voted = false;
                    if (oldAmount >= 10) postsModel.up_votes_amount = oldAmount - 10;

                    ary_postsData.set(mPosition, postsModel);
                    if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onGridItemClick(View view, int position) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
        Utility.saveImpressionToSql(this, impressionModel);

        Intent intent = new Intent(this, UsersActivity.class);

        intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_DISCOVER_DETAIL);

        if (requesType.equals(REQUEST_GET_POST_TAG)) {
            intent.putExtra(UsersActivity.TYPE_DISCOVER_DETAIL, UsersActivity.TYPE_DISCOVER_DETAIL_TAG);
        } else if (requesType.equals(REQUEST_GET_POST_PLACE)) {
            intent.putExtra(UsersActivity.TYPE_DISCOVER_DETAIL, UsersActivity.TYPE_DISCOVER_DETAIL_PLACE);
        }

        intent.putExtra(TAG, mitemID);

        intent.putExtra(UsersActivity.IS_SCROLL, true);
        intent.putExtra(UsersActivity.SCROLL_POSITION, position);

        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onClickSendingComment(String string, int postion) {

        addComment(string, postion);

        if (Constants.isLifeToken(this)) {

            requesType = Constants.REQUEST_SEND_COMMENT;
            mPosition = postion;

            PostsModel postsModel = ary_postsData.get(postion);
            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_COMMENT);
            Utility.saveImpressionToSql(this, impressionModel);

            Utility.dismissKeyboard(this);
            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().createComment(this, this, postsModel.id, string, access_token);

        } else {
            Constants.requestRefreshToken(this, this);
            requesType = "";
        }
    }

    private void addComment(String body, int position) {

        CommentsModel commentsModel = new CommentsModel();

        commentsModel.body = body;
        commentsModel.created_at = Utility.getCurrentDate();
        commentsModel.up_voted = false;

        commentsModel.userModel = new UserModel();
        commentsModel.userModel.id = Utility.getReadPref(this).getIntValue(Constants.ID);

        commentsModel.userModel.username = Utility.getReadPref(this).getStringValue(Constants.USERNAME);
        commentsModel.userModel.avatar = Utility.getReadPref(this).getStringValue(Constants.AVATAR);
        if (commentsModel.userModel.avatar.equals("null")) {
            if (commentsModel.userModel.username.length() > 15) {
                commentsModel.userModel.avatar_temp = 14;
            } else {
                commentsModel.userModel.avatar_temp = commentsModel.userModel.username.length();
            }
        }

        commentsModel.isSelected = false;
        commentsModel.isEnabled = false;

        commentsModel.ary_replies = new ArrayList<>();

        PostsModel postsModel = ary_postsData.get(position);
        postsModel.comments.add(commentsModel);
        postsModel.comments_count = postsModel.comments_count + 1;

        ary_postsData.set(position, postsModel);

        if (postsListAdaptar != null) postsListAdaptar.notifyDataSetChanged();
    }

    @Override
    public void onClickViewAllComment(int position, boolean flag) {
        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(this, impressionModel);

        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(CommentActivity.POST_ID, postsModel.id);
        intent.putExtra(CommentActivity.POST_DESCRIPTION, postsModel.caption);
        intent.putExtra(CommentActivity.POST_USER_AVATAR, postsModel.user.avatar);
        intent.putExtra(CommentActivity.POST_USER_NAME, postsModel.user.username);
        intent.putExtra(CommentActivity.POST_CREATED, postsModel.created_at);
        intent.putExtra(CommentActivity.KEY_BOARD_STATUS, flag);
        intent.putExtra(CommentActivity.POST_USER_AVATAR_TEMP, postsModel.user.avatar_temp);
        intent.putExtra(CommentActivity.POST_COMMENT_COUNT, postsModel.comments_count);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onVoted(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(this, impressionModel);

        Intent intent = new Intent(this, AddUpvoteActivity.class);
        intent.putExtra(AddUpvoteActivity.POST_ID, String.valueOf(postsModel.id));
        intent.putExtra(AddUpvoteActivity.PROMO_ID, promo_id);
        intent.putExtra(AddUpvoteActivity.IS_UPVOTED, postsModel.up_voted);
        intent.putExtra(AddUpvoteActivity.TARGET, HomepageFragment.TARGET_UPVOTE);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onFavorite(boolean favorit, int position) {

        if (Constants.isLifeToken(this)) {

            PostsModel postsModel = ary_postsData.get(position);
            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
            Utility.saveImpressionToSql(this, impressionModel);

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            requesType = Constants.REQUEST_SEND_FAVOURITE;
            mPosition = position;

            if (favorit) {

                ServerRequest.getSharedServerRequest().deselectFavorit(this, this, postsModel.id, access_token);

            } else {

                ServerRequest.getSharedServerRequest().selectFavorit(this, this, postsModel.id, access_token);

            }
        } else {
            Constants.requestRefreshToken(this, this);
            requesType = "";
        }
    }

    @Override
    public void onFollow(int position) {

        if (Constants.isLifeToken(this)) {

            PostsModel postsModel = ary_postsData.get(position);
            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_FOLLOWING);
            Utility.saveImpressionToSql(this, impressionModel);

            String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            requesType = Constants.REQUEST_SEND_FOLLOW;
            mPosition = position;

            if (postsModel.user.interaction_status.is_following) {

                ServerRequest.getSharedServerRequest().selectUnFollow(this, this, postsModel.user.username, access_token);

            } else {

                ServerRequest.getSharedServerRequest().selectFollow(this, this, postsModel.user.username, access_token);

            }

        } else {
            Constants.requestRefreshToken(this, this);
            requesType = "";
        }

    }

    @Override
    public void createPromotion(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

        Utility.showCreatePromotionDialog(this, postsModel);
    }

    @Override
    public void gotoPromotionDetail(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, postsModel.promotionModel.id, 0, Constants.METRICS_ACTION_BUTTON);
            Utility.saveImpressionToSql(this, impressionModel);
//            Utility.openBrowser(this, postsModel.promotionModel.site_url);
            Utility.showWebviewDialog(this, postsModel.promotionModel.site_url);

        } else {

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, postsModel.promotionModel.id, 0, Constants.METRICS_PROFILE_VIEW);
            Utility.saveImpressionToSql(this, impressionModel);

            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.SELECT_USERNAME, postsModel.user.username);
            intent.putExtra(UserProfileActivity.SELECT_USERID, postsModel.user.id);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }

    }

//    @Override
//    public void onCharityDonate(int position) {
//
//        PostsModel postsModel = ary_postsData.get(position);
//        int promo_id = -1;
//        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;
//
//        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
//        Utility.saveImpressionToSql(this, impressionModel);
//
//        mPosition = position;
//
//        Intent intent = new Intent(this, CreateDonateActivity.class);
//        intent.putExtra(CreateDonateActivity.POST_ID, postsModel.id);
//        intent.putExtra(CreateDonateActivity.PROMO_ID, promo_id);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//    }

    @Override
    public void showFundingStatus(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

//        Utility.showFundingDetailDialog(mContext, postsModel, false);
        fundingDetailBottomSheetDialog = FundingDetailBottomSheetDialog.getInstance(this);
        fundingDetailBottomSheetDialog.setData(postsModel, false);
        fundingDetailBottomSheetDialog.setCanceledOnTouchOutside(true);
        fundingDetailBottomSheetDialog.show();
    }

    @Override
    public void showAskingHelp(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.askingHelpModel != null) {

            Intent intent = new Intent(this, AskingHelpListActivity.class);
            intent.putExtra(Constants.ID, postsModel.askingHelpModel.id);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {

            askingHelpBottomSheetDialog = AskingHelpBottomSheetDialog.getInstance(this);
            askingHelpBottomSheetDialog.setCanceledOnTouchOutside(true);
            askingHelpBottomSheetDialog.setData(postsModel.id);
            askingHelpBottomSheetDialog.show();

        }
    }

//    @Override
//    public void onCharityJoin(int position) {
//
//        PostsModel postsModel = ary_postsData.get(position);
//
//        Utility.g_director = Constants.TEAM;
//        Utility.mainFeedParams = new MainFeedParams();
//
//        if (postsModel.type.equals(Constants.FUNDING) && postsModel.fundingModel != null) {
//
//            Utility.mainFeedParams.fundingModel = postsModel.fundingModel;
//
//            Intent intent = new Intent(this, PhotoPickerActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.top_in, R.anim.top_out);
//        }
//    }

    @Override
    public void onRunedAnimationComment(int position) {

    }

    @Override
    public void onShowingUserInfo(int position) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_PROFILE_VIEW);
        Utility.saveImpressionToSql(this, impressionModel);

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, postsModel.user.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, postsModel.user.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onShowingUserInfoWithUserinfo(UserModel userModel) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, userModel.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onShowActionBar(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
        Utility.saveImpressionToSql(this, impressionModel);

        if (postsModel.user.id == Utility.getReadPref(this).getIntValue(Constants.ID)) {

            mAlertView = new AlertView(null, null, getString(R.string.cancel), new String[]{getString(R.string.edit), getString(R.string.edit_tag), getString(R.string.share), getString(R.string.view_engage)},
                    new String[]{getString(R.string.delete)},
                    this, AlertView.Style.ActionSheet, new OnItemClickListener(){
                public void onItemClick(Object o,int position){

                    switch (position) {
                        case 0: {
                            Intent intent = new Intent(DiscoverItemDetailActivity.this, MediaCaptionEditActivity.class);
                            intent.putExtra(MediaCaptionEditActivity.SELECTED_POST, postsModel.id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        break;
                        case 1: {
                            Utility.mainFeedParams = new MainFeedParams();
                            Utility.mainFeedParams.ary_tags = postsModel.tags;

                            Intent intent = new Intent(DiscoverItemDetailActivity.this, MainFeedTagsActivity.class);
                            intent.putExtra(MainFeedTagsActivity.ACTIVITY_TYPE, TAG);
                            intent.putExtra(Constants.ID, postsModel.id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                        }
                        break;
                        case 2:
                            String shareURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);
                            Utility.shareTextAction(DiscoverItemDetailActivity.this, shareURL, getString(R.string.pibble), getString(R.string.share_via));
                            break;
                        case 3:
                            Utility.showPromotionEngageDetailDialog(DiscoverItemDetailActivity.this, postsModel);
                            break;
                        case 4: {

                            int upvoteAmount = ary_postsData.get(mPosition).up_votes_amount;
                            int userPRB = (int) Float.parseFloat(ary_postsData.get(mPosition).user.available_PRB);
                            int consum = upvoteAmount / 2;

                            if (userPRB < consum) {

                                String message = getString(R.string.insufficient_prb_for_deleting) + String.valueOf(consum) + " " + getString(R.string.prb);

                                new AlertView(null, message, null, new String[]{getString(R.string.cancel)},
                                        null,
                                        DiscoverItemDetailActivity.this, AlertView.Style.Alert, new OnItemClickListener(){
                                    public void onItemClick(Object o,int position){

                                    }

                                }).show();

                            } else {

                                String message = null;

                                if (consum != 0) {

                                    message = getString(R.string.consume) + " " + String.valueOf(consum) + " " + getString(R.string.prb);

                                }

                                new AlertView(getString(R.string.delete_post), message, null, new String[]{getString(R.string.cancel), getString(R.string.delete)},
                                        null,
                                        DiscoverItemDetailActivity.this, AlertView.Style.Alert, new OnItemClickListener(){
                                    public void onItemClick(Object o,int position){

                                        if (position == 1) {
                                            deleteMedia();
                                        }
                                    }

                                }).show();
                            }

                        }
                        break;
                    }

                }

            });
            mAlertView.show();

        } else {

            mAlertView = new AlertView(null, null, getString(R.string.cancel), new String[]{getString(R.string.share), getString(R.string.copy_link), getString(R.string.turn_on_post_notification)},
                    new String[]{getString(R.string.report), getString(R.string.mute)},
                    this, AlertView.Style.ActionSheet, new OnItemClickListener(){
                public void onItemClick(Object o,int position){

                    switch (position) {

                        case 0:

                            String shareURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);
                            Utility.shareTextAction(DiscoverItemDetailActivity.this, shareURL, getString(R.string.pibble), getString(R.string.share_via));

                            break;

                        case 1:

                            String copyURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("url", copyURL);
                            clipboard.setPrimaryClip(clip);

                            break;

                        case 2:
                            break;

                        case 3:

                            reportAction(mPosition);

                            break;

                        case 4:

                            String title = getString(R.string.mute) + " " + ary_postsData.get(mPosition).user.username + " ?";
                            String message = getString(R.string.unmute_description);

                            new AlertView(title, message, null, new String[]{getString(R.string.cancel), getString(R.string.mute_posts)},
                                    null,
                                    DiscoverItemDetailActivity.this, AlertView.Style.Alert, new OnItemClickListener(){
                                public void onItemClick(Object o,int position){

                                    if (position == 1) {
                                        userMute(mPosition);
                                    }
                                }

                            }).show();

                            break;
                    }

                }

            });

            mAlertView.show();

        }
    }

    private void reportAction(int position) {

        mPosition = position;

        mAlertView = new AlertView(null, null, getString(R.string.cancel), null,
                new String[]{getString(R.string.report_as_inappropriate), getString(R.string.block)},
                this, AlertView.Style.ActionSheet, new OnItemClickListener(){

            public void onItemClick(Object o,int position){

                Utility.g_isShowingTabbar = false;

                switch (position) {

                    case 0:

                        Intent intent = new Intent(DiscoverItemDetailActivity.this, ReportReasonSelectActivity.class);
                        intent.putExtra(ReportReasonSelectActivity.TAG, ary_postsData.get(mPosition).id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                        break;
                    case 1:

                        AlertVerticalDialog dialog = new AlertVerticalDialog(DiscoverItemDetailActivity.this, getString(R.string.thanks_for_reporting_this_post),
                                getString(R.string.report_post_description),
                                getString(R.string.block), getString(R.string.cancel), R.color.colorMain, R.color.black) {

                            @Override
                            public void onClickButton(int position) {

                                if (position == 0) {

                                    blockFeed(mPosition);

                                }

                                dismiss();

                            }

                        };
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        break;
                    case 2:

                        break;

                }

            }

        });

        mAlertView.show();
    }

    @Override
    public void onhideKeyboard() {

    }

    @Override
    public void onClickTag(String string, int position) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_TAG);
        Utility.saveImpressionToSql(this, impressionModel);

        Intent intent = new Intent(this, DiscoverItemDetailActivity.class);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, Utility.getStringWithoutFirstCharater(string));
        intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, "");
        intent.putExtra(DiscoverItemDetailActivity.ITEM_POST_ID, ary_postsData.get(position).id);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, Constants.DISCOVER_TAG_TEXT);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onClickPlace(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.placeModel == null) return;

        if (postsModel.placeModel.lng.equals("") || postsModel.placeModel.lng.equals("null")) return;
        if (postsModel.placeModel.lat.equals("") || postsModel.placeModel.lat.equals("null")) return;

        Intent intent = new Intent(this, PlaceDetailsActivity.class);
        intent.putExtra(PlaceDetailsActivity.PLACE_TITLE, postsModel.placeModel.description);
        intent.putExtra(PlaceDetailsActivity.PLACE_LAT, postsModel.placeModel.lat);
        intent.putExtra(PlaceDetailsActivity.PLACE_LNG, postsModel.placeModel.lng);

        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    private void getUpvotedUsers(int position) {
        String postid = String.valueOf(ary_postsData.get(position).id);

        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getUsersUpvoted(this, this, access_token, postid,1, 10);
    }

    @Override
    public void showUpvotedUserPopup(String position) {

        requesType = Constants.REQUEST_GET_USERS_UPVOTED;

        if (Constants.isLifeToken(this)) {

            mPosition = Integer.parseInt(position);

            getUpvotedUsers(mPosition);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    @Override
    public void hideWalletPreview() {

    }

    @Override
    public void OnClickDigitalGoods(int position, boolean isSeller) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(this, impressionModel);

        if (isSeller) {

            String title = postsModel.commerceModel.name;

            String detail = getString(R.string.price) + " " + String.valueOf(postsModel.commerceModel.price) + " " + getString(R.string.pib) + ", " + getString(R.string.reward) + " " + String.valueOf(postsModel.commerceModel.price * 0.15) + " " + getString(R.string.pib);

            Intent intent = new Intent(this, RoomsInGroupActivity.class);
            intent.putExtra(RoomsInGroupActivity.POST_ID, postsModel.id);
            intent.putExtra(RoomsInGroupActivity.GOODS_TITLE, title);
            intent.putExtra(RoomsInGroupActivity.GOODS_DETAIL, detail);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else {

            if (postsModel.invoiceModel != null) {

                if (postsModel.invoiceModel.status.equals(Constants.INVOICE_ACCEPTED)) {

                    String name = "";
                    if (ary_postsData.get(position).user.first_name.equals("null") || ary_postsData.get(position).user.first_name.equals("") || ary_postsData.get(position).user.last_name.equals("null") || ary_postsData.get(position).user.last_name.equals("")) {
                        name = ary_postsData.get(position).user.username;
                    } else {
//                        name = ary_postsData.get(position).user.last_name + " " + ary_postsData.get(position).user.first_name;
                        name = ary_postsData.get(position).user.username;
                    }

                    Intent intent = new Intent(this, ChatRoomActivity.class);
                    intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
                    intent.putExtra(Constants.TYPE, Constants.ROOM_DIGITAL_GOODS);
                    intent.putExtra(Constants.POSTID, postsModel.id);
                    intent.putExtra(Constants.USERNAME, name);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                } else {

                    Utility.showDigitalGoodsDialog(this, imageLoader, postsModel);

                }

            } else {

                Utility.showDigitalGoodsDialog(this, imageLoader, postsModel);

            }
        }
    }

    @Override
    public void OnClickGoods(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.user.id == Utility.getReadPref(this).getIntValue(Constants.ID)) {

            String title = postsModel.goodsModel.title;
            @SuppressLint("DefaultLocale") String detail = String.format("%s %s %s", getString(R.string.price), postsModel.goodsModel.price, getString(R.string.pib));

            Intent intent = new Intent(this, RoomsInGroupActivity.class);
            intent.putExtra(RoomsInGroupActivity.POST_ID, postsModel.id);
            intent.putExtra(RoomsInGroupActivity.GOODS_TITLE, title);
            intent.putExtra(RoomsInGroupActivity.GOODS_DETAIL, detail);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else {

            if (postsModel.goodsModel.goods_order_id.equals("null")) {

                Utility.showGoodsDialog(this, imageLoader, postsModel);

            } else {

                if (postsModel.user.id != Utility.getReadPref(this).getIntValue(Constants.ID)) {

                    Intent intent = new Intent(this, ChatRoomActivity.class);
                    intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
                    intent.putExtra(Constants.TYPE, Constants.ROOM_GOODS);
                    intent.putExtra(Constants.POSTID, postsModel.id);
                    intent.putExtra(Constants.USERNAME, postsModel.user.username);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                }
            }
        }
    }

    @Override
    public void onDoubleClickMediaView(RelativeLayout relativeLayout, LinearLayout.LayoutParams params, int position) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 10, Constants.METRICS_UP_VOTE);
        Utility.saveImpressionToSql(this, impressionModel);

        relative_upvoted_done = relativeLayout;
        mUpvotedDoneDialogParams = params;

        sendUpvote(position);
    }

    @Override
    public void onLongClickImageView(int position, int mediapos) {

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(this, postsModel.id, promo_id, 0, Constants.METRICS_COLLECT);
        Utility.saveImpressionToSql(this, impressionModel);

        String media_url;
        boolean isPurchased, isRotate;

        if (postsModel.invoiceModel != null) {

            if (postsModel.invoiceModel.status.equals(Constants.INVOICE_ACCEPTED)) {
                media_url = postsModel.ary_media.get(mediapos).s3ld;
                isPurchased = true;
            } else {
                media_url = postsModel.ary_media.get(mediapos).url;
                isPurchased = false;
            }

        } else {

            if (postsModel.user.id == Utility.getReadPref(this).getIntValue(Constants.ID)) {

                if (postsModel.commerceModel != null) {

                    media_url = postsModel.ary_media.get(mediapos).s3ld;
                    isPurchased = true;

                } else {

                    media_url = postsModel.ary_media.get(mediapos).url;
                    isPurchased = false;

                }

            } else {
                media_url = postsModel.ary_media.get(mediapos).url;
                isPurchased = false;
            }
        }

        isRotate = postsModel.ary_media.get(mediapos).width > postsModel.ary_media.get(mediapos).height;

        if (isImageLongClicked) {

            isImageLongClicked = false;

            Intent intent = new Intent(this, DisplayingImageActivity.class);
            intent.putExtra(DisplayingImageActivity.IMAGE_PATH, media_url);

            intent.putExtra(DisplayingImageActivity.THUMB_PATH, postsModel.ary_media.get(mediapos).thumbnail);

            if (postsModel.ary_media.get(mediapos).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                intent.putExtra(DisplayingImageActivity.MIME_TYPE, true);
            } else {
                intent.putExtra(DisplayingImageActivity.MIME_TYPE, false);
            }
            intent.putExtra(DisplayingImageActivity.MEDIA_WIDTH, postsModel.ary_media.get(mediapos).width);
            intent.putExtra(DisplayingImageActivity.MEDIA_HEIGHT, postsModel.ary_media.get(mediapos).height);

            intent.putExtra(DisplayingImageActivity.IS_PURCHASED, isPurchased);
            intent.putExtra(DisplayingImageActivity.IS_ROTATE, isRotate);
            intent.putExtra(DisplayingImageActivity.ORIGINAL_WIDTH, postsModel.ary_media.get(mediapos).original_width);
            intent.putExtra(DisplayingImageActivity.ORIGINAL_HEIGHT, postsModel.ary_media.get(mediapos).original_height);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    @Override
    public void onClickUserUpvoted(int position) {

        upvotedUserPopupview.dismiss();

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, ary_usersUpvoted.get(position).userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, ary_usersUpvoted.get(position).userModel.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onLongClickUserUpvoted(int position) {

        upvotedUserPopupview.dismiss();

        if (ary_postsData.get(mPosition).user.username.equals(Utility.getReadPref(this).getStringValue("username"))) {

            Intent intent = new Intent(this, AddUpvoteActivity.class);
            intent.putExtra(Constants.USERNAME, ary_usersUpvoted.get(position).userModel.username);
            intent.putExtra(HomepageFragment.RECEIVED_AMOUNT, ary_usersUpvoted.get(position).amount);
            intent.putExtra(Constants.TARGET, HomepageFragment.TARGET_USER_UPVOTE);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mPlaceModel.lng.equals("") || mPlaceModel.lng.equals("null")) return;
        if (mPlaceModel.lat.equals("") || mPlaceModel.lat.equals("null")) return;

        double lat = Double.parseDouble(mPlaceModel.lat);
        double lng = Double.parseDouble(mPlaceModel.lng);

        LatLng sydney = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_marker)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

}
