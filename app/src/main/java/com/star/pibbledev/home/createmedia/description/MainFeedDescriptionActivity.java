package com.star.pibbledev.home.createmedia.description;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;


public class MainFeedDescriptionActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back, img_check;
    EditText txt_description;
    LinearLayout linear_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_description);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        img_check = (ImageButton)findViewById(R.id.img_check);
        img_check.setOnClickListener(this);

        txt_description = (EditText)findViewById(R.id.txt_des);
        linear_edit = (LinearLayout)findViewById(R.id.linear_edit);

        if (Utility.mainFeedParams != null) txt_description.setText(Utility.mainFeedParams.caption);

    }

    @Override
    public void onClick(View v) {
        if (v == img_check) {

            Utility.mainFeedParams.caption = txt_description.getText().toString();

            linear_edit.post(new Runnable() {

                @Override
                public void run() {
                    Utility.mainFeedParams.height_textview = linear_edit.getHeight();
                }

            });

        }

//        Intent intent = new Intent(this, PostsMediaActivity.class);
//        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
    }
}
