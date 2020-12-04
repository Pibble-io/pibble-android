package com.star.pibbledev.home.funding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FundingRewardModel;

import org.web3j.abi.datatypes.Int;

public class CrowdRewardSetActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    public static CrowdRewardSetActivity activity;

    ImageButton btn_back;
    LinearLayout btn_next;
    TextView txt_next;
    EditText edit_pibble, edit_early, edit_early_limit, edit_super, edit_super_limit;

//    boolean isSuper1, isSuper1Limit, isEarly1, isEarly1Limit, isPibble1, isPibble1Limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_create_funding_reward_set);

        activity = this;

        setLightStatusBar();

        txt_next = (TextView) findViewById(R.id.txt_next);
        btn_back = (ImageButton) findViewById(R.id.img_back);
        btn_back.setOnClickListener(this);
        btn_next = (LinearLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        edit_super = (EditText)findViewById(R.id.edit_super);
        edit_early = (EditText)findViewById(R.id.edit_early);
        edit_early_limit = (EditText)findViewById(R.id.edit_early_limit);
        edit_pibble = (EditText)findViewById(R.id.edit_pibble);
        edit_super_limit = (EditText)findViewById(R.id.edit_super_limit);

        edit_super.addTextChangedListener(this);
        edit_early.addTextChangedListener(this);
        edit_early_limit.addTextChangedListener(this);
        edit_pibble.addTextChangedListener(this);
        edit_super_limit.addTextChangedListener(this);

        changeStatusNext(false, false, false, false, false);

    }

    @Override
    public void onClick(View view) {

        if (view == btn_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == btn_next) {

            Utility.mainFeedParams.fundingModel.rewardModel = new FundingRewardModel();

            Utility.mainFeedParams.fundingModel.rewardModel.price = Integer.parseInt(edit_pibble.getText().toString());
            Utility.mainFeedParams.fundingModel.rewardModel.super_early_price = Integer.parseInt(edit_super.getText().toString());
            Utility.mainFeedParams.fundingModel.rewardModel.super_early_amount = Integer.parseInt(edit_super_limit.getText().toString());
            Utility.mainFeedParams.fundingModel.rewardModel.early_price = Integer.parseInt(edit_early.getText().toString());
            Utility.mainFeedParams.fundingModel.rewardModel.early_amount = Integer.parseInt(edit_early_limit.getText().toString());

            Intent intent = new Intent(this, CreateFundingThirdStepActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

//        if (edit_super1.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_super1_limit.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_early1.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_early1_limit.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_pibble1.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_pibble1_limit.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_super2.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_super2_limit.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_early2.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_early2_limit.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_pibble2.getText().hashCode() == editable.hashCode()) {
//
//        } else if (edit_pibble2_limit.getText().hashCode() == editable.hashCode()) {
//
//        }

        changeStatusNext(isValidEditText(edit_super.getText().toString()), isValidEditText(edit_early.getText().toString()), isValidEditText(edit_early_limit.getText().toString()), isValidEditText(edit_pibble.getText().toString()), isValidEditText(edit_super_limit.getText().toString()));

    }

    private boolean isValidEditText(String s) {
        return s != null && s.length() > 0;
    }

    private void changeStatusNext(boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {

        if (b1 && b2 && b3 && b4 && b5) {

            btn_next.setEnabled(true);
            txt_next.setTextColor(getResources().getColor(R.color.colorMain));

        } else {

            btn_next.setEnabled(false);
            txt_next.setTextColor(getResources().getColor(R.color.light_gray));
        }
    }
}
