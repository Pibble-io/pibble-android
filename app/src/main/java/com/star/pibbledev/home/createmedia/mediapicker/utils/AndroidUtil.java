package com.star.pibbledev.home.createmedia.mediapicker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.View;

public class AndroidUtil {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void runOnUiThreadDelayed(Runnable runnable, int delay) {
        sHandler.postDelayed(runnable, delay);
    }

    @Nullable
    public static Drawable tintDrawable(Context context, @DrawableRes int drawableRes, @ColorRes int color) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) {
            return null;
        }
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color));
        return wrappedDrawable;
    }

    public static float px(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static void flashView(final View view) {
        final int duration = 500;
        view.setVisibility(View.VISIBLE);
        view.setClickable(true);
        view.setAlpha(0);
        view.animate()
                .setDuration(duration)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.animate()
                                .alpha(0)
                                .setStartDelay(200)
                                .setDuration(duration)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        view.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                });
    }
}
