package com.wonhigh.bs.sso.server.common.model;

import java.util.Date;

/**
 * ҵ��ϵͳ��¼�˺�
 * @author user
 * @date  2017-09-04 11:36:04
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
public class BizUser {
    /**
     * ID����,����
     */
    private Long id;

    /**
     * һ��ͨ����
     */
    private Long ssoId;

    /**
     * ҵ��ϵͳ���
     */
    private Integer bizCode;

    /**
     * ҵ��ϵͳ��¼��
     */
    private String bizUser;

    /**
     * ҵ��ϵͳ��¼����,Ԥ��
     */
    private String bizPassword;

    /**
     * ����ʱ��
     */
    private Date createTime;

    /**
     * ����ʱ��
     */
    private Date updateTime;

    /**
     * 
     * {@linkplain #id}
     *
     * @return the value of biz_user.id
     */
    public Long getId() {
        return id;
    }

    /**
     * 
     * {@linkplain #id}
     * @param id the value for biz_user.id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * {@linkplain #ssoId}
     *
     * @return the value of biz_user.sso_id
     */
    public Long getSsoId() {
        return ssoId;
    }

    /**
     * 
     * {@linkplain #ssoId}
     * @param ssoId the value for biz_user.sso_id
     */
    public void setSsoId(Long ssoId) {
        this.ssoId = ssoId;
    }

    /**
     * 
     * {@linkplain #bizCode}
     *
     * @return the value of biz_user.biz_code
     */
    public Integer getBizCode() {
        return bizCode;
    }

    /**
     * 
     * {@linkplain #bizCode}
     * @param bizCode the value for biz_user.biz_code
     */
    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    /**
     * 
     * {@linkplain #bizUser}
     *
     * @return the value of biz_user.biz_user
     */
    public String getBizUser() {
        return bizUser;
    }

    /**
     * 
     * {@linkplain #bizUser}
     * @param bizUser the value for biz_user.biz_user
     */
    public void setBizUser(String bizUser) {
        this.bizUser = bizUser;
    }

    /**
     * 
     * {@linkplain #bizPassword}
     *
     * @return the value of biz_user.biz_password
     */
    public String getBizPassword() {
        return bizPassword;
    }

    /**
     * 
     * {@linkplain #bizPassword}
     * @param bizPassword the value for biz_user.biz_password
     */
    public void setBizPassword(String bizPassword) {
        this.bizPassword = bizPassword;
    }

    /**
     * 
     * {@linkplain #createTime}
     *
     * @return the value of biz_user.create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * {@linkplain #createTime}
     * @param createTime the value for biz_user.create_time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     *
     * @return the value of biz_user.update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     * @param updateTime the value for biz_user.update_time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}