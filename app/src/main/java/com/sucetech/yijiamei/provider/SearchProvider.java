package com.sucetech.yijiamei.provider;

import android.content.Context;

import com.mapbar.android.model.ProviderResult;
import com.mapbar.android.net.MyHttpHandler;
import com.mapbar.android.provider.Provider;
import com.mapbar.android.provider.ResultParser;
import com.mapbar.android.provider.UrlHelper;

public class SearchProvider extends Provider
{
//	private final static String TAG = "SearchProvider";

	private final static int REQUEST_FLAG_KEYWORD = 0x01;
	private final static int REQUEST_FLAG_NEARBY = 0x02;
	
	public SearchProvider(Context context)
	{
		super(context);
	}
	
	public MyHttpHandler searchByKeyword(int requestCode, String keyword)
	{
		String url = UrlHelper.getInstance(mContext).getUrl(UrlHelper.URL_LOGIN);
		StringBuffer sb = new StringBuffer(url);
		return getDataFromNet(requestCode, REQUEST_FLAG_KEYWORD, sb.toString());
	}

	@Override
	public ResultParser createResultParser()
	{
		return new SearchResultParser();
	}
	
	private class SearchResultParser extends ResultParser
	{
		@Override
		public ProviderResult parseResult(int requestCode, int flag, String json)
		{
			ProviderResult pr = new ProviderResult();
			try
			{
				switch(flag)
				{
					case REQUEST_FLAG_KEYWORD:
					case REQUEST_FLAG_NEARBY:
					{
						pr.setResponseCode(Provider.RESPONSE_OK);
						return pr;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			pr.setResponseCode(Provider.RESPONSE_ERROR);
			return pr;
		}
	}
}
