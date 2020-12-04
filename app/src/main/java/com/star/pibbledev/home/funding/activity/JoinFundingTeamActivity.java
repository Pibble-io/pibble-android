package com.star.pibbledev.home.funding.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.PhotoPickerActivity;
import com.star.pibbledev.home.funding.JoinFundingTeamFragment;
import com.star.pibbledev.home.utility.FileManager;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.BackendAPI;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinFundingTeamActivity extends BaseActivity implements View.OnClickListener, RequestListener {

    public static JoinFundingTeamActivity activity;

    public static final String REQUEST_POST_UUID = "post_uuid";
    public static final String REQUEST_MEDIA_UUID = "media_uuid";
    public static final String REQUEST_CREATE_POST = "create_post";
    public static final String REQUEST_MEDIA_UPLOADING = "media_uploading";
    private static final String REQUEST_REFRESHTOKEN = "refresh_token";

    ImageButton btn_back;
    LinearLayout btn_next;
    TextView txt_title, txt_next;

    private FFmpeg ffmpeg;
    private String REQUEST_TYPE;

    int cnt_mediaUploading;

    long videoLengthInSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_join_team);

        activity = this;

        setLightStatusBar();

        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_next = (TextView)findViewById(R.id.txt_next);

        btn_back = (ImageButton) findViewById(R.id.img_back);
        btn_back.setOnClickListener(this);

        btn_next = (LinearLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        REQUEST_TYPE = "";

        if (getIntent().getStringArrayListExtra("paths") != null && getIntent().getStringArrayListExtra("paths").size() > 0) {

            Utility.mainFeedParams.mediaPaths.clear();
            Utility.mainFeedParams.mediaPaths.addAll(getIntent().getStringArrayListExtra("paths"));
        }

        Utility.mainFeedParams.caption = "";

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null && fm.findFragmentByTag(JoinFundingTeamFragment.TAG) == null) {
            JoinFundingTeamFragment fragment = JoinFundingTeamFragment.newInstance(getIntent().getExtras());
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment, JoinFundingTeamFragment.TAG);
            ft.commit();
        }

        txt_next.setText(getString(R.string.post));

        if (Utility.mainFeedParams.postsModel.type.equals(Constants.FUNDING_CHARITY)) txt_title.setText(getString(R.string.post_charity));
        else txt_title.setText(getString(R.string.post_funding));

        Utility.mainFeedParams.fundingModel.funding_as = Constants.TEAM;

        loadFFMpegBinary();
    }

    @Override
    public void onClick(View v) {

        if (v == btn_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

        } else if (v == btn_next) {

            if (Utility.mainFeedParams.mediaPaths.size() > 0) {

                REQUEST_TYPE = REQUEST_REFRESHTOKEN;
                Constants.requestRefreshToken(this, this);
            }
        }
    }

    private void createPosting() {

        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

        REQUEST_TYPE = REQUEST_CREATE_POST;
        ServerRequest.getSharedServerRequest().creatPosting(this, this, Utility.mainFeedParams.postsModel.type, Utility.mainFeedParams, access_token);

    }

    //--------video resolution---------

    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.e("ffmpeg", "Device Not Supported failed");
                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg", "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, "Device Not Supported", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("ffmpeg", "EXception no controlada : " + e);
        }
    }

    private void executeCompressCommand(String videoPath, String media_uuid) {

        @SuppressLint("SimpleDateFormat") String newPath = getCacheDir().getPath() + "/" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()) + ".mp4";

        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(videoPath);
            mp.prepare();
            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                    int size = FileManager.getTargetVideoSize(width, height);
                    int changed_width = (int)(size/100000);
                    int changed_height = (int)(size%100000);

                    videoLengthInSec = TimeUnit.MILLISECONDS.toSeconds(mp.getDuration());

                    String newSize = String.format("scale=%d:-1,crop=%d:%d", changed_width, changed_width, changed_height);

                    if (mp.getDuration() > 60000) {
                        String[] complexCommand = {"-y", "-i", videoPath, "-vf", newSize, "-c:v", "libx264", "-crf", "18", "-preset", "ultrafast", "-c:a", "copy", "-ss", "00:00:00", "-t", "00:00:59", newPath};
                        execFFmpegBinary(complexCommand, newPath, media_uuid);
                    } else {
                        String[] complexCommand = {"-y", "-i", videoPath, "-vf", newSize, "-c:v", "libx264", "-crf", "18", "-preset", "ultrafast", "-c:a", "copy", newPath};
                        execFFmpegBinary(complexCommand, newPath, media_uuid);
                    }

                }
            });

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Pattern pattern = Pattern.compile("time=([\\d\\w:]+)");
    private void getProgress(String message) {

        if (message.contains("speed")) {
            Matcher matcher = pattern.matcher(message);
            matcher.find();
            String tempTime = String.valueOf(matcher.group(1));
            String[] arrayTime = tempTime.split(":");
            long currentTime =
                    TimeUnit.HOURS.toSeconds(Long.parseLong(arrayTime[0]))
                            + TimeUnit.MINUTES.toSeconds(Long.parseLong(arrayTime[1]))
                            + Long.parseLong(arrayTime[2]);

            long percent = 100 * currentTime/videoLengthInSec;

            Utility.g_progressValue = (int)percent;

        }

    }

    private void execFFmpegBinary(final String[] command, final String filePath, final String media_uuid) {

        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {

                }
                @Override
                public void onSuccess(String s) {

                }
                @Override
                public void onProgress(String s) {
                    getProgress(s);
                }
                @Override
                public void onStart() {
//                    Log.d("fff", "Started command : ffmpeg " + command);
                }
                @Override
                public void onFinish() {

                    String access_token = Utility.getReadPref(JoinFundingTeamActivity.this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                    ServerRequest.getSharedServerRequest().uploadMediafile(JoinFundingTeamActivity.this, JoinFundingTeamActivity.this, access_token, filePath, Utility.mainFeedParams.postUuid, media_uuid);
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    @MainThread
    private void uploadMedia(int index) {

        Utility.g_progressValue = 0;

        String access_token = Utility.getReadPref(JoinFundingTeamActivity.this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

        String path = Utility.mainFeedParams.mediaPaths.get(index);
        String str_type = path.substring(path.lastIndexOf("."));

        if (str_type.equals(".mp4") || str_type.equals(".3gp") || str_type.equals(".mpeg") || str_type.equals(".avi")) {
            executeCompressCommand(path, Utility.mainFeedParams.ary_mediaTokens.get(index));
        } else {
            Utility.g_progressValue = 100;
            ServerRequest.getSharedServerRequest().uploadMediafile(this, this, access_token, path, Utility.mainFeedParams.postUuid, Utility.mainFeedParams.ary_mediaTokens.get(index));
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        switch (REQUEST_TYPE) {

            case REQUEST_REFRESHTOKEN: {

                Constants.saveRefreshToken(this, objResult);

                REQUEST_TYPE = REQUEST_POST_UUID;

                Utility.g_isPostedMedia = true;

                cnt_mediaUploading = 0;

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
                ServerRequest.getSharedServerRequest().get_uuid(this, this, BackendAPI.get_post_UUID, access_token);

                if (PhotoPickerActivity.photoActivity != null)
                    PhotoPickerActivity.photoActivity.finish();
                if (DashboardActivity.firstActivity != null)
                    DashboardActivity.firstActivity.finish();

                Utility.g_indexDashboardTab = 1;

                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);

                break;
            }
            case REQUEST_POST_UUID: {

                Utility.mainFeedParams.postUuid = objResult.optString("uuid");

                Utility.mainFeedParams.ary_mediaTokens = new ArrayList<>();

                REQUEST_TYPE = REQUEST_MEDIA_UUID;

                String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);

                for (int i = 0; i < Utility.mainFeedParams.mediaPaths.size(); i++) {

                    ServerRequest.getSharedServerRequest().get_uuid(this, this, BackendAPI.get_media_UUID, access_token);

                }

                break;
            }
            case REQUEST_MEDIA_UUID:

                String media_uuid = objResult.optString("uuid");
                Utility.mainFeedParams.ary_mediaTokens.add(media_uuid);

                if (Utility.mainFeedParams.ary_mediaTokens.size() == Utility.mainFeedParams.mediaPaths.size()) {

                    REQUEST_TYPE = REQUEST_MEDIA_UPLOADING;

                    cnt_mediaUploading = 0;
                    uploadMedia(cnt_mediaUploading);

                }

                break;
            case REQUEST_MEDIA_UPLOADING:

                cnt_mediaUploading++;

                if (cnt_mediaUploading == Utility.mainFeedParams.mediaPaths.size()) {

                    createPosting();

                    cnt_mediaUploading = 0;

                } else {
                    uploadMedia(cnt_mediaUploading);
                }

                break;

            case REQUEST_CREATE_POST:

                Utility.g_progressValue = 130;

                break;
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();
        Utility.g_progressValue = 404;
        Utility.g_postError = strError;
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {
        Log.e("xxx", objResult.toString());
    }
}

