package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.PhotoDirectory;

import java.util.List;

public class DirectoryGridAdaptar extends RecyclerView.Adapter<DirectoryGridAdaptar.ViewHolder> {

    private List<PhotoDirectory> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private DirectoryClickListener mClickListener;

    // data is passed into the constructor
    public DirectoryGridAdaptar(Context context, List<PhotoDirectory> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycle_directory, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PhotoDirectory data = mData.get(position);
        holder.txt_directory_name.setText(data.getName());
        holder.txt_directory_count.setText(String.valueOf(data.getPhotos().size()));

        Glide.with(this.mContext)
                .load(data.getCoverPath())
                .into(holder.img_cover);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_cover;
        TextView txt_directory_name, txt_directory_count;

        ViewHolder(View itemView) {

            super(itemView);

            txt_directory_name = itemView.findViewById(R.id.txt_directory_name);
            txt_directory_count = itemView.findViewById(R.id.txt_directory_count);
            img_cover = itemView.findViewById(R.id.img_cover);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    PhotoDirectory getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(DirectoryClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface DirectoryClickListener {
        void onItemClick(View view, int position);
    }
}
