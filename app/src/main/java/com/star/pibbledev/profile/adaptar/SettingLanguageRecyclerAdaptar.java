package com.star.pibbledev.profile.adaptar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;

public class SettingLanguageRecyclerAdaptar extends RecyclerView.Adapter<SettingLanguageRecyclerAdaptar.ViewHolder> {

    private String[] mData;
    private String[] mData_iso;
    private Context mContext;
    private LayoutInflater mInflater;
    private listListener mClickListener;

    public SettingLanguageRecyclerAdaptar(Context context, String[] data, String[] ios) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        mData_iso = ios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_profile_setting_langauge_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String value = mData[position];
        String value_iso = mData_iso[position];

        if (value_iso.equals(Utility.getReadPref(mContext).getStringValue(Constants.LANGUAGE))) {
            holder.txt_country.setTextColor(mContext.getResources().getColor(R.color.colorMain));
        } else {
            holder.txt_country.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        holder.txt_country.setText(value);
    }

    @Override
    public int getItemCount() {

        if (mData.length == 0) return 0;
        return mData.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_country;

        ViewHolder(View itemView) {

            super(itemView);

            txt_country = (TextView)itemView.findViewById(R.id.txt_country);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) {
                mClickListener.onClickCell(getAdapterPosition());
            }
        }
    }

    String getItem(int id) {
        return mData[id];
    }

    public void setClickListener(listListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface listListener {

        void onClickCell(int position);
    }
}
