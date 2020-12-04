package com.star.pibbledev.profile.photopicker.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperView;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.BaseFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.FragmentLifecycle;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.FileResultCallback;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.PhotoDirLoaderCallbacks;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.Photo;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;
import com.star.pibbledev.home.utility.FileManager;
import com.star.pibbledev.services.network.ScalingUtilities;
import com.star.pibbledev.profile.photopicker.PickerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class PickerFragment extends BaseFragment implements PickerGridAdaptar.OnItemClickPhotoGridListener, FragmentLifecycle {

    public static PickerFragment newInstance() {

        Bundle args = new Bundle();

        PickerFragment fragment = new PickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.mPreview)
    CropperView mPreview;

    @BindView(R.id.img_scale)
    ImageView mimg_scale;

    @BindView(R.id.linear_scale)
    LinearLayout linear_scale;

    @BindView(R.id.mAppBarContainer)
    AppBarLayout mAppBarContainer;

    private PickerGridAdaptar photoGridAdapter;
    private ArrayList<String> selectedPaths = new ArrayList<>();
    private Photo mPhoto;

    private boolean isScaled = true;
    private String mCropedPath = "";
    private String mSelectedPath = "";
    private boolean isCropGestureStart = false;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_photoeditor_photo_picker;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mPreview.setGridCallback(new CropperView.GridCallback() {
            @Override
            public boolean onGestureStarted() {

                controllAppbarlayoutScrolling(false);
                isCropGestureStart = true;

                return true;
            }

            @Override
            public boolean onGestureCompleted() {

                controllAppbarlayoutScrolling(true);
                isCropGestureStart = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!isCropGestureStart) {

                            mCropedPath = createBitmap();

                            String filename = mSelectedPath.substring(mSelectedPath.lastIndexOf("/")+1);

                            int index = -1;

                            for (int i = 0; i < PostMediaHelper.sCachePost.size(); i++) {
                                String path = PostMediaHelper.sCachePost.get(i);
                                String tempname = path.substring(path.lastIndexOf("/")+1);

                                if (tempname.toLowerCase().contains(filename.toLowerCase())) {
                                    index = i;
                                    break;
                                }
                            }

                            if (index != -1) {
                                PostMediaHelper.sCachePost.set(index, mCropedPath);
                            }
                        }

                    }
                }, 500);

                return false;
            }
        });

        linear_scale.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (isScaled) {
                        mPreview.fitToCenter();
                    } else {
                        mPreview.cropToCenter();
                    }

                    isScaled = !isScaled;
                }

                return false;
            }
        });

//        mPreview.setDebug(true);
        mPreview.setMakeSquare(false);

        getDataFromMedia();
    }

    private void getDataFromMedia() {
        if (getActivity() == null) {
            return;
        }

        getActivity().getSupportLoaderManager()
                .initLoader(0, new Bundle(),
                        new PhotoDirLoaderCallbacks(getActivity(),
                                new FileResultCallback<PhotoDirectory>() {
                                    @Override
                                    public void onResultCallback(List<PhotoDirectory> dirs) {
                                        if (photoGridAdapter != null
                                                && photoGridAdapter.getItemCount() > 0) {
                                            return;
                                        }
                                        Timber.d("dirs %s", dirs.size());
                                        updateList(dirs);
                                    }
                                }));
    }

    private void updateList(List<PhotoDirectory> dirs) {

        ArrayList<Photo> photos = new ArrayList<>();

        photos.addAll(dirs.get(Utility.g_photoDirectory).getPhotos());

        Collections.sort(photos, new DateSorter());

        if (photos.size() > 0) {
            emptyView.setVisibility(View.GONE);
            mPhoto = photos.get(0);
            showTopImageView(mPhoto);

        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        if (photoGridAdapter != null) {
            photoGridAdapter.setData(photos);
            if (photoGridAdapter != null) photoGridAdapter.notifyDataSetChanged();
        } else {
            photoGridAdapter = new PickerGridAdaptar(getActivity(), photos, selectedPaths);
            photoGridAdapter.setItemClickPhotoGridListener(this);
            recyclerView.setAdapter(photoGridAdapter);
        }

        Timber.d("item count %s", photoGridAdapter.getItemCount());

    }

    public ArrayList<String> getPaths() {

        return PostMediaHelper.sCachePost;
    }

    private void controllAppbarlayoutScrolling(boolean flag) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarContainer.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return flag;
                }
            });
        }

    }

    private void showTopImageView(Photo photo) {

        mSelectedPath = photo.getPath();

        mAppBarContainer.setExpanded(true);

        int size = FileManager.getSizeBitmap(mSelectedPath);
        int width = (int)(size/100000);
        int height = (int)(size%100000);
        Bitmap bitmap_checked = FileManager.getResizeImage(getContext(), width, height, ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);

        mPreview.setImageBitmap(bitmap_checked);
        mPreview.setAlpha(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mPreview != null) {
                    mPreview.fitToCenter();
                    mPreview.setAlpha(1);
                }

            }
        }, 10);

        isScaled = false;

    }


    public String bitmapConvertToFile(Bitmap bitmap, String filename) {
        FileOutputStream fileOutputStream = null;

        File bitmapFile = null;

        try {
            File file = new File(getContext().getCacheDir(), "");
            if (!file.exists()) {
                file.mkdir();
            }

            bitmapFile = new File(file, filename);
            fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return bitmapFile.getAbsolutePath();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStartFragment() {
    }

    @Override
    public void onStopFragment() {
    }

    @Override
    public void onSelected(Photo photo) {
        PostMediaHelper.sCachePost.clear();
        PostMediaHelper.sCachePost.add(photo.getPath());
        showTopImageView(photo);

        ((PickerActivity)getActivity()).changeButtonStatus(true);
    }

    @Override
    public void onUnSelected(Photo photo) {

        String deSelectedPath = photo.getPath();
        String filename = deSelectedPath.substring(deSelectedPath.lastIndexOf("/")+1);
        int index = -1;

        for (int i = 0; i < PostMediaHelper.sCachePost.size(); i++) {
            String path = PostMediaHelper.sCachePost.get(i);
            String tempname = path.substring(path.lastIndexOf("/")+1);

            if (tempname.toLowerCase().contains(filename.toLowerCase())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            PostMediaHelper.sCachePost.remove(index);
        } else {
            PostMediaHelper.sCachePost.remove(photo.getPath());
        }

        if (PostMediaHelper.sCachePost.size() > 0) {

            ((PickerActivity)getActivity()).changeButtonStatus(true);

            String lastPath = PostMediaHelper.sCachePost.get(PostMediaHelper.sCachePost.size() - 1);

            mSelectedPath = lastPath;

            int size = FileManager.getSizeBitmap(mSelectedPath);
            int width = (int)(size/100000);
            int height = (int)(size%100000);
            Bitmap bitmap_checked = FileManager.getResizeImage(getContext(), width, height, ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);

            mPreview.setImageBitmap(bitmap_checked);
            mPreview.setAlpha(0);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mPreview != null) {
                        mPreview.fitToCenter();
                        mPreview.setAlpha(1);
                    }
                }
            }, 20);

        } else {
            showTopImageView(mPhoto);
            ((PickerActivity)getActivity()).changeButtonStatus(false);
        }

    }

    private String createBitmap(){

        String getCropedPath = "";

        BitmapResult bitmapResult = mPreview.getCroppedBitmap();

        if (bitmapResult.getState() != CropState.FAILURE_GESTURE_IN_PROCESS) {

            Bitmap bitmap = bitmapResult.getBitmap();

            if (bitmap != null) {

                String filename = (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()) + mSelectedPath.substring(mSelectedPath.lastIndexOf("/")+1);
                getCropedPath = bitmapConvertToFile(bitmap, filename);

            }
        }

        return getCropedPath;
    }

    public class DateSorter implements Comparator<Photo> {

        @Override
        public int compare(Photo photo, Photo t1) {
            int returnVal = 0;

            if(photo.getDate() < t1.getDate()){
                returnVal =  1;
            }else if(photo.getDate() > t1.getDate()){
                returnVal =  -1;
            }else if(photo.getDate() == t1.getDate()){
                returnVal =  0;
            }
            return returnVal;
        }
    }

}