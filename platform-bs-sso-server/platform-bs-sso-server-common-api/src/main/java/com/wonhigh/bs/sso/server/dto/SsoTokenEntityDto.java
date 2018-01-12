package com.wonhigh.bs.sso.server.dto;

import java.io.Serializable;

public class SsoTokenEntityDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int state; //1验证成功 0验证失败
	
	private String msg;

	private String loginName; //业务系统登录名
	
	private String bizCode; //业务系统编号
	
	private Long loginTime; //sso账号登录时间

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}
	
}