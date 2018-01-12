package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 对应系统管理员表
 * @author zhang.rq
 * @since 2017-10-24 15:37
 *
 */
public class SsoAdminDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /****主键id****/
    private Integer id;

    /*****登录名***********/
    private String loginName;

    /*****真实姓名***********/
    private String sureName;

    /*****密码***********/
    private String password;

    /*****管理员描述***********/
    private String description;

    /*****管理员状态   1正常 2锁定***********/
    private Integer state;

    /*****管理员角色Id***********/
    private Integer roleId;

    /*****管理员角色***********/
    private String roleName;

    /*****管理员邮箱***********/
    private String email;

    /*****管理员手机**********/
    private String phone;

    /****创建人****/
    private Integer createUserId;

    /****创建人****/
    private String createUser;

    /****修改人****/
    private String updateUser;

    /****创建时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /****修改时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /***
     * 1:系统管理员 2：超级管理员 0：普通管理员（默认）
     */
    private Integer adminType;

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getAdminType() {
        return adminType;
    }

    public void setAdminType(Integer adminType) {
        this.adminType = adminType;
    }

    @Override
    public String toString() {
        return "SsoAdminDTO [id=" + id + ", loginName=" + loginName + ", sureName=" + sureName + ", password="
                + password + ", description=" + description + ", state=" + state + ", roleId=" + roleId + ", roleName="
                + roleName + ", email=" + email + ", phone=" + phone + ", createUserId=" + createUserId
                + ", createUser=" + createUser + ", updateUser=" + updateUser + ", createTime=" + createTime
                + ", updateTime=" + updateTime + ", adminType=" + adminType + "]";
    }

}
