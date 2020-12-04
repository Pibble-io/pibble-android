package com.star.pibbledev;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Objects;

public class BaseFragment extends Fragment {

    private KProgressHUD kProgressHUD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setkProgressHUD();
    }

    protected void setkProgressHUD() {
        kProgressHUD = KProgressHUD.create(Objects.requireNonNull(getActivity()))
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setMaxProgress(100)
                .setDimAmount(0.2f)
                .setBackgroundColor(Color.TRANSPARENT);

    }

    public void showHUD() {
        kProgressHUD.show();
    }

    public void hideHUD() {
        if (kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }
}
