package com.star.pibbledev.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;

public class PasswordResetSuccessfullActivity extends BaseActivity implements  View.OnClickListener {

    Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_resetpassword_success);

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_next) {
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }
    }

}