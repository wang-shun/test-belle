package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 对应系统管理员--组织单元信息表
 * @author zhang.rq
 * @since 2017-10-24 15:37
 *
 */
public class SsoAdminRoleOrgUnitDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /****主键id****/
    private Integer id;

    /****关联-组织单元信息表ID****/
    private Integer orgUnitId;

    /****关联-组织单元信息表unitCode****/
    private String unitCode;

    /****关联-系统管理员角色ID****/
    private Integer ssoAdminRoleId;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrgUnitId() {
        return orgUnitId;
    }

    public void setOrgUnitId(Integer orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public Integer getSsoAdminRoleId() {
        return ssoAdminRoleId;
    }

    public void setSsoAdminRoleId(Integer ssoAdminRoleId) {
        this.ssoAdminRoleId = ssoAdminRoleId;
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

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    @Override
    public String toString() {
        return "SsoAdminRoleOrgUnitDTO [id=" + id + ", orgUnitId=" + orgUnitId + ", unitCode=" + unitCode
                + ", ssoAdminRoleId=" + ssoAdminRoleId + ", createUser=" + createUser + ", updateUser=" + updateUser
                + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
    }

}
