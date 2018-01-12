package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 组织架构
 * @author zhang.rq
 * @date  2017-10-19 12:01:03
 * @version 1.0.0
 * @copyright (C) 2013 WonHigh Information Technology Co.,Ltd 
 * All Rights Reserved. 
 * 
 * The software for the WonHigh technology development, without the 
 * company's written consent, and any other individuals and 
 * organizations shall not be used, Copying, Modify or distribute 
 * the software.
 * 
 */
public class OrgUnitNew implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 组织架构码ID
     */
    private Integer unitId;

    /**
     * 组织编号
     */
    private String unitCode;

    /**
     * 组织级别名称
     */
    private String unitLevelName;

    /**
     * 组织级别ID
     */
    private Integer unitLevelId;

    /**
     * 组织父Id
     */
    private Integer parentId;

    /**
     * 组织单元名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 全名称
     */
    private String fullName;

    /**
     * 组织状态（0 无效  1 有效）
     */
    private Byte orgStatus;

    /**
     * 排序
     */
    private Short sort;

    /**
     * 生效期间
     */
    private Date effectiveTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * OA对应ID
     */
    private Integer oaId;

    /**
     * 删除状态(1:已删除 0:正常)
     */
    private Byte delflag;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 记录更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 
     * {@linkplain #unitId}
     *
     * @return the value of org_unit.unit_id
     */
    public Integer getUnitId() {
        return unitId;
    }

    /**
     * 
     * {@linkplain #unitId}
     * @param unitId the value for org_unit.unit_id
     */
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    /**
     * 
     * {@linkplain #unitCode}
     *
     * @return the value of org_unit.unit_code
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * 
     * {@linkplain #unitCode}
     * @param unitCode the value for org_unit.unit_code
     */
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    /**
     * 
     * {@linkplain #unitLevelName}
     *
     * @return the value of org_unit.unit_level_name
     */
    public String getUnitLevelName() {
        return unitLevelName;
    }

    /**
     * 
     * {@linkplain #unitLevelName}
     * @param unitLevelName the value for org_unit.unit_level_name
     */
    public void setUnitLevelName(String unitLevelName) {
        this.unitLevelName = unitLevelName;
    }

    /**
     * 
     * {@linkplain #unitLevelId}
     *
     * @return the value of org_unit.unit_level_id
     */
    public Integer getUnitLevelId() {
        return unitLevelId;
    }

    /**
     * 
     * {@linkplain #unitLevelId}
     * @param unitLevelId the value for org_unit.unit_level_id
     */
    public void setUnitLevelId(Integer unitLevelId) {
        this.unitLevelId = unitLevelId;
    }

    /**
     * 
     * {@linkplain #parentId}
     *
     * @return the value of org_unit.parent_id
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 
     * {@linkplain #parentId}
     * @param parentId the value for org_unit.parent_id
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 
     * {@linkplain #name}
     *
     * @return the value of org_unit.name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * {@linkplain #name}
     * @param name the value for org_unit.name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * {@linkplain #enName}
     *
     * @return the value of org_unit.en_name
     */
    public String getEnName() {
        return enName;
    }

    /**
     * 
     * {@linkplain #enName}
     * @param enName the value for org_unit.en_name
     */
    public void setEnName(String enName) {
        this.enName = enName;
    }

    /**
     * 
     * {@linkplain #fullName}
     *
     * @return the value of org_unit.full_name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * 
     * {@linkplain #fullName}
     * @param fullName the value for org_unit.full_name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * 
     * {@linkplain #orgStatus}
     *
     * @return the value of org_unit.org_status
     */
    public Byte getOrgStatus() {
        return orgStatus;
    }

    /**
     * 
     * {@linkplain #orgStatus}
     * @param orgStatus the value for org_unit.org_status
     */
    public void setOrgStatus(Byte orgStatus) {
        this.orgStatus = orgStatus;
    }

    /**
     * 
     * {@linkplain #sort}
     *
     * @return the value of org_unit.sort
     */
    public Short getSort() {
        return sort;
    }

    /**
     * 
     * {@linkplain #sort}
     * @param sort the value for org_unit.sort
     */
    public void setSort(Short sort) {
        this.sort = sort;
    }

    /**
     * 
     * {@linkplain #effectiveTime}
     *
     * @return the value of org_unit.effective_time
     */
    public Date getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * 
     * {@linkplain #effectiveTime}
     * @param effectiveTime the value for org_unit.effective_time
     */
    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * 
     * {@linkplain #endTime}
     *
     * @return the value of org_unit.end_time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 
     * {@linkplain #endTime}
     * @param endTime the value for org_unit.end_time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 
     * {@linkplain #oaId}
     *
     * @return the value of org_unit.oa_id
     */
    public Integer getOaId() {
        return oaId;
    }

    /**
     * 
     * {@linkplain #oaId}
     * @param oaId the value for org_unit.oa_id
     */
    public void setOaId(Integer oaId) {
        this.oaId = oaId;
    }

    /**
     * 
     * {@linkplain #delflag}
     *
     * @return the value of org_unit.delflag
     */
    public Byte getDelflag() {
        return delflag;
    }

    /**
     * 
     * {@linkplain #delflag}
     * @param delflag the value for org_unit.delflag
     */
    public void setDelflag(Byte delflag) {
        this.delflag = delflag;
    }

    /**
     * 
     * {@linkplain #createUser}
     *
     * @return the value of org_unit.create_user
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * 
     * {@linkplain #createUser}
     * @param createUser the value for org_unit.create_user
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * 
     * {@linkplain #createTime}
     *
     * @return the value of org_unit.create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * {@linkplain #createTime}
     * @param createTime the value for org_unit.create_time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * {@linkplain #updateUser}
     *
     * @return the value of org_unit.update_user
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * 
     * {@linkplain #updateUser}
     * @param updateUser the value for org_unit.update_user
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * 
     * {@linkplain #updateTime}
     *
     * @return the value of org_unit.update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     * @param updateTime the value for org_unit.update_time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 
     * {@linkplain #remark}
     *
     * @return the value of org_unit.remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 
     * {@linkplain #remark}
     * @param remark the value for org_unit.remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "OrgUnitNew [unitId=" + unitId + ", unitCode=" + unitCode + ", unitLevelName=" + unitLevelName
                + ", unitLevelId=" + unitLevelId + ", parentId=" + parentId + ", name=" + name + ", enName=" + enName
                + ", fullName=" + fullName + ", orgStatus=" + orgStatus + ", sort=" + sort + ", effectiveTime="
                + effectiveTime + ", endTime=" + endTime + ", oaId=" + oaId + ", delflag=" + delflag + ", createUser="
                + createUser + ", createTime=" + createTime + ", updateUser=" + updateUser + ", updateTime="
                + updateTime + ", remark=" + remark + "]";
    }

}