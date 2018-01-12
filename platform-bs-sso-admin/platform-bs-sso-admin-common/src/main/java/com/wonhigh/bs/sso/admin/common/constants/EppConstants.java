package com.wonhigh.bs.sso.admin.common.constants;

/**
 * epp相关的常量
 * 
 * @author user
 * @date 2017年11月12日 下午4:10:26
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class EppConstants {
    
    /**
     * sso-admin分配给EPP的bizCode值
     */
    public static final String SSOADMIN_EPP_BIZ_CODE  = "EPP";
	
	/**
	 * 修改密码时通知到epp
	 */
	public static final String CHANGE_PSWD_NOTICE = "CHANGE_PSWD";
	
	/**
	 * 解绑业务系统时通知到epp
	 */
	public static final String UNBIND_LOGINNAME_NOTICE = "UNBIND_LOGINNAME";
	
	/**
	 * 绑定业务系统时通知到epp
	 */
	public static final String BIND_LOGINNAME_NOTICE = "BIND_LOGINNAME";
	
	/**
     * 新用户入职时通知到epp
     */
    public static final String ADD_SSO_USER_NOTICE = "EMPLOY";
    
    /**
     * 用户离职时通知到epp
     */
    public static final String DELETE_SSO_USER_NOTICE = "DISMISS";
    
    /**
     * 更新用户信息时通知到epp
     */
    public static final String UPDATE_SSO_USER_NOTICE = "UPDATE_USER_INFO";
	
	
}
