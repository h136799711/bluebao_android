package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.CodeAndData;
import com.itboye.bluebao.util.Util;
import com.itboye.bluebao.util.UtilStream;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 注册Activity
 * 
 * @author Administrator
 *
 */
public class ActiRegister extends Activity implements OnClickListener {

	protected static final String TAG = "-----ActiRegister";

	private HttpUtils httpUtils = new HttpUtils(); // HttpUtils对象
	private Gson gson = new Gson();
	private ProgressDialog pdialog;

	private EditText et_email;
	private EditText et_password;
	private EditText et_passwordConfirm;
	private CheckBox cb_agreeWithThePolicy; // 记住密码 复选框
	private Button btn_register;

	private String email;//username
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
		btn_register = (Button) findViewById(R.id.acti_register_btn_register);
		btn_register.setOnClickListener(this);

	}
	
	@Override
	protected void onResume() {
		if (pdialog != null) {
			pdialog.cancel();
		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {

		pwd = et_password.getText().toString().trim();
		pwdConfirm = et_passwordConfirm.getText().toString().trim();
		email = et_email.getText().toString().trim();

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
			
			new Thread(
					new Runnable(){

						@Override
						public void run() {
							// 1 获取token
							String token = Util.getAccessToken(ActiRegister.this);
							if (token.isEmpty()) {
								Toast.makeText(ActiRegister.this, "注册失败(token is null)，请重新提交", Toast.LENGTH_SHORT).show();
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
										Toast.makeText(ActiRegister.this, "注册失败：" + arg1, Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onSuccess(ResponseInfo<String> arg0) {
										Log.i(TAG, "注册成功：" + arg0.result);
										CodeAndData info = gson.fromJson(arg0.result, CodeAndData.class);
										if( info.getCode() ==  -1  ){
											Toast.makeText(ActiRegister.this, "注册失败：" + info.getData() , Toast.LENGTH_SHORT).show();
										}else {
											Toast.makeText(ActiRegister.this, "注册成功"  , Toast.LENGTH_SHORT).show();
											Intent intent = new Intent(ActiRegister.this, ActiLogin.class);
											intent.putExtra("newUsername", email);
											startActivity(intent);
										}
									}
								});
							}
						}
					}
					).start();
		}
	}
}