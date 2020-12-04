package com.star.pibbledev.home.promotion;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ablanco.zoomy.Zoomy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.home.utility.videoview.CustomVideoView;
import com.star.pibbledev.home.utility.videoview.VideoView;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.videozoomy.VideoTapListener;
import com.star.pibbledev.services.global.customview.videozoomy.VideoZoomy;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.PostsModel;

import java.util.ArrayList;
import java.util.Random;

import co.lujun.androidtagview.TagContainerLayout;

public class PromotionPreviewActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back;
    TextView txt_username, txt_level, txt_place, txt_attr, txt_votes, txt_media, txt_userEmo;
    LinearLayout linear_place, linear_tags, linear_item_comment, linear_scroll, linear_charity, linear_show_charity_data,
             linear_charity_teaminfo, linear_promotion, linear_show_charity;
    TagContainerLayout tagContainerLayout;
    Button btn_comment;
    LinearLayout btn_follow;
    TextView txt_follow;
    ImageButton  btn_favourit;
    ImageView img_user, btn_votes;
    HorizontalScrollView scrollViewImage;
    FrameLayout frame_media_index, frag_process_charity;
    TextView txt_userEmo1, txt_title , txt_current_charity, txt_goal, txt_raised, txt_members, txt_left_date, txt_funding_action_type;
    TextView txt_teamname, txt_promotion;
    LinearLayout linear_commerce_warning, linear_digitalgoods;
    TextView txt_commerce_name, txt_commerce_price, txt_commerce_status, txt_commerce_error, txt_commerce_sales;
    FrameLayout frame_commerce_value;

    ArrayList<VideoView> mVideoViews = new ArrayList<>();
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promotion_preview);

        setLightStatusBar();

        imageLoader = ImageLoader.getInstance();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        linear_scroll = (LinearLayout)findViewById(R.id.linear_scroll);
        linear_place = (LinearLayout)findViewById(R.id.linear_place);
        linear_tags = (LinearLayout)findViewById(R.id.linear_tags);
        linear_item_comment = (LinearLayout)findViewById(R.id.linear_item_comment);
        linear_charity = (LinearLayout)findViewById(R.id.linear_charity);
        txt_username = (TextView)findViewById(R.id.txt_username);
        txt_level = (TextView)findViewById(R.id.txt_level);
        txt_place = (TextView)findViewById(R.id.txt_place);
        txt_attr = (TextView)findViewById(R.id.txt_attr);
        txt_votes = (TextView)findViewById(R.id.txt_votes);
        txt_media = (TextView)findViewById(R.id.txt_media);
        txt_userEmo = (TextView)findViewById(R.id.txt_userEmo);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_members = (TextView)findViewById(R.id.txt_members);
        btn_favourit = (ImageButton)findViewById(R.id.btn_favourit);
        btn_votes = (ImageView)findViewById(R.id.btn_votes);
        tagContainerLayout = (TagContainerLayout)findViewById(R.id.tagcontainerLayout);
        btn_comment = (Button)findViewById(R.id.btn_comment);
        btn_follow = (LinearLayout) findViewById(R.id.btn_follow);
        txt_follow = (TextView)findViewById(R.id.txt_follow);
        img_user = (ImageView)findViewById(R.id.img_user);
        scrollViewImage = (HorizontalScrollView)findViewById(R.id.scrollViewImage);
        frame_media_index = (FrameLayout)findViewById(R.id.frame_media_index);
        frag_process_charity = (FrameLayout) findViewById(R.id.frag_process_charity);
        txt_current_charity = (TextView)findViewById(R.id.txt_current_charity);
        txt_goal = (TextView)findViewById(R.id.txt_goal);
        txt_raised = (TextView)findViewById(R.id.txt_raised);
        txt_left_date = (TextView)findViewById(R.id.txt_left_date);
        txt_funding_action_type = (TextView)findViewById(R.id.txt_funding_action_type);
        linear_show_charity = (LinearLayout) findViewById(R.id.linear_show_charity);
        linear_show_charity_data = (LinearLayout)findViewById(R.id.linear_show_charity_data);
        linear_charity_teaminfo = (LinearLayout)findViewById(R.id.linear_charity_teaminfo);
        txt_teamname = (TextView)findViewById(R.id.txt_teamname);
        linear_promotion = (LinearLayout)findViewById(R.id.linear_promotion);
        txt_promotion = (TextView)findViewById(R.id.txt_promotion);
        linear_commerce_warning = (LinearLayout)findViewById(R.id.linear_commerce_warning);
        linear_digitalgoods = (LinearLayout)findViewById(R.id.linear_digitalgoods);
        txt_commerce_name = (TextView)findViewById(R.id.txt_commerce_name);
        txt_commerce_price = (TextView)findViewById(R.id.txt_commerce_price);
        txt_commerce_status = (TextView)findViewById(R.id.txt_commerce_status);
        txt_commerce_error = (TextView)findViewById(R.id.txt_commerce_error);
        txt_commerce_sales = (TextView)findViewById(R.id.txt_commerce_sales);
        frame_commerce_value = (FrameLayout)findViewById(R.id.frame_commerce_value);

        linear_promotion.setOnClickListener(this);

        initView();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        //-----------charity------------

        if (Utility.mainFeedParams.postsModel.fundingModel != null) {

            linear_charity.setVisibility(View.VISIBLE);

            txt_title.setText(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.title);
            int leftDays = Utility.getDates(Utility.getCurrentDate(), Utility.mainFeedParams.postsModel.fundingModel.end_date).size();
            txt_left_date.setText(leftDays + " " + getString(R.string.days_left));

            switch (Utility.mainFeedParams.postsModel.type) {

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

        } else {
            linear_charity.setVisibility(View.GONE);
        }

//        if (Utility.mainFeedParams.postsModel.type.equals(Constants.FUNDING) && Utility.mainFeedParams.postsModel.fundingModel != null) {
//
//            linear_charity.setVisibility(View.VISIBLE);
//
//            if (Utility.mainFeedParams.postsModel.fundraise_as.equals(Constants.TEAM)) {
//
//                txt_teamname.setText(Utility.mainFeedParams.postsModel.fundingModel.teamModel.name);
//                txt_members.setText(String.valueOf(Utility.mainFeedParams.postsModel.fundingModel.teamModel.members_count));
//
//            }
//
//            txt_title.setText(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.title);
//            txt_current_charity.setText(String.valueOf(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.raised) + " " + getString(R.string.pib));
//            txt_goal.setText(String.valueOf(String.valueOf(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.goal)) + " " + getString(R.string.pib));
//            txt_raised.setText(String.valueOf(processLenth(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.goal, Utility.mainFeedParams.postsModel.fundingModel.campaignModel.raised)) + " %");
//
//            float processLenth = processLenth(Utility.mainFeedParams.postsModel.fundingModel.campaignModel.goal, Utility.mainFeedParams.postsModel.fundingModel.campaignModel.raised) / 100;
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

        final int userid = Utility.getReadPref(this).getIntValue("id");

//        linear_show_charity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int height = 0;
//
//                if (Utility.mainFeedParams.postsModel.fundraise_as.equals(Constants.TEAM)) {
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

        if (Utility.mainFeedParams.postsModel.type.equals(Constants.SALE) && Utility.mainFeedParams.postsModel.commerceModel != null) {

            linear_digitalgoods.setVisibility(View.VISIBLE);

            txt_commerce_name.setText(Utility.mainFeedParams.postsModel.commerceModel.name);
            txt_commerce_price.setText(String.valueOf(Utility.mainFeedParams.postsModel.commerceModel.price) + " " + getString(R.string.pib));

            if (Utility.mainFeedParams.postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                frame_commerce_value.setVisibility(View.VISIBLE);
                linear_commerce_warning.setVisibility(View.GONE);
                txt_commerce_sales.setText(String.valueOf(Utility.mainFeedParams.postsModel.sales) + " " + getString(R.string.sales));
                txt_commerce_status.setText(getString(R.string.pib_reward));

            } else {

                frame_commerce_value.setVisibility(View.GONE);

                if (Utility.mainFeedParams.postsModel.commerceModel.status.equals(Constants.FAILED)) {
                    txt_commerce_status.setText(getString(R.string.failed));
                    linear_commerce_warning.setVisibility(View.VISIBLE);

                    if (Constants.digitalGoodsError(this, Utility.mainFeedParams.postsModel.commerceModel.error_code) != null) {
                        txt_commerce_error.setText(Constants.digitalGoodsError(this, Utility.mainFeedParams.postsModel.commerceModel.error_code));
                    }

                } else if (Utility.mainFeedParams.postsModel.commerceModel.status.equals(Constants.WAIT) || Utility.mainFeedParams.postsModel.commerceModel.status.equals(Constants.IN_PROGRESS)) {
                    txt_commerce_status.setText(getString(R.string.waiting));
                    linear_commerce_warning.setVisibility(View.GONE);
                }

            }

        } else {

            linear_digitalgoods.setVisibility(View.GONE);

        }


        //-----------promotion-----------

        Random r = new Random();

        linear_promotion.setVisibility(View.VISIBLE);
        linear_promotion.setBackgroundColor(getResources().getColor(Utility.g_promotionColors[r.nextInt(4)]));

        if (Utility.mainFeedParams.promotion_destination.equals(Constants.PROMOTION_SITE)) {
            txt_promotion.setText(Utility.mainFeedParams.promotion_actionButton);
        } else {
            txt_promotion.setText(R.string.visit_your_profile);
        }

        //----------- following----------

        if (Utility.mainFeedParams.postsModel.user.id != userid) {

            btn_follow.setVisibility(View.VISIBLE);

            if (Utility.mainFeedParams.postsModel.user.interaction_status != null) {

                if (Utility.mainFeedParams.postsModel.user.interaction_status.is_following) {
                    txt_follow.setText(getString(R.string.following));
                    txt_follow.setTextColor(getResources().getColor(R.color.black));
                } else {
                    txt_follow.setText(getString(R.string.follow));
                    txt_follow.setTextColor(getResources().getColor(R.color.colorMain));
                }
            } else {
                txt_follow.setText(getString(R.string.follow));
                txt_follow.setTextColor(getResources().getColor(R.color.colorMain));
            }

        } else {
            btn_follow.setVisibility(View.INVISIBLE);
        }

        //----------- show user image-----------

        if (!Utility.mainFeedParams.postsModel.user.avatar.equals("null")) {

            imageLoader.displayImage(Utility.mainFeedParams.postsModel.user.avatar, img_user);
            txt_userEmo.setVisibility(View.INVISIBLE);

        } else {
            txt_userEmo.setVisibility(View.VISIBLE);
            img_user.setImageDrawable(null);
            img_user.setBackgroundColor(getResources().getColor(Utility.g_aryColors[Utility.mainFeedParams.postsModel.user.avatar_temp]));
            txt_userEmo.setText(Utility.getUserEmoName(Utility.mainFeedParams.postsModel.user.username));
        }

        String rb = Utility.mainFeedParams.postsModel.user.available_PRB;
        String gb = Utility.mainFeedParams.postsModel.user.available_PGB;

        if (rb == null) rb = "0";
        if (gb == null) gb = "0";

        Float val_rb = Float.parseFloat(rb);
        Float val_gb = Float.parseFloat(gb);

        @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%d R.B %d G.B %d", Utility.mainFeedParams.postsModel.user.level, val_rb.intValue(), val_gb.intValue());
        txt_level.setText(str_level);

        txt_username.setText(Utility.getUpercaseString(Utility.mainFeedParams.postsModel.user.username));

        //-------------favorite---------------

        if (Utility.mainFeedParams.postsModel.favorites) {

            btn_favourit.setImageResource(R.drawable.icon_star_selected);

        } else {
            btn_favourit.setImageResource(R.drawable.icon_star);
        }

        //-------------Voted----------------

        if (Utility.mainFeedParams.postsModel.up_voted) {
            btn_votes.setImageResource(R.drawable.icon_brush_selected);
        } else {
            btn_votes.setImageResource(R.drawable.icon_brush);
        }

        String str_upVotes = String.format("%d %s", Utility.mainFeedParams.postsModel.up_votes_amount, getString(R.string.brushed));

        txt_votes.setText(str_upVotes);

        //------------imageScroll-------------

        if (Utility.mainFeedParams.postsModel.ary_media != null && Utility.mainFeedParams.postsModel.ary_media.size() != 0) {

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
                        else if (activeItem == Utility.mainFeedParams.postsModel.ary_media.size()) activeItem = activeItem - 1;

                        txt_media.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(Utility.mainFeedParams.postsModel.ary_media.size())));

                        if (!Utility.mainFeedParams.postsModel.ary_media.get(activeItem).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

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

            for (int i = 0; i < Utility.mainFeedParams.postsModel.ary_media.size(); i++) {

                if (Utility.mainFeedParams.postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {
                    index_video = i;
                }
            }

            for (int i = 0; i < Utility.mainFeedParams.postsModel.ary_media.size(); i++) {

                final int mediaView_height = (int)getImageViewHeight(Utility.mainFeedParams.postsModel.ary_media.get(index_video).width, Utility.mainFeedParams.postsModel.ary_media.get(index_video).height);

                if (Utility.mainFeedParams.postsModel.ary_media.size() > 1) {
                    frame_media_index.setVisibility(View.VISIBLE);

                    txt_media.setText(String.format("1/%s", String.valueOf(Utility.mainFeedParams.postsModel.ary_media.size())));

                } else {
                    frame_media_index.setVisibility(View.GONE);
                }

                int finalI = i;
                LinearLayout.LayoutParams img_layout = new LinearLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height);

                if (Utility.mainFeedParams.postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                    final String media_url = Utility.mainFeedParams.postsModel.ary_media.get(i).url;
                    final String media_thumb = Utility.mainFeedParams.postsModel.ary_media.get(i).poster;

                    CustomVideoView videoLayout = new CustomVideoView(this, Utility.getLocalVideoFileUrl(this, media_url), media_thumb, this.imageLoader);

                    videoLayout.setLayoutParams(img_layout);

                    VideoView videoView = videoLayout.getVideoView();
                    videoView.setTag(false);
                    mVideoViews.add(videoView);

                    RelativeLayout layout = new RelativeLayout(this);
                    layout.setLayoutParams(new RelativeLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height));
                    layout.addView(videoLayout);

                    linear_scroll.addView(layout);

                    VideoZoomy.Builder builder = new VideoZoomy.Builder(this)
                            .target(layout)
                            .enableImmersiveMode(false)
                            .interpolator(new OvershootInterpolator())
                            .tapListener(new VideoTapListener() {
                                @Override
                                public void onTap(View v) {
                                    if (videoView.getTag().equals(false)) {
                                        videoView.setMute(false);
                                        videoView.setTag(true);
                                    } else {
                                        videoView.setMute(true);
                                        videoView.setTag(false);
                                    }
                                }
                            });

                    builder.register();

                } else {

                    final ImageView img_post = new ImageView(this);

                    img_post.setAdjustViewBounds(true);
                    img_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img_post.setLayoutParams(img_layout);

                    Zoomy.Builder builder = new Zoomy.Builder(this)
                            .target(img_post)
                            .enableImmersiveMode(false)
                            .interpolator(new OvershootInterpolator());

                    builder.register();

                    linear_scroll.addView(img_post);

                    final String media_url = Utility.mainFeedParams.postsModel.ary_media.get(i).url;

                    imageLoader.displayImage(media_url, img_post);

                }

            }

            scrollViewImage.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewImage.fullScroll(View.FOCUS_UP);
                }
            });

        }

        //--------------show location----------------

        if (Utility.mainFeedParams.postsModel.placeModel == null) {

            linear_place.setVisibility(View.GONE);

        } else {

            String str_place = Utility.mainFeedParams.postsModel.placeModel.description;
            if (str_place.length() > 0) {
                linear_place.setVisibility(View.VISIBLE);
                txt_place.setText(str_place);
            } else {
                linear_place.setVisibility(View.GONE);
            }
        }

        // -------------show Attris----------------

        String str_date = " " + Utility.getTimeAgo(Utility.mainFeedParams.postsModel.created_at);
        String str_attr = Utility.mainFeedParams.postsModel.caption + str_date;
        Spannable spannable_attr = new SpannableString(str_attr);
        spannable_attr.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.grey_c6c6c6)), Utility.mainFeedParams.postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_attr.setSpan(new RelativeSizeSpan(0.84f), Utility.mainFeedParams.postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_attr.setText(spannable_attr, TextView.BufferType.SPANNABLE);


//        if (!pflag_sendActionRequest && viewHolder.txt_attr != null) {
//
//            if (postsModel.fundingModel.caption.length() > 0) {
//                viewHolder.txt_attr.setTextColor(context.getResources().getColor(R.color.black));
//                String date = " " + Utility.getTimeAgo(postsModel.created_at);
//                readMoreOption.addReadMoreTo(viewHolder.txt_attr, postsModel.fundingModel.caption, date);
//
//            } else {
//
////                String str_attr = postsModel.fundingModel.caption + date;
////                Spannable spannable_attr = new SpannableString(str_attr);
////                spannable_attr.setSpan(new ForegroundColorSpan(this.context.getResources().getColor(R.color.light_gray)), postsModel.fundingModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                viewHolder.txt_attr.setText(Utility.getTimeAgo(postsModel.created_at));
//                viewHolder.txt_attr.setTextColor(context.getResources().getColor(R.color.light_gray));
//
//            }
//
//        }

        //------------- show Tags -----------------

        if (Utility.mainFeedParams.postsModel.tags.size() > 0) {

            ArrayList<String> ary_tag = new ArrayList<>();

            for (int i = 0; i < Utility.mainFeedParams.postsModel.tags.size(); i++) {
                String string = String.format("#%s", Utility.mainFeedParams.postsModel.tags.get(i));
                ary_tag.add(string);
            }

            linear_tags.setVisibility(View.VISIBLE);
            tagContainerLayout.setTags(ary_tag);

        } else {
            linear_tags.setVisibility(View.GONE);
        }

        //------------- show comments---------------

        if (Utility.mainFeedParams.postsModel.comments_count > 0) {

            if (Utility.mainFeedParams.postsModel.comments_count > 4) {
                btn_comment.setVisibility(View.VISIBLE);
                btn_comment.setText(String.format("%s %s %s", getString(R.string.view_all), String.valueOf(Utility.mainFeedParams.postsModel.comments_count), getString(R.string.comments)));
            } else {
                btn_comment.setVisibility(View.GONE);
            }

            creatingCommentView(linear_item_comment, Utility.mainFeedParams.postsModel);
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

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == linear_promotion) {

            if (Utility.mainFeedParams.promotion_destination.equals(Constants.PROMOTION_SITE)) {

//                Utility.openBrowser(this, Utility.mainFeedParams.promotion_site);
                Utility.showWebviewDialog(this, Utility.mainFeedParams.promotion_site);

            } else {

                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.SELECT_USERNAME, Utility.getReadPref(this).getStringValue(Constants.USERNAME));
                intent.putExtra(UserProfileActivity.SELECT_USERID, Utility.getReadPref(this).getIntValue(Constants.ID));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            }
        }

    }
}
