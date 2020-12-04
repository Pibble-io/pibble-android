package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import androidx.loader.content.CursorLoader;

import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.PhotoPickerActivity;
import com.star.pibbledev.profile.photopicker.PickerActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

public class PhotoDirectoryLoader extends CursorLoader {

    String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
//            Media.LATITUDE,
//            Media.LONGITUDE,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
    };

    // Return only video and image metadata.

    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

    String selection_avatar = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

    Uri queryUri = MediaStore.Files.getContentUri("external");

    public PhotoDirectoryLoader(Context context, boolean showGif) {
        super(context);

        setProjection(projection);
        setUri(queryUri);
        setSortOrder(MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        Activity activity = (Activity)context;

        if (activity instanceof PhotoPickerActivity) {

            if (Utility.g_director.equals(Constants.COMMERCE)) {
                setSelection(selection_avatar);
            } else {
                setSelection(selection);
            }

        } else if (activity instanceof PickerActivity) {
            setSelection(selection_avatar);
        }

        setSelectionArgs(null);
    }

}

