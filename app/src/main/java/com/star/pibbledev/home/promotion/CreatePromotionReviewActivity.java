package com.star.pibbledev.home.promotion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

public class CreatePromotionReviewActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back;
    TextView txt_destination, txt_action_button, txt_budget, txt_create_detail;
    LinearLayout linear_create, linear_preview;

    boolean isChangedProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promotion_review);

        setLightStatusBar();

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        txt_destination = (TextView)findViewById(R.id.txt_destination);
        txt_action_button = (TextView)findViewById(R.id.txt_action_button);
        txt_budget = (TextView)findViewById(R.id.txt_budget);
        txt_create_detail = (TextView)findViewById(R.id.txt_create_detail);

        linear_create = (LinearLayout)findViewById(R.id.linear_create);
        linear_create.setOnClickListener(this);
        linear_preview = (LinearLayout)findViewById(R.id.linear_preview);
        linear_preview.setOnClickListener(this);

        String str_reach = Utility.formatedNumberString(Utility.mainFeedParams.promotion_budget / 30) + " - " + Utility.formatedNumberString(Utility.mainFeedParams.promotion_budget / 10);
        txt_create_detail.setText(getString(R.string.promotion_review_detail1) + " " + str_reach + " " + getString(R.string.promotion_review_detail2));

        txt_budget.setText(String.valueOf(Utility.mainFeedParams.promotion_budget) + " PIB / " + String.valueOf(Utility.mainFeedParams.promotion_duration) + " " + getString(R.string.days));

        txt_action_button.setText(Utility.mainFeedParams.promotion_actionButton);

        if (Utility.mainFeedParams.promotion_destination.equals(Constants.PROMOTION_PROFILE)) txt_destination.setText("@" + Utility.getReadPref(this).getStringValue(Constants.USERNAME));
        else txt_destination.setText(Utility.mainFeedParams.promotion_site);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!linear_create.isEnabled()) linear_create.setEnabled(true);

        if (Utility.g_isChanged) {

            AlertVerticalDialog dialog = new AlertVerticalDialog(this, getString(R.string.thank_you),
                    getString(R.string.alert_payment_successfull),
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {
                @Override
                public void onClickButton(int position) {

                    Utility.mainFeedParams = null;

                    dismiss();

                    if (CreatePromotionDestinationActivity.createPromotionDestination != null) CreatePromotionDestinationActivity.createPromotionDestination.finish();
                    if (CreatePromotionBudgetActivity.createPromotionBuget != null) CreatePromotionBudgetActivity.createPromotionBuget.finish();

                    finish();
                    overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

                }

            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

    private void createPromotion() {

        Intent intent = new Intent(this, RegisterPinActivity.class);
        intent.putExtra(RegisterPinActivity.TYPE_ACTION, Constants.REQUEST_CREATE_PROMOTION );
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (view == linear_create) {

            PromotionCreateDialog dialog = new PromotionCreateDialog(this, Utility.mainFeedParams.promotion_budget) {

                @Override
                public void onClickButton(int position) {

                    if (position == 1) {

                        linear_create.setEnabled(false);

                        createPromotion();
                    }
                    dismiss();
                }
            };

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else if (view == linear_preview) {

            Intent intent = new Intent(this, PromotionPreviewActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }

    }

}
