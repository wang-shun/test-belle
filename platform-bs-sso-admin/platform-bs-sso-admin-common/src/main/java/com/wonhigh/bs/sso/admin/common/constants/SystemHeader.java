package com.wonhigh.bs.sso.admin.common.constants;

/**
 * 系统HTTP头常量
 * 
 * @author user
 * @date 2017年11月12日 下午4:11:20
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SystemHeader {
    //签名Header
    public static final String X_CA_SIGNATURE = "X-Ca-Signature";
    //所有参与签名的Header
    public static final String X_CA_SIGNATURE_HEADERS = "X-Ca-Signature-Headers";
    //请求时间戳
    public static final String X_CA_TIMESTAMP = "X-Ca-Timestamp";
    //请求放重放Nonce,15分钟内保持唯一,建议使用UUID
    public static final String X_CA_NONCE = "X-Ca-Nonce";
    //APP KEY
    public static final String X_CA_KEY = "X-Ca-Key";
}
