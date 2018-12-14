package com.sucetech.yijiamei.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.FilterObj;
import com.mapbar.android.model.Log;
import com.mapbar.android.model.PageRestoreData;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;

public class LoginPage extends BasePage implements OnClickListener
{
	private final static String TAG = "LoginPage";
//	private Context mContext;
	private ActivityInterface mAif;

	public LoginPage(Context context, View view, ActivityInterface aif)
	{
		super(context, view, aif);
		
//		mContext = context;
		mAif = aif;
		
		View btn_show_tow = view.findViewById(R.id.btn_show_tow);
		btn_show_tow.setOnClickListener(this);
	}

	@Override
	public void setFilterObj(int flag, FilterObj filter)
	{
		super.setFilterObj(flag, filter);
	}

	@Override
	public void viewWillAppear(int flag)
	{
		super.viewWillAppear(flag);
		Log.e(TAG, TAG+"=>viewWillAppear");
	}
	
	private boolean isFinishedInit = false;

	@Override
	public void viewDidAppear(int flag)
	{
		super.viewDidAppear(flag);
		Log.e(TAG, TAG+"=>viewDidAppear");
		
		if(isFinishedInit)
			return;
		isFinishedInit = true;
	}

	@Override
	public void viewWillDisappear(int flag)
	{
		super.viewWillDisappear(flag);
		Log.e(TAG, TAG+"=>viewWillDisappear");
	}

	@Override
	public void viewDidDisappear(int flag)
	{
		super.viewDidDisappear(flag);
		Log.e(TAG, TAG+"=>viewDidDisappear");
	}

	@Override
	public int getMyViewPosition()
	{
		return Configs.VIEW_POSITION_Login;
	}

	@Override
	public void goBack()
	{
		mAif.showPrevious(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		goBack();
		return true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.e(TAG, TAG+"=>onDestroy");
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
	}

	@Override
	public PageRestoreData getRestoreData()
	{
		return null;
	}

	@Override
	public void onRestoreData(PageRestoreData data)
	{
		super.onRestoreData(data);
	}

	@Override
	public void onPerformAction()
	{
		super.onPerformAction();
	}

	@Override
	public void onClick(View v)
	{
		mAif.showPage(this.getMyViewPosition(), Configs.VIEW_POSITION_TWO, null);
	}
}
