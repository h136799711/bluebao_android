package com.itboye.bluebao.actiandfrag;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.adapter.AdapterForAim;
import com.itboye.bluebao.bean.Frag_tab_target_Aim;
import com.itboye.bluebao.util.Util;
import com.itboye.bluebao.util.UtilStream;

/**
 * fragment home for tab target
 * 
 * @author Administrator
 */
public class FragTabTarget extends Fragment {

	private static final String TAG = "-----FragTabTarget";
	private static final String SP_FILE_NAME = Util.SP_FN_TARGET;
	private static final String SP_FILE_NAME_KEY = Util.SP_KEY_TARGET;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	private AdapterForAim adapter;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private ArrayList<Frag_tab_target_Aim> aims = new ArrayList<Frag_tab_target_Aim>(); // 包含所有日期的aim
	private String aimsStr = "";
	private ArrayList<Frag_tab_target_Aim> aimsForAdapter = new ArrayList<Frag_tab_target_Aim>(); // 当前日期的aims，给adapter填充数据

	private String date; // 今天日期
	private int hour;
	private int minute;
	private int goal;
	private int minHour = 0; // number picker Hour 最小值
	private int minMinute = 0;
	private int minGoal = 0;
	private Calendar calendar = Calendar.getInstance(); // 得到日历
	private Date today = new Date();// 今天
	private int todayOfWeek;// 今天是这周的第几天，从周日算起

	private TextView tv_date;// 显示当前选择的日期
	private TextView tv_1;// 周一
	private TextView tv_2;
	private TextView tv_3;
	private TextView tv_4;
	private TextView tv_5;
	private TextView tv_6;
	private TextView tv_7;

	private ListView lv__addAims;
	private ImageButton ib_toAddAim;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View layout_fragment_target = inflater.inflate(R.layout.layout_fragment_tab_target, container, false);

		// 日期初始化
		tv_date = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_date);// 界面上显示的日期

		lv__addAims = (ListView) layout_fragment_target.findViewById(R.id.frag_tab_target_lv_addAims);
		ib_toAddAim = (ImageButton) layout_fragment_target.findViewById(R.id.frag_tab_target_ib_toAddAim);
		ib_toAddAim.setOnClickListener(new MyOnClickListener());
		tv_1 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_1);
		tv_2 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_2);
		tv_3 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_3);
		tv_4 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_4);
		tv_5 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_5);
		tv_6 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_6);
		tv_7 = (TextView) layout_fragment_target.findViewById(R.id.frag_tab_target_tv_7);
		tv_1.setOnClickListener(new MyOnClickListener());
		tv_2.setOnClickListener(new MyOnClickListener());
		tv_3.setOnClickListener(new MyOnClickListener());
		tv_4.setOnClickListener(new MyOnClickListener());
		tv_5.setOnClickListener(new MyOnClickListener());
		tv_6.setOnClickListener(new MyOnClickListener());
		tv_7.setOnClickListener(new MyOnClickListener());

		return layout_fragment_target;

	}

	@Override
	public void onResume() {
		super.onResume();

		date = df.format(new Date());// 当前系统日期
		tv_date.setText(date);
		todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);// 初始化todayOfWeek
		updateTheColor(todayOfWeek - 1);
		showAimsOfTheDay(date);// adapter 初始化

	}

	private class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			// ==添加aim=====
			case R.id.frag_tab_target_ib_toAddAim:

				// ==设定目标的弹出框中的时间和卡路里量的设置==========
				// 测试 弹出框让用户选择，弹出框是自定义的
				LinearLayout dialog_aim = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_aim,
						null);

				// picTime and picGoal
				NumberPicker picTimeHour = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_time_hour);
				NumberPicker picTimeMinute = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_time_minute);
				NumberPicker picGoal = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_goal);

				// 小时数
				picTimeHour.setMinValue(0);
				picTimeHour.setMaxValue(23);
				picTimeHour.setValue(minHour); // 默认显示数值
				picTimeHour.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						hour = newVal;
					}
				});

				// 分钟数
				picTimeMinute.setMinValue(0);
				picTimeMinute.setMaxValue(59);
				picTimeMinute.setValue(minMinute); // 默认显示数值
				picTimeMinute.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						minute = newVal;
					}
				});

				// 卡路里数
				picGoal.setMinValue(0);
				picGoal.setMaxValue(1000);
				picGoal.setValue(minGoal); // 默认显示数值
				picGoal.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						goal = newVal;
					}
				});

				// ==设定目标的弹出框==========

				// AlertDialog
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("设定目标").setView(dialog_aim) // 自定义弹出框样式
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								int duration = 60 * hour + minute;// 计算目标的时间，转换为分钟

								if (duration != 0 && goal != 0) {
									Log.i(TAG, "要设置的闹铃的uid是：" + Util.uId);
									Frag_tab_target_Aim aimTemp = new Frag_tab_target_Aim();
									aimTemp.setUid(Util.uId);// 9.14 add
									aimTemp.setDayOfWeek(tv_date.getText() + ""); // 当前日期
									aimTemp.setTime_hour(hour + "");
									aimTemp.setTime_minute(minute + "");
									aimTemp.setGoal(goal + "");

									Log.i(TAG,
											"aimTemp's uid: " + aimTemp.getUid() + "aimTemp's dayofweek: "
													+ aimTemp.getDayOfWeek() + "aimTemp's timeHour: " + aimTemp.getTime_hour()
													+ "aimTemp's timeMinute: " + aimTemp.getTime_minute() + "aimTemp's goal: "
													+ aimTemp.getGoal());

									// start 8.26 added
									// 如果有重复aim则不添加
									String temp = sp.getString(SP_FILE_NAME_KEY, "");
									if (!temp.isEmpty()) {
										
										try {
											@SuppressWarnings("unchecked")
											ArrayList<Frag_tab_target_Aim> aimsAlreadyHave = UtilStream.String2SurveyList(temp);
											
											for (int i = 0; i < aimsAlreadyHave.size(); i++) {
												Frag_tab_target_Aim tempAim = aimsAlreadyHave.get(i);
												Log.i(TAG,
														"tempAim uid: " + tempAim.getUid() + "tempAim dayofweek: "
																+ tempAim.getDayOfWeek()  + "tempAim timeHour: "
																+ tempAim.getTime_hour()  + "tempAim timeMinute: "
																+ tempAim.getTime_minute()  + "tempAim goal: "
																+ tempAim.getGoal());

												if (tempAim.getUid() == aimTemp.getUid()
														&& tempAim.getDayOfWeek().equals(aimTemp.getDayOfWeek())
														&& tempAim.getGoal().equals(aimTemp.getGoal())
														&& tempAim.getTime_hour().equals(aimTemp.getTime_hour())
														&& tempAim.getTime_minute().equals(aimTemp.getTime_minute())) {
													Log.i(TAG, "有目标重复设置了");
													return;
												}

											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									// end 8.26 added

									Log.i(TAG, "保存之前，最后一次显示新添加的aim的uid：" + aimTemp.getUid());
									aims.add(aimTemp);// aims中包含所有日期的目标，将新添加的目标保存到aims中
									aimsForAdapter.add(aimTemp); // 将新添加的目标也保存到listview的adapter中
									adapter.notifyDataSetChanged();// 更新数据
									
									//Log.i(TAG, "9.16added-添加新aim之后重新初始化adapter，防止第一次安装添加第一个aim之后直接修改或删除时崩溃-测试阶段");
									//adapter = new AdapterForAim(getActivity(), aimsForAdapter, aims); // 9.16added 

									try {// 把包含所有aim的aims写进SP
										aimsStr = UtilStream.SurveyList2String(aims);
										//9.16 added
										if( !aimsStr.isEmpty() ){
											Log.i(TAG, "第一次添加新aim之前UtilStream.SurveyList2String(aims)不空");
											editor = sp.edit();
											editor.putString(SP_FILE_NAME_KEY, aimsStr);
											editor.commit(); // 把新建的aim添加到包含所有日期的aims中去
										}
										//9.16 added
										/*editor = sp.edit();
										editor.putString(SP_FILE_NAME_KEY, aimsStr);
										editor.commit(); // 把新建的aim添加到包含所有日期的aims中去
*/									} catch (IOException e) {
										e.printStackTrace();
									}

									// 添加闹铃提醒
									Util.setAlarms(getActivity());

								} else {// 时间或卡路里量中有空值
									Toast.makeText(getActivity(), "目标设定有误，请重新设定！", Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create().show(); // 创建并展示
				break;

			case R.id.frag_tab_target_tv_1:
				int a1 = 2 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a1);
				Date day1 = calendar.getTime();

				tv_date.setText(df.format(day1));
				updateTheColor(1);
				showAimsOfTheDay(df.format(day1));
				break;

			case R.id.frag_tab_target_tv_2:
				int a2 = 3 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a2);
				Date day2 = calendar.getTime();

				tv_date.setText(df.format(day2));
				updateTheColor(2);
				showAimsOfTheDay(df.format(day2));
				break;

			case R.id.frag_tab_target_tv_3:
				int a3 = 4 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a3);
				Date day3 = calendar.getTime();

				tv_date.setText(df.format(day3));
				updateTheColor(3);
				showAimsOfTheDay(df.format(day3));
				break;

			case R.id.frag_tab_target_tv_4:
				int a4 = 5 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a4);
				Date day4 = calendar.getTime();

				tv_date.setText(df.format(day4));
				updateTheColor(4);
				showAimsOfTheDay(df.format(day4));
				break;

			case R.id.frag_tab_target_tv_5:
				int a5 = 6 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a5);
				Date day5 = calendar.getTime();

				tv_date.setText(df.format(day5));
				updateTheColor(5);
				showAimsOfTheDay(df.format(day5));
				break;

			case R.id.frag_tab_target_tv_6:
				int a6 = 7 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a6);
				Date day6 = calendar.getTime();

				tv_date.setText(df.format(day6));
				updateTheColor(6);
				showAimsOfTheDay(df.format(day6));
				break;

			case R.id.frag_tab_target_tv_7:
				int a7 = 8 - todayOfWeek;

				calendar.setTime(today);
				calendar.add(Calendar.DAY_OF_MONTH, a7);
				Date day7 = calendar.getTime();
				Log.i(TAG, "day7 is: " + df.format(day7));
				tv_date.setText(df.format(day7));
				updateTheColor(7);
				showAimsOfTheDay(df.format(day7));
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void showAimsOfTheDay(String date) {
		aimsForAdapter.clear();
		Log.i(TAG, "aimsForAdapter.clear()执行完了");
		sp = getActivity().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
		try {
			String spContent = sp.getString(SP_FILE_NAME_KEY, "");
			Log.i(TAG, "sp.getString(SP_FILE_NAME_KEY--执行完了，spContent is："+ spContent);
			aimsStr = spContent;// 初始化aimsStr

			//第一次安装，第一次设定aim之后直接删除或者修改崩溃的原因是这里的allAims没有初始化 9.16修改此bug
			ArrayList<Frag_tab_target_Aim> allAims = new ArrayList<Frag_tab_target_Aim>();

			if (!spContent.isEmpty()) {
				allAims = UtilStream.String2SurveyList(spContent);
				aims = allAims;// 初始化aims
				// 从allAims中解析出 当前日期 的aims
				Log.i(TAG, "showAimsOfTheDay--Util.uId:" + Util.uId);
				for (int i = 0; i < allAims.size(); i++) {
					Frag_tab_target_Aim aimtemp = allAims.get(i);
					Log.i(TAG, "showAimsOfTheDay--aimtemp.getUid():" + aimtemp.getUid());
					if (aimtemp.getUid() == Util.uId && date.equals(aimtemp.getDayOfWeek())) {
						aimsForAdapter.add(aimtemp);// 把所有当天的aim放入ArrayList中
					}
				}
			}

			adapter = new AdapterForAim(getActivity(), aimsForAdapter, allAims); // 第一次安装的时候也可以正常运行
			adapter.notifyDataSetChanged();
			lv__addAims.setAdapter(adapter);

		} catch (Exception e) {
			Toast.makeText(getActivity(), "请设定目标!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	// 改变周一周二的颜色
	private void updateTheColor(int i) {

		switch (i) {
		case 1:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 2:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 3:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 4:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 5:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 6:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			break;
		case 7:
			tv_1.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_2.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_3.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_4.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_5.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_6.setTextColor(getResources().getColorStateList(R.color.colorLightGray2));
			tv_7.setTextColor(getResources().getColorStateList(R.color.colorBlack));
			break;
		}
	}
}