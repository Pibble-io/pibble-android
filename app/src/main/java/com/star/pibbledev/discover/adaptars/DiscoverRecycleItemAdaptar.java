package com.star.pibbledev.discover.adaptars;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.DiscoverItemModel;

import java.util.ArrayList;

public class DiscoverRecycleItemAdaptar extends RecyclerView.Adapter<DiscoverRecycleItemAdaptar.ViewHolder> {

    private ArrayList<DiscoverItemModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private DiscoverRecycleItemListener mClickListener;
    private ImageLoader imageLoader;

    public DiscoverRecycleItemAdaptar(Context context, ArrayList<DiscoverItemModel> data, ImageLoader imageLoader) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.imageLoader = imageLoader;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_discover_recycler_cell, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DiscoverItemModel discoverItemModel = mData.get(position);

        if (discoverItemModel.isShowingCopy) {
            holder.linear_history_copy.setVisibility(View.VISIBLE);
        } else {
            holder.linear_history_copy.setVisibility(View.INVISIBLE);
        }

        if (discoverItemModel.itemType.equals(Constants.DISCOVER_USER)) {

            holder.img_type.setVisibility(View.INVISIBLE);

            holder.txt_title.setText(discoverItemModel.itemTitle);
            holder.txt_details.setText(discoverItemModel.itmeDetails) ;

            if (!discoverItemModel.itemAvatar.equals("null")) {

                this.imageLoader.displayImage(discoverItemModel.itemAvatar, holder.img_user);
                holder.txt_emo.setVisibility(View.INVISIBLE);

            } else {
                holder.txt_emo.setVisibility(View.VISIBLE);
                holder.img_user.setImageDrawable(null);
                holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[discoverItemModel.itemAvatarTemp]));
                holder.txt_emo.setText(Utility.getUserEmoName(discoverItemModel.itemTitle));
            }

        } else if (discoverItemModel.itemType.equals(Constants.DISCOVER_PLACE)) {

            holder.txt_title.setText(discoverItemModel.itemTitle);
            holder.txt_details.setText(discoverItemModel.itmeDetails + " " + mContext.getString(R.string.posted)) ;

            holder.txt_emo.setVisibility(View.VISIBLE);
            holder.img_user.setImageDrawable(null);
            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[discoverItemModel.itemAvatarTemp]));
            holder.txt_emo.setText(Utility.getUserEmoName(discoverItemModel.itemTitle));

            holder.img_type.setVisibility(View.VISIBLE);
            holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_discover_location));

        } else if (discoverItemModel.itemType.equals(Constants.DISCOVER_TAG)) {

            holder.txt_title.setText("#" + discoverItemModel.itemTitle);
            holder.txt_details.setText(discoverItemModel.itmeDetails + " " + mContext.getString(R.string.posted)) ;

            holder.txt_emo.setVisibility(View.VISIBLE);
            holder.img_user.setImageDrawable(null);
            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[discoverItemModel.itemAvatarTemp]));
            holder.txt_emo.setText(Utility.getUserEmoName(discoverItemModel.itemTitle));

            holder.img_type.setVisibility(View.VISIBLE);
            holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_discover_tag));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user, img_type;
        TextView txt_emo, txt_title, txt_details;
        LinearLayout linear_history_copy;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_details = itemView.findViewById(R.id.txt_details);
            img_user = itemView.findViewById(R.id.img_user);
            img_type = itemView.findViewById(R.id.img_type);
            linear_history_copy = itemView.findViewById(R.id.linear_history_copy);
            linear_history_copy.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view == linear_history_copy) {
                if (mClickListener != null) mClickListener.onClickCopyAction(getAdapterPosition());
            } else {
                if (mClickListener != null) mClickListener.onClickItem(getAdapterPosition());
            }

        }
    }

    DiscoverItemModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(DiscoverRecycleItemListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface DiscoverRecycleItemListener {
        void onClickItem(int position);
        void onClickCopyAction(int position);
    }
}
