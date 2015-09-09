package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.os.Bundle;

import com.itboye.bluebao.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 关于蓝堡activity
 * 
 * @author Administrator
 */
public class ActiYoushouldknow extends Activity {

	//private TextView tv_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_youshouldknow);
		//tv_text = (TextView) findViewById(R.id.acti_youshouldknow_tv_show);
		
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		ActiYoushouldknow.this.finish();
		super.onBackPressed();
	}

}