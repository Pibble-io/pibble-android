package com.star.pibbledev.profile.activity.setting.account;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.adaptar.SettingLanguageRecyclerAdaptar;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;

import java.util.Locale;

public class SettingAccountChangeLanguageActivity extends BaseActivity implements View.OnClickListener, SettingLanguageRecyclerAdaptar.listListener {

    public static boolean isRestarted;

    ImageButton img_back;
    RecyclerView recyclerview;

    String languages_iso[] = {"ko", "en"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting_account_language);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        String languages[] = {"Korean", "English"};

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        SettingLanguageRecyclerAdaptar adaptar = new SettingLanguageRecyclerAdaptar(this, languages, languages_iso);
        recyclerview.setAdapter(adaptar);
        adaptar.setClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        }

    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onClickCell(int position) {

        int cellPos = position;

        AlertVerticalDialog dialog = new AlertVerticalDialog(this, getString(R.string.confirm),
                getString(R.string.change_language),
                getString(R.string.change), getString(R.string.cancel), R.color.colorMain, R.color.black) {

            @Override
            public void onClickButton(int position) {

                if (position == 0) {

                    String languageToLoad  = languages_iso[cellPos]; // your language

                    Locale myLocale = new Locale(languageToLoad);
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);

                    isRestarted = true;

                    Utility.getSavedPref(SettingAccountChangeLanguageActivity.this).saveString(Constants.LANGUAGE, languages_iso[cellPos]);

                    restartActivity();
                }

                dismiss();

            }

        };
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}
