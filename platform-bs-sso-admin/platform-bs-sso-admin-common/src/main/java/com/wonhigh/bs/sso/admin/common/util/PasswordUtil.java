package com.wonhigh.bs.sso.admin.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午4:04:11
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class PasswordUtil {
	
	public static String createPassword(String text){
		//密码加密
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] bs = md.digest();
			BASE64Encoder encoder = new BASE64Encoder();
			String password = "{MD5}"+encoder.encode(bs);
			return password;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
