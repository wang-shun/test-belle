package com.wonhigh.bs.sso.server.common.constants;

/**
 * 通用常量
 */
public class ApiConstants {
	//签名算法HmacSha256
	public static final String HMAC_SHA256 = "HmacSHA256";
	public static final String MD5 = "MD5";
	//编码UTF-8
	public static final String ENCODING = "UTF-8";
	//UserAgent
	public static final String USER_AGENT = "sms/java";
	//换行符
	public static final String LF = "\n";
	//串联符
	public static final String SPE1 = ",";
	//示意符
	public static final String SPE2 = ":";
	//连接符
	public static final String SPE3 = "&";
	//赋值符
	public static final String SPE4 = "=";
	//问号符
	public static final String SPE5 = "?";
	//默认请求超时时间,单位毫秒
	public static final int DEFAULT_TIMEOUT = 1000;
	//参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
	public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";

	/** 公共参数字段  */
	public static final String APP_KEY = "appKey";
	public static final String APP_SECRET = "appSecret";
	public static final String TIMESTAMP = "timestamp";
	public static final String SIGN = "sign";

	/** 接口用户 */
	//成功
	public static final int SUCCESS = 1;
	//用户不存在
	public static final int USER_NOT_EXIST = 1001;
	//密码错误
	public static final int PASSWORD_ERROR = 1002;
	
	//新密码格式错误
	public static final int NEW_PASSWORD_ERROR = 1003;

	//请求超时
	public static final int REQUEST_TIMED_OUT = 2001;

	//签名错误
	public static final int SIGNATURE_ERROR = 3001;

	//ssotoken错误
	public static final int SSOTOKEN_ERROR = 4001;
	//ssotoken过期
	public static final int SSOTOKEN_OVERDUE = 4002;
	//bizcode参数有误
	public static final int BIZCODE_ERROR = 4003;

	//sso账号未绑定该业务系统账号
	public static final int NOT_FOUND_BIZ_USER = 5001;
	//sso账号已绑定该业务系统账号
    public static final int ALREADY_BIND_BIZ_USER = 5002;
    
    //appCode参数有误
    public static final int APPCODE_ERROR = 6001;
    //bizUserLoginName参数有误
    public static final int BIZUSERLOGINNAME_ERROR = 6002;
    
    //sso账号状态为0未激活
    public static final int BIZUSER_NONACTIVATED = 7001;
    
    //sso账号状态为2已锁定
    public static final int BIZUSER_LOCKED = 7002;

	//其它错误
	public static final int OTHER_ERROR = 9999;
	
	/**
	 * https调用
	 */
	public static final String HTTPS = "https";
	
	/**
	 * http调用
	 */
	public static final String HTTP = "http";

	/**
	 * sso-admin分配给EPP的bizCode值
	 */
	public static final String SSOADMIN_EPP_BIZ_CODE = "EPP";

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

	/**
	 * SsoUser moblie　空值
	 */
	public static final String SSOUSER_MOBILE_DEFAULT_VALUE = "0";

	/**
	 * SsoUser 通用空值
	 */
	public static final String SSOUSER_COMMON_DEFAULT_VALUE = "NULL";
	 
	 
	 /**
	 * 业务系统配置redis key
	 */
	public static final String BIZ_CONFIG_LIST_KEY = "biz_config_list_key";

}
