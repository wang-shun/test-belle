package org.jasig.cas.client.util;
/**
 * 用来存放自定义的常量
 * @author 杨志豪
 *
 */
public class MyConstants {
	/**
     * 是否需要强制修改密码标志【0表示需要修改 1表示不需要修改】.
     */
    public static final String IS_FORCE_EDIT_PWD_FLAG = "state";
    /**
     * 需要强制修改密码.
     */
    public static final int NEED_FORCE_EDIT_PWD = 0;
    /**
     * 不需要强制修改密码.
     */
    public static final int DO_NOT_NEED_FORCE_EDIT_PWD = 1;
    /**
     * 存放需要强制修改密码的用户名的session key
     */
    public static final String NEED_FORCE_EDIT_PWD_USERNAME = "editPwdUserName";
    
	/**
	 * 设备验证状态，1：成功，0：失败
	 */
    public static final String DEVICE_CHECK_STATUE = "1";
    
	/**
	 * SSO绑定业务系统信息key
	 */
	public static final String BIZ_USER_BINDING_INFO = "biz-user";
	
	/**
	 * SSO-SERVER的CODE
	 */
	public static final String SSO_SERVER_CODE = "SSO-SERVER";
	
	/**
	 * uid
	 */
	public static final String UID_USERNAME = "uid";
    
    
    
}
