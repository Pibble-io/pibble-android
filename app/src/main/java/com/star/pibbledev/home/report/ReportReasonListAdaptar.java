package com.star.pibbledev.home.report;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.model.ReportReasonModel;

import java.util.ArrayList;

public class ReportReasonListAdaptar extends RecyclerView.Adapter<ReportReasonListAdaptar.ViewHolder> {

    private ArrayList<ReportReasonModel> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private listListener mClickListener;

    public ReportReasonListAdaptar(Context context, ArrayList<ReportReasonModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_report_reason, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReportReasonModel model = mData.get(position);

        holder.txt_reason.setText(model.label);
    }

    @Override
    public int getItemCount() {

        if (mData.size() == 0) return 0;
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_reason;

        ViewHolder(View itemView) {

            super(itemView);

            txt_reason = itemView.findViewById(R.id.txt_reason);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) {
                mClickListener.onClickCell(getAdapterPosition());
            }
        }
    }

    ReportReasonModel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(listListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface listListener {

        void onClickCell(int position);
    }
}
