package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.CodeAndData;
import com.itboye.bluebao.bean.CodeAndDataLoginSuccess;
import com.itboye.bluebao.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录activity
 * @author Administrator
 */
public class ActiLogin extends Activity implements View.OnClickListener {

	protected static final String TAG = "-----ActiLogin";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Gson gson = new Gson();

	private EditText et_username;
	private EditText et_password;
	private String username;
	private String password;

	private CheckBox cb_rememberPwd; // 记住密码 复选框
	private Button btn_login;
	private Button btn_toRegister; // 转到注册页面的按钮

	private ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_login);
		et_username = (EditText) findViewById(R.id.acti_login_et_username);
		et_password = (EditText) findViewById(R.id.acti_login_et_password);
		btn_login = (Button) findViewById(R.id.acti_login_btn_login);
		btn_toRegister = (Button) findViewById(R.id.acti_login_btn_toregister);
		cb_rememberPwd = (CheckBox) findViewById(R.id.acti_login_cb_rememberpwd);
		btn_login.setOnClickListener(this);
		btn_toRegister.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);

		if (!Util.isInternetAvailable(ActiLogin.this)) {// 手机没有接入网络
			//Toast.makeText(ActiLogin.this, "手机未接入网络", Toast.LENGTH_LONG).show();
			AlertDialog.Builder ad = new AlertDialog.Builder(ActiLogin.this);
			ad.setMessage("手机未接入网络\n请联网后重新启动").setPositiveButton("知道了", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.create().show();
			btn_login.setEnabled(false);// 不可用
			btn_toRegister.setEnabled(false);
		}
	/*	if (!Util.isBleAvailable(ActiLogin.this)) {
			Log.i(TAG, "toast shoujiversion ");
			Toast.makeText(ActiLogin.this, "手机系统版本太低，请更新", Toast.LENGTH_LONG).show();
			btn_login.setEnabled(false);
			btn_toRegister.setEnabled(false);
		}*/

		if (pdialog != null) {
			pdialog.cancel();
		}

		sp = this.getSharedPreferences(Util.SP_FN_USERNAMEPWD, Context.MODE_PRIVATE);
		if (!sp.getString("username", "").isEmpty() && !sp.getString("password", "").isEmpty()) {
			et_username.setText(sp.getString("username", ""));
			et_password.setText(sp.getString("password", ""));
			cb_rememberPwd.setChecked(true);//9.14 add
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.acti_login_btn_login:

			pdialog = new ProgressDialog(ActiLogin.this);
			pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
			pdialog.setCancelable(true);// 设置是否可以通过点击Back键取消
			pdialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
			pdialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
			pdialog.setMessage("登录中......");
			pdialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					String token = Util.getAccessToken(ActiLogin.this);
					if (token.isEmpty()) {
						try {
							Thread.sleep(2000);
							token = Util.getAccessToken(ActiLogin.this);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!token.isEmpty()) {
						username = et_username.getText().toString().trim();
						password = et_password.getText().toString().trim();

						HttpUtils httpUtils = new HttpUtils();
						RequestParams params = new RequestParams();
						params.addBodyParameter("username", username);
						params.addBodyParameter("password", password);

						String urlLogin = Util.urlLogin + token;

						httpUtils.send(HttpMethod.POST, urlLogin, params, new RequestCallBack<String>() {
							@Override
							public void onFailure(HttpException arg0, String arg1) {
								Log.i(TAG, "登录失败：" + arg1);
								pdialog.cancel();
								Toast.makeText(ActiLogin.this, "登录失败" + arg1, Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								Log.i(TAG, "登录成功：" + arg0.result);
								int length = arg0.result.length();
								//Log.i(TAG, "返回信息的长度 :" + length);
								if (length > 100) {
									CodeAndDataLoginSuccess infoSuccess = gson.fromJson(arg0.result, CodeAndDataLoginSuccess.class);
									//Log.i(TAG, "uid  is :" + infoSuccess.getData().getUid());
									//Log.i(TAG, "continuous_day  is :" + infoSuccess.getData().getContinuous_day());
									Util.uId = infoSuccess.getData().getUid();
									Util.continuous_day = infoSuccess.getData().getContinuous_day();

									if (cb_rememberPwd.isChecked()) {
										sp = ActiLogin.this.getSharedPreferences(Util.SP_FN_USERNAMEPWD, Context.MODE_PRIVATE);
										editor = sp.edit();
										editor.putString("username", username);
										editor.putString("password", password);
										editor.commit();
									}

									pdialog.cancel();
									Toast.makeText(ActiLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
									// 3 转到ActiMain
									Intent intent = new Intent(ActiLogin.this, ActiMain.class);
									startActivity(intent);
									ActiLogin.this.finish();

								} else {
									CodeAndData info = gson.fromJson(arg0.result, CodeAndData.class);
									pdialog.cancel();
									Toast.makeText(ActiLogin.this, "登录失败：" + info.getData(), Toast.LENGTH_SHORT).show();
								}
							}
						});

					} else {
						pdialog.cancel();
						Toast.makeText(ActiLogin.this, "获取token失败", Toast.LENGTH_SHORT).show();
					}
				}
			}).start();

			break;

		// 转到注册页面
		case R.id.acti_login_btn_toregister:
			Intent intent = new Intent(ActiLogin.this, ActiRegister.class);
			startActivity(intent);
			//ActiLogin.this.finish();
			break;
		}
	}
}