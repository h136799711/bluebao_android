package com.itboye.bluebao.actiandfrag;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.DataFromServerDayBean;
import com.itboye.bluebao.bean.Frag_tab_target_Aim;
import com.itboye.bluebao.bean.PInfo;
import com.itboye.bluebao.ble.ActiBleScan;
import com.itboye.bluebao.ble.BluetoothLeService;
import com.itboye.bluebao.ble.DataToShowBean;
import com.itboye.bluebao.ble.SampleGattAttributes;
//import com.jys.bluetoothproj.ble.ActiBle;
//import com.jys.bluetoothproj.ble.ActiBle2;
import com.itboye.bluebao.breceiver.ReceiverTool;
import com.itboye.bluebao.exwidget.RoundProcessBar;
import com.itboye.bluebao.util.Util;
import com.itboye.bluebao.util.UtilStream;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * fragment home for tab home
 * 
 * @author Administrator
 */
public class FragTabHomeTest2 extends Fragment {

	private SharedPreferences sp;
	private Gson gson = new Gson();
	HttpUtils httpUtils = new HttpUtils();

	protected static final String TAG = "-----FragTabHomeTest2";
	private RoundProcessBar roundProcessBar;
	private float progress;
	// TODO
	private int aimValue = 500;// 设置目标为多少大卡，此数据从SP中获取，目前默认100大卡
	private int calNow;// 取出数字
	private int calBefore = 0;
	private float percentNow;
	private float percentBefore;
	private long timeLastTime = 0;

	private ImageButton btn_showMenu;
	private ImageButton ibtn_connectdevice; // 连接蓝牙设备图标

	// start 8.26 added
	private boolean mConnected;// ble连接状态
	private TextView tv_deviceConnState;
	private int progressUse = 0;
	private int progressBefore = 0;
	private int progressNow = 0;
	// end 8.26 added

	// 8.16--15:40--实现2
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	Calendar calendar = Calendar.getInstance(); // 得到日历
	Date today = new Date();
	private TextView tv_date;
	private ImageButton ibtn_previous;
	private ImageButton ibtn_next;

	// ==========
	TextView tv_mileage;// 里程数
	TextView tv_calorie;// 卡路里
	TextView tv_heartrate;// 心率
	TextView tv_speed;// 速度
	TextView tv_time;// 时间

	TextView tv_weight;// 体重
	TextView tv_bmi;// BMI

	TextView tv_tizhifanglv;// 体脂肪率
	TextView tv_tishuifenlv;// 体水分率
	TextView tv_tinianling;// 体年龄
	TextView tv_jichudaixie;// 基础代谢
	TextView tv_jirouhanliang;// 肌肉含量
	TextView tv_neizanghanliang;// 内脏含量
	TextView tv_gugehanliang;// 骨骼含量
	TextView tv_pixiazhifang;// 皮下脂肪
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View layout_fragment_home = inflater.inflate(R.layout.layout_fragment_tab_home_test2, container, false);

		tv_deviceConnState = (TextView) layout_fragment_home.findViewById(R.id.frag_tab_home_tv_deviceConnState);

		ibtn_connectdevice = (ImageButton) layout_fragment_home.findViewById(R.id.frag_tab_home_ibtn_connectdevice);
		ibtn_connectdevice.setOnClickListener(new MyOnClickListener());

		// ==RoundProcessBar========
		roundProcessBar = (RoundProcessBar) layout_fragment_home.findViewById(R.id.roundProgressBar1);
		roundProcessBar.setTextSizeAimNumber(25f);// 目标字体大小
		roundProcessBar.setAimNumber(aimValue);// 目标设定的数值

		// ==btn click to show slide menu========
		btn_showMenu = (ImageButton) layout_fragment_home.findViewById(R.id.frag_tab_home_ibtn_showMenu);
		btn_showMenu.setOnClickListener(new MyOnClickListener());

		tv_date = (TextView) layout_fragment_home.findViewById(R.id.frag_tab_home_tv_date);
		ibtn_previous = (ImageButton) layout_fragment_home.findViewById(R.id.frag_tab_home_ibtn_previous);
		ibtn_next = (ImageButton) layout_fragment_home.findViewById(R.id.frag_tab_home_ibtn_next);

		tv_date.setText("今天");
		Log.i(TAG, "today is :" + format.format(today));

		ibtn_previous.setOnClickListener(new MyOnClickListener());
		ibtn_next.setOnClickListener(new MyOnClickListener());

		initOtherViews(layout_fragment_home);

		return layout_fragment_home;
	}

	@Override
	public void onResume() {
		// 选取aim值显示在环形进度条内
		showAimValueToUI();

		// 获取当前日期的运动数据
		Log.i(TAG, "today is:" + format.format(today));
		showDataAccordingToTheDateFromServer(format.format(today));

		// 读取体重、BMI、8个参数
		sp = getActivity().getSharedPreferences(Util.SP_FN_PINFO, Context.MODE_PRIVATE);
		String strObj = sp.getString(Util.SP_KEY_PINFO, "");
		if (!strObj.isEmpty()) {
			PInfo pInfo = gson.fromJson(strObj, PInfo.class);
			tv_weight.setText(pInfo.getWeight() + "");
			tv_bmi.setText(pInfo.getBMI() + "");
			showEightArgus(pInfo.getGender(), pInfo.getAge());
		} else {
			tv_weight.setText("65kg");
			tv_bmi.setText("23.88");
			tv_tizhifanglv.setText("17");
			tv_tishuifenlv.setText("45");
			tv_tinianling.setText("23");
			tv_jichudaixie.setText("1012");
			tv_jirouhanliang.setText("31");
			tv_neizanghanliang.setText("3.1");
			tv_gugehanliang.setText("2.7");
			tv_pixiazhifang.setText("26");
		}

		// 接受运动设备传来的数据并展示
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

		// 8.31 added
		intentFilter.addAction("RECEIVE_SYSTEM_ALARM_BC");
		// 8.31 added

		getActivity().registerReceiver(bcReceiver, intentFilter);

		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(bcReceiver);
		super.onPause();
	}

	private void initOtherViews(View v) {

		tv_mileage = (TextView) v.findViewById(R.id.frag_tab_home_tv_mileage);// 里程数
		tv_calorie = (TextView) v.findViewById(R.id.frag_tab_home_tv_calorie);// 卡路里
		tv_heartrate = (TextView) v.findViewById(R.id.frag_tab_home_tv_heartrate);// 心率
		tv_speed = (TextView) v.findViewById(R.id.frag_tab_home_tv_speed);// 速度
		tv_time = (TextView) v.findViewById(R.id.frag_tab_home_tv_time);// 时间

		tv_weight = (TextView) v.findViewById(R.id.frag_tab_home_tv_weight);// 体重
		tv_bmi = (TextView) v.findViewById(R.id.frag_tab_home_tv_bmi);// BMI

		tv_tizhifanglv = (TextView) v.findViewById(R.id.frag_tab_home_tv_tizhifanglv);// 体脂肪率
		tv_tishuifenlv = (TextView) v.findViewById(R.id.frag_tab_home_tv_tishuifenlv);// 体水分率
		tv_tinianling = (TextView) v.findViewById(R.id.frag_tab_home_tv_tinianling);// 体年龄
		tv_jichudaixie = (TextView) v.findViewById(R.id.frag_tab_home_tv_jichudaixie);// 基础代谢
		tv_jirouhanliang = (TextView) v.findViewById(R.id.frag_tab_home_tv_jirouhanliang);// 肌肉含量
		tv_neizanghanliang = (TextView) v.findViewById(R.id.frag_tab_home_tv_neizanghanliang);// 内脏含量
		tv_gugehanliang = (TextView) v.findViewById(R.id.frag_tab_home_tv_gugehanliang);// 骨骼含量
		tv_pixiazhifang = (TextView) v.findViewById(R.id.frag_tab_home_tv_pixiazhifang);// 皮下脂肪

	}

	private BroadcastReceiver bcReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.i(TAG, "frag_tab_home收到广播，action为：" + action);

			// 1 处理BuletoothLeService发送来的广播----连接设备的状态改变了
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				tv_deviceConnState.setText("已连接");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				tv_deviceConnState.setText("未连接");
				Toast.makeText(getActivity(), "设备已断开", Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Toast.makeText(getActivity(), "发现服务", Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				displayData(data);
			} else if (BluetoothLeService.ACTION_GATT_SERVICE_NOT_FOUND.equals(action)) {
				Toast.makeText(getActivity(), "没有发现服务", Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_GATT_CHARA_NOT_FOUND.equals(action)) {
				Toast.makeText(getActivity(), "没有发现特征值", Toast.LENGTH_SHORT).show();
			} else if ("RECEIVE_SYSTEM_ALARM_BC".equals(action)) {// 闹铃广播
				Log.i(TAG, "tab_home : 开始运动了！");
				String howToStart = intent.getStringExtra("howToStart");
				Log.i(TAG, "howToStart: " + howToStart);

				if ("music".equals(howToStart)) {
					final MediaPlayer mMediaPlayer = new MediaPlayer();
					Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					Log.i(TAG, "music's uri is : " + uri);
					try {
						mMediaPlayer.setDataSource(context, uri);
						AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
						if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
							mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
							mMediaPlayer.setLooping(false);
							mMediaPlayer.prepare();
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					mMediaPlayer.start();

					new AlertDialog.Builder(getActivity()).setTitle("蓝堡提醒您").setMessage("运动时间到了！").setCancelable(false)
							.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mMediaPlayer.stop();
								}
							}).create().show();

				} else {
					final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
					/*
					 * long [] pattern = {2000,1000,2000,1000}; // 停止 开启 停止 开启
					 * vibrator.vibrate(pattern,2); //重复两次上面的pattern
					 * 如果只想震动一次，index设为-1
					 */

					vibrator.vibrate(15000);//15秒
					new AlertDialog.Builder(getActivity()).setTitle("蓝堡提醒您").setMessage("运动时间到了！").setCancelable(false)
							.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									vibrator.cancel();
								}
							}).create().show();

				}
			}

		};

	};

	// 根据“日期”tv显示的数据去服务器获取数据并展示

	private void showDataAccordingToTheDateFromServer(String data) {
		// String uid = Util.uId+"";
		// TODO ----------------------------------------------
		String uid = "39";
		String uuid = SampleGattAttributes.SERVICE_I_NEED;
		Log.i(TAG, "data is: " + data);

		String timeSeconds = null;
		try {
			Log.i(TAG, "时间转换之前是：" + data);
			Date theDay = format.parse(data);
			timeSeconds = theDay.getTime() / 1000 + "";
			Log.i(TAG, "时间转换之后是：" + timeSeconds);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// 1 获取token
		String token = Util.getAccessToken(getActivity());
		if (token.isEmpty()) {
			try {
				Thread.sleep(2000);
				token = Util.getAccessToken(getActivity());
				Log.i(TAG, " 2秒之后token值为：" + token);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!token.isEmpty()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", "39");
			params.addBodyParameter("uuid", uuid);
			params.addBodyParameter("time", timeSeconds);

			String urlTheDayData = Util.urlGetTheDayData + token;
			Log.i(TAG, urlTheDayData);

			httpUtils.send(HttpMethod.POST, urlTheDayData, params, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(TAG, "获取TheDay数据失败：" + arg1);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					Log.i(TAG, "获取TheDay数据成功：" + arg0.result);

					if (arg0.result.length() > 100) {
						DataFromServerDayBean bean = gson.fromJson(arg0.result, DataFromServerDayBean.class);
						if (bean != null) {
							tv_heartrate.setText(bean.getData().getHeart_rate());
							tv_speed.setText(bean.getData().getSpeed());
							tv_time.setText(bean.getData().getCost_time());
							tv_calorie.setText(bean.getData().getCalorie());
							tv_mileage.setText(bean.getData().getDistance());
							if (bean.getData().getTarget_calorie() != "0") {

								float a = Float.parseFloat(bean.getData().getCalorie());
								float b = Float.parseFloat(bean.getData().getTarget_calorie());
								roundProcessBar.setProgress(a / b);
							} else {
								roundProcessBar.setProgress(1f);
							}
						}
					}
				}
			});
		} else {
			Log.i(TAG, "token is empty.");
		}
	}

	// 页面下边8个参数的显示
	private void showEightArgus(int gender, int age) {
		String tizhifanglv, tishuifenlv, jichudaixie, jirouhanliang, neizanghanliang, gugehanliang, pixiazhifang;
		if (gender == 1) {// nan

			if (age <= 30) {
				tizhifanglv = 11 + "";
				tishuifenlv = 52 + "";
				jichudaixie = 1382 + "";
				jirouhanliang = 33 + "";
				neizanghanliang = 2.5 + "";
				gugehanliang = 2.3 + "";
				pixiazhifang = 15 + "";
			} else {
				tizhifanglv = 17 + "";
				tishuifenlv = 47 + "";
				jichudaixie = 1552 + "";
				jirouhanliang = 31 + "";
				neizanghanliang = 3.1 + "";
				gugehanliang = 2.7 + "";
				pixiazhifang = 25 + "";
			}

		} else {// nv
			if (age <= 30) {
				tizhifanglv = 10 + "";
				tishuifenlv = 55 + "";
				jichudaixie = 1010 + "";
				jirouhanliang = 26 + "";
				neizanghanliang = 2.7 + "";
				gugehanliang = 2.1 + "";
				pixiazhifang = 12 + "";
			} else {
				tizhifanglv = 16 + "";
				tishuifenlv = 46 + "";
				jichudaixie = 1130 + "";
				jirouhanliang = 28 + "";
				neizanghanliang = 3.3 + "";
				gugehanliang = 2.3 + "";
				pixiazhifang = 23 + "";
			}
		}

		tv_tizhifanglv.setText(tizhifanglv);// 体脂肪率
		tv_tishuifenlv.setText(tishuifenlv);// 体水分率
		tv_tinianling.setText(age + "");// 体年龄
		tv_jichudaixie.setText(jichudaixie);// 基础代谢
		tv_jirouhanliang.setText(jirouhanliang);// 肌肉含量
		tv_neizanghanliang.setText(neizanghanliang);// 内脏含量
		tv_gugehanliang.setText(gugehanliang);// 骨骼含量
		tv_pixiazhifang.setText(pixiazhifang);// 皮下脂肪
	}

	// 展示从ble获取的数据，每两分钟向服务器传送一次
	private void displayData(String data) {
		Log.i(TAG, "-----displayData");

		long timeNow = System.currentTimeMillis() / 1000;

		Log.i(TAG, "上次提交时间------：" + timeLastTime);

		if (data != null) {
			Log.i(TAG, "读取到的 data 是：" + data.toString());

			DataToShowBean dataToShowBean = gson.fromJson(data, DataToShowBean.class);

			// 展示数据到页面
			if (dataToShowBean != null) {
				tv_mileage.setText(dataToShowBean.getMiles());
				tv_calorie.setText(dataToShowBean.getCars());
				Toast.makeText(getActivity(), dataToShowBean.getCars(), Toast.LENGTH_SHORT).show();
				showPercent(dataToShowBean.getCars());
				Log.i(TAG, "-----displayData--dataToShowBean.getCars() is: " + dataToShowBean.getCars());
				tv_heartrate.setText(dataToShowBean.getXinlv());
				tv_speed.setText(dataToShowBean.getSpeed());
				tv_time.setText(dataToShowBean.getTime());
			} else {
				Toast.makeText(getActivity(), "读取到的数据为空", Toast.LENGTH_SHORT).show();
			}

			// 提交数据到服务器
			if (timeNow - timeLastTime > 10) { // 两分钟提交一次

				Log.i(TAG, "这次提交时间-----：" + timeNow);
				timeLastTime = timeNow;

				String token = Util.getAccessToken(getActivity());
				if (token.isEmpty()) {
					try {
						Thread.sleep(2000);
						token = Util.getAccessToken(getActivity());
						Log.i(TAG, " 2秒之后token值为：" + token);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!token.isEmpty()) {
					// String uid = Util.uId+"";
					// TODO ----------------------------------------------
					String uid = "39";
					String uuid = SampleGattAttributes.SERVICE_I_NEED;

					RequestParams params = new RequestParams();
					params.addBodyParameter("uid", uid);
					params.addBodyParameter("uuid", uuid);
					params.addBodyParameter("speed", dataToShowBean.getSpeed());
					params.addBodyParameter("heart_rate", dataToShowBean.getXinlv());
					params.addBodyParameter("distance", dataToShowBean.getMiles());
					params.addBodyParameter("total_distance", dataToShowBean.getTotalMiles());
					params.addBodyParameter("cost_time", dataToShowBean.getTime());
					params.addBodyParameter("calorie", dataToShowBean.getCars());
					params.addBodyParameter("upload_time", timeNow + "");

					String urlDataToServer = Util.urlDataToServer + token;
					Log.i(TAG, urlDataToServer);

					httpUtils.send(HttpMethod.POST, urlDataToServer, params, new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Log.i(TAG, "提交数据失败：" + arg1);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Log.i(TAG, "提交数据成功：" + arg0.result);
							char cc = arg0.result.charAt(8);
							if ('0' == cc) {
								Log.i(TAG, "提交数据成功--code is ： " + cc);
							} else {
								Log.i(TAG, "提交数据失败--code is ：" + cc);
							}
						}
					});
				}
			}
		}
	}

	// 显示环形进度条进度
	private void showPercent(String str) {
		String strint = str.substring(0, str.length() - 1);
		calNow = Integer.parseInt(strint);// 取出数字
		if (calNow > calBefore) {
			calNow += (calNow - calBefore);
			calBefore = calNow;
		}
		float fCalNow = calNow;
		float fCalBefore = calBefore;
		float fAimValue = aimValue;
		percentNow = fCalNow / fAimValue;// aimValue 目标值
		percentBefore = fCalBefore / fAimValue;
		if (percentNow > percentBefore) {
			percentNow += (percentNow - percentBefore);
			percentBefore = percentNow;
		} else {
			// 显示
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (percentBefore >= progress) {
						Util.df.format(progress);
						progress += 0.001;
						Log.i(TAG, "progress is:" + progress);
						roundProcessBar.setProgress(progress * 100);
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		Toast.makeText(
				getActivity(),
				"fAimValue is:" + fAimValue + "\n\r fCalNow is :" + fCalNow + "\n\r fCalBefore is :" + fCalBefore
						+ "\n\r percentNow is :" + percentNow + "\n\r percentBefore is : " + percentBefore, Toast.LENGTH_SHORT).show();
		Log.i(TAG, "progress is:" + Util.df.format(progress));
		// 显示
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (percentBefore < percentNow) {
					Util.df.format(progress);
					progress += 0.01;
					Log.i(TAG, "progress is:" + progress);
					roundProcessBar.setProgress(progress * 100);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// aim值的选取，只有当前日期可选
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void showAimValueToUI() {
		int minutesNow = today.getHours() * 60 + today.getMinutes();
		Log.i(TAG, "当前时间分钟数为：" + minutesNow);

		sp = getActivity().getSharedPreferences(Util.SP_FN_TARGET, Context.MODE_PRIVATE);
		ArrayList<Frag_tab_target_Aim> aims = null;
		String temp = sp.getString(Util.SP_KEY_TARGET, "");
		if (!temp.isEmpty()) {
			try {
				aims = UtilStream.String2SurveyList(temp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < aims.size(); i++) {
				Frag_tab_target_Aim aimTemp = aims.get(i);
				Log.i(TAG, "today.toString() is : " + format.format(today));
				if (format.format(today).equals(aimTemp.getDayOfWeek())) {// 今天的
					int minutesInAim = Integer.parseInt(aimTemp.getTime_hour()) * 60 + Integer.parseInt(aimTemp.getTime_minute());
					if (minutesNow <= minutesInAim) {
						roundProcessBar.setAimNumber(Integer.parseInt(aimTemp.getGoal()));
					}
				}
			}
		}
	}

	// 點擊前一天后一天之前先清楚之前一天的数据
	private void clearUI() {
		roundProcessBar.setProgress(0);
		tv_heartrate.setText(100 + "");
		tv_speed.setText(100 + "");
		tv_time.setText(100 + "");
		tv_calorie.setText(100 + "");
		tv_mileage.setText(100 + "");

	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// 根据它来显示数据,默认为 今天（系统当前日期）
			// 不管dateNow是 今天、昨天还是一个日期，都转换为日期类型的dateShowNow
			Date dateShowNow = today;
			String dateNow = tv_date.getText().toString();// 获取当前显示的日期
			calendar.setTime(today);// 把当前日期赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为昨天
			Date yesterday = calendar.getTime(); // 得到前一天的时间
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date dayBeforeYesterday = calendar.getTime();
			Log.i(TAG, "yesterday is :" + format.format(yesterday));
			Log.i(TAG, "dayBeforeYesterday is :" + format.format(dayBeforeYesterday));

			switch (v.getId()) {

			// 发送一个打开侧滑菜单的广播到ActiMainTest2
			case R.id.frag_tab_home_ibtn_showMenu:
				Intent intent = new Intent(ReceiverTool.OPEN_SLIDEMENU);
				getActivity().sendBroadcast(intent);
				break;

			// 切换日期==前一天
			case R.id.frag_tab_home_ibtn_previous:
				clearUI();
				if ("今天".equals(dateNow)) {
					tv_date.setText("昨天");
					Log.i(TAG, "dateShowNow is :" + format.format(yesterday));
					showDataAccordingToTheDateFromServer(format.format(yesterday));

				} else if ("昨天".equals(dateNow)) {
					tv_date.setText(format.format(dayBeforeYesterday));
					Log.i(TAG, "dateShowNow is :" + format.format(dayBeforeYesterday));
					showDataAccordingToTheDateFromServer(format.format(dayBeforeYesterday));
				} else {

					try {
						dateShowNow = format.parse(dateNow);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					calendar.setTime(dateShowNow);// 把当前时间赋给日历
					calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
					dateShowNow = calendar.getTime(); // 得到前一天的时间
					tv_date.setText(format.format(dateShowNow));
					Log.i(TAG, "dateShowNow is :" + format.format(dateShowNow));
					showDataAccordingToTheDateFromServer(format.format(dateShowNow));
				}
				break;

			// 切换日期==后一天
			case R.id.frag_tab_home_ibtn_next:
				clearUI();
				if ("今天".equals(dateNow)) {
					Log.i(TAG, "dateShowNow is :" + format.format(today));
				} else if ("昨天".equals(dateNow)) { // 昨天 变 今天
					tv_date.setText("今天");
					Log.i(TAG, "dateShowNow is :" + format.format(today));
					showDataAccordingToTheDateFromServer(format.format(today));
				} else if (format.format(dayBeforeYesterday).equals(dateNow)) {
					tv_date.setText("昨天");// 前天变昨天
					Log.i(TAG, "dateShowNow is :" + format.format(yesterday));
					showDataAccordingToTheDateFromServer(format.format(yesterday));
				} else {
					try {
						dateShowNow = format.parse(dateNow);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					calendar.setTime(dateShowNow);// 把当前时间赋给日历
					calendar.add(Calendar.DAY_OF_MONTH, 1); // 设置为前一天
					dateShowNow = calendar.getTime(); // 得到前一天的时间
					tv_date.setText(format.format(dateShowNow));
					Log.i(TAG, "dateShowNow is :" + format.format(dateShowNow));
					showDataAccordingToTheDateFromServer(format.format(dateShowNow));

				}

				break;

			case R.id.frag_tab_home_ibtn_connectdevice:
				Intent intent1 = new Intent(getActivity(), ActiBleScan.class);
				startActivity(intent1);
				break;

			}
		}

	}
}