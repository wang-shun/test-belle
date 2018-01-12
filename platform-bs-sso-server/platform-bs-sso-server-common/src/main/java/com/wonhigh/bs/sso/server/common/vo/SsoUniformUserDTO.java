package com.wonhigh.bs.sso.server.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * ssoUser mysql 统一登录用户实体类 
 * 
 * @author user
 * @date 2017年11月18日 下午12:07:59
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SsoUniformUserDTO implements Serializable{
	
	private static final long serialVersionUID = 636306404233429099L;

	private Integer id;
	
	/**
	 * 登录名
	 */
    private String loginName;
	/**
	 * 真实姓名
	 */
    private String sureName;
	
	/**
	 * 手机
	 */
	private String mobile;
	
	/**
	 * 电话
	 */
	private String telephoneNumber;
	
	/**
	 * 工号
	 */
	private String employeeNumber;
	
	/**
	 * 职位，员工类型
	 */
    private String employeeType;
	
	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 绑定的biz系统账户 json:{"bizCode":"bizLoginName","bizCode":"bizLoginName"}
	 */
	private String bizUser = "{}";
	
	/**
	 * 所属机构
	 */
	private String organizationalUnitName;
	
	/**
	 * 所属机构代码 
	 */
	private String organizationCode;
	
	/**
	 * 所属机构id 
	 */
	private Integer unitId;
	
	/**
	 * 创建者
	 */
	private String createUser;
	
	private Integer createUserId;
	
	private Date updateTime;
	
	private Date createTime;
	
	/**
	 * 性别 1男 0女
	 */
	private Integer sex = 1;
	
	/**
	 * 状态  0：未激活，1：正常，2已锁定
	 */
	private Integer state = 1;

	/**
	 * 1已删除 0未删除 
	 */
	private Integer delFlag = 0;
	
	/**
	 * 其它描述信息 如 所在部门 创建人
	 */
	private String description;
	
	/**
	 * 身份证号码
	 */
	private String idCard;
	 
	/**
	 * 数据库查询sql
	 */
	private String queryCondition;

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getSureName() {
		return sureName;
	}

	public void setSureName(String sureName) {
		this.sureName = sureName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBizUser() {
		return bizUser;
	}

	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
	}

	public String getOrganizationalUnitName() {
		return organizationalUnitName;
	}

	public void setOrganizationalUnitName(String organizationalUnitName) {
		this.organizationalUnitName = organizationalUnitName;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Override
	public String toString() {
		return "SsoUniformUserDTO [id=" + id + ", loginName=" + loginName + ", sureName=" + sureName + ", mobile="
				+ mobile + ", telephoneNumber=" + telephoneNumber + ", employeeNumber=" + employeeNumber
				+ ", employeeType=" + employeeType + ", email=" + email + ", password=" + password + ", bizUser="
				+ bizUser + ", organizationalUnitName=" + organizationalUnitName + ", organizationCode="
				+ organizationCode + ", unitId=" + unitId + ", createUser=" + createUser + ", createUserId="
				+ createUserId + ", updateTime=" + updateTime + ", createTime=" + createTime + ", sex=" + sex
				+ ", state=" + state + ", delFlag=" + delFlag + ", description=" + description + ", idCard=" + idCard
				+ ", queryCondition=" + queryCondition + "]";
	}


}