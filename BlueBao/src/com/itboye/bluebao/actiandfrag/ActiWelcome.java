package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.itboye.bluebao.R;
import com.umeng.analytics.MobclickAgent;

/**
 * ActiWelcome 欢迎界面
 * @author Administrator
 */
public class ActiWelcome extends Activity {

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_welcome);

		mHandler = new Handler();
		mHandler.postDelayed(gotoActiLogin, 2000);

		MobclickAgent.updateOnlineConfig(this);// 发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率

	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(ActiWelcome.this);
		super.onResume();
	};

	@Override
	protected void onPause() {
		MobclickAgent.onPause(ActiWelcome.this);
		super.onPause();
	};

	Runnable gotoActiLogin = new Runnable() {
		public void run() {
			Intent intent = new Intent(ActiWelcome.this, ActiLogin.class);
			startActivity(intent);
			ActiWelcome.this.finish();
		}
	};
}