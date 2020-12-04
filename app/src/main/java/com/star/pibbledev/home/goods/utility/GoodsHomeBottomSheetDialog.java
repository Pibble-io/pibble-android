package com.star.pibbledev.home.goods.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.chatroom.activity.ChatRoomActivity;
import com.star.pibbledev.home.HomepageFragment;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertHorizentalDialog;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

public class GoodsHomeBottomSheetDialog extends BottomSheetDialog {

    private Context context;

    private static GoodsHomeBottomSheetDialog instance;

    private TextView txt_title, txt_price, txt_type, txt_site, txt_description, txt_buyitem;
//    private ImageView img_goods;
    private LinearLayout linear_chatwithseller, linear_buy_item, linear_site;

    public static GoodsHomeBottomSheetDialog getInstance(@NonNull Context context) {
        return instance == null ? new GoodsHomeBottomSheetDialog(context) : instance;
    }

    private GoodsHomeBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    public void create() {

        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_goods_home_bottomsheet, null);
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
        txt_buyitem = (TextView)bottomSheetView.findViewById(R.id.txt_buyitem);

//        img_goods = (ImageView)bottomSheetView.findViewById(R.id.img_goods);

        linear_chatwithseller = (LinearLayout)bottomSheetView.findViewById(R.id.linear_chatwithseller);
        linear_buy_item = (LinearLayout)bottomSheetView.findViewById(R.id.linear_buy_item);
        linear_site = (LinearLayout)bottomSheetView.findViewById(R.id.linear_site);

    }

    public void setGoodsInfo(PostsModel postsModel, ImageLoader imageLoader) {

        txt_title.setText(postsModel.goodsModel.title);

        String price = Utility.formatedNumberString(Double.parseDouble(postsModel.goodsModel.price));
        txt_price.setText(String.format("%s %s %s", context.getString(R.string.price), price, context.getString(R.string.pib)));

        if (postsModel.goodsModel.is_new == 1) txt_type.setText(context.getString(R.string.new_lower));
        else txt_type.setText(context.getString(R.string.used_goods));

        txt_description.setText(postsModel.goodsModel.description);

        if (postsModel.goodsModel.status.equals(Constants.GOODS_FREE)) {

            linear_buy_item.setEnabled(true);
            linear_buy_item.setBackground(context.getDrawable(R.drawable.btn_background_buy_item));
            txt_buyitem.setText(context.getString(R.string.buy_item));

        } else if (postsModel.goodsModel.status.equals(Constants.GOODS_BOOKED)) {

            linear_buy_item.setEnabled(false);
            linear_buy_item.setBackground(context.getDrawable(R.drawable.btn_background_creating_invoice));
            txt_buyitem.setText(context.getString(R.string.booked_uppercase));

        } else if (postsModel.goodsModel.status.equals(Constants.GOODS_SOLD)) {

            linear_buy_item.setEnabled(false);
            linear_buy_item.setBackground(context.getDrawable(R.drawable.btn_background_creating_invoice));
            txt_buyitem.setText(context.getString(R.string.sold_out_uppercase));

        }

        if (postsModel.goodsModel.site != null && postsModel.goodsModel.site.length() > 0) {

            txt_site.setText(postsModel.goodsModel.site);

            linear_site.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Utility.showWebviewDialog(context, postsModel.goodsModel.site);
                }
            });

        } else {

            linear_site.setVisibility(View.GONE);
        }

        linear_buy_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = context.getString(R.string.checkout) + postsModel.goodsModel.price + " " + context.getString(R.string.pib) + " " + context.getString(R.string.to_buy_goods);

                AlertHorizentalDialog dialog = new AlertHorizentalDialog(context, null,
                        message,
                        context.getString(R.string.cancel), context.getString(R.string.ok), R.color.colorMain, R.color.colorMain) {

                    @Override
                    public void onClickButton(int position) {

                        if (position == 1) {

                            Intent intent = new Intent(context, RegisterPinActivity.class);
                            intent.putExtra(RegisterPinActivity.TYPE_ACTION, Constants.REQUEST_CREATE_GOODS_ORDER + "," + String.valueOf(postsModel.id) + "," + postsModel.goodsModel.price + "," + "pib");
                            context.startActivity(intent);
                            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }

                        dismiss();
                    }

                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                dismiss();
            }
        });

        linear_chatwithseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (postsModel.user.id != Utility.getReadPref(context).getIntValue(Constants.ID)) {

                    Intent intent = new Intent(context, ChatRoomActivity.class);
                    intent.putExtra(Constants.TARGET, HomepageFragment.TAG);
                    intent.putExtra(Constants.TYPE, Constants.ROOM_GOODS);
                    intent.putExtra(Constants.POSTID, postsModel.id);
                    intent.putExtra(Constants.USERNAME, postsModel.user.username);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                }

                dismiss();
            }
        });
    }
}
