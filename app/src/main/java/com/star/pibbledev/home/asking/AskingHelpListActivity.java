package com.star.pibbledev.home.asking;

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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.comments.CommentListAdaptar;
import com.star.pibbledev.home.comments.CommentListener;
import com.star.pibbledev.home.comments.ReplyListener;
import com.star.pibbledev.home.upvote.AddUpvoteActivity;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.AskingHelpModel;
import com.star.pibbledev.services.global.model.CommentReplyModel;
import com.star.pibbledev.services.global.model.CommentsModel;
import com.star.pibbledev.services.global.model.UserModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AskingHelpListActivity extends BaseActivity implements View.OnClickListener, RequestListener, CommentListener, ReplyListener {

    public static final String TARGET_UPVOTE = "answer_upvote";
    public static final String HELP_ID = "help_id";
    public static final String ANSWER_ID = "answer_id";

    private static final String REQUEST_GET_HELP_ANSWERS = "request_get_help_with_answers";
    private static final String REQUEST_GET_ANSWERS = "request_get_answers";
    private static final String REQUEST_GET_HELP = "request_get_help";
    private static final String REQUEST_CREATE_ANSWER = "request_create_answer";
    private static final String REQUEST_CREATE_ANSWER_REPLY = "request_create_answer_reply";
    private static final String REQUEST_PICK_ANSWER = "request_pick_answer";
    private static final String REQUEST_DELETE_ANSWER = "request_delete_answer";
    private static final String REQUEST_GET_ANSWER_FROMID = "request_get_answer_fromid";

    ImageButton img_cancel, btn_sentComment;
    ListView listview;
//    PullRefreshLayout pulltorefresh;
    EditText editTextComment;
    ImageView img_user, img_ownuser;
    TextView txt_emoName, txt_userEmo, txt_owncontent, txt_created;

    ImageLoader imageLoader;

    private int helpID, mCurrentPage, replySelected, mPostion;
    private String requestType;
    private boolean isLoadmore;
    private AskingHelpModel askingHelpModel;
    private ArrayList<CommentsModel> aryModels = new ArrayList<>();

    private AskingHelpListAdaptar askingHelpListAdaptar;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_help_list);

        setLightStatusBar();

        img_cancel = (ImageButton)findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(this);
        btn_sentComment = (ImageButton)findViewById(R.id.btn_sentComment);
        btn_sentComment.setOnClickListener(this);
        img_user = (ImageView)findViewById(R.id.img_user);
        listview = (ListView)findViewById(R.id.listview);
//        pulltorefresh = (PullRefreshLayout)findViewById(R.id.pulltorefresh);
        editTextComment = (EditText)findViewById(R.id.editTextComment);
        txt_emoName = (TextView)findViewById(R.id.txt_emoName);
        txt_userEmo = (TextView)findViewById(R.id.txt_userEmo);
        img_ownuser = (ImageView)findViewById(R.id.img_ownuser);
        txt_owncontent = (TextView)findViewById(R.id.txt_owncontent);
        txt_created = (TextView)findViewById(R.id.txt_created);

        imageLoader = ImageLoader.getInstance();

        helpID = getIntent().getIntExtra(Constants.ID, 0);
        mCurrentPage = 1;

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

        listview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                if (i3 < i7) {

                    listview.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            listview.setSelection(mPostion);

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

//        pulltorefresh.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
//        pulltorefresh.setColor(Color.LTGRAY);
//
//        pulltorefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//        });

        getHelpInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utility.g_isChanged) {

            getAnswerFromId(mPostion);

            Utility.g_isChanged = false;

        }
    }

    private void initView() {

        if (askingHelpModel != null) {

            boolean isCreator = false;

            if (askingHelpModel.userModel.username.equals(Utility.getReadPref(this).getStringValue(Constants.USERNAME))) isCreator = true;

            askingHelpListAdaptar = new AskingHelpListAdaptar(this, aryModels, this, this, imageLoader, isCreator, askingHelpModel.reward);
            listview.setAdapter(askingHelpListAdaptar);

            if (!askingHelpModel.userModel.avatar.equals("null")) {
                imageLoader.displayImage(askingHelpModel.userModel.avatar, img_ownuser, new ImageSize(Utility.dpToPx(30), Utility.dpToPx(30)));
                txt_userEmo.setVisibility(View.GONE);
            } else {
                img_ownuser.setBackgroundColor(getResources().getColor(Utility.g_aryColors[askingHelpModel.userModel.avatar_temp]));
                txt_userEmo.setVisibility(View.VISIBLE);
                txt_userEmo.setText(Utility.getUserEmoName(askingHelpModel.userModel.username));
            }

            String content = Utility.getUpercaseString(askingHelpModel.userModel.username) + " " + askingHelpModel.description;
            txt_owncontent.setText(content);
            txt_created.setText(Utility.getTimeAgo(askingHelpModel.created_at));

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
                        spannable_super.setSpan(new StyleSpan(Typeface.BOLD), 0, askingHelpModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txt_owncontent.setText(spannable_super, TextView.BufferType.SPANNABLE);

                    } else {

                        Spannable spannable_super = new SpannableString(content);
                        spannable_super.setSpan(new StyleSpan(Typeface.BOLD), 0, askingHelpModel.userModel.username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txt_owncontent.setText(spannable_super, TextView.BufferType.SPANNABLE);

                    }
                }
            });
        }
    }

    private void getHelpInfo() {

        requestType = REQUEST_GET_HELP;

        String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getHelp(this, this, accessToken, helpID);

    }

    private void getHelpWithAnswers() {

        requestType = REQUEST_GET_HELP_ANSWERS;

        String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getHelpWithAnswers(this, this, accessToken, helpID, 1, 10, "desc");

    }

    private void createAnswer() {

        requestType = REQUEST_CREATE_ANSWER;

        String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().createAnswer(this, this, accessToken, helpID, editTextComment.getText().toString());

        addAnswer(editTextComment.getText().toString());

    }

    private void createReply() {

        requestType = REQUEST_CREATE_ANSWER_REPLY;

        String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().createAnswerReply(this, this, accessToken, helpID, replySelected, editTextComment.getText().toString());

        addReply(replySelected, editTextComment.getText().toString());
    }

    private void getAnswers(int page) {

        requestType = REQUEST_GET_ANSWERS;

        String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getAnswers(this, this, accessToken, helpID, page, 15, "desc");

    }

    private void answerPick(int answer_id) {

        requestType = REQUEST_PICK_ANSWER;

        String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().pickAnswer(this, this, token, helpID, answer_id);

    }

    private void answerDelete(int answer_id) {

        requestType = REQUEST_DELETE_ANSWER;

        String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().deleteAnswer(this, this, token, helpID, answer_id);
    }

    private void getAnswerFromId(int position) {

        CommentsModel commentsModel = aryModels.get(position);

        requestType = REQUEST_GET_ANSWER_FROMID;

        String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getAnswerFromId(this, this, token, commentsModel.id);
    }

    private void addAnswer(String body) {

        CommentsModel commentsModel = new CommentsModel();

        commentsModel.body = body;
        commentsModel.created_at = Utility.getCurrentDate();
        commentsModel.up_voted = false;
        commentsModel.picked = false;
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

        aryModels.add(commentsModel);

        if (askingHelpListAdaptar != null) {

            askingHelpListAdaptar.notifyDataSetChanged();

            mPostion = askingHelpListAdaptar.getCount() - 1;

            listview.post(new Runnable() {
                @Override
                public void run() {
                    listview.setSelection(askingHelpListAdaptar.getCount() - 1);
                }
            });
        }
    }

    private void parsingAnswerInfo(JSONObject jsonObject) {

        CommentsModel commentsModel = aryModels.get(mPostion);

        commentsModel.id = jsonObject.optInt("id");
        commentsModel.up_voted = jsonObject.optBoolean("up_voted");
        commentsModel.up_votes_amount = jsonObject.optInt("up_votes_amount");

        commentsModel.isEnabled = true;

        aryModels.set(mPostion, commentsModel);
        askingHelpListAdaptar.notifyDataSetChanged();
    }

    private void addReply(int commentid, String body) {

        CommentReplyModel model = new CommentReplyModel();

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

        for (int i = 0; i < aryModels.size(); i++) {

            CommentsModel commentsModel = aryModels.get(i);

            if (commentsModel.id == commentid) {
                index = i;
            }
        }

        if (index != -1) {
            CommentsModel commentsModel = aryModels.get(index);
            commentsModel.ary_replies.add(model);

            aryModels.set(index, commentsModel);
        }

        if (askingHelpListAdaptar != null) {

            askingHelpListAdaptar.notifyDataSetChanged();
        }
    }

    private void parsingHelp(JSONObject jsonObject) {

        askingHelpModel = new AskingHelpModel();

        askingHelpModel.id = jsonObject.optInt(Constants.ID);
        askingHelpModel.description = jsonObject.optString(Constants.DESCRIPTION);
        askingHelpModel.closed = jsonObject.optBoolean(Constants.CLOSED);
        askingHelpModel.created_at = jsonObject.optString(Constants.CREATED_AT);
        askingHelpModel.reward = jsonObject.optInt(Constants.REWARD);

        JSONObject userObj = jsonObject.optJSONObject(Constants.USER);
        if (userObj != null) askingHelpModel.userModel = ParseUtility.userModel(userObj);

        initView();

        getAnswers(mCurrentPage);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0) {

                    if (isLoadmore) {

                        isLoadmore = false;

                        mCurrentPage++;

                        getAnswers(mCurrentPage);
                    }

                }
            }
        });
    }

    private void parsingAnswers(JSONObject jsonObject) {

        isLoadmore = false;

        JSONArray ary_comments = jsonObject.optJSONArray("items");

        if (ary_comments != null && ary_comments.length() > 0) {

            try {

                for (int j = 0; j < ary_comments.length(); j++) {

                    JSONObject  comment = (JSONObject) ary_comments.get(j);

                    if (comment == null) continue;

                    CommentsModel commentsModel = new CommentsModel();

                    commentsModel.id = comment.optInt("id");
                    commentsModel.body = comment.optString("body");
                    commentsModel.created_at = comment.optString("created_at");
                    commentsModel.picked = comment.optBoolean("picked");
                    commentsModel.up_voted = comment.optBoolean("up_voted");
                    commentsModel.up_votes_amount = comment.optInt("up_votes_amount");

                    JSONObject comment_user = comment.optJSONObject("user");
                    commentsModel.userModel = ParseUtility.userModel(comment_user);

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
                            model.user = ParseUtility.userModel(obj_user);

                            model.isSelected = false;
                            model.isEnabled = true;

                            commentsModel.ary_replies.add(model);
                        }
                    }

//                    aryModels.add(commentsModel);
                    aryModels.add(0, commentsModel);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (ary_comments.length() == 15) isLoadmore = true;

            if (askingHelpListAdaptar != null) {

                askingHelpListAdaptar.notifyDataSetChanged();

                mPostion = askingHelpListAdaptar.getCount() - 1;

                if (!isLoadmore) {

                    listview.post(new Runnable() {
                        @Override
                        public void run() {
                            listview.setSelection(askingHelpListAdaptar.getCount() - 1);
                        }
                    });

                }
            }

        }
    }

    private void parsingAnswer(JSONObject jsonObject) {

        try {

            CommentsModel commentsModel = new CommentsModel();

            commentsModel.id = jsonObject.optInt("id");
            commentsModel.body = jsonObject.optString("body");
            commentsModel.created_at = jsonObject.optString("created_at");
            commentsModel.picked = jsonObject.optBoolean("picked");
            commentsModel.up_voted = jsonObject.optBoolean("up_voted");
            commentsModel.up_votes_amount = jsonObject.optInt("up_votes_amount");

            JSONObject comment_user = jsonObject.optJSONObject("user");
            commentsModel.userModel = ParseUtility.userModel(comment_user);

            commentsModel.isSelected = false;
            commentsModel.isEnabled = true;

            JSONArray comment_reply = jsonObject.optJSONArray("replies");
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
                    model.user = ParseUtility.userModel(obj_user);

                    model.isSelected = false;
                    model.isEnabled = true;

                    commentsModel.ary_replies.add(model);
                }
            }

            aryModels.set(mPostion, commentsModel);

            askingHelpListAdaptar.notifyDataSetChanged();

            mPostion = aryModels.size() - 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btn_sentComment) {

            btn_sentComment.setEnabled(false);

//            ImpressionModel impressionModel = Utility.getImpressionFromPost(this, mPost_id, mPromo_id,0, Constants.METRICS_COMMENT);
//            Utility.saveImpressionToSql(this, impressionModel);

            if (replySelected == 0) {

                createAnswer();

            } else {

                createReply();
                replySelected = 0;

            }

            btn_sentComment.setImageResource(R.drawable.btn_comment_normal);
            editTextComment.setText("");

        } else if (view == img_cancel) {

            Utility.dismissKeyboard(this);
            Utility.g_isChanged = true;

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        switch (requestType) {

            case REQUEST_GET_HELP:

                if (objResult != null) parsingHelp(objResult);

                break;
            case REQUEST_GET_ANSWERS:

                if (objResult != null) parsingAnswers(objResult);

                break;
            case REQUEST_CREATE_ANSWER:

                if (objResult != null) parsingAnswerInfo(objResult);
                break;
            case REQUEST_PICK_ANSWER: {

                CommentsModel commentsModel = aryModels.get(mPostion);
                commentsModel.picked = true;
                aryModels.set(mPostion, commentsModel);

                askingHelpListAdaptar.notifyDataSetChanged();
            }

                break;
            case REQUEST_GET_ANSWER_FROMID:

                if (objResult != null) parsingAnswer(objResult);

                break;
        }
    }

    @Override
    public void failed(String strError, int errorCode) {

        Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    // comment listener

    @Override
    public void onResult(CommentsModel commentsModel, int position) {

        mPostion = position;

        for (int i = 0; i < aryModels.size(); i++) {
            CommentsModel comment_temp = aryModels.get(i);
            for (int j = 0; j < comment_temp.ary_replies.size(); j++) {
                CommentReplyModel replyModel = comment_temp.ary_replies.get(j);
                replyModel.isSelected = false;
                comment_temp.ary_replies.set(j, replyModel);
            }
            if (commentsModel.id == comment_temp.id) {
                aryModels.set(i, commentsModel);
            } else {
                comment_temp.isSelected = false;
                aryModels.set(i, comment_temp);
            }
        }
        if (askingHelpListAdaptar != null) askingHelpListAdaptar.notifyDataSetChanged();

        if (commentsModel.isSelected) {

            editTextComment.setText(String.format("@%s", commentsModel.userModel.username));

            int pos = editTextComment.length();
            Editable etext = editTextComment.getText();
            Selection.setSelection(etext, pos);

            editTextComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);

            replySelected = commentsModel.id;

        } else {
            editTextComment.setText("");
            editTextComment.clearFocus();
            Utility.dismissKeyboard(this);

            replySelected = 0;
        }
    }

    @Override
    public void onUpvote(CommentsModel commentsModel, int position) {

        mPostion = position;

        Intent intent = new Intent(this, AddUpvoteActivity.class);
        intent.putExtra(ANSWER_ID, commentsModel.id);
        intent.putExtra(HELP_ID, helpID);
//        intent.putExtra(AddUpvoteActivity.PROMO_ID, mPromo_id);
        intent.putExtra(Constants.TARGET, TARGET_UPVOTE);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClickUserAdaptar(int position) {

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.SELECT_USERNAME, aryModels.get(position).userModel.username);
        intent.putExtra(UserProfileActivity.SELECT_USERID, aryModels.get(position).userModel.id);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void deleteComment(int position) {

        CommentsModel model = aryModels.get(position);

        if (model.picked) {

        } else {

            answerDelete(model.id);

            aryModels.remove(position);
            askingHelpListAdaptar.notifyDataSetChanged();

        }

    }

    @Override
    public void pickComment(int position) {

        mPostion = position;

        CommentsModel model = aryModels.get(position);

        answerPick(model.id);

    }

    // reply listener

    @Override
    public void onResult(CommentReplyModel replyModel, int position) {

        mPostion = position;

        int commnetId = 0;

        for (int i = 0; i < aryModels.size(); i++) {

            CommentsModel commentsModel_temp = aryModels.get(i);
            commentsModel_temp.isSelected = false;

            for (int j = 0; j < commentsModel_temp.ary_replies.size(); j++) {

                CommentReplyModel reply_temp = commentsModel_temp.ary_replies.get(j);

                if (reply_temp.id == replyModel.id) {
                    commentsModel_temp.ary_replies.set(j, replyModel);
                    aryModels.set(i, commentsModel_temp);
                    commnetId = i;
                } else {
                    reply_temp.isSelected = false;
                    commentsModel_temp.ary_replies.set(j, reply_temp);
                    aryModels.set(i, commentsModel_temp);
                }
            }
        }

        if (askingHelpListAdaptar != null) askingHelpListAdaptar.notifyDataSetChanged();

        if (replyModel.isSelected) {

            editTextComment.setText(String.format("@%s", replyModel.user.username));

            int pos = editTextComment.length();
            Editable etext = editTextComment.getText();
            Selection.setSelection(etext, pos);

            editTextComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextComment, InputMethodManager.SHOW_IMPLICIT);

            replySelected = aryModels.get(commnetId).id;

        } else {
            editTextComment.setText("");
            editTextComment.clearFocus();
            Utility.dismissKeyboard(this);

            replySelected = 0;
        }
    }

    @Override
    public void onUpvote(CommentReplyModel replyModel, int position) {

        mPostion = position;

        Intent intent = new Intent(this, AddUpvoteActivity.class);
        intent.putExtra(ANSWER_ID, replyModel.id);
        intent.putExtra(HELP_ID, helpID);
//        intent.putExtra(AddUpvoteActivity.PROMO_ID, mPromo_id);
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
}
