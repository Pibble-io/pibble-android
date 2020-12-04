package com.star.pibbledev.home.promotion;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;

public abstract class PromotionCreateDialog extends Dialog implements View.OnClickListener {

    private LinearLayout btn_action, btn_cancel;
    private TextView txt_message;

    private Context context;
    private int budget;

    protected PromotionCreateDialog(@NonNull Context context, int budget) {
        super(context);
        this.context = context;
        this.budget = budget;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_promotion_create_dialog);

        txt_message = (TextView)findViewById(R.id.txt_message);
        btn_action = (LinearLayout)findViewById(R.id.btn_action);
        btn_cancel = (LinearLayout)findViewById(R.id.btn_cancel);

        btn_action.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        setTitle(budget);
    }

    public void setTitle(int totalBudget){
        String str_budget = Utility.formatedNumberString(Utility.mainFeedParams.promotion_budget);
        txt_message.setText(String.format("%s %s%s", context.getString(R.string.promotion_dialog_title1), str_budget, context.getString(R.string.promotion_dialog_title2)));
    }

    @Override
    public void onClick(View v) {
        if (v == btn_action) onClickButton(1);
        else if (v == btn_cancel) onClickButton(0);
    }

    public abstract void onClickButton(int position);
}
