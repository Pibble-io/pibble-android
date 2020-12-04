package com.star.pibbledev.home.createmedia.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.model.LocationModel;

import java.util.ArrayList;

public class LocationAdaptar extends BaseAdapter {
    Context context;
    ArrayList<LocationModel> pData;
    private static LayoutInflater inflater=null;

    public LocationAdaptar(Context context, ArrayList<LocationModel> pData) {
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
        TextView txt_place, txt_location;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_list_location, null);
            viewHolder.txt_place = (TextView)convertView.findViewById(R.id.txt_place);
            viewHolder.txt_location = (TextView)convertView.findViewById(R.id.txt_location);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_place.setText(getPlace(pData.get(position).description));

        if (getLocation(pData.get(position).description).equals("")) {
            viewHolder.txt_location.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txt_location.setVisibility(View.VISIBLE);
            viewHolder.txt_location.setText(getLocation(pData.get(position).description));
        }

        return convertView;
    }

    private String getPlace(String locationInfo) {

        String[] strings = locationInfo.split(",");

        return strings[0];
    }

    private String getLocation(String locationInfo) {
        String[] strings = locationInfo.split(",");
        String result = "";
        if (strings.length > 1) {
            for (int i = 1; i < strings.length; i++) {

                if (i == 1) {
                    result = strings[1];
                } else {
                    result = result + "," + strings[i];
                }

            }
        }
        return result;
    }

}
