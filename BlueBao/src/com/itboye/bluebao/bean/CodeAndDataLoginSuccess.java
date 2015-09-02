package com.itboye.bluebao.bean;

/**
 * 从服务器返回的消息，所有服务code 和 data两个字段的
 * @author Administrator
 *
 */
public class CodeAndDataLoginSuccess {
	
	private int code;
	private Data data;

	public int getCode() {
		return code;
	}

	public Data getData() {
		return data;
	}


	public static class Data{

		public String username;
		public String realname;
		public String nickname;
		public String email;
		//public int mobile;
		public int uid;
		//public int idnumber;
		public String birthday;
		public String last_login_ip;
		//public long last_login_time;
		//public long update_time;
		
		//public int avatar_id;
		public int height;
		public float weight;
		public float target_weight;
		public String signature;
		public int continuous_day;
		public int getUid() {
			return uid;
		}
		public void setUid(int uid) {
			this.uid = uid;
		}
		
		
	}
	
}
