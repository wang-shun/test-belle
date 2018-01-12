package com.wonhigh.bs.sso.admin.common.model;

import java.util.UUID;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午4:07:05
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SsoToken {
	private String ssoUser;

	private String bizCode;

	private String bizUser;

	private Long timeMillis;

	private String randomString;
	
	public SsoToken() {
		this.timeMillis = System.currentTimeMillis() + 60 * 1000;
		this.randomString = UUID.randomUUID().toString();
	}
	public SsoToken(String ssoUser, String bizCode, String bizUser) {
		this();
		this.ssoUser = ssoUser;
		this.bizCode = bizCode;
		this.bizUser = bizUser;
	}

	public String getSsoUser() {
		return ssoUser;
	}

	public void setSsoUser(String ssoUser) {
		this.ssoUser = ssoUser;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getBizUser() {
		return bizUser;
	}

	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
	}

	public Long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public String getRandomString() {
		return randomString;
	}
}