package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.BaseFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.FragmentLifecycle;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component.CameraControls;
import com.star.pibbledev.home.createmedia.mediapicker.utils.AndroidUtil;


import butterknife.BindView;

public class PhotoFragment extends BaseFragment implements FragmentLifecycle {
    public static PhotoFragment newInstance() {

        Bundle args = new Bundle();

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_mainfeed_photo;
    }

    @BindView(R.id.camera_controls)
    CameraControls cameraControls;

    @BindView(R.id.photo_cover)
    View mCover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getParentFragment() instanceof PrepareMediaFragment) {
            cameraControls.setCamera(((PrepareMediaFragment) getParentFragment()).getCamera());
        }
    }

    @Override
    public void onStartFragment() {
        AndroidUtil.flashView(mCover);
    }

    @Override
    public void onStopFragment() {

    }
}
