package com.star.pibbledev.home.utility.videoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

import static android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;

@SuppressLint("ViewConstructor")
public class CustomVideoView extends RelativeLayout {

    private Context mContext;

    private VideoView videoView;
    private ImageView img_view, img_sound, img_play;
    private TextView txt_time, txt_sound;
    private LinearLayout linear_sound;
    private boolean isHasAudio;

    private Runnable timerRunnable, soundRunnable;
    private Handler timerHandler, soundHandler;

    int count;

    @SuppressLint("ClickableViewAccessibility")
    public CustomVideoView(Context context, String url, final String thum, ImageLoader imageLoader) {
        super(context);

        mContext = context;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = minflater.inflate(R.layout.relativelayout_videoview, this, true);

        boolean isSound = true;

        txt_time = (TextView)view.findViewById(R.id.txt_time);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        img_view = (ImageView)view.findViewById(R.id.img_view);
        img_play = (ImageView)view.findViewById(R.id.img_play);
        txt_sound = (TextView)view.findViewById(R.id.txt_sound);
        linear_sound = (LinearLayout)view.findViewById(R.id.linear_sound);
        img_sound = (ImageView) view.findViewById(R.id.img_sound);

        txt_time.setVisibility(GONE);
        img_play.setVisibility(VISIBLE);
        linear_sound.setVisibility(GONE);

        videoView.setVideoPath(url);
        imageLoader.displayImage(thum, img_view);

        int userlevel = Integer.parseInt(Utility.getReadPref(context).getStringValue(Constants.LEVEL));
        if (userlevel > 6) txt_sound.setVisibility(GONE);
        else txt_sound.setVisibility(VISIBLE);

        img_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });

        soundHandler = new Handler();
        soundRunnable = new Runnable() {
            @Override
            public void run() {
                linear_sound.setVisibility(GONE);
            }
        };

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                MediaPlayer.TrackInfo[] infos = mediaPlayer.getTrackInfo();

                for (MediaPlayer.TrackInfo info : infos) {

                    if (info.getTrackType() == MEDIA_TRACK_TYPE_AUDIO) {
                        isHasAudio = true;
                    }
                }

                if (isHasAudio) {
                    img_sound.setImageDrawable(mContext.getDrawable(R.drawable.icon_sound_off));
                    txt_sound.setText(mContext.getString(R.string.tap_to_turn_on_sound));
                } else {
                    img_sound.setImageDrawable(mContext.getDrawable(R.drawable.icon_sound_not));
                    txt_sound.setText(getContext().getString(R.string.no_sound_video));
                }

                videoView.start();
                videoView.setMute(true);
                linear_sound.setVisibility(VISIBLE);

                timerHandler = new Handler();
                timerRunnable = new Runnable() {

                    @Override
                    public void run() {

                        timerHandler.postDelayed(this, 1000);

                        if (img_view.getVisibility() == VISIBLE) img_view.setVisibility(INVISIBLE);
                        if (img_play.getVisibility() == VISIBLE) img_play.setVisibility(GONE);
                        if (txt_time.getVisibility() == GONE) txt_time.setVisibility(VISIBLE);

                        if (count < 4) {

                            long duration = videoView.getDuration() - 1000 * count;
                            txt_time.setText(Utility.getTiemFromMS(duration));

                            count++;

                        } else {
                            txt_time.setVisibility(GONE);
                            linear_sound.setVisibility(GONE);
                            timerHandler.removeCallbacks(timerRunnable);
                        }

                    }
                };

                timerHandler.postDelayed(timerRunnable, 800);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    @Override
    public void onCancelPendingInputEvents() {
        super.onCancelPendingInputEvents();
        videoView.pause();
    }

    public VideoView getVideoView() {
        return videoView;
    }

    public void setMute(boolean isMute) {

        if (!isHasAudio) return;

        videoView.setMute(isMute);

        if (linear_sound.getVisibility() == GONE) linear_sound.setVisibility(VISIBLE);

        if (isMute) {

            img_sound.setImageDrawable(mContext.getDrawable(R.drawable.icon_sound_off));
            txt_sound.setText(mContext.getString(R.string.tap_to_turn_on_sound));

        } else {

            img_sound.setImageDrawable(mContext.getDrawable(R.drawable.icon_sound_on));
            txt_sound.setText(mContext.getString(R.string.tap_to_turn_off_sound));

        }

        soundHandler.removeCallbacks(soundRunnable);
        soundHandler.postDelayed(soundRunnable, 3000);
    }

    public ImageView getImageView() {
        return img_view;
    }

    public boolean isLoadedVideo() {

        return count != 0;

    }
}
