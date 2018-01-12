package com.wonhigh.bs.sso.admin.dto;

import java.io.Serializable;

/**
 * 业务系统
 * 
 * @author user
 * @date 2017年11月12日 下午3:42:14
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class BizUserEntityDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 业务系统编号，小于8位
	 */
	private String bizCode;
	/**
	 * 业务系统用户名，小于20位
	 */
	private String bizUserName;
	/**
	 * 业务系统用户密码，选填，小于40位
	 */
	private String bizPassword;

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getBizUserName() {
		return bizUserName;
	}

	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}

	public String getBizPassword() {
		return bizPassword;
	}

	public void setBizPassword(String bizPassword) {
		this.bizPassword = bizPassword;
	}
}