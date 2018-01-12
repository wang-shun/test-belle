package com.wonhigh.bs.sso.admin.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;

/**
 * 
 * http接口调用
 * 
 * @author user
 * @date 2017年11月12日 下午4:04:44
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class InvokeApiUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(InvokeApiUtil.class);
	
	/**
	 * 注册业务系统账号
	 * @param registerUrl
	 * @param bodys
	 * @param bizSecret
	 * @return
	 */
	public static Map<String, Object> registerBizUser(String registerUrl, Map<String, String> bodys, String bizSecret){
		registerUrl = StringUtils.trimToEmpty(registerUrl);
		LOGGER.info("注册业务系统账号接口被调用。参数：bodys="+bodys+",bizSecret="+bizSecret+",registerUrl="+registerUrl);
		Map<String,Object> r = new HashMap<String, Object>(3);
		String path = registerUrl.substring(registerUrl.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys);
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if(registerUrl.length()>=ApiConstants.HTTPS.length()){
			httpMethod = registerUrl.substring(0, ApiConstants.HTTPS.length());
		}
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(registerUrl, bodys);
		}else{
			rs = HttpClientUtil.doPost(registerUrl, bodys);
		}
		LOGGER.info("调用结果："+rs.toString());
		int statusCode = (int) rs.get("statusCode");
		String result = (String) rs.get("result");
		JSONObject rj = JSONObject.parseObject(result);
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","校验用户失败:网络错误");
    		return r;
		}else{
			//code状态：1注册成功 2：注册失败
			Integer code = rj.getInteger("code");
			if(code==1){
				r.put("code", 1);
	    		r.put("msg","校验用户成功");
			}else {
				r.put("code", 0);
	    		r.put("msg",rj.get("msg")==null?"校验用户失败":rj.getString("msg"));
			}
    		return r;
		}
		
	}
	
	/**
	 * 检查业务系统账号密码
	 * @param url
	 * @param bodys
	 * @param bizSecret
	 * @return
	 */
	public static Map<String, Object> checkBizUserPwd (String url, Map<String, String> bodys, String bizSecret){
		url = StringUtils.trimToEmpty(url);
		LOGGER.info("检查业务系统账号密码接口被调用。参数：bodys="+bodys+",bizSecret="+bizSecret+",url="+url);
		Map<String,Object> r = new HashMap<String, Object>(3);
		String path = url.substring(url.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys);
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if(url.length()>=ApiConstants.HTTPS.length()){
			httpMethod = url.substring(0, ApiConstants.HTTPS.length());
		}
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(url, bodys);
		}else{
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果："+rs.toString());
		int statusCode = (int) rs.get("statusCode");
		String result = (String) rs.get("result");
		JSONObject rj = JSONObject.parseObject(result);
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","校验用户失败:网络错误");
    		return r;
		}else{
			//code状态：1校验成功 2：用户名不存在 3：密码不对 4：其它错误
			Integer code = rj.getInteger("code");
			if(code==1){
				r.put("code", 1);
	    		r.put("msg","校验用户成功");
			}else {
				r.put("code", 0);
	    		r.put("msg",rj.get("msg")==null?"校验用户失败":rj.getString("msg"));
			}
    		return r;
		}
		
	}
	
	/**
	 * 删除业务系统账号
	 * @param url
	 * @param bodys
	 * @param bizSecret
	 * @return
	 */
	public static Map<String, Object> delBizUser (String url, Map<String, String> bodys, String bizSecret){
		url = StringUtils.trimToEmpty(url);
		LOGGER.info("删除业务系统账号接口被调用。参数：bodys="+bodys+",bizSecret="+bizSecret+",url="+url);
		Map<String,Object> r = new HashMap<String, Object>(3);
		String path = url.substring(url.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys); 
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if(url.length()>=ApiConstants.HTTPS.length()){
			httpMethod = url.substring(0, ApiConstants.HTTPS.length());
		}
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(url, bodys);
		}else{
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果："+rs.toString());
		int statusCode = (int) rs.get("statusCode");
		String result = (String) rs.get("result");
		JSONObject rj = JSONObject.parseObject(result);
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","删除用户失败:网络错误");
    		return r;
		}else{
			//code状态：1校验成功 2：用户名不存在 3：密码不对 4：其它错误
			Integer code = rj.getInteger("code");
			if(code==1){
				r.put("code", 1);
	    		r.put("msg","删除用户成功");
			}else {
				r.put("code", 0);
	    		r.put("msg",rj.get("msg")==null?"删除用户失败":rj.getString("msg"));
			}
    		return r;
		}
	}

	/**
	 * 主动同步HR人员信息接口
	 * @param url
	 * @param bodys
	 * @return
	 */
	public static Map<String, Object> getEmployeeInfo (String url, Map<String, String> bodys, String bizSecret){
		url = StringUtils.trimToEmpty(url);
		LOGGER.info("主动同步HR人员信息接口被调用。参数：bodys="+bodys+",bizSecret="+bizSecret+",url="+url);
		Map<String,Object> r = new HashMap<String, Object>(3);
		String path = url.substring(url.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys); 
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if(url.length()>=ApiConstants.HTTPS.length()){
			url.substring(0, ApiConstants.HTTPS.length());
		}
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(url, bodys);
		}else{
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果："+rs.toString());
		int statusCode = (int) rs.get("statusCode");
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","获取HR人员信息失败:网络错误");
    		return r;
		}else{
			String result = (String) rs.get("result");
			JSONObject rj = JSONObject.parseObject(result);
			int code = rj.getIntValue("result");
			if(code!=1){
				r.put("code", 0);
	    		r.put("msg","获取HR人员信息失败:"+rs.get("message"));
	    		return r;
			}
			r.put("code", 1);
    		r.put("json", rj);
    		return r;
		}
	}

	/**
	 * 调用epp通知接口
	 * @param url
	 * @param bodys
	 * @return
	 */
	public static Map<String, Object> sendNoticToEpp (String url, Map<String, String> bodys){
		url = StringUtils.trimToEmpty(url);
		LOGGER.info("epp通知接口被调用。参数：bodys="+bodys+",url="+url);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if(url.length()>=ApiConstants.HTTPS.length()){
			url.substring(0, ApiConstants.HTTPS.length());
		}
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(url, bodys);
		}else{
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果："+rs.toString());
		int statusCode = (int) rs.get("statusCode");
		Map<String,Object> r = new HashMap<String, Object>(3);
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","调用EPP通知接口失败:网络错误");
    		return r;
		}else{
			String result = (String) rs.get("result");
			JSONObject rj = JSONObject.parseObject(result);
			int code = rj.getIntValue("error_code");
			if(code==0){
				JSONObject bizResult = rj.getJSONObject("result");
				int bizCode = bizResult.getIntValue("data");
				if(bizCode==1){ //通知成功
					r.put("code", 1);
		    		r.put("msg","已成功通知EPP");
		    		return r;
				}else{
					r.put("code", 0);
		    		r.put("msg","通知EPP失败:"+bizResult.getString("message"));
		    		return r;
				}
			}else{
				r.put("code", 0);
	    		r.put("msg","通知EPP失败:"+rj.getString("message"));
	    		return r;
			}
		}
	}

}
