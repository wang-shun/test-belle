package com.wonhigh.bs.sso.admin.common.util;

import java.security.SecureRandom;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午4:05:35
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class BizSecretGenerator {

	public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] bytesKey = new byte[8];
            localSecureRandom.nextBytes(bytesKey);
            String strKey = byteToHexString(bytesKey);
            return strKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static String byteToHexString(byte[] bytes) {
	     StringBuffer sb = new StringBuffer();
	     for (int i = 0; i < bytes.length; i++) {
	       String strHex=Integer.toHexString(bytes[i]);
	       if(strHex.length() > 3) {
	         sb.append(strHex.substring(6));
	       } else {
	         if(strHex.length() < 2) {
	           sb.append("0" + strHex);
	         } else {
	           sb.append(strHex);
	         }
	       }
	     }
	     return sb.toString();
	   }
}
