package com.plug.lock.main;


import android.app.Activity;
import android.os.Bundle;

import com.plug.lock.R;


public class AuthActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    new Auth(AuthActivity.this,"ddd");
		// new Download(mProgressbar);
	}


}
