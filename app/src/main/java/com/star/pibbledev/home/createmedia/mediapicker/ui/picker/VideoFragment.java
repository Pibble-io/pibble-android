package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.BaseFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.FragmentLifecycle;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component.CameraControls;
import com.star.pibbledev.home.createmedia.mediapicker.utils.AndroidUtil;
import com.star.pibbledev.home.createmedia.mediapicker.utils.TimeUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import timber.log.Timber;

public class VideoFragment extends BaseFragment implements FragmentLifecycle {
    public static VideoFragment newInstance() {

        Bundle args = new Bundle();

        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_mainfeed_video;
    }

    @BindView(R.id.camera_controls)
    CameraControls mCameraControls;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.count_time)
    TextView mCountTimeView;

    @BindView(R.id.video_cover)
    View mCover;

    private Timer timer;
    private int count;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCameraControls.setOnClickCaptureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer == null) {
                    count = 1;
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(0);
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            countTime();
                        }
                    }, 1000, 1000);
                } else {
                    cancelTimer();
                }
            }
        });
    }

    private void countTime() {
        Timber.d("progress %s", count);
        AndroidUtil.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProgress != null) {
                    mProgress.setProgress(mProgress.getProgress() + 1);
                }
                if (mCountTimeView != null) {
                    mCountTimeView.setText(TimeUtils.formatCountDownTime(count * 1000));
                }
                count++;
            }
        }, 0);

    }

    @Override
    public void onStartFragment() {
        AndroidUtil.flashView(mCover);
    }

    @Override
    public void onStopFragment() {
        cancelTimer();
        hideProgress();
        AndroidUtil.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCameraControls != null) {
                    mCameraControls.stopRecordVideo();
                }
            }
        }, 250);
    }

    @Override
    public void onDestroyView() {
        cancelTimer();
        super.onDestroyView();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void hideProgress() {
        mProgress.setVisibility(View.INVISIBLE);
        mCountTimeView.setText(null);
    }
}
