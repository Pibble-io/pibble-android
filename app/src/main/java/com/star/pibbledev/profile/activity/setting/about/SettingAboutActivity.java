package com.star.pibbledev.profile.activity.setting.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.activity.WebviewActivity;
import com.star.pibbledev.services.global.Constants;

import java.util.ArrayList;

public class SettingAboutActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "SettingAboutActivity";

    ImageButton img_back;
    LinearLayout linear_terms, linear_policy, linear_guide;
    TextView txt_appversion;

    ArrayList<LinearLayout> ary_linears = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_about);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        txt_appversion = (TextView)findViewById(R.id.txt_appversion);

        linear_terms = (LinearLayout)findViewById(R.id.linear_terms);
        linear_terms.setOnClickListener(this);
        ary_linears.add(linear_terms);
        linear_policy = (LinearLayout)findViewById(R.id.linear_policy);
        linear_policy.setOnClickListener(this);
        ary_linears.add(linear_policy);
        linear_guide = (LinearLayout)findViewById(R.id.linear_guide);
        linear_guide.setOnClickListener(this);
        ary_linears.add(linear_guide);

        txt_appversion.setText(Constants.APP_VERSION);

    }

    private void changeColorLinearLayout(int position) {

        if (ary_linears != null && ary_linears.size() > position) {

            for (int i = 0; i < ary_linears.size(); i++) {

                if (i == position) {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.line_background_color));
                } else {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.white));
                }

            }

        }

    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_terms) {

            changeColorLinearLayout(0);

//            Utility.openBrowser(this, Constants.terms_url);
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra(WebviewActivity.TAG, TAG);
            intent.putExtra(Constants.SITE_URL, Constants.terms_url);
            intent.putExtra(Constants.TITLE, getString(R.string.terms_of_use));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_policy) {

            changeColorLinearLayout(1);

//            Utility.openBrowser(this, Constants.policy_url);
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra(WebviewActivity.TAG, TAG);
            intent.putExtra(Constants.SITE_URL, Constants.policy_url);
            intent.putExtra(Constants.TITLE, getString(R.string.private_policy));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_guide) {

            changeColorLinearLayout(2);

//            Utility.openBrowser(this, Constants.guide_url);
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra(WebviewActivity.TAG, TAG);
            intent.putExtra(Constants.SITE_URL, Constants.guide_url);
            intent.putExtra(Constants.TITLE, getString(R.string.community_guide));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }

    }
}
