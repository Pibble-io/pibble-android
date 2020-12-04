package com.star.pibbledev.home.comments;

import com.star.pibbledev.services.global.model.CommentReplyModel;

public interface ReplyListener {
    public void onResult(CommentReplyModel replyModel, int commentPos);
    public void onUpvote(CommentReplyModel replyModel, int position);
    public void onClickReplyUserAdaptar(String username, int userid);
}
