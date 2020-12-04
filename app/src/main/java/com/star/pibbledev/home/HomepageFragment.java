package com.star.pibbledev.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.star.pibbledev.BaseFragment;
import com.star.pibbledev.auth.PhoneVerificationActivity;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.dashboard.DashboardFragment;
import com.star.pibbledev.R;
import com.star.pibbledev.chatroom.activity.ChatRoomActivity;
import com.star.pibbledev.chatroom.activity.RoomsInGroupActivity;
import com.star.pibbledev.discover.activities.DiscoverItemDetailActivity;
import com.star.pibbledev.discover.activities.PlaceDetailsActivity;
import com.star.pibbledev.gifticon.GiftHomeActivity;
import com.star.pibbledev.home.asking.AskingHelpListActivity;
import com.star.pibbledev.home.createmedia.tags.MainFeedTagsActivity;
import com.star.pibbledev.home.imagedetail.DisplayingImageActivity;
import com.star.pibbledev.home.editmedia.MediaCaptionEditActivity;
import com.star.pibbledev.home.report.ReportReasonSelectActivity;
import com.star.pibbledev.home.topgroup.BannerDetailActivity;
import com.star.pibbledev.home.topgroup.LeaderBoardActivity;
import com.star.pibbledev.home.topgroup.LinearTopGroup;
import com.star.pibbledev.home.topgroup.TopGroupListener;
import com.star.pibbledev.profile.activity.PlayroomActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
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
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.services.global.model.UpvotedUsersModel;
import com.star.pibbledev.home.comments.CommentActivity;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.upvote.AddUpvoteActivity;
import com.star.pibbledev.home.upvote.UpvoteListener;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.home.utility.videoview.VideoView;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.services.network.BackendAPI;
import com.star.pibbledev.services.network.CheckUpdate;
import com.star.pibbledev.services.network.PromoRequestListener;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class HomepageFragment extends BaseFragment implements RequestListener, PromoRequestListener, MainFeedListener, View.OnClickListener, UpvoteListener, TopGroupListener {

    public static final String TAG = "HomepageFragment";

    private static final String SUCCESS_FOLLOW = "selectFollow";
    private static final String SUCCESS_GET_ALLPOSTS = "getAllposts";
    private static final String ONCREATE_GET_UNREAD_MESSAGE_COUNT = "oncreate_get_unread_messages";
    private static final String ONRESUME_GET_UNREAD_MESSAGE_COUNT = "onresume_get_unread_messages";
    private static final String SUCCESS_REFRESH = "getRefreshposts";
    private static final String SUCCESS_GET_UPVOTE = "limitedUpvote";
    private static final String SUCCESS_SEND_COMMENT = "sendingComment";
    private static final String SUCCESS_SEND_FAVORITE = "selectFavorite";
    private static final String SUCCESS_GET_WALLET = "getWallet";
    private static final String SUCCESS_GET_USERS_UPVOTED = "getUsersUpvoted";

    public static final String TARGET_UPVOTE = "post_upvote";
    public static final String TARGET_USER_UPVOTE = "user_upvote";

    public static final String RECEIVED_AMOUNT = "receivedAmount";

    private Context mContext;
    private ListView list_post;
    private RelativeLayout btn_gotoWallet, btn_gotoMessage, relative_upvoted_done;
    private LinearLayout linear_wallet, linear_progress, linear_progress_tab, btn_goto_playroom, btn_goto_gifticon;
    private Button btn_Wallet;
    private TextView txt_upvote, txt_prb, txt_pgb, txt_pib, txt_eth, txt_btc, txt_progress, txt_badgeNum, txt_badgeNum_wallet;
    private PullRefreshLayout pullRefreshLayout;
    private UpvotedUsersBottomSheetDialog upvotedUserPopupview;
    private ProgressBar view_progress;
    private FrameLayout frame_badge_wallet, frame_badge;

    private PostsListAdapter postsListAdapter;

    private int pageCount, totalPagecount, mPosition;

    private ArrayList<PostsModel> ary_postsData = new ArrayList<>();
    private ArrayList<Integer> ary_aniCommented = new ArrayList<>();
    private ArrayList<UpvotedUsersModel> ary_usersUpvoted = new ArrayList<>();

    private String category_success, severURL;
    private boolean isImageLongClicked, flag_loading, flag_walletAnimation;

    private LinearLayout.LayoutParams mUpvotedDoneDialogParams;

    private boolean isScrolledTop;

    private ImageLoader imageLoader;
    private DashboardFragment dashboardFragment;
    private AlertView mAlertView;
    private FundingDetailBottomSheetDialog fundingDetailBottomSheetDialog;
    private AskingHelpBottomSheetDialog askingHelpBottomSheetDialog;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment_home_allposts, null);
        mContext = getActivity();

        dismissTouch(view.findViewById(R.id.list_post), getActivity());

        list_post = (ListView) view.findViewById(R.id.list_post);
        linear_wallet = (LinearLayout) view.findViewById(R.id.linear_wallet);
        btn_Wallet = (Button) view.findViewById(R.id.btn_Wallet);
        btn_Wallet.setOnClickListener(this);
        txt_upvote = (TextView) view.findViewById(R.id.txt_upvote);
        txt_prb = (TextView) view.findViewById(R.id.txt_prb);
        txt_pgb = (TextView) view.findViewById(R.id.txt_pgb);
        txt_pib = (TextView) view.findViewById(R.id.txt_pib);
        txt_eth = (TextView) view.findViewById(R.id.txt_eth);
        txt_btc = (TextView) view.findViewById(R.id.txt_btc);

        txt_badgeNum = (TextView) view.findViewById(R.id.txt_badgeNum);
        txt_badgeNum_wallet = (TextView) view.findViewById(R.id.txt_badgeNum_wallet);
        frame_badge_wallet = (FrameLayout) view.findViewById(R.id.frame_badge_wallet);
        frame_badge_wallet.setVisibility(View.GONE);
        frame_badge = (FrameLayout) view.findViewById(R.id.frame_badge);
        frame_badge.setVisibility(View.GONE);

        view_progress = (ProgressBar) view.findViewById(R.id.view_progress);
        linear_progress = (LinearLayout) view.findViewById(R.id.linear_progress);
        linear_progress.setVisibility(View.GONE);
        linear_progress_tab = (LinearLayout) view.findViewById(R.id.linear_progress_tab);
        linear_progress_tab.setOnClickListener(this);
        linear_progress_tab.setVisibility(View.GONE);

        btn_goto_playroom = (LinearLayout) view.findViewById(R.id.btn_goto_playroom);
        btn_goto_playroom.setOnClickListener(this);
        btn_goto_gifticon = (LinearLayout) view.findViewById(R.id.btn_goto_gifticon);
        btn_goto_gifticon.setOnClickListener(this);

        txt_progress = (TextView) view.findViewById(R.id.txt_progress);

        btn_gotoWallet = (RelativeLayout) view.findViewById(R.id.btn_gotoWallet);
        btn_gotoWallet.setOnClickListener(this);
        btn_gotoMessage = (RelativeLayout) view.findViewById(R.id.btn_gotoMessage);
        btn_gotoMessage.setOnClickListener(this);

        category_success = "";
        severURL = BackendAPI.post_getAll;
        flag_walletAnimation = false;

        pullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.pulltorefresh);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        pullRefreshLayout.setColor(Color.LTGRAY);

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getRefreshPosts();

                if (linear_wallet.getVisibility() == View.VISIBLE) {

                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_scale_zoom);
                    animation.setDuration(400);
                    linear_wallet.setAnimation(animation);
                    linear_wallet.animate();
                    animation.start();
                    linear_wallet.setVisibility(View.GONE);

                    flag_walletAnimation = false;
                }
            }
        });

        pageCount = 1;

        getUnreadAllMessage(ONCREATE_GET_UNREAD_MESSAGE_COUNT);

        DashboardActivity dashboardActivity = (DashboardActivity)getActivity();
        dashboardFragment = ((DashboardFragment) dashboardActivity.getFragment(0));

        if (Utility.g_isPostedMedia) {

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    view_progress.setProgress(Utility.g_progressValue);

                    if (Utility.g_progressValue == 404) {

                        if (Utility.g_postError != null && Utility.g_postError.length() > 0) {
                            Utility.parseError((AppCompatActivity) mContext, Utility.g_postError);
                        }

                        linear_progress.setVisibility(View.GONE);
                        linear_progress_tab.setVisibility(View.GONE);

                        Utility.g_isPostedMedia = false;

                        handler.removeCallbacks(this);
                        Utility.g_progressValue = 0;

                    } else if (Utility.g_progressValue == 130) {

                        Utility.g_isPostedMedia = false;

                        handler.removeCallbacks(this);
                        Utility.g_progressValue = 0;

                        txt_progress.setText(R.string.posting_completed);
                        if (linear_progress_tab.getVisibility() == View.GONE) linear_progress_tab.setVisibility(View.VISIBLE);
                        if (linear_progress.getVisibility() == View.GONE) linear_progress.setVisibility(View.VISIBLE);

                    } else {

                        if (linear_progress.getVisibility() == View.GONE) {

                            linear_progress.setVisibility(View.VISIBLE);
                            txt_progress.setText(R.string.progressing_to_post);

                        }

                        if (linear_progress_tab.getVisibility() == View.VISIBLE) linear_progress_tab.setVisibility(View.GONE);

                        if (Utility.g_progressValue < 128) Utility.g_progressValue = Utility.g_progressValue + 1;
                        handler.postDelayed(this, 100);
                    }

                }
            };

            handler.postDelayed(runnable, 100);
        }

        list_post.setFriction((float) (ViewConfiguration.getScrollFriction() * 2));
        list_post.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                Utility.dismissKeyboard((DashboardActivity)mContext);

                if (postsListAdapter.pEditText != null && postsListAdapter.pEditText.hasFocus()) {
                    postsListAdapter.pEditText.clearFocus();
                }

                if (scrollState == SCROLL_STATE_IDLE) {

                    Utility.g_isScrollState = true;

                    if(ary_postsData.size() > Utility.g_currentListItem) {

                        PostsModel postsModel = ary_postsData.get(Utility.g_currentListItem);

                        int promo_id = -1;
                        if (postsModel.promotionModel != null)
                            promo_id = postsModel.promotionModel.id;

                        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
                        Utility.saveImpressionToSql(mContext, impressionModel);
                    }

                } else {

                    Utility.g_isScrollState = false;
                }

                if (linear_wallet.getVisibility() == View.VISIBLE) {

                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_scale_zoom);
                    animation.setDuration(400);
                    linear_wallet.setAnimation(animation);
                    linear_wallet.animate();
                    animation.start();
                    linear_wallet.setVisibility(View.GONE);

                    flag_walletAnimation = false;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Utility.g_currentListItem = firstVisibleItem;

                if(firstVisibleItem+visibleItemCount == totalItemCount - 5 && totalItemCount > 5) {

                    if(!flag_loading)
                    {
                        if (pageCount < totalPagecount) {
                            pageCount++;
                            flag_loading = true;
                            checkingAndGetposts(pageCount);
                        }
                    }

                }

                if (isScrolledTop && firstVisibleItem != 0) {
                    list_post.smoothScrollToPositionFromTop(0, 0, 10);
                } else if (isScrolledTop) {
                    isScrolledTop = false;
                }
            }

        });

        imageLoader = ImageLoader.getInstance();

        postsListAdapter = new PostsListAdapter(mContext, ary_postsData, ary_aniCommented, this, imageLoader);
        list_post.setAdapter(postsListAdapter);

        LinearLayout linearLayout = new LinearTopGroup(mContext, this);
        list_post.addHeaderView(linearLayout);

        return view;
    }

    public void customDestory() {
        imageLoader = null;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();

        if (postsListAdapter != null) {

            if (postsListAdapter.mVideoViews != null && postsListAdapter.mVideoViews.size() > 0) {

                for (int i = 0; i < postsListAdapter.mVideoViews.size(); i++) {
                    VideoView videoView = postsListAdapter.mVideoViews.get(i);
                    if (videoView != null && videoView.isPlaying()) {
                        videoView.pause();
                    }
                }
            }
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (!btn_Wallet.isEnabled()) btn_Wallet.setEnabled(true);

        isImageLongClicked = true;

        Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if (!(fragment instanceof HomepageFragment)) return;

        Utility.g_indexDashboardTab = 1;

        if (Utility.g_isChanged) {

            if (Utility.g_isChangedType != null && Utility.g_isChangedType.equals(Constants.REPORTED)) {

                Utility.g_isChangedType = null;

                ActionStatusDialog dialog = new ActionStatusDialog(mContext, getString(R.string.reported));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ary_postsData.remove(mPosition);
                        ary_aniCommented.remove(mPosition);
                        if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
                    }
                }, 1300);

            } else if (Utility.g_isChangedType != null && Utility.g_isChangedType.equals(Constants.GOODS_ORDER_CREATED)) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.thank_you),
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

            } else {

                if (askingHelpBottomSheetDialog != null) askingHelpBottomSheetDialog.dismiss();

                if (ary_postsData != null && ary_postsData.size() > mPosition) {
                    getPostsFromID(ary_postsData.get(mPosition).id);
                }
            }

            Utility.g_isChanged = false;

        } else {

            if(ary_postsData.size() > 0) {

                if (Utility.g_isResumedAcivity && list_post.getFirstVisiblePosition() == 0) {

                    pullRefreshLayout.setRefreshing(true);
                    getRefreshPosts();

                    Utility.g_isResumedAcivity = false;
                } else {
                    getUnreadAllMessage(ONRESUME_GET_UNREAD_MESSAGE_COUNT);
                }

            } else {
                Utility.g_isResumedAcivity = false;
            }
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

    private void getUnreadAllMessage(String category) {

        category_success = category;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUserInfoFromUsername(this, mContext, token, "me");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void checkingAndGetposts(int pageCount){

        category_success = SUCCESS_GET_ALLPOSTS;

//        if (!Constants.isNetwork(mContext)) {
//            Utility.isNetworkoffline((AppCompatActivity) mContext);
//            return;
//        }
//
        if (Constants.isLifeToken(mContext)) {
            getAllPosts(pageCount);
        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void dismissTouch(View view, final Activity activity) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof ImageButton)) {

            final int[] startIndex = {0};

            final int[] position = new int[1];

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    hideWallet();

//                    if (upvotedUserPopupview != null) {
//                        if (upvotedUserPopupview.isShowing()) upvotedUserPopupview.dismiss();
//                    }

                    int scrolly = (int) motionEvent.getY();

                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                        startIndex[0]++;

                        if (startIndex[0] == 1) position[0] = scrolly;

                        if (position[0] > scrolly) {
                            if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(false);
                        }

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                        startIndex[0] = 0;

                        if (scrolly - position[0] > 80) {
                            if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(true);
                        }
//                        else if (position[0] - scrolly > 80) {
//                            if (dashboardFragment != null) dashboardFragment.tabBarAnimaition(false);
//                        }

                    }

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

    private void initBadgeNumber(JSONObject jsonObject) {

        if (jsonObject == null) return;

        int active_invoice_count = jsonObject.optInt("active_invoice_count");
        int unread_messages_count = jsonObject.optInt("unread_messages_count");

        if (active_invoice_count > 0) {
            if (frame_badge_wallet.getVisibility() == View.GONE) frame_badge_wallet.setVisibility(View.VISIBLE);
            txt_badgeNum_wallet.setText(String.valueOf(active_invoice_count));
        } else {
            if (frame_badge_wallet.getVisibility() == View.VISIBLE) frame_badge_wallet.setVisibility(View.GONE);
        }

        if (unread_messages_count > 0) {

            if (frame_badge.getVisibility() == View.GONE) frame_badge.setVisibility(View.VISIBLE);
            txt_badgeNum.setText(String.valueOf(unread_messages_count));

//            btn_gotoMessage.setEnabled(true);

        } else {

            if (frame_badge.getVisibility() == View.VISIBLE) frame_badge.setVisibility(View.GONE);

//            btn_gotoMessage.setEnabled(false);
        }

    }

    private void getAllPosts(int pageNumber) {

//        if (!Constants.isNetwork(mContext)) {
//            Utility.isNetworkoffline((AppCompatActivity) mContext);
//            return;
//        }

        String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getAllPosts( this, mContext, severURL, access_token, String.valueOf(pageNumber), "10");

    }

    private void getRefreshPosts() {

        category_success = SUCCESS_REFRESH;

        pageCount = 1;

        if (Constants.isLifeToken(mContext)) {

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getAllPosts( this, mContext, severURL, access_token, "1", "10");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }
    }

    private void getPromoPosts(int count) {
        String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getPromoPosts(this, mContext, access_token, String.valueOf(count));
    }

    private void getPostsFromID (int id) {

        category_success = Constants.REQUEST_GET_POST_FROM_ID;

        if (Constants.isLifeToken(mContext)) {
            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPostFromid(this, mContext, access_token, id);
        } else {
            Constants.requestRefreshToken(mContext, this);
        }
    }

    private void changedSelectedPost(JSONObject objPost) {

        if (objPost == null) return;

        PostsModel postsModel = ParseUtility.postModel(objPost);

        postsModel.isAnimated = ary_aniCommented.get(mPosition) == 1;

        ary_postsData.set(mPosition, postsModel);

        if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();

        if (Utility.g_isChangedFundingStatus && fundingDetailBottomSheetDialog != null) {

            fundingDetailBottomSheetDialog.setData(postsModel, false);

            Utility.g_isChangedFundingStatus = false;
        }
    }

    private void deleteMedia() {

        category_success = Constants.REQUEST_DELETE_POST;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(getActivity()).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().deletingMedia(HomepageFragment.this, mContext, ary_postsData.get(mPosition).id, token);

        } else {
            Constants.requestRefreshToken(mContext, HomepageFragment.this);
        }

    }

    private void userMute(int position) {

        category_success = Constants.REQUEST_USER_MUTE;
        mPosition = position;

        if (Constants.isLifeToken(mContext)) {

            int userid = ary_postsData.get(position).user.id;

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().setting(this, mContext, token, userid, true);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void gettingPostsArray(JSONObject objResult) {

        int cnt_all = ary_postsData.size();

        try {

            JSONArray posts = objResult.optJSONArray("items");

            if (posts == null || posts.length() == 0) return;

            JSONObject pageObj = objResult.optJSONObject("pagination");
            totalPagecount = pageObj.optInt("pages");

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

                    for (int j = 0; j < postsModel.ary_media.size(); j++) {

                        final int finalJ = j;

                        if (finalJ == postsModel.ary_media.size() - 1) {
                            ary_postsData.add(postsModel);
                            ary_aniCommented.add(0);

                            //------ for comment animaiton-----

                            for (int k = 0; k < ary_postsData.size();k++) {
                                PostsModel temp = ary_postsData.get(k);
                                if (ary_aniCommented.get(k) == 1) {
                                    temp.isAnimated = true;
                                    ary_postsData.set(k, temp);
                                }
                            }
                        }

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

//                                    if (finalJ == postsModel.ary_media.size() - 1) {
//
//                                        if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
//
//                                    }
                                }
                            });
                        }
                    }
                }
            }

            getPromoPosts(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gettingRefreshPostsArray(JSONObject objResult) {

        ary_postsData.clear();
        ary_aniCommented.clear();

        list_post.setAdapter(postsListAdapter);

        try {

            JSONArray posts = objResult.optJSONArray("items");

            if (posts == null || posts.length() == 0) return;

            JSONObject pageObj = objResult.optJSONObject("pagination");
            totalPagecount = pageObj.optInt("pages");

            for (int i = 0; i < posts.length(); i++) {

                JSONObject objPost = (JSONObject) posts.get(i);

                PostsModel postsModel = ParseUtility.postModel(objPost);

                if (postsModel.ary_media != null && postsModel.ary_media.size() > 0) {
                    ary_postsData.add(postsModel);
                    ary_aniCommented.add(0);
                }

            }

//            if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();

            getPromoPosts(2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        listviewScrollToTop();
    }

    //---------------listview scroll to top--------------

    public void listviewScrollToTop() {

        if (list_post == null) return;
        isScrolledTop = true;
        list_post.smoothScrollToPosition(0);
    }

    //-----------------getLimited upvote-------------------

    private void getUpvoteData() {

        category_success = SUCCESS_GET_UPVOTE;

        if (Constants.isLifeToken(mContext)) {
            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getProfile_limitUpvote(this, mContext, access_token);
        } else {
            Constants.requestRefreshToken(mContext, this);
        }
    }

    //----------------show user info when going to wallet----------------

    @SuppressLint("DefaultLocale")
    private void showWalletInfo(JSONObject jsonObject) {

        JSONObject postdata = jsonObject.optJSONObject("post");

        if (postdata == null) return;

        int day_upvoteCount = postdata.optInt("day_count");
        int day_limit = postdata.optInt("day_limit");

        txt_upvote.setText(String.format("%d/%d", day_upvoteCount, day_limit));

        category_success = SUCCESS_GET_WALLET;

        String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getBalances(this, mContext, token);

    }

    @SuppressLint("DefaultLocale")
    private void getBalanceInfo(JSONArray objResult) {

        try {

            String str_eth = "";
            String str_pib = "";
            String str_prb = "";
            String str_pgb = "";
            String str_btc = "";

            for (int w = 0; w < objResult.length(); w++) {
                JSONObject walObj = (JSONObject)objResult.get(w);
                switch (walObj.optString(Constants.SYMBOL)) {
                    case "ETH":
                        str_eth = walObj.optString(Constants.AVAILABLE);
                        break;
                    case "PIB":
                        str_pib = walObj.optString(Constants.AVAILABLE);
                        break;
                    case "PRB":
                        str_prb = walObj.optString(Constants.AVAILABLE);
                        break;
                    case "PGB":
                        str_pgb = walObj.optString(Constants.AVAILABLE);
                        break;
                    case "BTC":
                        str_btc = walObj.optString(Constants.AVAILABLE);
                        break;
                }
            }

            txt_prb.setText(Utility.getConvertedValue(str_prb, false));
            txt_pgb.setText(Utility.getConvertedValue(str_pgb, false));
            txt_pib.setText(Utility.formatedNumberString(Float.parseFloat(str_pib)));
            txt_eth.setText(String.format("%.4f", Float.parseFloat(str_eth)));
            txt_btc.setText(String.format("%.8f", Float.parseFloat(str_btc)));

            Utility.getSavedPref(mContext).saveString("value_eth", str_eth);
            Utility.getSavedPref(mContext).saveString("value_pib", str_pib);
            Utility.getSavedPref(mContext).saveString("value_prb", str_prb);
            Utility.getSavedPref(mContext).saveString("value_pgb", str_pgb);
            Utility.getSavedPref(mContext).saveString("value_btc", str_btc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUsersUpvoted(JSONObject jsonObject) {

        ary_usersUpvoted.clear();

        JSONArray users = jsonObject.optJSONArray("items");
        JSONObject pageObj = jsonObject.optJSONObject("pagination");

        int total = pageObj.optInt("total");
        int pages = pageObj.optInt("pages");

        if (pages > 1) {

            int postid = ary_postsData.get(mPosition).id;

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUsersUpvoted(this, mContext, access_token, String.valueOf(postid),1, total);
            category_success = SUCCESS_GET_USERS_UPVOTED;

            return;
        }

        if (users == null || users.length() == 0) return;

        try {

            for (int i = 0; i < users.length(); i++) {

                UpvotedUsersModel upvotedUsersModel = new UpvotedUsersModel();
                JSONObject userObj = users.getJSONObject(i);
                upvotedUsersModel.amount = userObj.optInt(Constants.AMOUNT);
                upvotedUsersModel.re_upvoteAmount = 0;

                JSONObject object = userObj.optJSONObject(Constants.USER);

                if (object != null) {
                    upvotedUsersModel.userModel = ParseUtility.userModel(object);
                    ary_usersUpvoted.add(upvotedUsersModel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ary_usersUpvoted.size() > 0) {

            boolean ismyPost = false;
            if (ary_postsData.get(mPosition).user.username.equals(Utility.getReadPref(mContext).getStringValue(Constants.USERNAME))) ismyPost = true;

            upvotedUserPopupview = UpvotedUsersBottomSheetDialog.getInstance(mContext);
            upvotedUserPopupview.setCanceledOnTouchOutside(true);
            upvotedUserPopupview.setData(ary_usersUpvoted, ismyPost);
            upvotedUserPopupview.show();

//            Utility.showUpvotedUsersDialog(mContext, ary_usersUpvoted, ismyPost);
//            upvotedUserPopupview = new UpvotedUserPopupview(view_subPopup.getContext(), ary_usersUpvoted, this);
//            upvotedUserPopupview.setWidth(Utility.g_deviceWidth - Utility.dpToPx(40));
//            upvotedUserPopupview.setHeight(Utility.dpToPx(90));
//            upvotedUserPopupview.showOnAnchor(view_subPopup, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.CENTER, false);
        }

    }

    private void sendUpvote(int position) {

//        showHUD();

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        category_success = Constants.REQUEST_SEND_UPVOTE;

        if (Constants.isLifeToken(mContext)) {

            String balancesPGB = Utility.getReadPref(mContext).getStringValue("value_pgb");

            if (relative_upvoted_done != null && mUpvotedDoneDialogParams != null && balancesPGB != null && (int) Float.parseFloat(balancesPGB) > 10 && !postsModel.up_voted) {

                String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().sendingUpvote(this, mContext, postsModel.id, 10, token);

                int oldAmount = postsModel.up_votes_amount;
                postsModel.up_voted = true;
                postsModel.up_votes_amount = oldAmount + 10;

                ary_postsData.set(mPosition, postsModel);
                if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();

                UpvotedDoneDialog dialog = new UpvotedDoneDialog(mContext);
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
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void checkPhoneVerifyStatus() {

        AlertHorizentalDialog dialog = new AlertHorizentalDialog(mContext, null,
                getString(R.string.msg_not_verified_phone),
                getString(R.string.cancel), getString(R.string.ok), R.color.colorMain, R.color.colorMain) {

            @Override
            public void onClickButton(int position) {

                if (position == 1) {

                    Intent intent = new Intent(mContext, PhoneVerificationActivity.class);
                    intent.putExtra(PhoneVerificationActivity.ACTIVITY_TYPE, Constants.TYPE_CHECK_PHONEVERIFY);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                }

                dismiss();
            }

        };
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //--------block post----------

    private void blockFeed(int position) {

        PostsModel postsModel = ary_postsData.get(position);

        category_success = Constants.REQUEST_BLOCK_POST;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().blockFeed(this, mContext, token, postsModel.id);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    //-------remove post after mute user--------

    private void mutedUserPostRemove() {

        int userid = ary_postsData.get(mPosition).user.id;

        ActionStatusDialog dialog = new ActionStatusDialog(mContext, getString(R.string.muted));
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                int itemCount = ary_postsData.size();

                for (int i = itemCount - 1; i >=0; i--) {

                    if (ary_postsData.get(i).user.id == userid) {
                        ary_postsData.remove(i);
                        ary_aniCommented.remove(i);
                    }

                }

                if (ary_postsData.size() < 10) {
                    ary_postsData.clear();
                    pageCount = 1;
                    checkingAndGetposts(pageCount);
                } else {
                    pageCount = (int) (ary_postsData.size() / 10);
                    if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
                }
            }
        }, 1000);
    }

    private void requestRefreshToken() {
        Constants.requestRefreshToken(mContext, this);
    }

    //-------- requestListener---------

    @Override
    public void succeeded(JSONObject objResult) {

        hideHUD();

        pullRefreshLayout.setRefreshing(false);

        if (!Utility.g_isCalledRefreshToken) {

            switch (category_success) {

                case SUCCESS_GET_ALLPOSTS:

                    gettingPostsArray(objResult);

                    break;

                case SUCCESS_SEND_FAVORITE:

                    PostsModel temp_post = ary_postsData.get(mPosition);

                    if (temp_post.favorites) {

                        temp_post.favorites = false;
                        ary_postsData.set(mPosition, temp_post);

                    } else {

                        temp_post.favorites = true;
                        ary_postsData.set(mPosition, temp_post);
                    }

                    if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();

                    break;
                case SUCCESS_GET_UPVOTE:

                    showWalletInfo(objResult);

                    break;
                case SUCCESS_REFRESH:

                    gettingRefreshPostsArray(objResult);

                    break;
                case SUCCESS_GET_USERS_UPVOTED:

                    getUsersUpvoted(objResult);

                    break;
                case Constants.REQUEST_GET_POST_FROM_ID:

                    changedSelectedPost(objResult);

                    getUnreadAllMessage(ONRESUME_GET_UNREAD_MESSAGE_COUNT);

                    break;
                case ONRESUME_GET_UNREAD_MESSAGE_COUNT:

                    initBadgeNumber(objResult);

                    if (Utility.g_userInfoChanged) {

                        pullRefreshLayout.setRefreshing(true);
                        getRefreshPosts();

                        Utility.g_userInfoChanged = false;
                    }

                    break;
                case ONCREATE_GET_UNREAD_MESSAGE_COUNT:

                    initBadgeNumber(objResult);

                    checkingAndGetposts(1);

                    break;
                case Constants.REQUEST_USER_MUTE:

                    mutedUserPostRemove();

                    break;

                case Constants.REQUEST_DELETE_POST: {

                    ActionStatusDialog dialog = new ActionStatusDialog(mContext, getString(R.string.deleted));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ary_postsData.remove(mPosition);
                            ary_aniCommented.remove(mPosition);
                            if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
                        }
                    }, 1000);

                    break;
                }
                case Constants.REQUEST_BLOCK_POST: {

                    ActionStatusDialog dialog = new ActionStatusDialog(mContext, getString(R.string.blocked));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ary_postsData.remove(mPosition);
                            ary_aniCommented.remove(mPosition);
                            if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
                        }
                    }, 1000);

                    break;
                }
            }

        } else {

            Constants.saveRefreshToken(mContext, objResult);

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            switch (category_success) {

                case SUCCESS_REFRESH:

                    ServerRequest.getSharedServerRequest().getAllPosts(this, mContext, severURL, access_token, "1", "10");

                    break;
                case SUCCESS_GET_ALLPOSTS:

                    pageCount = 1;
                    checkingAndGetposts(pageCount);

                    break;
                case SUCCESS_GET_UPVOTE:

                    getUpvoteData();

                    break;
                case Constants.REQUEST_GET_POST_FROM_ID:

                    getPostsFromID(ary_postsData.get(mPosition).id);

                    break;
                case ONCREATE_GET_UNREAD_MESSAGE_COUNT:
                case ONRESUME_GET_UNREAD_MESSAGE_COUNT:

                    getUnreadAllMessage(category_success);

                    break;
                case Constants.REQUEST_DELETE_POST:

                    deleteMedia();

                    break;
                case Constants.REQUEST_USER_MUTE:

                    userMute(mPosition);

                    break;
                case Constants.REQUEST_SEND_UPVOTE:

                    sendUpvote(mPosition);

                    break;
                case Constants.REQUEST_BLOCK_POST:

                    blockFeed(mPosition);

                    break;
            }
        }
    }

    // getting promo posts
    @Override
    public void success(JSONObject objResult) {

        int count = ary_postsData.size();

        JSONArray items = objResult.optJSONArray("items");

        if (items != null && items.length() > 0) {

            for (int i = 0; i < items.length(); i++) {

                try {

                    JSONObject jsonObject = (JSONObject) items.get(i);
                    PostsModel postsModel = ParseUtility.postModel(jsonObject);

                    if (postsModel.ary_media != null && postsModel.ary_media.size() > 0) {

                        for (int j = 0; j < postsModel.ary_media.size(); j++) {

                            if (j == postsModel.ary_media.size() - 1) {

                                int index = 10 * (pageCount - 1) + 5 * (i + 1);

                                if (count > index) {

                                    ary_postsData.add(index, postsModel);
                                    ary_aniCommented.add(0);

                                }
                                //------ for comment animaiton-----

                                for (int k = 0; k < ary_postsData.size(); k++) {
                                    PostsModel temp = ary_postsData.get(k);
                                    if (ary_aniCommented.get(k) == 1) {
                                        temp.isAnimated = true;
                                        ary_postsData.set(k, temp);
                                    }
                                }
                            }

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

//                                        if (finalJ == postsModel.ary_media.size() - 1) {
//
//                                            if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
//
//                                        }
                                    }
                                });

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();

        flag_loading = false;
    }

    @Override
    public void failed(String strError) {

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

        if (Utility.g_isCalledRefreshToken) {

//            Utility.g_isCalledRefreshToken = false;

            AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.oh_snap),
                    getString(R.string.network_error_refreshtoken),
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {

                    requestRefreshToken();

                    dismiss();
                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else {

            if (errorCode == 403) {

                checkPhoneVerifyStatus();

            } else {

                Utility.parseError((AppCompatActivity) mContext, strError);

                if (category_success.equals(Constants.REQUEST_SEND_UPVOTE)) {

                    PostsModel postsModel = ary_postsData.get(mPosition);

                    int oldAmount = postsModel.up_votes_amount;
                    postsModel.up_voted = false;
                    if (oldAmount >= 10) postsModel.up_votes_amount = oldAmount - 10;

                    ary_postsData.set(mPosition, postsModel);
                    if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
                }

            }
        }

        pullRefreshLayout.setRefreshing(false);

        if (category_success.equals(SUCCESS_GET_ALLPOSTS)) {
            if (flag_loading) {
                flag_loading = false;
                pageCount = pageCount - 1;
            }
        }
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

        if (category_success.equals(SUCCESS_GET_WALLET)) getBalanceInfo(objResult);
    }

    @Override
    public void onClick(View v) {

        if (v == btn_gotoWallet) {

            if (flag_walletAnimation) {

                hideWallet();

            } else {

                getUpvoteData();

                linear_wallet.setVisibility(View.VISIBLE);
                btn_Wallet.setEnabled(true);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_scale);
                animation.setDuration(200);
                linear_wallet.setAnimation(animation);
                linear_wallet.animate();
                animation.start();

                flag_walletAnimation = true;
            }

        } else if (v == btn_gotoMessage) {

            if ((DashboardActivity)getActivity() != null) ((DashboardActivity)getActivity()).gotoMessage();

        } else if (v == btn_goto_playroom) {

            Intent intent = new Intent(getContext(), PlayroomActivity.class);

            intent.putExtra(Constants.EMAIL, Utility.getReadPref(mContext).getStringValue(Constants.EMAIL));
            intent.putExtra(Constants.PLACE, 1);

            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == btn_goto_gifticon) {

            Intent intent = new Intent(mContext, GiftHomeActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == btn_Wallet) {

            btn_Wallet.setEnabled(false);

            hideWallet();

            if (CheckUpdate.check(mContext)) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.update_required),
                        getString(R.string.update_required_description),
                        null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                    @Override
                    public void onClickButton(int position) {

                        if (position == 1) {
                            try {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.star.pibbledev")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id"+"com.star.pibbledev")));
                            }
                        }

                        dismiss();

                    }

                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            } else {

                String limitTime = Utility.getReadPref(mContext).getStringValue(Constants.RESET_PINCODE_FAILED_LIMIT);

                if (limitTime != null && limitTime.length() > 0 && Utility.isMoreThanMins(limitTime, 5)) {

                    AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.oh_snap),
                            getString(R.string.error_goto_wallet),
                            null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                        @Override
                        public void onClickButton(int position) {

                            dismiss();

                        }

                    };
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                } else {

                    Utility.getSavedPref(mContext).saveString(Constants.RESET_PINCODE_FAILED_LIMIT, "");

                    Intent intent = new Intent(mContext, RegisterPinActivity.class);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }

        } else if (v == linear_progress_tab) {

            pullRefreshLayout.setRefreshing(true);
            getRefreshPosts();
            PostMediaHelper.sCachePost.clear();

            Utility.mainFeedParams = null;

            linear_progress.setVisibility(View.GONE);
            linear_progress_tab.setVisibility(View.GONE);

        }
    }

    private void hideWallet() {

        if (flag_walletAnimation) {

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.layout_scale_zoom);
            animation.setDuration(200);
            linear_wallet.setAnimation(animation);
            linear_wallet.animate();
            animation.start();
            linear_wallet.setVisibility(View.GONE);
            btn_Wallet.setEnabled(false);

            flag_walletAnimation = false;
        }
    }

    //----------- MainFeedListener--------------

    @Override
    public void onClickSendingComment(String string, int postion) {

//        showHUD();

        addComment(string, postion);

        if (Constants.isLifeToken(mContext)) {

            category_success = SUCCESS_SEND_COMMENT;
            mPosition = postion;

            PostsModel postsModel = ary_postsData.get(postion);

            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_COMMENT);
            Utility.saveImpressionToSql(mContext, impressionModel);

            Utility.dismissKeyboard((Activity) mContext);
            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().createComment(this, mContext, postsModel.id, string, access_token);

        } else {
            Constants.requestRefreshToken(mContext, this);
            category_success = "";
        }
    }

    private void addComment(String body, int position) {

        CommentsModel commentsModel = new CommentsModel();

        commentsModel.body = body;
        commentsModel.created_at = Utility.getCurrentDate();
        commentsModel.up_voted = false;

        commentsModel.userModel = new UserModel();
        commentsModel.userModel.id = Utility.getReadPref(mContext).getIntValue(Constants.ID);

        commentsModel.userModel.username = Utility.getReadPref(mContext).getStringValue(Constants.USERNAME);
        commentsModel.userModel.avatar = Utility.getReadPref(mContext).getStringValue(Constants.AVATAR);
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

        if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickViewAllComment(int position, boolean flag) {
        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(mContext, impressionModel);

        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(CommentActivity.POST_ID, postsModel.id);
        intent.putExtra(CommentActivity.PROMO_ID, promo_id);
        intent.putExtra(CommentActivity.POST_DESCRIPTION, postsModel.caption);
        intent.putExtra(CommentActivity.POST_USER_AVATAR, postsModel.user.avatar);
        intent.putExtra(CommentActivity.POST_USER_NAME, postsModel.user.username);
        intent.putExtra(CommentActivity.POST_CREATED, postsModel.created_at);
        intent.putExtra(CommentActivity.KEY_BOARD_STATUS, flag);
        intent.putExtra(CommentActivity.POST_USER_AVATAR_TEMP, postsModel.user.avatar_temp);
        intent.putExtra(CommentActivity.POST_COMMENT_COUNT, postsModel.comments_count);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onVoted(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(mContext, impressionModel);

        Intent intent = new Intent(mContext, AddUpvoteActivity.class);
        intent.putExtra(AddUpvoteActivity.POST_ID, String.valueOf(postsModel.id));
        intent.putExtra(AddUpvoteActivity.PROMO_ID, promo_id);
        intent.putExtra(AddUpvoteActivity.IS_UPVOTED, postsModel.up_voted);
        intent.putExtra(AddUpvoteActivity.TARGET, TARGET_UPVOTE);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onFavorite(boolean favorit, int position) {

        showHUD();

        if (Constants.isLifeToken(mContext)) {

            PostsModel postsModel = ary_postsData.get(position);

            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
            Utility.saveImpressionToSql(mContext, impressionModel);

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            category_success = SUCCESS_SEND_FAVORITE;

            mPosition = position;

            if (favorit) {

                ServerRequest.getSharedServerRequest().deselectFavorit(this, mContext, postsModel.id, access_token);

            } else {

                ServerRequest.getSharedServerRequest().selectFavorit(this, mContext, postsModel.id, access_token);

            }

        } else {
            Constants.requestRefreshToken(mContext, this);
            category_success = "";
        }
    }

    @Override
    public void onFollow(int position) {

        showHUD();

        if (Constants.isLifeToken(mContext)) {

            PostsModel postsModel = ary_postsData.get(position);

            int promo_id = -1;
            if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_FOLLOWING);
            Utility.saveImpressionToSql(mContext, impressionModel);

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            category_success = SUCCESS_FOLLOW;
            mPosition = position;

            if (postsModel.user.interaction_status != null && postsModel.user.interaction_status.is_following) {
                ServerRequest.getSharedServerRequest().selectUnFollow(this, mContext, postsModel.user.username, access_token);

                postsModel.user.interaction_status.is_following = false;
                ary_postsData.set(position, postsModel);
                if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
            } else {
                ServerRequest.getSharedServerRequest().selectFollow(this, mContext, postsModel.user.username, access_token);

                postsModel.user.interaction_status.is_following = true;
                ary_postsData.set(position, postsModel);
                if (postsListAdapter != null) postsListAdapter.notifyDataSetChanged();
            }

        } else {
            Constants.requestRefreshToken(mContext, this);
            category_success = "";
        }

    }

    @Override
    public void createPromotion(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

        Utility.showCreatePromotionDialog(mContext, postsModel);

    }

    @Override
    public void gotoPromotionDetail(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.promotionModel == null) return;

        if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, postsModel.promotionModel.id, 0, Constants.METRICS_ACTION_BUTTON);
            Utility.saveImpressionToSql(mContext, impressionModel);

//            Utility.openBrowser(mContext, postsModel.promotionModel.site_url);
            Utility.showWebviewDialog(mContext, postsModel.promotionModel.site_url);

        } else {

            ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, postsModel.promotionModel.id, 0, Constants.METRICS_PROFILE_VIEW);
            Utility.saveImpressionToSql(mContext, impressionModel);

            Intent intent = new Intent(mContext, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.SELECT_USERNAME, postsModel.user.username);
            intent.putExtra(UserProfileActivity.SELECT_USERID, postsModel.user.id);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    @Override
    public void showFundingStatus(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

//        Utility.showFundingDetailDialog(mContext, postsModel, false);
        fundingDetailBottomSheetDialog = FundingDetailBottomSheetDialog.getInstance(mContext);
        fundingDetailBottomSheetDialog.setData(postsModel, false);
        fundingDetailBottomSheetDialog.setCanceledOnTouchOutside(true);
        fundingDetailBottomSheetDialog.show();
    }

    @Override
    public void showAskingHelp(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.askingHelpModel != null) {

            Intent intent = new Intent(getActivity(), AskingHelpListActivity.class);
            intent.putExtra(Constants.ID, postsModel.askingHelpModel.id);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {

            askingHelpBottomSheetDialog = AskingHelpBottomSheetDialog.getInstance(mContext);
            askingHelpBottomSheetDialog.setCanceledOnTouchOutside(true);
            askingHelpBottomSheetDialog.setData(postsModel.id);
            askingHelpBottomSheetDialog.show();

        }
    }

    @Override
    public void onRunedAnimationComment(int position) {

        ary_aniCommented.set(position, 1);
        postsListAdapter.notifyDataSetChanged();
    }

    public void onHideActionBar() {
        if (mAlertView != null && mAlertView.isShowing()) mAlertView.dismiss();
    }

    @Override
    public void onShowActionBar(int position) {

        Utility.g_isShowingTabbar = true;

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
        Utility.saveImpressionToSql(mContext, impressionModel);

        if (postsModel.user.id == Utility.getReadPref(mContext).getIntValue(Constants.ID)) {

            mAlertView = new AlertView(null, null, getString(R.string.cancel), new String[]{getString(R.string.edit), getString(R.string.edit_tag), getString(R.string.share), getString(R.string.view_engage)},
                    new String[]{getString(R.string.delete)},
                    mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                public void onItemClick(Object o,int position){

                    Utility.g_isShowingTabbar = false;

                    switch (position) {
                        case 0: {
                            Intent intent = new Intent(getActivity(), MediaCaptionEditActivity.class);
                            intent.putExtra(MediaCaptionEditActivity.SELECTED_POST, postsModel.id);
                            startActivity(intent);
                            ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                        break;
                        case 1: {
                            Utility.mainFeedParams = new MainFeedParams();
                            Utility.mainFeedParams.ary_tags = postsModel.tags;

                            Intent intent = new Intent(getActivity(), MainFeedTagsActivity.class);
                            intent.putExtra(MainFeedTagsActivity.ACTIVITY_TYPE, TAG);
                            intent.putExtra(Constants.ID, postsModel.id);
                            startActivity(intent);
                            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                        }
                        break;
                        case 2:
                            String shareURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);
                            Utility.shareTextAction(mContext, shareURL, getString(R.string.pibble), getString(R.string.share_via));
                            break;
                        case 3:
                            Utility.showPromotionEngageDetailDialog(mContext, postsModel);
                            break;
                        case 4: {

                            int upvoteAmount = ary_postsData.get(mPosition).up_votes_amount;
                            int userPRB = (int) Float.parseFloat(ary_postsData.get(mPosition).user.available_PRB);
                            int consum = upvoteAmount / 2;

                            if (userPRB < consum) {

                                String message = getString(R.string.already_upvoted_msg) + String.valueOf(consum) +" " + getString(R.string.prb);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        new AlertView(null, message, null, new String[]{getString(R.string.cancel)},
                                                null,
                                                mContext, AlertView.Style.Alert, new OnItemClickListener(){
                                            public void onItemClick(Object o,int position){

                                            }

                                        }).show();

                                    }
                                }, Constants.ALERTVIEW_DELAY);

                            } else {

                                String message = null;

                                if (consum != 0) {

                                    message = getString(R.string.consume) + " " + String.valueOf(consum) +" " + getString(R.string.prb);

                                }

                                String finalMessage = message;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        new AlertView(getString(R.string.delete_post), finalMessage, null, new String[]{getString(R.string.cancel), getString(R.string.delete)},
                                                null,
                                                mContext, AlertView.Style.Alert, new OnItemClickListener(){
                                            public void onItemClick(Object o,int position){

                                                if (position == 1) {
                                                    deleteMedia();
                                                }
                                            }

                                        }).show();

                                    }
                                }, Constants.ALERTVIEW_DELAY);

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
                    mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                public void onItemClick(Object o,int position){

                    Utility.g_isShowingTabbar = false;

                    switch (position) {
                        case 0:

                            String shareURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);
                            Utility.shareTextAction(mContext, shareURL, getString(R.string.pibble), getString(R.string.share_via));

                            break;

                        case 1:

                            String copyURL = String.format("https://social.pibble.app/share.html?postid=%s", postsModel.uuid);

                            ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("url", copyURL);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            }

                            break;

                        case 2:

                            break;

                        case 3:

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reportAction(mPosition);
                                }
                            }, Constants.ALERTVIEW_DELAY);

                            break;

                        case 4:

                            String title = getString(R.string.mute) + " " + ary_postsData.get(mPosition).user.username + " ?";
                            String message = getString(R.string.unmute_description);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    new AlertView(title, message, null, new String[]{getString(R.string.cancel), getString(R.string.mute_posts)},
                                            null,
                                            mContext, AlertView.Style.Alert, new OnItemClickListener(){
                                        public void onItemClick(Object o,int position){

                                            if (position == 1) {
                                                userMute(mPosition);
                                            }
                                        }

                                    }).show();

                                }
                            }, Constants.ALERTVIEW_DELAY);

                            break;
                    }
                }
            });

            mAlertView.show();

        }

    }

    private void reportAction(int position) {

        mPosition = position;

        if (mAlertView != null) mAlertView = null;

        mAlertView = new AlertView(null, null, getString(R.string.cancel), null,
                new String[]{getString(R.string.report_as_inappropriate), getString(R.string.block)},
                mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){

            public void onItemClick(Object o,int position){

                Utility.g_isShowingTabbar = false;

                switch (position) {

                    case 0:

                        Intent intent = new Intent(mContext, ReportReasonSelectActivity.class);
                        intent.putExtra(ReportReasonSelectActivity.TAG, ary_postsData.get(mPosition).id);
                        startActivity(intent);
                        ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                        break;

                    case 1:

                        AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.thanks_for_reporting_this_post),
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
    public void onShowingUserInfo(int position) {

        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_PROFILE_VIEW);
        Utility.saveImpressionToSql(mContext, impressionModel);

        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, postsModel.user.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, postsModel.user.id);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onShowingUserInfoWithUserinfo(UserModel userModel) {

        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, userModel.id);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onClickTag(String string, int position) {

        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_TAG);
        Utility.saveImpressionToSql(mContext, impressionModel);

        Intent intent = new Intent(mContext, DiscoverItemDetailActivity.class);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TITLE, Utility.getStringWithoutFirstCharater(string));
        intent.putExtra(DiscoverItemDetailActivity.ITEM_POSTS, "");
        intent.putExtra(DiscoverItemDetailActivity.ITEM_POST_ID, ary_postsData.get(position).id);
        intent.putExtra(DiscoverItemDetailActivity.ITEM_TYPE, Constants.DISCOVER_TAG_TEXT);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onLongClickImageView(int position, int mediapos) {

        PostsModel postsModel = ary_postsData.get(position);
        String media_url;
        boolean isPurchased, isRotate;

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_COLLECT);
        Utility.saveImpressionToSql(mContext, impressionModel);

        if (postsModel.invoiceModel != null) {

            if (postsModel.invoiceModel.status.equals(Constants.INVOICE_ACCEPTED)) {
                media_url = postsModel.ary_media.get(mediapos).s3ld;
                isPurchased = true;
            } else {
                media_url = postsModel.ary_media.get(mediapos).url;
                isPurchased = false;
            }

        } else {

            if (postsModel.user.id == Utility.getReadPref(mContext).getIntValue(Constants.ID)) {

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

            Intent intent = new Intent(mContext, DisplayingImageActivity.class);
            intent.putExtra(DisplayingImageActivity.IS_PURCHASED, isPurchased);
            intent.putExtra(DisplayingImageActivity.IS_ROTATE, isRotate);
            intent.putExtra(DisplayingImageActivity.IMAGE_PATH, media_url);

            intent.putExtra(DisplayingImageActivity.THUMB_PATH, postsModel.ary_media.get(mediapos).thumbnail);

            if (postsModel.ary_media.get(mediapos).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                intent.putExtra(DisplayingImageActivity.MIME_TYPE, true);
            } else {
                intent.putExtra(DisplayingImageActivity.MIME_TYPE, false);
            }
            intent.putExtra(DisplayingImageActivity.MEDIA_WIDTH, postsModel.ary_media.get(mediapos).width);
            intent.putExtra(DisplayingImageActivity.MEDIA_HEIGHT, postsModel.ary_media.get(mediapos).height);

            intent.putExtra(DisplayingImageActivity.ORIGINAL_WIDTH, postsModel.ary_media.get(mediapos).original_width);
            intent.putExtra(DisplayingImageActivity.ORIGINAL_HEIGHT, postsModel.ary_media.get(mediapos).original_height);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    @Override
    public void onDoubleClickMediaView(RelativeLayout relativeLayout, LinearLayout.LayoutParams params, int position) {

        PostsModel postsModel = ary_postsData.get(position);

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        int pgbValue = (int)Float.parseFloat(Utility.getReadPref(mContext).getStringValue("value_pgb"));

        if (pgbValue < 10) return;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 10, Constants.METRICS_UP_VOTE);
        Utility.saveImpressionToSql(mContext, impressionModel);

        relative_upvoted_done = relativeLayout;
        mUpvotedDoneDialogParams = params;

        sendUpvote(position);
    }

    @Override
    public void onClickPlace(int position) {

        mPosition = position;
        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.placeModel == null) return;

        if (postsModel.placeModel.lng.equals("") || postsModel.placeModel.lng.equals("null")) return;
        if (postsModel.placeModel.lat.equals("") || postsModel.placeModel.lat.equals("null")) return;

        Intent intent = new Intent(mContext, PlaceDetailsActivity.class);
        intent.putExtra(PlaceDetailsActivity.PLACE_TITLE, postsModel.placeModel.description);
        intent.putExtra(PlaceDetailsActivity.PLACE_LAT, postsModel.placeModel.lat);
        intent.putExtra(PlaceDetailsActivity.PLACE_LNG, postsModel.placeModel.lng);

        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void OnClickDigitalGoods(int position, boolean isSeller) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);
        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(mContext, impressionModel);

        if (isSeller) {

            String title = postsModel.commerceModel.name;
            @SuppressLint("DefaultLocale") String detail = String.format("%s %d %s, %s %.2f %s", getString(R.string.price), postsModel.commerceModel.price, getString(R.string.pib), getString(R.string.reward), postsModel.commerceModel.price * 0.15, getString(R.string.pib));

            if (postsModel.sales == 0) return;

            Intent intent = new Intent(mContext, RoomsInGroupActivity.class);
            intent.putExtra(RoomsInGroupActivity.POST_ID, postsModel.id);
            intent.putExtra(RoomsInGroupActivity.GOODS_TITLE, title);
            intent.putExtra(RoomsInGroupActivity.GOODS_DETAIL, detail);
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

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

                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
                    intent.putExtra(Constants.TYPE, Constants.ROOM_DIGITAL_GOODS);
                    intent.putExtra(Constants.POSTID, postsModel.id);
                    intent.putExtra(Constants.USERNAME, name);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                } else {

                    Utility.showDigitalGoodsDialog(mContext, imageLoader, postsModel);

                }

            } else {

                Utility.showDigitalGoodsDialog(mContext, imageLoader, postsModel);

            }
        }
    }

    @Override
    public void OnClickGoods(int position) {

        mPosition = position;

        PostsModel postsModel = ary_postsData.get(position);

        if (postsModel.user.id == Utility.getReadPref(mContext).getIntValue(Constants.ID)) {

            String title = postsModel.goodsModel.title;
            @SuppressLint("DefaultLocale") String detail = String.format("%s %s %s", getString(R.string.price), postsModel.goodsModel.price, getString(R.string.pib));

            Intent intent = new Intent(mContext, RoomsInGroupActivity.class);
            intent.putExtra(RoomsInGroupActivity.POST_ID, postsModel.id);
            intent.putExtra(RoomsInGroupActivity.GOODS_TITLE, title);
            intent.putExtra(RoomsInGroupActivity.GOODS_DETAIL, detail);
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else {

            if (postsModel.goodsModel.goods_order_id.equals("null")) {

                Utility.showGoodsDialog(mContext, imageLoader, postsModel);

            } else {

                if (postsModel.user.id != Utility.getReadPref(mContext).getIntValue(Constants.ID)) {

                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
                    intent.putExtra(Constants.TYPE, Constants.ROOM_GOODS);
                    intent.putExtra(Constants.POSTID, postsModel.id);
                    intent.putExtra(Constants.USERNAME, postsModel.user.username);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                }

            }
        }

    }

    @Override
    public void showUpvotedUserPopup(String position) {

        PostsModel postsModel = ary_postsData.get(Integer.parseInt(position));

        int promo_id = -1;
        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_ACTION_BUTTON);
        Utility.saveImpressionToSql(mContext, impressionModel);

        if (Constants.isLifeToken(mContext)) {

            mPosition = Integer.parseInt(position);

            String postid = String.valueOf(ary_postsData.get(mPosition).id);

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getUsersUpvoted(this, mContext, access_token, postid,1, 10);
            category_success = SUCCESS_GET_USERS_UPVOTED;

        } else {
            Constants.requestRefreshToken(mContext, this);
            category_success = "";
        }
    }

    @Override
    public void onClickUserUpvoted(int position) {

        upvotedUserPopupview.dismiss();

        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, ary_usersUpvoted.get(position).userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, ary_usersUpvoted.get(position).userModel.id);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onLongClickUserUpvoted(int position) {

        upvotedUserPopupview.dismiss();

        if (ary_postsData.get(mPosition).user.username.equals(Utility.getReadPref(mContext).getStringValue("username"))) {

            Intent intent = new Intent(mContext, AddUpvoteActivity.class);
            intent.putExtra(Constants.USERNAME, ary_usersUpvoted.get(position).userModel.username);
            intent.putExtra(RECEIVED_AMOUNT, ary_usersUpvoted.get(position).amount);
            intent.putExtra(Constants.TARGET, TARGET_USER_UPVOTE);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

    @Override
    public void hideWalletPreview() {
        hideWallet();
    }

    @Override
    public void onhideKeyboard() {

    }

    // TopGroun view Listener

    @Override
    public void goToLeaderBoard() {

        Intent intent = new Intent(mContext, LeaderBoardActivity.class);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void goToBannerDetail() {

        Intent intent = new Intent(mContext, BannerDetailActivity.class);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onClickNews(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getNews;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickCoin(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getCoin;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickHot(boolean isSelected) {

        if (isSelected) severURL = BackendAPI.post_getHot;
        else severURL = BackendAPI.post_getAll;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showHUD();
                getRefreshPosts();
            }
        }, 500);
    }

    @Override
    public void onClickWebtoon(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getWebtoon;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickNewbie(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getNewbie;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickFunding(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getFunding;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickPromote(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getPromoted;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }

    @Override
    public void onClickShopping(boolean isSelected) {

        showHUD();

        if (isSelected) severURL = BackendAPI.post_getShop;
        else severURL = BackendAPI.post_getAll;

        getRefreshPosts();
    }
}
