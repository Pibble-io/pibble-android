package com.star.pibbledev.home.createmedia.location;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.LocationModel;
import com.star.pibbledev.home.editmedia.MediaCaptionEditActivity;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFeedLocationActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static final String ACTIVITY_TYPE = "activity_type";
    private static final int DELAY_TIME = 500;

    EditText txt_search;
    ListView list_location;
    ImageButton img_back, btn_cancel;
    LinearLayout linear_list;

    LocationAdaptar mloactionAdaptar;
    ArrayList<LocationModel> locationModels = new ArrayList<>();

    String getActivityType;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_location);

        setLightStatusBar();

        txt_search = (EditText)findViewById(R.id.txt_search);
        list_location = (ListView)findViewById(R.id.list_location);
        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        btn_cancel = (ImageButton)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        linear_list = (LinearLayout)findViewById(R.id.linear_list);
        linear_list.setVisibility(View.GONE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                searchLocation(txt_search.getText().toString());
            }
        };

        mloactionAdaptar = new LocationAdaptar(this, locationModels);
        list_location.setAdapter(mloactionAdaptar);
        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Utility.dismissKeyboard(MainFeedLocationActivity.this);

                if (getActivityType != null && getActivityType.equals(MediaCaptionEditActivity.TAG)) {

                    MediaCaptionEditActivity.mainFeedParams.locationModel = locationModels.get(position);

                } else {

                    Utility.mainFeedParams.locationModel = locationModels.get(position);

                }

                finish();
                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

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

                    handler.removeCallbacks(runnable);

                    handler.postDelayed(runnable, DELAY_TIME);

                }
            }
        });

        getActivityType = getIntent().getStringExtra(ACTIVITY_TYPE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void searchLocation(String s) {

        if (Constants.isLifeToken(MainFeedLocationActivity.this)) {

            ServerRequest.getSharedServerRequest().getLocationSearchInfo(MainFeedLocationActivity.this, MainFeedLocationActivity.this, s);

        } else {

            Constants.requestRefreshToken(MainFeedLocationActivity.this, MainFeedLocationActivity.this);

        }
    }

    @Override
    public void onClick(View v) {

        Utility.dismissKeyboard(this);

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == btn_cancel) {
            txt_search.clearFocus();
            txt_search.setText("");
            Utility.dismissKeyboard(this);
            locationModels.clear();
            if (mloactionAdaptar != null) mloactionAdaptar.notifyDataSetChanged();

        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        Constants.saveRefreshToken(this, objResult);

    }

    @Override
    public void failed(String strError, int errorCode) {
        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

        locationModels.clear();

        if (objResult != null && objResult.length() > 0) {

            for (int i = 0; i < objResult.length(); i++) {

                try {
                    JSONObject jsonObject = (JSONObject) objResult.get(i);
                    LocationModel locationModel = new LocationModel();
                    locationModel.description = jsonObject.optString("description");
                    locationModel.place_id = jsonObject.optString("place_id");
                    JSONObject place = jsonObject.optJSONObject("place");
                    if (place != null) {
                        locationModel.latitude = String.valueOf(place.optDouble("lat"));
                        locationModel.longitude = String.valueOf(place.optDouble("lng"));
                    }

                    locationModels.add(locationModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        if (locationModels.size() > 0) {
            linear_list.setVisibility(View.VISIBLE);
        } else {
            linear_list.setVisibility(View.GONE);
        }

        if (mloactionAdaptar != null) mloactionAdaptar.notifyDataSetChanged();

    }
}
