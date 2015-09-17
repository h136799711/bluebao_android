package com.itboye.bluebao.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.itboye.bluebao.bean.Access_token;
import com.itboye.bluebao.bean.Frag_tab_target_Aim;
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

	// 保留两位小数
	public static final java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

	private static final String TAG = "-----Util";
	public static boolean vibrate;// 8.28 设置
	public static boolean sound = true;// 8.28 设置,默认只有音乐，没有震动

	// URL们
	private static final String urlCommon = "http://lanbao.itboye.com/api.php";
	private static final String urlGetAccessToken = urlCommon + "/Token/index"; // 获取token的URL
	public static final String urlLogin = urlCommon + "/User/login?access_token=";// 登录的URL
	public static final String urlRegister = urlCommon + "/User/register?access_token="; // 注册的URL
	public static final String urlUpdatePI = urlCommon + "/User/update?access_token="; // 更新个人信息的URL
	public static final String urlUploadUImg = urlCommon + "/File/upload?access_token="; // 更新个人信息的URL
	public static final String urlGetTotalData = urlCommon + "/Bicyle/total?access_token="; // 个人中心--总路程时间热量
	public static final String urlGetBestData = urlCommon + "/Bicyle/bestResult?access_token="; // 个人中心--最好成绩
	public static final String urlDataToServer = urlCommon + "/Bicyle/add?access_token="; // 提交数据
	public static final String urlGetTheDayData = urlCommon + "/Bicyle/day?access_token="; // 单日数据
	public static final String urlGetTheMonthData = urlCommon + "/Bicyle/month?access_token="; // 单月数据

	// SP FN and KEY们
	public static final String SP_FN_ACCESSTOKEN = "access_token";
	public static final String SP_KEY_ACCESSTOKENOBJ = "access_token_obj";

	public static final String SP_FN_TARGET = "frag_tab_target_aims";// 目标
	public static final String SP_KEY_TARGET = "aims";
	public static final String SP_KEY_TARGET_UID = "uid";// 9.14 added

	public static final String SP_FN_PINFO = "PINFO";// 用户信息
	public static final String SP_KEY_PINFO = "PINFO";
	public static int uId;// 用户id
	public static int continuous_day = 0;// 已锻炼天数，分享时用
	public static String time = "00:00:00";// 运动了多久，分享时用
	public static String cals = "0";// 消耗了多少卡路里，分享时用
	public static String miles = "0";// 运动里程数，分享时用

	public static final String SP_FN_USERIMG = "userimg";// 用户头像
	public static final String SP_KEY_USERIMG_PATH = "userimg_path";
	public static final String SP_KEY_USERIMG_USE = "userimg_usename"; // 显示时用

	public static final String SP_FN_USERNAMEPWD = "username_pwd";// 用户账号密码
	public static final String SP_KEY_USERNAMEPWD = "username_pwd";

	public static final String SP_FN_ALARM = "alarm";// 是否启用闹铃
	public static final String SP_KEY_ALARM = "alarm";

	public static String username = "";// 登录成功的时候保存在这里，在更新个人信息的时候把这些内容保存进去
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
		if (strOriginalTokenObj.isEmpty()) {
			access_token = getTokenFromServerAndSaveTokenObjToSP(context);
		} else {
			Access_token originalTokenObj = gson.fromJson(strOriginalTokenObj, Access_token.class);
			long spToken = originalTokenObj.getInfo().getGet_time();// SP中存放的获取token的时间
			if (timeNow - spToken >= 3300000) {
				// 超时，重新获取token
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
	private static String getTokenFromServerAndSaveTokenObjToSP(final Context context) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("grant_type", "client_credentials");
		params.addBodyParameter("client_id", "by55bec42a8e5431");
		params.addBodyParameter("client_secret", "f63515ffc8c1a200dedeffce9bd63492");
		// params.addHeader("http.keepAlive", "false");

		httpUtils.send(HttpMethod.POST, urlGetAccessToken, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i(TAG, "获取token失败：" + arg1);
				access_tokenForLinkServer = "";
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.i(TAG, "获取token成功");
				Access_token accessObj = gson.fromJson(arg0.result, Access_token.class);
				Log.i(TAG, "获取token成功--accessObj is：" + accessObj);
				// if (accessObj != null) {
				access_tokenForLinkServer = accessObj.getInfo().getAccess_token();
				// 加上时间戳保存到SP中
				accessObj.info.setGet_time(timeNow);
				String strObj = gson.toJson(accessObj);
				// Log.i(TAG, "新 获取token的  str 表示是： " + strObj);
				editor = sp.edit();
				editor.putString(SP_KEY_ACCESSTOKENOBJ, strObj);
				editor.commit();
				// }
			}
		});

		return access_tokenForLinkServer;
	}

	/**
	 * 保存账号和密码到SP
	 */
	public static void saveUandPtoSP(Context context, String username, String password) {
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
	 * 
	 * @param data
	 * @return
	 */
	public static String deletebracket(String data) {
		String temp = data.replace('[', ' ');
		String temp1 = temp.replace(']', ' ');
		return temp1.trim();
	}

	// 添加目标时检测是否闹铃提醒
	// 把当前用户所有未过期的aims的时间设定进闹钟里
	// date :
	@SuppressWarnings("unchecked")
	@SuppressLint("SimpleDateFormat")
	public static void setAlarms(Context context) {

		sp = context.getSharedPreferences(Util.SP_FN_ALARM, Context.MODE_PRIVATE);
		Log.i("alarm ", sp.getString(Util.SP_KEY_ALARM, ""));

		if (!sp.getString(Util.SP_KEY_ALARM, "").isEmpty()) {// 第一步：闹铃开启状态

			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			long timeNow = new Date().getTime();
			Log.i(TAG, "timeNow is: " + timeNow);// -----timeNow

			SharedPreferences sp = context.getSharedPreferences(Util.SP_FN_TARGET, Context.MODE_PRIVATE);
			String aimsInSP = sp.getString(Util.SP_KEY_TARGET, "");
			if (!aimsInSP.isEmpty()) {// SP中aims不空

				ArrayList<Frag_tab_target_Aim> aims = null;
				try {
					aims = UtilStream.String2SurveyList(aimsInSP);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < aims.size(); i++) {
					int uid = aims.get(i).getUid();// 当前用户id
					if (Util.uId == uid) {// 第二步：是当前用户的aim

						String timeThenStrDay = aims.get(i).getDayOfWeek();
						String timeThenStrHour = aims.get(i).getTime_hour();
						String timeThenStrMinute = aims.get(i).getTime_minute();
						String timeThenStr = timeThenStrDay + " " + timeThenStrHour + ":" + timeThenStrMinute;
						Log.i(TAG, "timeThenStr : " + timeThenStr);
						Date timeThenDate = null;
						try {
							timeThenDate = format.parse(timeThenStr);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						long timeThen = timeThenDate.getTime();// -----timeThen

						// 当前时间晚于目标设定的时间，就添加闹铃
						if (timeThen > timeNow) {// 第三步：aim的时间大于当前时间
							Log.i(TAG, "timeThen : " + timeThen);
							Intent intent = new Intent("RECEIVE_SYSTEM_ALARM_BC");
							String howToStart = Util.sound ? "music" : "vibrate";
							intent.putExtra("howToStart", howToStart);
							PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent,
									PendingIntent.FLAG_CANCEL_CURRENT);
							am.set(AlarmManager.RTC_WAKEUP, timeThen, pendingIntent);
							Log.i(TAG, "setAlarms");
							Log.i(TAG, "intent.getData():"+ intent.getData() + "  intent.getType():" + intent.getType() + "  intent.getClass():" +intent.getClass() + "  intent.getCategories():" + intent.getCategories() );
						}
					}
				}
			}
		}
	}

	// 取消所有闹铃
	/*
	 * cancel(PendingIntent operation)方法将会取消Intent匹配的任何闹钟。
	 * 关于Intent的匹配，查看filterEquals(Intent other)方法的说明可知， 两个Intent从intent
	 * resolution(filtering)(Intent决议或过滤)的角度来看是一致的，
	 * 即认为两个Intent相等。即是说，Intent的action
	 * ,data,type,class,categories是相同的，其他的数据都不在比较范围之内。
	 */
	public static void cancelAlarms(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("RECEIVE_SYSTEM_ALARM_BC");
		intent.putExtra("howToStart", "music");//test
		Log.i(TAG, "清楚所有闹铃");
		Log.i(TAG, "intent.getData():"+ intent.getData() + "  intent.getType():" + intent.getType() + "  intent.getClass():" +intent.getClass() + "  intent.getCategories():" + intent.getCategories() );
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(pendingIntent);
		
	}

}
