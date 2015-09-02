package com.itboye.bluebao.bean;

/**
 * 从服务器返回的某天的数据，code 和 data两个字段
 * @author Administrator
 *
 */
public class DataFromServerDayBean {
	
	private int code;
	private Data data;
	public int getCode() {
		return code;
	}
	public Data getData() {
		return data;
	}

	public static class Data{
		private String speed;
		private String heart_rate;
		private String distance;//路程
		private String total_distance;//总路程
		private String cost_time;
		private String calorie;
		private String target_calorie;//目标
		public String getSpeed() {
			return speed;
		}
		public String getHeart_rate() {
			return heart_rate;
		}
		public String getDistance() {
			return distance;
		}
		public String getTotal_distance() {
			return total_distance;
		}
		public String getCost_time() {
			return cost_time;
		}
		public String getCalorie() {
			return calorie;
		}
		public String getTarget_calorie() {
			return target_calorie;
		}
	}
	

}
