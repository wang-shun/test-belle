package com.wonhigh.bs.sso.server.service.impl;

import javax.annotation.Resource;

import com.wonhigh.bs.sso.server.common.model.SsoToken;
import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.util.RedisUtil;
import com.wonhigh.bs.sso.server.dto.SsoTokenEntityDto;
import com.wonhigh.bs.sso.server.service.SsoDubboService;

public class SsoDubboServiceImpl implements SsoDubboService{
	
	@Resource
	private RedisUtil redisUtil;

	@Override
	public SsoTokenEntityDto decodeToken(String token, String bizCode) {
		
		SsoTokenEntityDto result = new SsoTokenEntityDto();
		result.setBizCode(bizCode);
		
		if(redisUtil.exists(token)){
			SsoToken loginToken = (SsoToken) redisUtil.get(token);
			
			SsoUser ssoUser = loginToken.getSsoUser();
			String bizUser = null;
			
			if(bizUser!=null && !bizUser.equals("NULL")){
				result.setLoginName(bizUser);
				result.setLoginTime(loginToken.getLoginTime());
				result.setState(1);
			}else{
				result.setState(0);
				result.setMsg("SSO账号："+ssoUser.getLoginName()+"未绑定业务系统账号");
			}
		}
		
		return result;
	}
	
}