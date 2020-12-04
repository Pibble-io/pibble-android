package com.star.pibbledev.home.funding.adaptar;

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
import com.star.pibbledev.services.global.model.TeamModel;

import java.util.ArrayList;

public class TeamListAdaptar extends RecyclerView.Adapter<TeamListAdaptar.ViewHolder> {

    private ArrayList<TeamModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private TeamListListener mClickListener;

    private int mSelectedCell = -1;

    public TeamListAdaptar(Context context, ArrayList<TeamModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TeamModel teamModel = mData.get(position);

        holder.img_logo.setImageBitmap(null);

        if (!teamModel.logo.equals("null")) {
            ImageLoader.getInstance().displayImage(teamModel.logo, holder.img_logo);
            holder.txt_emo.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (teamModel.name.length() > 14) value = 14;
            else value = teamModel.name.length();

            holder.txt_emo.setVisibility(View.VISIBLE);
            holder.img_logo.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            holder.txt_emo.setText(Utility.getUserEmoName(teamModel.name));
        }

        holder.txt_title.setText(teamModel.title);
        holder.txt_teamname.setText((teamModel.name));
        holder.txt_members.setText(String.format("%s %s", mContext.getString(R.string.members), String.valueOf(teamModel.members_count)));
        holder.txt_created.setText(String.format("%s %s", mContext.getString(R.string.created_at), Utility.getTypeFromDate(teamModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MM-dd-yyyy")));
        holder.txt_raise.setText(String.format("%s %s", mContext.getString(R.string.raised), String.valueOf(teamModel.raised)));
        holder.txt_goal.setText(String.format("%s %s", mContext.getString(R.string.goal), String.valueOf(teamModel.goal)));

        if (mSelectedCell == teamModel.id) {
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

        ImageView img_logo, img_check;
        TextView txt_emo, txt_title, txt_teamname, txt_members, txt_created, txt_raise, txt_goal;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_teamname = itemView.findViewById(R.id.txt_teamname);
            txt_members = itemView.findViewById(R.id.txt_members);
            txt_created = itemView.findViewById(R.id.txt_created);
            txt_raise = itemView.findViewById(R.id.txt_raise);
            txt_goal = itemView.findViewById(R.id.txt_goal);
            img_logo = itemView.findViewById(R.id.img_logo);
            img_check = itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mSelectedCell = getItem(getAdapterPosition()).id;
                mClickListener.onItemClick(view, getAdapterPosition());
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

    private TeamModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(TeamListListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface TeamListListener {
        void onItemClick(View view, int position);
    }
}