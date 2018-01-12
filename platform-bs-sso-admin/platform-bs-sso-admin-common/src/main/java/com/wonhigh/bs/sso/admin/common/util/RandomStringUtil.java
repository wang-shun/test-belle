package com.wonhigh.bs.sso.admin.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * 生成随机字符串
 * 
 * @author user
 * @date 2017年11月12日 下午4:04:03
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class RandomStringUtil {

	/**
     * 
     * @param passLength 要生成多少长度的字符串
     * @param type  需要哪种类型
	    type=0：纯数字(0-9)
	    type=1：全小写字母(a-z)
	    type=2：全大写字母(A-Z)
	    type=3: 数字+小写字母
	    type=4: 数字+大写字母
	    type=5：大写字母+小写字母
	    type=6：数字+大写字母+小写字母
	    type=7：固定长度33位：根据UUID拿到的随机字符串，去掉了四个"-"(相当于长度33位的小写字母加数字)
     * @return
     */
    public static String getRandomCode(int passLength, int type) {
        StringBuffer buffer = null;
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        switch (type) {
        case 0:
            buffer = new StringBuffer("0123456789");
            break;
        case 1:
            buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
            break;
        case 2:
            buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            break;
        case 3:
            buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
            break;
        case 4:
            buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            break;
        case 5:
            buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
            break;
        case 6:
            buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
            passLength -= 1;
            break;
        case 7:
            String s = UUID.randomUUID().toString();
            sb.append(s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24));
            break;
        default : break;
        }

        if (type != 7) {
            int range = buffer.length();
            for (int i = 0; i < passLength; ++i) {
                sb.append(buffer.charAt(r.nextInt(range)));
            }
        }
        return sb.toString();
    }
    
    /**
     * 随机数
     * @param passLength 总长
     * @param letterCount 字母个数
     * @return
     */
    public static String getRandomCode2(int passLength, int letterCount) {
        StringBuffer buffer = new StringBuffer("0123456789");
        StringBuffer buffer2 = new StringBuffer("abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int range = buffer2.length();
        for (int i = 0; i < letterCount; ++i) {
            sb.append(buffer2.charAt(r.nextInt(range)));
        }
        range = buffer.length();
        for (int i = 0; i < (passLength-letterCount); ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }
    
}