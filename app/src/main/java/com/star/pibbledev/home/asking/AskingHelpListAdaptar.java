package com.star.pibbledev.home.asking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.star.pibbledev.R;
import com.star.pibbledev.home.comments.CommentListener;
import com.star.pibbledev.home.comments.ReplyCommentLinearLayout;
import com.star.pibbledev.home.comments.ReplyListener;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;

import java.util.ArrayList;

public class AskingHelpListAdaptar extends BaseAdapter implements View.OnClickListener{

    private Context context;
    private ArrayList<CommentsModel> pData;
    private ReplyListener listener;
    private CommentListener commentListener;
    private static LayoutInflater inflater=null;

    private ImageLoader imageLoader;
    private boolean isCreator;
    private int reward;

    private final ViewBinderHelper binderHelper;

    AskingHelpListAdaptar(Context context, ArrayList<CommentsModel> pData, ReplyListener listener, CommentListener commentListener, ImageLoader imageLoader, boolean isCreator, int reward) {

        this.context = context;
        this.pData = pData;
        this.listener = listener;
        this.commentListener = commentListener;
        this.imageLoader = imageLoader;
        this.isCreator = isCreator;
        this.reward = reward;

        binderHelper = new ViewBinderHelper();

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if (pData.size() == 0)
            return 0;
        else
            return pData.size();
    }

    @Override
    public Object getItem(int position) {
        return pData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View view) {

    }

    public class ViewHolder {

        ImageView img_user;
        TextView txt_userEmo, txt_comment, txt_date, txt_upvoteamount, txt_reward;
        Button btn_reply;
        LinearLayout linear_replies, linear_adaptar, linear_action, linear_reward;
        ImageButton img_upvote;
        SwipeRevealLayout swipeLayout;

        View pickView;
        View deleteView;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

//        if (convertView == null) {
        viewHolder = new ViewHolder();

        convertView = inflater.inflate(R.layout.item_list_asking_help, null);

        viewHolder.img_user = (ImageView)convertView.findViewById(R.id.img_user);
        viewHolder.txt_userEmo = (TextView)convertView.findViewById(R.id.txt_userEmo);
        viewHolder.txt_comment = (TextView)convertView.findViewById(R.id.txt_comment);
        viewHolder.txt_date = (TextView)convertView.findViewById(R.id.txt_date);
        viewHolder.txt_upvoteamount = (TextView)convertView.findViewById(R.id.txt_upvoteamount);
        viewHolder.btn_reply = (Button)convertView.findViewById(R.id.btn_reply);
        viewHolder.linear_replies = (LinearLayout)convertView.findViewById(R.id.linear_replies);
        viewHolder.img_upvote = (ImageButton)convertView.findViewById(R.id.img_upvote);
        viewHolder.linear_adaptar = (LinearLayout)convertView.findViewById(R.id.linear_adaptar);
        viewHolder.linear_action = (LinearLayout)convertView.findViewById(R.id.linear_action);
        viewHolder.linear_reward = (LinearLayout)convertView.findViewById(R.id.linear_reward);
        viewHolder.txt_reward = (TextView)convertView.findViewById(R.id.txt_reward);

        viewHolder.swipeLayout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe_layout);

        viewHolder.pickView = convertView.findViewById(R.id.pick_layout);
        viewHolder.deleteView = convertView.findViewById(R.id.delete_layout);

//            convertView.setTag(viewHolder);

//        } else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }

        CommentsModel commentsModel = pData.get(position);

        binderHelper.bind(viewHolder.swipeLayout, String.valueOf(commentsModel.id));
        binderHelper.setOpenOnlyOne(true);

        String username = Utility.getReadPref(context).getStringValue(Constants.USERNAME);

        if (commentsModel.userModel.username.equals(username)) {

            viewHolder.swipeLayout.setLockDrag(false);

            viewHolder.pickView.setVisibility(View.GONE);
            viewHolder.deleteView.setVisibility(View.VISIBLE);

        } else {

            if (isCreator) {

                viewHolder.deleteView.setVisibility(View.GONE);
                viewHolder.pickView.setVisibility(View.VISIBLE);

                viewHolder.swipeLayout.setLockDrag(false);

            } else {

                viewHolder.swipeLayout.setLockDrag(true);

            }

        }

        viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.swipeLayout.isOpened()) {
                    viewHolder.swipeLayout.close(true);
                }

                commentListener.deleteComment(position);
            }
        });
        viewHolder.pickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.swipeLayout.isOpened()) {
                    viewHolder.swipeLayout.close(true);
                }

                commentListener.pickComment(position);
            }
        });

        if (!commentsModel.userModel.avatar.equals("null")) {
            viewHolder.txt_userEmo.setVisibility(View.INVISIBLE);
            this.imageLoader.displayImage(commentsModel.userModel.avatar, viewHolder.img_user, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
        } else {
            viewHolder.txt_userEmo.setVisibility(View.VISIBLE);
            viewHolder.img_user.setImageDrawable(null);
            viewHolder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[commentsModel.userModel.avatar_temp]));
            viewHolder.txt_userEmo.setText(Utility.getUserEmoName(commentsModel.userModel.username));
        }

        if (commentsModel.up_voted) {
            viewHolder.img_upvote.setImageResource(R.drawable.icon_comment_upvote_gray);
        } else {
            viewHolder.img_upvote.setImageResource(R.drawable.icon_comment_upvote_green);
        }

        if (commentsModel.up_votes_amount > 0) {
            viewHolder.txt_upvoteamount.setText(String.format("%s %s", context.getString(R.string.brushed), String.valueOf(commentsModel.up_votes_amount)));
            viewHolder.txt_upvoteamount.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txt_upvoteamount.setVisibility(View.GONE);
        }

        if (commentsModel.isSelected) {
            viewHolder.btn_reply.setTextColor(context.getResources().getColor(Utility.g_aryColors[commentsModel.userModel.avatar_temp]));
        } else {
            viewHolder.btn_reply.setTextColor(context.getResources().getColor(R.color.light_gray));
        }

        if (commentsModel.isEnabled) {
            viewHolder.btn_reply.setEnabled(true);
            viewHolder.img_upvote.setEnabled(true);
        } else {
            viewHolder.btn_reply.setEnabled(false);
            viewHolder.img_upvote.setEnabled(false);

            viewHolder.swipeLayout.setLockDrag(true);
        }

        String s1 = commentsModel.userModel.username.substring(0, 1).toUpperCase();
        String nameCapitalized = s1 + commentsModel.userModel.username.substring(1);

        viewHolder.txt_comment.setText(String.format("%s: %s", nameCapitalized, commentsModel.body));
        viewHolder.txt_date.setText(Utility.getTimeAgo(commentsModel.created_at));

        if (commentsModel.picked) viewHolder.linear_reward.setVisibility(View.VISIBLE);
        else viewHolder.linear_reward.setVisibility(View.GONE);

        viewHolder.txt_reward.setText(String.valueOf(reward));

        if (commentsModel.ary_replies.size() > 0) {
            for (int i = 0; i < commentsModel.ary_replies.size(); i++) {
                CommentReplyModel replyModel = commentsModel.ary_replies.get(i);
                ReplyCommentLinearLayout layout = new ReplyCommentLinearLayout(this.context, replyModel, this.listener, this.imageLoader, position);
                viewHolder.linear_replies.addView(layout);
            }
        }

        viewHolder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentsModel.isSelected) commentsModel.isSelected = false;
                else commentsModel.isSelected = true;
                commentListener.onResult(commentsModel, position);
            }
        });

        viewHolder.img_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentListener.onUpvote(commentsModel, position);
            }
        });

        viewHolder.linear_adaptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentListener.onClickUserAdaptar(position);
            }
        });

        return convertView;
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     *
     */

    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }
}
