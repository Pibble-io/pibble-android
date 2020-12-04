package com.star.pibbledev.home.comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.CommentReplyModel;

@SuppressLint("ViewConstructor")
public class ReplyCommentLinearLayout extends LinearLayout  implements View.OnClickListener {

    ReplyListener listener;
    CommentReplyModel replyModel;

    TextView txt_body, txt_date, txt_username, txt_upvoteamount;
    ImageView img_user;
    Button btn_reply;
    ImageButton img_upvote;
    LinearLayout linear_adaptar;

    boolean isSelected;
    Context context;
    ImageLoader imageLoader;
    int mCommentPos;

    public ReplyCommentLinearLayout(Context context, CommentReplyModel replyModel, ReplyListener listener, ImageLoader imageLoader, int commentPos) {

        super(context);
        this.replyModel = replyModel;
        this.listener = listener;
        this.context = context;
        this.isSelected = replyModel.isSelected;
        this.imageLoader = imageLoader;
        this.mCommentPos = commentPos;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = minflater.inflate(R.layout.linear_item_list_home_comment, this, true);

        txt_body = (TextView)view.findViewById(R.id.txt_body);
        txt_date = (TextView)view.findViewById(R.id.txt_date);
        btn_reply = (Button)view.findViewById(R.id.btn_reply);
        btn_reply.setOnClickListener(this);
        txt_username = (TextView)view.findViewById(R.id.txt_username);
        img_user = (ImageView)view.findViewById(R.id.img_user);
        img_upvote = (ImageButton)view.findViewById(R.id.img_upvote);
        img_upvote.setOnClickListener(this);
        linear_adaptar = (LinearLayout)view.findViewById(R.id.linear_adaptar);
        linear_adaptar.setOnClickListener(this);
        txt_upvoteamount = (TextView)view.findViewById(R.id.txt_upvoteamount);

        if (!this.replyModel.user.avatar.equals("null")) {
            imageLoader.displayImage(this.replyModel.user.avatar, img_user, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
            txt_username.setVisibility(GONE);
        } else {
            img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[this.replyModel.user.avatar_temp]));
            txt_username.setText(Utility.getUserEmoName(this.replyModel.user.username));
            txt_username.setVisibility(VISIBLE);
        }

        txt_body.setText(String.format("%s: %s", replyModel.user.username, replyModel.body));
        txt_date.setText(Utility.getTimeAgo(replyModel.created_at));

        if (this.isSelected) {
            btn_reply.setTextColor(context.getResources().getColor(Utility.g_aryColors[this.replyModel.user.avatar_temp]));
        } else {
            btn_reply.setTextColor(context.getResources().getColor(R.color.light_gray));
        }

        if (this.replyModel.isEnabled) {
            btn_reply.setEnabled(true);
            img_upvote.setEnabled(true);
        } else {
            btn_reply.setEnabled(false);
            img_upvote.setEnabled(false);
        }

        if (replyModel.isUpvoted) {
            img_upvote.setImageResource(R.drawable.icon_comment_upvote_gray);
        } else {
            img_upvote.setImageResource(R.drawable.icon_comment_upvote_green);
        }

        if (replyModel.up_votes_amount > 0) {
            txt_upvoteamount.setText(String.format("%s %s", context.getString(R.string.brushed), String.valueOf(replyModel.up_votes_amount)));
            txt_upvoteamount.setVisibility(View.VISIBLE);
        } else {
            txt_upvoteamount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btn_reply) {
            if (isSelected) this.replyModel.isSelected = false;
            else this.replyModel.isSelected = true;
            this.listener.onResult(this.replyModel, mCommentPos);
        } else if (view == img_upvote) {
            this.listener.onUpvote(this.replyModel, mCommentPos);
        } else if (view == linear_adaptar) {
            this.listener.onClickReplyUserAdaptar(this.replyModel.user.username, this.replyModel.user.id);
        }

    }

}

