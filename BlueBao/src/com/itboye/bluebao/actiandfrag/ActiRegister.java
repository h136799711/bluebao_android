package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.CodeAndData;
import com.itboye.bluebao.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册Activity
 * 
 * @author Administrator
 */
public class ActiRegister extends Activity implements OnClickListener {

	protected static final String TAG = "-----ActiRegister";

	private HttpUtils httpUtils = new HttpUtils();
	private Gson gson = new Gson();
	private ProgressDialog pdialog;

	private EditText et_email;
	private EditText et_password;
	private EditText et_passwordConfirm;
	private CheckBox cb_agreeWithThePolicy;
	private TextView tv_agreeWithThePolicy;
	private Button btn_register;

	private String email;// username
	private String pwd;
	private String pwdConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_register);
		et_email = (EditText) findViewById(R.id.acti_register_et_emailaddress);
		et_password = (EditText) findViewById(R.id.acti_register_et_pwd);
		et_passwordConfirm = (EditText) findViewById(R.id.acti_register_et_confirmpwd);
		cb_agreeWithThePolicy = (CheckBox) findViewById(R.id.acti_register_cb_agreewiththepolicy);
		tv_agreeWithThePolicy = (TextView) findViewById(R.id.acti_register_tv_agreewiththepolicy);
		btn_register = (Button) findViewById(R.id.acti_register_btn_register);
		btn_register.setOnClickListener(this);
		tv_agreeWithThePolicy.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);

		if (pdialog != null) {
			pdialog.cancel();
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

		pwd = et_password.getText().toString().trim();
		pwdConfirm = et_passwordConfirm.getText().toString().trim();
		email = et_email.getText().toString().trim();
		
		switch ( v.getId() ){
		case R.id.acti_register_btn_register:
			// 不同意用户须知无法注册
			if (!cb_agreeWithThePolicy.isChecked()) {
				Toast.makeText(this, "您尚未同意用户须知", Toast.LENGTH_SHORT).show();
			} else if (email.isEmpty() || pwd.isEmpty() || pwdConfirm.isEmpty()) {
				Toast.makeText(this, "您有信息未填，请检查", Toast.LENGTH_SHORT).show();
			} else if (pwd.length() < 6 || pwdConfirm.length() < 6) {
				Toast.makeText(this, "密码长度要求6位以上", Toast.LENGTH_SHORT).show();
			} else if (!pwd.equals(pwdConfirm)) {
				Toast.makeText(this, "密码不一致，请检查", Toast.LENGTH_SHORT).show();
			} else {

				pdialog = new ProgressDialog(ActiRegister.this);
				pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
				pdialog.setCancelable(true);// 设置是否可以通过点击Back键取消
				pdialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
				pdialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
					}
				});
				pdialog.setMessage("注册中......");
				pdialog.show();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// 1 获取token
						String token = Util.getAccessToken(ActiRegister.this);
						if (token.isEmpty()) {
							try {
								Thread.sleep(2000);
								token = Util.getAccessToken(ActiRegister.this);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						if (token.isEmpty()) {
							pdialog.cancel();
							Toast.makeText(ActiRegister.this, "获取token失败", Toast.LENGTH_SHORT).show();
						} else {

							RequestParams params = new RequestParams();
							params.addBodyParameter("username", email);// username就是email
							params.addBodyParameter("password", pwd);

							String urlRegister = Util.urlRegister + token;
							Log.i(TAG, urlRegister);

							httpUtils.send(HttpMethod.POST, urlRegister, params, new RequestCallBack<String>() {
								@Override
								public void onFailure(HttpException arg0, String arg1) {
									Log.i(TAG, "注册失败：" + arg0.getLocalizedMessage() + "  " + arg0.getMessage() + "  " + arg1);
									pdialog.cancel();
									Toast.makeText(ActiRegister.this, "注册失败：" + arg1, Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									Log.i(TAG, "注册成功：" + arg0.result);
									pdialog.cancel();
									CodeAndData info = gson.fromJson(arg0.result, CodeAndData.class);
									if (info.getCode() == -1) {
										Toast.makeText(ActiRegister.this, "注册失败：" + info.getData(), Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(ActiRegister.this, "注册成功", Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(ActiRegister.this, ActiLogin.class);
										intent.putExtra("newUsername", email);
										startActivity(intent);
										ActiRegister.this.finish();
									}
								}
							});
						}
					}
				}).start();
				
			}
			break;
			
		case R.id.acti_register_tv_agreewiththepolicy:
			Intent intent =  new Intent(ActiRegister.this,ActiYoushouldknow.class);
			startActivity(intent);
			break;
		}
	}
}