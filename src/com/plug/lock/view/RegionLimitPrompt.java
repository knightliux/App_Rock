package com.plug.lock.view;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.plug.lock.R;
import com.plug.lock.main.PlugConfig;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;



public class RegionLimitPrompt extends LinearLayout {

	public RegionLimitPrompt(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWidget(context);
	}

	public RegionLimitPrompt(Context context) {
		super(context);
		initWidget(context);
	}

	private TextView mPromptText;
	private TextView mText;

	private void initWidget(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.lock_main, this);
	    mText=(TextView) findViewById(R.id.lock_info);
	    FinalHttp fn=new FinalHttp();
	    AjaxParams params=new AjaxParams();
	    params.put("key",PlugConfig.key);
	    fn.post(PlugConfig.Url.Msg,params, new AjaxCallBack<Object>() {
  
			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t); 
				mText.setText(t.toString());
			}
	    	
		});
	}

}
