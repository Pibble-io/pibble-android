package com.star.pibbledev.home;

import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.DirectoryModel;
import com.star.pibbledev.services.global.model.FundingModel;
import com.star.pibbledev.services.global.model.CommerceModel;
import com.star.pibbledev.services.global.model.GoodsModel;
import com.star.pibbledev.services.global.model.LocationModel;
import com.star.pibbledev.services.global.model.PostsModel;

import java.util.ArrayList;

public class MainFeedParams {

    public String postUuid = "";
    public String caption = "";
    public FundingModel fundingModel;
    public LocationModel locationModel = new LocationModel();
    public ArrayList<String> mediaPaths = new ArrayList<>();
    public ArrayList<DirectoryModel> directoryModels = new ArrayList<>();
    public ArrayList<String> ary_mediaTokens = new ArrayList<>();
    public ArrayList<String> ary_tags = new ArrayList<>();
    public int category_id = 1;

    // -- commerce --

    public CommerceModel commerceModel = new CommerceModel();

    public ArrayList<String> originalPaths = new ArrayList<>();

    public int height_textview;

    /* create promotion */
    public String promotion_destination = "";
    public String promotion_actionButton = "";
    public String promotion_actionButton_type = "";
    public String promotion_site = "";
    public int promotion_budget;
    public int promotion_duration;

    public PostsModel postsModel = new PostsModel();

    /* create goods */
    public GoodsModel goodsModel;
}
