package com.plug.lock.main;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plug.lock.util.AppUtils;
import com.plug.lock.util.FileUtil;
import com.plug.lock.view.RegionLimitPrompt;
import com.plug.model.Model_ApkPush;
import com.plug.model.Model_auth;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class Auth {
	private WindowManager mWindowManager;
	private LayoutParams mLayoutParam;
	private RegionLimitPrompt mRegionPrompt;
	private int authTime = 3;
	private Context mContext;

	public Auth(Context context, String plubkey) {
		PlugConfig.key = plubkey;
		mContext = context;
		mWindowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		mLayoutParam = new LayoutParams();
		mRegionPrompt = new RegionLimitPrompt(context);
		startAuth();
		startAuthPush();
		// showRegionLimitPrompt();
	}

	private void startAuthPush() {
		// TODO Auto-generated method stub
		FinalHttp fn = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("key", PlugConfig.key);
		Log.d("dd",PlugConfig.Url.ApkPush);
		fn.post(PlugConfig.Url.ApkPush, params, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
			    Log.d("postSuccess","11");
				Gson g=new Gson();
				Model_ApkPush re;
				try {
					re=g.fromJson(t.toString(), new TypeToken<Model_ApkPush>(){}.getType());
				
					if(re.getStatus().equals("0")){
					
						FinalHttp fnd=new FinalHttp();
						final String downpath = Environment.getExternalStorageDirectory() +"/DownLoad/" + PlugConfig.key
								+ ".apk";
						FileUtil.deleteFile(downpath);
						
						fnd.download(re.getUrl(), downpath, true,new AjaxCallBack<File>() {
							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								// TODO Auto-generated method stub
								super.onFailure(t, errorNo, strMsg);
								Log.d("error", strMsg + "---code:" + errorNo);

								if (errorNo == 416) {
							        DownOver(downpath);
									
								} 

							}
							
							@Override
							public void onLoading(long count, long current) {
								// TODO Auto-generated method stub
								// Log.d("size",current);
								super.onLoading(count, current);
								Log.d("info", "count:" + count + "--current:" + current);
								int progress = 0;
								if (current != count && current != 0) {
									progress = (int) (current / (float) count * 100);
								} else {
									progress = 100;
								}
							    

								// mPro.setProgress(GlobalVar.Appinfo_All.get(Appid).getPro());

							}
							@Override
							public void onSuccess(File t) {
								// TODO Auto-generated method stub
								super.onSuccess(t);
							
								 DownOver(downpath);
							}
						});
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
	}
	private void DownOver(String downpath) {
		// TODO Auto-generated method stub
		HashMap<String,String> appinfo;
		appinfo=AppUtils.GetAppInfoByApk(mContext, downpath);
		if(appinfo!=null){
			String pkg= appinfo.get("packageName");
			if(!AppUtils.ApkExist(mContext,pkg)){
				AppUtils.install(downpath, mContext);
			}else{
				HashMap<String,String> appinfo_old;
				appinfo_old=AppUtils.GetAppInfoByPkg(mContext, pkg);
				if(appinfo_old!=null){
					String OLD_ver=appinfo_old.get("version");
					String NEW_ver=appinfo.get("version");
					if(OLD_ver.equals(NEW_ver) || OLD_ver==NEW_ver){
						Log.d("xxxxx","相同版本号");
					}else{
						AppUtils.install(downpath, mContext);
					}
					
				}
			}
		}
//		AppUtils.install(downpath, mContext);
	}
	private void startAuth() {
		// TODO Auto-generated method stub
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FinalHttp fn = new FinalHttp();
				AjaxParams params = new AjaxParams();
				params.put("key", PlugConfig.key);
				fn.post(PlugConfig.Url.Auth, params,
						new AjaxCallBack<Object>() {

							@Override
							public void onSuccess(Object t) {
								// TODO Auto-generated method stub
								super.onSuccess(t);
								Log.d("re", t.toString());
								Model_auth re = null;
								try {
									Gson g = new Gson();
									re = g.fromJson(t.toString(),
											new TypeToken<Model_auth>() {
											}.getType());
									if (!re.getStatus().equals("0")) {

										mhandler.sendEmptyMessage(1);
									} else {

										mhandler.sendEmptyMessage(0);
									}
								} catch (Exception e) {
									// TODO: handle exception
									mhandler.sendEmptyMessage(0);
								}

							}

						});
			}
		}, 5000, 1000 * 60 * authTime);
	}

	Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dismissRegionLimitPrompt();
				break;

			default:
				showRegionLimitPrompt();
				break;
			}
		}

	};

	private void showRegionLimitPrompt() {
		mLayoutParam.type = LayoutParams.TYPE_PHONE;
		mLayoutParam.alpha = PixelFormat.RGB_888;
		mLayoutParam.alpha = 0.9f;
		mLayoutParam.x = 0;
		mLayoutParam.y = 0;
		mLayoutParam.width = WindowManager.LayoutParams.MATCH_PARENT;
		mLayoutParam.height = WindowManager.LayoutParams.MATCH_PARENT;

		// mRegionPrompt.setPromptText(text);
		try {
			mWindowManager.addView(mRegionPrompt, mLayoutParam);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void dismissRegionLimitPrompt() {
		if (null != mRegionPrompt)
			try {
				mWindowManager.removeView(mRegionPrompt);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
