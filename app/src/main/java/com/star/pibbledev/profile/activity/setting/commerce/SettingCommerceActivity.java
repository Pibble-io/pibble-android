package com.star.pibbledev.profile.activity.setting.commerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.services.network.BackendAPI;

public class SettingCommerceActivity extends BaseActivity implements View.OnClickListener {

    ImageButton img_back;
    LinearLayout linear_mygoods, linear_mypurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting_commerce);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        linear_mygoods = (LinearLayout)findViewById(R.id.linear_mygoods);
        linear_mygoods.setOnClickListener(this);

        linear_mypurchased = (LinearLayout)findViewById(R.id.linear_mypurchased);
        linear_mypurchased.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == linear_mygoods) {

            linear_mygoods.setBackgroundColor(getResources().getColor(R.color.line_background_color));
            linear_mypurchased.setBackgroundColor(getResources().getColor(R.color.white));

            Intent intent = new Intent(this, UsersActivity.class);

            intent.putExtra(UsersActivity.USER_NAME, "me");

            intent.putExtra(UsersActivity.SERVER_URL, BackendAPI.get_posts_mygoods);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_MYGOODS);

            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == linear_mypurchased) {

            linear_mygoods.setBackgroundColor(getResources().getColor(R.color.white));
            linear_mypurchased.setBackgroundColor(getResources().getColor(R.color.line_background_color));

            Intent intent = new Intent(this, UsersActivity.class);

            intent.putExtra(UsersActivity.USER_NAME, "me");

            intent.putExtra(UsersActivity.SERVER_URL, BackendAPI.get_posts_mypurchased);
            intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_MY_PURCHASED);

            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }

    }
}
