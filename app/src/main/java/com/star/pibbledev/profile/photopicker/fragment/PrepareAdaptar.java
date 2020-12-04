package com.star.pibbledev.profile.photopicker.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.star.pibbledev.home.createmedia.mediapicker.ui.component.AbstractFragmentPagerAdapter;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.PhotoFragment;

public class PrepareAdaptar extends AbstractFragmentPagerAdapter {
    PrepareAdaptar(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PickerFragment.newInstance();
            case 1:
                return PhotoFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LIBRARY";
            case 1:
                return "PHOTO";
            default:
                return null;
        }
    }
}
