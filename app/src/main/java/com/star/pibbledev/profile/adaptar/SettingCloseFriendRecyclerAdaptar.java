package com.star.pibbledev.profile.adaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FriendsModel;

import java.util.ArrayList;

public class SettingCloseFriendRecyclerAdaptar extends RecyclerView.Adapter<SettingCloseFriendRecyclerAdaptar.ViewHolder> {

    private ArrayList<FriendsModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private listListener mClickListener;

    private final ViewBinderHelper binderHelper;

    public SettingCloseFriendRecyclerAdaptar(Context context, ArrayList<FriendsModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;

        binderHelper = new ViewBinderHelper();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_profile_setting_closefriend_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FriendsModel friendsModel = mData.get(position);

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        binderHelper.setOpenOnlyOne(true);
        holder.swipeLayout.close(false);

        holder.img_user.setImageBitmap(null);

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

        if (mData.size() == 0) return 0;
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
        TextView txt_emo, txt_name, txt_details, txt_action;
        LinearLayout linear_content, linear_action;

        SwipeRevealLayout swipeLayout;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_details = itemView.findViewById(R.id.txt_details);

            txt_action = itemView.findViewById(R.id.txt_action);
            txt_action.setText(R.string.close);

            img_user = itemView.findViewById(R.id.img_user);
            img_user.setOnClickListener(this);

            linear_content = (LinearLayout)itemView.findViewById(R.id.linear_content);
            linear_action = (LinearLayout)itemView.findViewById(R.id.linear_action);
            linear_action.setOnClickListener(this);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);

        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) {

                if (view == linear_action) {

                    mClickListener.onUserClose(getAdapterPosition());
                    if (swipeLayout.isOpened()) swipeLayout.close(true);

                } else if (view == img_user) {

                    mClickListener.onClickUser(getAdapterPosition());
                    if (swipeLayout.isOpened()) swipeLayout.close(true);

                }
            }
        }
    }

    FriendsModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(listListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface listListener {

        void onClickUser(int position);
        void onUserClose(int position);
    }
}
