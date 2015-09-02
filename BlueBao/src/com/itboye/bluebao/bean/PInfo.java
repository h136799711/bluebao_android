package com.itboye.bluebao.bean;

/**
 * 用户信息 类
 * 把和用户有关的所有信息封装成类，保存在SP中，或者将一部分字段提交到服务器
 * @author Administrator
 *
 */
public class PInfo {
	
	private int uid;
	private String username;
	private String password;//密码
	private String realname;
	private String nickname;
	
	private String signature;//签名
	private String BMI;
	
	private int gender;
	private int age;
	private int height;
	private int weight;
	private int weightTarget;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getWeightTarget() {
		return weightTarget;
	}
	public void setWeightTarget(int weightTarget) {
		this.weightTarget = weightTarget;
	}
	public String getBMI() {
		return BMI;
	}
	public void setBMI(String bMI) {
		BMI = bMI;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}

	
	
	
}