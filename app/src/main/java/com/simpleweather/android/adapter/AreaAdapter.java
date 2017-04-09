package com.simpleweather.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simpleweather.android.R;
import com.simpleweather.android.application.MyApplication;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/28.
 */

public class AreaAdapter  extends BaseAdapter{
    private ArrayList<String> dataList;
    public AreaAdapter(ArrayList<String> dataList){
        this.dataList=dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.area_item,null);
            holder.textView= (TextView) convertView.findViewById(R.id.txt_area_iten);
            convertView.setTag(holder);

        }
        else {
            holder= (ViewHolder) convertView.getTag();
            holder.textView.setText(dataList.get(position));
        }
        return convertView;
    }
    private class ViewHolder{
        TextView textView;
    }
}
