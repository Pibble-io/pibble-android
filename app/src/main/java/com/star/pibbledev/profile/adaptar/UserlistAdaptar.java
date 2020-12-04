package com.star.pibbledev.profile.adaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FriendsModel;

import java.util.ArrayList;

public class UserlistAdaptar extends RecyclerView.Adapter<UserlistAdaptar.ViewHolder> {

    private ArrayList<FriendsModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private UserlistListener mClickListener;
    private boolean mIsfriend;

    public UserlistAdaptar(Context context, ArrayList<FriendsModel> data, boolean isFriend) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mIsfriend = isFriend;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_profile_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FriendsModel friendsModel = mData.get(position);

        holder.img_user.setImageBitmap(null);

        if (friendsModel.isTag) {

            holder.btn_follow.setVisibility(View.GONE);
            holder.img_director.setVisibility(View.VISIBLE);

            holder.img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_tag_avatar));
            holder.txt_emo.setVisibility(View.GONE);
            holder.txt_name.setText(friendsModel.tagTitle);
            holder.txt_details.setText(friendsModel.tagDetail);

        } else {

            holder.btn_follow.setVisibility(View.VISIBLE);
            holder.img_director.setVisibility(View.GONE);

            if (!friendsModel.user_avatar.equals("null")) {
                ImageLoader.getInstance().displayImage(friendsModel.user_avatar, holder.img_user);
                holder.txt_emo.setVisibility(View.GONE);
            } else {
                int value = 0;
                if (friendsModel.user_name.length() > 14) value = 14;
                else value = friendsModel.user_name.length();

                holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
                holder.txt_emo.setText(Utility.getUserEmoName(friendsModel.user_name));
                holder.txt_emo.setVisibility(View.VISIBLE);
            }

            if (friendsModel.available_PRB == null || friendsModel.available_PRB.equals("null")) friendsModel.available_PRB = "0";
            if (friendsModel.available_PGB == null || friendsModel.available_PGB.equals("null")) friendsModel.available_PGB = "0";

            Float val_rb = Float.parseFloat(friendsModel.available_PRB);
            Float val_gb = Float.parseFloat(friendsModel.available_PGB);

            @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%s R.B %d G.B %d", friendsModel.level, val_rb.intValue(), val_gb.intValue());

            holder.txt_details.setText(str_level);

            holder.txt_name.setText(friendsModel.user_name);

            if (mIsfriend || friendsModel.user_name.equals(Utility.getReadPref(mContext).getStringValue(Constants.USERNAME))) {

                holder.btn_follow.setVisibility(View.GONE);

            } else {

                holder.btn_follow.setVisibility(View.VISIBLE);
                if (friendsModel.is_following) {

                    holder.btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_gray));
                    holder.txt_btn_title.setText(mContext.getString(R.string.unfollow));
                    holder.txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.black));

                } else {
                    holder.btn_follow.setBackground(mContext.getResources().getDrawable(R.drawable.linear_corner5_blue));
                    holder.txt_btn_title.setText(mContext.getString(R.string.follow));
                    holder.txt_btn_title.setTextColor(mContext.getResources().getColor(R.color.colorMain));
                }
            }
        }

    }

    @Override
    public int getItemCount() {

        if (mData.size() == 0) return 0;
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user, img_director;
        TextView txt_emo, txt_name, txt_details, txt_btn_title;
        LinearLayout btn_follow;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_details = itemView.findViewById(R.id.txt_details);
            txt_btn_title = itemView.findViewById(R.id.txt_btn_title);
            img_user = itemView.findViewById(R.id.img_user);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_follow.setOnClickListener(this);
            img_director = itemView.findViewById(R.id.img_director);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {

                if (view == btn_follow) {

                    boolean isfollowing = getItem(getAdapterPosition()).is_following;
                    if (isfollowing) {
                        mClickListener.onClickFollow(getAdapterPosition(), false);
                    } else {
                        mClickListener.onClickFollow(getAdapterPosition(), true);
                    }

                } else {
                    mClickListener.onClickUser(getAdapterPosition());
                }

            }
        }
    }

    FriendsModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(UserlistListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface UserlistListener {
        void onClickUser(int position);
        void onClickFollow(int position, boolean flag);
    }
}
