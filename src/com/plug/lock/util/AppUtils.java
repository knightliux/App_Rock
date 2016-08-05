package com.plug.lock.util;

import java.io.File;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class AppUtils {
	public static void startAPP(Context context,String appPackageName){
	    try{
	        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
	        context.startActivity(intent);
	    }catch(Exception e){
	    	
	    	Toast.makeText(context, "APK未安装", Toast.LENGTH_LONG).show();
	    }
	}
	//根据包名判断是否已安装
	public static boolean ApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
    public static void uninstall(Context context,String pkgname){
    	Uri packageURI = Uri.parse("package:"+pkgname);
        Log.d("pkg",pkgname);
    	Intent intent = new Intent(Intent.ACTION_DELETE,packageURI);

    	context.startActivity(intent);
    }
	public static void install(String Path, Context context) {

		// 核心是下面几句代码
		try {
			Log.d("path", Path);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(Path)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	public static HashMap<String, String> GetAppInfoByPkg(Context context,String Pkg){
		
	      HashMap<String ,String> re=null;
	      PackageInfo packageInfo;
	      PackageManager manager = context.getPackageManager();
			try {
				re=new HashMap<String, String>();
				packageInfo=manager.getPackageInfo(Pkg, 0);
				String appName=packageInfo.applicationInfo.loadLabel(manager).toString();
			    String version=packageInfo.versionName;
			    re.put("AppName", appName);
	            re.put("version",version);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	      return re;
	}
	public static HashMap<String, String> GetAppInfoByApk(Context context,String FielPath){
		  PackageManager pm = context.getPackageManager();    
	      PackageInfo info = pm.getPackageArchiveInfo(FielPath, PackageManager.GET_ACTIVITIES);
	      HashMap<String ,String> re=null;
	      if(info != null){    
	            ApplicationInfo appInfo = info.applicationInfo;  
	            re=new HashMap<String, String>();
	            String appName = pm.getApplicationLabel(appInfo).toString();    
	            String packageName = appInfo.packageName;  //得到安装包名称
	            String version=info.versionName;   
	            re.put("AppName", appName);
	            re.put("packageName", packageName);
	            re.put("version",version);
	           

	        }  
	      return re;
	}
}
