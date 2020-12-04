package com.star.pibbledev.home.createmedia.mediapicker.ui.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.utils.AndroidUtil;

public abstract class ReferActivity extends AppCompatActivity {
    protected abstract BaseFragment fragment();

    public int getResLayoutId() {
        return R.layout.activity_actionbar;
    }

    @Nullable
    protected Toolbar mToolbar;

    @Nullable
    protected ImageView mLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayoutId());
        if (getIntent() == null) {
            finish();
        }
        mToolbar = findViewById(R.id.toolbar);
        mLogo = findViewById(R.id.logo);
        TextView titleView = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        setNavigation(true);
        if (titleView != null) {
            titleView.setText(mToolbar == null ? "" : mToolbar.getTitle());
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            addFragment(fragment());
        }

    }

    private void setNavigation(boolean dark) {
        int color = dark ? android.R.color.white : android.R.color.black;
        setNavigation(color);
    }

    protected void setNavigation(@ColorRes int color) {
        Drawable drawable = AndroidUtil.tintDrawable(this, R.drawable.back, color);
        if (getToolbar() != null) {
            getToolbar().setNavigationIcon(drawable);
        }
    }

    private void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            addFragment(fragment, R.id.fragment_container);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Nullable
    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setLogoVisibility(int visibility) {
        if (mLogo != null) {
            mLogo.setVisibility(visibility);
        }
    }

    protected void addFragment(BaseFragment f, int id) {
        if (getSupportFragmentManager().findFragmentByTag(f.TAG) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, f, f.TAG);
            ft.commit();
        }
    }
}
