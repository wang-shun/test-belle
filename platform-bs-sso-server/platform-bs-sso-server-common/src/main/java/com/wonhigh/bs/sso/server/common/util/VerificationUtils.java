package com.wonhigh.bs.sso.server.common.util;

import java.util.Random;

import com.wonhigh.bs.sso.server.common.constants.CustomConstants;

public class VerificationUtils {

	public static String noticeEncryption(String orgString, String type) {
		StringBuilder targetString = new StringBuilder();
		try {
			targetString.append("******");
			
			switch (type) {
			case CustomConstants.SMS_EMAIL_CODE.EMAIL:
				String lastAddress = orgString.substring(orgString.indexOf("@"));
				targetString.append(lastAddress);
				break;
			case CustomConstants.SMS_EMAIL_CODE.PHONE:
				String last4Number = orgString.substring(orgString.length() - 4);
				targetString.append(last4Number);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetString.toString();
	}

	/** 
     * 产生随机的六位数 
     * @return 
     */  
	public static String getRandom(int randomLength) {
		Random rad = new Random();

		StringBuilder str = new StringBuilder();
		str.append("1");

		for (int i = 0; i < randomLength; i++) {
			str.append("0");
		}

		int parseInt = Integer.parseInt(str.toString());
		
		String result = rad.nextInt(parseInt) + "";

		if (result.length() != randomLength) {
			return getRandom(randomLength);
		}
		return result;
	}
	
	/** 
     * 产生随机的六位数 
     * @return 
     */  
    public static String getSixRandNumber(){  
        Random rad=new Random();  
          
        String result  = rad.nextInt(1000000) +"";  
          
        if(result.length()!=6){  
            return getSixRandNumber();  
        }  
        return result;  
    }  

}
