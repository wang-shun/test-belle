package com.wonhigh.bs.sso.admin.common.util;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * 工具类
 * 
 * @author wang.w
 * @date 2014-6-16 上午10:29:32
 * @version 0.9.1 
 * @copyright yougou.com 
 */
public class CommonUtil {
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{6,20}$"); 
	
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
 	    Matcher m = EMAIL_PATTERN.matcher(email);  
 	    return m.find();
	}
	
	/**
	 * 检查电话号码
	 * @param mobile
	 * @return
	 * 
	 */
	public static boolean checkMobilePhone(String mobile){
        Matcher matcher = PHONE_PATTERN.matcher(mobile);
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
	 * 字符串转int,如果为空,使用一个默认值
	 * @param valueStr 需要转换的字符串
	 * @param defaultValue 默认值
	 * @return 转化后的int值
	 */
	public static int getIntValue(String valueStr, int defaultValue) {
		return (StringUtils.isNotEmpty(valueStr) && StringUtils.isNumeric(valueStr)) ? Integer.parseInt(valueStr)
				: defaultValue;
	}
	
	public static int getIntValue(String valueStr, int defaultValue,boolean existMinus) {
		if(existMinus&&valueStr.indexOf("-")==0){
			String tempStr =valueStr.replace("-", "");
			return (StringUtils.isNotEmpty(tempStr) && StringUtils.isNumeric(tempStr)) ? Integer.parseInt(valueStr)
					: defaultValue;
		}else{
			return getIntValue(valueStr, defaultValue);
		}

	}
	
	public static int getIntValue(Object value, int defaultValue) {
		String str = getStrValue(value,"");
		return (StringUtils.isNotEmpty(str) && StringUtils.isNumeric(str)) ? Integer.parseInt(str)
				: defaultValue;
	}

	/**
	 * 如果前置字符串为空 则使用默认字符串
	 * @param valueStr 前置字符串
	 * @param defaultValue 默认支付攒
	 * @return 字符串
	 */
	public static String getStrValue(String valueStr, String defaultValue) {
		return StringUtils.isEmpty(valueStr) ? defaultValue : valueStr;
	}
	public static String getStrValue(Object valueStr, String defaultValue) {
		return  valueStr == null? defaultValue : valueStr.toString();
	}		
	
	/**
	 * a 是否是 b 的前缀
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isPrefix(String a,String b){  
		if(a==null || b==null){
			return false;
		}
        int len = a.length() - b.length();  
        if(len>0){
        	return false;
        }
        len = a.length();
        if(a.substring(0, len).equals(b.substring(0,len))){  
            return true;  
        }  
        return false;  
    } 
	
}
