package com.star.pibbledev.home.funding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

public class CrowdRewardTypeSelectActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static CrowdRewardTypeSelectActivity activity;

    ImageButton btn_back;
    LinearLayout btn_next, linear_no_reward, linear_pledge;
    CheckBox check_no_reward, check_pledge;
    TextView txt_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_create_funding_reward_select);

        activity = this;

        setLightStatusBar();

        txt_next = (TextView) findViewById(R.id.txt_next);
        btn_back = (ImageButton) findViewById(R.id.img_back);
        btn_back.setOnClickListener(this);
        btn_next = (LinearLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        linear_no_reward = (LinearLayout)findViewById(R.id.linear_no_reward);
        linear_no_reward.setOnClickListener(this);
        linear_pledge = (LinearLayout)findViewById(R.id.linear_pledge);
        linear_pledge.setOnClickListener(this);
        check_no_reward = (CheckBox)findViewById(R.id.check_no_reward);
        check_no_reward.setOnCheckedChangeListener(this);
        check_pledge = (CheckBox)findViewById(R.id.check_pledge);
        check_pledge.setOnCheckedChangeListener(this);

        setChangeNextButton(false);

    }

    private void setChangeNextButton(boolean isValid) {

        if (isValid) {
            btn_next.setEnabled(true);
            txt_next.setTextColor(getResources().getColor(R.color.colorMain));
        } else {

            btn_next.setEnabled(false);
            txt_next.setTextColor(getResources().getColor(R.color.light_gray));
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btn_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_next) {

            if (check_no_reward.isChecked()) {

                Utility.mainFeedParams.fundingModel.funding_type = Constants.FUNDING_CROWD_NO_REWARD;

                Intent intent = new Intent(this, CreateFundingThirdStepActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            } else if (check_pledge.isChecked()) {

                Utility.mainFeedParams.fundingModel.funding_type = Constants.FUNDING_CROWD_REWARD;

                Intent intent = new Intent(this, CrowdRewardSetActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            }

        } else if (view == linear_no_reward) {

            if (check_no_reward.isChecked()) check_no_reward.setChecked(false);
            else check_no_reward.setChecked(true);

        } else if (view == linear_pledge) {

            if (check_pledge.isChecked()) check_pledge.setChecked(false);
            else check_pledge.setChecked(true);

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton == check_no_reward) {

            if (b) {
                check_pledge.setChecked(false);
            }

        } else if (compoundButton == check_pledge) {

            if (b) {
                check_no_reward.setChecked(false);
            }
        }

        if (check_no_reward.isChecked() || check_pledge.isChecked()) setChangeNextButton(true);
        else setChangeNextButton(false);

    }
}
