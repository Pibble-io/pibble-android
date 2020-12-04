package com.star.pibbledev.auth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.alertview.ActionStatusDialog;
import com.star.pibbledev.services.global.customview.PinView;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class EmailVerifyActivity extends BaseActivity implements OnKeyboardVisibilityListener, View.OnClickListener, RequestListener {

//    public static final String TAG = "EmailVerifyActivity";
    public static final String ACTIVITY_TYPE = "activity_type";

    private static final String REQUEST_CONFIRM_EMAIL_NEW = "confirm_email_new";
    private static final String REQUEST_CONFIRM_EMAIL_NEW_WITH_PIN = "confirm_email_new_pin";
    private static final String REQUEST_CONFIRM_EMAIL_VERIFY = "confirm_email_verify";
    private static final String REQUEST_CONFIRM_EMAIL_VERIFY_WITH_PIN = "confirm_email_verify_pin";

    private static final String REQUEST_RESEND_CONFIRM = "request_resend_confirm";

    int statusBarHeight;

    LinearLayout linear_Bottom;
    Button btn_next, btn_resend, btn_change_email;
    PinView firstPinView;
    TextView txt_email;

    private String email, str_code;
    private String activityType;
    private String request_type;

    private int cnt_resendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_email_verify);

        email = Utility.getWithoutLastSpace(getIntent().getStringExtra("email"));
        activityType = getIntent().getStringExtra(ACTIVITY_TYPE);

        btn_next = (Button)findViewById(R.id.btn_next);
        btn_resend = (Button)findViewById(R.id.btn_resend);
        btn_resend.setOnClickListener(this);

        btn_change_email = (Button) findViewById(R.id.btn_change_email);
        btn_change_email.setOnClickListener(this);

        linear_Bottom = (LinearLayout)findViewById(R.id.linear_Bottom);
        txt_email = (TextView)findViewById(R.id.txt_email);
        txt_email.setText(email);

        if (activityType.equals(SigninActivity.TAG)) {

            btn_change_email.setVisibility(View.GONE);
            confirmEmailNewUser();
//            verifyEmail();
        } else if (activityType.equals(SignupActivity.TAG)) {
            confirmEmailNewUser();
        }

        firstPinView = (PinView)findViewById(R.id.firstPinView);
        firstPinView.setAnimationEnable(true);
        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    showHUD();

                    if (activityType.equals(SignupActivity.TAG)) {
                        verificationPIN(s.toString());
                    } else if (activityType.equals(SigninActivity.TAG)) {
//                        verifyEmailPIN(s.toString());
                        verificationPIN(s.toString());

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setKeyboardVisibilityListener(this);
    }

    private void confirmEmailNewUser() {

        request_type = REQUEST_CONFIRM_EMAIL_NEW;

        if (Constants.isLifeToken(this)) {

            cnt_resendCode++;

            String token = Utility.getReadPref(this).getStringValue(Constants.SIGN_UP_TOKEN);
            ServerRequest.getSharedServerRequest().confirmSendEmail(this, this, email, "verification", token);

        } else {

            Constants.requestSignUpRefreshToken(this, this);

        }


    }

    private void verificationPIN(String code) {

        request_type = REQUEST_CONFIRM_EMAIL_NEW_WITH_PIN;
        str_code = code;

        Utility.dismissKeyboard(EmailVerifyActivity.this);

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.SIGN_UP_TOKEN);
            ServerRequest.getSharedServerRequest().confirmVerifyEmail(this, this, token, code);

        } else {

            Constants.requestSignUpRefreshToken(this, this);

        }

    }

    @Override
    public void onClick(View v) {

        if (v == btn_resend) {

            if (cnt_resendCode >= 5) {

                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                finishAffinity();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            } else {

                confirmEmailNewUser();

                ActionStatusDialog dialog = new ActionStatusDialog(this, getString(R.string.code_resent));
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Utility.showKeyboard(EmailVerifyActivity.this);
                    }
                }, 1000);
            }

        } else if (v == btn_change_email) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }

    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {

        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown, rect.bottom);

            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible, int newHeight) {
        if (visible) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linear_Bottom.getLayoutParams();
            params.height = newHeight - statusBarHeight;
            linear_Bottom.setLayoutParams(params);
        }
        else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) linear_Bottom.getLayoutParams();
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            linear_Bottom.setLayoutParams(params);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Utility.g_isCalledRefreshToken) {

            Constants.saveSignUpRefreshToken(this, objResult);

            if (request_type.equals(REQUEST_CONFIRM_EMAIL_NEW)) confirmEmailNewUser();
            else if (request_type.equals(REQUEST_CONFIRM_EMAIL_NEW_WITH_PIN)) verificationPIN(str_code);

        } else {

            hideHUD();

            if (request_type.equals(REQUEST_CONFIRM_EMAIL_NEW_WITH_PIN)) {

                Utility.dismissKeyboard(this);

                Utility.getSavedPref(this).saveBoolean(Constants.EMAIL_VERIFIED, true);

                boolean is_phone_verified = Utility.getReadPref(this).getBooleanValue(Constants.PHONE_VERIFIED);

                if (is_phone_verified) {

                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                } else {

                    Intent intent = new Intent(this, PhoneVerificationActivity.class);
                    intent.putExtra(PhoneVerificationActivity.ACTIVITY_TYPE, SigninActivity.TAG);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                }

            }

        }
    }

    @Override
    public void failed(String strError, int errorCode) {
        hideHUD();
        request_type = "";

        firstPinView.setText("");

        Utility.showKeyboard(this);
        Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
