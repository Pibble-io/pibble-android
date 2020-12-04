package com.star.pibbledev.auth;

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

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForgotPasswordEmailActivity extends BaseActivity implements OnKeyboardVisibilityListener, View.OnClickListener, RequestListener {
    int statusBarHeight;

    TextView txt_email;
    EditText edit_email;
    View viewEmailBottomline;
    FrameLayout frameLayoutBottom;
    Button btn_next;
    ImageButton img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_forgot_email);

        statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        txt_email = (TextView) findViewById(R.id.txt_email);

        edit_email = (EditText) findViewById(R.id.edit_email);

        viewEmailBottomline = (View) findViewById(R.id.viewEmailBottomline);

        frameLayoutBottom = (FrameLayout) findViewById(R.id.frameLayoutBottom);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        img_back = (ImageButton) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int pos = s.length();
                Layout layout = edit_email.getLayout();

                if (layout == null) return;

                float x = layout.getPrimaryHorizontal(pos);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewEmailBottomline.getLayoutParams();
                params.width = (int) x;
                viewEmailBottomline.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 0) {
                    txt_email.setVisibility(View.INVISIBLE);
                } else {
                    txt_email.setVisibility(View.VISIBLE);
                }
            }
        });

        setKeyboardVisibilityListener(this);
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
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLayoutBottom.getLayoutParams();
            params.height = newHeight - statusBarHeight;
            frameLayoutBottom.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLayoutBottom.getLayoutParams();
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            frameLayoutBottom.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {

        if( v == btn_next ) {

            if (edit_email.getText().toString().length() == 0) {
                Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.email_is_empty), getString(R.string.ok));
                return;
            }

            showHUD();

            ServerRequest.getSharedServerRequest().resetPasswordViaEmail(this, this, Utility.getWithoutLastSpace(edit_email.getText().toString()));

        } else if (v == img_back) {
            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {
        hideHUD();

        Intent intent = new Intent(this, ForgotpasswordEmailsentActivity.class);
        intent.putExtra("email", Utility.getWithoutLastSpace(edit_email.getText().toString()));
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void failed(String strError, int errorCode) {
        hideHUD();
        Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}

