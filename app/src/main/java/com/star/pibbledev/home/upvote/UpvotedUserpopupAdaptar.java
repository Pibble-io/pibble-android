package com.star.pibbledev.home.upvote;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.abdularis.civ.AvatarImageView;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.UpvotedUsersModel;

import java.util.ArrayList;


public class UpvotedUserpopupAdaptar extends RecyclerView.Adapter<UpvotedUserpopupAdaptar.ViewHolder> {

    private ArrayList<UpvotedUsersModel> mData;
    private LayoutInflater mInflater;
    private UsersUpvotedListener mClickListener;
    private Context mContext;
    private UpvoteListener upvoteListener;

    public UpvotedUserpopupAdaptar(Context context, ArrayList<UpvotedUsersModel> data, UpvoteListener upvoteListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.upvoteListener = upvoteListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_popup_user_upvoted, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UpvotedUsersModel upvotedUsersModel = mData.get(position);

        holder.txt_amount.setText(String.valueOf(upvotedUsersModel.amount));

        if (!upvotedUsersModel.userModel.avatar.equals("null")) {

            Glide.with(mContext).load(upvotedUsersModel.userModel.avatar).into(holder.img_avatar);
            holder.img_avatar.setState(AvatarImageView.SHOW_IMAGE);
            holder.txt_userEmo.setVisibility(View.INVISIBLE);

        } else {

            int value = 0;
            if (upvotedUsersModel.userModel.username.length() > 14) value = 14;
            else value = upvotedUsersModel.userModel.username.length();
            holder.txt_userEmo.setVisibility(View.VISIBLE);
            holder.img_avatar.setState(AvatarImageView.SHOW_INITIAL);
            holder.img_avatar.setAvatarBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            holder.txt_userEmo.setText(Utility.getUserEmoName(upvotedUsersModel.userModel.username));

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_amount, txt_userEmo;
        AvatarImageView img_avatar;

        ViewHolder(View itemView) {
            super(itemView);

            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_userEmo = itemView.findViewById(R.id.txt_userEmo);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            img_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upvoteListener.onClickUserUpvoted(getAdapterPosition());
                }
            });

            img_avatar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    upvoteListener.onLongClickUserUpvoted(getAdapterPosition());
                    return false;
                }
            });

//            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    UpvotedUsersModel getItem(int id) {
        return mData.get(id);
    }

    void setClickListener(UsersUpvotedListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface UsersUpvotedListener {
        void onClickUserUpvoted(View view, int position);
    }
}
