package com.star.pibbledev.home.editmedia;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.LocationModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.home.upvote.CreateDonateActivity;
import com.star.pibbledev.home.comments.CommentActivity;
import com.star.pibbledev.home.MainFeedParams;
import com.star.pibbledev.home.createmedia.location.MainFeedLocationActivity;
import com.star.pibbledev.home.upvote.AddUpvoteActivity;
import com.star.pibbledev.home.utility.videoview.CustomVideoView;
import com.star.pibbledev.home.utility.videoview.VideoView;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import co.lujun.androidtagview.TagContainerLayout;

public class MediaCaptionEditActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String ACTIVITY_TYPE = "activity_type";

    public static final String SELECTED_POST = "selectedPost";
    public static final String TARGET_UPVOTE = "post_upvote";
    public static final String SEND_COMMENT = "send_comment";
    public static final String GET_POST_DATA = "get_postdata";
    public static final String UPDATE_MEDIA_CAPTION = "update_media_caption";

    public static final String TAG = "MediaCaptionEditActivity";

    public static MainFeedParams mainFeedParams;

    private String REQUEST_TYPE;

    LinearLayout linear_cancel, linear_done, linear_remove, linear_change, linear_location_cancel;

    TextView txt_username, txt_user_total_won, txt_post_total_won, txt_level, txt_place, txt_votes, txt_media, txt_userEmo, txt_title , txt_left_date, txt_funding_action_type, txt_current_charity, txt_goal, txt_raised, txt_members
            , txt_commerce_name, txt_commerce_price, txt_commerce_status, txt_promotion, txt_promote, txt_promotion_statue, txt_commerce_error, txt_commerce_sales, txt_teamname, txt_help_reward;
    LinearLayout linear_place, linear_attr, linear_tags, linear_item_comment, linear_scroll, linear_charity, linear_show_charity_data,
            linear_charity_individual, linear_charity_teaminfo, linear_upvote, linear_actionbar, linear_commerce_warning, linear_digitalgoods, linear_promotion
            , linear_promote, linear_promotion_create, linear_all, linear_promote_engage, linear_show_charity, linear_post_total_won, linear_help, linear_help_reward;
    TagContainerLayout tagContainerLayout;
    Button btn_comment, btn_follow, btn_donate_charity;
    ImageButton imageButton_media, btn_favourit, btn_gotoallcomment;
    ImageView img_user, btn_votes, img_location, img_diamond;
    HorizontalScrollView scrollViewImage;
    FrameLayout frame_media_index, frag_process_background, frag_process_charity, frame_commerce_value;
    RelativeLayout relative_mainfeed;
    EditText txt_attr;
    ScrollView scrollview_all;

    ImageLoader imageLoader;

    PostsModel postsModel;

    boolean isLocationChanged = false;

    ArrayList<VideoView> mVideoViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_edit);

        setLightStatusBar();

        Utility.disKeyboardTouch(findViewById(R.id.linear_all), this);

        linear_done = (LinearLayout)findViewById(R.id.linear_done);
        linear_done.setOnClickListener(this);
        linear_cancel = (LinearLayout)findViewById(R.id.linear_cancel);
        linear_cancel.setOnClickListener(this);

        linear_remove = (LinearLayout)findViewById(R.id.linear_remove);
        linear_remove.setOnClickListener(this);
        linear_change = (LinearLayout)findViewById(R.id.linear_change);
        linear_change.setOnClickListener(this);
        linear_location_cancel = (LinearLayout)findViewById(R.id.linear_location_cancel);
        linear_location_cancel.setOnClickListener(this);

        linear_all = (LinearLayout)findViewById(R.id.linear_all);
        scrollview_all = (ScrollView)findViewById(R.id.scrollview_all);

        linear_scroll = (LinearLayout)findViewById(R.id.linear_scroll);
        linear_place = (LinearLayout)findViewById(R.id.linear_place);
        linear_place.setOnClickListener(this);
        linear_attr = (LinearLayout)findViewById(R.id.linear_attr);
        linear_tags = (LinearLayout)findViewById(R.id.linear_tags);
        linear_item_comment = (LinearLayout)findViewById(R.id.linear_item_comment);
        linear_charity = (LinearLayout)findViewById(R.id.linear_charity);
        linear_actionbar = (LinearLayout)findViewById(R.id.linear_actionbar);
        linear_post_total_won = (LinearLayout)findViewById(R.id.linear_post_total_won);
        linear_help = (LinearLayout)findViewById(R.id.linear_help);
        linear_help_reward = (LinearLayout)findViewById(R.id.linear_help_reward);
        txt_username = (TextView)findViewById(R.id.txt_username);
        txt_user_total_won = (TextView)findViewById(R.id.txt_user_total_won);
        txt_post_total_won = (TextView)findViewById(R.id.txt_post_total_won);
        txt_level = (TextView)findViewById(R.id.txt_level);
        txt_place = (TextView)findViewById(R.id.txt_place);
        txt_attr = (EditText) findViewById(R.id.txt_attr);
        txt_votes = (TextView)findViewById(R.id.txt_votes);
        txt_media = (TextView)findViewById(R.id.txt_media);
        txt_userEmo = (TextView)findViewById(R.id.txt_userEmo);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_members = (TextView)findViewById(R.id.txt_members);
        txt_help_reward = (TextView)findViewById(R.id.txt_help_reward);
        imageButton_media = (ImageButton)findViewById(R.id.img_media);
        btn_favourit = (ImageButton)findViewById(R.id.btn_favourit);
        btn_votes = (ImageView)findViewById(R.id.btn_votes);
        linear_upvote = (LinearLayout)findViewById(R.id.linear_upvote);
        tagContainerLayout = (TagContainerLayout)findViewById(R.id.tagcontainerLayout);
        btn_comment = (Button)findViewById(R.id.btn_comment);
        btn_follow = (Button)findViewById(R.id.btn_follow);
        img_user = (ImageView)findViewById(R.id.img_user);
        img_diamond = (ImageView)findViewById(R.id.img_diamond);
        img_location = (ImageView)findViewById(R.id.img_location);
        scrollViewImage = (HorizontalScrollView)findViewById(R.id.scrollViewImage);
        frame_media_index = (FrameLayout)findViewById(R.id.frame_media_index);
        relative_mainfeed = (RelativeLayout)findViewById(R.id.relative_mainfeed);
        frag_process_charity = (FrameLayout) findViewById(R.id.frag_process_charity);
        btn_gotoallcomment = (ImageButton)findViewById(R.id.btn_gotoallcomment);
        btn_donate_charity = (Button)findViewById(R.id.btn_donate_charity);
        txt_current_charity = (TextView)findViewById(R.id.txt_current_charity);
        txt_left_date = (TextView)findViewById(R.id.txt_left_date);
        txt_funding_action_type = (TextView)findViewById(R.id.txt_funding_action_type);
        txt_goal = (TextView)findViewById(R.id.txt_goal);
        frag_process_background = (FrameLayout)findViewById(R.id.frag_process_background);
        txt_raised = (TextView)findViewById(R.id.txt_raised);
        linear_show_charity = (LinearLayout) findViewById(R.id.linear_show_charity);
        linear_show_charity_data = (LinearLayout)findViewById(R.id.linear_show_charity_data);
        linear_charity_teaminfo = (LinearLayout)findViewById(R.id.linear_charity_teaminfo);
        linear_charity_individual = (LinearLayout)findViewById(R.id.linear_charity_individual);
        txt_teamname = (TextView)findViewById(R.id.txt_teamname);
        linear_commerce_warning = (LinearLayout)findViewById(R.id.linear_commerce_warning);
        linear_digitalgoods = (LinearLayout)findViewById(R.id.linear_digitalgoods);
        txt_commerce_name = (TextView)findViewById(R.id.txt_commerce_name);
        txt_commerce_price = (TextView)findViewById(R.id.txt_commerce_price);
        txt_commerce_status = (TextView)findViewById(R.id.txt_commerce_status);
        frame_commerce_value = (FrameLayout)findViewById(R.id.frame_commerce_value);
        linear_promotion = (LinearLayout)findViewById(R.id.linear_promotion);
        linear_promote = (LinearLayout)findViewById(R.id.linear_promote);
        linear_promote_engage = (LinearLayout)findViewById(R.id.linear_promote_engage);
        linear_promotion_create = (LinearLayout)findViewById(R.id.linear_promotion_create);
        txt_promotion = (TextView)findViewById(R.id.txt_promotion);
        txt_promotion_statue = (TextView)findViewById(R.id.txt_promotion_statue);
        txt_promotion_statue.setVisibility(View.GONE);
        txt_promote = (TextView)findViewById(R.id.txt_promote);
        txt_commerce_error = (TextView)findViewById(R.id.txt_commerce_error);
        txt_commerce_sales = (TextView)findViewById(R.id.txt_commerce_sales);

        postsModel = new PostsModel();

        mainFeedParams = new MainFeedParams();

        imageLoader = ImageLoader.getInstance();

        txt_attr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isLocationChanged) {
                    if (s.toString().equals(postsModel.caption)) {
                        linear_done.setAlpha(0.5f);
                        linear_done.setEnabled(false);
                    } else {
                        linear_done.setAlpha(1.0f);
                        linear_done.setEnabled(true);
                    }
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        linear_done.setEnabled(false);
        linear_done.setAlpha(0.5f);

        linear_all.setVisibility(View.INVISIBLE);

        requestPostData();
        Utility.g_isChanged = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mainFeedParams.locationModel.description != null && mainFeedParams.locationModel.description.length() > 0) {
            txt_place.setText(mainFeedParams.locationModel.description);
            linear_done.setEnabled(true);
            linear_done.setAlpha(1.0f);

            isLocationChanged = true;
        }
//        requestPostData();
//        Utility.g_changedType = "";
    }

    @Override
    public void onStop() {

        super.onStop();
        if (mVideoViews != null && mVideoViews.size() > 0) {

            for (int i = 0; i < mVideoViews.size(); i++) {
                VideoView videoView = mVideoViews.get(i);
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                }
            }
        }
    }



    private void requestPostData() {

        REQUEST_TYPE = GET_POST_DATA;

        if (Constants.isLifeToken(this)) {

            int postid = getIntent().getIntExtra(SELECTED_POST, 0);
            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPostFromid(this, this, accessToken, postid);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void updateMedia() {

        REQUEST_TYPE = UPDATE_MEDIA_CAPTION;

        if (Constants.isLifeToken(this)) {

            int postid = getIntent().getIntExtra(SELECTED_POST, 0);
            String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            ServerRequest.getSharedServerRequest().updateMedia(this, this, String.valueOf(postid), mainFeedParams, accessToken);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void initView(PostsModel postsModel) {

        linear_item_comment.removeAllViews();
        linear_scroll.removeAllViews();

        linear_show_charity_data.setVisibility(View.GONE);

        if (postsModel.fundingModel != null) {

            linear_charity.setVisibility(View.VISIBLE);

            txt_title.setText(postsModel.fundingModel.campaignModel.title);
            int leftDays = Utility.getDates(Utility.getCurrentDate(), postsModel.fundingModel.end_date).size();
            txt_left_date.setText(leftDays + " " + getString(R.string.days_left));

            switch (postsModel.type) {

                case Constants.FUNDING_CHARITY:

                    txt_funding_action_type.setText(getString(R.string.donate_uppearcase));

                    break;
                case Constants.FUNDING_CROWD_NO_REWARD:

                    txt_funding_action_type.setText(getString(R.string.contribute_uppercase));

                    break;
                case Constants.FUNDING_CROWD_REWARD:

                    txt_funding_action_type.setText(getString(R.string.pledge_uppercase));

                    break;
            }

            linear_show_charity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Utility.showFundingDetailDialog(MediaCaptionEditActivity.this, postsModel, false);
                }
            });

        } else {

            linear_charity.setVisibility(View.GONE);
        }

//        if (postsModel.type.equals(Constants.FUNDING)) {
//
//            linear_charity.setVisibility(View.VISIBLE);
//
//            if (postsModel.fundraise_as.equals(Constants.TEAM)) {
//
//                txt_teamname.setText(postsModel.fundingModel.teamModel.name);
//                txt_members.setText(String.valueOf(postsModel.fundingModel.teamModel.members_count));
//
//            }
//
//            txt_title.setText(postsModel.fundingModel.campaignModel.title);
//            txt_current_charity.setText(String.valueOf(postsModel.fundingModel.campaignModel.raised)  + " " + getString(R.string.pib));
//            txt_goal.setText(String.valueOf(String.valueOf(postsModel.fundingModel.campaignModel.goal)) + " " + getString(R.string.pib));
//            txt_raised.setText(String.valueOf(processLenth(postsModel.fundingModel.campaignModel.goal, postsModel.fundingModel.campaignModel.raised)) + " %");
//
//            float processLenth = processLenth(postsModel.fundingModel.campaignModel.goal, postsModel.fundingModel.campaignModel.raised) / 100;
//
//            int totalWidth = (int)(Utility.g_deviceWidth * 0.65) - Utility.dpToPx(10);
//            int prcesswidth = (int)(processLenth * totalWidth);
//
//            if (processLenth >= 1) {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                frag_process_charity.setLayoutParams(params);
//            } else {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(prcesswidth, FrameLayout.LayoutParams.MATCH_PARENT);
//                frag_process_charity.setLayoutParams(params);
//            }
//
//        } else {
//            linear_charity.setVisibility(View.GONE);
//        }

        final int userid = Utility.getReadPref(this).getIntValue(Constants.ID);

//        btn_donate_charity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (postsModel.user.id == userid) return;
//
//                Intent intent = new Intent(MediaCaptionEditActivity.this, CreateDonateActivity.class);
//                intent.putExtra("id", String.valueOf(postsModel.id));
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//
//            }
//        });
//
//        linear_show_charity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int height = 0;
//
//                if (postsModel.fundraise_as.equals(Constants.TEAM)) {
//                    height = 120;
//                    linear_charity_teaminfo.setVisibility(View.VISIBLE);
//                } else {
//                    height = 60;
//                    linear_charity_teaminfo.setVisibility(View.GONE);
//                }
//
//                if (linear_show_charity_data.getVisibility() == View.GONE) {
//                    linear_show_charity_data.setVisibility(View.VISIBLE);
//                    Utility.ViewAnimation(linear_show_charity_data, 0, height, 300);
//                } else {
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            linear_show_charity_data.setVisibility(View.GONE);
//                        }
//                    }, 300);
//
//                    Utility.ViewAnimation(linear_show_charity_data, height, 0, 300);
//                }
//            }
//        });

        //-----------digitalgoods--------

        if (postsModel.type.equals(Constants.DIGITAL_GOODS) && postsModel.commerceModel != null) {

            linear_digitalgoods.setVisibility(View.VISIBLE);

            txt_commerce_name.setText(postsModel.commerceModel.name);
            txt_commerce_price.setText(String.valueOf(postsModel.commerceModel.price) + " " + getString(R.string.pib));

            if (postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                frame_commerce_value.setVisibility(View.VISIBLE);
                linear_commerce_warning.setVisibility(View.GONE);
                txt_commerce_sales.setText(String.valueOf(postsModel.sales) + " " + getString(R.string.sales));
                txt_commerce_status.setText(getString(R.string.pib_reward));

            } else {

                frame_commerce_value.setVisibility(View.GONE);

                if (postsModel.commerceModel.status.equals(Constants.FAILED)) {
                    txt_commerce_status.setText(R.string.failed);
                    linear_commerce_warning.setVisibility(View.VISIBLE);

                    if (Constants.digitalGoodsError(this, postsModel.commerceModel.error_code) != null) {
                        txt_commerce_error.setText(Constants.digitalGoodsError(this, postsModel.commerceModel.error_code));
                    }

                } else if (postsModel.commerceModel.status.equals(Constants.WAIT) || postsModel.commerceModel.status.equals(Constants.IN_PROGRESS)) {
                    txt_commerce_status.setText(R.string.waiting);
                    linear_commerce_warning.setVisibility(View.GONE);
                }

            }

        } else {

            linear_digitalgoods.setVisibility(View.GONE);

        }

        //-----------promotion-----------

        Random r = new Random();

        if (postsModel.promotionModel != null && postsModel.promotionModel.budget > 0) {

            linear_promotion_create.setVisibility(View.GONE);
            linear_promotion.setVisibility(View.VISIBLE);

            if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {
                txt_promotion.setText(postsModel.promotionModel.action_button);
            } else {
                txt_promotion.setText(R.string.visit_your_profile);
            }

            if (postsModel.promotionModel.is_closed) {

                txt_promotion_statue.setVisibility(View.VISIBLE);
                txt_promotion_statue.setText(getString(R.string.closed));
                linear_promotion.setBackgroundColor(getResources().getColor(R.color.black));

            } else {

                if (postsModel.promotionModel.is_paused) {

                    txt_promotion_statue.setVisibility(View.VISIBLE);
                    txt_promotion_statue.setText(getString(R.string.paused));
                    linear_promotion.setBackgroundColor(getResources().getColor(R.color.grey_a8a8a8));

                } else {
                    txt_promotion_statue.setVisibility(View.GONE);
                    linear_promotion.setBackgroundColor(getResources().getColor(Utility.g_promotionColors[r.nextInt(4)]));
                }

            }

            linear_promotion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linear_promotion.setOnClickListener(this);
                }
            });

        } else {

            linear_promotion.setVisibility(View.GONE);
            linear_promotion_create.setVisibility(View.VISIBLE);
            int pos = r.nextInt(4);
            linear_promote.setBackground(getResources().getDrawable(Utility.g_promoteButtonBackground[pos]));
            linear_promote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.showCreatePromotionDialog(MediaCaptionEditActivity.this, postsModel);
                }
            });

            linear_promote_engage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.showPromotionEngageDetailDialog(MediaCaptionEditActivity.this, postsModel);
                }
            });
        }

        //----------- following----------

        if (postsModel.user.id != userid) {
            if (postsModel.user.interaction_status.is_following) {
                btn_follow.setText(getString(R.string.following));
                btn_follow.setTextColor(getResources().getColor(R.color.colorYellow));
            } else {
                btn_follow.setText(getString(R.string.follow));
                btn_follow.setTextColor(getResources().getColor(R.color.colorMain));
            }
        } else {
            btn_follow.setText(getString(R.string.follow));
            btn_follow.setTextColor(getResources().getColor(R.color.colorMain));
        }


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (postsModel.user.id == userid) return;

                if (Constants.isLifeToken(MediaCaptionEditActivity.this)) {

                    String access_token = Utility.getReadPref(MediaCaptionEditActivity.this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                    if (postsModel.user.interaction_status.is_following) {
                        ServerRequest.getSharedServerRequest().selectUnFollow(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this, postsModel.user.username, access_token);

                        postsModel.user.interaction_status.is_following = false;
                        btn_follow.setText(getString(R.string.follow));
                        btn_follow.setTextColor(getResources().getColor(R.color.colorMain));

                    } else {
                        ServerRequest.getSharedServerRequest().selectFollow(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this, postsModel.user.username, access_token);

                        postsModel.user.interaction_status.is_following = true;
                        btn_follow.setText(getString(R.string.following));
                        btn_follow.setTextColor(getResources().getColor(R.color.colorYellow));
                    }

                } else {
                    Constants.requestRefreshToken(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this);
                    REQUEST_TYPE = "";
                }

            }
        });

        //----------- show user image-----------

        if (!postsModel.user.avatar.equals("null")) {

            this.imageLoader.displayImage(postsModel.user.avatar, img_user);
            txt_userEmo.setVisibility(View.INVISIBLE);

        } else {
            txt_userEmo.setVisibility(View.VISIBLE);
            img_user.setImageDrawable(null);
            img_user.setBackgroundColor(getResources().getColor(Utility.g_aryColors[postsModel.user.avatar_temp]));
            txt_userEmo.setText(Utility.getUserEmoName(postsModel.user.username));
        }

        //------------prize----------

        if (postsModel.user.prizeAmount > 0) {

            img_diamond.setImageDrawable(getResources().getDrawable(R.drawable.icon_diamond_gold));
            txt_user_total_won.setVisibility(View.VISIBLE);
            txt_user_total_won.setText(String.valueOf(Utility.formatedNumberString(postsModel.user.prizeAmount)));

        } else {

            img_diamond.setImageDrawable(getResources().getDrawable(R.drawable.icon_diamond));
            txt_user_total_won.setVisibility(View.INVISIBLE);

        }

        if (postsModel.prize > 0) {

            linear_post_total_won.setVisibility(View.VISIBLE);
            txt_post_total_won.setText(Utility.getConvertedValue(String.valueOf(postsModel.prize), false));

        } else {

            linear_post_total_won.setVisibility(View.INVISIBLE);

        }

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postsModel.user.id == userid) return;
            }
        });

        String rb = postsModel.user.available_PRB;
        String gb = postsModel.user.available_PGB;

        if (rb == null) rb = "0";
        if (gb == null) gb = "0";

        Float val_rb = Float.parseFloat(rb);
        Float val_gb = Float.parseFloat(gb);

        @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%d R.B %d G.B %d", postsModel.user.level, val_rb.intValue(), val_gb.intValue());
        txt_level.setText(str_level);

        txt_username.setText(Utility.getUpercaseString(postsModel.user.username));

        //----------comment-----------

        btn_gotoallcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaCaptionEditActivity.this, CommentActivity.class);

                intent.putExtra(CommentActivity.POST_ID, postsModel.id);
                intent.putExtra(CommentActivity.POST_DESCRIPTION, postsModel.caption);
                intent.putExtra(CommentActivity.POST_USER_AVATAR, postsModel.user.avatar);
                intent.putExtra(CommentActivity.POST_USER_NAME, postsModel.user.username);
                intent.putExtra(CommentActivity.POST_CREATED, postsModel.created_at);
                intent.putExtra(CommentActivity.POST_USER_AVATAR_TEMP, postsModel.user.avatar_temp);
                intent.putExtra(CommentActivity.POST_COMMENT_COUNT, postsModel.comments_count);

                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaCaptionEditActivity.this, CommentActivity.class);

                intent.putExtra(CommentActivity.POST_ID, postsModel.id);
                intent.putExtra(CommentActivity.POST_DESCRIPTION, postsModel.caption);
                intent.putExtra(CommentActivity.POST_USER_AVATAR, postsModel.user.avatar);
                intent.putExtra(CommentActivity.POST_USER_NAME, postsModel.user.username);
                intent.putExtra(CommentActivity.POST_CREATED, postsModel.created_at);
                intent.putExtra(CommentActivity.POST_USER_AVATAR_TEMP, postsModel.user.avatar_temp);
                intent.putExtra(CommentActivity.POST_COMMENT_COUNT, postsModel.comments_count);

                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }
        });

        //-------------favorite---------------

        if (postsModel.favorites) {
            btn_favourit.setImageResource(R.drawable.icon_star_selected);
        } else {
            btn_favourit.setImageResource(R.drawable.icon_star);
        }
        btn_favourit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (userid == postsModel.user.id) return;

                if (Constants.isLifeToken(MediaCaptionEditActivity.this)) {

                    String access_token = Utility.getReadPref(MediaCaptionEditActivity.this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                    if (postsModel.favorites) {
                        ServerRequest.getSharedServerRequest().deselectFavorit(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this, postsModel.id, access_token);

                        postsModel.favorites = false;
                        btn_favourit.setImageResource(R.drawable.icon_star);
                    } else {
                        ServerRequest.getSharedServerRequest().selectFavorit(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this, postsModel.id, access_token);

                        postsModel.favorites = true;
                        btn_favourit.setImageResource(R.drawable.icon_star_selected);
                    }

                } else {
                    Constants.requestRefreshToken(MediaCaptionEditActivity.this, MediaCaptionEditActivity.this);
                    REQUEST_TYPE = "";
                }

            }
        });

        //-------------Voted----------------

        if (postsModel.up_voted) {
            btn_votes.setImageResource(R.drawable.icon_brush_selected);
        } else {
            btn_votes.setImageResource(R.drawable.icon_brush);
        }

        linear_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (postsModel.user.id == userid) return;

                Utility.scaleView(btn_votes, 0.9f, 1.0f, 300);

                Intent intent = new Intent(MediaCaptionEditActivity.this, AddUpvoteActivity.class);
                intent.putExtra("postid", String.valueOf(postsModel.id));
                intent.putExtra("target", TARGET_UPVOTE);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        String str_upVotes = String.format("%d %s", postsModel.up_votes_amount, getString(R.string.brushed));

        txt_votes.setText(str_upVotes);

        //------------asking help-------------

        if (postsModel.askingHelpModel != null) {

            linear_help_reward.setVisibility(View.VISIBLE);

            if (!postsModel.askingHelpModel.closed) linear_help_reward.setBackground(getResources().getDrawable(R.drawable.icon_help_reward_background));
            else linear_help_reward.setBackground(getResources().getDrawable(R.drawable.icon_help_reward_background_gray));

            txt_help_reward.setText(Utility.getConvertedValue(String.valueOf(postsModel.askingHelpModel.reward), false));

        } else {

            linear_help_reward.setVisibility(View.GONE);

        }

//        linear_help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        //------------imageScroll-------------

        if (postsModel.ary_media != null && postsModel.ary_media.size() != 0) {

            scrollViewImage.setOnTouchListener(new View.OnTouchListener() {

                int down_scrollX, up_scrollX;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        down_scrollX = scrollViewImage.getScrollX();

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                        up_scrollX = scrollViewImage.getScrollX();
                        int itemWidth = scrollViewImage.getMeasuredWidth();
                        int activeItem = ((up_scrollX + itemWidth / 2) / itemWidth);

                        if (up_scrollX - down_scrollX > 0) {
                            activeItem = activeItem + 1;
                        } else if (up_scrollX -down_scrollX < 0) {
                            activeItem = activeItem - 1;
                        }
//                        if (up_scrollX - down_scrollX > 0) {
//                            activeItem = activeItem + 1;
//                        } else {
//                            activeItem = activeItem - 1;
//                        }

                        int scrollTo = activeItem * itemWidth;

                        ObjectAnimator animator=ObjectAnimator.ofInt(scrollViewImage, "scrollX",scrollTo );
                        animator.setDuration(350);
                        animator.start();

                        if (activeItem == -1) activeItem = 0;
                        else if (activeItem == postsModel.ary_media.size()) activeItem = activeItem - 1;

                        txt_media.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(postsModel.ary_media.size())));

                        if (!postsModel.ary_media.get(activeItem).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                            if (mVideoViews != null && mVideoViews.size() > 0) {

                                for (int k = 0; k < mVideoViews.size(); k++) {
                                    VideoView videoView = mVideoViews.get(k);
                                    if (videoView != null && videoView.isPlaying()) videoView.setMute(true);
                                }
                            }
                        }

                        return true;
                    }

                    return false;
                }
            });

            int index_video = 0;

            for (int i = 0; i < postsModel.ary_media.size(); i++) {
                if (postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                    index_video = i;
                }
            }


            for (int i = 0; i < postsModel.ary_media.size(); i++) {

                final int mediaView_height = (int)getImageViewHeight(postsModel.ary_media.get(index_video).width,postsModel.ary_media.get(index_video).height);

                if (postsModel.ary_media.size() > 1) {
                    frame_media_index.setVisibility(View.VISIBLE);

                    txt_media.setText(String.format("1/%s", String.valueOf(postsModel.ary_media.size())));

                } else {
                    frame_media_index.setVisibility(View.GONE);
                }

                if (postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                    final String media_url = postsModel.ary_media.get(i).url;
                    final String media_thumb = postsModel.ary_media.get(i).poster;

                    CustomVideoView videoLayout = new CustomVideoView(this, media_url, media_thumb, this.imageLoader);

                    VideoView videoView = videoLayout.getVideoView();
                    mVideoViews.add(videoView);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height);
                    videoLayout.setLayoutParams(layoutParams);
                    linear_scroll.addView(videoLayout);

                } else {

                    final ImageView img_post = new ImageView(this);
                    LinearLayout.LayoutParams img_layout = new LinearLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height);
                    img_post.setAdjustViewBounds(true);
                    img_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img_post.setLayoutParams(img_layout);

                    Zoomy.Builder builder = new Zoomy.Builder(this)
                            .target(img_post)
                            .enableImmersiveMode(false)
                            .interpolator(new OvershootInterpolator())
                            .tapListener(new TapListener() {
                                @Override
                                public void onTap(View v) {

                                }
                            });

                    builder.register();

                    linear_scroll.addView(img_post);

                    final String media_url = postsModel.ary_media.get(i).url;

                    this.imageLoader.displayImage(media_url, img_post);

                }
            }

            scrollViewImage.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewImage.fullScroll(View.FOCUS_UP);
                }
            });

        }

        if (postsModel.placeModel == null) {

            txt_place.setText(R.string.add_location);
            txt_place.setTextColor(getResources().getColor(R.color.colorMain));
            img_location.setImageDrawable(getResources().getDrawable(R.drawable.icon_location_blue));

        } else {
            String str_place = postsModel.placeModel.description;
            if (str_place.length() > 0) {
                txt_place.setText(str_place);
                txt_place.setTextColor(getResources().getColor(R.color.gray));
                img_location.setImageDrawable(getResources().getDrawable(R.drawable.icon_loaction));
            } else {
                txt_place.setText(R.string.add_location);
                txt_place.setTextColor(getResources().getColor(R.color.colorMain));
                img_location.setImageDrawable(getResources().getDrawable(R.drawable.icon_location_blue));
            }
        }

        // -------------show Attris----------------

//        String str_date = " " + Utility.getTimeAgo(postsModel.created_at);
//        String str_attr = postsModel.fundingModel.caption + str_date;
//        Spannable spannable_attr = new SpannableString(str_attr);
//        spannable_attr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.line_background_color)), postsModel.fundingModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        txt_attr.setText(spannable_attr, TextView.BufferType.SPANNABLE);

        txt_attr.setText(postsModel.caption);

        //------------- show Tags -----------------

        if (postsModel.tags.size() > 0) {

            ArrayList<String> ary_tag = new ArrayList<>();

            for (int i = 0; i < postsModel.tags.size(); i++) {
                String string = String.format("#%s", postsModel.tags.get(i));
                ary_tag.add(string);
            }

            linear_tags.setVisibility(View.VISIBLE);
            tagContainerLayout.setTags(ary_tag);
        } else {
            linear_tags.setVisibility(View.GONE);
        }

        //------------- show comments---------------

        if (postsModel.comments_count > 0) {

            if (postsModel.comments_count > 4) {
                btn_comment.setVisibility(View.VISIBLE);
                btn_comment.setText(String.format("%s %s %s", getString(R.string.view_all), String.valueOf(postsModel.comments_count), getString(R.string.comments)));
            } else {
                btn_comment.setVisibility(View.GONE);
            }

            creatingCommentView(linear_item_comment, postsModel);
        }

    }

    private void creatingCommentView(LinearLayout linearLayout, PostsModel postsModel) {

        int count = 0;
        if (postsModel.comments.size() > 4) count = 4;
        else count = postsModel.comments.size();

        for (int i = 0; i < count; i++) {

            CommentsModel commentsModel = postsModel.comments.get(i);

            String str_superComment = " : " + commentsModel.body;
            String str_superTextComent = commentsModel.userModel.username + str_superComment;
            Spannable spannable_super = new SpannableString(str_superTextComent);
            spannable_super.setSpan(new StyleSpan(Typeface.BOLD), 0, commentsModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable_super.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.colorBlack)), 0, commentsModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView txt_superComment = new TextView(this);
            LinearLayout.LayoutParams txt_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txt_layout.setMargins(0, 10, 0, 0);
            txt_superComment.setLayoutParams(txt_layout);
            txt_superComment.setTextColor(this.getResources().getColor(R.color.colorLightGray));
            txt_superComment.setText(spannable_super, TextView.BufferType.SPANNABLE);
            txt_superComment.setTextSize(12);
            linearLayout.addView(txt_superComment);

            if (commentsModel.ary_replies != null && commentsModel.ary_replies.size() > 0) {

                LinearLayout linear_reply = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 5, 0, 0);
                linear_reply.setLayoutParams(layoutParams);

                linearLayout.addView(linear_reply);

                View view = new View(this);
                LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(Utility.dpToPx(1), LinearLayout.LayoutParams.MATCH_PARENT);
                view_params.setMargins(0, 5, 0, 5);
                view.setLayoutParams(view_params);
                view.setBackgroundColor(this.getResources().getColor(R.color.line_background_color));

                linear_reply.addView(view);

                LinearLayout linear_reply_item = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams_item = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams_item.setMargins(10, 0, 0, 0);
                linear_reply_item.setOrientation(LinearLayout.VERTICAL);
                linear_reply_item.setLayoutParams(layoutParams_item);

                linear_reply.addView(linear_reply_item);

                for (int j = 0; j < commentsModel.ary_replies.size(); j++) {

                    CommentReplyModel commentReplyModel = commentsModel.ary_replies.get(j);

                    String str_Comment = " : " + commentReplyModel.body;
                    String str_TextComent = commentReplyModel.user.username + str_Comment;
                    Spannable spannable = new SpannableString(str_TextComent);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, commentReplyModel.user.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.colorBlack)), 0, commentReplyModel.user.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    TextView txt_Comment = new TextView(this);
                    LinearLayout.LayoutParams txt_Commentlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        txt_Commentlayout.setMargins(0, Utility.dpToPx(10), 0, 0);
                    txt_Comment.setLayoutParams(txt_Commentlayout);
                    txt_Comment.setTextColor(this.getResources().getColor(R.color.colorLightGray));
                    txt_Comment.setText(spannable, TextView.BufferType.SPANNABLE);
                    txt_Comment.setTextSize(12);
                    linear_reply_item.addView(txt_Comment);

                }

            }

        }

    }

    private float processLenth(int totalValue, int currentValue) {

        if (totalValue == 0) {
            return 0;
        }

        return 100 * currentValue / totalValue;
    }

    private float getImageViewHeight(int width, int height) {

        return Utility.g_deviceWidth * height / width;
    }

    private void getPostData(JSONObject objPost) {

        if (objPost == null) return;

        postsModel = ParseUtility.postModel(objPost);

        if (postsModel.placeModel != null) mainFeedParams.locationModel.description = postsModel.placeModel.description;

        initView(postsModel);
        REQUEST_TYPE = "";
        linear_all.setVisibility(View.VISIBLE);
    }

    private void showActionBar() {

        Utility.dismissKeyboard(this);

        linear_actionbar.setVisibility(View.VISIBLE);

        final Animation animation = new TranslateAnimation(0, 0 , Utility.dpToPx(170), Utility.dpToPx(0));
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(300);
        linear_actionbar.setAnimation(animation);
        linear_actionbar.startAnimation(animation);
    }

    private void hideActionBar() {
        if (linear_actionbar.getVisibility() == View.VISIBLE) {

            Utility.dismissKeyboard(this);

            final Animation animation = new TranslateAnimation(0, 0 , Utility.dpToPx(0), Utility.dpToPx(170));
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(300);
            linear_actionbar.setAnimation(animation);
            linear_actionbar.startAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    linear_actionbar.setVisibility(View.GONE);
                }
            }, 300);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (REQUEST_TYPE.equals(GET_POST_DATA)) {

                getPostData(objResult);

            } else if (REQUEST_TYPE.equals(UPDATE_MEDIA_CAPTION)) {

                hideHUD();

                Utility.dismissKeyboard(this);

                Utility.g_isChanged = true;

                if (getIntent().getStringExtra(ACTIVITY_TYPE) == null) {

                    Utility.mainFeedParams = mainFeedParams;

                }

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (REQUEST_TYPE.equals(GET_POST_DATA)) {

                requestPostData();

            } else if (REQUEST_TYPE.equals(UPDATE_MEDIA_CAPTION)) {

                updateMedia();

            }

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onClick(View v) {

        Utility.dismissKeyboard(this);

        if (v == linear_place) {

            if (linear_actionbar.getVisibility() == View.GONE) {
                showActionBar();
            } else {
                hideActionBar();
            }

        } else if (v == linear_remove) {

            hideActionBar();

            mainFeedParams.caption = txt_attr.getText().toString();
            mainFeedParams.locationModel = new LocationModel();

            updateMedia();

        } else if (v == linear_change) {

            hideActionBar();

            Intent intent = new Intent(this, MainFeedLocationActivity.class);
            intent.putExtra(MainFeedLocationActivity.ACTIVITY_TYPE, TAG);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_location_cancel) {
            hideActionBar();
        }

        if (v == linear_done) {

            showHUD();

            mainFeedParams.caption = txt_attr.getText().toString();
            updateMedia();

        } else if (v == linear_cancel) {

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }
}
