package com.star.pibbledev.home.comments;

import com.star.pibbledev.services.global.model.CommentsModel;

public interface CommentListener {
    public void onResult(CommentsModel commentsModel, int position);
    public void onUpvote(CommentsModel commentsModel, int position);
    public void onClickUserAdaptar(int position);
    public void deleteComment(int commentposition);
    public void pickComment(int commentposition);
}
