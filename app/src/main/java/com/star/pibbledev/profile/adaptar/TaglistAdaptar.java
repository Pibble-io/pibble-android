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

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.model.TagModel;

import java.util.ArrayList;

public class TaglistAdaptar extends RecyclerView.Adapter<TaglistAdaptar.ViewHolder> {

    private ArrayList<TagModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private TaglistListener mClickListener;

    public TaglistAdaptar(Context context, ArrayList<TagModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_profile_user_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TagModel tagModel = mData.get(position);

        holder.img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_tag_avatar));
        holder.txt_name.setText("#" + tagModel.tag);
        holder.txt_details.setText(String.valueOf(tagModel.posted) + " " + mContext.getString(R.string.posted));

    }

    @Override
    public int getItemCount() {

        if (mData.size() == 0) return 0;
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) {

                if (view == btn_follow) {

                    boolean isfollowing = getItem(getAdapterPosition()).isfollowed;
                    if (isfollowing) {
                        mClickListener.onClickFollow(getAdapterPosition(), false);
                    } else {
                        mClickListener.onClickFollow(getAdapterPosition(), true);
                    }

                } else {
                    mClickListener.onClickTag(getAdapterPosition());
                }

            }
        }
    }

    TagModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(TaglistListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface TaglistListener {
        void onClickTag(int position);
        void onClickFollow(int position, boolean flag);
    }
}
