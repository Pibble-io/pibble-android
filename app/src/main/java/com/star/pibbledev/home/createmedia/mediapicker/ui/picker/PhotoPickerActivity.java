package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.Photo;
import com.star.pibbledev.home.funding.activity.JoinFundingTeamActivity;
import com.star.pibbledev.home.createmedia.postmedia.MediaPostFirstActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.FileResultCallback;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.PhotoDirLoaderCallbacks;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoPickerActivity extends BaseActivity implements View.OnClickListener, DirectoryGridAdaptar.DirectoryClickListener {

    @SuppressLint("StaticFieldLeak")
    public static PhotoPickerActivity photoActivity;

    ImageView img_back;
    LinearLayout btn_next;
    LinearLayout linear_allphotos;
    TextView txt_directory, txt_done;
    FrameLayout container, container_directory;

    RecyclerView recyclerView;

    private boolean isDirectories;

    private DirectoryGridAdaptar directoryGridAdaptar;
    private List<PhotoDirectory> list_directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfeed_photopicker);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        photoActivity = this;

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        btn_next = (LinearLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        txt_done = (TextView)findViewById(R.id.txt_done);
        txt_directory = (TextView)findViewById(R.id.txt_directory);

        container = (FrameLayout)findViewById(R.id.container);

        container_directory = (FrameLayout)findViewById(R.id.container_directory);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        linear_allphotos = (LinearLayout)findViewById(R.id.linear_allphotos);
        linear_allphotos.setOnClickListener(this);

        isDirectories = false;

        Utility.g_photoDirectory = 0;

        btn_next.setEnabled(false);
        txt_done.setTextColor(getResources().getColor(R.color.line_background_color));

        if (Utility.mainFeedParams.mediaPaths == null) {
            Utility.mainFeedParams.mediaPaths = new ArrayList<>();
            Utility.mainFeedParams.originalPaths = new ArrayList<>();
            Utility.mainFeedParams.directoryModels = new ArrayList<>();
        } else {
            Utility.mainFeedParams.mediaPaths.clear();
            Utility.mainFeedParams.originalPaths.clear();
            Utility.mainFeedParams.directoryModels.clear();
        }

        if (PostMediaHelper.sCachePost.size() > 0) PostMediaHelper.sCachePost.clear();

        initDirectoryRecycle();

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null && fm.findFragmentByTag(PrepareMediaFragment.TAG) == null) {
            PrepareMediaFragment fragment = PrepareMediaFragment.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment, PrepareMediaFragment.TAG);
            ft.commit();
        }
    }

    public void changeButtonStatus(boolean isHas) {
        if (isHas) {
            btn_next.setEnabled(true);
            txt_done.setTextColor(getResources().getColor(R.color.black));
        } else {
            btn_next.setEnabled(false);
            txt_done.setTextColor(getResources().getColor(R.color.line_background_color));
        }
    }

//    public void finishActivity() {
//        this.finish();
//    }

    @Override
    public void onClick(View v) {

        if (v == btn_next) {

            ArrayList<String> paths = getItemsSelected();
            if (paths == null || paths.isEmpty()) {
                Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utility.g_director.equals(Constants.TEAM)) {

                Intent intent = new Intent(this, JoinFundingTeamActivity.class);
                intent.putStringArrayListExtra("paths", paths);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            } else {

                Intent intent = new Intent(this, MediaPostFirstActivity.class);
                intent.putStringArrayListExtra("paths", paths);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            }

        } else if (v == img_back) {

            Utility.mainFeedParams = null;

            finish();
            overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);

        } else if (v == linear_allphotos) {

            if (isDirectories) {

                isDirectories = false;

                PrepareMediaFragment fragment = PrepareMediaFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.setCustomAnimations(R.anim.top_finish_out, R.anim.top_finish_in);
                ft.replace(R.id.container, fragment, PrepareMediaFragment.TAG);
                ft.commit();

                container.setVisibility(View.VISIBLE);
                container_directory.setVisibility(View.INVISIBLE);

                img_back.setVisibility(View.VISIBLE);
                btn_next.setVisibility(View.VISIBLE);

            } else {

                isDirectories = true;

                container.setVisibility(View.INVISIBLE);
                container_directory.setVisibility(View.VISIBLE);

                img_back.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);

                getDataFromMedia();

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDirectoryRecycle() {

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    private void getDataFromMedia() {

        this.getSupportLoaderManager()
                .initLoader(0, new Bundle(),
                        new PhotoDirLoaderCallbacks(this,
                                new FileResultCallback<PhotoDirectory>() {
                                    @Override
                                    public void onResultCallback(List<PhotoDirectory> dirs) {

                                        list_directory = insertedVideo(dirs);

                                        if (directoryGridAdaptar == null) {

                                            directoryGridAdaptar = new DirectoryGridAdaptar(PhotoPickerActivity.this, list_directory);
                                            directoryGridAdaptar.setClickListener(PhotoPickerActivity.this);
                                            recyclerView.setAdapter(directoryGridAdaptar);

                                        } else {
                                            directoryGridAdaptar.notifyDataSetChanged();
                                        }

                                    }
                                }));
    }

    private ArrayList<String> getItemsSelected() {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(PrepareMediaFragment.TAG);
        if (fragment instanceof PrepareMediaFragment) {
            return ((PrepareMediaFragment) fragment).getItemsSelected();
        }

        return new ArrayList<>();
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
                        //storage/emulated/0/DCIM/Camera/20191210_015950.jpg
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

    @Override
    public void onItemClick(View view, int position) {

        Utility.g_photoDirectory = position;

        isDirectories = false;

        PrepareMediaFragment fragment = PrepareMediaFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.setCustomAnimations(R.anim.top_finish_out, R.anim.top_finish_in);
        ft.replace(R.id.container, fragment, PrepareMediaFragment.TAG);
        ft.commit();

        PhotoDirectory selectedDir = list_directory.get(position);
        txt_directory.setText(selectedDir.getName());

        container.setVisibility(View.VISIBLE);
        container_directory.setVisibility(View.INVISIBLE);

        img_back.setVisibility(View.VISIBLE);
        btn_next.setVisibility(View.VISIBLE);

    }
}
