package com.star.pibbledev.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.star.pibbledev.R;

public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    public static final String ACTIVITY_TYPE = "forgotPassword";

    ImageButton btn_back;
    FrameLayout frame_viaSMS, frame_viaEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_forgotpassword);

        btn_back = (ImageButton) findViewById(R.id.img_back);
        btn_back.setOnClickListener(this);

        frame_viaSMS = (FrameLayout)findViewById(R.id.frame_viaSMS);
        frame_viaEmail = (FrameLayout)findViewById(R.id.frame_viaEmail);
        frame_viaSMS.setOnClickListener(this);
        frame_viaEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if( v == btn_back ){
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        } else if (v == frame_viaSMS) {

            Intent intent = new Intent(this, PhoneVerificationActivity.class);
            intent.putExtra(PhoneVerificationActivity.ACTIVITY_TYPE, ACTIVITY_TYPE);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == frame_viaEmail) {

            Intent intent = new Intent(this, ForgotPasswordEmailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

}