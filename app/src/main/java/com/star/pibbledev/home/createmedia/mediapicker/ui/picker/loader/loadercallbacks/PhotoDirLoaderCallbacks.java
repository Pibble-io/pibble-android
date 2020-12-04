package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.PhotoDirectoryLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static int INDEX_ALL_PHOTOS = 0;
    private WeakReference<Context> context;
    private FileResultCallback<PhotoDirectory> resultCallback;

    public PhotoDirLoaderCallbacks(Context context, FileResultCallback<PhotoDirectory> resultCallback) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PhotoDirectoryLoader(context.get(), true);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data == null) {
            return;
        }
        List<PhotoDirectory> directories = new ArrayList<>();
        PhotoDirectory photoDirectoryAll = new PhotoDirectory();
        photoDirectoryAll.setName(context.get().getString(R.string.all_images));
        photoDirectoryAll.setId("ALL");

        if (!data.moveToFirst()) return;

        data.moveToPrevious();

        while (data.moveToNext()) {

            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            String fileName = data.getString(data.getColumnIndexOrThrow(TITLE));
            long date = data.getLong(data.getColumnIndexOrThrow(DATE_ADDED));
            long duration = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));

            String mediaType = data.getString(data.getColumnIndexOrThrow(MIME_TYPE));

            PhotoDirectory photoDirectory = new PhotoDirectory();
            photoDirectory.setId(bucketId);
            photoDirectory.setName(name);

            if (!directories.contains(photoDirectory)) {
                photoDirectory.setCoverPath(path);
                photoDirectory.addPhoto(imageId, fileName, path, mediaType, duration, date);
                photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                directories.add(photoDirectory);
            } else {
                directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, fileName, path, mediaType, duration, date);
            }

            photoDirectoryAll.addPhoto(imageId, fileName, path, mediaType, duration, date);
        }

        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }

        directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);
        if (resultCallback != null) {
            resultCallback.onResultCallback(directories);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}