package com.star.pibbledev.profile.photopicker;

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

import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.home.funding.activity.CreateFundingThirdStepActivity;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.DirectoryGridAdaptar;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.FileResultCallback;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.loader.loadercallbacks.PhotoDirLoaderCallbacks;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;
import com.star.pibbledev.profile.photopicker.fragment.PrepareFragment;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.services.network.BackendAPI;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;
import com.star.pibbledev.profile.ProfileHomeFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends BaseActivity implements View.OnClickListener, RequestListener, DirectoryGridAdaptar.DirectoryClickListener {

    public static final String TAG = "PickerActivity";
    public static final String IMAGE_TYPE = "type";

    ImageView btn_cancel;
    LinearLayout linear_allphotos, btn_next;
    FrameLayout container, container_directory;
    TextView txt_directory, txt_done;
    RecyclerView recyclerView;

    private boolean isDirectories;
    private List<PhotoDirectory> list_directory;
    private DirectoryGridAdaptar directoryGridAdaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoeditor_picker);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_next = (LinearLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        linear_allphotos = (LinearLayout)findViewById(R.id.linear_allphotos);
        linear_allphotos.setOnClickListener(this);
        txt_directory = (TextView)findViewById(R.id.txt_directory);
        txt_done = (TextView)findViewById(R.id.txt_done);
        container = (FrameLayout)findViewById(R.id.container);
        container_directory = (FrameLayout)findViewById(R.id.container_directory);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        btn_next.setEnabled(false);
        txt_done.setTextColor(getResources().getColor(R.color.line_background_color));

        isDirectories = false;

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null && fm.findFragmentByTag(PrepareFragment.TAG) == null) {
            PrepareFragment fragment = PrepareFragment.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment, PrepareFragment.TAG);
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

    private void createUserPhoto() {

        ArrayList<String> paths = getItemsSelected();
        if (paths == null || paths.isEmpty()) {
            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.selected_photo_is_none), getString(R.string.ok));
            return;
        }

        String access_token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        String filePath = PostMediaHelper.concatenate(paths).get(0);

        if (getIntent().getStringExtra(IMAGE_TYPE).equals(ProfileHomeFragment.IMAGE_COVER)) {

            ServerRequest.getSharedServerRequest().updateUserImage(this, this, access_token, filePath, "wall_cover", BackendAPI.update_wall_cover);

        } else if (getIntent().getStringExtra(IMAGE_TYPE).equals(ProfileHomeFragment.IMAGE_USER)) {

            ServerRequest.getSharedServerRequest().updateUserImage(this, this, access_token, filePath, "avatar", BackendAPI.update_user_avatar);

        } else if (getIntent().getStringExtra(IMAGE_TYPE).equals(CreateFundingThirdStepActivity.IMAGE_TEAM)) {

            CreateFundingThirdStepActivity.str_teamloag = filePath;
            finish();
            overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);

        }

    }

    @Override
    public void onClick(View v) {

        if (v == btn_next) {

            if (Constants.isLifeToken(this)) {

                createUserPhoto();

            } else {
                Constants.requestRefreshToken(this, this);
            }

            showHUD();

        } else if (v == btn_cancel) {

            finish();
            overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);

        } else if (v == linear_allphotos) {

            if (isDirectories) {

                isDirectories = false;

                PrepareFragment fragment = PrepareFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.setCustomAnimations(R.anim.top_finish_out, R.anim.top_finish_in);
                ft.replace(R.id.container, fragment, PrepareFragment.TAG);
                ft.commit();

                container.setVisibility(View.VISIBLE);
                container_directory.setVisibility(View.INVISIBLE);

                btn_cancel.setVisibility(View.VISIBLE);
                btn_next.setVisibility(View.VISIBLE);

            } else {

                isDirectories = true;

                container.setVisibility(View.INVISIBLE);
                container_directory.setVisibility(View.VISIBLE);

                btn_cancel.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);

                getDataFromMedia();

            }

        }
    }

    private void getDataFromMedia() {

        this.getSupportLoaderManager()
                .initLoader(0, new Bundle(),
                        new PhotoDirLoaderCallbacks(this,
                                new FileResultCallback<PhotoDirectory>() {
                                    @Override
                                    public void onResultCallback(List<PhotoDirectory> dirs) {

                                        list_directory = dirs;

                                        if (directoryGridAdaptar == null) {

                                            directoryGridAdaptar = new DirectoryGridAdaptar(PickerActivity.this, list_directory);
                                            directoryGridAdaptar.setClickListener(PickerActivity.this);
                                            recyclerView.setAdapter(directoryGridAdaptar);

                                        } else {
                                            directoryGridAdaptar.notifyDataSetChanged();
                                        }

                                    }
                                }));
    }

    private ArrayList<String> getItemsSelected() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(PrepareFragment.TAG);
        if (fragment instanceof PrepareFragment) {
            return ((PrepareFragment) fragment).getItemsSelected();
        }

        return new ArrayList<>();
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            hideHUD();

            JSONObject obj_path = objResult.optJSONObject("media");
            String path = obj_path.optString("url");

            if (getIntent().getStringExtra(IMAGE_TYPE).equals(ProfileHomeFragment.IMAGE_COVER)) {

                Utility.getSavedPref(this).saveString("wall_cover", path);

            } else if (getIntent().getStringExtra(IMAGE_TYPE).equals(ProfileHomeFragment.IMAGE_USER)) {

                Utility.getSavedPref(this).saveString("avatar", path);
            }

            Utility.g_userInfoChanged = true;

            finish();
            overridePendingTransition(R.anim.top_finish_out, R.anim.top_finish_in);

        } else {
            Constants.saveRefreshToken(this, objResult);
            createUserPhoto();
        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        hideHUD();

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }

    @Override
    public void onItemClick(View view, int position) {

        Utility.g_photoDirectory = position;

        isDirectories = false;

        PrepareFragment fragment = PrepareFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.setCustomAnimations(R.anim.top_finish_out, R.anim.top_finish_in);
        ft.replace(R.id.container, fragment, PrepareFragment.TAG);
        ft.commit();

        PhotoDirectory selectedDir = list_directory.get(position);
        txt_directory.setText(selectedDir.getName());

        container.setVisibility(View.VISIBLE);
        container_directory.setVisibility(View.INVISIBLE);

        btn_cancel.setVisibility(View.VISIBLE);
        btn_next.setVisibility(View.VISIBLE);

    }
}
