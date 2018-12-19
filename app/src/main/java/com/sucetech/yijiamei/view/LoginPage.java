package com.sucetech.yijiamei.view;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.FilterObj;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.model.LaJiBean;
import com.sucetech.yijiamei.provider.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPage extends BasePage implements OnClickListener {
    private final static String TAG = "LoginPage";
    private ActivityInterface mAif;
    private EditText username, pwd;
    private View commit, pwdEyeIcon;
    private Context context;


    public LoginPage(Context context, View view, ActivityInterface aif) {
        super(context, view, aif);
        this.context = context;
        this.mAif = aif;
        username = view.findViewById(R.id.username);
        pwd = view.findViewById(R.id.pwd);
        commit = view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        pwdEyeIcon = view.findViewById(R.id.pwdEyeIcon);
        pwdEyeIcon.setOnClickListener(this);
        username.setText(UserMsg.getUserName());
        pwd.setText(UserMsg.getPwd());
        mAif = aif;
    }

    @Override
    public int getMyViewPosition() {
        return Configs.VIEW_POSITION_Login;
    }

    @Override
    public void goBack() {
        mAif.showPrevious(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        goBack();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commit) {
//            ((MainActivity) getContext()).showProgressDailogView("登陆中...");
            UserMsg.saveUserName(username.getText().toString());
            UserMsg.savePwd(pwd.getText().toString());
//            if (baseUrl.getText() != null && !baseUrl.getText().toString().equals("") && baseUrl.getText().toString().contains("http://")) {
//                Configs.baseUrl = baseUrl.getText().toString();
//            }
            TaskManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    requestLoing2();
                }
            });
        } else if (v.getId() == R.id.pwdEyeIcon) {
            pwdEyeIcon.setSelected(!pwdEyeIcon.isSelected());
            if (pwdEyeIcon.isSelected()) {
                pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String requestLoing2() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", UserMsg.getUserName());
            jsonObject.put("password", UserMsg.getPwd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(Configs.baseUrl + "/api/v1/yijiamei/login")
                .post(body)
                .build();
        try {
            final Response response = ((MainActivity) context).client.newCall(request).execute();
            if (response.isSuccessful()) {
                UserMsg.saveToken(response.header("Authorization"));
                getMeteriType();
//                loginSucced();
//                requestYiyuan();
//                this.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((MainActivity)getContext()).hideProgressDailogView();
//                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        LoginView.this.setVisibility(View.GONE);
////                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        Toast.makeText(getContext(),"chengong -->",Toast.LENGTH_LONG);
//                    }
//                });
//
                return response.body().string();
            } else {
                android.util.Log.e("LLL", "shibai--->");

//                loginFail();
//                Toast.makeText(getContext(),"shibai -->"+response.message(),Toast.LENGTH_LONG);
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            loginFail();
        }
        return null;
    }

    private void getMeteriType() {

        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(Configs.baseUrl + ":8081/datong/v1/recycleType")
                .get()
                .build();
        try {
            final Response response = ((MainActivity) context).client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e("LLL", "chenggong--getMeteriType->");
                List<LaJiBean> data = new Gson().fromJson(response.body().string(), new TypeToken<List<LaJiBean>>() {
                }.getType());//把JSON字符串转为对象
                loginSucced(data);

            } else {
                Log.e("LLL", "shibai---requestYiyuan>");
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loginSucced(final List<LaJiBean> data) {
        username.post(new Runnable() {
            @Override
            public void run() {
                FilterObj filterObj=new FilterObj();
                filterObj.setTag(data);
                mAif.showPage(getMyViewPosition(), Configs.VIEW_POSITION_Main, filterObj);
            }
        });

    }
}
