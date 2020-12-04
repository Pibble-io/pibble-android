package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.AbstractFragmentPagerAdapter;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

final public class PrepareMediaAdapter extends AbstractFragmentPagerAdapter {
    public PrepareMediaAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PhotoPickerFragment.newInstance();
            case 1:
                return PhotoFragment.newInstance();
            default:
                return VideoFragment.newInstance();
        }
    }

    @Override
    public int getCount() {

        if (Utility.g_director.equals(Constants.COMMERCE)) return 2;
        else return 3;
    }

    public int getTitle(int position) {
        switch (position) {
            case 0:
                return R.string.library;
            case 1:
                return R.string.photo;
            case 2:
                return R.string.video;
            default:
                return -1;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LIBRARY";
            case 1:
                return "PHOTO";
            case 2:
                return "VIDEO";
            default:
                return null;
        }
    }
}
