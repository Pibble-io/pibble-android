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

public class SettingCurrenciesRecyclerAdaptar extends RecyclerView.Adapter<SettingCurrenciesRecyclerAdaptar.ViewHolder> {

    private String[] mData;
    private String[] mData_currency;
    private Context mContext;
    private LayoutInflater mInflater;
    private listListener mClickListener;

    public SettingCurrenciesRecyclerAdaptar(Context context, String[] country, String[] currency) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = country;
        this.mContext = context;
        mData_currency = currency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_profile_setting_currency_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String value = mData[position];
        String value_currency = mData_currency[position];

        if (value_currency.toUpperCase().equals(Utility.getReadPref(mContext).getStringValue(Constants.CURRENCY).toUpperCase())) {
            holder.txt_country.setTextColor(mContext.getResources().getColor(R.color.colorMain));
            holder.txt_currency.setTextColor(mContext.getResources().getColor(R.color.colorMain));
        } else {
            holder.txt_country.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.txt_currency.setTextColor(mContext.getResources().getColor(R.color.light_gray));
        }

        holder.txt_country.setText(value);
        holder.txt_currency.setText(value_currency);
    }

    @Override
    public int getItemCount() {

        if (mData.length == 0) return 0;
        return mData.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_country, txt_currency;

        ViewHolder(View itemView) {

            super(itemView);

            txt_country = (TextView)itemView.findViewById(R.id.txt_country);
            txt_currency = (TextView)itemView.findViewById(R.id.txt_currency);
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
