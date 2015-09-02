package com.itboye.bluebao.bean;

import java.io.Serializable;


/**
 * frag_tab_target中添加目标，将其封装成类。
 * 
 * implements Serializable，实现了序列化借口
 * 
 * dayOfWeek 星期几
 * time_hour,time_minute 小时数和分钟数，goal 多久完成多少任务量。
 * 这些数据要保存在SharedPreferences中，
 * frag_tab_target中的ListView要从其中读取这些数据。
 * @author Administrator
 *
 */
public class Frag_tab_target_Aim implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String dayOfWeek;
	private String time_hour;
	private String time_minute;
	private String goal;
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getTime_hour() {
		return time_hour;
	}
	public void setTime_hour(String time_hour) {
		this.time_hour = time_hour;
	}
	public String getTime_minute() {
		return time_minute;
	}
	public void setTime_minute(String time_minute) {
		this.time_minute = time_minute;
	}
	public String getGoal() {
		return goal;
	}
	public void setGoal(String goal) {
		this.goal = goal;
	}
}
