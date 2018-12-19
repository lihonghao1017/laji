package com.sucetech.yijiamei.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.FilterObj;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.PageRestoreData;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.model.JuMinBean;
import com.sucetech.yijiamei.model.LaJiBean;
import com.sucetech.yijiamei.model.XiaoQuBean;
import com.sucetech.yijiamei.provider.BluthConnectTool;
import com.sucetech.yijiamei.provider.NFCTool;
import com.sucetech.yijiamei.widget.BluthDailog;
import com.sucetech.yijiamei.widget.CommitView;
import com.sucetech.yijiamei.widget.JuMinDialog;
import com.sucetech.yijiamei.widget.XiaoQuDailog;

import java.util.Arrays;
import java.util.List;

public class HomePage extends BasePage implements OnClickListener, BluthConnectTool.BluthStutaListener {
    private final static String TAG = "HomePage";
    private Context mContext;
    private ActivityInterface mAif;
    public BluthConnectTool bluthConnectTool;
    public int BluthStuta;
    private BluthDailog bluthDailog;
    public XiaoQuBean XiaoQuBean;
    private View tabwei01, tabwei02,back;
    private TextView wei;
    private View tabCursor01, tabCursor02;
    public JuMinBean juMinBean;
    private JuMinDialog juMinDialog;
    public boolean isOneWEi=true;
    private CommitView commitView;
    public List<LaJiBean> data;

    private Handler mHandler=new Handler();
    private void  sendTime(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBluthStutaListener(weied,"99");
                sendTime();
            }
        },1000*2);
    }

    public HomePage(Context context, View view, ActivityInterface aif) {
        super(context, view, aif);

        mContext = context;
        mAif = aif;
        bluthConnectTool = new BluthConnectTool(context, this);
        bluthDailog = new BluthDailog(context, this);
        if (BluthStuta != 4) {
            bluthDailog.show();
        }
        bluthDailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                XiaoQuDailog xiaoQuDailog= new XiaoQuDailog(mContext, HomePage.this);
                xiaoQuDailog.show();
                xiaoQuDailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        juMinBean= new JuMinBean();
                        juMinBean.carNub="2018121901234566";
                        juMinBean.name="张小三";
                        juMinBean.phone="18701161788";
                        juMinDialog=new JuMinDialog(mContext,HomePage.this);
                        juMinDialog.show();
                    }
                });
                sendTime();
            }
        });
        tabwei01 = view.findViewById(R.id.tabLayout01);
        tabwei02 = view.findViewById(R.id.tabLayout02);
        commitView= view.findViewById(R.id.CommitView);
        commitView.setHomePage(this);
//        camore.setOnClickListener(this);
//        voice.setOnClickListener(this);
//        commit.setOnClickListener(this);
        tabwei01.setOnClickListener(this);
        tabwei02.setOnClickListener(this);
        wei = view.findViewById(R.id.weiText);
        tabCursor01 = view.findViewById(R.id.tabCursor01);
        tabCursor02 = view.findViewById(R.id.tabCursor02);
        back= view.findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void setFilterObj(int flag, FilterObj filter) {
        super.setFilterObj(flag, filter);
        if (flag==Configs.VIEW_POSITION_Login){
            data= (List<LaJiBean>) filter.getTag();

        }
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
        switch (v.getId()) {
            case R.id.tabLayout01:
                tabCursor01.setVisibility(View.VISIBLE);
                tabCursor02.setVisibility(View.GONE);
                isOneWEi=true;
                break;
            case R.id.tabLayout02:
                tabCursor01.setVisibility(View.GONE);
                tabCursor02.setVisibility(View.VISIBLE);
                isOneWEi=false;
                break;
            case R.id.back:
                back.setVisibility(View.GONE);
                commitView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBluthStutaListener(int type, Object obj) {
        BluthStuta = type;
        switch (type) {
            case weied:
                Log.e("LLL", "onBluthStutaListener--->" + (String) obj);
                final String we = (String) obj;
                wei.setText(we + "");
                if (juMinDialog!=null&&juMinDialog.isShowing()){
                    juMinDialog.setWei(we);
                }
                break;
            case coned:
                bluthDailog.setBluthConOk((String) obj);
//                new XiaoQuDailog(mContext, this).show();
                break;
            case coning:
                break;
            case failed:
                bluthDailog.setBluthConFailed((String) obj);
                break;
        }

    }

    @Override
    public void onReceiveData(int i0, int i1, Object o) {
        super.onReceiveData(i0, i1, o);
        if (o instanceof Intent) {
            String phon = NFCTool.getPhone((Intent) o);
            String[] use=phon.split(":");
            if (use.length>2){
                juMinBean= new JuMinBean();
                juMinBean.carNub=use[2];
                juMinBean.name=use[0];
                juMinBean.phone=use[1];
                juMinDialog=new JuMinDialog(mContext,this);
                juMinDialog.show();
            }else{
                Toast.makeText(mContext, "卡信息异常", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(mContext, phon, Toast.LENGTH_LONG).show();
        }else if(o instanceof FormImage){
            commitView.showImg((FormImage) o);
        }
    }
    public void willCommit(CommitLajiBean commitLajiBean){
        commitView.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        commitView.showWillCommit(commitLajiBean);
    }
}
