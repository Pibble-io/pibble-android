package com.star.pibbledev.home.goods.utility;

import android.content.Context;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.ChatItemModel;

public class GoodsConfirmBottomSheetDialog extends BottomSheetDialog {

    private Context context;

    private static GoodsConfirmBottomSheetDialog instance;

    private TextView txt_title, txt_price, txt_type, txt_site, txt_description;
    private ImageView img_goods;
    private LinearLayout linear_return, linear_confirm, linear_site;

    private GoodsOrderStateListener listener;

    public static GoodsConfirmBottomSheetDialog getInstance(@NonNull Context context, GoodsOrderStateListener listener) {
        return instance == null ? new GoodsConfirmBottomSheetDialog(context, listener) : instance;
    }

    private GoodsConfirmBottomSheetDialog(@NonNull Context context, GoodsOrderStateListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        create();
    }

    public void create() {

        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_goods_confirm_bottomsheet, null);
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

        txt_title = (TextView)bottomSheetView.findViewById(R.id.txt_title);
        txt_price = (TextView)bottomSheetView.findViewById(R.id.txt_price);
        txt_type = (TextView)bottomSheetView.findViewById(R.id.txt_type);
        txt_site = (TextView)bottomSheetView.findViewById(R.id.txt_site);
        txt_description = (TextView)bottomSheetView.findViewById(R.id.txt_description);

        img_goods = (ImageView)bottomSheetView.findViewById(R.id.img_goods);

        linear_return = (LinearLayout)bottomSheetView.findViewById(R.id.linear_return);
        linear_confirm = (LinearLayout)bottomSheetView.findViewById(R.id.linear_confirm);
        linear_site = (LinearLayout)bottomSheetView.findViewById(R.id.linear_site);

    }

    public void setGoodsInfo(ChatItemModel itemModel, ImageLoader imageLoader) {

        txt_title.setText(itemModel.postMessage.goodsModel.title);

        String price = Utility.formatedNumberString(Double.parseDouble(itemModel.postMessage.goodsModel.price));
        txt_price.setText(context.getString(R.string.price) + " " + price + " " + context.getString(R.string.pib));

        if (itemModel.postMessage.goodsModel.is_new == 1) txt_type.setText(context.getString(R.string.new_lower));
        else txt_type.setText(context.getString(R.string.used_goods));

        txt_description.setText(itemModel.postMessage.goodsModel.description);

        imageLoader.displayImage(itemModel.postMessage.ary_media.get(0).thumbnail, img_goods);

        if (itemModel.postMessage.goodsModel.site != null && itemModel.postMessage.goodsModel.site.length() > 0) {

            txt_site.setText(itemModel.postMessage.goodsModel.site);

            linear_site.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Utility.showWebviewDialog(context, itemModel.postMessage.goodsModel.site);
                }
            });

        } else {

            linear_site.setVisibility(View.GONE);
        }

        linear_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickGoodsConfirm(itemModel.goods_order_id);
                dismiss();
            }
        });

        linear_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickGoodsReturn(itemModel.goods_order_id);
                dismiss();
            }
        });

    }

}
