package com.star.pibbledev.chatroom.adaptar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MessageRoomModel;

import java.util.ArrayList;

public class MessageRoomListAdaptar extends RecyclerView.Adapter<MessageRoomListAdaptar.ViewHolder> {

    private ArrayList<MessageRoomModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private MessageRoomListListener mClickListener;

    private final ViewBinderHelper binderHelper;

    public MessageRoomListAdaptar(Context context, ArrayList<MessageRoomModel> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;

        binderHelper = new ViewBinderHelper();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_messageroom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessageRoomModel roomModel = mData.get(position);

        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        binderHelper.setOpenOnlyOne(true);
        holder.swipeLayout.close(false);

        holder.txt_available.setVisibility(View.GONE);
        holder.img_user.setImageBitmap(null);

        if (roomModel.avatar == null || roomModel.avatar.equals("null")) {

            int colorNum = 0;
            if (roomModel.name.length() > 14) colorNum = 14;
            else colorNum = roomModel.name.length();

            holder.txt_emo.setVisibility(View.VISIBLE);
            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[colorNum]));
            holder.txt_emo.setText(Utility.getUserEmoName(roomModel.name));

        } else {

            Glide.with(this.mContext)
                    .load(roomModel.avatar)
                    .into(holder.img_user);
            holder.txt_emo.setVisibility(View.GONE);

        }

        if (roomModel.firstName.equals("null") || roomModel.lastName.equals("null") || roomModel.firstName.equals("") || roomModel.lastName.equals("")) {
            holder.txt_name.setText(roomModel.name);
        } else {
//            holder.txt_name.setText(String.format("%s %s", roomModel.lastName, roomModel.firstName));
            holder.txt_name.setText(roomModel.name);
        }

        if (roomModel.lastMessage != null) holder.txt_details.setText(roomModel.lastMessage);

        if (roomModel.lastMessageTime != null) holder.txt_date.setText(Utility.getTimeAgoWithoutYear(mContext, roomModel.lastMessageTime));

        if (roomModel.type.equals(Constants.ROOM_DIGITAL_GOODS)) {

            holder.swipeLayout.setLockDrag(true);

            holder.img_type.setVisibility(View.VISIBLE);

            if (roomModel.isDeleted) {

                holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_mark_digital_goods));
                holder.txt_available.setVisibility(View.GONE);

            } else {

                holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_mark_digital_goods));
                holder.txt_available.setVisibility(View.VISIBLE);

            }

        } else if (roomModel.type.equals(Constants.ROOM_GOODS)) {

            holder.swipeLayout.setLockDrag(true);

            holder.img_type.setVisibility(View.VISIBLE);

            if (roomModel.isDeleted) {

                holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_mark_goods));
                holder.txt_available.setVisibility(View.GONE);

            } else {

                holder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_mark_goods));
                holder.txt_available.setVisibility(View.VISIBLE);

            }

        } else {

            holder.swipeLayout.setLockDrag(false);
            holder.img_type.setVisibility(View.GONE);

        }

        if (roomModel.chatRoomCNT > 0) {
            holder.txt_members.setVisibility(View.VISIBLE);
            holder.txt_members.setText(String.valueOf(roomModel.chatRoomCNT));
        } else {
            holder.txt_members.setVisibility(View.GONE);
        }

        if (roomModel.unreadMessageCNT > 0) {

            holder.frame_badge.setVisibility(View.VISIBLE);
            holder.txt_badgeNum.setText(String.valueOf(roomModel.unreadMessageCNT));

        } else {
            holder.frame_badge.setVisibility(View.GONE);
        }

        if (roomModel.isMute) {
            holder.img_badge.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_message_badge_gray));
        } else {
            holder.img_badge.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_message_badge));
        }

    }

    @Override
    public int getItemCount() {

        if (mData.size() == 0) return 0;
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user, img_type, img_badge;
        TextView txt_emo, txt_name, txt_date, txt_details, txt_badgeNum, txt_available, txt_members;
        FrameLayout frame_badge;
        LinearLayout linear_content;

        SwipeRevealLayout swipeLayout;
        View muteView;
        View leaveView;

        ViewHolder(View itemView) {

            super(itemView);

            img_user = (ImageView)itemView.findViewById(R.id.img_user);
            img_type = (ImageView)itemView.findViewById(R.id.img_type);
            txt_emo = (TextView)itemView.findViewById(R.id.txt_emo);
            txt_name = (TextView)itemView.findViewById(R.id.txt_name);
            txt_date = (TextView)itemView.findViewById(R.id.txt_date);
            txt_details = (TextView)itemView.findViewById(R.id.txt_details);
            txt_badgeNum = (TextView)itemView.findViewById(R.id.txt_badgeNum);
            txt_available = (TextView)itemView.findViewById(R.id.txt_available);
            img_badge = (ImageView)itemView.findViewById(R.id.img_badge);
            txt_members = (TextView)itemView.findViewById(R.id.txt_members);

            frame_badge = (FrameLayout)itemView.findViewById(R.id.frame_badge);
            linear_content = (LinearLayout)itemView.findViewById(R.id.linear_content);
            linear_content.setOnClickListener(this);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            muteView = itemView.findViewById(R.id.mute_layout);
            leaveView = itemView.findViewById(R.id.leave_layout);

            muteView.setOnClickListener(this);
            leaveView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view == muteView) {

                if (swipeLayout.isOpened()) swipeLayout.close(true);

                if (mClickListener != null) mClickListener.onClickMute(getAdapterPosition());

            } else if (view == leaveView) {

                if (swipeLayout.isOpened()) swipeLayout.close(true);

                if (mClickListener != null) mClickListener.onClickLeave(getAdapterPosition());

            } else if (view == linear_content){

                if (mClickListener != null) {

                    if (mData.get(getAdapterPosition()).isSeller) {
                        mClickListener.onClickGroupInfo(getAdapterPosition());
                    } else {
                        mClickListener.onClickRoom(getAdapterPosition());
                    }

                }
            }
        }
    }

    public void clear() {
        final int size = mData.size();

        if (size > 0) {

            for (int i = 0; i < size; i++) {
                mData.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }

    }

    MessageRoomModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(MessageRoomListListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface MessageRoomListListener {

        void onClickRoom(int position);
        void onClickMute(int position);
        void onClickLeave(int position);
        void onClickGroupInfo(int position);

    }
}
