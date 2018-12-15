package com.sucetech.yijiamei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.XiaoQuBean;

import java.util.List;

public class XiaoQuAdapter extends BaseAdapter {
    private List<XiaoQuBean> deviceList;
    private LayoutInflater inflater;

    public XiaoQuAdapter(Context context, List<XiaoQuBean> deviceList){
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
            convertView = inflater.inflate(R.layout.xiaoqu_item_layout, null);
            holder.name = (TextView) convertView.findViewById(R.id.text1);
            holder.statuStr = (TextView) convertView.findViewById(R.id.tatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(deviceList.get(position).name );
        return convertView;
    }
    class ViewHolder{
        TextView name;
        TextView statuStr;
    }
}
