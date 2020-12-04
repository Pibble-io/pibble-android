package com.star.pibbledev.home.createmedia.mediapicker.ui.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.collection.SparseArrayCompat;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created by AnhHieu on 9/28/16.
 * *
 */

public abstract class AbstractFragmentPagerAdapter extends FragmentPagerAdapter {

    private final SparseArrayCompat<WeakReference<Fragment>> holder;

    public AbstractFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.holder = new SparseArrayCompat<>(getCount());
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        if (item instanceof Fragment) {
            holder.put(position, new WeakReference<>((Fragment) item));
        }
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        holder.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getPage(int position) {
        final WeakReference<Fragment> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

}
