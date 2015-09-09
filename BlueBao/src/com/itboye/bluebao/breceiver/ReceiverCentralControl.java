package com.itboye.bluebao.breceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 中枢控制Receiver
 * @author Administrator
 *
 */
public class ReceiverCentralControl extends BroadcastReceiver {
	
	private static final String TAG = "-----ReceiverCentralControl";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if ( "TO_LOGIN".equals(action) ) {
			
			String access_token = intent.getStringExtra("access_token");
			String username = intent.getStringExtra("username");
			String password = intent.getStringExtra("password");
			
			String url = "http://lanbao.itboye.com/api.php/User/login?access_token="+ access_token;
			
			Log.i(TAG, "token is :" + access_token + "  username is :" + username + "  password is :" +password +"  url is: " + url);
			
			 HttpUtils httpUtils = new HttpUtils();
			  
			  RequestParams params = new RequestParams();
			  params.addBodyParameter("username",username);
			  params.addBodyParameter("password",password);
			  
			  httpUtils.send(HttpMethod.POST, url ,params, new RequestCallBack<String>(){
			  @Override 
				  public void onFailure(HttpException arg0, String arg1) {
					  Log.i(TAG, "登录失败：" +  arg0.getLocalizedMessage() + "  " + arg0.getMessage() + "  " + arg1);
				  }
				  
				  @Override public void onSuccess(ResponseInfo<String> arg0) {
					  Log.i(TAG, "登录成功：" + arg0.result); 
				  }
			  });
			
		}
		//==========

		
	}

}
