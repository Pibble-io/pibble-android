package com.star.pibbledev.home.funding.adaptar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.model.CategoryModel;

import java.util.ArrayList;

public class FundingCategoryAdaptar extends BaseAdapter {
    Context context;
    ArrayList<CategoryModel> pData;
    private static LayoutInflater inflater=null;

    private int mSelectedCell = -1;
    private FundingCategoryListener categoryListener;

    public FundingCategoryAdaptar(Context context, ArrayList<CategoryModel> pData) {
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
        return pData.size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class ViewHolder
    {
        TextView txt_content;
        LinearLayout linear_background;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; // view lookup cache stored in tag

        viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.item_list_charity_who, null);

        viewHolder.txt_content = (TextView)convertView.findViewById(R.id.txt_content);
        viewHolder.linear_background = (LinearLayout)convertView.findViewById(R.id.linear_background);

        viewHolder.txt_content.setText(pData.get(position).name);


        if (mSelectedCell == position) {
            viewHolder.linear_background.setBackground(context.getResources().getDrawable(R.drawable.linear_category_select));
            viewHolder.txt_content.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            viewHolder.linear_background.setBackground(context.getResources().getDrawable(R.drawable.linear_category_unselect));
            viewHolder.txt_content.setTextColor(context.getResources().getColor(R.color.black));
        }

        viewHolder.linear_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (categoryListener != null) {
                    mSelectedCell = position;
                    categoryListener.onCategoryItemClick(view, mSelectedCell);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    public void setClickListener(FundingCategoryListener itemClickListener) {
        this.categoryListener = itemClickListener;
    }

    public interface FundingCategoryListener {
        void onCategoryItemClick(View view, int position);
    }

}
