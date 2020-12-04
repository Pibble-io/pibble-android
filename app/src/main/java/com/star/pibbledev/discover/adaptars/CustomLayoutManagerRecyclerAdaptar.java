package com.star.pibbledev.discover.adaptars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.star.pibbledev.R;
import com.star.pibbledev.discover.listeners.DiscoverLoadmoreListener;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.PostsModel;
import com.star.pibbledev.home.utility.videoview.VideoView;

import java.io.File;
import java.util.ArrayList;

public class CustomLayoutManagerRecyclerAdaptar extends RecyclerView.Adapter {

    private static final int TYPE_SQUARE = 0;
    private static final int TYPE_VERTICAL = 1;

    private CustomLayoutItemListener customLayoutItemListener;

    public static class SquareImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, img_type;

        VideoView gridVideoview;

        SquareImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gridImageview);
            img_type = itemView.findViewById(R.id.img_type);
            gridVideoview = itemView.findViewById(R.id.gridVideoview);
        }
    }

    public static class VerticalImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView1;
        ImageView imageView2;

        ImageView img_type1, img_type2;

        VideoView gridVideoview1, gridVideoview2;

        VerticalImagesViewHolder(View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.gridImageview1);
            imageView2 = itemView.findViewById(R.id.gridImageview2);
            img_type1 = itemView.findViewById(R.id.img_type1);
            img_type2 = itemView.findViewById(R.id.img_type2);
            gridVideoview1 = itemView.findViewById(R.id.gridVideoview1);
            gridVideoview2 = itemView.findViewById(R.id.gridVideoview2);

        }
    }

    public void updateData(ArrayList<PostsModel> images, int oldCount) {
        this.images.clear();
        distributeImages(images);
        notifyItemRangeChanged(oldCount, images.size() - 1);
    }

    public void refreshData(ArrayList<PostsModel> images) {

        this.images.clear();
        distributeImages(images);
        notifyDataSetChanged();
    }

    private static class MyImage {
        PostsModel postsModel;
        int span = 1;

        MyImage(PostsModel postsModel, int span) {
            this.postsModel = postsModel;
            this.span = span;
        }
    }

    private Context mContext;
    private ImageLoader imageLoader;
    private ArrayList<ArrayList<MyImage>> images = new ArrayList<>();
    private DiscoverLoadmoreListener loadmoreListener;

    public CustomLayoutManagerRecyclerAdaptar(Context context, DiscoverLoadmoreListener listener, ArrayList<PostsModel> images, ImageLoader imageLoader) {
        this.mContext = context;
        this.imageLoader = imageLoader;
        this.loadmoreListener = listener;
        distributeImages(images);
    }

    private void distributeImages(ArrayList<PostsModel> images) {
        ArrayList<MyImage> arrayOfImages = new ArrayList<>();
        int i = 0;
        int rightPos = 0;
        boolean left = true;
        int span;
        for (PostsModel image : images) {
            i++;
            span = 1;
            if (left) {
                if (i % 6 == 1) {
                    span = 2;
                    rightPos = i + 8;
                }
                arrayOfImages.add(new MyImage(image, span));
                if (i % 6 == 2) {
                    left = false;
                    continue;
                }
            } else {
                if (i % rightPos == 0) {
                    span = 2;
                    left = true;
                }
                arrayOfImages.add(new MyImage(image, span));
                if (i % rightPos == rightPos - 2) {
                    continue;
                }
            }
            this.images.add(arrayOfImages);
            arrayOfImages = new ArrayList<>();
        }

        if(arrayOfImages.size() > 0) {
            this.images.add(arrayOfImages);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);

        // Create a custom SpanSizeLookup where the first item spans both columns
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return images.get(position).get(0).span;
            }
        });

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadmoreListener.LoadmoreDatas(page + 1);
            }

            @Override
            public void onScrolledUp() {

            }

            @Override
            public void onScrolledDown() {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_VERTICAL) {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View view = inflater.inflate(R.layout.item_vertical_square_images, parent, false);
            VerticalImagesViewHolder holder = new VerticalImagesViewHolder(view);

            return holder;

        } else {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View view = inflater.inflate(R.layout.item_square_image, parent, false);
            SquareImageViewHolder holder = new SquareImageViewHolder(view);

            return holder;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ArrayList<MyImage> imgs = images.get(position);

        if (imgs.size() > 1) {

            VerticalImagesViewHolder myHolder = (VerticalImagesViewHolder) holder;

            myHolder.img_type1.setVisibility(View.INVISIBLE);
            myHolder.img_type2.setVisibility(View.INVISIBLE);
            myHolder.gridVideoview1.setVisibility(View.INVISIBLE);
            myHolder.gridVideoview2.setVisibility(View.INVISIBLE);
            myHolder.imageView1.setAlpha(1.0f);
            myHolder.imageView2.setAlpha(1.0f);

            if (imgs.get(0).postsModel.ary_media.get(0).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                myHolder.img_type1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_video));
                myHolder.img_type1.setVisibility(View.VISIBLE);

//                imageLoader.displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView1);
                displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView1);

                myHolder.gridVideoview1.setVisibility(View.VISIBLE);

                myHolder.gridVideoview1.setVideoPath(imgs.get(0).postsModel.ary_media.get(0).url);
                myHolder.gridVideoview1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview1.start();
                        myHolder.gridVideoview1.setMute(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                myHolder.imageView1.setVisibility(View.INVISIBLE);
                                myHolder.imageView1.setAlpha(0.0f);
                            }
                        }, 800);
                    }
                });

                myHolder.gridVideoview1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview1.start();
                    }
                });

                myHolder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(0).postsModel.id);
                    }
                });

//                myHolder.gridVideoview1.setTag(imgs.get(0).postsModel.ary_media.get(0).thumbnail);

            } else {

//                imageLoader.displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView1);
                displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView1);

                if (imgs.get(0).postsModel.ary_media.size() == 1) {
                    myHolder.img_type1.setVisibility(View.INVISIBLE);
                } else {
                    myHolder.img_type1.setVisibility(View.VISIBLE);
                    myHolder.img_type1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_multi_image));
                }

                myHolder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(0).postsModel.id);
                    }
                });

            }

            if (imgs.get(1).postsModel.ary_media.get(0).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                myHolder.img_type2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_video));
                myHolder.img_type2.setVisibility(View.VISIBLE);

//                imageLoader.displayImage(imgs.get(1).postsModel.ary_media.get(0).thumbnail, myHolder.imageView2);
                displayImage(imgs.get(1).postsModel.ary_media.get(0).thumbnail, myHolder.imageView2);

                myHolder.gridVideoview2.setVisibility(View.VISIBLE);

                myHolder.gridVideoview2.setVideoPath(imgs.get(1).postsModel.ary_media.get(0).url);
                myHolder.gridVideoview2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview2.start();
                        myHolder.gridVideoview2.setMute(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                myHolder.imageView2.setVisibility(View.INVISIBLE);
                                myHolder.imageView2.setAlpha(0.0f);
                            }
                        }, 800);
                    }
                });

                myHolder.gridVideoview2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview2.start();
                    }
                });

                myHolder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(1).postsModel.id);
                    }
                });

//                myHolder.gridVideoview2.setTag(imgs.get(1).postsModel.ary_media.get(0).thumbnail);

            } else {

//                imageLoader.displayImage(imgs.get(1).postsModel.ary_media.get(0).thumbnail, myHolder.imageView2);
                displayImage(imgs.get(1).postsModel.ary_media.get(0).thumbnail, myHolder.imageView2);

                if (imgs.get(1).postsModel.ary_media.size() == 1) {
                    myHolder.img_type2.setVisibility(View.INVISIBLE);
                } else {
                    myHolder.img_type2.setVisibility(View.VISIBLE);
                    myHolder.img_type2.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_multi_image));
                }

                myHolder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(1).postsModel.id);
                    }
                });

            }

//            imageLoader.displayImage(imgs.get(1).postsModel.ary_media.get(0).thumbnail, myHolder.imageView2);
//            myHolder.imageView2.setTag(imgs.get(1).postsModel.ary_media.get(0).thumbnail);

        } else {

            SquareImageViewHolder myHolder = (SquareImageViewHolder) holder;

            myHolder.img_type.setVisibility(View.INVISIBLE);
            myHolder.gridVideoview.setVisibility(View.INVISIBLE);
            myHolder.imageView.setAlpha(1.0f);

            if (imgs.get(0).postsModel.ary_media.get(0).mimetype.equals(Constants.VIDEO_MIMETYPE)) {

                myHolder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_video));
                myHolder.img_type.setVisibility(View.VISIBLE);

//                imageLoader.displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView);
                displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView);
                myHolder.gridVideoview.setVisibility(View.VISIBLE);

                myHolder.gridVideoview.setVideoPath(imgs.get(0).postsModel.ary_media.get(0).url);
                myHolder.gridVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview.start();
                        myHolder.gridVideoview.setMute(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                myHolder.imageView.setVisibility(View.INVISIBLE);
                                myHolder.imageView.setAlpha(0.0f);
                            }
                        }, 800);
                    }
                });

                myHolder.gridVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        myHolder.gridVideoview.start();
                    }
                });

                myHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(0).postsModel.id);
                    }
                });

//                myHolder.gridVideoview.setTag(imgs.get(0).postsModel.ary_media.get(0).thumbnail);

            } else {

//                imageLoader.displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView);
                displayImage(imgs.get(0).postsModel.ary_media.get(0).thumbnail, myHolder.imageView);

                if (imgs.get(0).postsModel.ary_media.size() == 1) {
                    myHolder.img_type.setVisibility(View.INVISIBLE);
                } else {
                    myHolder.img_type.setVisibility(View.VISIBLE);
                    myHolder.img_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_type_multi_image));
                }

                myHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customLayoutItemListener != null) customLayoutItemListener.CustomLayoutItemOnClick(imgs.get(0).postsModel.id);
                    }
                });

            }

        }
    }

    private void displayImage(String imagePath, ImageView imageView) {

        imageView.setImageBitmap(null);

        final File image = DiskCacheUtils.findInCache(imagePath, imageLoader.getDiskCache());
        if (image!= null && image.exists()) {
            Glide.with(mContext).load(image).into(imageView);
        } else {

//            imageLoader.displayImage(imagePath, imageView);

            ImageLoader.getInstance().loadImage(imagePath, new ImageSize(Utility.dpToPx(Utility.g_deviceWidth / 3), Utility.dpToPx(Utility.g_deviceWidth / 3)), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
//                    Glide.with(mContext).load(imageUri).into(imageView);
                    imageView.setImageBitmap(loadedImage);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (images.get(position).size() > 1) {
            return TYPE_VERTICAL;
        }

        return TYPE_SQUARE;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setCustomLayoutItemOnclickListener(CustomLayoutItemListener customLayoutItemOnclickListener) {
        this.customLayoutItemListener = customLayoutItemOnclickListener;
    }

    public interface CustomLayoutItemListener {
        public void CustomLayoutItemOnClick(int id);
    }
}
