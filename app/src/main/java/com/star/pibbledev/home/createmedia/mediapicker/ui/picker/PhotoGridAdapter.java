package com.star.pibbledev.home.createmedia.mediapicker.ui.picker;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.star.pibbledev.R;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.component.SelectableAdapter;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper.PostMediaHelper;
import com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models.Photo;
import com.star.pibbledev.services.global.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder, Photo> {

    public interface OnItemClickPhotoGridListener {
        void onSelected(Photo photo, int position);

        void onUnSelected(Photo photo, int position, int number);
    }

    private final Context context;
    // private int imageSize;

    private static final int MAX_SELECTION_NUMBER = 50;
    private final static int ITEM_TYPE_CAMERA = 100;
    private final static int ITEM_TYPE_PHOTO = 101;

    private HashMap<Integer, Integer> mMap;
    private int mSelectPosition = 0;

    private OnItemClickPhotoGridListener mItemClickPhotoGridListener;

    PhotoGridAdapter(Context context, ArrayList<Photo> photos, ArrayList<String> selectedPaths, HashMap<Integer, Integer> mMap) {
        super(photos, selectedPaths);
        this.context = context;
        this.mMap = mMap;

        //    setColumnNumber(context);
    }

    void setItemClickPhotoGridListener(OnItemClickPhotoGridListener listener) {
        this.mItemClickPhotoGridListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_layout, parent, false);
        return new PhotoViewHolder(itemView);
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
        if (mSelectPosition == position) {
            return;
        }

        mSelectPosition = position;
//        notifyItemChanged(0);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            final Photo photo = getItems().get(position);

            final Uri imageUri = Uri.fromFile(new File(photo.getPath()));

            Glide.with(this.context)
                    .load(imageUri)
                    .into(holder.imageView);

            List<String> params = Arrays.asList(photo.getMimeType().split("/"));

            if (params.size() > 0 && params.get(0).toLowerCase().equals("video")) {

                holder.txt_videolenth.setVisibility(View.VISIBLE);

                holder.txt_videolenth.setText(Utility.getTiemFromMS(photo.getDuration()));

            } else {

                holder.txt_videolenth.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ClipboardUtil.copyToClipboard(v.getContext(), photo.getPath());
                    toggleSelection(photo);
                    int adapterPosition = holder.getAdapterPosition();

                    if (isPhotoSelected(adapterPosition)) {
                        holder.checkBox2.setSelected(false);
                        holder.textCheckBox.setText("");
                        int number = mMap.remove(adapterPosition);
                        uncheck(number);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemClickPhotoGridListener != null) {
                                    mItemClickPhotoGridListener.onUnSelected(photo, adapterPosition, number);
                                }
                            }
                        }, 20);

                    } else if (mMap.size() < MAX_SELECTION_NUMBER) {

                        if (params.size() > 0 && params.get(0).toLowerCase().equals("video")) {

                            if (photo.getDuration() > 60000) {

                                new AlertView(context.getString(R.string.oh_snap),context.getString(R.string.video_length_msg),  null, new String[]{context.getString(R.string.cancel), context.getString(R.string.ok)}, null,
                                        context, AlertView.Style.Alert, new OnItemClickListener(){
                                    public void onItemClick(Object o,int position){

                                        if (position == 1) {

                                            mMap.put(adapterPosition, PostMediaHelper.sCachePost.size() + 1);
                                            holder.checkBox2.setSelected(true);
                                            holder.textCheckBox.setText(String.valueOf(mMap.get(adapterPosition)));
                                            onSelectChanged(adapterPosition - 1);
                                            if (mItemClickPhotoGridListener != null) {
                                                mItemClickPhotoGridListener.onSelected(photo, adapterPosition);
                                            }
                                        }
                                    }

                                }).show();

                            } else {

                                mMap.put(adapterPosition, PostMediaHelper.sCachePost.size() + 1);
                                holder.checkBox2.setSelected(true);
                                holder.textCheckBox.setText(String.valueOf(mMap.get(adapterPosition)));
                                onSelectChanged(adapterPosition - 1);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mItemClickPhotoGridListener != null) {
                                            mItemClickPhotoGridListener.onSelected(photo, adapterPosition);
                                        }
                                    }
                                }, 20);
                            }
                        } else {

                            mMap.put(adapterPosition, PostMediaHelper.sCachePost.size() + 1);
                            holder.checkBox2.setSelected(true);
                            holder.textCheckBox.setText(String.valueOf(mMap.get(adapterPosition)));
                            onSelectChanged(adapterPosition - 1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mItemClickPhotoGridListener != null) {
                                        mItemClickPhotoGridListener.onSelected(photo, adapterPosition);
                                    }
                                }
                            }, 20);
                        }

                    }

                }
            });

            if (isPhotoSelected(position)) {
                holder.checkBox2.setSelected(true);
                holder.textCheckBox.setText(String.valueOf(mMap.get(position)));
            } else {
                holder.checkBox2.setSelected(false);
                holder.textCheckBox.setText("");
            }

            holder.checkBox2.setVisibility(View.VISIBLE);

        } else {

            holder.checkBox2.setVisibility(View.INVISIBLE);
            Photo photo = getItems().get(mSelectPosition);
//            holder.imageView.setImageURI(Uri.fromFile(new File(photo.getPath())));

            Glide.with(this.context)
                    .load(Uri.fromFile(new File(photo.getPath())))
                    .into(holder.imageView);
            //  FrescoFactory.getLoader().showImage(holder.imageView, Uri.fromFile(new File(photo.getPath())), FrescoFactory.newOption(imageSize, imageSize));
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

        ImageView imageView;
        View checkBox2;
        TextView textCheckBox, txt_videolenth;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            checkBox2 = itemView.findViewById(R.id.checkbox2);
            textCheckBox = itemView.findViewById(R.id.number);
            txt_videolenth = itemView.findViewById(R.id.txt_videolenth);
        }
    }
}
