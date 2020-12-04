package com.star.pibbledev.services.global.customview.alertview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.star.pibbledev.R;

public class ActionStatusDialog extends Dialog {

    private String mTitle;

    public ActionStatusDialog(@NonNull Context context, String title) {
        super(context);
        this.mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_custom_action_status_dialog);

        TextView txt_title = (TextView) findViewById(R.id.txt_title);

        txt_title.setText(mTitle);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 300);
    }
}
