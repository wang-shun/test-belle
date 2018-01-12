package com.wonhigh.bs.sso.server.common.model;

import java.io.Serializable;

import com.wonhigh.bs.sso.server.common.util.MD5Util;
import com.wonhigh.bs.sso.server.common.util.RandomStringUtil;


public class SsoToken implements Serializable{
	
	private static final long serialVersionUID = -2392527529364092304L;

	private String token; //生成的唯一字符串
	
	private SsoUser ssoUser; //登录的sso用户

	private Long loginTime; //登录时间

	private String randomSalt; //每次登录随机生成的字符串，作为salt 生成token
	
	private String deviceId; //当前登录的设备id
	
	private SsoToken() {}

	public SsoToken(SsoUser ssoUser, String deviceId) {
		this.ssoUser = ssoUser;
		this.loginTime = System.currentTimeMillis();
		this.randomSalt = RandomStringUtil.getRandomCode(32, 7);
		this.deviceId = deviceId;
		this.token = MD5Util.encode((MD5Util.encode(this.randomSalt)+this.randomSalt+this.ssoUser.toString()+this.loginTime)+this.deviceId);
	}

	public String getToken() {
		return token;
	}

	public SsoUser getSsoUser() {
		return ssoUser;
	}

	public void setSsoUser(SsoUser ssoUser) {
		this.ssoUser = ssoUser;
	}

	public Long getLoginTime() {
		return loginTime;
	}
	
	public void setLoginTime(Long loginTime) {
		this.loginTime=loginTime;
	}

	public String getDeviceId() {
		return deviceId;
	}

}