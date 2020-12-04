package com.star.pibbledev.home.topgroup;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.UserModel;

import java.util.ArrayList;

public class LeaderBoardAdaptar extends RecyclerView.Adapter {

    private static final int VIEW_TOP = 1;
    private static final int VIEW_NORMAL = 2;

    private Context mContext;
    private ArrayList<UserModel> mData;

    private LeaderBoardListener leaderBoardListener;

    public LeaderBoardAdaptar(Context context, ArrayList<UserModel> arrayList) {

        mContext = context;
        mData = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TOP) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_topview, parent, false);
            return new topViewHolder(view);

        } else if (viewType == VIEW_NORMAL) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_normalview, parent, false);
            return new normalViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_TOP) {

            ((topViewHolder)holder).bind();

        } else if (holder.getItemViewType() == VIEW_NORMAL) {

            UserModel userModel = mData.get(position + 2);

            ((normalViewHolder)holder).bind(userModel, position + 2);

        }
    }

    @Override
    public int getItemCount() {

        if (mData.size() == 0) return 0;
        else if (mData.size() < 4) return 1;
        else return mData.size() - 2;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) return VIEW_TOP;
        else return VIEW_NORMAL;
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

    private class topViewHolder extends RecyclerView.ViewHolder {

        TextView txt_first_name, txt_second_name, txt_third_name, txt_second_emo, txt_third_emo, txt_first_emo, txt_first_value, txt_second_value, txt_third_value;
        RelativeLayout releative_second, releative_third;
        ImageView img_second_user, img_first_user, img_third_user;
        LinearLayout linear_won_first, linear_won_second, linear_won_third;

        topViewHolder(View itemView) {
            super(itemView);

            txt_first_name = (TextView)itemView.findViewById(R.id.txt_first_name);
            txt_second_name = (TextView)itemView.findViewById(R.id.txt_second_name);
            txt_third_name = (TextView)itemView.findViewById(R.id.txt_third_name);
            txt_second_emo = (TextView)itemView.findViewById(R.id.txt_second_emo);
            txt_third_emo = (TextView)itemView.findViewById(R.id.txt_third_emo);
            txt_first_emo = (TextView)itemView.findViewById(R.id.txt_first_emo);
            txt_first_value = (TextView)itemView.findViewById(R.id.txt_first_value);
            txt_second_value = (TextView)itemView.findViewById(R.id.txt_second_value);
            txt_third_value = (TextView)itemView.findViewById(R.id.txt_third_value);

            img_first_user = (ImageView)itemView.findViewById(R.id.img_first_user);
            img_second_user = (ImageView)itemView.findViewById(R.id.img_second_user);
            img_third_user = (ImageView)itemView.findViewById(R.id.img_third_user);

            linear_won_first = (LinearLayout)itemView.findViewById(R.id.linear_won_first);
            linear_won_second = (LinearLayout)itemView.findViewById(R.id.linear_won_second);
            linear_won_third = (LinearLayout)itemView.findViewById(R.id.linear_won_third);

            releative_second = (RelativeLayout)itemView.findViewById(R.id.releative_second);
            releative_third = (RelativeLayout)itemView.findViewById(R.id.releative_third);
        }

        void bind() {

            if (mData.size() == 1) {

                releative_second.setVisibility(View.INVISIBLE);
                releative_third.setVisibility(View.INVISIBLE);

                UserModel user1 = mData.get(0);

                txt_first_name.setText(user1.username);

                txt_first_value.setText(String.valueOf(Utility.formatedNumberString(user1.prizeAmount)));

                if (!user1.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user1.avatar, img_first_user);
                    txt_first_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_first_emo.setVisibility(View.VISIBLE);
                    img_first_user.setImageDrawable(null);
                    img_first_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user1.avatar_temp]));
                    txt_first_emo.setText(Utility.getUserEmoName(user1.username));
                }

            } else if (mData.size() == 2) {

                releative_second.setVisibility(View.VISIBLE);
                releative_third.setVisibility(View.INVISIBLE);

                UserModel user1 = mData.get(0);

                txt_first_name.setText(user1.username);
                txt_first_value.setText(String.valueOf(Utility.formatedNumberString(user1.prizeAmount)));

                if (!user1.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user1.avatar, img_first_user);
                    txt_first_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_first_emo.setVisibility(View.VISIBLE);
                    img_first_user.setImageDrawable(null);
                    img_first_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user1.avatar_temp]));
                    txt_first_emo.setText(Utility.getUserEmoName(user1.username));
                }


                UserModel user2 = mData.get(1);

                txt_second_name.setText(user2.username);
                txt_second_value.setText(String.valueOf(Utility.formatedNumberString(user2.prizeAmount)));

                if (!user2.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user2.avatar, img_second_user);
                    txt_second_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_second_emo.setVisibility(View.VISIBLE);
                    img_second_user.setImageDrawable(null);
                    img_second_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user2.avatar_temp]));
                    txt_second_emo.setText(Utility.getUserEmoName(user2.username));
                }

            } else {

                releative_second.setVisibility(View.VISIBLE);
                releative_third.setVisibility(View.VISIBLE);

                UserModel user1 = mData.get(0);

                txt_first_name.setText(user1.username);
                txt_first_value.setText(String.valueOf(Utility.formatedNumberString(user1.prizeAmount)));

                if (!user1.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user1.avatar, img_first_user);
                    txt_first_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_first_emo.setVisibility(View.VISIBLE);
                    img_first_user.setImageDrawable(null);
                    img_first_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user1.avatar_temp]));
                    txt_first_emo.setText(Utility.getUserEmoName(user1.username));
                }


                UserModel user2 = mData.get(1);

                txt_second_name.setText(user2.username);
                txt_second_value.setText(String.valueOf(Utility.formatedNumberString(user2.prizeAmount)));

                if (!user2.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user2.avatar, img_second_user);
                    txt_second_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_second_emo.setVisibility(View.VISIBLE);
                    img_second_user.setImageDrawable(null);
                    img_second_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user2.avatar_temp]));
                    txt_second_emo.setText(Utility.getUserEmoName(user2.username));
                }

                UserModel user3 = mData.get(2);

                txt_third_name.setText(user3.username);
                txt_third_value.setText(String.valueOf(Utility.formatedNumberString(user3.prizeAmount)));

                if (!user3.avatar.equals("null")) {

                    ImageLoader.getInstance().displayImage(user3.avatar, img_third_user);
                    txt_third_emo.setVisibility(View.INVISIBLE);

                } else {
                    txt_third_emo.setVisibility(View.VISIBLE);
                    img_third_user.setImageDrawable(null);
                    img_third_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[user3.avatar_temp]));
                    txt_third_emo.setText(Utility.getUserEmoName(user3.username));
                }
            }

            img_first_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemClick(0);
                }
            });

            img_second_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemClick(1);
                }
            });

            img_third_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemClick(2);
                }
            });

            linear_won_first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemWonClick(0);
                }
            });

            linear_won_second.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemWonClick(1);
                }
            });

            linear_won_third.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemWonClick(2);
                }
            });
        }
    }

    private class normalViewHolder extends RecyclerView.ViewHolder {

        TextView txt_username, txt_index, txt_value, txt_emo;
        ImageView img_user;
        LinearLayout linear_won;

        normalViewHolder(View itemView) {
            super(itemView);

            txt_username = (TextView)itemView.findViewById(R.id.txt_username);
            txt_index = (TextView)itemView.findViewById(R.id.txt_index);
            txt_value = (TextView)itemView.findViewById(R.id.txt_value);
            txt_emo = (TextView)itemView.findViewById(R.id.txt_emo);
            linear_won = (LinearLayout)itemView.findViewById(R.id.linear_won);

            img_user = (ImageView)itemView.findViewById(R.id.img_user);
        }

        void bind(UserModel userModel, int position) {

            txt_index.setText(String.valueOf(position + 1));
            txt_username.setText(userModel.username);
            txt_value.setText(String.valueOf(Utility.formatedNumberString(userModel.prizeAmount)));

            if (!userModel.avatar.equals("null")) {

                ImageLoader.getInstance().displayImage(userModel.avatar, img_user);
                txt_emo.setVisibility(View.INVISIBLE);

            } else {
                txt_emo.setVisibility(View.VISIBLE);
                img_user.setImageDrawable(null);
                img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[userModel.avatar_temp]));
                txt_emo.setText(Utility.getUserEmoName(userModel.username));
            }

            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemClick(position);
                }
            });

            linear_won.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (leaderBoardListener != null) leaderBoardListener.onItemWonClick(position);
                }
            });
        }

    }

    public void setClickListener(LeaderBoardListener itemClickListener) {
        this.leaderBoardListener = itemClickListener;
    }

    public interface LeaderBoardListener {
        void onItemClick(int position);
        void onItemWonClick(int position);
    }
}
