package com.wonhigh.bs.sso.server.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.server.common.constants.ApiConstants;

public class InvokeApiUtil {

private final static Logger LOGGER = LoggerFactory.getLogger(InvokeApiUtil.class);
	
	public static Map<String, Object> registerBizUser(String registerUrl, Map<String, String> bodys, String bizSecret){
		Map<String,Object> r = new HashMap<String, Object>();
		String path = registerUrl.substring(registerUrl.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys);
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = registerUrl.substring(0, 5);
		if("https".equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(registerUrl, bodys);
		}else{
			rs = HttpClientUtil.doPost(registerUrl, bodys);
		}
		int statusCode = (int) rs.get("statusCode");
		String result = (String) rs.get("result");
		JSONObject rj = JSONObject.parseObject(result);
		if(statusCode==0){
			r.put("code", 0);
    		r.put("msg","校验用户失败:网络错误");
    		LOGGER.info("校验用户失败:网络错误");
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
	    		LOGGER.info(rj.get("msg")==null?"校验用户失败":rj.getString("msg"));
			}
    		return r;
		}
		
	}

	/**
	 * 校验biz系统用户密码
	 * @param url
	 * @param bodys
	 * @param bizSecret
	 * @return
	 */
	public static Map<String, Object> checkBizUserPwd(String url, Map<String, String> bodys, String bizSecret) {
		LOGGER.info("检查业务系统账号密码接口被调用。参数：bodys=" + bodys + ",bizSecret=" + bizSecret + ",url" + url);
		Map<String, Object> r = new HashMap<String, Object>(3);
        if(StringUtils.isEmpty(url)||StringUtils.isEmpty(bizSecret)){
            r.put("code", 0);
            r.put("msg","url或bizSecret为空");
            return r;
        }
		String path = url.substring(url.lastIndexOf("/") + 1);
		String nonce = CommonUtil.create32RandomNumber();
		String timestamp = System.currentTimeMillis() + "";
		bodys.put("nonce", nonce);
		bodys.put("timestamp", timestamp);
		bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys);
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = "";
		if (url.length() >= ApiConstants.HTTPS.length()) {
			httpMethod = url.substring(0, ApiConstants.HTTPS.length());
		}
		if (ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)) {
			rs = HttpClientUtil.doPostSSL(url, bodys);
		} else {
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果：" + rs.toString());
		int statusCode = (int) rs.get("statusCode");
		String result = (String) rs.get("result");
		JSONObject rj = JSONObject.parseObject(result);
		if (statusCode == 0) {
			r.put("code", 0);
			r.put("msg", "校验用户失败:网络错误");
			return r;
		} else {
			//code状态：1校验成功 2：用户名不存在 3：密码不对 4：其它错误
			Integer code = rj.getInteger("code");
			if (code == 1) {
				r.put("code", 1);
				r.put("msg", "校验用户成功");
			} else {
				r.put("code", 0);
				r.put("msg", rj.get("msg") == null ? "校验用户失败" : rj.getString("msg"));
			}
			return r;
		}

	}
	
	public static Map<String, Object> delBizUser (String url, Map<String, String> bodys, String bizSecret){
	    LOGGER.info("删除业务系统账号接口被调用。参数：bodys="+bodys+",url="+url+",bizSecret="+bizSecret);
	    Map<String, Object> r = new HashMap<String, Object>(3);
        if(StringUtils.isEmpty(url)||StringUtils.isEmpty(bizSecret)){
            r.put("code", 0);
            r.put("msg","url或bizSecret为空");
            return r;
        }
		String path = url.substring(url.lastIndexOf("/")+1);
		String nonce = CommonUtil.create32RandomNumber();
    	String timestamp = System.currentTimeMillis()+"";
    	bodys.put("nonce", nonce);
    	bodys.put("timestamp", timestamp);
    	bodys.put("method", "POST");
		String sign = SignUtil.sign(bizSecret, "POST", path, bodys); 
		bodys.put("sign", sign);
		Map<String, Object> rs = null;
		String httpMethod = url.substring(0, 5);
		if(ApiConstants.HTTPS.equalsIgnoreCase(httpMethod)){
			rs = HttpClientUtil.doPostSSL(url, bodys);
		}else{
			rs = HttpClientUtil.doPost(url, bodys);
		}
		LOGGER.info("调用结果："+rs);
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
	 * 调用epp通知接口
	 * @param url
	 * @param bodys
	 * @return
	 */
	public static Map<String, Object> sendNoticToEpp (String url, Map<String, String> bodys){
		LOGGER.info("epp通知接口被调用。参数：bodys="+bodys+",url="+url);
		Map<String, Object> r = new HashMap<String, Object>(3);
        if(StringUtils.isEmpty(url)){
            r.put("code", 0);
            r.put("msg","url为空");
            return r;
        }
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
