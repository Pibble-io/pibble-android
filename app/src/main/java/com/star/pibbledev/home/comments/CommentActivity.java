package com.star.pibbledev.home.comments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.ImpressionModel;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.home.upvote.AddUpvoteActivity;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentActivity extends BaseActivity implements View.OnClickListener, RequestListener, ReplyListener, CommentListener {

    public static final String TARGET_UPVOTE = "comment_upvote";

    public static final String POST_ID = "postid";
    public static final String PROMO_ID = "promo_id";
    public static final String POST_COMMENT_COUNT = "post_comment_count";
    public static final String POST_DESCRIPTION = "postDiscription";
    public static final String POST_USER_AVATAR = "postUseravatar";
    public static final String POST_USER_NAME = "postUsername";
    public static final String POST_USER_AVATAR_TEMP = "postUseravatarTemp";
    public static final String POST_CREATED = "postCreated";

    public static final String KEY_BOARD_STATUS = "keyboard_status";

    private String TYPE_COMMENT = "comment";
    private String TYPE_COMMENT_REPLY = "comment_reply";
    private String TYPE_GET_ALL = "getAll";
    private String TYPE_GET_COMMENT = "get_comment";

    ImageButton img_back, btn_sentComment;
    ListView listview;
    PullRefreshLayout pulltorefresh;
    EditText editTextComment;
    ImageView img_user, img_ownuser;
    TextView txt_emoName, txt_userEmo, txt_owncontent, txt_created;

    CommentListAdaptar commentListAdaptar;
    ArrayList<CommentsModel> commentsModels;
    int mPost_id, mPromo_id;

    String access_token, type_RequestAction;
    int buttonSelected, mPostion;
    boolean flag_loading;
    ImageLoader imageLoader;
    private int commentCNT;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_post_comment);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        btn_sentComment = (ImageButton)findViewById(R.id.btn_sentComment);
        btn_sentComment.setOnClickListener(this);
        img_user = (ImageView)findViewById(R.id.img_user);
        listview = (ListView)findViewById(R.id.listview);
        pulltorefresh = (PullRefreshLayout)findViewById(R.id.pulltorefresh);
        editTextComment = (EditText)findViewById(R.id.editTextComment);
        txt_emoName = (TextView)findViewById(R.id.txt_emoName);
        txt_userEmo = (TextView)findViewById(R.id.txt_userEmo);
        img_ownuser = (ImageView)findViewById(R.id.img_ownuser);
        txt_owncontent = (TextView)findViewById(R.id.txt_owncontent);
        txt_created = (TextView)findViewById(R.id.txt_created);

        access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

        mPost_id = getIntent().getIntExtra(POST_ID, 0);
        mPromo_id = getIntent().getIntExtra(PROMO_ID, -1);
        commentCNT = getIntent().getIntExtra(POST_COMMENT_COUNT, 0);

        flag_loading = false;

        commentsModels = new ArrayList<>();

        imageLoader = ImageLoader.getInstance();

        String avatar = Utility.getReadPref(this).getStringValue(Constants.AVATAR);
        String username = Utility.getReadPref(this).getStringValue(Constants.USERNAME);

        if (!avatar.equals("null")) {
            imageLoader.displayImage(avatar, img_user, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
            txt_emoName.setVisibility(View.GONE);
        } else {
            int value = 0;
            if (username.length() > 14) value = 14;
            else value = username.length();
            txt_emoName.setVisibility(View.VISIBLE);
            img_user.setBackgroundColor(getResources().getColor(Utility.g_aryColors[value]));
            txt_emoName.setText(Utility.getUserEmoName(username));

        }

        String str_description = getIntent().getStringExtra(POST_DESCRIPTION);
        String postUseravatar = getIntent().getStringExtra(POST_USER_AVATAR);
        String postUsername = getIntent().getStringExtra(POST_USER_NAME);
        int postUseravatarTemp = getIntent().getIntExtra(POST_USER_AVATAR_TEMP, 0);
        String postCreatedTime = Utility.getTimeAgo(getIntent().getStringExtra(POST_CREATED));

        boolean isKeyboard = getIntent().getBooleanExtra(KEY_BOARD_STATUS, false);
        if (isKeyboard) {
            editTextComment.requestFocus();
        }

        if (!postUseravatar.equals("null")) {
            imageLoader.displayImage(postUseravatar, img_ownuser, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
            txt_userEmo.setVisibility(View.GONE);
        } else {
            img_ownuser.setBackgroundColor(getResources().getColor(Utility.g_aryColors[postUseravatarTemp]));
            txt_userEmo.setVisibility(View.VISIBLE);
            txt_userEmo.setText(Utility.getUserEmoName(postUsername));
        }

        String content = Utility.getUpercaseString(postUsername) + " " + str_description;
        txt_owncontent.setText(content);
        txt_created.setText(postCreatedTime);

        ViewTreeObserver viewTreeObserver = txt_owncontent.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver viewTreeObserver1 = txt_owncontent.getViewTreeObserver();
                viewTreeObserver1.removeOnGlobalLayoutListener(this);

                if (txt_owncontent.getLineCount() > 2) {

                    int endOfLastLine = txt_owncontent.getLayout().getLineEnd(1);
                    String newString = txt_owncontent.getText().subSequence(0, endOfLastLine - 3) + "...";

                    Spannable spannable_super = new SpannableString(newString);
                    spannable_super.setSpan(new StyleSpan(Typeface.BOLD), 0, postUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txt_owncontent.setText(spannable_super, TextView.BufferType.SPANNABLE);

                } else {

                    Spannable spannable_super = new SpannableString(content);
                    spannable_super.setSpan(new StyleSpan(Typeface.BOLD), 0, postUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txt_owncontent.setText(spannable_super, TextView.BufferType.SPANNABLE);

                }
            }
        });

        commentListAdaptar = new CommentListAdaptar(this, commentsModels, this, this, imageLoader);
        listview.setAdapter(commentListAdaptar);

        listview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                if (i3 < i7) {

                    listview.postDelayed(new Runnable() {
                        @Override
                        public void run() {

//                            if (commentListAdaptar.getCount() > 1 && mPostion == 0) {
//                                listview.setSelection(commentListAdaptar.getCount() - 1);
//                            } else if (commentListAdaptar.getCount() > mPostion) {
                                listview.setSelection(mPostion);
//                            }

                        }
                    }, 100);

                }

            }
        });

        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btn_sentComment.setImageResource(R.drawable.btn_comment_selected);
                    btn_sentComment.setEnabled(true);
                } else {
                    btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
                    btn_sentComment.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pulltorefresh.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        pulltorefresh.setColor(Color.LTGRAY);

        pulltorefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getComments();

            }
        });

        buttonSelected = 0;

        getComments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utility.g_isChanged) getCommentInfo();
    }

    private void getComments() {

        type_RequestAction = TYPE_GET_ALL;

        if (Constants.isLifeToken(this)) {
            ServerRequest.getSharedServerRequest().getComments(this, this, access_token, String.valueOf(mPost_id), "1", String.valueOf(commentCNT), "asc");
        } else {
            Constants.requestRefreshToken(this, this);
        }
    }

    private void getCommentInfo() {

        type_RequestAction = TYPE_GET_COMMENT;

        int comment_id = commentsModels.get(mPostion).id;

        ServerRequest.getSharedServerRequest().getCommentFromId(this, this, access_token, String.valueOf(mPost_id), String.valueOf(comment_id));

    }

    private void sentComment() {

        type_RequestAction = TYPE_COMMENT;

        commentCNT++;

        if (Constants.isLifeToken(this)) {

            ServerRequest.getSharedServerRequest().createComment(this, this, mPost_id, editTextComment.getText().toString(), access_token);

        } else {
            Constants.requestRefreshToken(this, this);
        }

        addComment(editTextComment.getText().toString());

    }

    private void sendReply() {

        type_RequestAction = TYPE_COMMENT_REPLY;

        if (Constants.isLifeToken(this)) {
            ServerRequest.getSharedServerRequest().createCommentReply(this, this, mPost_id, buttonSelected, editTextComment.getText().toString(), access_token);
        } else {
            Constants.requestRefreshToken(this, this);
        }

        addReply(buttonSelected, editTextComment.getText().toString());
    }

    private void getAllComments(JSONObject objResult) {

        JSONArray ary_comments = objResult.optJSONArray("items");

        if (ary_comments != null && ary_comments.length() > 0) {

            try {

                for (int j = 0; j < ary_comments.length(); j++) {

                    JSONObject  comment = (JSONObject) ary_comments.get(j);

                    if (comment == null) continue;

                    CommentsModel commentsModel = new CommentsModel();

                    commentsModel.id = comment.optInt("id");
                    commentsModel.body = comment.optString("body");
                    commentsModel.created_at = comment.optString("created_at");
                    commentsModel.up_voted = comment.optBoolean("up_voted");
                    commentsModel.up_votes_amount = comment.optInt("up_votes_amount");

                    JSONObject comment_user = comment.optJSONObject("user");
                    commentsModel.userModel = new UserModel();
                    commentsModel.userModel.id = comment_user.optInt("id");

                    commentsModel.userModel.username = comment_user.optString("username");
                    commentsModel.userModel.avatar = comment_user.optString("avatar");
                    if (commentsModel.userModel.avatar.equals("null")) {
                        if (commentsModel.userModel.username.length() > 14) {
                            commentsModel.userModel.avatar_temp = 14;
                        } else {
                            commentsModel.userModel.avatar_temp = commentsModel.userModel.username.length();
                        }
                    }

                    commentsModel.isSelected = false;
                    commentsModel.isEnabled = true;

                    JSONArray comment_reply = comment.optJSONArray("replies");
                    commentsModel.ary_replies = new ArrayList<>();
                    if (comment_reply != null && comment_reply.length() > 0) {
                        for (int k = 0; k < comment_reply.length(); k++) {

                            JSONObject obj = (JSONObject) comment_reply.get(k);
                            CommentReplyModel model = new CommentReplyModel();
                            model.id = obj.optInt("id");
                            model.body = obj.optString("body");
                            model.created_at = obj.optString("created_at");
                            model.isUpvoted = obj.optBoolean("up_voted");
                            model.up_votes_amount = obj.optInt("up_votes_amount");

                            JSONObject obj_user = obj.optJSONObject("user");
                            model.user = new UserModel();
                            model.user.id = obj_user.optInt(Constants.ID);
                            model.user.avatar = obj_user.optString("avatar");
                            model.user.username = obj_user.optString("username");

                            if (model.user.avatar.equals("null")) {
                                if (model.user.username.length() > 14) {
                                    model.user.avatar_temp = 14;
                                } else {
                                    model.user.avatar_temp = model.user.username.length();
                                }
                            }
                            model.isSelected = false;
                            model.isEnabled = true;

                            commentsModel.ary_replies.add(model);
                        }
                    }

                    commentsModels.add(commentsModel);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (commentListAdaptar != null) {

                commentListAdaptar.notifyDataSetChanged();

                mPostion = commentListAdaptar.getCount() - 1;

                listview.post(new Runnable() {
                    @Override
                    public void run() {
                        listview.setSelection(commentListAdaptar.getCount() - 1);
                    }
                });
            }

            flag_loading = false;
        }
    }

    private void parsingCommentInfo(JSONObject jsonObject) {

        CommentsModel commentsModel = commentsModels.get(mPostion);

        commentsModel.id = jsonObject.optInt("id");
//        commentsModel.body = jsonObject.optString("body");
//        commentsModel.created_at = jsonObject.optString("created_at");
        commentsModel.up_voted = jsonObject.optBoolean("up_voted");
        commentsModel.up_votes_amount = jsonObject.optInt("up_votes_amount");

//        JSONObject comment_user = jsonObject.optJSONObject("user");
//        commentsModel.userModel = new UserModel();
//        commentsModel.userModel.id = comment_user.optInt("id");
//
//        commentsModel.userModel.username = comment_user.optString("username");
//        commentsModel.userModel.avatar = comment_user.optString("avatar");
//        if (commentsModel.userModel.avatar.equals("null")) {
//            if (commentsModel.userModel.username.length() > 15) {
//                commentsModel.userModel.avatar_temp = 14;
//            } else {
//                commentsModel.userModel.avatar_temp = commentsModel.userModel.username.length();
//            }
//        }
//
//        commentsModel.isSelected = false;
        commentsModel.isEnabled = true;
//
//        JSONArray comment_reply = jsonObject.optJSONArray("replies");
//        commentsModel.ary_replies = new ArrayList<>();
//        if (comment_reply != null && comment_reply.length() > 0) {
//            for (int k = 0; k < comment_reply.length(); k++) {
//
//                try {
//
//                    JSONObject obj = (JSONObject) comment_reply.get(k);
//
//                    CommentReplyModel model = new CommentReplyModel();
//                    model.id = obj.optInt("id");
//                    model.body = obj.optString("body");
//                    model.created_at = obj.optString("created_at");
//                    model.isUpvoted = obj.optBoolean("up_voted");
//                    model.up_votes_amount = obj.optInt("up_votes_amount");
//
//                    JSONObject obj_user = obj.optJSONObject("user");
//                    model.user = new UserModel();
//                    model.user.id = obj_user.optInt(Constants.ID);
//                    model.user.avatar = obj_user.optString("avatar");
//                    model.user.username = obj_user.optString("username");
//
//                    if (model.user.avatar.equals("null")) {
//                        if (model.user.username.length() > 15) {
//                            model.user.avatar_temp = 14;
//                        } else {
//                            model.user.avatar_temp = model.user.username.length();
//                        }
//                    }
//                    model.isSelected = false;
//                    model.isEnabled = true;
//
//                    commentsModel.ary_replies.add(model);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        commentsModels.set(mPostion, commentsModel);
        commentListAdaptar.notifyDataSetChanged();
    }

    private void addComment(String body) {

        CommentsModel commentsModel = new CommentsModel();

//        commentsModel.id = comment.optInt("id");
        commentsModel.body = body;
        commentsModel.created_at = Utility.getCurrentDate();
        commentsModel.up_voted = false;
//        commentsModel.up_votes_amount = comment.optInt("up_votes_amount");

        commentsModel.userModel = new UserModel();
        commentsModel.userModel.id = Utility.getReadPref(this).getIntValue(Constants.ID);

        commentsModel.userModel.username = Utility.getReadPref(this).getStringValue(Constants.USERNAME);
        commentsModel.userModel.avatar = Utility.getReadPref(this).getStringValue(Constants.AVATAR);
        if (commentsModel.userModel.avatar.equals("null")) {
            if (commentsModel.userModel.username.length() > 14) {
                commentsModel.userModel.avatar_temp = 14;
            } else {
                commentsModel.userModel.avatar_temp = commentsModel.userModel.username.length();
            }
        }

        commentsModel.isSelected = false;
        commentsModel.isEnabled = false;

        commentsModel.ary_replies = new ArrayList<>();

        commentsModels.add(commentsModel);

        if (commentListAdaptar != null) {

            commentListAdaptar.notifyDataSetChanged();

            mPostion = commentListAdaptar.getCount() - 1;

            listview.post(new Runnable() {
                @Override
                public void run() {
                    listview.setSelection(commentListAdaptar.getCount() - 1);
                }
            });
        }
    }

    private void addReply(int commentid, String body) {

        CommentReplyModel model = new CommentReplyModel();
//        model.id = obj.optInt("id");
        model.body = body;
        model.created_at = Utility.getCurrentDate();
        model.isUpvoted = false;

        model.user = new UserModel();
        model.user.id = Utility.getReadPref(this).getIntValue(Constants.ID);
        model.user.avatar = Utility.getReadPref(this).getStringValue(Constants.AVATAR);
        model.user.username = Utility.getReadPref(this).getStringValue(Constants.USERNAME);

        if (model.user.avatar.equals("null")) {
            if (model.user.username.length() > 14) {
                model.user.avatar_temp = 14;
            } else {
                model.user.avatar_temp = model.user.username.length();
            }
        }
        model.isSelected = false;
        model.isEnabled = false;

        int index = -1;

        for (int i = 0; i < commentsModels.size(); i++) {

            CommentsModel commentsModel = commentsModels.get(i);

            if (commentsModel.id == commentid) {
                index = i;
            }
        }

        if (index != -1) {
            CommentsModel commentsModel = commentsModels.get(index);
            commentsModel.ary_replies.add(model);

            commentsModels.set(index, commentsModel);
        }

        if (commentListAdaptar != null) {

            commentListAdaptar.notifyDataSetChanged();

//            listview.post(new Runnable() {
//                @Override
//                public void run() {
//                    listview.setSelection(commentListAdaptar.getCount() - 1);
//                }
//            });
        }
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            Utility.dismissKeyboard(this);
            Utility.g_isChanged = true;

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_sentComment) {

            btn_sentComment.setEnabled(false);

            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, mPost_id, mPromo_id,0, Constants.METRICS_COMMENT);
            Utility.saveImpressionToSql(this, impressionModel);

            if (buttonSelected == 0) {
                sentComment();
            } else {
                sendReply();
                buttonSelected = 0;
            }

//            Utility.dismissKeyboard(this);

            btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
            editTextComment.setText("");
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            if (type_RequestAction.equals(TYPE_GET_ALL)) {

                commentsModels.clear();

                JSONObject pagination = objResult.optJSONObject("pagination");

                if (pagination != null) {

                    int total = pagination.optInt("total");

                    if (total > commentCNT) {

                        getComments();

                    } else {

                        getAllComments(objResult);
                        pulltorefresh.setRefreshing(false);
                        type_RequestAction = "";
                    }

                }

            } else if (type_RequestAction.equals(TYPE_GET_COMMENT)) {

                if (objResult != null) parsingCommentInfo(objResult);

            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            if (type_RequestAction.equals(TYPE_GET_ALL)) {
                ServerRequest.getSharedServerRequest().getComments(this, this, access_token, String.valueOf(mPost_id), "1", String.valueOf(commentCNT), "asc");
            } else if (type_RequestAction.equals(TYPE_COMMENT)) {
                sentComment();
            } else if (type_RequestAction.equals(TYPE_COMMENT_REPLY)) {
                sendReply();
            }
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

        pulltorefresh.setRefreshing(false);

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;
            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    // reply listner

    @Override
    public void onResult(CommentReplyModel replyModel, int position) {

        mPostion = position;

        int commnetId = 0;

        for (int i = 0; i < commentsModels.size(); i++) {

            CommentsModel commentsModel_temp = commentsModels.get(i);
            commentsModel_temp.isSelected = false;

            for (int j = 0; j < commentsModel_temp.ary_replies.size(); j++) {

                CommentReplyModel reply_temp = commentsModel_temp.ary_replies.get(j);

                if (reply_temp.id == replyModel.id) {
                    commentsModel_temp.ary_replies.set(j, replyModel);
                    commentsModels.set(i, commentsModel_temp);
                    commnetId = i;
                } else {
                    reply_temp.isSelected = false;
                    commentsModel_temp.ary_replies.set(j, reply_temp);
                    commentsModels.set(i, commentsModel_temp);
                }
            }
        }

        if (commentListAdaptar != null) commentListAdaptar.notifyDataSetChanged();

        if (replyModel.isSelected) {

            editTextComment.setText(String.format("@%s", replyModel.user.username));

            int pos = editTextComment.length();
            Editable etext = editTextComment.getText();
            Selection.setSelection(etext, pos);

            editTextComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);

            buttonSelected = commentsModels.get(commnetId).id;

        } else {
            editTextComment.setText("");
            editTextComment.clearFocus();
            Utility.dismissKeyboard(this);

            buttonSelected = 0;
        }

    }

    @Override
    public void onUpvote(CommentReplyModel replyModel, int position) {

        mPostion = position;

        Intent intent = new Intent(this, AddUpvoteActivity.class);
        intent.putExtra("commentid", String.valueOf(replyModel.id));
        intent.putExtra(AddUpvoteActivity.POST_ID, String.valueOf(mPost_id));
        intent.putExtra(AddUpvoteActivity.PROMO_ID, mPromo_id);
        intent.putExtra(Constants.TARGET, TARGET_UPVOTE);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClickReplyUserAdaptar(String username, int userid) {

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, userid);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    // comment listner

    @Override
    public void onResult(CommentsModel commentsModel, int postion) {

        mPostion = postion;

        for (int i = 0; i < commentsModels.size(); i++) {
            CommentsModel comment_temp = commentsModels.get(i);
            for (int j = 0; j < comment_temp.ary_replies.size(); j++) {
                CommentReplyModel replyModel = comment_temp.ary_replies.get(j);
                replyModel.isSelected = false;
                comment_temp.ary_replies.set(j, replyModel);
            }
            if (commentsModel.id == comment_temp.id) {
                commentsModels.set(i, commentsModel);
            } else {
                comment_temp.isSelected = false;
                commentsModels.set(i, comment_temp);
            }
        }
        if (commentListAdaptar != null) commentListAdaptar.notifyDataSetChanged();

        if (commentsModel.isSelected) {

            editTextComment.setText(String.format("@%s", commentsModel.userModel.username));

            int pos = editTextComment.length();
            Editable etext = editTextComment.getText();
            Selection.setSelection(etext, pos);

            editTextComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);

            buttonSelected = commentsModel.id;

        } else {
            editTextComment.setText("");
            editTextComment.clearFocus();
            Utility.dismissKeyboard(this);

            buttonSelected = 0;
        }
    }

    @Override
    public void onUpvote(CommentsModel commentsModel, int position) {

        mPostion = position;

        Intent intent = new Intent(this, AddUpvoteActivity.class);
        intent.putExtra("commentid", String.valueOf(commentsModel.id));
        intent.putExtra(AddUpvoteActivity.POST_ID, String.valueOf(mPost_id));
        intent.putExtra(AddUpvoteActivity.PROMO_ID, mPromo_id);
        intent.putExtra(Constants.TARGET, TARGET_UPVOTE);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClickUserAdaptar(int position) {

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, commentsModels.get(position).userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, commentsModels.get(position).userModel.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void deleteComment(int commentposition) {

        new AlertView(getString(R.string.delete_comment), null, null, new String[]{getString(R.string.cancel), getString(R.string.delete)},
                null,
                this, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){

                if (position == 1) {

                    if (Constants.isLifeToken(CommentActivity.this)) {
                        int commentid = commentsModels.get(commentposition).id;
                        ServerRequest.getSharedServerRequest().deleteComment(CommentActivity.this, CommentActivity.this, mPost_id, commentid, access_token);
                        commentsModels.remove(commentposition);
                        if (commentListAdaptar != null) commentListAdaptar.notifyDataSetChanged();
                    } else {
                        Constants.requestRefreshToken(CommentActivity.this, CommentActivity.this);
                        type_RequestAction = "";
                    }
                }
            }

        }).show();

    }

    @Override
    public void pickComment(int commentposition) {

    }

}
