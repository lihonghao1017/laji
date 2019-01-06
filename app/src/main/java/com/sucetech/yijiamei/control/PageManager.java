package com.sucetech.yijiamei.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mapbar.android.model.ActivityInterface;
import com.mapbar.android.model.BasePage;
import com.mapbar.android.model.PageObject;
import com.sucetech.yijiamei.Configs;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.view.HomePage;
import com.sucetech.yijiamei.view.HuiShouiluPage;
import com.sucetech.yijiamei.view.LoginPage;

public class PageManager
{
	private Context mContext;
	private LayoutInflater mInflater;
	private ActivityInterface mActivityInterface;
	
	public PageManager(Context context, ActivityInterface activityInterface)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mActivityInterface = activityInterface;
	}
	
	public PageObject createPage(int index)
	{
		BasePage page = null;
		View view = null;
		switch(index)
		{
			case Configs.VIEW_POSITION_Main:
			{
				view = mInflater.inflate(R.layout.layout_home, null);
				page = new HomePage(mContext, view, mActivityInterface);
				break;
			}
			case Configs.VIEW_POSITION_Login:
			{
				view = mInflater.inflate(R.layout.layout_login, null);
				page = new LoginPage(mContext, view, mActivityInterface);
				break;
			}
			case Configs.VIEW_POSITION_jilu:
			{
				view = mInflater.inflate(R.layout.huishoujilu_layout, null);
				page = new HuiShouiluPage(mContext, view, mActivityInterface);
				break;
			}
//			case Configs.VIEW_POSITION_TWO:
//			{
//				view = mInflater.inflate(R.layout.layout_two, null);
//				page = new TwoPage(mContext, view, mActivityInterface);
//				break;
//			}
		}
		if(page == null || view == null)
			throw new IllegalArgumentException("the Page is null or the View is null.");
		return new PageObject(index, view, page);
	}
}
