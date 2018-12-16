package com.sucetech.yijiamei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.LaJiBean;

import java.util.List;

public class LajiFenLeiAdapter extends BaseAdapter {
    private List<LaJiBean> deviceList;
    private LayoutInflater inflater;

    public LajiFenLeiAdapter(Context context, List<LaJiBean> deviceList){
        inflater=LayoutInflater.from(context);
        this.deviceList=deviceList;
    }
    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.laji_fenlei_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.text1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(deviceList.get(position).name );
        holder.name.setSelected(deviceList.get(position).isSelected);
        return convertView;
    }
    class ViewHolder{
        TextView name;
    }
}
