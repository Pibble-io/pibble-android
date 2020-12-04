package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

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
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnPageChange;

public class PrepareMediaFragment extends RuntimePermissionFragment {
    public static final String TAG = "PrepareMediaFragment";

    public static PrepareMediaFragment newInstance() {

        Bundle args = new Bundle();

        PrepareMediaFragment fragment = new PrepareMediaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_mainfeed_prepare_media;
    }

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.indicator)
    PagerSlidingTabStrip mIndicator;

    PrepareMediaAdapter mAdapter;

    private int mCurrentPage, mOldPage;

    @BindView(R.id.camera)
    CameraView camera;

    private boolean isInit = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PrepareMediaAdapter(getChildFragmentManager());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camera.setCropOutput(false);
        camera.setVisibility(View.INVISIBLE);
        mOldPage = 0;
        if (isPermissionGrantedAndRequest(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE.READ_EXTERNAL_STORAGE)) {
            init();
        }
    }

    private void init() {
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(mAdapter.getCount());
        mIndicator.setViewPager(mPager);
        camera.setVisibility(View.INVISIBLE);
//        camera.start();
    }

    @OnPageChange(value = R.id.pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onPageSelected(int newPosition) {

        isInit = true;

        mCurrentPage = newPosition;

        if (isPermissionGrantedAndRequest(Manifest.permission.CAMERA, PERMISSION_CODE.CAMERA) && isPermissionGrantedAndRequest(Manifest.permission.RECORD_AUDIO, PERMISSION_CODE.RECORD_AUDIO)) {
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

                Fragment fragmentToHide = mAdapter.getPage(mOldPage);
                if (fragmentToHide instanceof FragmentLifecycle) {
                    ((FragmentLifecycle) fragmentToHide).onStopFragment();
                }

                mOldPage = newPosition;

            }

        }, 200);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!camera.isStarted() && isInit) {
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

//    @Override
//    public void onDestroyView() {
//        PostMediaHelper.sCachePost.clear();
//        super.onDestroyView();
//    }

    public CameraView getCamera() {
        return camera;
    }

    @Override
    protected void permissionGranted(int permissionRequestCode, boolean isGranted) {
        if (isGranted) {
            if (permissionRequestCode == PERMISSION_CODE.READ_EXTERNAL_STORAGE) {
                init();
            } else if (permissionRequestCode == PERMISSION_CODE.CAMERA) {
                if (isPermissionGrantedAndRequest(Manifest.permission.RECORD_AUDIO, PERMISSION_CODE.RECORD_AUDIO)) {
                    selectedPageView(mCurrentPage);
                }
            } else if (permissionRequestCode == PERMISSION_CODE.RECORD_AUDIO) {
                selectedPageView(mCurrentPage);
            }
        }
    }

    public ArrayList<String> getItemsSelected() {

        Fragment fragment = mAdapter.getPage(0);
        if (fragment instanceof PhotoPickerFragment) {
            return ((PhotoPickerFragment) fragment).getPaths();
        }
        return new ArrayList<>();
    }
}
