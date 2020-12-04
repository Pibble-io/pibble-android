package com.star.pibbledev.home.createmedia.tags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.model.TagModel;

import java.util.ArrayList;

public class TagAdaptar extends BaseAdapter {
    Context context;
    ArrayList<TagModel> pData;
    private static LayoutInflater inflater=null;

    public TagAdaptar(Context context, ArrayList<TagModel> pData) {
        this.context = context;
        this.pData = pData;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return pData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return pData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class ViewHolder
    {
        TextView txt_tag, txt_posted;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_list_tags, null);
            viewHolder.txt_tag = (TextView)convertView.findViewById(R.id.txt_tag);
            viewHolder.txt_posted = (TextView)convertView.findViewById(R.id.txt_posted);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_tag.setText("#" + pData.get(position).tag);
        viewHolder.txt_posted.setText(String.valueOf(pData.get(position).posted) + " " + context.getString(R.string.posts));

        return convertView;
    }

}
