package com.wonhigh.bs.sso.server.common.constants;

/**
 * 常量定义
 * 
 * @author user
 * @date 2017年11月7日 下午11:08:04
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface CustomConstants {
	
	interface RESULT_CODE{
		
		/**
		 * 返回值：状态标记
		 */
		String CODE = "code";
		
		/**
		 * 返回信息
		 */
		String MSG = "msg";

		/**
		 * 返回值：错误标志
		 */
		int ERROR = 0;

		/**
		 * 返回值：成功标志
		 */
		int SUCCESS = 1;

	}

	interface SMS_EMAIL_CODE {
		
		String PHONE = "phone";

		String EMAIL = "email";

		String PHONE_NUMBER = "phoneNumber";
		
		String EMAIL_ADDRESS = "emailAddress";

		String SMS_VERIF_CODE = "sms_VerifCode";

		String EMAIL_VERIF_CODE = "email_VerifCode";

		String NOTICE_ACCOUNT = "{account}";

		String NOTICE_CODE = "{code}";
	}

	interface ACCOUNT_STATUS {
		// 账号状态，0：未登录状态（原始密码状态）, 1：正常状态， 2：锁定状态
		int ORIGINAL_STATE = 0;
		
		int ACTIVATED_STATE = 1;
		
		int LOCKED_STATE = 2;
		
		// 账户真实姓名
		String SN_SURENAME = "sn";
		
		// 用户名
		String UID_USERNAME = "uid";
	}
	
	interface REGEX {
		
		String REGEX_PHONE = "^1[3-9]+\\d{9}$";

		String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.|\\_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

		String REGEX_PASSWORD = "(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,16}$";
	}

	interface CACHE_KEY {

		String BIZCONFIG = "BIZCONFIG";

	}

}
