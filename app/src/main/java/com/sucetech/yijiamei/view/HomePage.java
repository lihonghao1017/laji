package com.sucetech.yijiamei.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.FilterObj;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.PageRestoreData;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.XiaoQuBean;
import com.sucetech.yijiamei.provider.BluthConnectTool;
import com.sucetech.yijiamei.widget.BluthDailog;
import com.sucetech.yijiamei.widget.XiaoQuDailog;

public class HomePage extends BasePage implements OnClickListener, BluthConnectTool.BluthStutaListener {
    private final static String TAG = "HomePage";
    private Context mContext;
    private ActivityInterface mAif;
    public BluthConnectTool bluthConnectTool;
    public int BluthStuta;
    private BluthDailog bluthDailog;
    public XiaoQuBean XiaoQuBean;
    private View camore,voice,commit;
    private View tabwei01,tabwei02;
    private TextView wei;

    public HomePage(Context context, View view, ActivityInterface aif) {
        super(context, view, aif);

        mContext = context;
        mAif = aif;
        bluthConnectTool=new BluthConnectTool(context,this);
        bluthDailog=new BluthDailog(context,this);
        if (BluthStuta!=4){
            bluthDailog.show();
        }
        camore=view.findViewById(R.id.bottom01);
        voice=view.findViewById(R.id.bottom02);
        commit=view.findViewById(R.id.bottom03);
        tabwei01=view.findViewById(R.id.chenzhong);
        tabwei02=view.findViewById(R.id.chenzhong2);
        camore.setOnClickListener(this);
        voice.setOnClickListener(this);
        commit.setOnClickListener(this);
        tabwei01.setOnClickListener(this);
        tabwei02.setOnClickListener(this);
        wei=view.findViewById(R.id.weiText);
    }

    @Override
    public void setFilterObj(int flag, FilterObj filter) {
        super.setFilterObj(flag, filter);
    }

    @Override
    public int getMyViewPosition() {
        return Configs.VIEW_POSITION_Main;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom01:
                break;
            case R.id.bottom02:
                break;
            case R.id.bottom03:
                break;
            case R.id.chenzhong:
                break;
            case R.id.chenzhong2:
                break;
        }
//        mAif.showPage(this.getMyViewPosition(), Configs.VIEW_POSITION_Login, null);
    }

    @Override
    public void onBluthStutaListener(int type, Object obj) {
        BluthStuta=type;
        switch (type) {
            case weied:
                Log.e("LLL","onBluthStutaListener--->"+(String)obj);
                final String we=(String)obj;
                        wei.setText(we+"");
                break;
            case coned:
                bluthDailog.setBluthConOk((String)obj);
                new XiaoQuDailog(mContext,this).show();
                break;
            case coning:
                break;
            case failed:
                bluthDailog.setBluthConFailed((String)obj);
                break;
        }

    }
}
