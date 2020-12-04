package com.star.pibbledev.wallet.paybil;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.EntityModel;

import java.util.ArrayList;

public class PaybilRecyclerAdaptar extends RecyclerView.Adapter<PaybilRecyclerAdaptar.ViewHolder> {

    private Context context;
    private ArrayList<EntityModel> pData;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater=null;
    private ActivityListener activityListListener;

    private String ownUUID;

    public PaybilRecyclerAdaptar(Context context, ArrayList<EntityModel> pData, ImageLoader imageLoader) {

        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.pData = pData;
        this.imageLoader = imageLoader;

        ownUUID = Utility.getReadPref(this.context).getStringValue("uuid");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_wallet_paybil, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EntityModel entityModel = pData.get(position);

        if (entityModel.from_user != null) {
            if (!entityModel.from_user.avatar.equals("null")) {
                this.imageLoader.displayImage(entityModel.from_user.avatar, holder.img_user);
                holder.txt_emo.setVisibility(View.INVISIBLE);

            } else {
                holder.txt_emo.setVisibility(View.VISIBLE);
                holder.img_user.setImageBitmap(null);
                holder.img_user.setBackgroundColor(context.getResources().getColor(Utility.g_aryColors[entityModel.from_user.avatar_temp]));
                holder.txt_emo.setText(Utility.getUserEmoName(entityModel.from_user.username));
            }

            holder.txt_content.setText(context.getString(R.string.payment_request_has_arrived) + " @" + entityModel.from_user.username);
        }

        holder.txt_pibvalue.setText(String.format("%s %s", entityModel.value, entityModel.coin));
        if (entityModel.description.length() > 0) {
            holder.txt_message.setVisibility(View.VISIBLE);
            holder.txt_message.setText(entityModel.description);
        } else {
            holder.txt_message.setVisibility(View.GONE);
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
        TextView txt_content, txt_content1, txt_pib, txt_time, txt_message, txt_emo, txt_pibvalue;
        Button btn_cancel, btn_confirm;
        LinearLayout linear_message, linear_action;

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
