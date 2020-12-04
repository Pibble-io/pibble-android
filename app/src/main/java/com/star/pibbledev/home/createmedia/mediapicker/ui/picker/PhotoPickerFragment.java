package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperView;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.DirectoryModel;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.BaseFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.component.FragmentLifecycle;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.FileResultCallback;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.PhotoDirLoaderCallbacks;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.Photo;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;
import com.star.pibbledev.home.utility.FileManager;
import com.star.pibbledev.home.utility.videoview.VideoView;
import com.star.pibbledev.services.network.ScalingUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class PhotoPickerFragment extends BaseFragment implements PhotoGridAdapter.OnItemClickPhotoGridListener, FragmentLifecycle {

    public static PhotoPickerFragment newInstance() {

        Bundle args = new Bundle();

        PhotoPickerFragment fragment = new PhotoPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.mPreview)
    CropperView mPreview;

    @BindView(R.id.mVideoView)
    VideoView mVideoView;

    @BindView(R.id.img_scale)
    ImageView mimg_scale;

    @BindView(R.id.linear_scale)
    LinearLayout linear_scale;

    @BindView(R.id.mAppBarContainer)
    AppBarLayout mAppBarContainer;

    private PhotoGridAdapter photoGridAdapter;
    private ArrayList<String> selectedPaths = new ArrayList<>();
    private Photo mPhoto;

    private boolean isScaled = true;
    private String mCropedPath = "";
    private String mSelectedPath = "";
    private boolean isCropGestureStart = false;

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_mainfeed_photo_picker;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
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
                                        updateList(insertedVideo(dirs));
                                    }
                                }));
    }

    private void updateList(List<PhotoDirectory> dirs) {

        ArrayList<Photo> photos = new ArrayList<>();

        photos.addAll(dirs.get(Utility.g_photoDirectory).getPhotos());

        HashMap<Integer, Integer> mMap = new HashMap<>();

        if (Utility.mainFeedParams.directoryModels != null && Utility.mainFeedParams.directoryModels.size() > 0) {

            for (int i = 0; i < Utility.mainFeedParams.directoryModels.size(); i++) {

                DirectoryModel model = Utility.mainFeedParams.directoryModels.get(i);

                if (model.directoryIndex == Utility.g_photoDirectory) mMap.put(model.cellIndex, model.indexValue);
            }

        }

        Collections.sort(photos, new DateSorter());

        if (photos.size() > 0) {
            emptyView.setVisibility(View.GONE);
            mPhoto = photos.get(0);
            showTopImageView(mPhoto, false);

        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        if (photoGridAdapter != null) {
            photoGridAdapter.setData(photos);
            photoGridAdapter.notifyDataSetChanged();
        } else {
            photoGridAdapter = new PhotoGridAdapter(getActivity(), photos, selectedPaths, mMap);
            photoGridAdapter.setItemClickPhotoGridListener(this);
            recyclerView.setAdapter(photoGridAdapter);
        }
    }

    private List<PhotoDirectory> insertedVideo(List<PhotoDirectory> dirs) {

        ArrayList<PhotoDirectory> result = new ArrayList<>();

        PhotoDirectory directory = new PhotoDirectory();

        for (int k = 0; k < dirs.size(); k++) {

            result.add(dirs.get(k));

            if (k == 0) {

                for (int i = 0; i < dirs.get(k).getPhotos().size(); i++) {

                    Photo photo = dirs.get(k).getPhotos().get(i);

                    List<String> params = Arrays.asList(photo.getMimeType().split("/"));
                    if (params.size() > 0 && params.get(0).toLowerCase().equals("video")) {

                        directory.addPhoto(photo.getId(), photo.getName(), photo.getPath(), photo.getMimeType(), photo.getDuration(), photo.getDate());
                    }
                }

                if (directory.getPhotos().size() == 0) continue;

                directory.setCoverPath(directory.getPhotos().get(k).getPath());
                directory.setName(getString(R.string.video));

                result.add(directory);

            }
        }

        return result;
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

    private void showTopImageView(Photo photo, boolean isSelect) {

        mSelectedPath = photo.getPath();

        mAppBarContainer.setExpanded(true);

        List<String> params = Arrays.asList(photo.getMimeType().split("/"));

        if (params.get(0).toLowerCase().equals("video")) {

            mPreview.setVisibility(View.INVISIBLE);
            mVideoView.setVisibility(View.VISIBLE);

            mVideoView.setVideoPath(mSelectedPath);
            mVideoView.start();

        } else {

            mVideoView.setVisibility(View.INVISIBLE);
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            }

            mPreview.setVisibility(View.VISIBLE);

            int size = FileManager.getSizeBitmap(mSelectedPath);
            int width = (int)(size/100000);
            int height = (int)(size%100000);
            Bitmap bitmap_checked = FileManager.getResizeImage(getContext(), width, height, ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);

//            Bitmap bitmap_checked = FileManager.getRotateImage(getContext(),ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);
            if (bitmap_checked != null) mPreview.setImageBitmap(bitmap_checked);
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

            isScaled = false;
        }

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

        if (PostMediaHelper.sCachePost.size() > 0) {

            ((PhotoPickerActivity)getActivity()).changeButtonStatus(true);

            String lastPath = PostMediaHelper.sCachePost.get(PostMediaHelper.sCachePost.size() - 1);

            mSelectedPath = lastPath;

            String str_type = mSelectedPath.substring(mSelectedPath.lastIndexOf("."));

            if (str_type.equals(".mp4") || str_type.equals(".3gp") || str_type.equals(".mpeg") || str_type.equals(".avi")){

                mPreview.setVisibility(View.INVISIBLE);
                mVideoView.setVisibility(View.VISIBLE);

                mVideoView.setVideoPath(mSelectedPath);
                mVideoView.start();

            } else {

                mVideoView.setVisibility(View.INVISIBLE);
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }

                mPreview.setVisibility(View.VISIBLE);

                int size = FileManager.getSizeBitmap(mSelectedPath);
                int width = (int)(size/100000);
                int height = (int)(size%100000);

                Bitmap bitmap_checked = FileManager.getResizeImage(getContext(), width, height, ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);

                if (bitmap_checked != null) mPreview.setImageBitmap(bitmap_checked);
                mPreview.setAlpha(0);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.fitToCenter();
                        mPreview.setAlpha(1);
                    }
                }, 20);
            }

        } else {
            ((PhotoPickerActivity)getActivity()).changeButtonStatus(false);
            showTopImageView(mPhoto, false);
        }
    }

    @Override
    public void onStopFragment() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    public void onSelected(Photo photo, int position) {

        PostMediaHelper.sCachePost.add(photo.getPath());
        Utility.mainFeedParams.originalPaths.add(photo.getPath());

        DirectoryModel model = new DirectoryModel();
        model.directoryIndex = Utility.g_photoDirectory;
        model.cellIndex = position;
        model.indexValue = PostMediaHelper.sCachePost.size();

        Utility.mainFeedParams.directoryModels.add(model);

        showTopImageView(photo, true);

        ((PhotoPickerActivity)getActivity()).changeButtonStatus(true);
    }

    @Override
    public void onUnSelected(Photo photo, int position, int number) {

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

        Utility.mainFeedParams.originalPaths.remove(photo.getPath());

        int unselectedIndex = -1;
        for (int i = 0; i < Utility.mainFeedParams.directoryModels.size(); i++) {

            DirectoryModel model = Utility.mainFeedParams.directoryModels.get(i);

            if (model.indexValue > number) {
                model.indexValue = model.indexValue - 1;
                Utility.mainFeedParams.directoryModels.set(i, model);
            }

            if (model.directoryIndex == Utility.g_photoDirectory && model.cellIndex == position) unselectedIndex = i;

        }
        Utility.mainFeedParams.directoryModels.remove(unselectedIndex);

        if (PostMediaHelper.sCachePost.size() > 0) {

            ((PhotoPickerActivity)getActivity()).changeButtonStatus(true);

            String lastPath = PostMediaHelper.sCachePost.get(PostMediaHelper.sCachePost.size() - 1);

            mSelectedPath = lastPath;

            String str_type = mSelectedPath.substring(mSelectedPath.lastIndexOf("."));

            if (str_type.equals(".mp4") || str_type.equals(".3gp") || str_type.equals(".mpeg") || str_type.equals(".avi")){

                mPreview.setVisibility(View.INVISIBLE);
                mVideoView.setVisibility(View.VISIBLE);

                mVideoView.setVideoPath(mSelectedPath);
                mVideoView.start();

            } else {

                mVideoView.setVisibility(View.INVISIBLE);
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }

                mPreview.setVisibility(View.VISIBLE);

                int size = FileManager.getSizeBitmap(lastPath);
                int width = (int)(size/100000);
                int height = (int)(size%100000);

                Bitmap bitmap_checked = FileManager.getResizeImage(getContext(), width, height, ScalingUtilities.ScalingLogic.FIT, true, mSelectedPath);

                if (bitmap_checked != null) mPreview.setImageBitmap(bitmap_checked);
                mPreview.setAlpha(0);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.fitToCenter();
                        mPreview.setAlpha(1);
                    }
                }, 20);
            }

        } else {
            ((PhotoPickerActivity)getActivity()).changeButtonStatus(false);
            showTopImageView(mPhoto, false);
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

    private String getLatitude(String imagePath) {

        String latitude = "";
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        String exif="Exif: " + imagePath;

        try {

            ExifInterface exifInterface = new ExifInterface(imagePath);

            exif += "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            exif += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            exif += "\nGPS related:";

//            float[] LatLong = new float[2];
//            if(exifInterface.getLatLong(LatLong)){
//                exif += "\n latitude= " + LatLong[0];
//                exif += "\n longitude= " + LatLong[1];
//            }else{
//                exif += "Exif tags are not available!";
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return exif;
    }

    public class DateSorter implements Comparator<Photo>{

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


