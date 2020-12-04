package com.star.pibbledev.chatroom.adaptar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.home.digitalgoods.utility.LinearLayoutScrollViewItem;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.ChatItemModel;
import com.star.pibbledev.services.global.model.MediaModel;

import java.util.ArrayList;

public class ChatListAdaptar extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_DIGITAL_GOODS = 3;
    private static final int VIEW_TYPE_GOODS = 4;
    private static final int VIEW_TYPE_SYSTEM = 5;

    private Context mContext;
    private ArrayList<ChatItemModel> aryChats;
    private ImageLoader mImageLoader;
    private GoodsListener mClickListener;
    private boolean mIsSeller;

    public ChatListAdaptar(Context context, ArrayList<ChatItemModel> arrayList, ImageLoader imageLoader, boolean isSeller) {

        mContext = context;
        aryChats = arrayList;
        mImageLoader = imageLoader;
        mIsSeller = isSeller;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_DIGITAL_GOODS) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_digitalgoods_commerce_info, parent, false);
            return new  DigitalGoodsHolder(view);

        } else if (viewType == VIEW_TYPE_GOODS) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_goods_info, parent, false);
            return new GoodsHolder(view);

        } else if (viewType == VIEW_TYPE_SYSTEM) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_system, parent, false);
            return new SystemMessageHolder(view);

        } else if (viewType == VIEW_TYPE_SENT) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_RECEIVED) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_member, parent, false);
            return new ReceivedMessageHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatItemModel chatItemModel = aryChats.get(position);

        switch (holder.getItemViewType()) {

            case VIEW_TYPE_DIGITAL_GOODS:

                ((DigitalGoodsHolder) holder).bind(chatItemModel);

                break;

            case VIEW_TYPE_GOODS:

                ((GoodsHolder) holder).bind(chatItemModel);

                break;

            case VIEW_TYPE_SYSTEM:

                ((SystemMessageHolder) holder).bind(chatItemModel);

                break;

            case VIEW_TYPE_SENT:

                if (position == 0) ((SentMessageHolder) holder).bind(chatItemModel, null);
                else ((SentMessageHolder) holder).bind(chatItemModel, aryChats.get(position - 1));

                break;

            case VIEW_TYPE_RECEIVED:

                if (position == 0) ((ReceivedMessageHolder)holder).bind(chatItemModel, null);
                else ((ReceivedMessageHolder)holder).bind(chatItemModel, aryChats.get(position - 1));

                break;
        }

    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView txt_date, txt_createTime, txt_message;

        SentMessageHolder(View itemView) {
            super(itemView);

            txt_date = (TextView)itemView.findViewById(R.id.txt_date);
            txt_date.setVisibility(View.GONE);
            txt_createTime = (TextView)itemView.findViewById(R.id.txt_createTime);
            txt_message = (TextView)itemView.findViewById(R.id.txt_message);
        }

        void bind(ChatItemModel chatItemModel, ChatItemModel oldChatItem) {

            if (oldChatItem == null) {

                txt_date.setVisibility(View.VISIBLE);
                txt_date.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, MMM d"));

            } else {

                if (isEqualDate(chatItemModel.created_at, oldChatItem.created_at)) {

                    txt_date.setVisibility(View.GONE);

                } else {
                    txt_date.setVisibility(View.VISIBLE);
                    txt_date.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, MMM d"));
                }

            }

            txt_message.setText(chatItemModel.txtMessage);
            txt_createTime.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","hh:mm aa"));

        }

    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView txt_date, txt_emo, txt_message, txt_createTime;
        ImageView img_user;
        FrameLayout frame_avatar;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            txt_date = (TextView)itemView.findViewById(R.id.txt_date);
            txt_date.setVisibility(View.GONE);
            txt_emo = (TextView)itemView.findViewById(R.id.txt_emo);
            txt_message = (TextView)itemView.findViewById(R.id.txt_message);
            txt_createTime = (TextView)itemView.findViewById(R.id.txt_createTime);
            img_user = (ImageView)itemView.findViewById(R.id.img_user);
            frame_avatar = (FrameLayout)itemView.findViewById(R.id.frame_avatar);
        }

        void bind(ChatItemModel chatItemModel, ChatItemModel oldChatItem) {

            if (chatItemModel.fromUser.avatar.equals("null")) {

                txt_emo.setVisibility(View.VISIBLE);
                img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[chatItemModel.fromUser.avatar_temp]));
                txt_emo.setText(Utility.getUserEmoName(chatItemModel.fromUser.username));

            } else {
                Glide.with(mContext)
                        .load(chatItemModel.fromUser.avatar)
                        .into(img_user);
                txt_emo.setVisibility(View.GONE);
            }

            if (oldChatItem == null) {

                txt_date.setVisibility(View.VISIBLE);
                txt_date.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, MMM d"));

            } else {

                if (isEqualDate(chatItemModel.created_at, oldChatItem.created_at)) {

                    txt_date.setVisibility(View.GONE);

                } else {
                    txt_date.setVisibility(View.VISIBLE);
                    txt_date.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, MMM d"));
                }

            }

            txt_message.setText(chatItemModel.txtMessage);
            txt_createTime.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "hh:mm aa"));

            frame_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) mClickListener.OnClickUserAvatar(chatItemModel.fromUser.id, chatItemModel.fromUser.username);
                }
            });

        }

    }

    private class DigitalGoodsHolder extends RecyclerView.ViewHolder {

        TextView txt_pagecount, txt_name, txt_commerce_price, txt_reward, txt_buyitem, txt_sales;
        LinearLayout linear_imageinfo, linear_buy_item;
        HorizontalScrollView scrollview;

        int imageIndex;

        DigitalGoodsHolder(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_commerce_price = (TextView) itemView.findViewById(R.id.txt_commerce_price);
            txt_reward = (TextView) itemView.findViewById(R.id.txt_reward);
            txt_pagecount = (TextView)itemView.findViewById(R.id.txt_pagecount);
            txt_buyitem = (TextView) itemView.findViewById(R.id.txt_buyitem);
            txt_sales = (TextView) itemView.findViewById(R.id.txt_sales);
            linear_imageinfo = (LinearLayout)itemView.findViewById(R.id.linear_imageinfo);
            linear_buy_item = (LinearLayout)itemView.findViewById(R.id.linear_buy_item);
            scrollview = (HorizontalScrollView)itemView.findViewById(R.id.scrollview);

        }

        void bind(ChatItemModel chatItemModel) {

            txt_name.setText(chatItemModel.postMessage.commerceModel.name);
            txt_commerce_price.setText(String.valueOf(chatItemModel.postMessage.commerceModel.price));
            txt_reward.setText(String.valueOf(chatItemModel.postMessage.commerceModel.price * 0.15));
            txt_sales.setText(String.format("%s %s", String.valueOf(chatItemModel.postMessage.sales), mContext.getString(R.string.sales)));

            if (chatItemModel.postMessage.user.id == Utility.getReadPref(mContext).getIntValue(Constants.ID)) linear_buy_item.setVisibility(View.GONE);
            else linear_buy_item.setVisibility(View.VISIBLE);

            if (chatItemModel.postMessage.invoiceModel != null && chatItemModel.postMessage.invoiceModel.status.equals(Constants.INVOICE_ACCEPTED)) {

                linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.colorMain));

                if (chatItemModel.postMessage.commerceModel.is_downloadable) {
                    txt_buyitem.setText(mContext.getString(R.string.download_your_order));
                } else {
                    txt_buyitem.setText(mContext.getString(R.string.view_your_order));
                }


                linear_buy_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mClickListener != null) mClickListener.onClickDownload(chatItemModel, imageIndex);

                    }
                });

            } else {

                txt_buyitem.setText(mContext.getString(R.string.buy_item_small));
                linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.colorMain));

                linear_buy_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mClickListener != null) mClickListener.onClickBuyItem(chatItemModel);

                        linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_creating_invoice));
                        txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.white));
                        txt_buyitem.setText(R.string.invoice_requesting);

                    }
                });
            }

            setImageInfo(chatItemModel.postMessage.ary_media);

        }

        @SuppressLint("ClickableViewAccessibility")
        void setImageInfo(ArrayList<MediaModel> arrayList) {

            for (int i = 0; i < arrayList.size(); i++) {

                MediaModel mediaModel = arrayList.get(i);

                String resolution = String.valueOf(mediaModel.original_width) + " x " + String.valueOf(mediaModel.original_height);
                String dpi = "300" + " (" + String.valueOf((int)(mediaModel.original_width * Constants.unitPxToinch)) + "cm" + " x " + String.valueOf((int)(mediaModel.original_height * Constants.unitPxToinch)) + "cm" + ")";

                LinearLayoutScrollViewItem item = new LinearLayoutScrollViewItem(mContext, mImageLoader, mediaModel.thumbnail, resolution, dpi, "JPEG");
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utility.g_deviceWidth - Utility.dpToPx(92), Utility.dpToPx(100));
                item.setLayoutParams(layoutParams);

                linear_imageinfo.addView(item);
            }

            if (arrayList.size() == 1) {
                txt_pagecount.setVisibility(View.INVISIBLE);
            }

            scrollview.setOnTouchListener(new View.OnTouchListener() {

                int down_scrollX, up_scrollX;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        down_scrollX = scrollview.getScrollX();

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                        up_scrollX = scrollview.getScrollX();
                        int itemWidth = scrollview.getMeasuredWidth();
                        int activeItem = ((up_scrollX + itemWidth / 2) / itemWidth);

                        if (up_scrollX - down_scrollX > 0) {
                            activeItem = activeItem + 1;
                        } else if (up_scrollX -down_scrollX < 0) {
                            activeItem = activeItem - 1;
                        }

                        int scrollTo = activeItem * itemWidth;

                        ObjectAnimator animator=ObjectAnimator.ofInt(scrollview, "scrollX",scrollTo );
                        animator.setDuration(300);
                        animator.start();

                        if (activeItem == -1) activeItem = 0;
                        else if (activeItem == arrayList.size()) activeItem = activeItem - 1;

                        txt_pagecount.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(arrayList.size())));
                        imageIndex = activeItem;

                        return true;
                    }

                    return false;
                }
            });

        }

    }

    private class GoodsHolder extends RecyclerView.ViewHolder {

        TextView txt_name, txt_price, txt_site, txt_sales, txt_buyitem, txt_pagecount;
        LinearLayout linear_buy_item, linear_site, linear_imageinfo;
//        ImageView img_goods;
        HorizontalScrollView scrollview;

        GoodsHolder(View itemView) {
            super(itemView);

            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_sales = (TextView) itemView.findViewById(R.id.txt_sales);
            txt_site = (TextView) itemView.findViewById(R.id.txt_site);
            txt_buyitem = (TextView)itemView.findViewById(R.id.txt_buyitem);
//            img_goods = (ImageView)itemView.findViewById(R.id.img_goods);
            linear_buy_item = (LinearLayout)itemView.findViewById(R.id.linear_buy_item);
            linear_site = (LinearLayout)itemView.findViewById(R.id.linear_site);
            scrollview = (HorizontalScrollView)itemView.findViewById(R.id.scrollview);
            linear_imageinfo = (LinearLayout)itemView.findViewById(R.id.linear_imageinfo);
            txt_pagecount = (TextView)itemView.findViewById(R.id.txt_pagecount);

        }

        void bind(ChatItemModel chatItemModel) {

            txt_name.setText(chatItemModel.postMessage.goodsModel.title);
            txt_price.setText(chatItemModel.postMessage.goodsModel.price);
            txt_sales.setText(String.format("%s %s", String.valueOf(chatItemModel.postMessage.sales), mContext.getString(R.string.sales)));

            if (chatItemModel.goods_order_statue == null) {

                if (chatItemModel.postMessage.goodsModel.status == null) {

                    txt_buyitem.setText(mContext.getString(R.string.buy_item_small));
                    linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                    txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                    linear_buy_item.setEnabled(true);

                } else {

                    switch (chatItemModel.postMessage.goodsModel.status) {

                        case Constants.GOODS_FREE:

                            txt_buyitem.setText(mContext.getString(R.string.buy_item_small));
                            linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                            txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                            linear_buy_item.setEnabled(true);

                            break;
                        case Constants.GOODS_BOOKED:

                            txt_buyitem.setText(mContext.getString(R.string.booked));
                            linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_background_gray));
                            txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.white));
                            linear_buy_item.setEnabled(false);

                            break;
                        case Constants.GOODS_SOLD:

                            txt_buyitem.setText(mContext.getString(R.string.sold_out));
                            linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_background_gray));
                            txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.white));
                            linear_buy_item.setEnabled(false);

                            break;
                    }
                }

            } else {

                if (chatItemModel.goods_order_statue.equals(Constants.ORDER_STATUE_APPROVE)) {

                    txt_buyitem.setText(mContext.getString(R.string.buy_item_small));
                    linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                    txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                    linear_buy_item.setEnabled(true);

                } else if (chatItemModel.goods_order_statue.equals(Constants.ORDER_STATUE_CONFIRM)) {

                    txt_buyitem.setText(mContext.getString(R.string.confirmed));
                    linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                    txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.black));
                    linear_buy_item.setEnabled(false);

                } else {

                    linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_creating_invoice));
                    txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.white));
                    txt_buyitem.setText(R.string.waiting_buyer_approval);

                    linear_buy_item.setEnabled(false);
                }
            }

//            mImageLoader.displayImage(chatItemModel.postMessage.ary_media.get(0).thumbnail, img_goods);

            setImageInfo(chatItemModel.postMessage.ary_media);

            if (chatItemModel.postMessage.goodsModel.site != null && chatItemModel.postMessage.goodsModel.site.length() > 0) {

                txt_site.setText(chatItemModel.postMessage.goodsModel.site);

                linear_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Utility.showWebviewDialog(mContext, chatItemModel.postMessage.goodsModel.site);
                    }
                });

            } else {

                linear_site.setVisibility(View.GONE);
            }

            linear_buy_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mClickListener != null) mClickListener.onClickGoodsBuyItem(chatItemModel);

                    linear_buy_item.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_creating_invoice));
                    txt_buyitem.setTextColor(mContext.getResources().getColor(R.color.white));
                    txt_buyitem.setText(R.string.invoice_requesting);
                }
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        void setImageInfo(ArrayList<MediaModel> arrayList) {

            for (int i = 0; i < arrayList.size(); i++) {

                MediaModel mediaModel = arrayList.get(i);

//                String resolution = String.valueOf(mediaModel.original_width) + " x " + String.valueOf(mediaModel.original_height);
//                String dpi = "300" + " (" + String.valueOf((int)(mediaModel.original_width * Constants.unitPxToinch)) + "cm" + " x " + String.valueOf((int)(mediaModel.original_height * Constants.unitPxToinch)) + "cm" + ")";

                LinearLayout linearLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utility.g_deviceWidth - Utility.dpToPx(92), LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);

                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(Utility.dpToPx(130), Utility.dpToPx(90));
                imageView.setLayoutParams(imgParams);

                mImageLoader.displayImage(mediaModel.thumbnail, imageView);

                linearLayout.addView(imageView);

                linear_imageinfo.addView(linearLayout);
            }

            if (arrayList.size() == 1) {
                txt_pagecount.setVisibility(View.INVISIBLE);
            }

            scrollview.setOnTouchListener(new View.OnTouchListener() {

                int down_scrollX, up_scrollX;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        down_scrollX = scrollview.getScrollX();

                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                        up_scrollX = scrollview.getScrollX();
                        int itemWidth = scrollview.getMeasuredWidth();
                        int activeItem = ((up_scrollX + itemWidth / 2) / itemWidth);

                        if (up_scrollX - down_scrollX > 0) {
                            activeItem = activeItem + 1;
                        } else if (up_scrollX -down_scrollX < 0) {
                            activeItem = activeItem - 1;
                        }

                        int scrollTo = activeItem * itemWidth;

                        ObjectAnimator animator=ObjectAnimator.ofInt(scrollview, "scrollX",scrollTo );
                        animator.setDuration(300);
                        animator.start();

                        if (activeItem == -1) activeItem = 0;
                        else if (activeItem == arrayList.size()) activeItem = activeItem - 1;

                        txt_pagecount.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(arrayList.size())));
//                        imageIndex = activeItem;

                        return true;
                    }

                    return false;
                }
            });

        }
    }

    private class SystemMessageHolder extends RecyclerView.ViewHolder {

        TextView txt_message, txt_createTime, txt_emo, txt_return;
        FrameLayout frame_avatar;
        LinearLayout linear_return;
        ImageView img_user;

        SystemMessageHolder(View itemView) {
            super(itemView);

            txt_message = (TextView)itemView.findViewById(R.id.txt_message);
            txt_createTime = (TextView)itemView.findViewById(R.id.txt_createTime);
            txt_emo = (TextView)itemView.findViewById(R.id.txt_emo);
            txt_return = (TextView)itemView.findViewById(R.id.txt_return);
            frame_avatar = (FrameLayout)itemView.findViewById(R.id.frame_avatar);
            img_user = (ImageView)itemView.findViewById(R.id.img_user);
            linear_return = (LinearLayout)itemView.findViewById(R.id.linear_return);
        }

        void bind(ChatItemModel chatItemModel) {

            txt_message.setText(chatItemModel.txtMessage);
            txt_createTime.setText(Utility.getTypeFromDate(chatItemModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "hh:mm aa"));

            if (chatItemModel.toUser != null) {

                frame_avatar.setVisibility(View.VISIBLE);

                frame_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mClickListener != null) mClickListener.OnClickUserAvatar(chatItemModel.toUser.id, chatItemModel.toUser.username);
                    }
                });

                if (chatItemModel.toUser.avatar.equals("null")) {

                    txt_emo.setVisibility(View.VISIBLE);
                    img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[chatItemModel.toUser.avatar_temp]));
                    txt_emo.setText(Utility.getUserEmoName(chatItemModel.toUser.username));

                } else {
                    Glide.with(mContext)
                            .load(chatItemModel.toUser.avatar)
                            .into(img_user);
                    txt_emo.setVisibility(View.GONE);
                }

            } else {

                frame_avatar.setVisibility(View.GONE);

            }

            if (chatItemModel.system_msg_type != null && chatItemModel.system_msg_type.equals(Constants.CHAT_SYSTEM_RETURN_REQUEST)) {

                linear_return.setVisibility(View.VISIBLE);
                txt_message.setTextColor(mContext.getResources().getColor(R.color.red));

                int order_id = aryChats.get(getItemCount() - 1).goods_order_id;
                String order_status = aryChats.get(getItemCount() - 1).goods_order_statue;

                if (mIsSeller) {

                    if (order_id == chatItemModel.goods_order_id && order_status != null && order_status.equals(Constants.ORDER_STATUE_RETURN)) { // whenever create new order, order_id is updated

                        linear_return.setEnabled(true);

                        linear_return.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                        txt_return.setText(mContext.getString(R.string.approve_return));
                        txt_return.setTextColor(mContext.getResources().getColor(R.color.colorMain));

                        if (chatItemModel.goods_order_statue.equals(Constants.ORDER_STATUE_RETURN)) {

                            linear_return.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    linear_return.setEnabled(false);

                                    linear_return.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                                    txt_return.setText(R.string.return_approved);
                                    txt_return.setTextColor(mContext.getResources().getColor(R.color.line_background_color));

                                    if (mClickListener != null) mClickListener.onClickApproveReturn(chatItemModel.goods_order_id);
                                }
                            });
                        }

                    } else {

                        linear_return.setEnabled(false);

                        linear_return.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                        txt_return.setText(R.string.return_approved);
                        txt_return.setTextColor(mContext.getResources().getColor(R.color.line_background_color));
                    }

                } else {

                    linear_return.setEnabled(false);

                    linear_return.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                    txt_return.setTextColor(mContext.getResources().getColor(R.color.line_background_color));

                    if (order_id == chatItemModel.goods_order_id && order_status != null && order_status.equals(Constants.ORDER_STATUE_RETURN)) {
                        txt_return.setText(R.string.wating_seller_approve);
                    } else {
                        txt_return.setText(R.string.return_approved);
                    }
                }

            } else {

                txt_message.setTextColor(mContext.getResources().getColor(R.color.black));
                linear_return.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public int getItemCount() {
        return aryChats.size();
    }

    @Override
    public int getItemViewType(int position) {

        ChatItemModel chatItemModel = aryChats.get(position);

        switch (chatItemModel.type) {

            case Constants.CHAT_GOODS:

                if (chatItemModel.postMessage.goodsModel != null && chatItemModel.postMessage.type.equals(Constants.GOODS)) {
                    return VIEW_TYPE_GOODS;
                } else if (chatItemModel.postMessage.commerceModel != null && chatItemModel.postMessage.type.equals(Constants.DIGITAL_GOODS)) {
                    return VIEW_TYPE_DIGITAL_GOODS;
                }

                break;
            case Constants.CHAT_TEXT:

                if (chatItemModel.fromUser.username.equals(Utility.getReadPref(mContext).getStringValue(Constants.USERNAME))) {
                    return VIEW_TYPE_SENT;
                } else {
                    return VIEW_TYPE_RECEIVED;
                }

            case Constants.CHAT_SYESTEM:
                return VIEW_TYPE_SYSTEM;
        }

        return 0;
    }

    private boolean isEqualDate(String date1, String date2) {

        return Utility.getTypeFromDate(date1, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MM").equals(Utility.getTypeFromDate(date2, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MM"))
                && Utility.getTypeFromDate(date1, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd").equals(Utility.getTypeFromDate(date2, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd"));

    }

    public void setClickListener(GoodsListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface GoodsListener {

        void onClickBuyItem(ChatItemModel chatItemModel);
        void onClickGoodsBuyItem(ChatItemModel chatItemModel);
        void onClickDownload(ChatItemModel chatItemModel, int index);
        void OnClickUserAvatar(int userid, String username);

        void onClickApproveReturn(int order_id);
    }
}
