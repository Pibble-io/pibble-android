package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaActionSound;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.home.funding.activity.JoinFundingTeamActivity;
import com.star.pibbledev.home.createmedia.mediapicker.rx.DefaultObserver;
import com.star.pibbledev.home.createmedia.mediapicker.utils.AndroidUtil;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.postmedia.MediaPostFirstActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.OnCameraKitEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CameraControls extends LinearLayout {
    private static final String TAG = "CameraControls";

    private int cameraViewId = -1;
    private CameraView cameraView;

    private int coverViewId = -1;
    private View coverView;

    @BindView(R.id.facingButton)
    ImageView facingButton;

    @BindView(R.id.captureButton)
    ImageView captureButton;

    @BindView(R.id.flashButton)
    ImageView flashButton;

    @BindView(R.id.threeButton)
    TextView threeButton;

    private long captureDownTime;
    private long captureStartTime;
    private boolean pendingVideoCapture;
    private boolean capturingVideo;
    private boolean isCaptureVideo = false;

    public CameraControls(Context context) {
        this(context, null);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.custom_camera_controls, this);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CameraControls,
                    0, 0);

            try {
                cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1);
                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1);
                boolean hidingThree = a.getBoolean(R.styleable.CameraControls_hidingThree, false);
                Drawable captureDrawable = a.getDrawable(R.styleable.CameraControls_captureDrawable);
                boolean hidingFlash = a.getBoolean(R.styleable.CameraControls_hidingFlash, false);
                isCaptureVideo = a.getBoolean(R.styleable.CameraControls_captureVideo, false);
                if (captureDrawable != null) {
                    captureButton.setImageDrawable(captureDrawable);
                }

                flashButton.setVisibility(hidingFlash ? GONE : VISIBLE);
                threeButton.setVisibility(hidingThree ? INVISIBLE : VISIBLE);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (cameraViewId != -1) {
            View view = getRootView().findViewById(cameraViewId);
            if (view instanceof CameraView) {
                cameraView = (CameraView) view;
                cameraView.bindCameraKitListener(this);
                setFacingImageBasedOnCamera();
            }
        }

        if (coverViewId != -1) {
            View view = getRootView().findViewById(coverViewId);
            if (view != null) {
                coverView = view;
                coverView.setVisibility(VISIBLE);
            }
        }
    }

    private void setFacingImageBasedOnCamera() {
        if (cameraView.isFacingFront()) {
            facingButton.setImageResource(R.drawable.camera_rotate_copy);
        } else {
            facingButton.setImageResource(R.drawable.camera_rotate_copy);
        }
    }

    //@OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        byte[] jpeg = image.getJpeg();

        long callbackTime = System.currentTimeMillis();
        ResultHolder.dispose();
        ResultHolder.setImage(jpeg);
        ResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
        ResultHolder.setTimeToCallback(callbackTime - captureStartTime);
      /*  Intent intent = new Intent(getContext(), PreviewActivity.class);
        getContext().startActivity(intent);*/
    }

    @OnCameraKitEvent(CameraKitVideo.class)
    public void videoCaptured(CameraKitVideo video) {
        File videoFile = video.getVideoFile();
        if (videoFile != null) {
            ResultHolder.dispose();
            ResultHolder.setVideo(videoFile);
            ResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
           /* Intent intent = new Intent(getContext(), PreviewActivity.class);
            getContext().startActivity(intent);*/
        }
    }

    @OnClick(R.id.captureButton)
    public void onClickCaptureButton(View view) {
        if (cameraView == null) {
            return;
        }

        Timber.d("mCaptureListener %s", mCaptureListener);
        if (mCaptureListener != null) {
            mCaptureListener.onClick(view);
        }

        // capture image
        if (!isCaptureVideo) {
            cameraView.setMethod(CameraKit.Constants.METHOD_STILL);
            final ArrayList<String> list = new ArrayList<>();
            captureImage(false)
                    .flatMap(new Function<CameraKitImage, ObservableSource<String>>() {
                        @Override
                        public ObservableSource<String> apply(CameraKitImage cameraKitImage) {
                            return saveFile(String.format("photo_%s.jpeg", System.currentTimeMillis() / 1000), cameraKitImage.getJpeg());
                        }
                    })
                    .subscribeOn(Schedulers.trampoline())
                    .safeSubscribe(new DefaultObserver<String>() {
                        @Override
                        public void onNext(String filePath) {
                            Timber.d("Image path: [%s]", filePath);
                            if (TextUtils.isEmpty(filePath)) {
                                return;
                            }

                            list.add(filePath);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.d(e, "capture image fail");
                        }

                        @Override
                        public void onComplete() {
                            startPostMediaActivity(getContext(), list);
                            Timber.d("complete capture");
                        }
                    });

//                    .safeSubscribe(new DefaultObserver() {
//                        @Override
//                        public void onNext(String filePath) {
//                            Timber.d("Image path: [%s]", filePath);
//                            if (TextUtils.isEmpty(filePath)) {
//                                return;
//                            }
//
//                            list.add(filePath);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Timber.d(e, "capture image fail");
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            startPostMediaActivity(getContext(), list);
//                            Timber.d("complete capture");
//                        }
//                    });
            return;
        }

        //record video
        if (capturingVideo) {
            stopRecordVideo();
            return;
        }

        capturingVideo = true;

        File file = new File(getContext().getCacheDir(), String.format("video_%s.mp4", System.currentTimeMillis() / 1000));
        Timber.d("file %s", file.getPath());
        cameraView.captureVideo(file, new CameraKitEventCallback<CameraKitVideo>() {
            @Override
            public void callback(CameraKitVideo event) {
                Timber.d("event: " + event.getMessage() + " video file " + event.getVideoFile());
                ArrayList<String> path = new ArrayList<>();
                path.add(event.getVideoFile().getPath());
                startPostMediaActivity(getContext(), path);
                Timber.d("complete capture");
            }
        });
    }

    public void stopRecordVideo() {
        if (capturingVideo) {
            cameraView.stopVideo();
        }
        capturingVideo = false;
    }

    @OnTouch(R.id.captureButton)
    boolean onTouchCapture(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        return false;
    }

   /* @OnTouch(R.id.captureButton)
    boolean onTouchCapture(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                captureDownTime = System.currentTimeMillis();
                pendingVideoCapture = true;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pendingVideoCapture) {
                            capturingVideo = true;
                            cameraView.captureVideo();
                        }
                    }
                }, 250);
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mCaptureListener != null) {
                    mCaptureListener.onClick(view);
                }
                pendingVideoCapture = false;

                if (capturingVideo) {
                    capturingVideo = false;
                    cameraView.stopVideo();
                } else {
                    captureStartTime = System.currentTimeMillis();
                    cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                        @Override
                        public void callback(CameraKitImage event) {
                            imageCaptured(event);
                        }
                    });
                }
                break;
            }
        }
        return true;
    }*/

    @OnTouch(R.id.facingButton)
    boolean onTouchFacing(final View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                coverView.setAlpha(0);
                coverView.setVisibility(VISIBLE);
                coverView.animate()
                        .alpha(1)
                        .setStartDelay(0)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (cameraView.isFacingFront()) {
                                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                                    changeViewImageResource((ImageView) view, R.drawable.camera_rotate_copy);
                                } else {
                                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                                    changeViewImageResource((ImageView) view, R.drawable.camera_rotate_copy);
                                }

                                coverView.animate()
                                        .alpha(0)
                                        .setStartDelay(200)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                coverView.setVisibility(GONE);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();

                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flashButton)
    boolean onTouchFlash(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    changeViewImageResource((ImageView) view, R.drawable.ic_flash_on);
                } else {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    changeViewImageResource((ImageView) view, R.drawable.ic_flash_off);
                }

                break;
            }
        }
        return true;
    }

    void handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return;
            }

            default: {
            }
        }
    }

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }

    private OnClickListener mCaptureListener;

    public void setOnClickCaptureListener(OnClickListener listener) {
        mCaptureListener = listener;
    }

    public void setCamera(@NonNull CameraView view) {
        cameraView = view;
        cameraView.bindCameraKitListener(this);
        setFacingImageBasedOnCamera();
    }

    private Disposable mCaptureImage;
    int count = 0;

    @OnClick(R.id.threeButton)
    public void onClickThreeButton() {
//        final int[] txtCount = {3};
//        threeButton.setText(String.valueOf("2"));
        cameraView.setMethod(CameraKit.Constants.METHOD_STILL);
        final ArrayList<String> list = new ArrayList<>();
        Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        mCaptureImage = disposable;
                    }
                })
                .flatMap(new Function<Long, ObservableSource<CameraKitImage>>() {
                    @Override
                    public ObservableSource<CameraKitImage> apply(Long aLong) {
                        return captureImage(true);
                    }
                })
                .flatMap(new Function<CameraKitImage, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(CameraKitImage cameraKitImage) {

                        count++;
                        return saveFile(String.format("photo_%s_%s.jpeg", count, System.currentTimeMillis() / 1000), cameraKitImage.getJpeg());
                    }
                })
                .take(3)
                .subscribeOn(Schedulers.trampoline())
                .safeSubscribe(new DefaultObserver<String>() {
                    @Override
                    public void onNext(String filePath) {
                        Timber.d("Image path: [%s]", filePath);
                        if (TextUtils.isEmpty(filePath)) {
                            return;
                        }
//                        txtCount[0]--;
//                        threeButton.setText(String.valueOf(txtCount[0]));
                        list.add(filePath);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d(e, "capture image fail");
                    }

                    @Override
                    public void onComplete() {
                        startPostMediaActivity(getContext(), list);
                        Timber.d("complete capture");
                    }
                });
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCaptureImage != null && !mCaptureImage.isDisposed()) {
            mCaptureImage.dispose();
        }
        super.onDetachedFromWindow();
    }

    private static void flashView(@NonNull final View view) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            AndroidUtil.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    flashView(view);
                }
            }, 0);
            return;
        }

        view.setVisibility(VISIBLE);
        view.setAlpha(0.8f);
        view.animate()
                .setDuration(600)
                .alpha(0f)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (Build.VERSION.SDK_INT >= 16) {
                            MediaActionSound sound = new MediaActionSound();
                            sound.play(MediaActionSound.SHUTTER_CLICK);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    @OnTouch(R.id.threeButton)
    public boolean onTouchThreeButton(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        return false;
    }

    private Observable<CameraKitImage> captureImage(boolean flash) {
        Timber.d("capture Image");
        if (coverView != null && flash) {
            flashView(coverView);
        }
        return Observable
                .create(new ObservableOnSubscribe<CameraKitImage>() {
                    @Override
                    public void subscribe(ObservableEmitter<CameraKitImage> emitter) {
                        captureImage(emitter);
                    }
                })
                .timeout(2, TimeUnit.SECONDS);
    }

    private void captureImage(@NonNull final ObservableEmitter<CameraKitImage> emitter) {
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage event) {
                Timber.d("image length %s", event.getJpeg().length);
                emitter.onNext(event);
                emitter.onComplete();
            }
        });
    }

    private Observable<String> saveFile(final String filename, final byte[] jpeg) {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                File file = new File(getContext().getCacheDir(), filename);
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(jpeg);
                outputStream.close();
                return Observable.just(file.getPath());
            }
        });
    }

    private void startPostMediaActivity(Context context, ArrayList<String> entries) {

        ArrayList<String> paths = PostMediaHelper.concatenate(entries);
        Utility.mainFeedParams.originalPaths.addAll(entries);

        if (Utility.g_director.equals(Constants.TEAM)) {
            Intent intent = new Intent(context, JoinFundingTeamActivity.class);
            intent.putStringArrayListExtra("paths", paths);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, MediaPostFirstActivity.class);
            intent.putStringArrayListExtra("paths", paths);
            context.startActivity(intent);
        }

    }
}
