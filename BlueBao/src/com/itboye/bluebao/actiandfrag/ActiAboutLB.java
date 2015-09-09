package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.itboye.bluebao.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 关于蓝堡activity
 * @author Administrator
 */
public class ActiAboutLB extends Activity {
	
	private WebView wv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_aboutlb);
		wv = (WebView) findViewById(R.id.acti_aboutlb_wv);
		wv.loadUrl("http://lanbao.app.itboye.com/contact.html");
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
	protected void onDestroy() {
		ActiAboutLB.this.finish();
		super.onDestroy();
	}
}