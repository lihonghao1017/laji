package com.sucetech.yijiamei.view;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.adapter.HuishouAdapter;
import com.sucetech.yijiamei.model.JiLuBean;
import com.sucetech.yijiamei.model.JuMinBean;
import com.sucetech.yijiamei.provider.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HuiShouiluPage extends BasePage implements View.OnClickListener{
    private View back;
    private ListView listView;
    private HuishouAdapter huishouAdapter;
    private List<JiLuBean> datas;
    private Context context;
    private ActivityInterface mAif;
private EditText editText;
    public HuiShouiluPage(Context context, View view, ActivityInterface aif) {
        super(context, view, aif);
        this.context = context;
        this.mAif = aif;
        back = view.findViewById(R.id.back);
        back.setOnClickListener(this);
        listView= view.findViewById(R.id.listView);
        editText = view.findViewById(R.id.input);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String searText = v.getText().toString();
                    if (searText != null && !searText.equals(""))
                        TaskManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                requestLoing2(searText);
                            }
                        });
                        return true;
                }
                return false;
            }
        });
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        datas=new ArrayList<>();
        huishouAdapter=new HuishouAdapter(context,datas);
        listView.setAdapter(huishouAdapter);
        TaskManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                requestLoing2(null);
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        onClick(back);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                mAif.showPrevious();
                break;
        }

    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String requestLoing2(String phone) {

        JSONObject jsonObject = new JSONObject();
        try {
            Date currentTime = new Date();
            jsonObject.put("start", "2019-01-01 10:10:10");
            jsonObject.put("end", DateToString(currentTime));
            if (phone!=null){
                jsonObject.put("cellphone", phone);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(Configs.baseUrl + ":8081/datong/v1/recycle/search")
                .header("Authorization", UserMsg.getToken())
                .post(body)
                .build();
        try {
            final Response response = ((MainActivity) context).client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    JSONObject object=new JSONObject(response.body().string());
                    Log.e("LLL","object-->"+object.toString());
                    JSONArray jsonArray=object.getJSONArray("content");
                    if ( jsonArray!=null&&jsonArray.length()>0){
                        List<JiLuBean> jiLuBeanList=new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JiLuBean jiLuBean=new JiLuBean();
                            JSONObject object1=jsonArray.getJSONObject(i);
                            jiLuBean.dateTime=object1.optString("dateTime");
                            jiLuBean.score=object1.optInt("score");
                            jiLuBean.weight=object1.optInt("weight");
                            jiLuBean.typeName=object1.optString("typeName");
                            jiLuBean.residentName=object1.optString("residentName");
                            jiLuBean.cellphone=object1.optString("cellphone");
                            jiLuBeanList.add(jiLuBean);
                        }
                        requestOK(jiLuBeanList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                android.util.Log.e("LLL", "shibai--->"+ response.body().string());
                return null;
            } else {
                android.util.Log.e("LLL", "shibai--->"+ response.body().string());
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String DateToString(Date source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(source);
    }
    private void requestOK( final List<JiLuBean> jiLuBeanList){
        back.post(new Runnable() {
            @Override
            public void run() {
                datas.clear();
                datas.addAll(jiLuBeanList);
                huishouAdapter.notifyDataSetChanged();
            }
        });
    }
}