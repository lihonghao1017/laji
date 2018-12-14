package com.sucetech.yijiamei;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mapbar.android.control.AppActivity;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.OnDialogListener;
import com.mapbar.android.model.PageObject;
import com.mapbar.android.model.VersionInfo;
import com.sucetech.yijiamei.control.MainController;

public class MainActivity extends AppActivity {
    private final static String TAG = "MainActivity";

    private MainController mMainController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		long time = System.currentTimeMillis();
        setContentView(R.layout.layout_main);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xff4AA9FD);
        Log.isDebug = true;

        mMainController = new MainController(this);
//		Log.e(TAG, TAG+":onCreate->time="+(System.currentTimeMillis()-time));
    }


    @Override
    public PageObject createPage(int index) {
        return mMainController.createPage(index);
    }

    @Override
    public int getAnimatorResId() {
        return R.id.animator;
    }

    @Override
    public int getMainPosition() {
        return Configs.VIEW_POSITION_Main;
    }

    @Override
    public int getOutPosition() {
        return Configs.VIEW_POSITION_NONE;
    }

    @Override
    public int getNonePositioin() {
        return Configs.VIEW_POSITION_NONE;
    }

    @Override
    public int getViewNoneFlag() {
        return Configs.VIEW_FLAG_NONE;
    }


    /**
     * 正常的Activity onResume，
     * 不在此处做任何初始化工作
     * 一般是应用前台后台来回切换时
     * 按须做处理即可
     */
    @Override
    public void onResume() {
        mMainController.onResume();
        super.onResume();
    }

    /**
     * 正常的Activity onPause
     */
    @Override
    protected void onPause() {
        mMainController.onPause();
        super.onPause();
    }

    /**
     * 正常的Activity onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainController.onDestroy();
    }

    private boolean isCanExit = false;

    /**
     * 界面有切换动作时，会调用到
     */
    @Override
    public void onPageActivity() {
        isCanExit = false;
    }

    /**
     * 是否可以正常退出
     */
    @Override
    public boolean canExit() {
        if (!isCanExit) {
            this.isCanExit = true;
            this.showAlert(R.string.toast_againto_exit);
            return false;
        }
        return true;
    }

    /**
     * 正常的Activity onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMainController.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 统一Dialog样式，可在此做处理
     */
    @Override
    public void showDialog(int style, int resId, String title, String content, String cancelText, String okText,
                           OnDialogListener listener) {
        showDialog(style, resId, title, content,
                cancelText, okText, listener, Gravity.CENTER);
    }

    /**
     * 统一Dialog样式，可在此做处理
     */
    @Override
    public void showDialog(int style, int resId, String title, String content,
                           String cancelText, String okText, final OnDialogListener listener, int gravity) {
		/*
		final Dialog dialog = new Dialog(this, style);
		dialog.setContentView(resId);
		dialog.setCanceledOnTouchOutside(false);
		if(!TextUtils.isEmpty(title))
		{
			TextView motorcade_tv_title = (TextView)dialog.findViewById(R.id.motorcade_tv_title);
			if(motorcade_tv_title != null)
			{
				motorcade_tv_title.setVisibility(View.VISIBLE);
				motorcade_tv_title.setText(title);
			}
		}
		TextView tv = (TextView) dialog.findViewById(R.id.motorcade_tv_context);
		tv.setGravity(gravity);
		tv.setText(content);
		TextView tv_yes = (TextView) dialog.findViewById(R.id.motorcade_tv_yes);
		if(!TextUtils.isEmpty(okText))
			tv_yes.setText(okText);
		tv_yes.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (dialog != null && dialog.isShowing())
					dialog.cancel();
				if(listener != null)
					listener.onOk();
			}
		});
		TextView tv_no = (TextView) dialog.findViewById(R.id.motorcade_tv_no);
		if(TextUtils.isEmpty(cancelText))
		{
			tv_no.setVisibility(View.GONE);
			View h_line = dialog.findViewById(R.id.motorcade_dialog_h_line);
			h_line.setVisibility(View.GONE);
		}
		else
		{
			tv_no.setText(cancelText);
		}
		tv_no.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				if(listener != null)
					listener.onCancel();
			}
		});
		if (!dialog.isShowing())
			dialog.show();
		*/
    }

    /**
     * 由框架获取的上下文
     */
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 有新版本更新时，会被调用
     */
    @Override
    public void onNewVersionUpdate(final VersionInfo vi) {
    }

    /**
     * 应用将要退出时，会被调用
     */
    @Override
    public void onRelease() {
        super.onRelease();
    }

    /**
     * 当Activity界面初始化完毕后
     * 系统进入初始化前被调用
     */
    @Override
    public void appWillEnterBackgroundInit(int flag) {
        Log.e(TAG, "appWillEnterBackgroundInit");
    }

    /**
     * 设置是否需要等待进入后台初始化
     * 如果存在一些比较耗时的初始化时
     * 将此方法打开
     * 并在appDidEnterBackgroundInit中进行初始化工作
     * 等待进入后台初始化时
     * 需要在等待结束后调用doEnterBackgroundInit进行初始化的完成工作
     */
    @Override
    public boolean waitEnterBackgroundInit(int flag) {
        Log.e(TAG, "waitEnterBackgroundInit");
        return false;
    }

    /**
     * 当waitEnterBackgroundInit返回false时
     * 该方法将会被调用
     * 一些比较耗时的初始化工作将在此进行
     */
    @Override
    public void appDidEnterBackgroundInit(int flag) {
        Log.e(TAG, "appDidEnterBackgroundInit");
    }

    /**
     * 初始化完成，可以进行界面切换
     */
    @Override
    public void onFinishedInit(int flag) {
        Log.e(TAG, "onFinishedInit");
        mMainController.onResume(flag);
    }
}
