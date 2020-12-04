package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.star.pibbledev.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by droidNinja on 29/07/16.
 */
public class PhotoHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<String> paths;
    private final Context mContext;

    public PhotoHorizontalAdapter(Context context, ArrayList<String> paths) {
        this.paths = paths;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View addView = LayoutInflater.from(mContext).inflate(R.layout.item_add_layout, parent, false);
            return new AddViewHolder(addView);
        }

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FileViewHolder) {
            String path = paths.get(position);
            // FrescoFactory.getLoader().showImage(((FileViewHolder) holder).imageView, Uri.fromFile(new File(path)), FrescoFactory.newOption(imageSize, imageSize));
            ((FileViewHolder) holder).mPlayBtn.setVisibility(path.endsWith(".mp4") ? View.VISIBLE : View.GONE);


//            ((FileViewHolder) holder).imageView.setImageURI(Uri.fromFile(new File(path)));
            Glide.with(mContext).load(Uri.fromFile(new File(path))).into(((FileViewHolder) holder).imageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return paths.size() == position ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return paths.size() + 1;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView imageView;

        @BindView(R.id.play_btn)
        View mPlayBtn;

        FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        SimpleDraweeView image;

        AddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(String.valueOf(R.drawable.add))
                    .build();
            image.setImageURI(uri);
        }

        @OnClick(R.id.image)
        void onClickAdd(View v) {
            if (v.getContext() instanceof Activity) {
                ((Activity) v.getContext()).finish();
            }
        }
    }

    public void setData(List<String> list) {
        paths.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<String> getItems() {
        return paths;
    }

}
