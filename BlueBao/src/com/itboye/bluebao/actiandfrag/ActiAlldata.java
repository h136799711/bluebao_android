package com.itboye.bluebao.actiandfrag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.DataFromServerMonthOuterBean;
import com.itboye.bluebao.ble.SampleGattAttributes;
import com.itboye.bluebao.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;

/**
 * 运动数据activity
 * @author Administrator
 */
@SuppressLint("SimpleDateFormat")
public class ActiAlldata extends Activity implements OnClickListener {

	private static final String TAG = "----ActiAlldata";

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
	private Calendar calendar = Calendar.getInstance();
	private Date thisMonth = new Date();
	private Gson gson = new Gson();
	private HttpUtils httpUtils = new HttpUtils();
	// 从服务器获取数据的data部分
	private ArrayList<DataFromServerMonthOuterBean.DataBean> datas;

	private LineChart lc_chat;
	private ImageButton ibtn_previous;
	private ImageButton ibtn_next;
	private TextView tv_month;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_alldata);
		lc_chat = (LineChart) findViewById(R.id.acti_adddata_lc_alldata);
		ibtn_previous = (ImageButton) findViewById(R.id.acti_alldata_ibtn_previous);
		ibtn_next = (ImageButton) findViewById(R.id.acti_alldata_ibtn_next);
		tv_month = (TextView) findViewById(R.id.acti_alldata_tv_date);
		ibtn_previous.setOnClickListener(this);
		ibtn_next.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		datas = new ArrayList<DataFromServerMonthOuterBean.DataBean>();// 初始化
		// 显示月份
		String thisMonthStr = format.format(thisMonth);
		tv_month.setText(thisMonthStr);
		// 展示本月数据
		showTheMonthData(thisMonthStr);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		String monthNow = tv_month.getText().toString();
		//Log.i(TAG, "monthNow is :" + tv_month.getText().toString());
		Date monthDate = null;
		try {
			monthDate = format.parse(monthNow);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//Log.i(TAG, "monthNow parse  to Date is  :" + format.format(monthDate));

		calendar.setTime(monthDate);

		switch (v.getId()) {
		case R.id.acti_alldata_ibtn_previous:
			calendar.add(Calendar.MONTH, -1);
			Date nowP = calendar.getTime();
			tv_month.setText(format.format(nowP));
			showTheMonthData(format.format(nowP));
			break;

		case R.id.acti_alldata_ibtn_next:
			calendar.add(Calendar.MONTH, 1);
			Date nowN = calendar.getTime();
			tv_month.setText(format.format(nowN));
			showTheMonthData(format.format(nowN));
			break;
		}
	}

	// 展示给定月份的数据
	private void showTheMonthData(String month) {
		// 从服务器取得数据，加载到LineData中（数据获取成功之后，因为是异步获取），然后展示出来
		getDataFromServer(month);
	}

	private void getDataFromServer(final String month) {

		Date theGivenMonth = null;
		try {
			theGivenMonth = format.parse(month);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long time = theGivenMonth.getTime() / 1000;

		// 从服务器获取数据
		String token = Util.getAccessToken(ActiAlldata.this);
		if (token.isEmpty()) {
			try {
				Thread.sleep(2000);
				token = Util.getAccessToken(ActiAlldata.this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!token.isEmpty()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", Util.uId + "");
			params.addBodyParameter("uuid", SampleGattAttributes.SERVICE_I_NEED);
			params.addBodyParameter("time", time + "");

			String urlTheMonthData = Util.urlGetTheMonthData + token;

			httpUtils.send(HttpMethod.POST, urlTheMonthData, params, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(TAG, "获取TheMonth数据失败：" + arg1);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					Log.i(TAG, "获取TheMonth数据成功：" + arg0.result);
					lc_chat.setVisibility(View.VISIBLE);
					if (arg0.result.length() > 30) {
						DataFromServerMonthOuterBean bean = gson.fromJson(arg0.result, DataFromServerMonthOuterBean.class);
						for (int i = 0; i < bean.getDatas().length; i++) {
							//Log.i(TAG, "innerBean  " + bean.getDatas()[i].getUpload_day() + "  " + bean.getDatas()[i].getMax_calorie());
							datas.add(bean.getDatas()[i]);// 数据源
						}
						// 准备要显示的数据
						LineData ldata = getDataUse(datas, month);
						setupChart(lc_chat, ldata);
						datas.clear();// 使用过之后清除其中数据
					} else {
						lc_chat.clear();
					}
				}
			});
		}
	}

	private LineData getDataUse(ArrayList<DataFromServerMonthOuterBean.DataBean> datas, String month) {
		String yearStr = month.substring(0, 4);
		String monthStr = month.substring(5, 7);
		int yearInt = Integer.parseInt(yearStr);
		int monthInt = Integer.parseInt(monthStr);
		int monthDays = getHowManyDaysInTheMonthOfTheYear(yearInt, monthInt);
		Log.i(TAG, "年份：" + yearInt + "  月份： " + monthInt + "  天数：" + monthDays);
		// x轴显示的数据，这里默认使用数字下标显示
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 1; i <= monthDays; i++) {
			xVals.add(i + "");
		}
		// y轴的数据
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		for (int j = 0; j < datas.size(); j++) {
			int cal = Integer.parseInt(datas.get(j).getMax_calorie());
			int ud = Integer.parseInt(datas.get(j).getUpload_day());
			yVals.add(new Entry(cal, ud - 1));
		}
		// y轴的数据集合
		LineDataSet set1 = new LineDataSet(yVals, "");

		// 数据线
		set1.setLineWidth(1.75f); // 线宽
		set1.setCircleSize(3f);// 显示的圆形大小
		set1.setColor(getResources().getColor(R.color.colorActiAlldata_dataline));// 显示颜色
		set1.setCircleColor(getResources().getColor(R.color.colorActiAlldata_dataline));// 圆形的颜色
		set1.setHighLightColor(getResources().getColor(R.color.colorActiAlldata_dataline)); // 高亮的线的颜色

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1); // add the datasets

		LineData data = new LineData(xVals, dataSets);

		return data;
	}

	private void setupChart(LineChart chart, LineData data) {
		chart.setDescription("");// 数据描述
		chart.getXAxis().setPosition(XAxisPosition.BOTTOM);// x轴放下边
		chart.getAxisRight().setDrawLabels(false);// 显示右侧y轴，但是没有描述
		chart.getXAxis().setSpaceBetweenLabels(1);// 隔几个显示一个
		chart.getXAxis().setDrawAxisLine(true);
		chart.setNoDataTextDescription("所选月份没有数据");
		chart.setTouchEnabled(true);
		chart.setDragEnabled(true);
		chart.setScaleEnabled(true);
		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setData(data); // 设置数据
		chart.animateX(2500); // 立即执行的动画,x轴
	}

	private int getHowManyDaysInTheMonthOfTheYear(int year, int month) {
		int days = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			days = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			days = 30;
			break;
		case 2:
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				days = 29;
			} else {
				days = 28;
			}
			break;
		}
		return days;
	}
}