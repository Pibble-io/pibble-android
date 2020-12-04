package com.star.pibbledev.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.star.pibbledev.R;

public class ForgotpasswordEmailsentActivity extends Activity implements View.OnClickListener {

    public static String METHOD_TYPE = "resetViaEmail";

    Button btn_next;
//    ImageButton img_back;
    String str_email;
    TextView txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_forgot_emailsent);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

//        img_back = (ImageButton) findViewById(R.id.img_back);
//        img_back.setOnClickListener(this);

        str_email = getIntent().getStringExtra("email");

        txt_email = (TextView)findViewById(R.id.txt_email);
        txt_email.setText(str_email);

    }

    @Override
    public void onClick(View v) {

        if( v == btn_next ) {

            Intent intent = new Intent(this, VerifyCodeActivity.class);
            intent.putExtra("methodType", METHOD_TYPE);
            intent.putExtra("email", str_email);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }
//        else if (v == img_back) {
//            finish();
//            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
//        }
    }

}