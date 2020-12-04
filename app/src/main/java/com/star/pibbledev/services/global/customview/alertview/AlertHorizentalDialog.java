package com.star.pibbledev.services.global.customview.alertview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;

public abstract class AlertHorizentalDialog extends Dialog implements View.OnClickListener {

    private LinearLayout btn_action, btn_cancel;

    private String title, message, button1, button2;
    private int button1Color, button2Color;
    private Context context;

    protected AlertHorizentalDialog(@NonNull Context context, String title, String message, String btnTitle1, String btnTitle2, int buttonColor1, int buttonColor2) {
        super(context);
        this.context = context;
        this.title = title;
        this.message = message;
        this.button1 = btnTitle1;
        this.button2 = btnTitle2;
        this.button1Color = buttonColor1;
        this.button2Color = buttonColor2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_alertview_horizental_dialog);

        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        TextView txt_message = (TextView) findViewById(R.id.txt_message);
        TextView txt_action = (TextView) findViewById(R.id.txt_action);
        TextView txt_cancel = (TextView) findViewById(R.id.txt_cancel);

        btn_action = (LinearLayout)findViewById(R.id.btn_action);
        btn_cancel = (LinearLayout)findViewById(R.id.btn_cancel);

        if (title == null) {
            txt_title.setVisibility(View.GONE);
        } else {
            txt_title.setText(title);
        }

        txt_message.setText(message);

        txt_action.setText(button2);
        txt_action.setTextColor(context.getResources().getColor(button2Color));

        txt_cancel.setText(button1);
        txt_cancel.setTextColor(context.getResources().getColor(button1Color));


        btn_action.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_action) onClickButton(1);
        else if (v == btn_cancel) onClickButton(0);
    }

    public abstract void onClickButton(int position);
}
