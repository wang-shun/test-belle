package com.wonhigh.bs.sso.server.common.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 对应业务系统配置表
 * @author zhang.rq
 * @since 2017-10-24 15:37
 *
 */
public class BizConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /****主键id****/
    private Integer id;

    /****业务系统代码******/
    private String bizCode;

    /****业务系统名称******/
    private String bizName;

    /****业务系统秘钥******/
    private String bizSecret;

    /****登录url********/
    private String loginUrl;

    /****注册的url******/
    private String registerUrl;

    /****验证用户密码url******/
    private String verifyUserPwdUrl;

    /****删除用户url******/
    private String delUserUrl;
    
    /****同步用户信息url******/
    private String syncUserInfoUrl;

    /****邮件*********/
    private String email;

    /****描述，备注****/
    private String description;

    /****创建人****/
    private String createUser;

    /****修改人****/
    private String updateUser;
    
    /****是否接入epp,0:未接入，1:已接入****/
    private Integer joinUpEppFlag;

    /****创建时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /****修改时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getJoinUpEppFlag() {
        return joinUpEppFlag;
    }

    public void setJoinUpEppFlag(Integer joinUpEppFlag) {
        this.joinUpEppFlag = joinUpEppFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizSecret() {
        return bizSecret;
    }

    public void setBizSecret(String bizSecret) {
        this.bizSecret = bizSecret;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getVerifyUserPwdUrl() {
        return verifyUserPwdUrl;
    }

    public void setVerifyUserPwdUrl(String verifyUserPwdUrl) {
        this.verifyUserPwdUrl = verifyUserPwdUrl;
    }

    public String getDelUserUrl() {
        return delUserUrl;
    }

    public void setDelUserUrl(String delUserUrl) {
        this.delUserUrl = delUserUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSyncUserInfoUrl() {
		return syncUserInfoUrl;
	}

	public void setSyncUserInfoUrl(String syncUserInfoUrl) {
		this.syncUserInfoUrl = syncUserInfoUrl;
	}

	@Override
    public String toString() {
        return "BizConfigDTO [id=" + id + ", bizCode=" + bizCode + ", bizName=" + bizName + ", bizSecret=" + bizSecret
                + ", loginUrl=" + loginUrl + ", registerUrl=" + registerUrl + ", verifyUserPwdUrl=" + verifyUserPwdUrl
                + ", delUserUrl=" + delUserUrl + ", email=" + email + ", description=" + description + ", createUser="
                + createUser + ", updateUser=" + updateUser + ", createTime=" + createTime + ", updateTime="
                + updateTime + "]";
    }

}
