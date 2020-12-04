package com.star.pibbledev.home.digitalgoods.utility;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MediaModel;
import com.star.pibbledev.services.global.model.PostsModel;

import java.util.ArrayList;

public class LinearLayoutDigitalgoodsInfo extends LinearLayout {

    private TextView txt_pagecount;
    private LinearLayout linear_commerce_content, linear_imageinfo;
    private HorizontalScrollView scrollview;

    private ImageLoader mImageLoader;
    private Context mContext;

    public LinearLayoutDigitalgoodsInfo(Context context, PostsModel mPostModel, ImageLoader imageLoader) {
        super(context);

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = minflater.inflate(R.layout.item_digitalgoods_commerce_info, this, true);

        mImageLoader = imageLoader;
        mContext = context;

        TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
        TextView txt_commerce_price = (TextView) view.findViewById(R.id.txt_commerce_price);
        TextView txt_reward = (TextView) view.findViewById(R.id.txt_reward);
        txt_pagecount = (TextView)view.findViewById(R.id.txt_pagecount);
        linear_imageinfo = (LinearLayout)view.findViewById(R.id.linear_imageinfo);
        scrollview = (HorizontalScrollView)view.findViewById(R.id.scrollview);

        txt_name.setText(mPostModel.commerceModel.name);
        txt_commerce_price.setText(String.valueOf(mPostModel.commerceModel.price));
        txt_reward.setText(String.valueOf(mPostModel.commerceModel.price * 0.15));

        setImageInfo(mPostModel.ary_media);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImageInfo(ArrayList<MediaModel> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {

            MediaModel mediaModel = arrayList.get(i);

            String resolution = String.valueOf(mediaModel.width) + " x " + String.valueOf(mediaModel.height);
            String dpi = "300" + " (" + String.valueOf((int)(mediaModel.width * Constants.unitPxToinch)) + "cm" + " x " + String.valueOf((int)(mediaModel.height * Constants.unitPxToinch)) + "cm" + ")";

            LinearLayoutScrollViewItem item = new LinearLayoutScrollViewItem(mContext, mImageLoader, mediaModel.thumbnail, resolution, dpi, "JPEG");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utility.g_deviceWidth - Utility.dpToPx(112), Utility.dpToPx(100));
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

                    return true;
                }

                return false;
            }
        });

    }

}
