package com.itboye.bluebao.bean;

/**
 * 从服务器返回的某月的数据，code 和 data两个字段
 * 为得到data部分数组的长度
 * data是一个数组
 * {
“code":0,
“data":[
	{“max_calorie":"76","upload_day":"26"},
	{“max_calorie”:"64","upload_day":"27"},
	{“max_calorie":"78","upload_day":"30"}
	]
}
 * @author Administrator
 *
 */
public class DataFromServerMonthOuterBean {
		private String code;
		private DataBean[] data;
	
		public String getCode() {
			return code;
		}
		public DataBean[] getDatas() {
			return data;
		}

		public static class DataBean{
		private String max_calorie;
		private String upload_day;
		public String getMax_calorie() {
			return max_calorie;
		}
		public String getUpload_day() {
			return upload_day;
		}
		}
	
}
