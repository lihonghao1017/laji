package com.sucetech.yijiamei.control;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import com.mapbar.android.control.AppActivity;
import com.mapbar.android.model.FilterObj;
import com.mapbar.android.model.PageObject;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.view.HomePage;


public class MainController
{
	private AppActivity mBaseActivity;
	private PageManager mPageManager;

	private Handler mHandler;
	private boolean isFinishInitView = false;

	private boolean isFinishInit = false;

	public MainController(AppActivity activity)
	{
		this.mBaseActivity = activity;
		this.mHandler = new Handler();
	}

	public void onResume(int flag)
	{
		if(!isFinishInitView)
		{
			isFinishInitView = true;
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					initView();
				}
			}, 100);
		}
	}

	public void onDestroy()
	{
	}

	public PageObject createPage(int index)
	{
		return mPageManager.createPage(index);
	}

	private void initView()
	{

		mPageManager = new PageManager(mBaseActivity, mBaseActivity);

		View launcher_main = this.mBaseActivity.findViewById(R.id.home_main);

		PageObject mainPage = new PageObject(Configs.VIEW_POSITION_Main,
				launcher_main, new HomePage(this.mBaseActivity, launcher_main,
				this.mBaseActivity));
		this.mBaseActivity.pushPage(mainPage, Configs.VIEW_FLAG_NONE, null);


//		this.mBaseActivity.showPage(Configs.VIEW_POSITION_NONE, Configs.VIEW_POSITION_Main, null);

		isFinishInit = true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(!isFinishInit)
			return true;
		return false;
	}

	public void onResume()
	{
	}

	public void onPause()
	{
	}

	public void setTitle(View view, int titleId)
	{
		new TitleBarManager(mBaseActivity, view, mBaseActivity).setTitle(titleId);
	}

	public void setTitleCustomView(View view, View customView)
	{
		new TitleBarManager(mBaseActivity, view, mBaseActivity, customView);
	}
}
