package com.itboye.bluebao.bean;

/**
 * Access_token类
 * 获取access_token时服务器返回的json格式数据封装成的类
 * @author Administrator
 *
 */
public class Access_token {
	
	//{“code”:0,”info":{"access_token":"2c3084b8a7decb6fd39ebd7cfd7d5f8430f01430","expires_in":3600,"token_type":"Bearer","scope":"base"}}
	
	public int code;
	public Info info;
	
	public int getCode() {
		return code;
	}

	public Info getInfo() {
		return info;
	}

	public static class Info {
		public String access_token;
		public String expires_in;
		public String token_type;
		public String scope;
		public long get_time;//获取access_token的时间，新加的成员
		public String getAccess_token() {
			return access_token;
		}
		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
		public String getExpires_in() {
			return expires_in;
		}
		public void setExpires_in(String expires_in) {
			this.expires_in = expires_in;
		}
		public String getToken_type() {
			return token_type;
		}
		public void setToken_type(String token_type) {
			this.token_type = token_type;
		}
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public long getGet_time() {
			return get_time;
		}
		public void setGet_time(long get_time) {
			this.get_time = get_time;
		}
		
	}
	
	
}