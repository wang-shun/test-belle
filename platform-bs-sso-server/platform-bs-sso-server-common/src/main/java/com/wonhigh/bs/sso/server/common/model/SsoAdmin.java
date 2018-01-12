package com.wonhigh.bs.sso.server.common.model;

import java.util.Date;

/**
 * ��д�������; 
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
public class SsoAdmin {
    /**
     * ID����,����
     */
    private Integer id;

    /**
     * ����Ա��¼��,3-20λ
     */
    private String name;

    /**
     * ����6-20λ,MD5
     */
    private String password;

    /**
     * ��ʵ����
     */
    private String realName;

    /**
     * ����Ա����,SSO��̨����������Ա������Ȩ
     */
    private String adminLevel;

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
     * @return the value of sso_admin.id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * {@linkplain #id}
     * @param id the value for sso_admin.id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * {@linkplain #name}
     *
     * @return the value of sso_admin.name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * {@linkplain #name}
     * @param name the value for sso_admin.name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * {@linkplain #password}
     *
     * @return the value of sso_admin.password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * {@linkplain #password}
     * @param password the value for sso_admin.password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * {@linkplain #realName}
     *
     * @return the value of sso_admin.real_name
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 
     * {@linkplain #realName}
     * @param realName the value for sso_admin.real_name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 
     * {@linkplain #adminLevel}
     *
     * @return the value of sso_admin.admin_level
     */
    public String getAdminLevel() {
        return adminLevel;
    }

    /**
     * 
     * {@linkplain #adminLevel}
     * @param adminLevel the value for sso_admin.admin_level
     */
    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    /**
     * 
     * {@linkplain #createTime}
     *
     * @return the value of sso_admin.create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 
     * {@linkplain #createTime}
     * @param createTime the value for sso_admin.create_time
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     *
     * @return the value of sso_admin.update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * {@linkplain #updateTime}
     * @param updateTime the value for sso_admin.update_time
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}