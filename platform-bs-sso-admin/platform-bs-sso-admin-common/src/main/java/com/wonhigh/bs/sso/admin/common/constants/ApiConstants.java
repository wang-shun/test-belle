package com.wonhigh.bs.sso.admin.common.constants;

/**
 * 通用常量
 * 
 * @author user
 * @date 2017年11月12日 下午4:10:26
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class ApiConstants {
	
	/**
	 * 整个组织机构树  orgUnit-{unitCode}-{level}
	 */
	public static final String ORG_UNTI_TREE_ALL = "orgUnit-all-all";
	
    
    /** 公共参数字段  */
    public static final String APP_KEY = "appKey";
    public static final String APP_SECRET = "appSecret";
    public static final String TIMESTAMP = "timestamp";
    public static final String SIGN = "sign";
    
    /**
     * 全量同步时，先获取该redis值，作为同步锁
     */
    public static final String SYNC_HR_USER_REDIS_KEY = "SYNC_HR_USER_REDIS_KEY_VALUE";
    
    /** 接口用户 */
    public static final int API_USER_TYPE = 1;
    
	/** 接口过期时间 */
	public static final long API_EXPIRATION_TIME = 60*1000*10L;
	/** 默认短信优先级 */
	public static final int DEFAULT_SMS_PRIORITY = 1;
	/** 默认每页显示条数  */
	public static final int DEFAULT_PAGE_SIZE = 20;
	/** 每页显示最大条数  */
	public static final int MAX_PAGE_SIZE = 200;
	
	
	/**  SMS 模块 错误码 从 600  */
	public static final int SMS  = 6_00;
	
	/**
	 * sso-admin分配给hr系统的bizCode值
	 */
	public static final String SSOADMIN_HR_BIZ_CODE  = "HR";
	
	/**
	 * sso-admin分配给EPP的bizCode值
	 */
	public static final String SSOADMIN_EPP_BIZ_CODE  = "EPP";
	
	/**
	 * SsoUser moblie　空值
	 */
	public static final String SSOUSER_MOBILE_DEFAULT_VALUE  = "0";
	
	/**
	 * SsoUser 通用空值
	 */
	public static final String SSOUSER_COMMON_DEFAULT_VALUE  = "NULL";
	
	/**
	 * 系统管理员
	 */
	public static final int SSOADMIN_SYSTEM_ROLE = 1;
	
	/**
	 * 超级管理员
	 */
	public static final int SSOADMIN_SUPER_ROLE = 2;
	
	/**
	 * 普通管理员
	 */
	public static final int SSOADMIN_NORMAL_ROLE = 0;
	
	/**
	 * 管理员正常状态
	 */
	public static final int SSOADMIN_NORMAL_STATE_VALUE = 1;
	
	/**
	 * 管理员锁定状态
	 */
	public static final int SSOADMIN_LOCK_STATE_VALUE = 2;
	
	/**
	 * 一账通正常状态
	 */
	public static final int SSOUSER_NORMAL_STATE_VALUE = 1;
	
	/**
	 * 一账通初始状态
	 */
	public static final int SSOUSER_INITIAL_STATE_VALUE = 0;
	
	/**
	 * 一账通锁定状态
	 */
	public static final int SSOUSER_LOCKED_STATE_VALUE = 2;
	
	/**
	 * 发送邮件短信报警
	 */
	public static final String SEND_EMAIL_SWITCH_ON = "on";
	
	/**
	 * 不发送邮件短信报警
	 */
	public static final String SEND_EMAIL_SWITCH_OFF = "on";
	
	/**
	 * hr系统发送的 mq消息类型：新增ssoUser
	 */
	public static final int SSOUSER_HRMQ_ADD = 1;
	
	/**
	 * hr系统发送的 mq消息类型：修改ssoUser
	 */
	public static final int SSOUSER_HRMQ_UPDATE = 2;
	
	/**
	 * hr系统发送的 mq消息类型：删除ssoUser
	 */
	public static final int SSOUSER_HRMQ_DELETE = 3;
	
	/**
	 * ssoUser 未绑定业务系统
	 */
	public static final String SSOUSER_NOT_BIND_BIZ = "0";
	
	/**
	 * ssoUser 已绑定业务系统
	 */
	public static final String SSOUSER_IS_BIND_BIZ = "1";
	
	/**
	 * https调用
	 */
	public static final String HTTPS = "https";
	
	/**
	 * http调用
	 */
	public static final String HTTP = "http";
	
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
	 * 调用epp通知接口时source参数值(生产)
	 */
	public static final String EPP_SOURCE_PARAMETER = "belletone_prod";
	
	/**
	 * 调用epp通知接口时source参数值(开发)
	 */
	public static final String EPP_SOURCE_PARAMETER_DEV = "belletone_dev";
	
	/**
	 * 调用epp通知接口时source参数值(测试)
	 */
	public static final String EPP_SOURCE_PARAMETER_TEST = "belletone_test";
	
}
