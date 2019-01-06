package com.sucetech.yijiamei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.JiLuBean;

import java.util.List;

public class HuishouAdapter extends BaseAdapter {
    private List<JiLuBean> deviceList;
    private LayoutInflater inflater;

    public HuishouAdapter(Context context, List<JiLuBean> deviceList){
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
            convertView = inflater.inflate(R.layout.huishoujilu_layout_item, null);
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.card = (TextView) convertView.findViewById(R.id.card);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText("账户:"+deviceList.get(position).residentName );
        if (deviceList.get(position).cellphone!=null&&!deviceList.get(position).cellphone.equals(""))
        holder.card.setText("电话:"+deviceList.get(position).cellphone );
        else
            holder.card.setText("");
        holder.type.setText(deviceList.get(position).typeName+":"+deviceList.get(position).weight+"kg" );
        holder.day.setText("日期:"+deviceList.get(position).dateTime );
        return convertView;
    }
    class ViewHolder{
        TextView type;
        TextView userName;
        TextView card;
        TextView day;
    }
}
