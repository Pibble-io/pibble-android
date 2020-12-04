package com.star.pibbledev.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.star.pibbledev.R;
import com.star.pibbledev.gifticon.GiftHomeActivity;
import com.star.pibbledev.home.MainFeedParams;
import com.star.pibbledev.home.album.AlbumActivity;
import com.star.pibbledev.home.myfeed.MyFeedsActivity;
import com.star.pibbledev.home.userinfo.UserProfileActivity;
import com.star.pibbledev.profile.activity.UsersActivity;
import com.star.pibbledev.profile.activity.setting.SettingHomeActicity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.customview.alertview.AlertVerticalDialog;
import com.star.pibbledev.services.global.customview.alertview.NotReadyYetDialog;
import com.star.pibbledev.services.global.customview.SlidingFrameLayoutAnimation;
import com.star.pibbledev.home.HomepageFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.PhotoPickerActivity;

import com.star.pibbledev.services.network.CheckUpdate;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.profile.ProfileFragment;
import com.star.pibbledev.profile.ProfileHomeFragment;
import com.star.pibbledev.discover.DiscoverSearchFragment;
import com.star.pibbledev.wallet.home.RegisterPinActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class DashboardFragment extends Fragment implements View.OnClickListener, RequestListener {

    Context mContext;

    public static final String TAG = "DashboardFragment";

    LinearLayout linear_btns, linear_setting_btns, linear_tab_add, linear_tab_home, linear_tab_discover, linear_tab_profile,
    linear_tab_setting;
    RelativeLayout linear_camera, linear_charity, linear_goods, linear_commerce, linear_album, linear_funding;
    FrameLayout frame_content, frame_tabbar, frame_bottombar;
    ImageView img_tab_home, img_tab_discover,img_tab_profile, img_tab_setting, img_setting, img_notification, img_wallet, img_gifticon;

    ImageView img_tab_add;

    boolean type_actionBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashborad_new, container, false);

        mContext = getActivity();

        linear_tab_add = (LinearLayout)view.findViewById(R.id.linear_tab_add);
        linear_tab_add.setOnClickListener(this);
        linear_tab_home = (LinearLayout)view.findViewById(R.id.linear_tab_home);
        linear_tab_home.setOnClickListener(this);
        linear_tab_discover = (LinearLayout)view.findViewById(R.id.linear_tab_discover);
        linear_tab_discover.setOnClickListener(this);
        linear_tab_profile = (LinearLayout)view.findViewById(R.id.linear_tab_profile);
        linear_tab_profile.setOnClickListener(this);
        linear_tab_setting = (LinearLayout)view.findViewById(R.id.linear_tab_setting);
        linear_tab_setting.setOnClickListener(this);
        linear_charity = (RelativeLayout)view.findViewById(R.id.linear_charity);
        linear_charity.setOnClickListener(this);
        frame_bottombar = (FrameLayout) view.findViewById(R.id.frame_bottombar);
        linear_goods = (RelativeLayout)view.findViewById(R.id.linear_goods);
        linear_goods.setOnClickListener(this);
        linear_commerce = (RelativeLayout)view.findViewById(R.id.linear_commerce);
        linear_commerce.setOnClickListener(this);
        linear_album = (RelativeLayout)view.findViewById(R.id.linear_album);
        linear_album.setOnClickListener(this);
        linear_funding = (RelativeLayout)view.findViewById(R.id.linear_funding);
        linear_funding.setOnClickListener(this);
        linear_camera = (RelativeLayout)view.findViewById(R.id.linear_camera);
        linear_camera.setOnClickListener(this);
        linear_btns = (LinearLayout) view.findViewById(R.id.linear_btns);
        linear_setting_btns = (LinearLayout)view.findViewById(R.id.linear_setting_btns);

        frame_tabbar = (FrameLayout)view.findViewById(R.id.frame_tabbar);
        frame_content = (FrameLayout)view.findViewById(R.id.frame_content);

        img_tab_add = (ImageView)view.findViewById(R.id.img_tab_add);
        img_tab_add.setTag(false);

        img_gifticon = (ImageView)view.findViewById(R.id.img_gifticon);
        img_gifticon.setOnClickListener(this);
        img_setting = (ImageView)view.findViewById(R.id.img_setting);
        img_setting.setOnClickListener(this);
        img_notification = (ImageView)view.findViewById(R.id.img_notification);
        img_notification.setOnClickListener(this);
        img_wallet = (ImageView)view.findViewById(R.id.img_wallet);
        img_wallet.setOnClickListener(this);

        img_tab_home = (ImageView)view.findViewById(R.id.img_tab_home);
        img_tab_discover = (ImageView)view.findViewById(R.id.img_tab_discover);
        img_tab_profile = (ImageView)view.findViewById(R.id.img_tab_profile);
        img_tab_setting = (ImageView)view.findViewById(R.id.img_tab_setting);
        img_tab_setting.setTag(false);

        if (Utility.g_indexDashboardTab == 0) {
            Utility.g_indexDashboardTab = 1;
        }

        type_actionBar = false;

        String type = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(Constants.TYPE);
        if (type != null && type.length() > 0) {

            Utility.g_indexDashboardTab = 1;

            setTab(Utility.g_indexDashboardTab);

            if (type.equals(Constants.TYPE_POST)) {

                int id = Objects.requireNonNull(getActivity()).getIntent().getIntExtra(Constants.ID, -1);

                Intent intent = new Intent(mContext, UsersActivity.class);
                intent.putExtra(UsersActivity.ACTIVITY_TYPE, UsersActivity.TYPE_DISPLAY_ONE_POST);
                intent.putExtra(Constants.POSTID, id);
                mContext.startActivity(intent);
                ((AppCompatActivity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            } else if (type.equals(Constants.TYPE_PROFILE)) {

                int id = Objects.requireNonNull(getActivity()).getIntent().getIntExtra(Constants.ID, -1);
                String username = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(Constants.USERNAME);

                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.SELECT_USERNAME, username);
                intent.putExtra(UserProfileActivity.SELECT_USERID, id);
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
            }

        } else {

            setTab(Utility.g_indexDashboardTab);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTab(int nIndex) {

        Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.frame_content);

        if (nIndex == 1) {

            Utility.g_indexDashboardTab = 1;

            img_tab_home.setImageDrawable(getResources().getDrawable(R.drawable.menu_home_select));
            img_tab_discover.setImageDrawable(getResources().getDrawable(R.drawable.menu_discover_unselect));
            img_tab_profile.setImageDrawable(getResources().getDrawable(R.drawable.menu_profile_unselect));

            if (fragment instanceof ProfileFragment) {
                Fragment fm1 = Objects.requireNonNull(((ProfileFragment) fragment).getFragmentManager()).findFragmentById(R.id.frame_home_profile);

                if (fm1 instanceof ProfileHomeFragment) {
                    ((ProfileHomeFragment)fm1).customDestroy();
                }
            } else if (fragment instanceof DiscoverSearchFragment) {

                ((DiscoverSearchFragment) fragment).customDestory();

            }

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new HomepageFragment(), HomepageFragment.TAG).addToBackStack(null).commit();

        } else if (nIndex == 2) {

            Utility.g_indexDashboardTab = 2;

            img_tab_home.setImageDrawable(getResources().getDrawable(R.drawable.menu_home_unselect));
            img_tab_discover.setImageDrawable(getResources().getDrawable(R.drawable.menu_discover_select));
            img_tab_profile.setImageDrawable(getResources().getDrawable(R.drawable.menu_profile_unselect));

            if (fragment instanceof ProfileFragment) {
                Fragment fm1 = Objects.requireNonNull(((ProfileFragment) fragment).getFragmentManager()).findFragmentById(R.id.frame_home_profile);

                if (fm1 instanceof ProfileHomeFragment) {
                    ((ProfileHomeFragment)fm1).customDestroy();
                }

            } else if (fragment instanceof HomepageFragment) {
                ((HomepageFragment)fragment).customDestory();
            }

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new DiscoverSearchFragment(), DiscoverSearchFragment.TAG).addToBackStack(null).commit();

        } else if (nIndex == 3) {

            Utility.g_indexDashboardTab = 3;

            img_tab_home.setImageDrawable(getResources().getDrawable(R.drawable.menu_home_unselect));
            img_tab_discover.setImageDrawable(getResources().getDrawable(R.drawable.menu_discover_unselect));
            img_tab_profile.setImageDrawable(getResources().getDrawable(R.drawable.menu_profile_select));

            if (fragment instanceof HomepageFragment) {

                ((HomepageFragment)fragment).customDestory();

            } else if (fragment instanceof DiscoverSearchFragment) {

                ((DiscoverSearchFragment) fragment).customDestory();

            }

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment fragOne = new ProfileFragment();
            Bundle arguments = new Bundle();
            arguments.putBoolean("shouldYouCreateAChildFragment", true);
            fragOne.setArguments(arguments);
            ft.replace(R.id.frame_content, fragOne, ProfileFragment.TAG);
            ft.addToBackStack(null).commit();
        }
    }

    private void gotoWallet(){

        if (CheckUpdate.check(mContext)) {

            AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.update_required),
                    getString(R.string.update_required_description),
                    null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                @Override
                public void onClickButton(int position) {

                    if (position == 1) {
                        try {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.star.pibbledev")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id"+"com.star.pibbledev")));
                        }
                    }

                    dismiss();

                }
            };
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else {

            String limitTime = Utility.getReadPref(mContext).getStringValue(Constants.RESET_PINCODE_FAILED_LIMIT);

            if (limitTime != null && limitTime.length() > 0 && Utility.isMoreThanMins(limitTime, 5)) {

                AlertVerticalDialog dialog = new AlertVerticalDialog(mContext, getString(R.string.oh_snap),
                        getString(R.string.error_goto_wallet),
                        null, getString(R.string.okay), R.color.colorMain, R.color.colorMain) {

                    @Override
                    public void onClickButton(int position) {

                        dismiss();

                    }
                };

                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            } else {

                Utility.getSavedPref(mContext).saveString(Constants.RESET_PINCODE_FAILED_LIMIT, "");

                Intent intent = new Intent(mContext, RegisterPinActivity.class);
                startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == linear_tab_home) {

            onClickAddTab(false, false);

            if (Utility.g_indexDashboardTab == 1) {

                Fragment fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(HomepageFragment.TAG);

                if (fm instanceof HomepageFragment) {
                    ((HomepageFragment)fm).listviewScrollToTop();
                }

            } else {

                setTab(1);
            }

        } else if (v == linear_tab_discover) {

            onClickAddTab(false, false);

            if (Utility.g_indexDashboardTab == 2) {

                Fragment fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(DiscoverSearchFragment.TAG);

                if (fm instanceof DiscoverSearchFragment) {
                    ((DiscoverSearchFragment)fm).autoScrollToTop();
                }

            } else {

                setTab(2);

            }

        } else if (v == linear_tab_profile) {

            onClickAddTab(false, false);

            if (Utility.g_indexDashboardTab == 3) {

                Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);

                if (fragment instanceof ProfileFragment) {

                    Fragment fm1 = ((ProfileFragment)fragment).getFragmentManager().findFragmentById(R.id.frame_home_profile);

                    if (fm1 instanceof ProfileHomeFragment) {
                        ((ProfileHomeFragment)fm1).scrollToTop();
                    }
                }

            } else {

                setTab(3);
            }

        } else if (v == linear_tab_setting) {

            onClickAddTab(false, false);

            if (img_tab_setting.getTag().equals(true)) {
                onClickAddTab(false, true);
            } else {
                onClickAddTab(true, true);
            }

        } else if (v == linear_tab_add) {

            onClickAddTab(false, true);

            if (img_tab_add.getTag().equals(true)) {
                onClickAddTab(false, false);
            } else {
                onClickAddTab(true, false);
            }

        } else if (v == linear_camera) {

            onClickAddTab(false, false);

            Utility.g_director = Constants.MEDIA;
            Utility.mainFeedParams = new MainFeedParams();

            Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (v == linear_charity) {

            onClickAddTab(false, false);

            Utility.g_director = Constants.CHARITY;
            Utility.mainFeedParams = new MainFeedParams();

            Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (v == linear_goods) {

            onClickAddTab(false, false);

            Utility.g_director = Constants.GOODS;
            Utility.mainFeedParams = new MainFeedParams();

            Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (v == linear_commerce) {

            onClickAddTab(false, false);

            Utility.g_director = Constants.COMMERCE;
            Utility.mainFeedParams = new MainFeedParams();

            Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (v == linear_album) {

            onClickAddTab(false, false);

            Intent intent = new Intent(mContext, AlbumActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

//            NotReadyYetDialog dialog = new NotReadyYetDialog(mContext);
//            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.show();

        } else if (v == linear_funding) {

            Utility.g_director = Constants.CROWD;

            onClickAddTab(false, false);

            Utility.mainFeedParams = new MainFeedParams();

            Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.top_in, R.anim.top_out);

        } else if (v == img_gifticon) {

            onClickAddTab(false, true);

            Intent intent = new Intent(mContext, GiftHomeActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == img_wallet) {

            onClickAddTab(false, true);

            gotoWallet();

        } else if (v == img_notification) {

            onClickAddTab(false, true);

            Intent intent = new Intent(mContext, MyFeedsActivity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        } else if (v == img_setting) {

            onClickAddTab(false, true);

            Intent intent = new Intent(mContext, SettingHomeActicity.class);
            startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    public void tabBarAnimaition(boolean isShow) {

        if (frame_tabbar == null) return;

        if (Utility.g_isShowingTabbar) return;

        if (isShow) {

            if (type_actionBar) {

                type_actionBar = false;

                Animation animation = new SlidingFrameLayoutAnimation(frame_tabbar, Utility.dpToPx(0), Utility.dpToPx(45));
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(300);
                frame_tabbar.setAnimation(animation);
                frame_tabbar.startAnimation(animation);

            }

        } else {

            if (!type_actionBar) {

                type_actionBar = true;

                Animation animation = new SlidingFrameLayoutAnimation(frame_tabbar, Utility.dpToPx(45), Utility.dpToPx(0));
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(300);
                frame_tabbar.setAnimation(animation);
                frame_tabbar.startAnimation(animation);

            }
        }
    }

    public void onClickAddTab(boolean isShow, boolean isSetting) {

        if (isShow) {

            Utility.g_isShowingTabbar = true;

            if (frame_tabbar != null) {

                Animation animation = new SlidingFrameLayoutAnimation(frame_tabbar, Utility.dpToPx(45), Utility.dpToPx(263));
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(300);
                frame_tabbar.setAnimation(animation);
                frame_tabbar.startAnimation(animation);

                if (isSetting) {

                    linear_setting_btns.setVisibility(View.VISIBLE);
                    linear_btns.setVisibility(View.INVISIBLE);

                    img_tab_setting.setImageResource(R.drawable.menu_more_select);
                    img_tab_setting.setTag(true);
                    img_tab_add.setTag(false);

                } else {

                    linear_setting_btns.setVisibility(View.INVISIBLE);
                    linear_btns.setVisibility(View.VISIBLE);

                    img_tab_add.setImageResource(R.drawable.menu_post_select);
                    img_tab_add.setTag(true);
                    img_tab_setting.setTag(false);
                }
            }

        } else {

            Utility.g_isShowingTabbar = false;

            if (img_tab_add != null && img_tab_add.getTag().equals(true)) {

                if (frame_tabbar != null) {

                    Animation animation = new SlidingFrameLayoutAnimation(frame_tabbar, Utility.dpToPx(263), Utility.dpToPx(45));
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(300);
                    frame_tabbar.setAnimation(animation);
                    frame_tabbar.startAnimation(animation);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        img_tab_add.setImageResource(R.drawable.menu_post_unselect);
                        img_tab_add.setTag(false);

                    }
                }, 200);

            } else if (img_tab_setting != null && img_tab_setting.getTag().equals(true)) {

                if (frame_tabbar != null) {

                    Animation animation = new SlidingFrameLayoutAnimation(frame_tabbar, Utility.dpToPx(263), Utility.dpToPx(45));
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(300);
                    frame_tabbar.setAnimation(animation);
                    frame_tabbar.startAnimation(animation);

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        img_tab_setting.setTag(false);
                        img_tab_setting.setImageResource(R.drawable.menu_more_unselect);
                    }
                }, 200);
            }
        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

    }

    @Override
    public void failed(String strError, int errorCode) {
//        Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }


}
