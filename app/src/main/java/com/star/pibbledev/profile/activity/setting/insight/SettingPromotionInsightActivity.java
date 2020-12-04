package com.star.pibbledev.profile.activity.setting.insight;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.barchart.BarChart;
import com.star.pibbledev.services.global.customview.barchart.BarChartModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SettingPromotionInsightActivity extends BaseActivity implements View.OnClickListener , RequestListener{

    ImageButton img_back;
    BarChart barChart_engage, barChart_impression;
    TextView txt_budget, txt_total_left, txt_total_used, txt_total_budget, txt_chart_used, txt_chart_left, txt_date, txt_engage, txt_impression;
    FrameLayout frame_used_chart;

    int mPromotionId;

    String engageTitle[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting_promotion_insight);

        setLightStatusBar();

        engageTitle = new String[]{getString(R.string.upvote), getString(R.string.comment), getString(R.string.collect), getString(R.string.tag_follow), getString(R.string.follow)};

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        txt_budget = (TextView)findViewById(R.id.txt_budget);
        txt_total_left = (TextView)findViewById(R.id.txt_total_left);
        txt_total_used = (TextView)findViewById(R.id.txt_total_used);
        txt_total_budget = (TextView)findViewById(R.id.txt_total_budget);
        txt_chart_used = (TextView)findViewById(R.id.txt_chart_used);
        txt_chart_left = (TextView)findViewById(R.id.txt_chart_left);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_impression = (TextView)findViewById(R.id.txt_impression);
        txt_engage = (TextView)findViewById(R.id.txt_engage);
        frame_used_chart = (FrameLayout)findViewById(R.id.frame_used_chart);

        mPromotionId = getIntent().getIntExtra(Constants.ID, -1);

        barChart_engage = (BarChart) findViewById(R.id.barChart_engage);
        barChart_impression = (BarChart) findViewById(R.id.barChart_impression);

        getInsightInfo();
    }

    private void initView(JSONObject jsonObject) {

        String start_date = jsonObject.optString("start_date");
        String end_date = jsonObject.optString("end_date");
        int budget = jsonObject.optInt("budget");
        int action_spent = jsonObject.optInt("actions_spent");
        int total_left = jsonObject.optInt("total_left");

        int spent = budget - total_left;

        txt_date.setText(String.format("%s - %s", Utility.getTypeFromDate(start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM d"), Utility.getTypeFromDate(end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM d")));
        txt_budget.setText(String.format("%s PIB", String.valueOf(budget)));
        txt_total_budget.setText(String.format("%s PIB", String.valueOf(budget)));
        txt_total_used.setText(String.format("%s PIB", String.valueOf(spent)));
        txt_total_left.setText(String.format("%s PIB", String.valueOf(total_left)));

        int usedPercent = spent * 100 / budget;
        int leftPercent = 100 - usedPercent;

        txt_chart_used.setText(String.format("%s %s%%", getString(R.string.used), String.valueOf(usedPercent)));
        txt_chart_left.setText(String.format("%s %s%%", getString(R.string.left), String.valueOf(leftPercent)));

        int usedLenth = (Utility.g_deviceWidth - Utility.dpToPx(40)) * usedPercent / 100;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(usedLenth, FrameLayout.LayoutParams.MATCH_PARENT);
        frame_used_chart.setLayoutParams(params);
        frame_used_chart.requestLayout();

        // engage chart

        JSONObject engageObj = jsonObject.optJSONObject("engage");

        if (engageObj != null) {

            ArrayList<Integer> ary_engageValue = new ArrayList<>();

//            int i = 0;
//
//            while (i < engageTitle.length) {
//
//                int value = engageObj.optInt(engageTitle[i].toLowerCase());
//
//                ary_engageValue.add(value);
//
//                i++;
//            }

            int upvoteVal = engageObj.optInt("up_vote");
            ary_engageValue.add(upvoteVal);

            int commentVal = engageObj.optInt("comment");
            ary_engageValue.add(commentVal);

            int collectVal = engageObj.optInt("collect");
            ary_engageValue.add(collectVal);

            int tagVal = engageObj.optInt("tag");
            ary_engageValue.add(tagVal);

            int followVal = engageObj.optInt("following");
            ary_engageValue.add(followVal);

            int maxValue = Collections.max(ary_engageValue);
            if (maxValue < 5) maxValue = 5;

            barChart_engage.setBarMaxValue(maxValue);

            txt_engage.setText(String.valueOf(Collections.max(ary_engageValue)));

            for (int k = 0; k < ary_engageValue.size(); k++) {

                int value = ary_engageValue.get(k);

                BarChartModel barChartModel = new BarChartModel();
                barChartModel.setBarValue(value);
                barChartModel.setBarTag(null);
                barChartModel.setBarTitle(engageTitle[k]);
                barChartModel.setBarText(String.valueOf(value));

                barChart_engage.addBar(barChartModel);

            }
        }

        // impression chart

        barChart_impression.setBarWidthValue((Utility.g_deviceWidth - Utility.dpToPx(80)) / 5);

        List<Date> dates = Utility.getDates(start_date, end_date);

        JSONArray ary_impressions = jsonObject.optJSONArray("impression");

        if (ary_impressions != null && ary_impressions.length() > 0) {

            ArrayList<Integer> ary_impressionValue = new ArrayList<>();
            ArrayList<String> ary_dates = new ArrayList<>();

            for (int i = 0; i < ary_impressions.length(); i++) {

                try {

                    JSONObject imprssionObj = (JSONObject)ary_impressions.get(ary_impressions.length() - 1 - i);

                    if (imprssionObj == null) continue;

                    String date = imprssionObj.optString("date");
                    int count = imprssionObj.optInt("count");

                    ary_dates.add(date);
                    ary_impressionValue.add(count);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            int maxValue = Collections.max(ary_impressionValue);
            if (maxValue < 5) maxValue = 5;

            barChart_impression.setBarMaxValue(maxValue);
            txt_impression.setText(String.valueOf(Collections.max(ary_impressionValue)));

            for (int k = 0; k < ary_impressionValue.size(); k++) {

                int count = ary_impressionValue.get(k);
                String date = ary_dates.get(k);

                BarChartModel barChartModel = new BarChartModel();
                barChartModel.setBarValue(count);
                barChartModel.setBarTag(null);
                barChartModel.setBarTitle(Utility.getTypeFromDate(date, "yyyy-MM-dd", "MMM d"));
                barChartModel.setBarText(String.valueOf(count));

                barChart_impression.addBar(barChartModel);
            }
        }
    }

    private void getInsightInfo() {

        if (mPromotionId == -1) return;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getPromotionInsight(this, this, token, mPromotionId);

        } else {

            Constants.requestRefreshToken(this, this);

        }

    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            getInsightInfo();

        } else {

            if (objResult == null) return;

            initView(objResult);

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
