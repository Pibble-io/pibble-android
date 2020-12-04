package com.star.pibbledev.profile.adaptar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.UserModel;

public class ItemReferralUser extends LinearLayout implements View.OnClickListener {

    ImageView img_user;
    TextView txt_emo, txt_username;
    Context context;
    UserModel userModel;

    public ItemReferralUser(Context context) {
        super(context);

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_referral_user, this, true);

        this.context = context;

        img_user = (ImageView)view.findViewById(R.id.img_user);
        img_user.setOnClickListener(this);
        txt_emo = (TextView)view.findViewById(R.id.txt_emo);
        txt_username = (TextView)view.findViewById(R.id.txt_username);
    }

    public void setImage(UserModel userModel) {

        this.userModel = userModel;

        if (userModel == null) {

            txt_username.setVisibility(INVISIBLE);
            img_user.setImageDrawable(context.getDrawable(R.drawable.icon_referral_empty_avatar));

        } else {

            if (userModel.avatar == null || userModel.avatar.equals("null") || userModel.avatar.length() == 0) {

                txt_emo.setVisibility(VISIBLE);
                img_user.setImageDrawable(null);
                img_user.setBackgroundColor(context.getColor(Utility.g_aryColors[userModel.avatar_temp]));
                txt_emo.setText(Utility.getUserEmoName(userModel.username));

            } else {

                ImageLoader.getInstance().displayImage(userModel.avatar, img_user);

            }

            txt_username.setVisibility(VISIBLE);
            txt_username.setText(userModel.username);

        }
    }

    @Override
    public void onClick(View v) {
        if (v == img_user) {

            if (userModel != null) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.SELECT_USERNAME, userModel.username);
                intent.putExtra(UserProfileActivity.SELECT_USERID, userModel.id);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }
        }
    }
}
