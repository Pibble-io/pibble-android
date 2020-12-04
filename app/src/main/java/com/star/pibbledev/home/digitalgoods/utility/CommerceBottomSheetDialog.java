package com.star.pibbledev.home.digitalgoods.utility;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.chatroom.activity.ChatRoomActivity;
import com.star.pibbledev.home.HomepageFragment;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MediaModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommerceBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener, RequestListener {

    private Context context;
    private PostsModel mPostModel;

    private static CommerceBottomSheetDialog instance;

    private TextView txt_name, txt_commerce_price, txt_reward, txt_pagecount, txt_buyitem, txt_sales, txt_downloadable;
    private TextView txt_commercial, txt_royalty, txt_editioial, txt_exclusive;
    private ImageView img_commercial, img_royalty, img_editionial, img_exclusive;
    private LinearLayout linear_imageinfo, linear_chatwithseller, linear_buy_item;
    private HorizontalScrollView scrollview;

    public static CommerceBottomSheetDialog getInstance(@NonNull Context context) {
        return instance == null ? new CommerceBottomSheetDialog(context) : instance;
    }

    private CommerceBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    public void create() {

        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_digitalgoods_bottomsheet, null);
        setContentView(bottomSheetView);

        final LinearLayout linearLayout = findViewById(R.id.linear_content);

        if (linearLayout != null) {
            linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)bottomSheetView.getParent());
                    bottomSheetBehavior.setPeekHeight(linearLayout.getMeasuredHeight());
                }
            });
        }

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };

        txt_name = (TextView)bottomSheetView.findViewById(R.id.txt_name);
        txt_commerce_price = (TextView)bottomSheetView.findViewById(R.id.txt_commerce_price);
        txt_reward = (TextView)bottomSheetView.findViewById(R.id.txt_reward);
        txt_commercial = (TextView)bottomSheetView.findViewById(R.id.txt_commercial);
        txt_royalty = (TextView)bottomSheetView.findViewById(R.id.txt_royalty);
        txt_editioial = (TextView)bottomSheetView.findViewById(R.id.txt_editioial);
        txt_exclusive = (TextView)bottomSheetView.findViewById(R.id.txt_exclusive);
        txt_pagecount = (TextView)bottomSheetView.findViewById(R.id.txt_pagecount);
        txt_buyitem = (TextView)bottomSheetView.findViewById(R.id.txt_buyitem);
        txt_buyitem.setText(R.string.buy_item);
        txt_sales = (TextView)bottomSheetView.findViewById(R.id.txt_sales);
        txt_downloadable = (TextView)bottomSheetView.findViewById(R.id.txt_downloadable);

        linear_imageinfo = (LinearLayout)bottomSheetView.findViewById(R.id.linear_imageinfo);
        scrollview = (HorizontalScrollView)bottomSheetView.findViewById(R.id.scrollview);

        img_commercial = (ImageView)bottomSheetView.findViewById(R.id.img_commercial);
        img_royalty = (ImageView)bottomSheetView.findViewById(R.id.img_royalty);
        img_editionial = (ImageView)bottomSheetView.findViewById(R.id.img_editionial);
        img_exclusive = (ImageView)bottomSheetView.findViewById(R.id.img_exclusive);

        linear_chatwithseller = (LinearLayout)bottomSheetView.findViewById(R.id.linear_chatwithseller);
        linear_chatwithseller.setOnClickListener(this);
        linear_buy_item = (LinearLayout)bottomSheetView.findViewById(R.id.linear_buy_item);
        linear_buy_item.setOnClickListener(this);

    }

    public void setCommerceName(String name) {
        this.txt_name.setText(name);
    }

    @SuppressLint("SetTextI18n")
    public void setCommerceSales(int count) {
        this.txt_sales.setText(String.valueOf(count) + " " + context.getString(R.string.sales));
    }

    @SuppressLint("SetTextI18n")
    public void setCommercePrice(int price) {
        this.txt_commerce_price.setText(String.valueOf(price));
        this.txt_reward.setText(String.valueOf(price * 0.15));
    }

    public void setPhotoLicencing(boolean isDownloadable, boolean isCommercial, boolean isRoyalty, boolean isEditional, boolean isExclusive) {

        if (isCommercial) {
            txt_commercial.setTextColor(context.getResources().getColor(R.color.black));
            img_commercial.setVisibility(View.VISIBLE);
        } else {
            txt_commercial.setTextColor(context.getResources().getColor(R.color.light_gray));
            img_commercial.setVisibility(View.INVISIBLE);
        }

        if (isRoyalty) {
            txt_royalty.setTextColor(context.getResources().getColor(R.color.black));
            img_royalty.setVisibility(View.VISIBLE);
        } else {
            txt_royalty.setTextColor(context.getResources().getColor(R.color.light_gray));
            img_royalty.setVisibility(View.INVISIBLE);
        }

        if (isEditional) {
            txt_editioial.setTextColor(context.getResources().getColor(R.color.black));
            img_editionial.setVisibility(View.VISIBLE);
        } else {
            txt_editioial.setTextColor(context.getResources().getColor(R.color.light_gray));
            img_editionial.setVisibility(View.INVISIBLE);
        }

        if (isExclusive) {
            txt_exclusive.setTextColor(context.getResources().getColor(R.color.black));
            img_exclusive.setVisibility(View.VISIBLE);
        } else {
            txt_exclusive.setTextColor(context.getResources().getColor(R.color.light_gray));
            img_exclusive.setVisibility(View.INVISIBLE);
        }

        if (isDownloadable) {
            txt_downloadable.setText(R.string.yes_uppear);
        } else {
            txt_downloadable.setText(R.string.no_uppear);
        }

    }

    public void getPostModel(PostsModel postsModel) {
        this.mPostModel = postsModel;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setImageInfo(ImageLoader imageLoader, ArrayList<MediaModel> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {

            MediaModel mediaModel = arrayList.get(i);

            String resolution = String.valueOf(mediaModel.original_width) + " x " + String.valueOf(mediaModel.original_height);
            String dpi = "300" + " (" + String.valueOf((int)(mediaModel.original_width * Constants.unitPxToinch)) + "cm" + " x " + String.valueOf((int)(mediaModel.original_height * Constants.unitPxToinch)) + "cm" + ")";

            LinearLayoutScrollViewItem item = new LinearLayoutScrollViewItem(context, imageLoader, mediaModel.thumbnail, resolution, dpi, "JPEG");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utility.g_deviceWidth - Utility.dpToPx(32), Utility.dpToPx(100));
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

//                            viewHolder.scrollViewImage.smoothScrollTo(scrollTo, 0);
                    ObjectAnimator animator=ObjectAnimator.ofInt(scrollview, "scrollX",scrollTo );
                    animator.setDuration(300);
                    animator.start();

                    if (activeItem == -1) activeItem = 0;
                    else if (activeItem == arrayList.size()) activeItem = activeItem - 1;

                    txt_pagecount.setText(String.format("%s/%s", String.valueOf(activeItem + 1), String.valueOf(arrayList.size())));

                    return true;
                }

                return false;
            }
        });

    }

    private void createInvoiceBuyItem() {

        if (Constants.isLifeToken(context)) {

            if (mPostModel != null) {

                String token = Utility.getReadPref(context).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().createInvoiceBuyItem(this, context, token, mPostModel.user.uuid, mPostModel.commerceModel.price, "pib", "", "digital_goods", mPostModel.id);

            }

        } else {
            Constants.requestRefreshToken(context, this);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == linear_chatwithseller) {

            dismiss();

            Intent intent = new Intent(context, ChatRoomActivity.class);
            intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
            intent.putExtra(Constants.TYPE, Constants.ROOM_DIGITAL_GOODS);
            intent.putExtra(Constants.POSTID, mPostModel.id);
            intent.putExtra(Constants.USERNAME, mPostModel.user.username);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_buy_item) {

            linear_buy_item.setEnabled(false);

            linear_buy_item.setBackground(context.getResources().getDrawable(R.drawable.btn_background_creating_invoice));
            txt_buyitem.setText(R.string.invoice_requesting);
            createInvoiceBuyItem();

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(context)) {

            if (!linear_buy_item.isEnabled()) linear_buy_item.setEnabled(true);

            if (objResult != null) {

                int invoiceID = objResult.optInt(Constants.ID);

                String message = context.getString(R.string.checkout) + String.valueOf(mPostModel.commerceModel.price) + " " + context.getString(R.string.pib) + "\n@" + mPostModel.user.username + context.getString(R.string.to_buy_this_item);

                new AlertView(message,null,  null, new String[]{context.getString(R.string.cancel), context.getString(R.string.ok)}, null,
                        context, AlertView.Style.Alert, new OnItemClickListener(){
                    public void onItemClick(Object o,int position){

                        if (position == 1) {

                            Intent intent = new Intent(context, RegisterPinActivity.class);
                            intent.putExtra(RegisterPinActivity.TYPE_ACTION, Constants.REQUEST_ACCEPT_INVOICE + "," + String.valueOf(invoiceID));
                            context.startActivity(intent);
                            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                    }

                }).show();
            }

            dismiss();
        } else {

            Constants.saveRefreshToken(context, objResult);

            createInvoiceBuyItem();

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert((AppCompatActivity)context, context.getString(R.string.oh_snap), context.getString(R.string.network_error_refreshtoken), context.getString(R.string.okay));

        }

        if (Constants.isLifeToken(context)) Utility.parseError((AppCompatActivity) context, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
