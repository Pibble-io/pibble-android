package com.star.pibbledev.tutorial;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.auth.SignupActivity;
import com.star.pibbledev.auth.SocialLoginActivity;

import java.util.Objects;

public class TutorialFragmentViewPager extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_item, container, false);

        ImageView img_bg = (ImageView)view.findViewById(R.id.img_bg);
        TextView txt_title = (TextView)view.findViewById(R.id.txt_title);
        TextView txt_detail = (TextView)view.findViewById(R.id.txt_detail);
        LinearLayout linear_action = (LinearLayout)view.findViewById(R.id.linear_action);
        LinearLayout linear_started = (LinearLayout)view.findViewById(R.id.linear_started);

        assert getArguments() != null;
        txt_title.setText(getArguments().getInt("title"));
        txt_detail.setText(getArguments().getInt("detail"));
        img_bg.setBackgroundResource(getArguments().getInt("img"));

        if (getArguments().getBoolean("action")) {
            linear_action.setVisibility(View.VISIBLE);
        } else {
            linear_action.setVisibility(View.INVISIBLE);
        }

        linear_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent = new Intent(getActivity(), SocialLoginActivity.class);
                startActivity(mainIntent);
                Objects.requireNonNull(getActivity()).finishAffinity();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return view;
    }

    public static TutorialFragmentViewPager newInstance(int title, int detail, int image, boolean action) {

        TutorialFragmentViewPager f = new TutorialFragmentViewPager();
        Bundle b = new Bundle();
        b.putInt("title", title);
        b.putInt("detail", detail);
        b.putInt("img", image);
        b.putBoolean("action", action);

        f.setArguments(b);

        return f;
    }
}
