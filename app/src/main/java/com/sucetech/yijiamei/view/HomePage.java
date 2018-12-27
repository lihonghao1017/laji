package com.sucetech.yijiamei.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.FilterObj;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.PageRestoreData;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.model.CommitLajiBean;
import com.sucetech.yijiamei.model.FormImage;
import com.sucetech.yijiamei.model.JuMinBean;
import com.sucetech.yijiamei.model.LaJiBean;
import com.sucetech.yijiamei.model.XiaoQuBean;
import com.sucetech.yijiamei.provider.BluthConnectTool;
import com.sucetech.yijiamei.provider.NFCTool;
import com.sucetech.yijiamei.provider.TaskManager;
import com.sucetech.yijiamei.widget.BluthDailog;
import com.sucetech.yijiamei.widget.CommitView;
import com.sucetech.yijiamei.widget.JuMinDialog;
import com.sucetech.yijiamei.widget.MaoWeiDialog;
import com.sucetech.yijiamei.widget.XiaoQuDailog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private MaoWeiDialog maoWeiDialog;
    public boolean isOneWEi=true;
    private CommitView commitView;
    public List<LaJiBean> data;

    private View searchBurron;
    private EditText search;
    private View bottmLayout;

//    private Handler mHandler=new Handler();
//    private void  sendTime(){
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onBluthStutaListener(weied,"99");
//                sendTime();
//            }
//        },1000*2);
//    }

    public HomePage(Context context, View view, ActivityInterface aif) {
        super(context, view, aif);
        mContext = context;
        showBluthDailog();
        mAif = aif;
        tabwei01 = view.findViewById(R.id.tabLayout01);
        tabwei02 = view.findViewById(R.id.tabLayout02);
        commitView= view.findViewById(R.id.CommitView);
        commitView.setHomePage(this);
        bottmLayout=view.findViewById(R.id.bottmLayout);
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
        searchBurron=view.findViewById(R.id.searchBurron);
        searchBurron.setOnClickListener(this);
        search=view.findViewById(R.id.searchUser);
        search.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    private void showBluthDailog(){
        bluthConnectTool = new BluthConnectTool(mContext, this);
        bluthDailog = new BluthDailog(mContext, this);
        if (BluthStuta != weied) {
            bluthDailog.show();
        }
        bluthDailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                XiaoQuDailog xiaoQuDailog= new XiaoQuDailog(mContext, HomePage.this);
                xiaoQuDailog.show();
            }
        });
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
                bottmLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.searchBurron:
                if (search.getText().toString()!=null&&!search.getText().toString().equals("")){
                    TaskManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            sendData(search.getText().toString());
                        }
                    });

                }
                break;
        }
    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private void sendData(String phone) {
        Request request = new Request.Builder()
                .url(Configs.baseUrl + ":8081/datong/v1/residents/cellphone/"+phone)
                .get()
                .build();
        try {
            final Response response = ((MainActivity) mContext).client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    JSONObject object=new JSONObject(response.body().string());
                    JuMinBean jbean= new JuMinBean();
                    jbean.name=object.optString("name");
                    jbean.phone=object.optString("cellphone");
                    searchOK(jbean);
                } catch (JSONException e) {
                    searchError();
                    e.printStackTrace();
                }

            } else {
                android.util.Log.e("LLL", "shibai--->");
                searchError();
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            searchError();
            android.util.Log.e("LLL", "IOException--->" + e.toString());
        }
    }
    private void searchError(){
        search.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,"没有此居民",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void searchOK(final JuMinBean juMinBean){
        this.juMinBean=juMinBean;
        search.post(new Runnable() {
            @Override
            public void run() {
                if (juMinBean!=null){
                    if (!isOneWEi&&UserMsg.getPizhongByCarId(juMinBean.carNub)!=null&&!UserMsg.getPizhongByCarId(juMinBean.carNub).equals("")){
                        maoWeiDialog=new MaoWeiDialog(mContext,HomePage.this);
                        maoWeiDialog.show();
                        maoWeiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                bottmLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{
                        juMinDialog=new JuMinDialog(mContext,HomePage.this);
                        juMinDialog.show();
                        juMinDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                bottmLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    bottmLayout.setVisibility(View.GONE);
                }
            }
        });
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
                }else if(maoWeiDialog!=null&&maoWeiDialog.isShowing()){
                    maoWeiDialog.setWei(we);
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
//            if (BluthStuta!=weied){
//                showBluthDailog();
//                return;
//            }
            final String cardId = NFCTool.getCardId((Intent) o);
//            String[] use=phon.split(":");
            if (cardId!=null&&!cardId.equals("")){
//                juMinBean= new JuMinBean();
//                juMinBean.carNub=use[2];
//                juMinBean.name=use[0];
//                juMinBean.phone=use[1];
                TaskManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        getUserMsg(cardId);
                    }
                });
//                if (!isOneWEi&&UserMsg.getPizhongByCarId(juMinBean.carNub)!=null&&!UserMsg.getPizhongByCarId(juMinBean.carNub).equals("")){
//                    maoWeiDialog=new MaoWeiDialog(mContext,this);
//                    maoWeiDialog.show();
//                }else{
//                    juMinDialog=new JuMinDialog(mContext,this);
//                    juMinDialog.show();
//                }
//                bottmLayout.setVisibility(View.GONE);
            }else{
                Toast.makeText(mContext, "卡信息异常", Toast.LENGTH_LONG).show();
            }
//            Toast.makeText(mContext, phon, Toast.LENGTH_LONG).show();
        }else if(o instanceof FormImage){
            commitView.showImg((FormImage) o);
        }
    }
    public void willCommit(CommitLajiBean commitLajiBean, JSONObject data){
        if (isOneWEi){
            commitView.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            commitView.showWillCommit(commitLajiBean,data);
        }else{
            if (commitLajiBean!=null)
            UserMsg.savePizhongByCarId(juMinBean.carNub,data.toString());
            else{
                commitView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                commitView.showWillCommit(commitLajiBean,data);
            }
        }

    }

    public void commitOK(){
        back.post(new Runnable() {
            @Override
            public void run() {
                onClick(back);

            }
        });
    }
    public void getUserMsg(final String cardId){
        Request request = new Request.Builder()
                .url(Configs.baseUrl + ":8081/datong/v1/residents/"+cardId)
                .get()
                .build();
        try {
            final Response response = ((MainActivity) mContext).client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    JSONObject object=new JSONObject(response.body().string());
                    JuMinBean jbean= new JuMinBean();
                    jbean.name=object.optString("name");
                    jbean.phone=object.optString("cellphone");
                    jbean.carNub=cardId;
                    searchOK(jbean);
                } catch (JSONException e) {
                    searchError();
                    e.printStackTrace();
                }

            } else {
                android.util.Log.e("LLL", "shibai--->");
                searchError();
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            searchError();
            android.util.Log.e("LLL", "IOException--->" + e.toString());
        }
    }

}
