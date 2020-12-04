package com.star.pibbledev.home.myfeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.star.pibbledev.R;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MyfeedModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("ViewConstructor")
public class MyfeedItemLinearLayout extends LinearLayout implements View.OnClickListener, RequestListener {

    Context mContext;

    ImageView img_user, img_user1, img_thumbnail;
    TextView txt_emo, txt_emo1, txt_message, txt_btn_title;
    LinearLayout linear_detail, btn_follow;
    FrameLayout frame_user;

    ImageLoader mImageLoader;
    MyfeedModel mMyfeedModel;

    public MyfeedItemLinearLayout(Context context, ImageLoader imageLoader) {
        super(context);

        mContext = context;
        mImageLoader = imageLoader;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = minflater.inflate(R.layout.item_list_myfeeds, this, true);

        img_user = (ImageView)view.findViewById(R.id.img_user);
        img_user1 = (ImageView)view.findViewById(R.id.img_user1);
        img_thumbnail = (ImageView)view.findViewById(R.id.img_thumbnail);
        img_thumbnail.setOnClickListener(this);
        txt_emo = (TextView)view.findViewById(R.id.txt_emo);
        txt_emo1 = (TextView)view.findViewById(R.id.txt_emo1);
        txt_btn_title = (TextView)view.findViewById(R.id.txt_btn_title);
        txt_message = (TextView)view.findViewById(R.id.txt_message);
        linear_detail = (LinearLayout)view.findViewById(R.id.linear_detail);
        linear_detail.setOnClickListener(this);
        btn_follow = (LinearLayout)view.findViewById(R.id.btn_follow);
        btn_follow.setOnClickListener(this);
        frame_user = (FrameLayout)view.findViewById(R.id.frame_user);
        frame_user.setOnClickListener(this);

        btn_follow.setVisibility(GONE);
        frame_user.setVisibility(GONE);
        img_thumbnail.setVisibility(GONE);
    }

    public void setData(MyfeedModel myfeedModel) {

        mMyfeedModel = myfeedModel;

        txt_message.setText(myfeedModel.message);

        switch (myfeedModel.entity_type) {

            case Constants.MYFEED_ENTITY_POST:

                if (myfeedModel.entity_post != null && myfeedModel.entity_post.ary_media.get(0).thumbnail.length() > 0) {

                    img_thumbnail.setVisibility(VISIBLE);
                    mImageLoader.displayImage(myfeedModel.entity_post.ary_media.get(0).thumbnail, img_thumbnail);

                }

                break;

            case Constants.MYFEED_ENTITY_USER:

                if (myfeedModel.entity_user != null) {

                    frame_user.setVisibility(VISIBLE);

                    if (!myfeedModel.entity_user.avatar.equals("null")) {
                        txt_emo1.setVisibility(View.INVISIBLE);
                        mImageLoader.displayImage(myfeedModel.entity_user.avatar, img_user1, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
                    } else {
                        txt_emo1.setVisibility(View.VISIBLE);
                        img_user1.setImageDrawable(null);
                        img_user1.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[myfeedModel.entity_user.avatar_temp]));
                        txt_emo1.setText(Utility.getUserEmoName(myfeedModel.entity_user.username));
                    }
                }

                break;
            default:

                if (myfeedModel.type.equals(Constants.ADD_FOLLOWER)) {

                    btn_follow.setVisibility(VISIBLE);

                    if (myfeedModel.user_from.interaction_status != null && myfeedModel.user_from.interaction_status.is_following) {

                        btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                        txt_btn_title.setText(mContext.getString(R.string.following));
                        txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.black));

                    } else {

                        btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_promote_button_1));
                        txt_btn_title.setText(mContext.getString(R.string.follow));
                        txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.white));

                    }
                }

                break;
        }

        if (myfeedModel.user_from != null) {

            if (!myfeedModel.user_from.avatar.equals("null")) {
                txt_emo.setVisibility(View.INVISIBLE);
                mImageLoader.displayImage(myfeedModel.user_from.avatar, img_user, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
            } else {
                txt_emo.setVisibility(View.VISIBLE);
                img_user.setImageDrawable(null);
                img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[myfeedModel.user_from.avatar_temp]));
                txt_emo.setText(Utility.getUserEmoName(myfeedModel.user_from.username));
            }
        }

    }

    @Override
    public void onClick(View view) {

        if (view == img_thumbnail) {

            Intent intent = new Intent(mContext, UsersActivity.class);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_DISPLAY_ONE_POST);
            intent.putExtra(Constants.POSTID, mMyfeedModel.entity_post.id);
            mContext.startActivity(intent);
            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == linear_detail) {

            if (mMyfeedModel.user_from == null) return;

            Intent intent = new Intent(mContext, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.SELECT_USERNAME, mMyfeedModel.user_from.username);
            intent.putExtra(UserProfileActivity.SELECT_USERID, mMyfeedModel.user_from.id);
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (view == btn_follow) {

            if (mMyfeedModel.user_from.interaction_status != null) {

                String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                if (mMyfeedModel.user_from.interaction_status.is_following) {

                    mMyfeedModel.user_from.interaction_status.is_following = false;

                    btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_promote_button_1));
                    txt_btn_title.setText(mContext.getString(R.string.follow));
                    txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.white));

                    ServerRequest.getSharedServerRequest().selectUnFollow(this, mContext, mMyfeedModel.user_from.username, access_token);

                } else {

                    mMyfeedModel.user_from.interaction_status.is_following = true;

                    btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                    txt_btn_title.setText(mContext.getString(R.string.following));
                    txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.black));

                    ServerRequest.getSharedServerRequest().selectFollow(this, mContext, mMyfeedModel.user_from.username, access_token);

                }
            }
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
