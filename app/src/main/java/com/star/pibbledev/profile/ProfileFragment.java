package com.star.pibbledev.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.profile.activity.setting.SettingHomeActicity;
import com.star.pibbledev.services.global.Utility;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ProfileFragment";

    View view;
    Context mContext;
    TextView txt_title;
    ImageButton img_more;

    private AlertView mAlertView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = (View) inflater.inflate(R.layout.fragment_profile_firstview, null);
        mContext = getActivity();

        txt_title = (TextView)view.findViewById(R.id.txt_title);
        img_more = (ImageButton)view.findViewById(R.id.img_more);
        img_more.setOnClickListener(this);

        boolean shouldCreateChild = getArguments().getBoolean("shouldYouCreateAChildFragment");

        if (shouldCreateChild) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            fm.beginTransaction();
            Fragment fragTwo = new ProfileHomeFragment();
            Bundle arguments = new Bundle();
            arguments.putBoolean("shouldYouCreateAChildFragment", false);
            fragTwo.setArguments(arguments);
            ft.add(R.id.frame_home_profile, fragTwo);
            ft.commit();

        }

        return view;
    }

    public void onHideActionBar() {

        if (mAlertView != null && mAlertView.isShowing()) mAlertView.dismiss();

    }

    public void changeMoreButton(boolean isHide) {
        if (isHide) img_more.setVisibility(View.INVISIBLE);
        else img_more.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        String username = Utility.getReadPref(mContext).getStringValue("username");
        txt_title.setText(Utility.truncate(username, 15));
    }

    @Override
    public void onClick(View view) {

        if (view == img_more) {

            Utility.g_isShowingTabbar = true;

            mAlertView = new AlertView(null, null, getString(R.string.cancel), new String[]{getString(R.string.message_room), getString(R.string.settings)},
                    new String[]{},
                    mContext, AlertView.Style.ActionSheet, new OnItemClickListener(){
                public void onItemClick(Object o,int position){

                    Utility.g_isShowingTabbar = false;

                    switch (position) {
                        case 0:

                            if ((DashboardActivity)getActivity() != null) ((DashboardActivity)getActivity()).gotoMessage();

                            break;

                        case 1:

                            Intent intent = new Intent(mContext, SettingHomeActicity.class);
                            startActivity(intent);
                            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

                            break;
                    }

                }

            });

            mAlertView.show();
        }

    }
}
