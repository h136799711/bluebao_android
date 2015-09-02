package com.itboye.bluebao.util;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.itboye.bluebao.bean.Access_token;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 帮助类
 * 
 * @author Administrator
 */
public class Util {
	
	//保留两位小数
	public static final java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00"); 
	
	private static final String TAG = "-----Util";
	public static boolean vibrate;//8.28 设置
	public static boolean sound = true;//8.28 设置,默认只有音乐，没有震动
	
	//URL们
	private static final String urlCommon = "http://lanbao.itboye.com/api.php";
	private static final String urlGetAccessToken = urlCommon + "/Token/index"; //获取token的URL
	public static final String urlLogin = urlCommon + "/User/login?access_token=";//登录的URL
	public static final String urlRegister = urlCommon + "/User/register?access_token="; //注册的URL
	public static final String urlUpdatePI = urlCommon + "/User/update?access_token="; //更新个人信息的URL
	public static final String urlUploadUImg= urlCommon + "/File/upload?access_token="; //更新个人信息的URL
	public static final String urlGetTotalData = urlCommon + "/Bicyle/total?access_token="; //个人中心--总路程时间热量
	public static final String urlGetBestData = urlCommon + "/Bicyle/bestResult?access_token="; //个人中心--最好成绩
	public static final String urlDataToServer = urlCommon + "/Bicyle/add?access_token="; //提交数据
	public static final String urlGetTheDayData = urlCommon + "/Bicyle/day?access_token="; //单日数据
	public static final String urlGetTheMonthData = urlCommon + "/Bicyle/month?access_token="; //单月数据
	
	//SP FN and KEY们
	public static final String SP_FN_ACCESSTOKEN = "access_token";
	public static final String SP_KEY_ACCESSTOKENOBJ = "access_token_obj";
	
	public static final String SP_FN_TARGET = "frag_tab_target_aims";//目标
	public static final String SP_KEY_TARGET = "aims";
	
	public static final String SP_FN_PINFO = "PINFO";//用户信息
	public static final String SP_KEY_PINFO = "PINFO";
	public static int uId=39;//用户id
	
	public static final String SP_FN_USERIMG = "userimg";//用户头像
	public static final String SP_KEY_USERIMG_PATH = "userimg_path"; 
	//public static final String SP_KEY_USERIMG_CHOSENAME = "userimg_chosename";//删除时用
	//public static final String SP_KEY_USERIMG_TAKENAME = "userimg_takename"; //删除时用
	public static final String SP_KEY_USERIMG_USE = "userimg_usename"; //显示时用
	
	public static final String SP_FN_USERNAMEPWD = "username_pwd";//用户账号密码
	public static final String SP_KEY_USERNAMEPWD = "username_pwd"; 
	
	public static final String SP_FN_ALARM = "alarm";//是否启用闹铃
	public static final String SP_KEY_ALARM = "alarm"; 
	
	
	public static  String username = "";//登录成功的时候保存在这里，在更新个人信息的时候把这些内容保存进去
	public static String password = "";
	
	private static String access_tokenForLinkServer = "";
	
	public static HttpUtils httpUtils = new HttpUtils(); // HttpUtils对象
	public static Date date = new Date();
	public static long timeNow = date.getTime(); // 系统当前时间
	public static Gson gson = new Gson(); 
	public static SharedPreferences sp; 
	public static SharedPreferences.Editor editor;

	
	/**
	 * 获取token：如果SP中已经有token并且没有超时的话，就从SP中取出token；否则就从服务器获取新token，并写入SP中。
	 */
	public static String getAccessToken(final Context context) {
		 String access_token = "";
		sp = context.getSharedPreferences(SP_FN_ACCESSTOKEN, Context.MODE_PRIVATE); 
		String strOriginalTokenObj = sp.getString(SP_KEY_ACCESSTOKENOBJ, "");
		if ( strOriginalTokenObj.isEmpty() ) {
			access_token = getTokenFromServerAndSaveTokenObjToSP(context);
		}else {
			Access_token originalTokenObj = gson.fromJson(strOriginalTokenObj, Access_token.class);
			long spToken = originalTokenObj.getInfo().getGet_time();// SP中存放的获取token的时间
			if ( timeNow - spToken >= 3300000 ){ 
				//超时，重新获取token
				access_token = getTokenFromServerAndSaveTokenObjToSP(context);
			} else { 
				Access_token tokenTemp = gson.fromJson(strOriginalTokenObj, Access_token.class);
				access_token = tokenTemp.getInfo().getAccess_token();
			}
		}
		return access_token;
	}
	
	/**
	 * getToken,执行连接服务器获取token的操作,获取成功后保存至SP
	 */
	private static String getTokenFromServerAndSaveTokenObjToSP(final Context context){
		
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("grant_type", "client_credentials");
		params.addBodyParameter("client_id", "by55bec42a8e5431");
		params.addBodyParameter("client_secret", "f63515ffc8c1a200dedeffce9bd63492");
		//params.addHeader("http.keepAlive", "false");

		httpUtils.send(HttpMethod.POST, urlGetAccessToken, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i(TAG, "获取token失败：" + arg1);
				access_tokenForLinkServer = "";
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.i(TAG, "获取token成功" );
				Access_token accessObj = gson.fromJson(arg0.result, Access_token.class);
				//if (accessObj != null) {
					access_tokenForLinkServer = accessObj.getInfo().getAccess_token();
					// 加上时间戳保存到SP中
					accessObj.info.setGet_time(timeNow);
					String strObj = gson.toJson(accessObj);
					//Log.i(TAG, "新 获取token的  str 表示是： " + strObj);
					editor = sp.edit();
					editor.putString(SP_KEY_ACCESSTOKENOBJ, strObj);
					editor.commit();
				//}
			}
		});
		
		return access_tokenForLinkServer;
	}

	/**
	 * 保存账号和密码到SP
	 */
	public static void saveUandPtoSP(Context context, String username, String password){
		sp = context.getSharedPreferences(SP_FN_PINFO, Context.MODE_PRIVATE); // 只能被本应用程序读写;
		editor = sp.edit();
		
		editor.putString("username", username);// 可以用方法加密一下再保存，这里直接保存到SP中
		editor.putString("password", password);
		editor.commit();
	}
	
	// 是否联网网络
	public static boolean isInternetAvailable(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}

	// 是否支持BLE
	public static boolean isBleAvailable(final Context context) {
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 去除data中的中括号
	 * @param data
	 * @return
	 */
	public static String deletebracket(String data){
		String temp = data.replace('[', ' ');
		String temp1 = temp.replace(']', ' ');
		return temp1.trim();
	}
	
}
