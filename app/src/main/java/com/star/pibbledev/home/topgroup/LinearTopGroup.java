package com.star.pibbledev.home.topgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("ViewConstructor")
public class LinearTopGroup extends LinearLayout implements RequestListener {

    private static final String TOP_BANNER_KO = "https://media.pibble.app/top-group-banner/top_banner_ko.jpg";
    private static final String TOP_BANNER_EN = "https://media.pibble.app/top-group-banner/top_banner_en.jpg";

    private LinearLayout linear_leaderboard, linear_banner, linear_banner_message;
    private TextView txt_news, txt_blockchain, txt_top, txt_webtoon, txt_newbie, txt_funding, txt_promote, txt_shopping, txt_time, txt_banner_detail;
    private AvatarImageView img_news, img_blockchain, img_top , img_webtoon, img_newbie, img_funding, img_promote, img_shopping;
    private ImageView img_banner;

    private Context pContext;
    private TopGroupListener pTopGroupListener;

    private int bannerHeight;

    private Runnable timerRunnable;
    private Handler timerHandler;

    public LinearTopGroup(Context context, TopGroupListener listener) {

        super(context);

        this.pTopGroupListener = listener;
        this.pContext = context;

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_list_posts_topgroup, this, true);

        linear_leaderboard = (LinearLayout)view.findViewById(R.id.linear_leaderboard);
        linear_banner = (LinearLayout)view.findViewById(R.id.linear_banner);
        linear_banner_message = (LinearLayout)view.findViewById(R.id.linear_banner_message);
        img_news = (AvatarImageView)view.findViewById(R.id.img_news);
        img_blockchain = (AvatarImageView)view.findViewById(R.id.img_blockchain);
        img_top = (AvatarImageView)view.findViewById(R.id.img_top);
        img_webtoon = (AvatarImageView)view.findViewById(R.id.img_webtoon);
        img_newbie = (AvatarImageView)view.findViewById(R.id.img_newbie);
        img_funding = (AvatarImageView)view.findViewById(R.id.img_funding);
        img_promote = (AvatarImageView)view.findViewById(R.id.img_promote);
        img_shopping = (AvatarImageView)view.findViewById(R.id.img_shopping);
        txt_news = (TextView)view.findViewById(R.id.txt_news);
        txt_blockchain = (TextView)view.findViewById(R.id.txt_blockchain);
        txt_top = (TextView)view.findViewById(R.id.txt_top);
        txt_webtoon = (TextView)view.findViewById(R.id.txt_webtoon);
        txt_newbie = (TextView)view.findViewById(R.id.txt_newbie);
        txt_funding = (TextView)view.findViewById(R.id.txt_funding);
        txt_promote = (TextView)view.findViewById(R.id.txt_promote);
        txt_shopping = (TextView)view.findViewById(R.id.txt_shopping);
        txt_time = (TextView)view.findViewById(R.id.txt_time);
        img_banner = (ImageView)view.findViewById(R.id.img_banner);
        txt_banner_detail = (TextView)view.findViewById(R.id.txt_banner_detail);

        linear_banner_message.setVisibility(GONE);

        String language = Utility.getReadPref(context).getStringValue(Constants.LANGUAGE);

        String banner_url = TOP_BANNER_EN;

        if (language != null && language.equals("ko")) {
            banner_url = TOP_BANNER_KO;
        } else if (language != null && language.equals("en")) {
            banner_url = TOP_BANNER_EN;
        }

        ImageLoader.getInstance().displayImage(banner_url, img_banner);
        ImageLoader.getInstance().loadImage(banner_url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                super.onLoadingComplete(imageUri, view, loadedImage);

                ViewTreeObserver vto = linear_banner.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        linear_banner.getViewTreeObserver().removeOnPreDrawListener(this);

                        bannerHeight = linear_banner.getMeasuredHeight();

                        linear_banner.setVisibility(GONE);

                        return true;
                    }
                });
            }
        });

        timerHandler = new Handler();
        timerRunnable = new Runnable() {

            @Override
            public void run() {

                getTime();

                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.postDelayed(timerRunnable, 0);

        img_banner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                pTopGroupListener.goToBannerDetail();
            }
        });

        getBannerMessage();

        initTopGroupView();

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            timerHandler.postDelayed(timerRunnable, 0);
        } else {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void initTopGroupView() {

        ArrayList<AvatarImageView> avatarImageViews = new ArrayList<>();

        avatarImageViews.add(img_news);
        avatarImageViews.add(img_blockchain);
        avatarImageViews.add(img_top);
        avatarImageViews.add(img_newbie);
        avatarImageViews.add(img_webtoon);
        avatarImageViews.add(img_funding);
        avatarImageViews.add(img_promote);
        avatarImageViews.add(img_shopping);

        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(txt_news);
        textViews.add(txt_blockchain);
        textViews.add(txt_top);
        textViews.add(txt_newbie);
        textViews.add(txt_webtoon);
        textViews.add(txt_funding);
        textViews.add(txt_promote);
        textViews.add(txt_shopping);

        linear_leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pTopGroupListener.goToLeaderBoard();

            }
        });

        img_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_news.getTag() == null || img_news.getTag().equals(false)) pTopGroupListener.onClickNews(true);
                else pTopGroupListener.onClickNews(false);

                topGroupStatus(avatarImageViews,  textViews,0);
            }
        });

        img_blockchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_blockchain.getTag() == null || img_blockchain.getTag().equals(false)) pTopGroupListener.onClickCoin(true);
                else pTopGroupListener.onClickCoin(false);

                topGroupStatus(avatarImageViews,  textViews,1);
            }
        });

        img_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_top.getTag() == null || img_top.getTag().equals(false)) pTopGroupListener.onClickHot(true);
                else pTopGroupListener.onClickHot(false);

                if (img_top.getTag() == null || img_top.getTag().equals(false)) {

                    linear_banner.setVisibility(View.VISIBLE);
                    Utility.bannerViewAnimation(linear_banner, 0, bannerHeight, 500);

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linear_banner.setVisibility(View.GONE);
                        }
                    }, 300);

                    if (linear_banner.getVisibility() == View.VISIBLE) Utility.bannerViewAnimation(linear_banner, bannerHeight, 0, 500);
                }

                topGroupStatus(avatarImageViews,  textViews,2);
            }
        });

        img_newbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_newbie.getTag() == null || img_newbie.getTag().equals(false)) pTopGroupListener.onClickNewbie(true);
                else pTopGroupListener.onClickNewbie(false);

                topGroupStatus(avatarImageViews,  textViews,3);
            }
        });

        img_webtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_webtoon.getTag() == null || img_webtoon.getTag().equals(false)) pTopGroupListener.onClickWebtoon(true);
                else pTopGroupListener.onClickWebtoon(false);

                topGroupStatus(avatarImageViews,  textViews,4);
            }
        });

        img_funding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_funding.getTag() == null || img_funding.getTag().equals(false)) pTopGroupListener.onClickFunding(true);
                else pTopGroupListener.onClickFunding(false);

                topGroupStatus(avatarImageViews,  textViews,5);
            }
        });

        img_promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_promote.getTag() == null || img_promote.getTag().equals(false)) pTopGroupListener.onClickPromote(true);
                else pTopGroupListener.onClickPromote(false);

                topGroupStatus(avatarImageViews,  textViews,6);
            }
        });

        img_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img_shopping.getTag() == null || img_shopping.getTag().equals(false)) pTopGroupListener.onClickShopping(true);
                else pTopGroupListener.onClickShopping(false);

                topGroupStatus(avatarImageViews, textViews,7);
            }
        });
    }

    private void topGroupStatus(ArrayList<AvatarImageView> arrayList, ArrayList<TextView> textViews, int position) {

        if (arrayList != null && arrayList.size() > 0) {

            for (int i = 0; i < arrayList.size(); i++) {

                AvatarImageView imageView = (AvatarImageView)arrayList.get(i);
                TextView textView = (TextView)textViews.get(i);

                if (i == position) {

                    if (imageView.getTag() == null || imageView.getTag().equals(false)) {

                        imageView.setTag(true);
                        imageView.setStrokeColor(pContext.getResources().getColor(R.color.colorMain));

                        textView.setTextColor(pContext.getResources().getColor(R.color.colorMain));

                    } else {

                        imageView.setTag(false);
                        imageView.setStrokeColor(pContext.getResources().getColor(R.color.transparent));

                        textView.setTextColor(pContext.getResources().getColor(R.color.black));
                    }

                } else {

                    imageView.setTag(false);
                    imageView.setStrokeColor(pContext.getResources().getColor(R.color.transparent));

                    textView.setTextColor(pContext.getResources().getColor(R.color.black));

                    if (position != 2) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                linear_banner.setVisibility(View.GONE);
                            }
                        }, 300);

                        if (linear_banner.getVisibility() == View.VISIBLE) Utility.ViewAnimation(linear_banner, 200, 0, 300);

                    }
                }
            }
        }
    }

    private void getTime() {
//        String token = Utility.getReadPref(pContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
//        ServerRequest.getSharedServerRequest().getTopGroupTime(this, pContext, token);

        int left_hour, left_mins, left_seconds;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        Matcher m = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})").matcher(sdf.format(new Date()));
        if (! m.matches())
            throw new IllegalArgumentException("Invalid time format");

        int hours = Integer.parseInt(m.group(1));
        int mins = Integer.parseInt(m.group(2));
        int seconds = Integer.parseInt(m.group(3));

        int total = 24 * 3600;
        int current  = hours * 3600 + mins * 60 + seconds;
        int difference = total - current;

        left_hour = difference / 3600;
        left_mins = difference % 3600 / 60;
        left_seconds = difference % 3600 % 60;

        String leftTime = String.format("%s%s %s%s %s%s %s", left_hour, pContext.getString(R.string.left_hour), left_mins, pContext.getString(R.string.left_min), left_seconds, pContext.getString(R.string.left_sec), pContext.getString(R.string.time_left));
        txt_time.setText(leftTime);
    }

    private void getBannerMessage() {
        String token = Utility.getReadPref(pContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
        ServerRequest.getSharedServerRequest().getTopGroupBannerMessage(this, pContext, token);
    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (objResult == null || objResult.optString("message") == null || objResult.optString("message").equals("")) {

            linear_banner_message.setVisibility(GONE);

        } else {

            linear_banner_message.setVisibility(VISIBLE);

            txt_banner_detail.setSelected(true);
            txt_banner_detail.setText(objResult.optString("message"));

        }
    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
