package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 对应系统管理员角色表
 * @author zhang.rq
 * @since 2017-10-24 15:37
 *
 */
public class SsoAdminRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /****主键id****/
    private Integer id;

    /****角色名称****/
    private String roleName;

    /****角色编码****/
    private String roleCode;

    /****角色描述****/
    private String description;

    /****创建人****/
    private Integer createUserId;
    private String createUser;

    /****修改人****/
    private String updateUser;

    List<OrgUnitDTO> orgUnitList;

    /****创建时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /****修改时间****/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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

    public List<OrgUnitDTO> getOrgUnitList() {
        return orgUnitList;
    }

    public void setOrgUnitList(List<OrgUnitDTO> orgUnitList) {
        this.orgUnitList = orgUnitList;
    }

    @Override
    public String toString() {
        return "SsoAdminRoleDTO [id=" + id + ", roleName=" + roleName + ", roleCode=" + roleCode + ", description="
                + description + ", createUserId=" + createUserId + ", createUser=" + createUser + ", updateUser="
                + updateUser + ", orgUnitList=" + orgUnitList + ", createTime=" + createTime + ", updateTime="
                + updateTime + "]";
    }

}
