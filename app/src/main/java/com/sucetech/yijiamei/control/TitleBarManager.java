package com.sucetech.yijiamei.control;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.mapbar.android.model.ActivityInterface;

public class TitleBarManager implements OnClickListener
{
//	private ActivityInterface mAif;
	
	public TitleBarManager(Context context, View view, ActivityInterface aif)
	{
//		mAif = aif;
	}
	
	public TitleBarManager(Context context, View view, ActivityInterface aif, View customView)
	{
//		mAif = aif;
	}
	
	public void setTitle(int titleId)
	{
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
		}
	}
}
