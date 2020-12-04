package com.star.pibbledev.home.funding;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.description.MainFeedDescriptionActivity;
import com.star.pibbledev.home.createmedia.location.MainFeedLocationActivity;
import com.star.pibbledev.home.createmedia.mediapicker.ui.base.BaseFragment;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.PhotoHorizontalAdapter;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.tags.MainFeedTagsActivity;
import com.star.pibbledev.services.global.Utility;

import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class JoinFundingTeamFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "JoinFundingTeamFragment";

    public static JoinFundingTeamFragment newInstance(Bundle args) {
        JoinFundingTeamFragment fragment = new JoinFundingTeamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_mainfeed_join_funding_team;
    }

    @BindView(R.id.list)
    RecyclerView mList;

    @BindView(R.id.txt_description)
    TextView mDescView;

    @BindView(R.id.txt_tag)
    TextView mTagView;

    @BindView(R.id.txt_location)
    TextView mLocationView;

    @BindView(R.id.img_teamlogo)
    ImageView img_teamlogo;

    @BindView(R.id.txt_teamEmo)
    TextView txt_teamEmo;

    @BindView(R.id.txt_teamname)
    TextView txt_teamname;

    @BindView(R.id.txt_teamtitle)
    TextView txt_teamtitle;

    @BindView(R.id.txt_members)
    TextView txt_members;

    @BindView(R.id.txt_raise)
    TextView txt_raise;

    private PhotoHorizontalAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        mAdapter = new PhotoHorizontalAdapter(getContext(), Utility.mainFeedParams.mediaPaths);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_post) {
            // TODO: 10/24/2018 Update this method
            Timber.d("Media Path [%s]", mAdapter.getItems());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_media_menu, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mList.setHasFixedSize(true);
        mList.setAdapter(mAdapter);

        mDescView.setOnClickListener(this);
        mLocationView.setOnClickListener(this);
        mTagView.setOnClickListener(this);

        if (Utility.mainFeedParams.fundingModel != null) {

            String teamName = Utility.mainFeedParams.fundingModel.teamModel.name;

            if (Utility.mainFeedParams.fundingModel.teamModel.logo.equals("null") || Utility.mainFeedParams.fundingModel.teamModel.logo.equals("")) {

                txt_teamEmo.setVisibility(View.VISIBLE);
                int value = 0;
                if (teamName.length() > 14) value = 14;
                else value = teamName.length();
                img_teamlogo.setBackgroundColor(getResources().getColor(Utility.g_aryColors[value]));
                txt_teamEmo.setText(Utility.getUserEmoName(teamName));

            } else {

                ImageLoader.getInstance().displayImage(Utility.mainFeedParams.fundingModel.teamModel.logo, img_teamlogo);
                txt_teamEmo.setVisibility(View.GONE);

            }

            txt_teamname.setText(teamName);
            txt_teamtitle.setText(Utility.mainFeedParams.fundingModel.campaignModel.title);

            String created_at = Utility.getTypeFromDate(Utility.mainFeedParams.fundingModel.teamModel.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","MM-dd-yyyy");
            String str_members = getString(R.string.members) + " " + String.valueOf(Utility.mainFeedParams.fundingModel.teamModel.members_count) + " " + getString(R.string.created_at) + " " + created_at;
            String str_raise = String.format("%s %s PIB %s %s PIB", getString(R.string.raised), String.valueOf(Utility.mainFeedParams.fundingModel.campaignModel.raised), getString(R.string.goal), String.valueOf(Utility.mainFeedParams.fundingModel.campaignModel.goal));

            txt_members.setText(str_members);
            txt_raise.setText(str_raise);
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        if (Utility.mainFeedParams.caption.length() > 0) {

            mDescView.setText(Utility.mainFeedParams.caption);

            if (Utility.mainFeedParams.height_textview > Utility.dpToPx(100)) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,Utility.dpToPx(16),0,0);
                mDescView.setLayoutParams(params);
            }
        }

        if (Utility.mainFeedParams.locationModel.description.length() > 0) {
            mLocationView.setText(Utility.mainFeedParams.locationModel.description);
        }

        if (Utility.mainFeedParams.ary_tags.size() > 0) {
            String str_tag = "";
            for (int i = 0; i < Utility.mainFeedParams.ary_tags.size(); i++) {
                if (i == 0) {
                    str_tag = "#" + Utility.mainFeedParams.ary_tags.get(0);
                } else {
                    str_tag = str_tag + " #" + Utility.mainFeedParams.ary_tags.get(i);
                }
            }

            mTagView.setText(str_tag);

        }

    }

    @Override
    public void onDestroyView() {
        PostMediaHelper.sCachePost.clear();
        PostMediaHelper.sCachePost.addAll(mAdapter.getItems());
        super.onDestroyView();
    }

    public List<String> getPaths() {
        return mAdapter.getItems();
    }

    public String getLocation() {
        return mLocationView.getText().toString();
    }

    public String getTags() {
        return mTagView.getText().toString();
    }

    public String getDescription() {
        return mDescView.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (v == mDescView) {
            Intent intent = new Intent(getContext(), MainFeedDescriptionActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        } else if (v == mLocationView) {
            Intent intent = new Intent(getContext(), MainFeedLocationActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        } else if (v == mTagView) {
            Intent intent = new Intent(getContext(), MainFeedTagsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }
    }
}
