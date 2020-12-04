package com.star.pibbledev.home.createmedia.mediapicker.ui.base;

import androidx.annotation.NonNull;

import com.star.pibbledev.home.createmedia.mediapicker.utils.PermissionUtil;

import java.util.Arrays;
import timber.log.Timber;

public abstract class RuntimePermissionFragment extends BaseFragment {

    protected abstract void permissionGranted(int permissionRequestCode, boolean isGranted);

    protected boolean isPermissionGrantedAndRequest(String[] permissions, int requestPermissionCode) {
        if (!PermissionUtil.checkAndroidMVersion()) {
            return true;
        }

        int[] selfPermission = PermissionUtil.getSelfPermission(getContext(), permissions);

        if (PermissionUtil.verifyPermission(selfPermission)) {
            return true;
        } else {
            requestPermissions(permissions, requestPermissionCode);
        }

        return false;
    }

    protected boolean isPermissionGrantedAndRequest(String permissions, int requestPermissionCode) {
        return isPermissionGrantedAndRequest(new String[]{permissions}, requestPermissionCode);
    }

    protected boolean isPermissionGranted(String[] permissions) {
        if (!PermissionUtil.checkAndroidMVersion()) {
            return true;
        }

        int[] selfPermission = PermissionUtil.getSelfPermission(getContext(), permissions);

        return PermissionUtil.verifyPermission(selfPermission);
    }

    protected boolean isPermissionGranted(String permissions) {
        return isPermissionGranted(new String[]{permissions});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Timber.d("Permissions result: requestCode [%s] permissions [%s] results[%s]", requestCode, Arrays.toString(permissions), Arrays.toString(grantResults));

        if (isAdded()) {
            permissionGranted(requestCode, PermissionUtil.verifyPermission(grantResults));
        }
    }

    public interface PERMISSION_CODE {
        int DENY_PERMISSION = -1;
        int RECEIVE_SMS = 10;
        int READ_SMS = 10;
        int READ_PHONE_STATE = 11;
        int PERMISSION_STORAGE = 12;
        int WRITE_EXTERNAL_STORAGE = 13;
        int READ_EXTERNAL_STORAGE = 14;
        int CAMERA = 15;
        int RECORD_AUDIO = 16;
        int NFC = 17;
        int ACCESS_FINE_LOCATION = 18;
        int ACCESS_COARSE_LOCATION = 19;
    }
}
