package com.star.pibbledev;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class PibbleApplication extends Application implements Application.ActivityLifecycleCallbacks{

    public static PibbleApplication instance;

    private static Date lastActiveAt = new Date();
    private static boolean activityVisible;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        lastActiveAt.setTime(0);

        registerActivityLifecycleCallbacks(this);

    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static PibbleApplication getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!activityVisible && (new Date().getTime() - lastActiveAt.getTime() > 4000)) {
            Utility.g_isResumedAcivity = true;
        }

        activityVisible = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

        lastActiveAt = new Date();
        activityVisible = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
