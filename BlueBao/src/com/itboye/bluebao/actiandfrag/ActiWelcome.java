package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.itboye.bluebao.R;

/**
 * ActiWelcome 欢迎界面
 * 
 * @author Administrator
 *
 */
public class ActiWelcome extends Activity {

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_welcome);

		mHandler = new Handler();
		mHandler.postDelayed(gotoActiLogin, 2000);
		
/*		// 手机是否联网
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (!cManager.getActiveNetworkInfo().isAvailable()) {
			
			new AlertDialog.Builder(ActiWelcome.this).setMessage("手机没有接入网络").create().show();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Toast.makeText(ActiWelcome.this, "2 s 过去了", Toast.LENGTH_SHORT).show();
			//android.os.Process.killProcess(android.os.Process.myPid());// 退出应用

		}else{
			
		}*/

		

	}

	Runnable gotoActiLogin = new Runnable() {
		public void run() {
			Intent intent = new Intent(ActiWelcome.this, ActiLogin.class);
			startActivity(intent);
		}
	};

}
