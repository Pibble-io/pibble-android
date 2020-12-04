package com.star.pibbledev.wallet.send;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FriendsModel;

import java.util.ArrayList;

public class FriendsListAdaptar extends RecyclerView.Adapter<FriendsListAdaptar.ViewHolder> {

    private ArrayList<FriendsModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private FriendsListListener mClickListener;

    public int mSelectedCell = -1;

    public FriendsListAdaptar(Context context, ArrayList<FriendsModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_wallet_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FriendsModel friendsModel = mData.get(position);

        holder.img_user.setImageBitmap(null);

        if (!friendsModel.user_avatar.equals("null")) {
            ImageLoader.getInstance().displayImage(friendsModel.user_avatar, holder.img_user);
            holder.txt_emo.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (friendsModel.user_name.length() > 14) value = 14;
            else value = friendsModel.user_name.length();

            holder.txt_emo.setVisibility(View.VISIBLE);
            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            holder.txt_emo.setText(Utility.getUserEmoName(friendsModel.user_name));
        }

        holder.txt_name.setText(friendsModel.user_name);

        if (mSelectedCell == position) {
            holder.img_check.setVisibility(View.VISIBLE);
        } else {
            holder.img_check.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user, img_check;
        TextView txt_emo, txt_name;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_user = itemView.findViewById(R.id.img_user);
            img_check = itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mSelectedCell = getAdapterPosition();
                mClickListener.onItemClick(view, mSelectedCell);
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {

        int size = mData.size();

        if (size > 0) {

            for (int i = 0; i < size; i++) {
                mData.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }

    }

    FriendsModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(FriendsListListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface FriendsListListener {
        void onItemClick(View view, int position);
    }
}
