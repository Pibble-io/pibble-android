package com.star.pibbledev.home.funding.adaptar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.FundingSupportersModel;

import java.util.ArrayList;

public class SupportListAdaptar extends RecyclerView.Adapter<SupportListAdaptar.ViewHolder> {

    private ArrayList<FundingSupportersModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private SupportListListener mClickListener;

    public SupportListAdaptar(Context context, ArrayList<FundingSupportersModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_funding_supporter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FundingSupportersModel supportersModel = mData.get(position);

        if (!supportersModel.userModel.avatar.equals("null") && !supportersModel.userModel.avatar.equals("")) {
            ImageLoader.getInstance().displayImage(supportersModel.userModel.avatar, holder.img_user);
            holder.txt_emo.setVisibility(View.GONE);
        } else {

            int value = 0;
            if (supportersModel.userModel.username.length() > 14) value = 14;
            else value = supportersModel.userModel.username.length();

            holder.img_user.setBackgroundColor(mContext.getResources().getColor(Utility.g_aryColors[value]));
            holder.txt_emo.setText(Utility.getUserEmoName(supportersModel.userModel.username));
        }

        holder.txt_name.setText(supportersModel.userModel.username);
        holder.txt_date.setText(Utility.getTimeAgo(supportersModel.created_at));

        if (supportersModel.price != null) holder.txt_price.setText(supportersModel.price);
        else holder.txt_price.setVisibility(View.INVISIBLE);

        holder.txt_value.setText(String.valueOf(supportersModel.value));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_user;
        TextView txt_emo, txt_name, txt_date, txt_price, txt_value;

        ViewHolder(View itemView) {

            super(itemView);

            txt_emo = itemView.findViewById(R.id.txt_emo);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_user = itemView.findViewById(R.id.img_user);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_value = itemView.findViewById(R.id.txt_value);

            img_user.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view == img_user) {
                if (mClickListener != null) mClickListener.onClickSupporter(getAdapterPosition());
            }

        }
    }

    FundingSupportersModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(SupportListListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface SupportListListener {
        void onClickSupporter(int position);
    }
}
