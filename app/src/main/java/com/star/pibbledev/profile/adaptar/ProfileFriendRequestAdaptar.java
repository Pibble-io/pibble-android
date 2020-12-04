package com.star.pibbledev.profile.adaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FriendsModel;

import java.util.ArrayList;

public class ProfileFriendRequestAdaptar extends RecyclerView.Adapter<ProfileFriendRequestAdaptar.ViewHolder> {

    private ArrayList<FriendsModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private ProfileFriendRequestListener mClickListener;

    public ProfileFriendRequestAdaptar(Context context, ArrayList<FriendsModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_profile_friends_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FriendsModel friendsModel = mData.get(position);

        if (!friendsModel.user_avatar.equals("null")) {
            ImageLoader.getInstance().displayImage(friendsModel.user_avatar, holder.img_user);
            holder.txt_emo.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (friendsModel.user_name.length() > 14) value = 14;
            else value = friendsModel.user_name.length();

            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            holder.txt_emo.setText(Utility.getUserEmoName(friendsModel.user_name));
        }

        if (friendsModel.available_PRB == null || friendsModel.available_PRB.equals("null")) friendsModel.available_PRB = "0";
        if (friendsModel.available_PGB == null || friendsModel.available_PGB.equals("null")) friendsModel.available_PGB = "0";

        Float val_rb = Float.parseFloat(friendsModel.available_PRB);
        Float val_gb = Float.parseFloat(friendsModel.available_PGB);
        @SuppressLint("DefaultLocale") String str_level = String.format("Lv.%s R.B %d G.B %d", friendsModel.level, val_rb.intValue(), val_gb.intValue());

        holder.txt_details.setText(str_level);

        holder.txt_name.setText(friendsModel.user_name);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
        TextView txt_emo, txt_name, txt_details, txt_result;
        LinearLayout linear_action, linear_result, linear_deny, linear_accept;
        FrameLayout frame_user;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_details = itemView.findViewById(R.id.txt_details);
            txt_result = itemView.findViewById(R.id.txt_result);
            img_user = itemView.findViewById(R.id.img_user);

            linear_action = itemView.findViewById(R.id.linear_action);
            linear_result = itemView.findViewById(R.id.linear_result);
            linear_result.setVisibility(View.GONE);

            linear_accept = itemView.findViewById(R.id.linear_accept);
            linear_accept.setOnClickListener(this);
            linear_deny = itemView.findViewById(R.id.linear_deny);
            linear_deny.setOnClickListener(this);

            frame_user = itemView.findViewById(R.id.frame_user);
            frame_user.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view == linear_accept) {

                linear_action.setVisibility(View.GONE);
                linear_result.setVisibility(View.VISIBLE);
                linear_result.setBackground(mContext.getResources().getDrawable(R.drawable.linear_background_green));
                txt_result.setText(mContext.getString(R.string.accept));
                if (mClickListener != null) mClickListener.onClickRequestAccept(getAdapterPosition());

            } else if (view == linear_deny) {

                linear_action.setVisibility(View.GONE);
                linear_result.setVisibility(View.VISIBLE);
                linear_result.setBackground(mContext.getResources().getDrawable(R.drawable.linear_background_pink));
                txt_result.setText(R.string.denied);
                if (mClickListener != null) mClickListener.onClickRequestDeny(getAdapterPosition());

            } else if (view == frame_user) {
                if (mClickListener != null) mClickListener.onFriendRequestClick(getAdapterPosition());
            }
//            else if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    FriendsModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ProfileFriendRequestListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ProfileFriendRequestListener {
        void onFriendRequestClick(int position);
        void onClickRequestAccept(int position);
        void onClickRequestDeny(int position);
    }
}