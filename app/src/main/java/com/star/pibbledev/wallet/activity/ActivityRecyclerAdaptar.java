package com.star.pibbledev.wallet.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.EntityModel;

import java.util.ArrayList;

public class ActivityRecyclerAdaptar extends RecyclerView.Adapter<ActivityRecyclerAdaptar.ViewHolder> {

    private Context context;
    private ArrayList<EntityModel> pData;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater=null;
    private ActivityListener activityListListener;

    private int mIndex;
    private String ownUUID;

    ActivityRecyclerAdaptar(Context context, ArrayList<EntityModel> pData, ImageLoader imageLoader, int index) {

        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.pData = pData;
        this.imageLoader = imageLoader;
        this.mIndex = index;

        ownUUID = Utility.getReadPref(this.context).getStringValue("uuid");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_wallet_acts, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EntityModel entityModel = pData.get(position);

        holder.txt_content1.setVisibility(View.GONE);
        holder.txt_message.setVisibility(View.GONE);
        holder.linear_message.setVisibility(View.GONE);
        holder.linear_action.setVisibility(View.GONE);
        holder.txt_emo.setVisibility(View.GONE);
        holder.linear_txid.setVisibility(View.GONE);
        holder.linear_copied.setVisibility(View.INVISIBLE);

        holder.img_user.setImageBitmap(null);
        holder.img_user.setForeground(context.getResources().getDrawable(R.drawable.img_corner_radius));
        holder.img_user.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        switch (entityModel.entity_type) {

            case Constants.WALLET_EXCHANGE: {

                holder.txt_content1.setVisibility(View.VISIBLE);

                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));

                float fromVal = Float.parseFloat(entityModel.from_value);
                float toVal = Float.parseFloat(entityModel.to_value);

                @SuppressLint("DefaultLocale") String content = String.format("%s %.1f %s to", context.getString(R.string.exchanged), fromVal, entityModel.from_symbol);
                @SuppressLint("DefaultLocale") String content1 = String.format("%.1f %s", toVal, entityModel.to_symbol);

                int fromColor = 0;
                if (entityModel.from_symbol.equals("PRB")) {
                    fromColor = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.from_symbol.equals("PIB") || entityModel.from_symbol.equals("PIBK")) {
                    fromColor = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.from_symbol.equals("PGB")) {
                    fromColor = this.context.getResources().getColor(R.color.colorWalletGreen);
                }

                int toColor = 0;
                if (entityModel.to_symbol.equals("PRB")) {
                    toColor = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.to_symbol.equals("PIB") || entityModel.to_symbol.equals("PIBK")) {
                    toColor = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.to_symbol.equals("PGB")) {
                    toColor = this.context.getResources().getColor(R.color.colorWalletGreen);
                }

                Spannable spannable = new SpannableString(content);
                spannable.setSpan(new ForegroundColorSpan(fromColor), context.getString(R.string.exchanged).length(), context.getString(R.string.exchanged).length() + String.valueOf(fromVal).length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannable.setSpan(new ForegroundColorSpan(toColor), 18 + String.valueOf(fromVal).length() , content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.txt_content.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.txt_content1.setText(content1);
                holder.txt_content1.setTextColor(toColor);

                if (mIndex == 0) {

                    holder.txt_pib.setText(String.format("%s %s", String.valueOf(toVal), entityModel.to_symbol));
                    holder.txt_pib.setTextColor(toColor);

                } else {

                    if (entityModel.from_symbol.equals("PIB") || entityModel.from_symbol.equals("PIBK")) {
                        holder.txt_pib.setText(String.format("- %s %s", String.valueOf(fromVal), entityModel.from_symbol));
                    } else {
                        holder.txt_pib.setText(String.format("%s %s", String.valueOf(fromVal), entityModel.from_symbol));
                    }
                    holder.txt_pib.setTextColor(this.context.getResources().getColor(R.color.colorMain));
                }

                break;
            }
            case Constants.WALLET_REWARD: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);
                String content = "";
                String value = "";

                switch (entityModel.type) {

                    case Constants.REWARD_DEFAULT:

                        content = context.getString(R.string.got_media_post_reward);
                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));

                        break;
                    case Constants.REWARD_UPVOTE_POST: {

                        String userImg = "";
                        String username = "";
                        int userImgEmo = 0;

                        if (entityModel.to_user != null && entityModel.from_user != null) {

                            if (ownUUID.equals(entityModel.from_user.uuid)) {
                                userImg = entityModel.to_user.avatar;
                                userImgEmo = entityModel.to_user.avatar_temp;
                                username = entityModel.to_user.username;
                                content = context.getString(R.string.upvote_to_feed) + " @" + username;
                                value = "- " + String.valueOf(f_value);
                            } else {
                                userImg = entityModel.from_user.avatar;
                                userImgEmo = entityModel.from_user.avatar_temp;
                                username = entityModel.from_user.username;
                                content = context.getString(R.string.got_upvote_from) + " @" + username;
                                value = String.valueOf(f_value);
                            }

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(username));
                            }
                        }

                        break;
                    }
                    case Constants.REWARD_REWARD_FREE:

                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        content = context.getString(R.string.you_got_free_upvote_reward);
                        value = String.valueOf(f_value);

                        break;
                    case Constants.REWARD_UPVOTE_COMMENT: {

                        String userImg = "";
                        String username = "";
                        int userImgEmo = 0;

                        if (entityModel.to_user != null && entityModel.from_user != null) {

                            if (ownUUID.equals(entityModel.from_user.uuid)) {
                                userImg = entityModel.to_user.avatar;
                                userImgEmo = entityModel.to_user.avatar_temp;
                                username = entityModel.to_user.username;
                                content = context.getString(R.string.upvote_to_comment) + " @" + username;
                                value = "- " + String.valueOf(f_value);
                            } else {
                                userImg = entityModel.from_user.avatar;
                                userImgEmo = entityModel.from_user.avatar_temp;
                                username = entityModel.from_user.username;
                                content = context.getString(R.string.got_comment_upvote_from) + " @" + username;
                                value = String.valueOf(f_value);
                            }

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(entityModel.from_user.username));
                            }

                        }

                        break;
                    }
                    case Constants.REWARD_UPVOTE_PROFILE: {

                        String userImg = "";
                        String username = "";
                        int userImgEmo = 0;

                        if (entityModel.to_user != null && entityModel.from_user != null) {

                            if (ownUUID.equals(entityModel.from_user.uuid)) {
                                userImg = entityModel.to_user.avatar;
                                userImgEmo = entityModel.to_user.avatar_temp;
                                username = entityModel.to_user.username;
                                content = context.getString(R.string.upvote_to_profile) + " @" + username;
                                value = "- " + String.valueOf(f_value);
                            } else {
                                userImg = entityModel.from_user.avatar;
                                userImgEmo = entityModel.from_user.avatar_temp;
                                username = entityModel.from_user.username;
                                content = context.getString(R.string.got_profile_upvote_from) + " @" + username;
                                value = String.valueOf(f_value);
                            }

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(username));
                            }

                        }

                        break;
                    }

                    case Constants.REWARD_UPVOTE_HELP_ANSWER: {

                        String userImg = "";
                        String username = "";
                        int userImgEmo = 0;

                        if (entityModel.to_user != null && entityModel.from_user != null) {

                            if (ownUUID.equals(entityModel.from_user.uuid)) {
                                userImg = entityModel.to_user.avatar;
                                userImgEmo = entityModel.to_user.avatar_temp;
                                username = entityModel.to_user.username;
                                content = context.getString(R.string.upvote_answer) + " @" + username;
                                value = "- " + f_value;
                            } else {
                                userImg = entityModel.from_user.avatar;
                                userImgEmo = entityModel.from_user.avatar_temp;
                                username = entityModel.from_user.username;
                                content = context.getString(R.string.got_upvote_answer) + " @" + username;
                                value = String.valueOf(f_value);
                            }

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(username));
                            }
                        }

                        break;
                    }

                    case Constants.REWARD_DELETE_MEDIA_POST:

                        content = context.getString(R.string.lost_media_delete_reward);
                        if (f_value == 0) value = "0";
                        else value = "-" + String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));

                        break;

                    case Constants.REWARD_REFERRAL:

                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        content = context.getString(R.string.referral_reward);
                        value = String.valueOf(f_value);

                        break;

                    case Constants.REWARD_CHALLANGE_10MIN:
                    case Constants.REWARD_CHALLANGE_DAILY:
                    case Constants.REWARD_CHALLANGE_HOURLY:{

                        String userImg = "";
                        String username = "";
                        int userImgEmo = 0;

                        if (entityModel.to_user != null) {

                            userImg = entityModel.to_user.avatar;
                            userImgEmo = entityModel.to_user.avatar_temp;
                            username = entityModel.to_user.username;
                            content = context.getString(R.string.challenge_reward);
                            value = String.valueOf(f_value);

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(username));
                            }
                        }

                        break;
                    }
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_DIGITAL_GOODS: {

                int color = 0;
                if (entityModel.coin.equals("PRB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                    color = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.coin.equals("PGB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletGreen);
                } else {
                    color = this.context.getResources().getColor(R.color.black);
                }

                holder.txt_pib.setTextColor(color);

                float f_value = Float.parseFloat(entityModel.value);
                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                if (entityModel.to_user != null && entityModel.from_user != null) {

                    if (ownUUID.equals(entityModel.from_user.uuid)) {
                        userImg = entityModel.to_user.avatar;
                        userImgEmo = entityModel.to_user.avatar_temp;
                        username = entityModel.to_user.username;
                        content = String.format("%s %s %s @%s", context.getString(R.string.sent), entityModel.coin, context.getString(R.string.to), username);
                        value = "- " + String.valueOf(f_value);
                    } else {
                        userImg = entityModel.from_user.avatar;
                        userImgEmo = entityModel.from_user.avatar_temp;
                        username = entityModel.from_user.username;
                        content = String.format("%s %s %s @%s", context.getString(R.string.received), entityModel.coin, context.getString(R.string.from), username);
                        value = String.valueOf(f_value);
                    }

                    if (!userImg.equals("null")) {
                        this.imageLoader.displayImage(userImg, holder.img_user);
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                    } else {
                        holder.txt_emo.setVisibility(View.VISIBLE);
                        holder.img_user.setImageDrawable(null);
                        holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                        holder.txt_emo.setText(Utility.getUserEmoName(username));
                    }
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));


                break;
            }

            case Constants.WALLET_INTERNAL: {

                int color = 0;
                if (entityModel.coin.equals("PRB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                    color = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.coin.equals("PGB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletGreen);
                } else {
                    color = this.context.getResources().getColor(R.color.black);
                }

                holder.txt_pib.setTextColor(color);

                float f_value = Float.parseFloat(entityModel.value);
                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                if (entityModel.to_user != null && entityModel.from_user != null) {

                    if (ownUUID.equals(entityModel.from_user.uuid)) {
                        userImg = entityModel.to_user.avatar;
                        userImgEmo = entityModel.to_user.avatar_temp;
                        username = entityModel.to_user.username;
                        content = String.format("%s %s %s @%s", context.getString(R.string.sent), entityModel.coin, context.getString(R.string.to), username);

                        if (entityModel.coin.equals("BTC")) value = "- " + String.format("%.8f", f_value);
                        else value = "- " + String.valueOf(f_value);

                    } else {
                        userImg = entityModel.from_user.avatar;
                        userImgEmo = entityModel.from_user.avatar_temp;
                        username = entityModel.from_user.username;
                        content = String.format("%s %s %s @%s", context.getString(R.string.received), entityModel.coin, context.getString(R.string.from), username);

                        if (entityModel.coin.equals("BTC")) value = String.format("%.8f", f_value);
                        else value = String.valueOf(f_value);
                    }

                    if (!userImg.equals("null")) {
                        this.imageLoader.displayImage(userImg, holder.img_user);
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                    } else {
                        holder.txt_emo.setVisibility(View.VISIBLE);
                        holder.img_user.setImageDrawable(null);
                        holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                        holder.txt_emo.setText(Utility.getUserEmoName(username));
                    }
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));


                break;
            }
            case Constants.WALLET_EXTERNAL: {

                int color = 0;

                if (entityModel.coin.equals("PRB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                    color = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.coin.equals("PGB")) {
                    color = this.context.getResources().getColor(R.color.colorWalletGreen);
                } else if (entityModel.coin.equals("ETH")) {
                    color = this.context.getResources().getColor(R.color.black);
                } else if (entityModel.coin.equals("BTC")) {
                    color = this.context.getResources().getColor(R.color.black);
                }

                holder.txt_pib.setTextColor(color);

                float f_value = Float.parseFloat(entityModel.value);
                String content = "";
                String content1 = "";
                String value = "";

                if (entityModel.type.equals(Constants.WALLET_EXTERNAL_DEPOSIT)) {

                    content = String.format("%s %s %s", context.getString(R.string.received), entityModel.coin, context.getString(R.string.from));
                    content1 = entityModel.from_address;

                    if (entityModel.coin.equals("BTC")) value = String.format("%.8f", f_value);
                    else value = String.valueOf(f_value);

                } else if (entityModel.type.equals(Constants.WALLET_EXTERNAL_WITHDRAW)) {

                    content = String.format("%s %s %s", context.getString(R.string.sent), entityModel.coin, context.getString(R.string.to));
                    content1 = entityModel.to_address;

                    if (entityModel.coin.equals("BTC")) value = "- " + String.format("%.8f", f_value);
                    else value = "- " + f_value;

                }

                if (entityModel.transaction_hash != null && !entityModel.transaction_hash.equals("null")) {

                    holder.linear_txid.setVisibility(View.VISIBLE);
                    holder.txt_txid.setText(entityModel.transaction_hash);

                    holder.linear_txid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            holder.linear_copied.setVisibility(View.VISIBLE);

                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("TxID", entityModel.transaction_hash);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.linear_copied.setVisibility(View.INVISIBLE);
                                }
                            }, 1500);
                        }
                    });

                } else {

                    holder.linear_txid.setVisibility(View.VISIBLE);
                    holder.txt_txid.setText(R.string.sending);

                }

                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.img_user.setImageBitmap(null);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_qr));
                holder.img_user.setForeground(null);

                holder.txt_content1.setVisibility(View.VISIBLE);
                holder.txt_content1.setText(content1);
                holder.txt_content1.setTextColor(context.getResources().getColor(R.color.gray));
                holder.txt_content1.setTextSize(10);

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }
            case Constants.WALLET_INVOICE: {

                holder.linear_message.setVisibility(View.VISIBLE);

                holder.txt_pib.setVisibility(View.GONE);

                float f_value = Float.parseFloat(entityModel.value);
                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(Utility.dpToPx(60), 0, Utility.dpToPx(30), 0);

                if (entityModel.to_user != null && entityModel.from_user != null) {

                    if (ownUUID.equals(entityModel.from_user.uuid)) {
                        userImg = entityModel.to_user.avatar;
                        userImgEmo = entityModel.to_user.avatar_temp;
                        username = entityModel.to_user.username;

                        if (entityModel.status.equals(Constants.INVOICE_REQUESTED)) {
                            content = String.format("%s @%s", context.getString(R.string.you_sent_request_to), username);
                        } else if (entityModel.status.equals(Constants.INVOICE_ACCEPTED)) {
                            content = String.format("@%s %s", username, context.getString(R.string.confirmed_your_request));
                        } else if (entityModel.status.equals(Constants.INVOICE_REJECTED)) {
                            content = String.format("@%s %s", username, context.getString(R.string.canceled_your_request));
                        }

                        value = String.valueOf(f_value);

                        holder.linear_message.setLayoutParams(layoutParams);

                    } else {
                        userImg = entityModel.from_user.avatar;
                        userImgEmo = entityModel.from_user.avatar_temp;
                        username = entityModel.from_user.username;

                        if (entityModel.status.equals(Constants.INVOICE_REQUESTED)) {

                            content = String.format("@%s %s", username, context.getString(R.string.sent_you_request));
                            holder.linear_action.setVisibility(View.VISIBLE);

                        } else if (entityModel.status.equals(Constants.INVOICE_ACCEPTED)) {

                            content = String.format("%s @%s", context.getString(R.string.you_confirmed_request_from), username);


                            holder.linear_message.setLayoutParams(layoutParams);

                        } else if (entityModel.status.equals(Constants.INVOICE_REJECTED)) {

                            holder.linear_message.setLayoutParams(layoutParams);

                            content = String.format("%s @%s", context.getString(R.string.you_cancelled_request_from), username);
                        }

                        value = String.valueOf(f_value);

                    }

                }

                if (!userImg.equals("null")) {
                    this.imageLoader.displayImage(userImg, holder.img_user);
                    holder.txt_emo.setVisibility(View.INVISIBLE);

                } else {
                    holder.txt_emo.setVisibility(View.VISIBLE);
                    holder.img_user.setImageDrawable(null);
                    holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                    holder.txt_emo.setText(Utility.getUserEmoName(username));
                }

                holder.txt_content.setText(content);
                holder.txt_pibvalue.setText(String.format("%s %s", value, entityModel.coin));
                if (entityModel.description.length() > 0) {
                    holder.txt_message.setVisibility(View.VISIBLE);
                    holder.txt_message.setText(entityModel.description);
                }

                break;
            }
            case Constants.WALLET_PROMOTION: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                switch (entityModel.type) {

                    case Constants.PROMOTION_DEBIT:

                        content = context.getString(R.string.promote_v) + " " + entityModel.coin;
                        value = "- " + String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_DEBIT:

                        content = context.getString(R.string.wallet_promotion_budget);
                        value = "- " + String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_promotion_mark_circle));
                        holder.img_user.setForeground(null);
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_IMPRESSION:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_impression) + " @" + entityModel.from_user.username;
//                        } else

                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_impression) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_impression);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_UPVOTE:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_upvote) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_upvote) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_upvote);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_COMMENT:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_comment) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_comment) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_comment);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_COLLECT:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_collect) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_collect) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_collect);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_FOLLOW:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_follow) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_follow) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_follow);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_ACTION_BUTTON:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_action_button) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_action_button) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_action_button);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_FOLLOW_TAG:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_tag_follow) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_tag_follow) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_tag_follow);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    case Constants.PROMOTION_POST_PROFILE_VIEW:

//                        if (entityModel.from_user != null) {
//                            content = context.getString(R.string.promotion_reward_profile_view) + " @" + entityModel.from_user.username;
//                        } else
                        if (entityModel.from_user != null) {
                            content = context.getString(R.string.promotion_reward_profile_view) + " @" + entityModel.from_user.username;
                        } else {
                            content = context.getString(R.string.promotion_reward_profile_view);
                        }

                        value = String.valueOf(f_value);
                        holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                        break;
                    default:

                        if (entityModel.from_user != null) {

                            userImg = entityModel.from_user.avatar;
                            userImgEmo = entityModel.from_user.avatar_temp;
                            username = entityModel.from_user.username;
                            content = context.getString(R.string.got_promote_post_reward);
                            value = String.valueOf(f_value);

                            if (!userImg.equals("null")) {
                                this.imageLoader.displayImage(userImg, holder.img_user);
                                holder.txt_emo.setVisibility(View.INVISIBLE);

                            } else {
                                holder.txt_emo.setVisibility(View.VISIBLE);
                                holder.img_user.setImageDrawable(null);
                                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                                holder.txt_emo.setText(Utility.getUserEmoName(username));
                            }

                        }

                        break;
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_FUNDING_CHARITY: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                if (entityModel.postsModel != null) {
                    content = context.getString(R.string.charity_funding_to) + " @" + entityModel.postsModel.user.username;
                }

                value = "- " + String.valueOf(f_value);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_charity_mark_circle));
                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_FUNDING_CHARITY_RESULT:
            case Constants.WALLET_FUNDING_CROWD_RESULT:
            case Constants.WALLET_FUNDING_REWARD_CROWD_RESULT:{

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                if (entityModel.funding_status) {

                    switch (entityModel.entity_type) {

                        case Constants.WALLET_FUNDING_CHARITY_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_charity_mark_circle));
                            content = context.getString(R.string.collected_from_charity);

                            break;
                        case Constants.WALLET_FUNDING_CROWD_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_noreward_circle));
                            content = context.getString(R.string.collected_from_crowd);

                            break;
                        case Constants.WALLET_FUNDING_REWARD_CROWD_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_hasreward_circle));
                            content = context.getString(R.string.collected_from_reward_crowd);

                            break;
                    }

                    value = String.valueOf(f_value);

                } else {

                    switch (entityModel.entity_type) {

                        case Constants.WALLET_FUNDING_CHARITY_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_charity_mark_circle));
                            content = context.getString(R.string.charity_funding_back);

                            break;
                        case Constants.WALLET_FUNDING_CROWD_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_noreward_circle));
                            content = context.getString(R.string.crowd_funding_back);

                            break;
                        case Constants.WALLET_FUNDING_REWARD_CROWD_RESULT:

                            holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_hasreward_circle));
                            content = context.getString(R.string.reward_crowd_funding_back);

                            break;
                    }

                    value = "- " + String.valueOf(f_value);
                }

                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_FUNDING_CROWD: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                if (entityModel.postsModel != null) {
                    content = context.getString(R.string.crowd_funding_to) + " @" + entityModel.postsModel.user.username;
                }

                value = "- " + String.valueOf(f_value);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_noreward_circle));
                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_FUNDING_REWARD_CROWD: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                if (entityModel.postsModel != null) {
                    content = context.getString(R.string.reward_crowd_funding_to) + " @" + entityModel.postsModel.user.username;
                }

                value = "- " + String.valueOf(f_value);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_funding_mark_hasreward_circle));
                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_FUNDING: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                if (entityModel.postsModel != null) {

                    userImg = entityModel.postsModel.user.avatar;
                    userImgEmo = entityModel.postsModel.user.avatar_temp;
                    username = entityModel.postsModel.user.username;

                }

                content = context.getString(R.string.charity_funding) + " " + entityModel.coin + " " + context.getString(R.string.to) + " @" + username;
                value = "- " + String.valueOf(f_value);

                if (!userImg.equals("null")) {
                    this.imageLoader.displayImage(userImg, holder.img_user);
                    holder.txt_emo.setVisibility(View.INVISIBLE);

                } else {
                    holder.txt_emo.setVisibility(View.VISIBLE);
                    holder.img_user.setImageDrawable(null);
                    holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                    holder.txt_emo.setText(Utility.getUserEmoName(username));
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_ASKING_HELP_PREPAYMENT: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                content = context.getString(R.string.prepayment_for_answer);

                value = "- " + String.valueOf(f_value);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_asking_mark_circle));
                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_ASKING_HELP_REWARD: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = this.context.getResources().getColor(R.color.colorMain);
                    holder.txt_pib.setTextColor(color);
                }

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                if (entityModel.from_user != null) {
                    content = context.getString(R.string.reward_for_answer) + " @" + entityModel.from_user.username;
                } else {
                    content = context.getString(R.string.reward_for_answer);
                }

                value = String.valueOf(f_value);
                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_asking_mark_circle));
                holder.img_user.setForeground(null);
                holder.txt_emo.setVisibility(View.INVISIBLE);
                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_ANSWER_UPVOTE: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                if (entityModel.from_user != null && entityModel.to_user != null) {

                    if (entityModel.from_user.uuid.equals(ownUUID)) {

                        content = context.getString(R.string.upvote_answer) + " @" + entityModel.to_user.username;
                        userImg = entityModel.to_user.avatar;
                        username = entityModel.to_user.username;
                        value = "- " + f_value;

                    } else if (entityModel.to_user.uuid.equals(ownUUID)) {

                        content = context.getString(R.string.got_upvote_answer) + " @" + entityModel.from_user.username;
                        userImg = entityModel.from_user.avatar;
                        username = entityModel.from_user.username;
                        value = String.valueOf(f_value);

                    }

                    if (!userImg.equals("null")) {
                        this.imageLoader.displayImage(userImg, holder.img_user);
                        holder.txt_emo.setVisibility(View.INVISIBLE);

                    } else {
                        holder.txt_emo.setVisibility(View.VISIBLE);
                        holder.img_user.setImageDrawable(null);
                        holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                        holder.txt_emo.setText(Utility.getUserEmoName(username));
                    }

                } else {

                    content = context.getString(R.string.upvote_answer);
                    value = String.valueOf(f_value);

                    holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_asking_mark_circle));
                    holder.img_user.setForeground(null);
                    holder.txt_emo.setVisibility(View.INVISIBLE);
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }

            case Constants.WALLET_GOODS: {

                holder.txt_pib.setVisibility(View.VISIBLE);

                if (entityModel.coin != null) {

                    int color = 0;
                    if (entityModel.coin.equals("PRB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletPink);
                    } else if (entityModel.coin.equals("PIB") || entityModel.coin.equals("PIBK")) {
                        color = this.context.getResources().getColor(R.color.colorMain);
                    } else if (entityModel.coin.equals("PGB")) {
                        color = this.context.getResources().getColor(R.color.colorWalletGreen);
                    }

                    holder.txt_pib.setTextColor(color);
                }

                float f_value = Float.parseFloat(entityModel.value);

                String content = "";
                String value = "";

                String userImg = "";
                String username = "";
                int userImgEmo = 0;

                if (entityModel.to_user != null && entityModel.from_user != null && entityModel.postsModel != null && entityModel.postsModel.goodsModel != null) {

                    if (entityModel.from_user.id == Utility.getReadPref(context).getIntValue(Constants.ID)) {

                        String str_type = "";

                        if (entityModel.type.equals(Constants.WALLET_GOODS_ESCROW)) {

                            str_type = context.getString(R.string.goods_buy_escrow);

                        } else if (entityModel.type.equals(Constants.WALLET_GOODS_PAY)) {

                            str_type = context.getString(R.string.goods_buy);

                        } else if (entityModel.type.equals(Constants.WALLET_GOODS_RETURN)) {

                            str_type = context.getString(R.string.goods_return); 

                        }

                        content = String.format("%s \"%s\" @%s", str_type, entityModel.postsModel.goodsModel.title, entityModel.to_user.username);
                        userImg = entityModel.to_user.avatar;
                        username = entityModel.to_user.username;
                        value = "- " + f_value;

                    } else {

                        String str_type = "";

                        if (entityModel.type.equals(Constants.WALLET_GOODS_ESCROW)) {

                            str_type = context.getString(R.string.goods_buy_escrow);
                            value = String.valueOf(f_value * (100 - entityModel.fee) / 100);

                        } else if (entityModel.type.equals(Constants.WALLET_GOODS_PAY)) {

                            str_type = context.getString(R.string.goods_sell);
                            value = String.valueOf(f_value * (100 - entityModel.fee) / 100);

                        } else if (entityModel.type.equals(Constants.WALLET_GOODS_RETURN)) {

                            str_type = context.getString(R.string.goods_return);
                            value = String.valueOf(f_value);

                        }

                        content = String.format("%s \"%s\" @%s", str_type, entityModel.postsModel.goodsModel.title, entityModel.from_user.username);
                        userImg = entityModel.from_user.avatar;
                        username = entityModel.from_user.username;

                    }

                }

                if (!userImg.equals("null")) {
                    this.imageLoader.displayImage(userImg, holder.img_user);
                    holder.txt_emo.setVisibility(View.INVISIBLE);

                } else {
                    holder.txt_emo.setVisibility(View.VISIBLE);
                    holder.img_user.setImageDrawable(null);
                    holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[userImgEmo]));
                    holder.txt_emo.setText(Utility.getUserEmoName(username));
                }

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", value, entityModel.coin));

                break;
            }
            case Constants.WALLET_3RFEXCHANGE: {

                holder.txt_content1.setVisibility(View.VISIBLE);

                imageLoader.displayImage(entityModel.from_icon, holder.img_user);

                float toVal = Float.parseFloat(entityModel.to_value);

                @SuppressLint("DefaultLocale") String content = String.format("%s %s", context.getString(R.string.exchanged_with), entityModel.from_symbol);
                String content1 = "@" + entityModel.fromApp_name;

                int toColor = 0;
                if (entityModel.to_symbol.equals("PRB")) {
                    toColor = this.context.getResources().getColor(R.color.colorWalletPink);
                } else if (entityModel.to_symbol.equals("PIB") || entityModel.to_symbol.equals("PIBK")) {
                    toColor = this.context.getResources().getColor(R.color.colorMain);
                } else if (entityModel.to_symbol.equals("PGB")) {
                    toColor = this.context.getResources().getColor(R.color.colorWalletGreen);
                }

                holder.txt_content.setText(content);
                holder.txt_content1.setText(content1);
                holder.txt_pib.setText(String.format("%s %s", String.valueOf(toVal), entityModel.to_symbol));
                holder.txt_pib.setTextColor(toColor);

                break;
            }
            case Constants.WALLET_AIRDROP: {

                holder.img_user.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_wallet_activity_pibble));

                float toVal = Float.parseFloat(entityModel.value);

                String content = context.getString(R.string.airdrop);

                holder.txt_content.setText(content);
                holder.txt_pib.setText(String.format("%s %s", String.valueOf(toVal), entityModel.coin));
                holder.txt_pib.setTextColor(context.getResources().getColor(R.color.colorMain));

                break;
            }
        }

        holder.txt_time.setText(Utility.getTimeAgo(entityModel.created_at));
    }

    @Override
    public int getItemCount() {

        if (pData.size() == 0) return 0;
        return pData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
        TextView txt_content, txt_content1, txt_pib, txt_time, txt_message, txt_emo, txt_pibvalue, txt_txid;
        Button btn_cancel, btn_confirm;
        LinearLayout linear_message, linear_action, linear_txid, linear_copied;

        ViewHolder(View itemView) {

            super(itemView);

            img_user = (ImageView)itemView.findViewById(R.id.img_user);
            txt_content = (TextView)itemView.findViewById(R.id.txt_content);
            txt_content1 = (TextView)itemView.findViewById(R.id.txt_content1);
            txt_pib = (TextView)itemView.findViewById(R.id.txt_pib);
            txt_time = (TextView)itemView.findViewById(R.id.txt_time);
            txt_message = (TextView)itemView.findViewById(R.id.txt_message);
            btn_cancel = (Button)itemView.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(this);
            btn_confirm = (Button)itemView.findViewById(R.id.btn_confirm);
            btn_confirm.setOnClickListener(this);
            linear_message = (LinearLayout)itemView.findViewById(R.id.linear_message);
            linear_action = (LinearLayout)itemView.findViewById(R.id.linear_action);
            linear_txid = (LinearLayout)itemView.findViewById(R.id.linear_txid);
            linear_copied = (LinearLayout)itemView.findViewById(R.id.linear_copied);
            txt_txid = (TextView)itemView.findViewById(R.id.txt_txid);
            txt_emo = (TextView)itemView.findViewById(R.id.txt_emo);
            txt_pibvalue = (TextView)itemView.findViewById(R.id.txt_pibvalue);

        }

        @Override
        public void onClick(View view) {
            if (activityListListener != null) {

                if (view == btn_confirm) {
                    activityListListener.onClickRequestAccept(getAdapterPosition());
                } else if (view == btn_cancel) {
                    activityListListener.onClickRequestCancel(getAdapterPosition());
                }
            }
        }
    }

    EntityModel getItem(int id) {
        return pData.get(id);
    }

    public void setClickListener(ActivityListener itemClickListener) {
        this.activityListListener = itemClickListener;
    }

    public interface ActivityListener {
        void onClickRequestAccept(int position);
        void onClickRequestCancel(int position);
    }
}
