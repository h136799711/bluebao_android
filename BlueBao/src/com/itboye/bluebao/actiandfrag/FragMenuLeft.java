package com.itboye.bluebao.actiandfrag;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.Frag_tab_target_Aim;
import com.itboye.bluebao.ble2.ActiBleScan;
import com.itboye.bluebao.breceiver.ReceiverTool;
import com.itboye.bluebao.exwidget.CircleImageView;
import com.itboye.bluebao.util.Util;
import com.itboye.bluebao.util.UtilStream;

/**
 * fragment left_menu for drawer
 * 
 * @author Administrator
 */
public class FragMenuLeft extends Fragment implements OnItemClickListener {

	protected static final String TAG = "-----FragMenuLeft";

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private CircleImageView iv_userimg;

	ListView lv_menu_left; // frag_menu_left中的ListView
	ArrayAdapter<CharSequence> adapter; // adapter for listview

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_fragment_menu_left, container, false);
		iv_userimg = (CircleImageView) view.findViewById(R.id.fragment_menu_left_top_userimg);

		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.menu_left, R.layout.layout_fragment_menu_left_item);
		lv_menu_left = (ListView) view.findViewById(R.id.fragment_meun_left_lv);
		lv_menu_left.setAdapter(adapter);
		lv_menu_left.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		// 如果用户修改过头像，则显示更改之后的头像
		sp = getActivity().getSharedPreferences(Util.SP_FN_USERIMG, Context.MODE_PRIVATE);
		String strUserImgName = sp.getString(Util.SP_KEY_USERIMG_USE, "");
		String strUserImgPath = sp.getString(Util.SP_KEY_USERIMG_PATH, "");
		String strUserImgUrl = strUserImgPath + "/" + strUserImgName;
		Log.i(TAG, "strUserImgPath is : " + strUserImgPath);
		Log.i(TAG, "strUserImgName is : " + strUserImgName);
		Log.i(TAG, "strUserImgUrl is : " + strUserImgUrl);

		// 若存在，判断其中是否有需要的这张图片
		if (strUserImgUrl.length() != 1) {
			Bitmap photoBitmap = BitmapFactory.decodeFile(strUserImgUrl);
			if (photoBitmap != null) {
				iv_userimg.setImageBitmap(photoBitmap);
			} else {
				iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
			}
		} else {
			iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
		}
		super.onResume();
	}

	// 点击菜单项
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		switch (position) {

		case 0: // 个人资料
			Intent intent = new Intent(getActivity(), ActiPersonalInfo.class);
			startActivity(intent);
			break;

		case 1: // 设备管理
			Intent intent3 = new Intent(getActivity(), ActiBleScan.class);
			startActivity(intent3);
			break;

		case 2: // 运动数据
			Intent intent2 = new Intent(getActivity(), ActiAlldata.class);
			startActivity(intent2);
			break;

		case 3: // 目标管理
			//FragMenuLeft.this.getActivity().getFragmentManager().beginTransaction();
			//9.7added 先把此项关掉
			Intent intent4 = new Intent(ReceiverTool.SHOW_MY_AIMS);
			getActivity().sendBroadcast(intent4);
			break;

		case 4: // 关于蓝堡  
			Intent intent5 = new Intent(getActivity(), ActiAboutLB.class);
			startActivity(intent5);
			break;

		case 5: // 购买器材
			Intent intent6 = new Intent(getActivity(), ActiBuyDevice.class);
			startActivity(intent6);
			break;

		case 6: // 闹铃提醒
			sp = getActivity().getSharedPreferences(Util.SP_FN_ALARM, Context.MODE_PRIVATE);
			editor = sp.edit();

			new AlertDialog.Builder(getActivity()).setTitle("开启闹铃提醒？").setPositiveButton("是", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 是--开启 否--空
					editor.putString(Util.SP_KEY_ALARM, "开启");
					editor.commit();
					setAlarms();
				}
			}).setNegativeButton("否", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editor.putString(Util.SP_KEY_ALARM, "");
					editor.commit();
				}
			}).create().show();
			break;

		case 7: // 设置
			Intent intent7 = new Intent(getActivity(), ActiConfig.class);
			startActivity(intent7);
			break;

		case 8: // 退出，弹出对话框供用户选择

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("您确定要注销吗？").setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					android.os.Process.killProcess(android.os.Process.myPid());// 这里目前是退出功能
				}
			}).setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create().show();

			break;

		}
	}

	// 把所有未过期的aims的时间设定进闹钟里
	@SuppressWarnings("unchecked")
	@SuppressLint("SimpleDateFormat")
	private void setAlarms() {

		AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		long timeNow = new Date().getTime();
		Log.i(TAG, "timeNow is: " + timeNow);// -----timeNow

		SharedPreferences sp = getActivity().getSharedPreferences(Util.SP_FN_TARGET, Context.MODE_PRIVATE);
		String aimsInSP = sp.getString(Util.SP_KEY_TARGET, "");
		if (!aimsInSP.isEmpty()) {
			Log.i(TAG, "aimsInSP are: " + aimsInSP);

			ArrayList<Frag_tab_target_Aim> aims = null;
			try {
				aims = UtilStream.String2SurveyList(aimsInSP);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < aims.size(); i++) {
				String timeThenStrDay = aims.get(i).getDayOfWeek();
				String timeThenStrHour = aims.get(i).getTime_hour();
				String timeThenStrMinute = aims.get(i).getTime_minute();
				String timeThenStr = timeThenStrDay + " " + timeThenStrHour + ":" + timeThenStrMinute;

				Date timeThenDate = null;
				try {
					timeThenDate = format.parse(timeThenStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long timeThen = timeThenDate.getTime();// -----timeThen

				//Log.i(TAG, "第 " + i + " 个闹铃，timeThen-timeNow  : " + (timeThen - timeNow));
				// 当前时间晚于目标设定的时间，就添加闹铃 + 9.14add 只添加当前用户的
				if  ( (timeThen > timeNow) && aims.get(i).getUid()==Util.uId ){
					Log.i(TAG, "timeThen : " + timeThen);
					Intent intent = new Intent("RECEIVE_SYSTEM_ALARM_BC");
					String howToStart = Util.sound ? "music" : "vibrate";
					intent.putExtra("howToStart", howToStart);
					Log.i(TAG, "howToStart : " + howToStart);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), i, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					am.set(AlarmManager.RTC_WAKEUP, timeThen, pendingIntent);
				}
			}
		}
	}

}