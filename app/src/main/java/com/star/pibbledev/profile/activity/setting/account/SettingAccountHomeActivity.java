package com.star.pibbledev.profile.activity.setting.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;

import java.util.ArrayList;

public class SettingAccountHomeActivity extends BaseActivity implements View.OnClickListener {

    public static boolean isRestarted;

    ImageButton img_back;
    LinearLayout linear_muted_accounts, linear_username, linear_language;

    ArrayList<LinearLayout> ary_linears = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_account_home);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        linear_language = (LinearLayout)findViewById(R.id.linear_language);
        linear_language.setOnClickListener(this);
        ary_linears.add(linear_language);
        linear_username = (LinearLayout)findViewById(R.id.linear_username);
        linear_username.setOnClickListener(this);
        ary_linears.add(linear_username);
        linear_muted_accounts = (LinearLayout)findViewById(R.id.linear_muted_accounts);
        linear_muted_accounts.setOnClickListener(this);
        ary_linears.add(linear_muted_accounts);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SettingAccountChangeLanguageActivity.isRestarted) {

            SettingAccountChangeLanguageActivity.isRestarted = false;
            restartActivity();
        }
    }

    private void changeColorLinearLayout(int position) {

        if (ary_linears != null && ary_linears.size() > position) {

            for (int i = 0; i < ary_linears.size(); i++) {

                if (i == position) {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.line_background_color));
                } else {
                    ary_linears.get(i).setBackgroundColor(getResources().getColor(R.color.white));
                }

            }

        }

    }

    private void restartActivity() {

        isRestarted = true;

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_language) {

            changeColorLinearLayout(0);

            Intent intent = new Intent(this, SettingAccountChangeLanguageActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_username) {

            changeColorLinearLayout(1);

            Intent intent = new Intent(this, SettingAccountUsernameActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_muted_accounts) {

            changeColorLinearLayout(2);

            Intent intent = new Intent(this, SettingAccountMutedUsersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }

    }
}
