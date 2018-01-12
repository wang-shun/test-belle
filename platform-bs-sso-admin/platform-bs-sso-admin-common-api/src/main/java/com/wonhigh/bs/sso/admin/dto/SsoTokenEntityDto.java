package com.wonhigh.bs.sso.admin.dto;

import java.io.Serializable;

/**
 * app端统一登录token
 * 
 * @author user
 * @date 2017年11月12日 下午3:41:46
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SsoTokenEntityDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 一账通登录名，3-20位
	 */
	private String ssoUser;
	/**
	 * 业务系统编号，小于8位
	 */
	private String bizCode;
	/**
	 * 业务系统用户名，小于20位
	 */
	private String bizUser;
	
	/**
	 * 失效时间
	 */
	private Long timeMillis;

	public Long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
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
}