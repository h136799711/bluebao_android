package com.itboye.bluebao.bean;

/**
 * BestScoreBean类
 * frag_tab_pcenter中最好成绩
 * @author Administrator
 *
 */
public class BestScoreBean {
	
	//{"code":0,"data":[{"best_distance":"60","best_calorie":"6250","best_cost_time":"3119"}]}
	
	public int code;
	public Data data;
	
	public int getCode() {
		return code;
	}
	public Data getData() {
		return data;
	}

	public static class Data {
		public String best_distance;
		public String best_calorie;
		public String best_cost_time;
		public String getBest_distance() {
			return best_distance;
		}
		public String getBest_calorie() {
			return best_calorie;
		}
		public String getBest_cost_time() {
			return best_cost_time;
		}
	}
}