package com.wonhigh.bs.sso.server.dto;

import java.io.Serializable;
import java.util.List;

public class SsoUserEntityDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 登录名，3-20位
	 */
	private String name;
	/**
	 * 密码，6-20位
	 */
	private String password;
	/**
	 * 手机号，长度仅在6-20位，只能填写1个手机号码
	 */
	private String phone;
	/**
	 * email，须遵守email地址格式，长度小于80位
	 */
	private String email;
	/**
	 * 职工工号，在工号未知的情况下，同登录名，小于40位
	 */
	private String employeeId;
	/**
	 * 真实姓名，选填，长度小于20位
	 */
	private String realName;
	/**
	 * 性别，选填，0：女，1：男
	 */
	private Integer gender;
	/**
	 * 部门，选填，长度小于40
	 */
	private String department;
	/**
	 * 业务系统关联用户，选填，SSO账户关联的业务系统账户列表
	 */
	private List<BizUserEntityDto> bizUsers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<BizUserEntityDto> getBizUsers() {
		return bizUsers;
	}

	public void setBizUsers(List<BizUserEntityDto> bizUsers) {
		this.bizUsers = bizUsers;
	}
}