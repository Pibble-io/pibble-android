package com.star.pibbledev.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.star.pibbledev.services.global.model.UserModel;

public interface MainFeedListener {
    public void onClickSendingComment(String string, int postion);
    public void onClickViewAllComment(int position, boolean flag);
    public void onVoted(int position);
    public void onFavorite(boolean favorit, int position);
    public void onFollow(int position);
    public void createPromotion(int position);
    public void gotoPromotionDetail(int position);
    public void showFundingStatus(int position);
    public void showAskingHelp(int position);
    public void onRunedAnimationComment(int position);
    public void onShowActionBar(int position);
    public void onShowingUserInfo(int position);
    public void onShowingUserInfoWithUserinfo(UserModel userModel);
    public void onClickTag(String string, int position);
    public void onLongClickImageView(int position, int mediapos);
    public void onDoubleClickMediaView(RelativeLayout relativeLayout, LinearLayout.LayoutParams params, int position);
    public void onClickPlace(int position);
    public void OnClickDigitalGoods(int position, boolean isSeller);
    public void OnClickGoods(int position);
    public void showUpvotedUserPopup(String position);
    public void hideWalletPreview();
    public void onhideKeyboard();

}
