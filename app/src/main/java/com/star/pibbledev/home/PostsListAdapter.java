package com.star.pibbledev.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
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
import android.widget.BaseAdapter;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import com.star.pibbledev.R;
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
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.home.utility.videoview.CustomVideoView;
import com.star.pibbledev.home.utility.videoview.VideoView;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class PostsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PostsModel> pData;
    private ArrayList<Integer> pCommented;

    private MainFeedListener pMainfeedListener;

    private boolean pflag_sendActionRequest = false;
    private boolean pflag_upvoteChanged = false;
    private int pupvoteChanged = 0;
    EditText pEditText;

    ArrayList<VideoView> mVideoViews = new ArrayList<>();

    private static LayoutInflater inflater=null;

    private int lastFocussedPosition = -1;
    private Handler handler = new Handler();

    private ImageLoader imageLoader;
//    private ReadMoreOption readMoreOption;

    PostsListAdapter(Context context, ArrayList<PostsModel> pData, ArrayList<Integer> pCommneted, MainFeedListener listener, ImageLoader imageLoader) {

        this.context = context;
        this.pData = pData;
        this.pMainfeedListener = listener;
        this.imageLoader = imageLoader;
        this.pCommented = pCommneted;

//        readMoreOption = new ReadMoreOption.Builder(context)
//                .textLength(3, ReadMoreOption.TYPE_LINE)
//                .moreLabel("more")
//                .moreLabelColor(context.getResources().getColor(R.color.light_gray))
//                .lessLabelColor(context.getResources().getColor(R.color.light_gray))
//                .build();

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if (pData.size() == 0)
            return 0;
        else
            return pData.size();
    }

    @Override
    public Object getItem(int position) {

        return pData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public class ViewHolder {

        EditText editTextComment;
        TextView txt_username, txt_level, txt_place, txt_attr, txt_votes, txt_media, txt_userEmo;
        LinearLayout linear_place, linear_attr, linear_tags, linear_comment, linear_item_comment, linear_scroll, linear_commentkey, linear_goods, linear_charity, linear_show_charity_data,
                linear_charity_individual, linear_show_charity, linear_charity_teaminfo, linear_funding_join, linear_upvote, linear_users_upvoted, linear_promotion, linear_promote, linear_promotion_create, linear_view_engage;
        TagContainerLayout tagContainerLayout;
        LinearLayout btn_comment, linear_help, linear_help_reward;
        LinearLayout btn_follow, linear_post_total_won;
        TextView txt_follow, txt_user_total_won, txt_btn_comment, txt_post_total_won, txt_help_reward;
        ImageButton imageButton_media, btn_sentComment, btn_favourit, btn_gotoallcomment;
        ImageView img_user, btn_votes;
        HorizontalScrollView scrollViewImage;
        FrameLayout frame_media_index, frag_process_background, frag_process_charity;
        RelativeLayout relative_mainfeed;
        ImageView img_user1, img_diamond;
        TextView txt_userEmo1, txt_title , txt_current_charity, txt_goal, txt_raised, txt_members, txt_goods_price, txt_goods_type, txt_goods_sales, txt_goods_purchase;
        Button btn_donate_charity;
        ImageView img_more_action;
        TextView txt_teamname, txt_promotion, txt_promote, txt_promotion_statue, txt_left_date, txt_funding_action_type;
        LinearLayout linear_commerce_warning, linear_digitalgoods;
        TextView txt_commerce_name, txt_commerce_price, txt_commerce_status, txt_commerce_error, txt_commerce_sales;
        FrameLayout frame_commerce_value;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_list_posts, null);

            viewHolder.linear_scroll = (LinearLayout)convertView.findViewById(R.id.linear_scroll);
            viewHolder.linear_place = (LinearLayout)convertView.findViewById(R.id.linear_place);
            viewHolder.linear_attr = (LinearLayout)convertView.findViewById(R.id.linear_attr);
            viewHolder.linear_tags = (LinearLayout)convertView.findViewById(R.id.linear_tags);
            viewHolder.linear_comment = (LinearLayout)convertView.findViewById(R.id.linear_comment);
            viewHolder.linear_item_comment = (LinearLayout)convertView.findViewById(R.id.linear_item_comment);
            viewHolder.linear_goods = (LinearLayout)convertView.findViewById(R.id.linear_goods);
            viewHolder.txt_goods_price = (TextView)convertView.findViewById(R.id.txt_goods_price);
            viewHolder.txt_goods_type = (TextView)convertView.findViewById(R.id.txt_goods_type);
            viewHolder.txt_goods_sales = (TextView)convertView.findViewById(R.id.txt_goods_sales);
            viewHolder.txt_goods_purchase = (TextView)convertView.findViewById(R.id.txt_goods_purchase);
            viewHolder.linear_charity = (LinearLayout)convertView.findViewById(R.id.linear_charity);
            viewHolder.linear_post_total_won = (LinearLayout)convertView.findViewById(R.id.linear_post_total_won);
            viewHolder.txt_username = (TextView)convertView.findViewById(R.id.txt_username);
            viewHolder.txt_user_total_won = (TextView)convertView.findViewById(R.id.txt_user_total_won);
            viewHolder.txt_post_total_won = (TextView)convertView.findViewById(R.id.txt_post_total_won);
            viewHolder.txt_level = (TextView)convertView.findViewById(R.id.txt_level);
            viewHolder.txt_place = (TextView)convertView.findViewById(R.id.txt_place);
            viewHolder.txt_attr = (TextView)convertView.findViewById(R.id.txt_attr);
            viewHolder.txt_votes = (TextView)convertView.findViewById(R.id.txt_votes);
            viewHolder.txt_media = (TextView)convertView.findViewById(R.id.txt_media);
            viewHolder.txt_userEmo = (TextView)convertView.findViewById(R.id.txt_userEmo);
            viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txt_title);
            viewHolder.txt_members = (TextView)convertView.findViewById(R.id.txt_members);
            viewHolder.imageButton_media = (ImageButton)convertView.findViewById(R.id.img_media);
            viewHolder.btn_sentComment = (ImageButton)convertView.findViewById(R.id.btn_sentComment);
            viewHolder.btn_favourit = (ImageButton)convertView.findViewById(R.id.btn_favourit);
            viewHolder.btn_votes = (ImageView)convertView.findViewById(R.id.btn_votes);
            viewHolder.linear_upvote = (LinearLayout)convertView.findViewById(R.id.linear_upvote);
            viewHolder.linear_users_upvoted = (LinearLayout)convertView.findViewById(R.id.linear_users_upvoted);
            viewHolder.tagContainerLayout = (TagContainerLayout)convertView.findViewById(R.id.tagcontainerLayout);
            viewHolder.btn_comment = (LinearLayout) convertView.findViewById(R.id.btn_comment);
            viewHolder.txt_btn_comment = (TextView)convertView.findViewById(R.id.txt_btn_comment);
            viewHolder.btn_follow = (LinearLayout) convertView.findViewById(R.id.btn_follow);
            viewHolder.txt_follow = (TextView)convertView.findViewById(R.id.txt_follow);
            viewHolder.img_user = (ImageView)convertView.findViewById(R.id.img_user);
            viewHolder.img_diamond = (ImageView)convertView.findViewById(R.id.img_diamond);
            viewHolder.scrollViewImage = (HorizontalScrollView)convertView.findViewById(R.id.scrollViewImage);
            viewHolder.editTextComment = (EditText)convertView.findViewById(R.id.editTextComment);
            viewHolder.frame_media_index = (FrameLayout)convertView.findViewById(R.id.frame_media_index);
            viewHolder.relative_mainfeed = (RelativeLayout)convertView.findViewById(R.id.relative_mainfeed);
            viewHolder.frag_process_charity = (FrameLayout) convertView.findViewById(R.id.frag_process_charity);
            viewHolder.linear_commentkey = (LinearLayout)convertView.findViewById(R.id.linear_commentkey);
            viewHolder.btn_gotoallcomment = (ImageButton)convertView.findViewById(R.id.btn_gotoallcomment);
            viewHolder.linear_help = (LinearLayout) convertView.findViewById(R.id.linear_help);
            viewHolder.linear_help_reward = (LinearLayout)convertView.findViewById(R.id.linear_help_reward);
            viewHolder.txt_help_reward = (TextView)convertView.findViewById(R.id.txt_help_reward);
            viewHolder.img_user1 = (ImageView)convertView.findViewById(R.id.img_user1);
            viewHolder.txt_userEmo1 = (TextView)convertView.findViewById(R.id.txt_userEmo1);
            viewHolder.btn_donate_charity = (Button)convertView.findViewById(R.id.btn_donate_charity);
            viewHolder.txt_current_charity = (TextView)convertView.findViewById(R.id.txt_current_charity);
            viewHolder.txt_goal = (TextView)convertView.findViewById(R.id.txt_goal);
            viewHolder.frag_process_background = (FrameLayout)convertView.findViewById(R.id.frag_process_background);
            viewHolder.txt_raised = (TextView)convertView.findViewById(R.id.txt_raised);
            viewHolder.linear_show_charity = (LinearLayout) convertView.findViewById(R.id.linear_show_charity);
            viewHolder.linear_show_charity_data = (LinearLayout)convertView.findViewById(R.id.linear_show_charity_data);
            viewHolder.img_more_action = (ImageView)convertView.findViewById(R.id.img_more_action);
            viewHolder.linear_charity_teaminfo = (LinearLayout)convertView.findViewById(R.id.linear_charity_teaminfo);
            viewHolder.linear_charity_individual = (LinearLayout)convertView.findViewById(R.id.linear_charity_individual);
            viewHolder.linear_funding_join = (LinearLayout)convertView.findViewById(R.id.linear_funding_join);
            viewHolder.txt_teamname = (TextView)convertView.findViewById(R.id.txt_teamname);
            viewHolder.linear_promotion = (LinearLayout)convertView.findViewById(R.id.linear_promotion);
            viewHolder.txt_promotion = (TextView)convertView.findViewById(R.id.txt_promotion);
            viewHolder.linear_promote = (LinearLayout)convertView.findViewById(R.id.linear_promote);
            viewHolder.txt_promote = (TextView)convertView.findViewById(R.id.txt_promote);
            viewHolder.txt_promotion_statue = (TextView)convertView.findViewById(R.id.txt_promotion_statue);
            viewHolder.txt_left_date = (TextView)convertView.findViewById(R.id.txt_left_date);
            viewHolder.txt_funding_action_type = (TextView)convertView.findViewById(R.id.txt_funding_action_type);
            viewHolder.linear_promotion_create = (LinearLayout)convertView.findViewById(R.id.linear_promotion_create);
            viewHolder.linear_view_engage = (LinearLayout)convertView.findViewById(R.id.linear_view_engage);
            viewHolder.linear_commerce_warning = (LinearLayout)convertView.findViewById(R.id.linear_commerce_warning);
            viewHolder.linear_digitalgoods = (LinearLayout)convertView.findViewById(R.id.linear_digitalgoods);
            viewHolder.txt_commerce_name = (TextView)convertView.findViewById(R.id.txt_commerce_name);
            viewHolder.txt_commerce_price = (TextView)convertView.findViewById(R.id.txt_commerce_price);
            viewHolder.txt_commerce_status = (TextView)convertView.findViewById(R.id.txt_commerce_status);
            viewHolder.txt_commerce_error = (TextView)convertView.findViewById(R.id.txt_commerce_error);
            viewHolder.txt_commerce_sales = (TextView)convertView.findViewById(R.id.txt_commerce_sales);
            viewHolder.frame_commerce_value = (FrameLayout)convertView.findViewById(R.id.frame_commerce_value);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.editTextComment.setTag(String.valueOf(position));

        initPostView(viewHolder, position);

        return convertView;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void initPostView(ViewHolder viewHolder, int position) {

        final PostsModel postsModel = (PostsModel) getItem(position);

        boolean isMyPost = false;
        if (postsModel.user.username.equals(Utility.getReadPref(context).getStringValue(Constants.USERNAME))) {
            isMyPost = true;
        }

        if (!Utility.g_isScrollState) {
            pflag_sendActionRequest = false;
        }

        viewHolder.linear_item_comment.removeAllViews();
        viewHolder.linear_comment.setVisibility(View.GONE);
        viewHolder.btn_comment.setVisibility(View.GONE);
//        viewHolder.editTextComment.setText("");
        viewHolder.txt_promotion_statue.setVisibility(View.GONE);

        if (!pflag_sendActionRequest) {
            viewHolder.linear_show_charity_data.setVisibility(View.GONE);
            viewHolder.linear_scroll.removeAllViews();
        }

        //--------------masters image----------

        String avatar = Utility.getReadPref(this.context).getStringValue("avatar");
        String username = Utility.getReadPref(this.context).getStringValue("username");

        if (!avatar.equals("null")) {

            this.imageLoader.displayImage(avatar, viewHolder.img_user1);
            viewHolder.txt_userEmo1.setVisibility(View.INVISIBLE);

        } else {

            int value = 0;
            if (username.length() > 14) value = 14;
            else value = username.length();
            viewHolder.txt_userEmo1.setVisibility(View.VISIBLE);
            viewHolder.img_user1.setImageDrawable(null);
            viewHolder.img_user1.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[value]));
            viewHolder.txt_userEmo1.setText(Utility.getUserEmoName(username));

        }

        //------------prize----------

        if (postsModel.user.prizeAmount > 0) {

            viewHolder.img_diamond.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_diamond_gold));
            viewHolder.txt_user_total_won.setVisibility(View.VISIBLE);
            viewHolder.txt_user_total_won.setText(String.valueOf(Utility.formatedNumberString(postsModel.user.prizeAmount)));

        } else {

            viewHolder.img_diamond.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_diamond));
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
                pMainfeedListener.onShowActionBar(position);
            }
        });

        //-----------goods--------------

        if (postsModel.type.equals(Constants.GOODS) && postsModel.goodsModel != null) {

            viewHolder.linear_goods.setVisibility(View.VISIBLE);

            String price = Utility.formatedNumberString(Double.parseDouble(postsModel.goodsModel.price));
            viewHolder.txt_goods_price.setText(price + " " + context.getString(R.string.pib));

            if (postsModel.goodsModel.status.equals(Constants.GOODS_FREE)) {
                viewHolder.txt_goods_purchase.setVisibility(View.GONE);
            } else {
                viewHolder.txt_goods_purchase.setVisibility(View.VISIBLE);

                if (postsModel.goodsModel.status.equals(Constants.GOODS_BOOKED)) {
                    viewHolder.txt_goods_purchase.setText(context.getString(R.string.booked));
                } else if (postsModel.goodsModel.status.equals(Constants.GOODS_SOLD)) {
                    viewHolder.txt_goods_purchase.setText(context.getString(R.string.sold_out));
                }
            }

            if (postsModel.goodsModel.is_new == 0) viewHolder.txt_goods_type.setText(context.getString(R.string.used_goods));
            else viewHolder.txt_goods_type.setText(context.getString(R.string.new_lower));

            if (postsModel.sales == 0) viewHolder.txt_goods_sales.setVisibility(View.GONE);
            else {
                viewHolder.txt_goods_sales.setVisibility(View.VISIBLE);
                viewHolder.txt_goods_sales.setText(String.valueOf(postsModel.sales) + " " + context.getString(R.string.sales));
            }

            viewHolder.linear_goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pMainfeedListener.OnClickGoods(position);
                }
            });

        } else {
            viewHolder.linear_goods.setVisibility(View.GONE);
        }

        final int userid = Utility.getReadPref(this.context).getIntValue("id");

        //-----------charity------------

        if (postsModel.fundingModel != null) {

            viewHolder.linear_charity.setVisibility(View.VISIBLE);

            viewHolder.txt_title.setText(postsModel.fundingModel.campaignModel.title);

            int leftDays = Utility.getDates(Utility.getCurrentDate(), postsModel.fundingModel.end_date).size();
            viewHolder.txt_left_date.setText(leftDays + " " + context.getString(R.string.days_left));

            switch (postsModel.type) {

                case Constants.FUNDING_CHARITY:

                    viewHolder.txt_funding_action_type.setText(context.getString(R.string.donate_uppearcase));

                    break;
                case Constants.FUNDING_CROWD_NO_REWARD:

                    viewHolder.txt_funding_action_type.setText(context.getString(R.string.contribute_uppercase));

                    break;
                case Constants.FUNDING_CROWD_REWARD:

                    viewHolder.txt_funding_action_type.setText(context.getString(R.string.pledge_uppercase));

                    break;
            }

            viewHolder.linear_show_charity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Utility.showFundingDetailDialog(context, postsModel, false);
                    pMainfeedListener.showFundingStatus(position);
                }
            });

        } else {
            viewHolder.linear_charity.setVisibility(View.GONE);
        }

        //-----------digitalgoods--------

        if (postsModel.type.equals(Constants.DIGITAL_GOODS) && postsModel.commerceModel != null) {

            boolean finalIsMyPost = isMyPost;
            viewHolder.linear_digitalgoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                        pMainfeedListener.OnClickDigitalGoods(position, finalIsMyPost);

                    }
                }
            });

            viewHolder.linear_digitalgoods.setVisibility(View.VISIBLE);

            viewHolder.txt_commerce_name.setText(postsModel.commerceModel.name);
            viewHolder.txt_commerce_price.setText(String.valueOf(postsModel.commerceModel.price) + " " + context.getString(R.string.pib));

            if (postsModel.commerceModel.status.equals(Constants.SUCCESS)) {

                viewHolder.frame_commerce_value.setVisibility(View.VISIBLE);
                viewHolder.linear_commerce_warning.setVisibility(View.GONE);
                viewHolder.txt_commerce_sales.setText(String.valueOf(postsModel.sales) + " " + context.getString(R.string.sales));
                viewHolder.txt_commerce_status.setText(context.getString(R.string.pib_reward));

            } else {

                if (!isMyPost) viewHolder.linear_digitalgoods.setVisibility(View.GONE);
                else {
                    viewHolder.frame_commerce_value.setVisibility(View.GONE);

                    if (postsModel.commerceModel.status.equals(Constants.FAILED)) {
                        viewHolder.txt_commerce_status.setText(context.getString(R.string.failed));
                        viewHolder.linear_commerce_warning.setVisibility(View.VISIBLE);

                        if (Constants.digitalGoodsError(context, postsModel.commerceModel.error_code) != null) {
                            viewHolder.txt_commerce_error.setText(Constants.digitalGoodsError(context, postsModel.commerceModel.error_code));
                        }

                    } else if (postsModel.commerceModel.status.equals(Constants.WAIT) || postsModel.commerceModel.status.equals(Constants.IN_PROGRESS)) {
                        viewHolder.txt_commerce_status.setText(context.getString(R.string.waiting));
                        viewHolder.linear_commerce_warning.setVisibility(View.GONE);
                    }
                }
            }

        } else {

            viewHolder.linear_digitalgoods.setVisibility(View.GONE);

        }

        //-----------promotion-----------

        if (postsModel.promotionModel != null && postsModel.promotionModel.budget > 0) {

            if (postsModel.promotionModel.is_closed || postsModel.promotionModel.is_paused) {

                viewHolder.linear_promotion.setVisibility(View.GONE);
                viewHolder.linear_promotion_create.setVisibility(View.GONE);

            } else {

                viewHolder.linear_promotion_create.setVisibility(View.GONE);
                viewHolder.linear_promotion.setVisibility(View.VISIBLE);
                viewHolder.linear_promotion.setBackgroundColor(context.getResources().getColor(Utility.g_promotionColors[position % 4]));

                if (postsModel.promotionModel.destination.equals(Constants.PROMOTION_SITE)) {
                    viewHolder.txt_promotion.setText(postsModel.promotionModel.action_button);
                } else {
                    viewHolder.txt_promotion.setText(R.string.visit_your_profile);
                }

                viewHolder.linear_promotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pMainfeedListener.gotoPromotionDetail(position);
                    }
                });
            }

        } else {

            viewHolder.linear_promotion.setVisibility(View.GONE);
            viewHolder.linear_promotion_create.setVisibility(View.GONE);

            if (isMyPost) {

                viewHolder.linear_promotion_create.setVisibility(View.VISIBLE);
                int pos = position % 4;
                viewHolder.linear_promote.setBackground(context.getResources().getDrawable(Utility.g_promoteButtonBackground[pos]));

                viewHolder.linear_promote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pMainfeedListener.createPromotion(position);

                    }
                });

                viewHolder.linear_view_engage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Utility.showPromotionEngageDetailDialog(context, postsModel);

                    }
                });
            }
        }

        //----------- following----------

        if (postsModel.user.id != userid) {

            viewHolder.btn_follow.setVisibility(View.VISIBLE);

            if (postsModel.user.interaction_status != null) {

                if (postsModel.user.interaction_status.is_following) {
                    viewHolder.txt_follow.setText(context.getString(R.string.following));
                    viewHolder.txt_follow.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    viewHolder.txt_follow.setText(context.getString(R.string.follow));
                    viewHolder.txt_follow.setTextColor(context.getResources().getColor(R.color.colorMain));
                }

            } else {
                viewHolder.txt_follow.setText(context.getString(R.string.follow));
                viewHolder.txt_follow.setTextColor(context.getResources().getColor(R.color.colorMain));
            }

        } else {
            viewHolder.btn_follow.setVisibility(View.INVISIBLE);
        }


        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (postsModel.user.id == userid) return;
                pMainfeedListener.onFollow(position);
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
            viewHolder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[postsModel.user.avatar_temp]));
            viewHolder.txt_userEmo.setText(Utility.getUserEmoName(postsModel.user.username));
        }

        viewHolder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pMainfeedListener.onShowingUserInfo(position);
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
                pMainfeedListener.onClickViewAllComment(position, true);
//                pflag_sendActionRequest = true;
            }
        });

        viewHolder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pMainfeedListener.onClickViewAllComment(position, false);
//                pflag_sendActionRequest = true;
            }
        });

        viewHolder.btn_sentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
                pMainfeedListener.onClickSendingComment(viewHolder.editTextComment.getText().toString(), position);
                viewHolder.editTextComment.setText("");
                viewHolder.editTextComment.clearFocus();
//                pflag_sendActionRequest = true;
            }
        });

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

        viewHolder.editTextComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                                lastFocussedPosition = position;
                                viewHolder.editTextComment.requestFocus();

                                pEditText = viewHolder.editTextComment;
                            }
                        }
                    }, 200);
                } else {
                    lastFocussedPosition = -1;
                }
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

                pMainfeedListener.onFavorite(postsModel.favorites, position);

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

                pMainfeedListener.onVoted(position);
                pflag_upvoteChanged = true;
                pupvoteChanged = postsModel.up_votes_amount;
//                pflag_sendActionRequest = true;
            }

            @Override
            public void onDoubleClick(View view) {

                pMainfeedListener.showUpvotedUserPopup(String.valueOf(position));
            }
        }));

        viewHolder.linear_upvote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                pMainfeedListener.showUpvotedUserPopup(String.valueOf(position));

                return true;
            }
        });

        @SuppressLint("DefaultLocale") String str_upVotes = String.format("%d %s", postsModel.up_votes_amount, context.getString(R.string.brushed));

        if (pflag_upvoteChanged) {

            if (postsModel.up_votes_amount != pupvoteChanged) {
                ValueAnimator animator = ValueAnimator.ofInt(pupvoteChanged, postsModel.up_votes_amount);
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        viewHolder.txt_votes.setText(animation.getAnimatedValue().toString());
                    }
                });
                animator.start();
            }

            pflag_upvoteChanged = false;
//            pflag_sendActionRequest = false;

        } else {
            viewHolder.txt_votes.setText(str_upVotes);
        }

        //------------asking help-------------

        if (postsModel.askingHelpModel != null) {

            viewHolder.linear_help_reward.setVisibility(View.VISIBLE);

            if (!postsModel.askingHelpModel.closed) viewHolder.linear_help_reward.setBackground(context.getResources().getDrawable(R.drawable.icon_help_reward_background));
            else viewHolder.linear_help_reward.setBackground(context.getResources().getDrawable(R.drawable.icon_help_reward_background_gray));

            viewHolder.txt_help_reward.setText(Utility.getConvertedValue(String.valueOf(postsModel.askingHelpModel.reward), false));

        } else {

            viewHolder.linear_help_reward.setVisibility(View.GONE);

        }

        viewHolder.linear_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pMainfeedListener.showAskingHelp(position);

            }
        });

        //------------imageScroll-------------

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

                        count_pos = 0;
                        up_scrollX = viewHolder.scrollViewImage.getScrollX();

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

                if (!pflag_sendActionRequest) {

                    int finalI = i;
                    LinearLayout.LayoutParams img_layout = new LinearLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height);

                    if (postsModel.ary_media.get(i).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                        final String media_url = postsModel.ary_media.get(i).url;
                        final String media_thumb = postsModel.ary_media.get(i).poster;

                        CustomVideoView videoLayout = new CustomVideoView(context, Utility.getLocalVideoFileUrl(context, media_url), media_thumb, this.imageLoader);

                        VideoView videoView = videoLayout.getVideoView();
                        videoView.setTag(false);
                        mVideoViews.add(videoView);

                        videoLayout.setLayoutParams(img_layout);

                        RelativeLayout layout = new RelativeLayout(context);
                        layout.setLayoutParams(new RelativeLayout.LayoutParams(Utility.g_deviceWidth, mediaView_height));
                        layout.addView(videoLayout);

//                        ZoomLayout zoomLayout = new ZoomLayout(context);
//                        zoomLayout.setLayoutParams(img_layout);
//                        zoomLayout.addView(videoLayout);

                        viewHolder.linear_scroll.addView(layout);

                        VideoZoomy.Builder builder = new VideoZoomy.Builder((Activity) context)
                                .target(layout)
                                .enableImmersiveMode(false)
                                .interpolator(new OvershootInterpolator())
                                .tapListener(new VideoTapListener() {
                                    @Override
                                    public void onTap(View v) {
                                        if (videoView.getTag().equals(false)) {
//                                            videoView.setMute(false);
                                            videoView.setTag(true);
                                            videoLayout.setMute(false);
                                        } else {
//                                            videoView.setMute(true);
                                            videoLayout.setMute(true);
                                            videoView.setTag(false);
                                        }
                                    }
                                }).longPressListener(new VideoLongPressListener() {
                                    @Override
                                    public void onLongPress(View v) {
                                        pMainfeedListener.onLongClickImageView(position, finalI);
                                    }
                                })
                                .doubleTapListener(new VideoDoubleTapListener() {
                                    @Override
                                    public void onDoubleTap(View v) {
                                        pMainfeedListener.onDoubleClickMediaView(viewHolder.relative_mainfeed, img_layout, position);
                                    }
                                });

                        builder.register();

                    } else {

                        final ImageView img_post = new ImageView(this.context);

                        img_post.setAdjustViewBounds(true);
                        img_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        img_post.setLayoutParams(img_layout);

                        Zoomy.Builder builder = new Zoomy.Builder((Activity) context)
                                .target(img_post)
                                .enableImmersiveMode(false)
                                .interpolator(new OvershootInterpolator())
                                .tapListener(new TapListener() {
                                    @Override
                                    public void onTap(View v) {
                                        pMainfeedListener.hideWalletPreview();
                                    }
                                })
                                .longPressListener(new LongPressListener() {
                                    @Override
                                    public void onLongPress(View v) {
                                        pMainfeedListener.onLongClickImageView(position, finalI);
                                    }
                                })
                                .doubleTapListener(new DoubleTapListener() {
                                    @Override
                                    public void onDoubleTap(View v) {
                                        pMainfeedListener.onDoubleClickMediaView(viewHolder.relative_mainfeed, img_layout, position);
                                    }
                                });

                        builder.register();

                        viewHolder.linear_scroll.addView(img_post);

                        final String media_url = postsModel.ary_media.get(i).url;

                        this.imageLoader.displayImage(media_url, img_post);

                    }
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

        }

        //--------------show location----------------

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
                pMainfeedListener.onClickPlace(position);
            }
        });

        // -------------show Attris----------------

        String str_date = " " + Utility.getTimeAgo(postsModel.created_at);
        String str_attr = postsModel.caption + str_date;
        Spannable spannable_attr = new SpannableString(str_attr);
        spannable_attr.setSpan(new ForegroundColorSpan(this.context.getResources().getColor(R.color.grey_c6c6c6)), postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_attr.setSpan(new RelativeSizeSpan(0.84f), postsModel.caption.length(), str_attr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.txt_attr.setText(spannable_attr, TextView.BufferType.SPANNABLE);


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
                    pMainfeedListener.onClickTag(text, position);
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
                viewHolder.txt_btn_comment.setText(String.format("%s %s %s", context.getString(R.string.view_all), String.valueOf(postsModel.comments_count), context.getString(R.string.comments)));
            } else {
                viewHolder.btn_comment.setVisibility(View.GONE);
            }

            creatingCommentView(viewHolder.linear_item_comment, postsModel);
        }

        //---animation----

        if (this.pCommented.get(position) == 0) {

            viewHolder.linear_comment.setVisibility(View.GONE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Utility.g_isScrollState && Utility.g_currentListItem == position || Utility.g_isScrollState && Utility.g_currentListItem - 1 == position) {

                        if (pCommented.get(position) == 0) {

                            pMainfeedListener.onRunedAnimationComment(position);
                            pflag_sendActionRequest = true;

                        }
                    }
                }
            }, 4000);

        } else {

            if (postsModel.isAnimated) {

                viewHolder.linear_comment.setVisibility(View.VISIBLE);

            } else {

                postsModel.isAnimated = true;
                pData.set(position, postsModel);

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
            spannable_super.setSpan(new ForegroundColorSpan(this.context.getResources().getColor(R.color.colorBlack)), 0, commentsModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView txt_superComment = new TextView(this.context);
            LinearLayout.LayoutParams txt_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txt_layout.setMargins(0, 10, 0, 0);
            txt_superComment.setLayoutParams(txt_layout);
            txt_superComment.setTextColor(this.context.getResources().getColor(R.color.colorLightGray));
            txt_superComment.setText(spannable_super, TextView.BufferType.SPANNABLE);
            txt_superComment.setTextSize(12);

            txt_superComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pMainfeedListener.onShowingUserInfoWithUserinfo(commentsModel.userModel);
                }
            });

            linearLayout.addView(txt_superComment);

            if (commentsModel.ary_replies != null && commentsModel.ary_replies.size() > 0) {

                LinearLayout linear_reply = new LinearLayout(this.context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 5, 0, 0);
                linear_reply.setLayoutParams(layoutParams);

                linearLayout.addView(linear_reply);

                View view = new View(this.context);
                LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(Utility.dpToPx(1), LinearLayout.LayoutParams.MATCH_PARENT);
                view_params.setMargins(0, 5, 0, 5);
                view.setLayoutParams(view_params);
                view.setBackgroundColor(this.context.getResources().getColor(R.color.line_background_color));

                linear_reply.addView(view);

                LinearLayout linear_reply_item = new LinearLayout(this.context);
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
                    spannable.setSpan(new ForegroundColorSpan(this.context.getResources().getColor(R.color.colorBlack)), 0, commentReplyModel.user.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    TextView txt_Comment = new TextView(this.context);
                    LinearLayout.LayoutParams txt_Commentlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        txt_Commentlayout.setMargins(0, Utility.dpToPx(10), 0, 0);
                    txt_Comment.setLayoutParams(txt_Commentlayout);
                    txt_Comment.setTextColor(this.context.getResources().getColor(R.color.colorLightGray));
                    txt_Comment.setText(spannable, TextView.BufferType.SPANNABLE);
                    txt_Comment.setTextSize(12);

                    txt_Comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pMainfeedListener.onShowingUserInfoWithUserinfo(commentReplyModel.user);
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