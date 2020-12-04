package com.star.pibbledev.home.digitalgoods.utility;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MediaModel;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackBottemSheetDialog extends BottomSheetDialog implements View.OnClickListener, View.OnTouchListener, RequestListener {

    private Context context;
    private PostsModel mPostModel;
    private int rate;

    private static FeedbackBottemSheetDialog instance;

    private TextView txt_name, txt_commerce_price, txt_pagecount;
    private ImageView img_star1, img_star2, img_star3, img_star4, img_star5;
    private LinearLayout linear_imageinfo, linear_rateitem;
    private HorizontalScrollView scrollview;
    private EditText editTextComment;

    public static FeedbackBottemSheetDialog getInstance(@NonNull Context context) {
        return instance == null ? new FeedbackBottemSheetDialog(context) : instance;
    }

    private FeedbackBottemSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void create() {

        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_digitalgoods_feedback_bottomsheet, null);
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
        txt_pagecount = (TextView)bottomSheetView.findViewById(R.id.txt_pagecount);

        img_star1 = (ImageView)bottomSheetView.findViewById(R.id.img_star1);
        img_star1.setOnTouchListener(this);
        img_star2 = (ImageView)bottomSheetView.findViewById(R.id.img_star2);
        img_star2.setOnTouchListener(this);
        img_star3 = (ImageView)bottomSheetView.findViewById(R.id.img_star3);
        img_star3.setOnTouchListener(this);
        img_star4 = (ImageView)bottomSheetView.findViewById(R.id.img_star4);
        img_star4.setOnTouchListener(this);
        img_star5 = (ImageView)bottomSheetView.findViewById(R.id.img_star5);
        img_star5.setOnTouchListener(this);

        editTextComment = (EditText) bottomSheetView.findViewById(R.id.editTextComment);

        linear_rateitem = (LinearLayout)bottomSheetView.findViewById(R.id.linear_rateitem);
        linear_rateitem.setOnClickListener(this);

        linear_imageinfo = (LinearLayout)bottomSheetView.findViewById(R.id.linear_imageinfo);
        scrollview = (HorizontalScrollView)bottomSheetView.findViewById(R.id.scrollview);

        rate = 0;

    }

    public void setCommerceName(String name) {
        this.txt_name.setText(name);
    }

    @SuppressLint("SetTextI18n")
    public void setCommercePrice(int price) {
        this.txt_commerce_price.setText(String.valueOf(price));
    }

    public void setPostModel(PostsModel postsModel) {
        this.mPostModel = postsModel;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setImageInfo(ImageLoader imageLoader, ArrayList<MediaModel> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {

            MediaModel mediaModel = arrayList.get(i);

            String resolution = String.valueOf(mediaModel.width) + " x " + String.valueOf(mediaModel.height);
            String dpi = "300" + " (" + String.valueOf((int)(mediaModel.width * Constants.unitPxToinch)) + "cm" + " x " + String.valueOf((int)(mediaModel.height * Constants.unitPxToinch)) + "cm" + ")";

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
                    ObjectAnimator animator = ObjectAnimator.ofInt(scrollview, "scrollX",scrollTo );
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

    private void sendFeedback() {

        if (Constants.isLifeToken(context)) {

            String token = Utility.getReadPref(context).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().sendItemFeedback(this, context, token, mPostModel.id, editTextComment.getText().toString(), rate);

        } else {
            Constants.requestRefreshToken(context, this);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

            if (view == img_star1) {

                if (img_star1.getTag() != null && img_star1.getTag().equals("1")) {

                    img_star1.setTag("0");

                    rate = 0;

                    img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));

                } else {

                    rate = 1;

                    img_star1.setTag("1");

                    img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                    img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                    img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));

                }

            } else if (view == img_star2) {

                rate = 2;

                img_star1.setTag("0");

                img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));

            } else if (view == img_star3) {

                rate = 3;

                img_star1.setTag("0");

                img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));
                img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));

            } else if (view == img_star4) {

                rate = 4;

                img_star1.setTag("0");

                img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_black));

            } else if (view == img_star5) {

                rate = 5;

                img_star1.setTag("0");

                img_star1.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star2.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star3.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star4.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));
                img_star5.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_feedback_star_yellow));

            }

        }

        return false;
    }


    @Override
    public void onClick(View view) {

        if (view == linear_rateitem) {

            dismiss();

            sendFeedback();

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(context)) {
            Log.e("ssss", objResult.toString());
        } else {
            Constants.saveRefreshToken(context, objResult);
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
