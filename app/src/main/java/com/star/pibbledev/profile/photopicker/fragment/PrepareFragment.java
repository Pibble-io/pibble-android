package com.star.pibbledev.profile.photopicker.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.ReferActivity;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.RuntimePermissionFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.FragmentLifecycle;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.PagerSlidingTabStrip;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnPageChange;

public class PrepareFragment extends RuntimePermissionFragment {
    public static final String TAG = "PrepareFragment";

    public static PrepareFragment newInstance() {

        Bundle args = new Bundle();

        PrepareFragment fragment = new PrepareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_photoeditor_prepare;
    }

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.indicator)
    PagerSlidingTabStrip mIndicator;

    PrepareAdaptar mAdapter;

    private int mCurrentPage;

    @BindView(R.id.camera)
    CameraView camera;

    private boolean isInit = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PrepareAdaptar(getChildFragmentManager());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camera.setCropOutput(false);
        camera.setVisibility(View.INVISIBLE);
        if (isPermissionGrantedAndRequest(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE.READ_EXTERNAL_STORAGE)) {
            init();
        }
    }

    private void init() {
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(mAdapter.getCount());
        mIndicator.setViewPager(mPager);
        camera.setVisibility(View.VISIBLE);
//        camera.start();
//        isInit = true;
    }

    @OnPageChange(value = R.id.pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onPageSelected(int newPosition) {

        isInit = true;

        mCurrentPage = newPosition;

        if (isPermissionGrantedAndRequest(Manifest.permission.CAMERA, PERMISSION_CODE.CAMERA)) {
            selectedPageView(newPosition);
        }
    }

    private void selectedPageView(int newPosition) {

        if (newPosition == 0) {
            camera.stop();
            camera.setVisibility(View.INVISIBLE);
        } else {
            camera.setVisibility(View.VISIBLE);
            camera.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getActivity() instanceof ReferActivity) {
                    ((ReferActivity) getActivity()).setLogoVisibility(newPosition == 0 ? View.GONE : View.VISIBLE);
                }

                Fragment fragmentToShow = mAdapter.getPage(newPosition);
                if (fragmentToShow instanceof FragmentLifecycle) {
                    ((FragmentLifecycle) fragmentToShow).onStartFragment();
                }

                Fragment fragmentToHide = mAdapter.getPage(mCurrentPage);
                if (fragmentToHide instanceof FragmentLifecycle) {
                    ((FragmentLifecycle) fragmentToHide).onStopFragment();
                }


            }

        }, 200);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInit) {
            camera.start();
        }
    }

    @Override
    public void onPause() {
        if (camera.isStarted()) {
            camera.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        PostMediaHelper.sCachePost.clear();
        super.onDestroyView();
    }

    public CameraView getCamera() {
        return camera;
    }

    @Override
    protected void permissionGranted(int permissionRequestCode, boolean isGranted) {

        if (isGranted) {
            if (permissionRequestCode == PERMISSION_CODE.READ_EXTERNAL_STORAGE) {
                init();
            } else if (permissionRequestCode == PERMISSION_CODE.CAMERA) {
                selectedPageView(mCurrentPage);
            }
        }
    }

    public ArrayList<String> getItemsSelected() {
        Fragment fragment = mAdapter.getPage(0);
        if (fragment instanceof PickerFragment) {
            return ((PickerFragment) fragment).getPaths();
        }
        return new ArrayList<>();
    }
}
