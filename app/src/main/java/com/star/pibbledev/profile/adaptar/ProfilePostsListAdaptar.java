package com.star.pibbledev.profile.adaptar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ablanco.zoomy.DoubleTapListener;
import com.ablanco.zoomy.LongPressListener;
import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.home.MainFeedListener;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.videozoomy.VideoDoubleTapListener;
import com.star.pibbledev.services.global.customview.videozoomy.VideoLongPressListener;
import com.star.pibbledev.services.global.customview.videozoomy.VideoTapListener;
import com.star.pibbledev.services.global.customview.videozoomy.VideoZoomy;
import com.star.pibbledev.services.global.doubleclick.DoubleClick;
import com.star.pibbledev.services.global.doubleclick.DoubleClickListener;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.home.utility.videoview.CustomVideoView;
import com.star.pibbledev.home.utility.videoview.VideoView;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class ProfilePostsListAdaptar extends RecyclerView.Adapter<ProfilePostsListAdaptar.ViewHolder> {

    private ArrayList<PostsModel> mData;
    private ArrayList<Integer> pCommented;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean mIsPromotionPage;
    private boolean mIsProfilePage;
    private MainFeedListener profileListener;

    private boolean pflag_sendActionRequest = false;
    private boolean pflag_upvoteChanged = false;
    private int pUpvotePreviousValue = 0;

    public ArrayList<VideoView> mVideoViews = new ArrayList<>();

    public ProfilePostsListAdaptar(Context context, ArrayList<PostsModel> data, ArrayList<Integer> pCommneted, MainFeedListener listener, ImageLoader imageLoader, boolean isPromotionPage, boolean isProfilePage) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.imageLoader = imageLoader;
        this.mContext = context;
        this.profileListener = listener;
        this.pCommented = pCommneted;
        this.mIsPromotionPage = isPromotionPage;
        this.mIsProfilePage = isProfilePage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_posts, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        PostsModel postsModel = mData.get(position);

        boolean isMyPost = false;
        if (postsModel.user.username.equals(Utility.getReadPref(mContext).getStringValue(Constants.USERNAME))) {
            isMyPost = true;
        }

        viewHolder.linear_scroll.removeAllViews();
        viewHolder.linear_item_comment.removeAllViews();

        viewHolder.linear_comment.setVisibility(View.GONE);
        viewHolder.txt_promotion_statue.setVisibility(View.GONE);

        if (!pflag_sendActionRequest) {
            viewHolder.linear_show_charity_data.setVisibility(View.GONE);
        }

        //--------------masters image----------

        String avatar = Utility.getReadPref(this.mContext).getStringValue("avatar");
        String username = Utility.getReadPref(this.mContext).getStringValue("username");

        if (!avatar.equals("null")) {

            Glide.with(mContext).load(avatar).into(viewHolder.img_user1);
            viewHolder.txt_userEmo1.setVisibility(View.INVISIBLE);

        } else {

            int value = 0;
            if (username.length() > 14) value = 14;
            else value = username.length();
            viewHolder.txt_userEmo1.setVisibility(View.VISIBLE);
            viewHolder.img_user1.setImageDrawable(null);
            viewHolder.img_user1.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            viewHolder.txt_userEmo1.setText(Utility.getUserEmoName(username));

        }

        //------------prize----------

        if (postsModel.user.prizeAmount > 0) {

            viewHolder.img_diamond.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_diamond_gold));
            viewHolder.txt_user_total_won.setVisibility(View.VISIBLE);
            viewHolder.txt_user_total_won.setText(String.valueOf(Utility.formatedNumberString(postsModel.user.prizeAmount)));

        } else {

            viewHolder.img_diamond.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_diamond));
            viewHolder.txt_user_total_won.setVisibility(View.INVISIBLE);

        }

        if (postsModel.prize > 0) {

            viewHolder.linear_post_total_won.setVisibility(View.VISIBLE);
            viewHolder.txt_post_total_won.setText(Utility.getConvertedValue(String.valueOf(postsModel.prize), false));

        } else {

            viewHolder.linear_post_total_won.setVisibility(View.INVISIBLE);

        }

        //-----------more action--------

        viewHolder.img_more_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileListener.onShowActionBar(position);
            }
        });

        //-----------goods--------------

        if (postsModel.type.equals(Constants.GOODS) && postsModel.goodsModel != null) {

            viewHolder.linear_goods.setVisibility(View.VISIBLE);

            String price = Utility.formatedNumberString(Double.parseDouble(postsModel.goodsModel.price));
            viewHolder.txt_goods_price.setText(price + " " + mContext.getString(R.string.pib));

            if (postsModel.goodsModel.status.equals(Constants.GOODS_FREE)) {
                viewHolder.txt_goods_purchase.setVisibility(View.GONE);
            } else {
                viewHolder.txt_goods_purchase.setVisibility(View.VISIBLE);

                if (postsModel.goodsModel.status.equals(Constants.GOODS_BOOKED)) {
                    viewHolder.txt_goods_purchase.setText(mContext.getString(R.string.booked));
                } else if (postsModel.goodsModel.status.equals(Constants.GOODS_SOLD)) {
                    viewHolder.txt_goods_purchase.setText(mContext.getString(R.string.sold_out));
                }
            }

            if (postsModel.goodsModel.is_new == 0) viewHolder.txt_goods_type.setText(mContext.getString(R.string.used_goods));
            else viewHolder.txt_goods_type.setText(mContext.getString(R.string.new_lower));

            if (postsModel.sales == 0) viewHolder.txt_goods_sales.setVisibility(View.GONE);
            else {
                viewHolder.txt_goods_sales.setVisibility(View.VISIBLE);
                viewHolder.txt_goods_sales.setText(String.valueOf(postsModel.sales) + " " + mContext.getString(R.string.sales));
            }

            viewHolder.linear_goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileListener.OnClickGoods(position);
                }
            });

        } else {
            viewHolder.linear_goods.setVisibility(View.GONE);
        }

        final int userid = Utility.getReadPref(this.mContext).getIntValue(Constants.ID);

        //-----------charity------------

        if (postsModel.fundingModel != null) {

            viewHolder.linear_charity.setVisibility(View.VISIBLE);

            viewHolder.txt_title.setText(postsModel.fundingModel.campaignModel.title);

            int leftDays = Utility.getDates(Utility.getCurrentDate(), postsModel.fundingModel.end_date).size();
            viewHolder.txt_left_date.setText(leftDays + " " + mContext.getString(R.string.days_left));

            switch (postsModel.type) {

                case Constants.FUNDING_CHARITY:

                    viewHolder.txt_funding_action_type.setText(mContext.getString(R.string.donate_uppearcase));

                    break;
                case Constants.FUNDING_CROWD_NO_REWARD:

                    viewHolder.txt_funding_action_type.setText(mContext.getString(R.string.contribute_uppercase));

                    break;
                case Constants.FUNDING_CROWD_REWARD:

                    viewHolder.txt_funding_action_type.setText(mContext.getString(R.string.pledge_uppercase));

                    break;
            }

            viewHolder.linear_show_charity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Utility.showFundingDetailDialog(mContext, postsModel, false);
                    profileListener.showFundingStatus(position);
                }
            });

        } else {
            viewHolder.linear_charity.setVisibility(View.GONE);
        }

//        if (postsModel.type.equals(Constants.FUNDING)) {
//
//            viewHolder.linear_charity.setVisibility(View.VISIBLE);
//
//            if (postsModel.fundraise_as.equals(Constants.TEAM)) {
//
//                viewHolder.txt_teamname.setText(postsModel.fundingModel.teamModel.name);
//                viewHolder.txt_members.setText(String.valueOf(postsModel.fundingModel.teamModel.members_count));
//
//                viewHolder.linear_funding_join.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        profileListener.onCharityJoin(position);
//                    }
//                });
//
//            }
//
//            viewHolder.txt_title.setText(postsModel.fundingModel.campaignModel.title);
//            viewHolder.txt_current_charity.setText(String.valueOf(postsModel.fundingModel.campaignModel.raised) + " " + mContext.getString(R.string.pib));
//            viewHolder.txt_goal.setText(String.valueOf(String.valueOf(postsModel.fundingModel.campaignModel.hard_cap)) + " " + mContext.getString(R.string.pib));
//            viewHolder.txt_raised.setText(String.valueOf(processLenth(postsModel.fundingModel.campaignModel.hard_cap, postsModel.fundingModel.campaignModel.raised)) + " %");
//
//            float processLenth = processLenth(postsModel.fundingModel.campaignModel.hard_cap, postsModel.fundingModel.campaignModel.raised) / 100;
//
//            int totalWidth = (int)(Utility.g_deviceWidth * 0.65) - Utility.dpToPx(10);
//            int prcesswidth = (int)(processLenth * totalWidth);
//
//            if (processLenth >= 1) {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                viewHolder.frag_process_charity.setLayoutParams(params);
//            } else {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(prcesswidth, FrameLayout.LayoutParams.MATCH_PARENT);
//                viewHolder.frag_process_charity.setLayoutParams(params);
//            }
//
//        } else {
//            viewHolder.linear_charity.setVisibility(View.GONE);
//        }
//
//        final int userid = Utility.getReadPref(this.mContext).getIntValue(Constants.ID);
//
//        viewHolder.btn_donate_charity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (postsModel.user.id == userid) return;
//                profileListener.onCharityDonate(position);
//
////                pflag_sendActionRequest = true;
//
//            }
//        });
//
//        viewHolder.linear_show_charity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                pflag_sendActionRequest = true;
//
//                int height = 0;
//
//                if (postsModel.fundraise_as.equals(Constants.TEAM)) {
//                    height = 120;
//                    viewHolder.linear_charity_teaminfo.setVisibility(View.VISIBLE);
//                } else {
//                    height = 60;
//                    viewHolder.linear_charity_teaminfo.setVisibility(View.GONE);
//                }
//
//                if (viewHolder.linear_show_charity_data.getVisibility() == View.GONE) {
//                    viewHolder.linear_show_charity_data.setVisibility(View.VISIBLE);
//                    Utility.ViewAnimation(viewHolder.linear_show_charity_data, 0, height, 300);
//                } else {
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            viewHolder.linear_show_charity_data.setVisibility(View.GONE);
//                        }
//                    }, 300);
//
//                    Utility.ViewAnimation(viewHolder.linear_show_charity_data, height, 0, 300);
//                }
//            }
//        });

        //-----------digitalgoods--------

        if (postsModel.type.equals(Constants.DIGITAL_GOODS) && postsModel.commerceModel != null) {

            boolean finalIsMyPost = isMyPost;
            viewHolder.linear_digitalgoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                        profileListener.OnClickDigitalGoods(position, finalIsMyPost);

                    }
                }
            });

            viewHolder.linear_digitalgoods.setVisibility(View.VISIBLE);

            viewHolder.txt_commerce_name.setText(postsModel.commerceModel.name);
            viewHolder.txt_commerce_price.setText(String.valueOf(postsModel.commerceModel.price) + " " + mContext.getString(R.string.pib));

            if (postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                viewHolder.frame_commerce_value.setVisibility(View.VISIBLE);
                viewHolder.linear_commerce_warning.setVisibility(View.GONE);
                viewHolder.txt_commerce_sales.setText(String.valueOf(postsModel.sales) + " " + mContext.getString(R.string.sales));
                viewHolder.txt_commerce_status.setText(mContext.getString(R.string.pib_reward));

            } else {

                if (!isMyPost) viewHolder.linear_digitalgoods.setVisibility(View.GONE);
                else {
                    viewHolder.frame_commerce_value.setVisibility(View.GONE);

                    if (postsModel.commerceModel.status.equals(Constants.FAILED)) {
                        viewHolder.txt_commerce_status.setText(mContext.getString(R.string.failed));
                        viewHolder.linear_commerce_warning.setVisibility(View.VISIBLE);

                        if (Constants.digitalGoodsError(mContext, postsModel.commerceModel.error_code) != null) {
                            viewHolder.txt_commerce_error.setText(Constants.digitalGoodsError(mContext, postsModel.commerceModel.error_code));
                        }

                    } else if (postsModel.commerceModel.status.equals(Constants.WAIT) || postsModel.commerceModel.status.equals(Constants.IN_PROGRESS)) {
                        viewHolder.txt_commerce_status.setText(mContext.getString(R.string.waiting));
                        viewHolder.linear_commerce_warning.setVisibility(View.GONE);
                    }
                }

            }

        } else {

            viewHolder.linear_digitalgoods.setVisibility(View.GONE);

        }

        //-----------promotion-----------

        if (mIsPromotionPage) {

            if (postsModel.promotionModel != null && postsModel.promotionModel.budget > 0) {

                viewHolder.linear_promotion_create.setVisibility(View.GONE);
                viewHolder.linear_promotion.setVisibility(View.VISIBLE);

                if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {
                    viewHolder.txt_promotion.setText(postsModel.promotionModel.action_button);
                } else {
                    viewHolder.txt_promotion.setText(R.string.visit_your_profile);
                }

                if (postsModel.promotionModel.is_closed) {

                    viewHolder.txt_promotion_statue.setVisibility(View.VISIBLE);
                    viewHolder.txt_promotion_statue.setText(R.string.promotion_closed);
                    viewHolder.linear_promotion.setBackgroundColor(mContext.getResources().getColor(R.color.black));

                } else {

                    if (postsModel.promotionModel.is_paused) {

                        viewHolder.txt_promotion_statue.setVisibility(View.VISIBLE);
                        viewHolder.txt_promotion_statue.setText(mContext.getString(R.string.paused));
                        viewHolder.linear_promotion.setBackgroundColor(mContext.getResources().getColor(R.color.grey_a8a8a8));

                    } else {
                        viewHolder.txt_promotion_statue.setVisibility(View.GONE);
                        viewHolder.linear_promotion.setBackgroundColor(mContext.getResources().getColor(Utility.g_promotionColors[position % 4]));
                    }

                }

                viewHolder.linear_promotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profileListener.gotoPromotionDetail(position);
                    }
                });

            } else {

                viewHolder.linear_promotion.setVisibility(View.GONE);
                viewHolder.linear_promotion_create.setVisibility(View.GONE);

                if (isMyPost) {

                    viewHolder.linear_promotion_create.setVisibility(View.VISIBLE);
                    int pos = position % 4;
                    viewHolder.linear_promote.setBackground(mContext.getResources().getDrawable(Utility.g_promoteButtonBackground[pos]));
                    viewHolder.linear_promote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileListener.createPromotion(position);
                        }
                    });
                }
            }

        } else {

            if (postsModel.promotionModel != null && postsModel.promotionModel.budget > 0) {

                if (postsModel.promotionModel.is_paused || postsModel.promotionModel.is_closed) {

                    viewHolder.linear_promotion_create.setVisibility(View.GONE);
                    viewHolder.linear_promotion.setVisibility(View.GONE);

                } else {

                    viewHolder.linear_promotion_create.setVisibility(View.GONE);
                    viewHolder.linear_promotion.setVisibility(View.VISIBLE);

                    if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {
                        viewHolder.txt_promotion.setText(postsModel.promotionModel.action_button);
                    } else {
                        viewHolder.txt_promotion.setText(R.string.visit_your_profile);
                    }

                    viewHolder.txt_promotion_statue.setVisibility(View.GONE);
                    viewHolder.linear_promotion.setBackgroundColor(mContext.getResources().getColor(Utility.g_promotionColors[position % 3]));

                    viewHolder.linear_promotion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileListener.gotoPromotionDetail(position);
                        }
                    });
                }

            } else {

                viewHolder.linear_promotion.setVisibility(View.GONE);
                viewHolder.linear_promotion_create.setVisibility(View.GONE);

                if (isMyPost) {

                    viewHolder.linear_promotion_create.setVisibility(View.VISIBLE);
                    int pos = position % 4;

                    if (mIsProfilePage) viewHolder.linear_promote.setBackground(mContext.getResources().getDrawable(Utility.g_promoteButtonBackground[3]));
                    else viewHolder.linear_promote.setBackground(mContext.getResources().getDrawable(Utility.g_promoteButtonBackground[pos]));

                    viewHolder.linear_promote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileListener.createPromotion(position);
                        }
                    });

                    viewHolder.linear_view_engage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Utility.showPromotionEngageDetailDialog(mContext, postsModel);

                        }
                    });
                }
            }

        }

        //----------- following----------

        if (postsModel.user.interaction_status != null) {

            if (postsModel.user.id != userid) {
                if (postsModel.user.interaction_status.is_following) {
                    viewHolder.txt_follow.setText(mContext.getString(R.string.following));
                    viewHolder.txt_follow.setTextColor(mContext.getResources().getColor(R.color.black));
                } else {
                    viewHolder.txt_follow.setText(mContext.getString(R.string.follow));
                    viewHolder.txt_follow.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                }
            } else {
                viewHolder.txt_follow.setText(mContext.getString(R.string.follow));
                viewHolder.txt_follow.setTextColor(mContext.getResources().getColor(R.color.colorMain));
            }
        } else {
            viewHolder.btn_follow.setVisibility(View.INVISIBLE);
//            viewHolder.linear_comment.setVisibility(View.GONE);
        }

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (postsModel.user.id == userid) return;
                profileListener.onFollow(position);
//                pflag_sendActionRequest = true;
            }
        });

        //----------- show user image-----------

        if (!postsModel.user.avatar.equals("null")) {

            this.imageLoader.displayImage(postsModel.user.avatar, viewHolder.img_user);
            viewHolder.txt_userEmo.setVisibility(View.INVISIBLE);

        } else {
            viewHolder.txt_userEmo.setVisibility(View.VISIBLE);
            viewHolder.img_user.setImageDrawable(null);
            viewHolder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[postsModel.user.avatar_temp]));
            viewHolder.txt_userEmo.setText(Utility.getUserEmoName(postsModel.user.username));
        }

        viewHolder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileListener.onShowingUserInfo(position);
            }
        });

        String rb = postsModel.user.available_PRB;
        String gb = postsModel.user.available_PGB;

        if (rb == null) rb = "0";
        if (gb == null) gb = "0";

        Float val_rb = Float.parseFloat(rb);
        Float val_gb = Float.parseFloat(gb);

        @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%d R.B %d G.B %d", postsModel.user.level, val_rb.intValue(), val_gb.intValue());
        viewHolder.txt_level.setText(str_level);

        viewHolder.txt_username.setText(Utility.getUpercaseString(postsModel.user.username));

        //----------comment-----------

        viewHolder.btn_gotoallcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileListener.onClickViewAllComment(position, true);
//                pflag_sendActionRequest = true;
            }
        });

        viewHolder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileListener.onClickViewAllComment(position, false);
//                pflag_sendActionRequest = true;
            }
        });

        viewHolder.btn_sentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
                profileListener.onClickSendingComment(viewHolder.editTextComment.getText().toString(), position);
                viewHolder.editTextComment.setText("");
                viewHolder.editTextComment.clearFocus();
//                pflag_sendActionRequest = true;
            }
        });

//        viewHolder.editTextComment.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    viewHolder.editTextComment.setCursorVisible(true);
//                }
//
//                return false;
//            }
//        });


        viewHolder.editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    viewHolder.btn_sentComment.setImageResource(R.drawable.btn_comment_selected);
                    viewHolder.btn_sentComment.setEnabled(true);
                } else {
                    viewHolder.btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
                    viewHolder.btn_sentComment.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //-------------favorite---------------

        if (postsModel.favorites) {
            viewHolder.btn_favourit.setImageResource(R.drawable.icon_star_selected);
        } else {
            viewHolder.btn_favourit.setImageResource(R.drawable.icon_star);
        }
        viewHolder.btn_favourit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (userid == postsModel.user.id) return;

                profileListener.onFavorite(postsModel.favorites, position);
            }
        });

        //-------------Voted----------------

        if (postsModel.up_voted) {
            viewHolder.btn_votes.setImageResource(R.drawable.icon_brush_selected);
        } else {
            viewHolder.btn_votes.setImageResource(R.drawable.icon_brush);
        }

        viewHolder.linear_upvote.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

                if (postsModel.user.id == userid) return;

                Utility.scaleView(viewHolder.btn_votes, 0.9f, 1.0f, 300);

                profileListener.onVoted(position);
                pflag_upvoteChanged = true;
                pUpvotePreviousValue = postsModel.up_votes_amount;
//                pflag_sendActionRequest = true;
            }

            @Override
            public void onDoubleClick(View view) {

                profileListener.showUpvotedUserPopup(String.valueOf(position));
            }
        }));

        viewHolder.linear_upvote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                profileListener.showUpvotedUserPopup(String.valueOf(position));

                return true;
            }
        });

        @SuppressLint("DefaultLocale") String str_upVotes = String.format("%d %s", postsModel.up_votes_amount, mContext.getString(R.string.brushed));

        if (pflag_upvoteChanged) {

            if (pUpvotePreviousValue != postsModel.up_votes_amount) {

                ValueAnimator animator = ValueAnimator.ofInt(pUpvotePreviousValue, postsModel.up_votes_amount);
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        viewHolder.txt_votes.setText(animation.getAnimatedValue().toString());
                    }
                });
                animator.start();
            }

            pflag_upvoteChanged = false;

        } else {
            viewHolder.txt_votes.setText(str_upVotes);
        }

        //------------asking help-------------

        if (postsModel.askingHelpModel != null) {

            viewHolder.linear_help_reward.setVisibility(View.VISIBLE);

            if (!postsModel.askingHelpModel.closed) viewHolder.linear_help_reward.setBackground(mContext.getResources().getDrawable(R.drawable.icon_help_reward_background));
            else viewHolder.linear_help_reward.setBackground(mContext.getResources().getDrawable(R.drawable.icon_help_reward_background_gray));

            viewHolder.txt_help_reward.setText(Utility.getConvertedValue(String.valueOf(postsModel.askingHelpModel.reward), false));

        } else {

            viewHolder.linear_help_reward.setVisibility(View.GONE);

        }

        viewHolder.linear_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileListener.showAskingHelp(position);

            }
        });

        if (postsModel.ary_media != null && postsModel.ary_media.size() != 0) {

            viewHolder.scrollViewImage.setOnTouchListener(new View.OnTouchListener() {

                int down_scrollX, up_scrollX, count_pos;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        if (count_pos == 0) {
                            down_scrollX = viewHolder.scrollViewImage.getScrollX();
                            count_pos++;
                        }

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                        up_scrollX = viewHolder.scrollViewImage.getScrollX();
                        count_pos = 0;

                        int itemWidth = viewHolder.scrollViewImage.getMeasuredWidth();
                        int activeItem = ((up_scrollX + itemWidth / 2) / itemWidth);

                        if (up_scrollX - down_scrollX > 0) {
                            activeItem = activeItem + 1;
                        } else if (up_scrollX -down_scrollX < 0) {
                            activeItem = activeItem - 1;
                        }

                        int scrollTo = activeItem * itemWidth;

                        ObjectAnimator animator=ObjectAnimator.ofInt(viewHolder.scrollViewImage, "scrollX",scrollTo );
                        animator.setDuration(350);
                        animator.start();

                        if (activeItem == -1) activeItem = 0;
                        else if (activeItem == postsModel.ary_media.size()) activeItem = activeItem - 1;

                        viewHolder.txt_media.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(postsModel.ary_media.size())));

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
                    viewHolder.frame_media_index.setVisibility(View.VISIBLE);

                    if (!pflag_sendActionRequest) {
                        viewHolder.txt_media.setText(String.format("1/%s", String.valueOf(postsModel.ary_media.size())));
                    }

                } else {
                    viewHolder.frame_media_index.setVisibility(View.GONE);
                }

                int finalI = i;
                LinearLayout.LayoutParams img_layout = new LinearLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height);

                if (postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                    final String media_url = postsModel.ary_media.get(i).url;
                    final String media_thumb = postsModel.ary_media.get(i).poster;

                    CustomVideoView videoLayout = new CustomVideoView(mContext, Utility.getLocalVideoFileUrl(mContext, media_url), media_thumb, this.imageLoader);

                    VideoView videoView = videoLayout.getVideoView();
                    videoView.setTag(false);
                    mVideoViews.add(videoView);

                    videoLayout.setLayoutParams(img_layout);

                    RelativeLayout layout = new RelativeLayout(mContext);
                    layout.setLayoutParams(new RelativeLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height));
                    layout.addView(videoLayout);

//                        ZoomLayout zoomLayout = new ZoomLayout(context);
//                        zoomLayout.setLayoutParams(img_layout);
//                        zoomLayout.addView(videoLayout);

                    viewHolder.linear_scroll.addView(layout);

                    VideoZoomy.Builder builder = new VideoZoomy.Builder((Activity) mContext)
                            .target(layout)
                            .enableImmersiveMode(false)
                            .interpolator(new OvershootInterpolator())
                            .tapListener(new VideoTapListener() {
                                @Override
                                public void onTap(View v) {
                                    if (videoView.getTag().equals(false)) {
//                                        videoView.setMute(false);
                                        videoLayout.setMute(false);
                                        videoView.setTag(true);
                                    } else {
//                                        videoView.setMute(true);
                                        videoLayout.setMute(true);
                                        videoView.setTag(false);
                                    }
                                }
                            }).longPressListener(new VideoLongPressListener() {
                                @Override
                                public void onLongPress(View v) {
                                    profileListener.onLongClickImageView(position, finalI);
                                }
                            }).doubleTapListener(new VideoDoubleTapListener() {
                                @Override
                                public void onDoubleTap(View v) {
                                    profileListener.onDoubleClickMediaView(viewHolder.relative_mainfeed, img_layout, position);
                                }
                            });

                    builder.register();

                } else {

                    final ImageView img_post = new ImageView(this.mContext);

                    img_post.setAdjustViewBounds(true);
                    img_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img_post.setLayoutParams(img_layout);

                    Zoomy.Builder builder = new Zoomy.Builder((Activity) mContext)
                            .target(img_post)
                            .enableImmersiveMode(false)
                            .interpolator(new OvershootInterpolator())
                            .tapListener(new TapListener() {
                                @Override
                                public void onTap(View v) {
                                    profileListener.onhideKeyboard();
                                }
                            })
                            .longPressListener(new LongPressListener() {
                                @Override
                                public void onLongPress(View v) {
                                    profileListener.onLongClickImageView(position, finalI);
                                }
                            })
                            .doubleTapListener(new DoubleTapListener() {
                                @Override
                                public void onDoubleTap(View v) {
                                    profileListener.onDoubleClickMediaView(viewHolder.relative_mainfeed, img_layout, position);
                                }
                            });
                    builder.register();

                    viewHolder.linear_scroll.addView(img_post);

                    final String media_url = postsModel.ary_media.get(i).url;

                    this.imageLoader.displayImage(media_url, img_post);

                }
            }

            if (!pflag_sendActionRequest) {

                viewHolder.scrollViewImage.post(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.scrollViewImage.fullScroll(View.FOCUS_UP);
                    }
                });
            }

            //----------show place----------

            if (postsModel.placeModel == null) {
                viewHolder.linear_place.setVisibility(View.GONE);
            } else {
                String str_place = postsModel.placeModel.description;
                if (str_place.length() > 0) {
                    viewHolder.linear_place.setVisibility(View.VISIBLE);
                    viewHolder.txt_place.setText(str_place);
                } else {
                    viewHolder.linear_place.setVisibility(View.GONE);
                }
            }

            viewHolder.linear_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileListener.onClickPlace(position);
                }
            });

            // -------------show Attris----------------

            String str_date = " " + Utility.getTimeAgo(postsModel.created_at);
            String str_attr = postsModel.caption + str_date;
            Spannable spannable_attr = new SpannableString(str_attr);
            spannable_attr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.grey_c6c6c6)), postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable_attr.setSpan(new RelativeSizeSpan(0.84f), postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.txt_attr.setText(spannable_attr, TextView.BufferType.SPANNABLE);


            //------------- show Tags -----------------

            if (postsModel.tags.size() > 0) {

                ArrayList<String> ary_tag = new ArrayList<>();

                for (int i = 0; i < postsModel.tags.size(); i++) {
                    String string = String.format("#%s", postsModel.tags.get(i));
                    ary_tag.add(string);
                }

                viewHolder.linear_tags.setVisibility(View.VISIBLE);
                viewHolder.tagContainerLayout.setTags(ary_tag);

                viewHolder.tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int mposition, String text) {
                        profileListener.onClickTag(text, position);
                    }

                    @Override
                    public void onTagLongClick(int position, String text) {

                    }

                    @Override
                    public void onSelectedTagDrag(int position, String text) {

                    }

                    @Override
                    public void onTagCrossClick(int position) {

                    }
                });

            } else {
                viewHolder.linear_tags.setVisibility(View.GONE);
            }

            //------------- show comments---------------

            if (postsModel.comments_count > 0) {

                if (postsModel.comments_count > 4) {
                    viewHolder.btn_comment.setVisibility(View.VISIBLE);
                    viewHolder.txt_btn_comment.setText(String.format("%s %s %s", mContext.getString(R.string.view_all), String.valueOf(postsModel.comments_count), mContext.getString(R.string.comments)));
                } else {
                    viewHolder.btn_comment.setVisibility(View.GONE);
                }

                creatingCommentView(viewHolder.linear_item_comment, postsModel);
            }

            if (this.pCommented.get(position) == 0) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Utility.g_isScrollState && Utility.g_currentListItem == position  || Utility.g_isScrollState && Utility.g_currentListItem + 1 == position) {

                            pCommented.set(position, 1);

                            if (viewHolder.linear_comment.getVisibility() == View.GONE) {

                                viewHolder.linear_comment.setVisibility(View.VISIBLE);

                                Utility.ViewAnimation(viewHolder.linear_comment, 0, 44, 300);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        pflag_sendActionRequest = false;

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        viewHolder.linear_comment.setLayoutParams(params);
                                        viewHolder.linear_comment.requestLayout();

                                    }
                                }, 350);
                            }
                        }
                    }

                }, 4000);

            } else {
                viewHolder.linear_comment.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utility.dpToPx(44));
//                viewHolder.linear_comment.setLayoutParams(params);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Utility.g_isScrollState && Utility.g_currentListItem == position  || Utility.g_isScrollState && Utility.g_currentListItem + 1 == position) {
                        int promo_id = -1;
                        if (postsModel.promotionModel != null) promo_id = postsModel.promotionModel.id;

                        ImpressionModel impressionModel = Utility.getImpressionFromPost(mContext, postsModel.id, promo_id, 0, Constants.METRICS_IMPRESSION);
                        Utility.saveImpressionToSql(mContext, impressionModel);
                    }
                }

            }, 4000);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).id;
    }

    public void clear() {

        int size = mData.size();

        if (size > 0) {

            for (int i = 0; i < size; i++) {
                mData.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText editTextComment;
        TextView txt_username, txt_level, txt_place, txt_attr, txt_votes, txt_media, txt_userEmo;
        LinearLayout linear_place, linear_attr, linear_tags, linear_comment, linear_item_comment, linear_scroll, linear_commentkey, linear_charity, linear_goods, linear_show_charity_data,
                linear_charity_individual, linear_charity_teaminfo, linear_show_charity, linear_funding_join, linear_upvote, linear_promotion, linear_users_upvoted, linear_promotion_create, linear_promote, linear_view_engage;
        TagContainerLayout tagContainerLayout;
        LinearLayout btn_follow, btn_comment, linear_post_total_won, linear_help, linear_help_reward;
        TextView txt_follow, txt_user_total_won, txt_btn_comment, txt_post_total_won, txt_help_reward;
        ImageButton imageButton_media, btn_sentComment, btn_favourit, btn_gotoallcomment;
        ImageView img_user, btn_votes;
        HorizontalScrollView scrollViewImage;
        FrameLayout frame_media_index, frag_process_background, frag_process_charity;
        RelativeLayout relative_mainfeed;
        ImageView img_user1, img_diamond;
        TextView txt_userEmo1, txt_title , txt_current_charity, txt_goal, txt_left_date, txt_funding_action_type, txt_raised, txt_members, txt_goods_price, txt_goods_type, txt_goods_sales, txt_goods_purchase;
        Button btn_donate_charity;
        ImageView img_more_action;
        TextView txt_teamname, txt_promotion, txt_promote, txt_promotion_statue;
        LinearLayout linear_commerce_warning, linear_digitalgoods;
        TextView txt_commerce_name, txt_commerce_price, txt_commerce_status, txt_commerce_error, txt_commerce_sales;
        FrameLayout frame_commerce_value;

        ViewHolder(View itemView) {

            super(itemView);

            linear_scroll = (LinearLayout)itemView.findViewById(R.id.linear_scroll);
            linear_place = (LinearLayout)itemView.findViewById(R.id.linear_place);
            linear_attr = (LinearLayout)itemView.findViewById(R.id.linear_attr);
            linear_tags = (LinearLayout)itemView.findViewById(R.id.linear_tags);
            linear_comment = (LinearLayout)itemView.findViewById(R.id.linear_comment);
            linear_item_comment = (LinearLayout)itemView.findViewById(R.id.linear_item_comment);
            linear_charity = (LinearLayout)itemView.findViewById(R.id.linear_charity);
            linear_goods = (LinearLayout)itemView.findViewById(R.id.linear_goods);
            txt_goods_price = (TextView)itemView.findViewById(R.id.txt_goods_price);
            txt_goods_type = (TextView)itemView.findViewById(R.id.txt_goods_type);
            txt_goods_sales = (TextView)itemView.findViewById(R.id.txt_goods_sales);
            txt_goods_purchase = (TextView)itemView.findViewById(R.id.txt_goods_purchase);
            linear_users_upvoted = (LinearLayout)itemView.findViewById(R.id.linear_users_upvoted);
            txt_username = (TextView)itemView.findViewById(R.id.txt_username);
            txt_user_total_won = (TextView)itemView.findViewById(R.id.txt_user_total_won);
            txt_post_total_won = (TextView)itemView.findViewById(R.id.txt_post_total_won);
            linear_post_total_won = (LinearLayout)itemView.findViewById(R.id.linear_post_total_won);
            txt_level = (TextView)itemView.findViewById(R.id.txt_level);
            txt_place = (TextView)itemView.findViewById(R.id.txt_place);
            txt_attr = (TextView)itemView.findViewById(R.id.txt_attr);
            txt_votes = (TextView)itemView.findViewById(R.id.txt_votes);
            txt_media = (TextView)itemView.findViewById(R.id.txt_media);
            txt_userEmo = (TextView)itemView.findViewById(R.id.txt_userEmo);
            txt_title = (TextView)itemView.findViewById(R.id.txt_title);
            txt_members = (TextView)itemView.findViewById(R.id.txt_members);
            imageButton_media = (ImageButton)itemView.findViewById(R.id.img_media);
            btn_sentComment = (ImageButton)itemView.findViewById(R.id.btn_sentComment);
            linear_help = (LinearLayout)itemView.findViewById(R.id.linear_help);
            linear_help_reward = (LinearLayout)itemView.findViewById(R.id.linear_help_reward);
            txt_help_reward = (TextView)itemView.findViewById(R.id.txt_help_reward);
            btn_favourit = (ImageButton)itemView.findViewById(R.id.btn_favourit);
            btn_votes = (ImageView)itemView.findViewById(R.id.btn_votes);
            linear_upvote = (LinearLayout)itemView.findViewById(R.id.linear_upvote);
            tagContainerLayout = (TagContainerLayout)itemView.findViewById(R.id.tagcontainerLayout);
            btn_comment = (LinearLayout) itemView.findViewById(R.id.btn_comment);
            txt_btn_comment = (TextView)itemView.findViewById(R.id.txt_btn_comment);
            btn_follow = (LinearLayout) itemView.findViewById(R.id.btn_follow);
            txt_follow = (TextView)itemView.findViewById(R.id.txt_follow);
            img_user = (ImageView)itemView.findViewById(R.id.img_user);
            scrollViewImage = (HorizontalScrollView)itemView.findViewById(R.id.scrollViewImage);
            editTextComment = (EditText)itemView.findViewById(R.id.editTextComment);
            frame_media_index = (FrameLayout)itemView.findViewById(R.id.frame_media_index);
            relative_mainfeed = (RelativeLayout)itemView.findViewById(R.id.relative_mainfeed);
            frag_process_charity = (FrameLayout) itemView.findViewById(R.id.frag_process_charity);
            linear_commentkey = (LinearLayout)itemView.findViewById(R.id.linear_commentkey);
            btn_gotoallcomment = (ImageButton)itemView.findViewById(R.id.btn_gotoallcomment);
            img_user1 = (ImageView)itemView.findViewById(R.id.img_user1);
            img_diamond = (ImageView)itemView.findViewById(R.id.img_diamond);
            txt_userEmo1 = (TextView)itemView.findViewById(R.id.txt_userEmo1);
            btn_donate_charity = (Button)itemView.findViewById(R.id.btn_donate_charity);
            txt_current_charity = (TextView)itemView.findViewById(R.id.txt_current_charity);
            txt_goal = (TextView)itemView.findViewById(R.id.txt_goal);
            txt_left_date = (TextView)itemView.findViewById(R.id.txt_left_date);
            txt_funding_action_type = (TextView)itemView.findViewById(R.id.txt_funding_action_type);
            frag_process_background = (FrameLayout)itemView.findViewById(R.id.frag_process_background);
            txt_raised = (TextView)itemView.findViewById(R.id.txt_raised);
            linear_show_charity = (LinearLayout) itemView.findViewById(R.id.linear_show_charity);
            linear_show_charity_data = (LinearLayout)itemView.findViewById(R.id.linear_show_charity_data);
            img_more_action = (ImageView)itemView.findViewById(R.id.img_more_action);
            linear_charity_teaminfo = (LinearLayout)itemView.findViewById(R.id.linear_charity_teaminfo);
            linear_funding_join = (LinearLayout)itemView.findViewById(R.id.linear_funding_join);
            linear_charity_individual = (LinearLayout)itemView.findViewById(R.id.linear_charity_individual);
            txt_teamname = (TextView)itemView.findViewById(R.id.txt_teamname);
            linear_promotion = (LinearLayout)itemView.findViewById(R.id.linear_promotion);
            linear_view_engage = (LinearLayout)itemView.findViewById(R.id.linear_view_engage);
            txt_promotion = (TextView)itemView.findViewById(R.id.txt_promotion);
            txt_promote = (TextView)itemView.findViewById(R.id.txt_promote);
            txt_promotion_statue = (TextView)itemView.findViewById(R.id.txt_promotion_statue);
            linear_promotion_create = (LinearLayout)itemView.findViewById(R.id.linear_promotion_create);
            linear_promote = (LinearLayout)itemView.findViewById(R.id.linear_promote);
            linear_commerce_warning = (LinearLayout)itemView.findViewById(R.id.linear_commerce_warning);
            linear_digitalgoods = (LinearLayout)itemView.findViewById(R.id.linear_digitalgoods);
            txt_commerce_name = (TextView)itemView.findViewById(R.id.txt_commerce_name);
            txt_commerce_price = (TextView)itemView.findViewById(R.id.txt_commerce_price);
            txt_commerce_status = (TextView)itemView.findViewById(R.id.txt_commerce_status);
            txt_commerce_sales = (TextView)itemView.findViewById(R.id.txt_commerce_sales);
            txt_commerce_error = (TextView)itemView.findViewById(R.id.txt_commerce_error);
            frame_commerce_value = (FrameLayout)itemView.findViewById(R.id.frame_commerce_value);

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
            spannable_super.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.colorBlack)), 0, commentsModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView txt_superComment = new TextView(this.mContext);
            LinearLayout.LayoutParams txt_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txt_layout.setMargins(0, 10, 0, 0);
            txt_superComment.setLayoutParams(txt_layout);
            txt_superComment.setTextColor(this.mContext.getResources().getColor(R.color.colorLightGray));
            txt_superComment.setText(spannable_super, TextView.BufferType.SPANNABLE);
            txt_superComment.setTextSize(12);

            txt_superComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileListener.onShowingUserInfoWithUserinfo(commentsModel.userModel);
                }
            });

            linearLayout.addView(txt_superComment);

            if (commentsModel.ary_replies != null && commentsModel.ary_replies.size() > 0) {

                LinearLayout linear_reply = new LinearLayout(this.mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 5, 0, 0);
                linear_reply.setLayoutParams(layoutParams);

                linearLayout.addView(linear_reply);

                View view = new View(this.mContext);
                LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(Utility.dpToPx(1), LinearLayout.LayoutParams.MATCH_PARENT);
                view_params.setMargins(0, 5, 0, 5);
                view.setLayoutParams(view_params);
                view.setBackgroundColor(this.mContext.getResources().getColor(R.color.line_background_color));

                linear_reply.addView(view);

                LinearLayout linear_reply_item = new LinearLayout(this.mContext);
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
                    spannable.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.colorBlack)), 0, commentReplyModel.user.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    TextView txt_Comment = new TextView(this.mContext);
                    LinearLayout.LayoutParams txt_Commentlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        txt_Commentlayout.setMargins(0, Utility.dpToPx(10), 0, 0);
                    txt_Comment.setLayoutParams(txt_Commentlayout);
                    txt_Comment.setTextColor(this.mContext.getResources().getColor(R.color.colorLightGray));
                    txt_Comment.setText(spannable, TextView.BufferType.SPANNABLE);
                    txt_Comment.setTextSize(12);

                    txt_Comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileListener.onShowingUserInfoWithUserinfo(commentReplyModel.user);
                        }
                    });

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

}