package com.star.pibbledev.profile.photopicker.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component.SelectableAdapter;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PickerGridAdaptar extends SelectableAdapter<PickerGridAdaptar.PhotoViewHolder, Photo> {

    public interface OnItemClickPhotoGridListener {
        void onSelected(Photo photo);

        void onUnSelected(Photo photo);
    }

    private final Context context;
    // private int imageSize;

    private static final int MAX_SELECTION_NUMBER = 50;
    private final static int ITEM_TYPE_CAMERA = 100;
    private final static int ITEM_TYPE_PHOTO = 101;

    private HashMap<Integer, Integer> mMap = new HashMap<>();
    private int mSelectPosition = 0;

    private PickerGridAdaptar.OnItemClickPhotoGridListener mItemClickPhotoGridListener;

    PickerGridAdaptar(Context context, ArrayList<Photo> photos, ArrayList<String> selectedPaths) {
        super(photos, selectedPaths);
        this.context = context;

        //    setColumnNumber(context);
    }

    void setItemClickPhotoGridListener(PickerGridAdaptar.OnItemClickPhotoGridListener listener) {
        this.mItemClickPhotoGridListener = listener;
    }

    @NonNull
    @Override
    public PickerGridAdaptar.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_photoeditor_grid_photo, parent, false);
        return new PickerGridAdaptar.PhotoViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? ITEM_TYPE_PHOTO : ITEM_TYPE_PHOTO;
    }

    private void uncheck(int number) {
        if (mMap.isEmpty()) {
            onSelectChanged(0);
            return;
        }

        int biggestPosition = 0;
        int biggestValue = -1;

        for (Map.Entry<Integer, Integer> integerIntegerEntry : mMap.entrySet()) {
            Integer key = integerIntegerEntry.getKey();
            Integer value = integerIntegerEntry.getValue();

            if (key == null || value == null) {
                continue;
            }
            int newValue = value;
            if (value >= number) {
                newValue = value - 1;
                mMap.put(key, newValue);
            }

            if (newValue >= biggestValue) {
                biggestValue = newValue;
                biggestPosition = key;
            }
        }
        mSelectPosition = biggestPosition - 1;
        notifyDataSetChanged();
    }

    private void onSelectChanged(int position) {

        notifyDataSetChanged();

        if (mSelectPosition == position) {
            return;
        }

        mSelectPosition = position;
    }

    @Override
    public void onBindViewHolder(@NonNull final PickerGridAdaptar.PhotoViewHolder holder, final int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            final Photo photo = getItems().get(position);

            final Uri imageUri = Uri.fromFile(new File(photo.getPath()));

            Glide.with(this.context)
                    .load(imageUri)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getSelection(photo);

                    int adapterPosition = holder.getAdapterPosition();
                    if (isPhotoSelected(adapterPosition)) {
                        holder.img_checking.setImageResource(R.drawable.img_uncheck);
                        int number = mMap.remove(adapterPosition);
                        uncheck(number);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemClickPhotoGridListener != null) {
                                    mItemClickPhotoGridListener.onUnSelected(photo);
                                }
                            }
                        }, 20);

                    } else if (mMap.size() < MAX_SELECTION_NUMBER) {

                        mMap.clear();
                        mMap.put(adapterPosition, mMap.size() + 1);
                        holder.img_checking.setImageResource(R.drawable.img_check);
                        onSelectChanged(adapterPosition);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemClickPhotoGridListener != null) {
                                    mItemClickPhotoGridListener.onSelected(photo);
                                }
                            }
                        }, 20);

                    }

                }
            });

            if (isPhotoSelected(position)) {
                holder.img_checking.setImageResource(R.drawable.img_check);
            } else {
                holder.img_checking.setImageResource(R.drawable.img_uncheck);
            }

        }
    }

    private boolean isPhotoSelected(int position) {
        return mMap.containsKey(position);
    }

    /*private void setColumnNumber(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / 3;
    }*/

    @Override
    public int getItemCount() {
        if (getItems().size() > 0) {
            return getItems().size();
        }
        return getItems().size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, img_checking;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            img_checking = itemView.findViewById(R.id.img_checking);
        }
    }
}
