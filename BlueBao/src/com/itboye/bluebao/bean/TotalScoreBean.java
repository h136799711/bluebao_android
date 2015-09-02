package com.itboye.bluebao.bean;

/**
 * TotalScoreBean类
 * frag_tab_pcenter中总成绩
 * @author Administrator
 *
 */
public class TotalScoreBean {
	
	//{"code":0,"data":{"sum_max_calorie":null,"sum_max_distance":null,"sum_max_time":null}}
	
	public int code;
	public Data data;
	
	public int getCode() {
		return code;
	}
	public Data getData() {
		return data;
	}

	public static class Data {
		public String sum_max_calorie;
		public String sum_max_distance;
		public String sum_max_time;
		public String getSum_max_calorie() {
			return sum_max_calorie;
		}
		public String getSum_max_distance() {
			return sum_max_distance;
		}
		public String getSum_max_time() {
			return sum_max_time;
		}
	}
}