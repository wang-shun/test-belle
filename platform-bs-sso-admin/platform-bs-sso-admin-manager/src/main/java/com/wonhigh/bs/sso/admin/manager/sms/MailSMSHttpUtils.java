package com.wonhigh.bs.sso.admin.manager.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonhigh.bs.sso.admin.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;


/**
 * http 发送邮件/短信服务接口
 * @author zhang.rq
 * @date 2016-9-13 上午10:26:10
 * @version 0.2.0
 */
public class MailSMSHttpUtils {
	
    private static Logger logger = LoggerFactory.getLogger(MailSMSHttpUtils.class);	
	
	/**
	 * 发送邮件方法
	 * @param url
	 * @param objVo   HttpEmailSend/HttpSMSSend
	 * @return
	 */
	public static Boolean sendPost(String url, Object objVo) {
		
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		Map<String, String> params=new HashMap<String, String>();
		
		Class<? extends Object> clazz =null;
		
		//1---------------邮件封装
		if(objVo instanceof HttpEmailSend){
			clazz = HttpEmailSend.class;
		}else if(objVo instanceof HttpSMSSend){ 
			//短信参数封装
			clazz = HttpSMSSend.class;
		}else{		
			return false;
		}
		
		//2---------------整理发送内容（短信/邮件）
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String setMeth = method.getName();
			if (method.getName().startsWith("get")) {
				try {
					System.out.println("");
					Object obj=method.invoke(objVo);
					String val=obj==null?"":obj+"";
					if(StringUtils.isBlank(val)){
						continue;
					}
					
					String key=setMeth.substring(3, setMeth.length());
					key=key.substring(0,1).toLowerCase()+key.substring(1,key.length());
					params.put(key, val);
				} catch (IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException e) {
					logger.error("invoke method error==>", e);
					return false;
				}

			}
		}
		
		
		//3----------------开始发送
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf-8");
		// 设置Http Post数据
		if (params != null) {
			NameValuePair[] nvp = new NameValuePair[params.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nvp[i] = new NameValuePair(entry.getKey(), entry.getValue());
				i++;
			}
			method.setRequestBody(nvp);
		}

		try {
			int reCode = client.executeMethod(method);
			if (reCode == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								"utf-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
			return false;
		} finally {
			method.releaseConnection();
		}
		
		if("success".equalsIgnoreCase(response.toString())){
			return true;
		}
		return false;
	}
	

}
