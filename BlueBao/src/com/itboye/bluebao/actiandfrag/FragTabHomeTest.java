package com.itboye.bluebao.actiandfrag;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.itboye.bluebao.R;

/**
 * fragment home for tab home
 * 
 * @author Administrator
 *
 */
public class FragTabHomeTest extends Fragment {

	private PieChart pc_aimandpercent;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout_fragment_home = inflater.inflate(R.layout.layout_fragment_tab_home_test, container, false);

		pc_aimandpercent = (PieChart) layout_fragment_home.findViewById(R.id.frag_tab_home_test_ibtn_aimandpercent);

		PieData mPieData = getPieData(2, 100); // 0到100，分2份

		showChart(pc_aimandpercent, mPieData);

		return layout_fragment_home;
	}

	private void showChart(PieChart pieChart, PieData pieData) {
		
		// 圆中间部分是否透明，注释掉之后中间区域显示为白色
		// pieChart.setHoleColorTransparent(true);
		
		pieChart.setHoleRadius(70f); // 半径,半径越大，圆环越窄
		//pieChart.setTransparentCircleRadius(64f); // 半透明圈
		//pieChart.setHoleRadius(0); //实心圆
		//pieChart.setDrawHoleEnabled(false); //是否在中间空出一个圆，false：实心圆  true：环
		
		//饼图 右下角的注释,不写的话会显示 description，所以让其显示“”
		pieChart.setDescription("");
		//pieChart.setDescription("测试饼状图");

		 //mChart.setDrawYValues(true);
		pieChart.setDrawCenterText(true); // 饼状图中间可以添加文字
		//TODO 获取数据之后填写在这里-------------------------------------------------------------------
		pieChart.setCenterText("目标  100"); 
		

		pieChart.setRotationAngle(270); // 初始旋转角度

		// draws the corresponding description value into the slice
		// mChart.setDrawXValues(true);

		pieChart.setRotationEnabled(true); // 是否可以手动旋转

		pieChart.setUsePercentValues(true); // 显示成百分比
		// mChart.setUnit(" €");
		// mChart.setDrawUnitsInChart(true);

		// add a selection listener
		// mChart.setOnChartValueSelectedListener(this);
		// mChart.setTouchEnabled(false);

		// mChart.setOnAnimationListener(this);

		

		// 设置数据
		pieChart.setData(pieData);

		// undo all highlights
		//pieChart.highlightValues(null);
		//pieChart.invalidate();

		
		//Legend 图例，右上角的总结（什么颜色代表哪一部分这些内容）
		Legend mLegend = pieChart.getLegend(); // 设置比例图
		mLegend.setEnabled(false); //不允许显示这一部分内容
		//mLegend.setPosition(LegendPosition.RIGHT_OF_CHART); // 最右边显示
		// mLegend.setForm(LegendForm.LINE); //设置比例图的形状，默认是方形
		//mLegend.setXEntrySpace(7f);
		//mLegend.setYEntrySpace(5f);

		pieChart.animateXY(1000, 1000); // 设置动画
		// mChart.spin(2000, 0, 360);
	}

	/**
	 * 
	 * @param count
	 *            分成几部分
	 * @param range
	 */
	private PieData getPieData(int count, float range) {

		ArrayList<String> xValues = new ArrayList<String>(); // xVals用来表示每个饼块上的内容
		xValues.add("");
		xValues.add("");

		ArrayList<Entry> yValues = new ArrayList<Entry>(); // yVals用来表示封装每个饼块的实际数据

		//TODO 获取数据之后填写在这里-------------------------------------------------------------------
		yValues.add(new Entry(15, 0)); //已完成 15%
		yValues.add(new Entry(85, 1)); //未完成  85%
		
		// y轴的集合
		//PieDataSet pieDataSet = new PieDataSet(yValues, "Quarterly Revenue 2014"/* 显示在比例图上 */);
		PieDataSet pieDataSet = new PieDataSet(yValues, "Quarterly Revenue 2014"/* 显示在比例图上 */);
		pieDataSet.setSliceSpace(2f); // 设置个饼状图之间的距离

		ArrayList<Integer> colors = new ArrayList<Integer>();

		// 饼图颜色
		colors.add(Color.parseColor("#148EE1"));
		colors.add(Color.LTGRAY);
		//colors.add(Color.BLUE);
		//colors.add(Color.parseColor("#E8F2FE"));
		//colors.add(Color.rgb(255, 123, 124));// 品红色
		//colors.add(Color.rgb(205, 205, 205));// 灰色
		//colors.add(Color.rgb(114, 188, 223));
		//colors.add(Color.rgb(57, 135, 200));
		pieDataSet.setColors(colors);

		// 点击一部分时，拓宽的幅度
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = 5 * (metrics.densityDpi / 360f);
		pieDataSet.setSelectionShift(px); // 选中态多出的长度

		PieData pieData = new PieData(xValues, pieDataSet);
		return pieData;
	}
}
