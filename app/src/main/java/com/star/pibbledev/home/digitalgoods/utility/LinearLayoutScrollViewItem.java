package com.star.pibbledev.home.digitalgoods.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.pibbledev.R;

@SuppressLint("ViewConstructor")
public class LinearLayoutScrollViewItem extends LinearLayout {

    @SuppressLint("SetTextI18n")
    public LinearLayoutScrollViewItem(Context context, ImageLoader imageLoader, String imagePath, String resolution, String dpi, String format) {

        super(context);

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_digitalgoods_commerce_horizontalscrollview, this, true);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView txt_resolution = (TextView)view.findViewById(R.id.txt_resolution);
        TextView txt_dpi = (TextView) view.findViewById(R.id.txt_dpi);
        TextView txt_format = (TextView) view.findViewById(R.id.txt_format);

        imageLoader.displayImage(imagePath, imageView);

        txt_resolution.setText(getContext().getString(R.string.resolution) + " " + resolution + " " + getContext().getString(R.string.pixel));
        txt_dpi.setText(getContext().getString(R.string.dpi) + " " + dpi);
        txt_format.setText(getContext().getString(R.string.files_format) + " " + format);

    }
}
