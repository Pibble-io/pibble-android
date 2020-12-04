package com.star.pibbledev.home.report;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.ReportReasonModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportReasonSelectActivity extends BaseActivity implements View.OnClickListener, RequestListener,ReportReasonListAdaptar.listListener {

    public static final String TAG = "ReportReasonSelectActivity";

    private static final String GET_REASONS = "get_reasons";
    private static final String REQUEST_REPORT = "request_report";

    ImageButton img_back;
    RecyclerView recyclerview;
    int mPostid;
    String mReason;
    String requestType;

    ArrayList<ReportReasonModel> reasonModels = new ArrayList<>();
    ReportReasonListAdaptar adaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_reason_select);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        mPostid = getIntent().getIntExtra(TAG, -1);

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adaptar = new ReportReasonListAdaptar(this, reasonModels);
        recyclerview.setAdapter(adaptar);
        adaptar.setClickListener(this);

        getReasonsList();
    }

    private void getReasonsList() {

        requestType = GET_REASONS;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getReasonList(this, this, token);

        } else {

            Constants.requestRefreshToken(this, this);

        }
    }

    private void reportAsSpam(String reason) {

        mReason = reason;
        requestType = REQUEST_REPORT;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().blockFeedAsSpam(this, this, token, mPostid, reason);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void parsingReasonList(JSONObject jsonObject) {

        if (jsonObject == null) return;

        JSONArray jsonArray = jsonObject.optJSONArray("items");

        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {

                    JSONObject object = (JSONObject)jsonArray.get(i);

                    if (object == null) continue;

                    ReportReasonModel model = new ReportReasonModel();
                    model.type = object.optString("type");
                    model.label = object.optString("label");

                    reasonModels.add(model);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adaptar.notifyDataSetChanged();

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
    public void onClickCell(int position) {

        reportAsSpam(reasonModels.get(position).type);
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveRefreshToken(this, objResult);

            if (requestType.equals(GET_REASONS)) getReasonsList();
            else if (requestType.equals(REQUEST_REPORT)) reportAsSpam(mReason);

        } else {

            if (requestType.equals(GET_REASONS)) {

                parsingReasonList(objResult);

            } else if (requestType.equals(REQUEST_REPORT)) {

                Utility.g_isChangedType = Constants.REPORTED;
                Utility.g_isChanged = true;

                finish();
                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

            }
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (!Utility.g_isCalledRefreshToken) Utility.parseError(this, strError);

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}


