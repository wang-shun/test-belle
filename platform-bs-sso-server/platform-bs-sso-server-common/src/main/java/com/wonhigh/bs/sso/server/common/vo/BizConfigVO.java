package com.wonhigh.bs.sso.server.common.vo;

import java.io.Serializable;

/**
 * 对应业务系统配置表
 * @author zhang.rq
 * @since 2017-10-24 15:37
 *
 */
public class BizConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /****主键id****/
    private Integer id;

    /****业务系统代码******/
    private String bizCode;

    /****业务系统名称******/
    private String bizName;

    /****描述，备注****/
    private String description;

    /****是否接入epp,0:未接入，1:已接入****/
    private Integer joinUpEppFlag;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getJoinUpEppFlag() {
        return joinUpEppFlag;
    }

    public void setJoinUpEppFlag(Integer joinUpEppFlag) {
        this.joinUpEppFlag = joinUpEppFlag;
    }

    @Override
    public String toString() {
        return "BizConfigVO [id=" + id + ", bizCode=" + bizCode + ", bizName=" + bizName + ", description="
                + description + ", joinUpEppFlag=" + joinUpEppFlag + "]";
    }
    
}
