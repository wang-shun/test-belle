package com.wonhigh.bs.sso.server.common.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.wonhigh.bs.sso.server.common.constants.CustomConstants;

/**
 * 工具类
 * 
 * @author wang.w
 * @date 2014-6-16 上午10:29:32
 * @version 0.9.1 
 * @copyright yougou.com 
 */
public class CommonUtil {
	
	public static String create32RandomNumber(){
		 UUID uuid = UUID.randomUUID();
		 String r = uuid.toString().replace("-", "");
		 return r;
	}
	
	/**
	 * 按要求截取异常信息
	 * @param e 异常
	 * @param maxNum 最大的限制字符输出数
	 * @return
	 */
	public static String splitExceptionStr(Exception e,int maxNum){
		return ExceptionUtils.getFullStackTrace(e).substring(0, maxNum);
	}
	
	/**
	 * 将异常信息转换为字符串
	 * @param e
	 * @return
	 */
	public static String formatException2Str(Exception e){
		return ExceptionUtils.getFullStackTrace(e);
	}
	
	/**
	 * 检查email地址是否正确
	 * @param email
	 * @return
	 */
	public static boolean checkEmailAddr(String email){
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");  
 	    Matcher m = p.matcher(email);  
 	    return m.find();
	}
	
	/**
	 * 检查电话号码
	 * @param mobile
	 * @return
	 * 
	 */
	public static boolean checkMobilePhone(String mobile){
		Pattern pattern = Pattern.compile("^\\d{6,20}$"); 
        Matcher matcher = pattern.matcher(mobile);
         if (matcher.matches()) {
            return true;
        }
        return false;
	}
	
	/**
	 * 校验list值
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkList(List list){
		return list != null && list.size() > 0;
	}
	
	/**
	* @param regex
	* 正则表达式字符串
	* @param str
	* 要匹配的字符串
	* @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	*/
	public static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 设置返回信息
	 * @param code
	 * @param message
	 * @param result
	 */
	public static void setResultMessage(int code, String message, Map<String, Object> result) {
		result.put(CustomConstants.RESULT_CODE.CODE, code);
		result.put(CustomConstants.RESULT_CODE.MSG, message);
	}

}
