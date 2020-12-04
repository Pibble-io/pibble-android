package com.star.pibbledev.home.createmedia.tags;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.discover.activities.DiscoverItemDetailActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.TagModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.profile.activity.UsersActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.originqiu.library.EditTag;
import me.originqiu.library.MEditText;

public class MainFeedTagsActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String ACTIVITY_TYPE = "activity_type";

    public static final String UPDATE_TAGS = "media_update_tags";
    public static final String SEARCH_TAGS = "search_tags";
    public static String REQUEST_TYPE;

    MEditText txt_search;
    EditTag edittag;

    ListView list_tag;
    ImageButton img_back, img_check;

    TagAdaptar mTagAdaptar;
    ArrayList<TagModel> mTags = new ArrayList<>();

    String str_selected = "";
    String getActivityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_tags);

        setLightStatusBar();

        list_tag = (ListView)findViewById(R.id.list_tag);
        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_check = (ImageButton)findViewById(R.id.img_check);
        img_check.setOnClickListener(this);
        txt_search = (MEditText)findViewById(R.id.txt_search);
        edittag = (EditTag)findViewById(R.id.edittag);

        img_check.setAlpha(0.5f);
        img_check.setEnabled(false);

        getActivityType = getIntent().getStringExtra(ACTIVITY_TYPE);

        if (Utility.mainFeedParams != null && Utility.mainFeedParams.ary_tags.size() > 0) {

            for (int i = 0; i < Utility.mainFeedParams.ary_tags.size(); i++) {
                String tag = Utility.mainFeedParams.ary_tags.get(i);
                edittag.addTag("#" + tag);
            }

            img_check.setAlpha(1.0f);
            img_check.setEnabled(true);
        }

        mTagAdaptar = new TagAdaptar(this, mTags);
        list_tag.setAdapter(mTagAdaptar);
        list_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                edittag.addTag("#" + mTags.get(position).tag);
                txt_search.setText("");
            }
        });

        txt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                String cleardString = Utility.getStringWithoutFirstSpace(Utility.getWithoutLastSpace(txt_search.getText().toString()));

                String[]strings = getSplitStringArray(cleardString);

                for (String string : strings) {
                    edittag.addTag("#" + Utility.getStringWithoutSpecialCharacter(string));
                }

                return false;
            }
        });

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 0) {

                    img_check.setAlpha(1.0f);
                    img_check.setEnabled(true);

                    REQUEST_TYPE = SEARCH_TAGS;
                    ServerRequest.getSharedServerRequest().getTagSearchInfo(MainFeedTagsActivity.this, MainFeedTagsActivity.this, Utility.getStringWithoutFirstCharater(s.toString()));

                    if (txt_search.getText().toString().substring(txt_search.getText().toString().length() - 1).equals(" ")) {

                        String cleardString = Utility.getStringWithoutFirstSpace(Utility.getWithoutLastSpace(txt_search.getText().toString()));

                        String[]strings = getSplitStringArray(cleardString);

                        for (String string : strings) {

                            String str = Utility.getStringWithoutSpecialCharacter(string);
                            if (str.equals("") || str.equals(" ")) continue;

                            edittag.addTag("#" + str);
                        }

                    }
                }
            }
        });
    }
//
    public String[] getSplitStringArray(String string) {
        return string.split("\\s+");
    }

    @Override
    public void onClick(View v) {

        Utility.dismissKeyboard(this);

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == img_check){

            Utility.mainFeedParams.ary_tags.clear();

            for (int i = 0; i < edittag.getTagList().size(); i++) {
                String tag = edittag.getTagList().get(i);
                Utility.mainFeedParams.ary_tags.add(Utility.getStringWithoutFirstCharater(tag));
            }

            if (!txt_search.getText().toString().equals(" ") && txt_search.getText().toString().length() > 0) {
                Utility.mainFeedParams.ary_tags.add(Utility.getStringWithoutSpecialCharacter(Utility.getWithoutLastSpace(Utility.getStringWithoutFirstSpace(txt_search.getText().toString()))));
            }

            if (getActivityType != null) {

                REQUEST_TYPE = UPDATE_TAGS;

                String id = String.valueOf(getIntent().getIntExtra(Constants.ID, 0));
                String accessToken = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                ServerRequest.getSharedServerRequest().updateMediaTags(this, this, id, Utility.mainFeedParams, accessToken);

            } else {

                finish();
                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
            }
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (REQUEST_TYPE.equals(UPDATE_TAGS)) {

            Utility.g_isChanged = true;

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (REQUEST_TYPE.equals(SEARCH_TAGS)){

            JSONArray jsonArray = objResult.optJSONArray("items");

            mTags.clear();

            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        TagModel tagModel = new TagModel();

                        tagModel.tag = jsonObject.optString("tag");
                        tagModel.posted = jsonObject.optInt("posted");

                        mTags.add(tagModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            if (mTagAdaptar != null) mTagAdaptar.notifyDataSetChanged();
        }

    }

    @Override
    public void failed(String strError, int errorCode) {
//        Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
