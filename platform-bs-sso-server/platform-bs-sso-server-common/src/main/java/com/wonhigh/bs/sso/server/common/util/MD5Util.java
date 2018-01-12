package com.wonhigh.bs.sso.server.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密工具类
 * 
 * @author yang.wei
 * @since 2016-12-14
 */
public class MD5Util {
	/**
	 * MD5加密字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String encode(String src) {
		return DigestUtils.md5Hex(src);
	}
}