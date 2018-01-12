package com.wonhigh.bs.sso.server.common.model;

import java.util.Date;

/**
 * ҵ��ϵͳ����Ϣ
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
public class BizType {
    /**
     * ID����,����
     */
    private Integer id;

    /**
     * ҵ��ϵͳ���,ϵͳ����,4λ
     */
    private Integer bizCode;

    /**
     * ҵ��ϵͳ���
     */
    private String bizName;

    /**
     * ҵ��ϵͳȱʡҳURL
     */
    private String bizHomeUrl;

    /**
     * ҵ��ϵͳע���û�URL
     */
    private String bizRegisterUrl;

    /**
     * ҵ��ϵͳɾ���û�URL
     */
    private String bizDeluserUrl;

    /**
     * ҵ��ϵͳ�޸�����URL
     */
    private String bizPasswordUrl;

    /**
     * ҵ��ϵͳ��һ��ͨURL
     */
    private String bizBindssoUrl;

    /**
     * ҵ��ϵͳ��¼URL
     */
    private String bizLoginUrl;

    /**
     * ҵ��ϵͳ�ǳ�URL
     */
    private String bizLogoutUrl;

    /**
     * ҵ��ϵͳ֪ͨ�绰�б�,֧�ֶ���ֻ�,�á�,���ָ�
     */
    private String bizInformPhone;

    /**
     * ҵ��ϵͳ֪ͨ����
     */
    private String bizInformEmail;

    /**
     * ��ע
     */
    private String bizComment;

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
     * @return the value of biz_type.id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * {@linkplain #id}
     * @param id the value for biz_type.id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * {@linkplain #bizCode}
     *
     * @return the value of biz_type.biz_code
     */
    public Integer getBizCode() {
        return bizCode;
    }

    /**
     * 
     * {@linkplain #bizCode}
     * @param bizCode the value for biz_type.biz_code
     */
    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    /**
     * 
     * {@linkplain #bizName}
     *
     * @return the value of biz_type.biz_name
     */
    public String getBizName() {
        return bizName;
    }

    /**
     * 
     * {@linkplain #bizName}
     * @param bizName the value for biz_type.biz_name
     */
    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    /**
     * 
     * {@linkplain #bizHomeUrl}
     *
     * @return the value of biz_type.biz_home_url
     */
    public String getBizHomeUrl() {
        return bizHomeUrl;
    }

    /**
     * 
     * {@linkplain #bizHomeUrl}
     * @param bizHomeUrl the value for biz_type.biz_home_url
     */
    public void setBizHomeUrl(String bizHomeUrl) {
        this.bizHomeUrl = bizHomeUrl;
    }

    /**
     * 
     * {@linkplain #bizRegisterUrl}
     *
     * @return the value of biz_type.biz_register_url
     */
    public String getBizRegisterUrl() {
        return bizRegisterUrl;
    }

    /**
     * 
     * {@linkplain #bizRegisterUrl}
     * @param bizRegisterUrl the value for biz_type.biz_register_url
     */
    public void setBizRegisterUrl(String bizRegisterUrl) {
        this.bizRegisterUrl = bizRegisterUrl;
    }

    /**
     * 
     * {@linkplain #bizDeluserUrl}
     *
     * @return the value of biz_type.biz_deluser_url
     */
    public String getBizDeluserUrl() {
        return bizDeluserUrl;
    }

    /**
     * 
     * {@linkplain #bizDeluserUrl}
     * @param bizDeluserUrl the value for biz_type.biz_deluser_url
     */
    public void setBizDeluserUrl(String bizDeluserUrl) {
        this.bizDeluserUrl = bizDeluserUrl;
    }

    /**
     * 
     * {@linkplain #bizPasswordUrl}
     *
     * @return the value of biz_type.biz_password_url
     */
    public String getBizPasswordUrl() {
        return bizPasswordUrl;
    }

    /**
     * 
     * {@linkplain #bizPasswordUrl}
     * @param bizPasswordUrl the value for biz_type.biz_password_url
     */
    public void setBizPasswordUrl(String bizPasswordUrl) {
        this.bizPasswordUrl = bizPasswordUrl;
    }

    /**
     * 
     * {@linkplain #bizBindssoUrl}
     *
     * @return the value of biz_type.biz_bindsso_url
     */
    public String getBizBindssoUrl() {
        return bizBindssoUrl;
    }

    /**
     * 
     * {@linkplain #bizBindssoUrl}
     * @param bizBindssoUrl the value for biz_type.biz_bindsso_url
     */
    public void setBizBindssoUrl(String bizBindssoUrl) {
        this.bizBindssoUrl = bizBindssoUrl;
    }

    /**
     * 
     * {@linkplain #bizLoginUrl}
     *
     * @return the value of biz_type.biz_login_url
     */
    public String getBizLoginUrl() {
        return bizLoginUrl;
    }

    /**
     * 
     * {@linkplain #bizLoginUrl}
     * @param bizLoginUrl the value for biz_type.biz_login_url
     */
    public void setBizLoginUrl(String bizLoginUrl) {
        this.bizLoginUrl = bizLoginUrl;
    }

    /**
     * 
     * {@linkplain #bizLogoutUrl}
     *
     * @return the value of biz_type.biz_logout_url
     */
    public String getBizLogoutUrl() {
        return bizLogoutUrl;
    }

    /**
     * 
     * {@linkplain #bizLogoutUrl}
     * @param bizLogoutUrl the value for biz_type.biz_logout_url
     */
    public void setBizLogoutUrl(String bizLogoutUrl) {
        this.bizLogoutUrl = bizLogoutUrl;
    }

    /**
     * 
     * {@linkplain #bizInformPhone}
     *
     * @return the value of biz_type.biz_inform_phone
     */
    public String getBizInformPhone() {
        return bizInformPhone;
    }

    /**
     * 
     * {@linkplain #bizInformPhone}
     * @param bizInformPhone the value for biz_type.biz_inform_phone
     */
    public void setBizInformPhone(String bizInformPhone) {
        this.bizInformPhone = bizInformPhone;
    }

    /**
     * 
     * {@linkplain #bizInformEmail}
     *
     * @return the value of biz_type.biz_inform_email
     */
    public String getBizInformEmail() {
        return bizInformEmail;
    }

    /**
     * 
     * {@linkplain #bizInformEmail}
     * @param bizInformEmail the value for biz_type.biz_inform_email
     */
    public void setBizInformEmail(String bizInformEmail) {
        this.bizInformEmail = bizInformEmail;
    }

    /**
     * 
     * {@linkplain #bizComment}
     *
     * @return the value of biz_type.biz_comment
     */
    public String getBizComment() {
        return bizComment;
    }

    /**
     * 
     * {@linkplain #bizComment}
     * @param bizComment the value for biz_type.biz_comment
     */
    public void setBizComment(String bizComment) {
        this.bizComment = bizComment;
    }

    /**
     * 
     * {@linkplain #createTime}
     *
     * @return the value of biz_type.create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * {@linkplain #createTime}
     * @param createTime the value for biz_type.create_time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     *
     * @return the value of biz_type.update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     * @param updateTime the value for biz_type.update_time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}