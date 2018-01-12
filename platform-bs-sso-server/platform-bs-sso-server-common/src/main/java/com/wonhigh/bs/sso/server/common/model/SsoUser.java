package com.wonhigh.bs.sso.server.common.model;

import java.io.Serializable;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

import com.wonhigh.bs.sso.server.common.constants.ApiConstants;

@Entry(objectClasses = { "SsoUser", "top" })
public class SsoUser implements Serializable {

	@Transient
	private static final long serialVersionUID = 6363064026174349099L;

	@Id
	private Name dn;

	@DnAttribute(value = "uid", index = 1)
	private String uid;

	@Transient
	@DnAttribute(value = "ou", index = 0)
	private String dn2;

	//loginName primarykey
	@Attribute(name = "uid")
	private String loginName;

	//真实姓名
	@Attribute(name = "sn")
	private String sureName;

	//手机
	@Attribute(name = "mobile")
	private String mobile = "0";

	//电话
	@Attribute(name = "telephoneNumber")
	private String telephoneNumber = "0";

	//工号
	@Attribute(name = "employeeNumber")
	private String employeeNumber = "NULL";

	//职位 
	@Attribute(name = "employeeType")
	private String employeeType = "NULL";

	//邮箱
	@Attribute(name = "mail")
	private String email = "NULL";

	@Attribute(name = "userPassword")
	private String password;

	//绑定的biz系统账户 json:{"bizCode":"bizLoginName","bizCode":"bizLoginName"}
	@Attribute(name = "biz-user")
	private String bizUser = "{}";

	//所属机构
	@Attribute(name = "organizationalUnitName")
	private String organizationalUnitName = "NULL";

	//所属机构代码 
	@Attribute(name = "organization-code")
	private String organizationCode = "NULL";
	
	//所属机构id
    @Attribute(name = "unit-id")
    private Integer organizationId;

	//创建者
	@Attribute(name = "creater")
	private String creater = "NULL";

	@Attribute(name = "update-time-str")
	private String updateTime;

	@Attribute(name = "create-time-str")
	private String createTime;

	//性别 1男 0女
	@Attribute(name = "sex")
	private int sex = 1;

	//状态  0：未激活，1：正常，2已锁定
	@Attribute(name = "state")
	private Integer state = 1;

	//1已删除 0未删除 
	@Attribute(name = "del-flag")
	private Integer delFlag = 0;

	//其它描述信息 如 所在部门 创建人
	@Attribute(name = "description")
	private String description = "NULL";

	//身份证号码
	@Attribute(name = "id-card")
	private String idCard = "NULL";

	//绑定状态1：已绑定 0：未绑定
	@Transient
	private String bindState;

	@Transient
	private String bizConfigCode;

	public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
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

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getBindState() {
		return bindState;
	}

	public void setBindState(String bindState) {
		this.bindState = bindState;
	}

	public String getBizConfigCode() {
		return bizConfigCode;
	}

	public void setBizConfigCode(String bizConfigCode) {
		this.bizConfigCode = bizConfigCode;
	}

	public String getDn2() {
		return dn2;
	}

	public void setDn2(String dn2) {
		this.dn2 = dn2;
	}

	public Name getDn() {
		return dn;
	}

	public void setDn(Name dn) {
		this.dn = dn;
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
		if (StringUtils.isEmpty(mobile)) {
			this.mobile = ApiConstants.SSOUSER_MOBILE_DEFAULT_VALUE;
			return;
		}
		this.mobile = mobile;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		if (StringUtils.isEmpty(telephoneNumber)) {
			this.telephoneNumber = ApiConstants.SSOUSER_MOBILE_DEFAULT_VALUE;
			return;
		}
		this.telephoneNumber = telephoneNumber;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		if (StringUtils.isEmpty(employeeNumber)) {
			this.employeeNumber = ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE;
			return;
		}
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		if (StringUtils.isEmpty(employeeType)) {
			this.employeeType = ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE;
			return;
		}
		this.employeeType = employeeType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			this.employeeNumber = ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE;
			return;
		}
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
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
		if (StringUtils.isEmpty(idCard)) {
			this.idCard = ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE;
			return;
		}
		this.idCard = idCard;
	}

	public String getBizUser() {
		return bizUser;
	}

	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
	}

}