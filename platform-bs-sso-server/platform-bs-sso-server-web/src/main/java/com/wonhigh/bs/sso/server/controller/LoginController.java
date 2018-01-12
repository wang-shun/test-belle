package com.wonhigh.bs.sso.server.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wonhigh.bs.sso.server.common.model.SsoToken;
import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.util.RedisUtil;
import com.wonhigh.bs.sso.server.manager.SsoUserManager;

@Controller
@RequestMapping("/home")
public class LoginController {
	
	@Resource
	private SsoUserManager ssoUserManager;
	
	@Resource
	private RedisUtil redisUtil;
	
	@RequestMapping("/index")
	public String index() {
		return "login";
	}
	
	@RequestMapping("/login")
	@ResponseBody
	public Map<String,Object> loginSSO(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="loginName",defaultValue="1",required=true) String loginName,
			@RequestParam(value="password",defaultValue="",required=true) String password,
			@RequestParam(value="deviceId",defaultValue="1",required=true) String deviceId
			){
		
		Map<String,Object> result = new HashMap<String, Object>();
		try{
			
			SsoUser user = ssoUserManager.getLoginDn(loginName, password);
			
			if(StringUtils.isEmpty(deviceId)){
				deviceId = "0000";
			}
			
			SsoToken loginToken = new SsoToken(user, deviceId);
			while(redisUtil.exists(loginToken.getToken())){
				loginToken = new SsoToken(user, deviceId);
			}
			
			//登录24小时有效
			redisUtil.set("loginToken", loginToken,1*24*60*60l);
			
			if(user != null){
				result.put("success", true);
				result.put("loginToken", loginToken.getToken());
			}
		}catch(EmptyResultDataAccessException e){
			e.printStackTrace();
			result.put("success", false);
			result.put("msg", "用户不存在");
		}catch(AuthenticationException e){
			result.put("success", false);
			result.put("msg", "密码错误");
		}
		
		return result;
	}

}