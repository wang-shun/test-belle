package com.wonhigh.bs.sso.server.controller.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.aggregation.StringOperators.Split;
import org.springframework.ldap.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.server.common.constants.ApiConstants;
import com.wonhigh.bs.sso.server.common.model.SsoToken;
import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.util.AESUtil;
import com.wonhigh.bs.sso.server.common.util.DateUtil;
import com.wonhigh.bs.sso.server.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.server.common.util.PasswordUtil;
import com.wonhigh.bs.sso.server.common.util.RedisUtil;
import com.wonhigh.bs.sso.server.common.util.SignUtil;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.wonhigh.bs.sso.server.common.vo.ResultModel;
import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.server.manager.BizConfigManager;
import com.wonhigh.bs.sso.server.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.server.manager.SsoUserManager;
import com.yougou.logistics.base.common.exception.ManagerException;

@Controller
@RequestMapping("/api")
public class IndexController {
	
	@Resource
	private SsoUserManager ssoUserManager;
	@Resource
	private BizConfigManager bizConfigManager;
	@Resource
	private SsoUniformUserManager ssoUniformUserManager;
	@Resource
	private RedisUtil redisUtil;
	
	private static final Map<String, BizConfigDTO> bizConfigCache = new HashMap<String, BizConfigDTO>(10);
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public ResultModel login(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="loginName",defaultValue="",required=true) String loginName,
			@RequestParam(value="password",defaultValue="",required=true) String password,
			@RequestParam(value="deviceId",defaultValue="",required=true) String deviceId,
			@RequestParam(value="bizCode",defaultValue="",required=true) String bizCode
			){
		ResultModel result = new ResultModel();
		try{
			LOGGER.info(request.getRemoteAddr()+"请求登录,参数：loginName="+loginName+",password="+password+",deviceId="+deviceId+",bizCode="+bizCode);
			Map<String, Object> p = new HashMap<String, Object>();
			if(StringUtils.isEmpty(loginName)){
				result.setCode(ApiConstants.USER_NOT_EXIST);
				result.setMsg("用户不存在");
				LOGGER.error("用户"+loginName+"不存在");
				return result;
			}
			if(StringUtils.isEmpty(bizCode)){
				result.setCode(ApiConstants.BIZCODE_ERROR);
				result.setMsg("bizCode错误");
				LOGGER.error("bizCode错误,bizCode="+bizCode);
				return result;
			}
			if(StringUtils.isEmpty(password)){
				result.setCode(ApiConstants.PASSWORD_ERROR);
				result.setMsg("密码错误");
				LOGGER.error("用户"+loginName+"密码错误");
				return result;
			}
			//根据bizCode 取出 bizSecret]
			String key = "bizConfig"+bizCode;
			BizConfigDTO bizConfig = bizConfigCache.get(key);
			if(bizConfig==null){
				BizConfigDTO model = new BizConfigDTO();
				p.put("bizCode", bizCode);
				List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(model, p);
				if(bizConfigList==null || bizConfigList.size()==0){
					result.setCode(ApiConstants.BIZCODE_ERROR);
					result.setMsg("bizCode错误");
					LOGGER.info("bizCode错误");
					return result;
				}
				bizConfig = bizConfigList.get(0);
				bizConfigCache.put(key, bizConfig);
			}
			
			password = AESUtil.decode(password, bizConfig.getBizSecret());
			if(StringUtils.isEmpty(password)){
				result.setCode(ApiConstants.PASSWORD_ERROR);
				result.setMsg("密码错误");
				LOGGER.error("用户"+loginName+"密码错误");
				return result;
			}
			SsoUser user = null;
			try{
				//首先用户uid进行登录
				user = ssoUserManager.getLoginDn(loginName, password);
			}catch(EmptyResultDataAccessException e){
				try{
					//用户不存在再使用手机号登录
					user = ssoUserManager.getLoginDnByMobile(loginName, password);
					int state = user.getState().intValue();
					if(state==0){
					    result.setCode(ApiConstants.BIZUSER_NONACTIVATED);
	                    result.setMsg("用户未激活");
	                    LOGGER.error("用户"+loginName+"未激活");
	                    return result;
					}
					if(state==2){
					    result.setCode(ApiConstants.BIZUSER_LOCKED);
	                    result.setMsg("用户被锁定");
	                    LOGGER.error("用户"+loginName+"被锁定");
	                    return result;
					}
				}catch(EmptyResultDataAccessException e2){
					result.setCode(ApiConstants.USER_NOT_EXIST);
					result.setMsg("用户不存在");
					LOGGER.error("用户"+loginName+"不存在");
					return result;
				}catch(AuthenticationException e2){
					result.setCode(ApiConstants.PASSWORD_ERROR);
					result.setMsg("密码错误");
					LOGGER.error("用户"+loginName+"密码错误");
					return result;
				}
			}catch(AuthenticationException e){
				result.setCode(ApiConstants.PASSWORD_ERROR);
				result.setMsg("密码错误");
				LOGGER.error("用户"+loginName+"密码错误");
				return result;
			}
			//app登录会生成token
			SsoToken loginToken = new SsoToken(user,deviceId);
			while (redisUtil.exists(loginToken.getToken())) {
				loginToken = new SsoToken(user,deviceId);
			}
			//登录信息保存24h
			redisUtil.set(loginToken.getToken(), loginToken, 1*24*60*60l);
			Map<String,Object> content = new HashMap<String, Object>();
			content.put("loginName", loginName);
			content.put("deviceId", deviceId);
			result.setContent(content);
			if(user != null){
				//AES加密
				String token = AESUtil.encode(loginToken.getToken(), bizConfig.getBizSecret());
				content.put("ssoToken", token);
				content.put("loginName",  AESUtil.encode(user.getLoginName(), bizConfig.getBizSecret()));
				content.put("sureName",  AESUtil.encode(user.getSureName(), bizConfig.getBizSecret()));
				String employeeNumber = user.getEmployeeNumber();
				if(employeeNumber==null){
					employeeNumber = "";
				}
				if(StringUtils.equals("NULL", employeeNumber)){
					employeeNumber = "";
				}
				content.put("employeeNumber",  AESUtil.encode(employeeNumber, bizConfig.getBizSecret()));
				String mobile = user.getMobile();
				if(mobile==null){
					mobile = "";
				}
				if(StringUtils.equals("0", mobile)){
					mobile = "";
				}
				content.put("mobile",  AESUtil.encode(mobile, bizConfig.getBizSecret()));
				String email = user.getEmail();
				if(email==null){
					email = "";
				}
				if(StringUtils.equals("NULL", email)){
					email = "";
				}
				content.put("email",  AESUtil.encode(email, bizConfig.getBizSecret()));
				String organizationCode = user.getOrganizationCode();
				if(organizationCode==null){
					organizationCode = "";
				}
				if(StringUtils.equals("NULL", organizationCode)){
					organizationCode = "";
				}
				content.put("organizationCode",  AESUtil.encode(organizationCode, bizConfig.getBizSecret()));
				String organizationalUnitName = user.getOrganizationalUnitName();
				if(organizationalUnitName==null){
					organizationCode = "";
				}
				if(StringUtils.equals("NULL", organizationalUnitName)){
					organizationalUnitName = "";
				}
				content.put("organizationalUnitName",  AESUtil.encode(organizationalUnitName, bizConfig.getBizSecret()));
				result.setCode(ApiConstants.SUCCESS);
				result.setMsg("登录成功");
				LOGGER.info("用户"+loginName+"登录成功");
			}
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("用户"+loginName+"登录报错。"+e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 验证登录token有效性/同时生生新的token
	 * @param request
	 * @param resp
	 * @param timeStamp 时间戳 毫秒数
	 * @param nonce 随机数 32位
	 * @param ssoToken 登录产生的token （加密后传输）
	 * @param bizCode 业务系统代码
	 * @param method 请求方式
	 * @param sign 生成的签名
	 * @return 业务系统此次登录的账号
	 */
	@RequestMapping(value="/verifySsoToken",method=RequestMethod.POST)
	@ResponseBody
	public ResultModel verifySsoToken(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
			@RequestParam(value="nonce",defaultValue="",required=true) String nonce,
			@RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
			@RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
			@RequestParam(value="method",defaultValue="",required=true) String method,
			@RequestParam(value="sign",defaultValue="",required=true) String sign
			){
		ResultModel result = new ResultModel();
		Map<String,Object> content = new HashMap<String,Object>();
		result.setContent(content);
		StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
		try{
			LOGGER.info(request.getRemoteAddr()+"验证ssoToken有效性,参数：ssoToken="+ssoToken+",bizCode="+bizCode+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",sign="+sign);
			long timeStampV = Long.valueOf(timeStamp);
			long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
			if(spec>0){
				result.setCode(ApiConstants.REQUEST_TIMED_OUT);
				result.setMsg("请求超时");
				LOGGER.error("请求超时,timeStamp="+timeStamp);
				return result;
			}
			if(StringUtils.isEmpty(sign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			if(StringUtils.isEmpty(bizCode)){
				result.setCode(ApiConstants.BIZCODE_ERROR);
				result.setMsg("bizCode错误");
				LOGGER.error("bizCode错误,bizCode="+bizCode);
				return result;
			}
			if(StringUtils.isEmpty(ssoToken)){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			Map<String,String> bodys = new HashMap<String, String>();
			bodys.put("timeStamp", timeStamp);
			bodys.put("nonce", nonce);
			bodys.put("ssoToken", ssoToken);
			bodys.put("bizCode", bizCode);
			bodys.put("method", method);
			//根据bizCode 取出 bizSecret
			String key = "bizConfig"+bizCode;
			BizConfigDTO bizConfig = bizConfigCache.get(key);
			if(bizConfig==null){
				BizConfigDTO model = new BizConfigDTO();
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("bizCode", bizCode);
				List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(model, p);
				if(bizConfigList==null || bizConfigList.size()==0){
					result.setCode(ApiConstants.BIZCODE_ERROR);
					result.setMsg("bizCode错误");
					LOGGER.info("bizCode错误");
					return result;
				}
				bizConfig = bizConfigList.get(0);
				bizConfigCache.put(key, bizConfig);
			}
			String bizSecret = bizConfig.getBizSecret();
			String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
			if(!sign.equals(vfSign)){
				result.setCode(0);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			//解密token
			ssoToken = AESUtil.decode(ssoToken, bizSecret);
			if(ssoToken==null){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			if(redisUtil.exists(ssoToken)){
				//移除旧的token
				SsoToken token = (SsoToken) redisUtil.get(ssoToken);
				Long loginTime = token.getLoginTime();
				redisUtil.remove(ssoToken);
				//生成新的token
				SsoUser ssoUser = token.getSsoUser();
				String deviceId = token.getDeviceId();
				token = new SsoToken(ssoUser,deviceId);
				//更新
				while (redisUtil.exists(token.getToken())) {
					token = new SsoToken(ssoUser,deviceId);
				}
				token.setLoginTime(loginTime);
				redisUtil.set(token.getToken(), token, 1*24*60*60l);
				result.setContent(content);
				//对ssoToken AES加密
				content.put("ssoToken", AESUtil.encode(token.getToken(), bizSecret));
				content.put("loginName", AESUtil.encode(ssoUser.getLoginName(), bizSecret));
				content.put("deviceId", deviceId);
				content.put("loginTime", loginTime);
				result.setCode(ApiConstants.SUCCESS);
				LOGGER.info("ssoToken验证成功,ssoToken="+token.getToken());
			}else{
				result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
				result.setMsg("token已失效");
				LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
				return result;
			}
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("验证ssoToken有效性报错。"+e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value="/getBizLoginName",method=RequestMethod.POST)
	@ResponseBody
	public ResultModel getBizLoginName(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
			@RequestParam(value="nonce",defaultValue="",required=true) String nonce,
			@RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
			@RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
			@RequestParam(value="appCode",defaultValue="",required=false) String appCode,
			@RequestParam(value="method",defaultValue="",required=true) String method,
			@RequestParam(value="sign",defaultValue="",required=true) String sign
			){
		ResultModel result = new ResultModel();
		Map<String,Object> content = new HashMap<String,Object>();
		result.setContent(content);
		StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
		try{
			LOGGER.info(request.getRemoteAddr()+"获取业务系统登录名,参数：ssoToken="+ssoToken+",bizCode="+bizCode+",appCode="+appCode+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",sign="+sign);
			long timeStampV = Long.valueOf(timeStamp);
			long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
			if(spec>0){
				result.setCode(ApiConstants.REQUEST_TIMED_OUT);
				result.setMsg("请求超时");
				LOGGER.error("请求超时,timeStamp="+timeStamp);
				return result;
			}
			if(StringUtils.isEmpty(sign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			if(StringUtils.isEmpty(bizCode)){
				result.setCode(ApiConstants.BIZCODE_ERROR);
				result.setMsg("bizCode错误");
				LOGGER.error("bizCode错误,bizCode="+bizCode);
				return result;
			}
			if(StringUtils.isEmpty(ssoToken)){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			Map<String,String> bodys = new HashMap<String, String>();
			bodys.put("timeStamp", timeStamp);
			bodys.put("nonce", nonce);
			bodys.put("ssoToken", ssoToken);
			bodys.put("bizCode", bizCode);
			if(!StringUtils.isEmpty(appCode)){
				bodys.put("appCode", appCode);
			}
			bodys.put("method", method);
			//根据bizCode 取出 bizSecret
			String key = "bizConfig"+bizCode;
			BizConfigDTO bizConfig = bizConfigCache.get(key);
			if(bizConfig==null){
				BizConfigDTO model = new BizConfigDTO();
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("bizCode", bizCode);
				List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(model, p);
				if(bizConfigList==null || bizConfigList.size()==0){
					result.setCode(ApiConstants.BIZCODE_ERROR);
					result.setMsg("bizCode错误");
					LOGGER.info("bizCode错误");
					return result;
				}
				bizConfig = bizConfigList.get(0);
				bizConfigCache.put(key, bizConfig);
			}
			String bizSecret = bizConfig.getBizSecret();
			String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
			if(!sign.equals(vfSign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			//解密token
			ssoToken = AESUtil.decode(ssoToken, bizSecret);
			if(ssoToken==null){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			if(redisUtil.exists(ssoToken)){
				SsoToken token = (SsoToken) redisUtil.get(ssoToken);
				SsoUser ssoUser = token.getSsoUser();
				String bizUser = null;
				if(StringUtils.isEmpty(appCode)) appCode = bizCode;
				
				String bizUserJS = ssoUser.getBizUser();
				JSONObject bizUserJO = JSONObject.parseObject(bizUserJS);
				if(bizUserJO.get(appCode)!=null){
					bizUser = bizUserJO.getString(appCode);
				}
				
				if(bizUser!=null){
					//加密业务系统的登录账号
					bizUser = AESUtil.encode(bizUser, bizSecret);
					result.setCode(ApiConstants.SUCCESS);
					content.put("loginName", bizUser);
					content.put("loginTime", token.getLoginTime());
					LOGGER.info("SSO账号："+ssoUser.getLoginName()+"获取业务系统账号成功。bizUser="+bizUser);
				}else{
					result.setCode(ApiConstants.NOT_FOUND_BIZ_USER);
					result.setMsg("SSO账号："+ssoUser.getLoginName()+"未绑定业务系统账号");
					LOGGER.error("SSO账号："+ssoUser.getLoginName()+"未绑定业务系统账号");
				}
			}else{
				result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
				result.setMsg("token已失效");
				LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
			}
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("获取业务系统登录名报错。"+e.getMessage());
		}
		LOGGER.info("获取业务系统登录名成功");
		return result;
	}
	
	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
	@ResponseBody
	public ResultModel changePassword(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
			@RequestParam(value="nonce",defaultValue="",required=true) String nonce,
			@RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
			@RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
			//@RequestParam(value="appCode",defaultValue="",required=false) String appCode,
			@RequestParam(value="ssoUserLoginName",defaultValue="",required=false) String ssoUserLoginName,
			@RequestParam(value="oldPassword",defaultValue="",required=false) String oldPassword,
			@RequestParam(value="newPassword",defaultValue="",required=false) String newPassword,
			@RequestParam(value="method",defaultValue="",required=true) String method,
			@RequestParam(value="sign",defaultValue="",required=true) String sign
			){
		ResultModel result = new ResultModel();
		Map<String,Object> content = new HashMap<String,Object>();
		result.setContent(content);
		StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
		try{
			LOGGER.info(request.getRemoteAddr()+"更改密码,参数：ssoToken="+ssoToken+",bizCode="+bizCode+",ssoUserLoginName="+ssoUserLoginName+",oldPassword="+oldPassword+",newPassword="+newPassword+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",sign="+sign);
			long timeStampV = Long.valueOf(timeStamp);
			long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
			if(spec>0){
				result.setCode(ApiConstants.REQUEST_TIMED_OUT);
				result.setMsg("请求超时");
				LOGGER.error("请求超时,timeStamp="+timeStamp);
				return result;
			}
			if(StringUtils.isEmpty(sign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			if(StringUtils.isEmpty(bizCode)){
				result.setCode(ApiConstants.BIZCODE_ERROR);
				result.setMsg("bizCode错误");
				LOGGER.error("bizCode错误,bizCode="+bizCode);
				return result;
			}
			if(StringUtils.isEmpty(ssoToken)){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			if(StringUtils.isEmpty(oldPassword)){
				result.setCode(ApiConstants.PASSWORD_ERROR);
				result.setMsg("账号密码错误");
				LOGGER.error("账号"+ssoUserLoginName+"密码错误");
				return result;
			}
			if(StringUtils.isEmpty(newPassword)){
				result.setCode(ApiConstants.NEW_PASSWORD_ERROR);
				result.setMsg("账号新密码不符合要求");
				LOGGER.error("账号"+ssoUserLoginName+"密码错误");
				return result;
			}
			Map<String,String> bodys = new HashMap<String, String>();
			bodys.put("timeStamp", timeStamp);
			bodys.put("nonce", nonce);
			bodys.put("ssoToken", ssoToken);
			bodys.put("bizCode", bizCode);
			bodys.put("ssoUserLoginName", ssoUserLoginName);
			bodys.put("oldPassword", oldPassword);
			bodys.put("newPassword", newPassword);
			/*if(!StringUtils.isEmpty(appCode)){
				bodys.put("appCode", appCode);
			}*/
			bodys.put("method", method);
			//根据bizCode 取出 bizSecret
			String key = "bizConfig"+bizCode;
			BizConfigDTO bizConfig = bizConfigCache.get(key);
			if(bizConfig==null){
				BizConfigDTO model = new BizConfigDTO();
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("bizCode", bizCode);
				List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(model, p);
				if(bizConfigList==null || bizConfigList.size()==0){
					result.setCode(ApiConstants.BIZCODE_ERROR);
					result.setMsg("bizCode错误");
					LOGGER.info("bizCode错误");
					return result;
				}
				bizConfig = bizConfigList.get(0);
				bizConfigCache.put(key, bizConfig);
			}
			String bizSecret = bizConfig.getBizSecret();
			String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
			if(!sign.equals(vfSign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			//解密token
			ssoToken = AESUtil.decode(ssoToken, bizSecret);
			if(ssoToken==null){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			if(redisUtil.exists(ssoToken)){
				SsoToken token = (SsoToken) redisUtil.get(ssoToken);
				SsoUser ssoUser = token.getSsoUser();
				if(ssoUser!=null){
					//校验biz用户是否一致
					if(!StringUtils.equals(ssoUser.getLoginName(), ssoUserLoginName)){
						result.setCode(ApiConstants.OTHER_ERROR);
						result.setMsg("登录账号和请求修改密码的账号不一致");
						LOGGER.error("登录账号和请求修改密码的账号不一致");
						return result;
					}
					//解密旧密码
					oldPassword = AESUtil.decode(oldPassword, bizSecret);
					if(StringUtils.isEmpty(oldPassword)){
						result.setCode(ApiConstants.PASSWORD_ERROR);
						result.setMsg("账号密码错误");
						LOGGER.error("账号"+ssoUserLoginName+"密码错误");
						return result;
					}
					try{
						ssoUserManager.getLoginDn(ssoUserLoginName,oldPassword);
					}catch(EmptyResultDataAccessException e){
						result.setCode(ApiConstants.USER_NOT_EXIST);
						result.setMsg("账号不存在");
						LOGGER.error("账号"+ssoUserLoginName+"不存在");
						return result;
					}catch(AuthenticationException e){
						result.setCode(ApiConstants.PASSWORD_ERROR);
						result.setMsg("账号密码错误");
						LOGGER.error("账号"+ssoUserLoginName+"密码错误");
						return result;
					}
					
					//解密新密码
					newPassword = AESUtil.decode(newPassword, bizSecret);
					if(StringUtils.isEmpty(newPassword)){
						result.setCode(ApiConstants.NEW_PASSWORD_ERROR);
						result.setMsg("账号新密码不能为空");
						LOGGER.error("账号"+ssoUserLoginName+"密码错误");
						return result;
					}
					String md5Password = PasswordUtil.createPassword(newPassword);
					ssoUser.setPassword(md5Password);
					//修改mysql数据
					SsoUniformUserDTO userdto = ssoUniformUserManager.findByLoginName(ssoUser.getLoginName());
					if(userdto!=null){
						userdto.setPassword(md5Password);
						LOGGER.info("修改ssoUserDto密码到mysql... "+userdto);
						ssoUniformUserManager.modifyById(userdto);
						LOGGER.info("修改ssoUserDto密码到mysql成功。 ");
					}
					//修改ldap
					try{
						SsoUser userl = ssoUserManager.findByPrimaryKey(ssoUser.getLoginName());
						if(userl==null){
							userl = ssoUserManager.findByMobile(ssoUser.getLoginName());
							if(userl!=null){
								userl.setPassword(ssoUser.getPassword());
								LOGGER.info("修改ssoUser密码到ldap... "+ssoUser);
								ssoUserManager.update(userl);
								LOGGER.info("修改ssoUser密码到ldap成功。 ");
							}
						}else{
							userl.setPassword(ssoUser.getPassword());
							LOGGER.info("修改ssoUser密码到ldap... "+ssoUser);
							ssoUserManager.update(userl);
							LOGGER.info("修改ssoUser密码到ldap成功。 ");
						}
					}catch(Exception e){
						e.printStackTrace();
						LOGGER.info("修改ssoUser密码到ldap失败。 e="+e);
					}
					result.setCode(ApiConstants.SUCCESS);
					result.setMsg("修改成功");
					LOGGER.info("修改成功");
				}else{
					result.setCode(ApiConstants.USER_NOT_EXIST);
					if(ssoUser!=null){
						result.setMsg("SSO账号："+ssoUser.getLoginName()+"未登录");
						LOGGER.error("SSO账号："+ssoUser.getLoginName()+"未登录");
					}
				}
			}else{
				result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
				result.setMsg("token已失效");
				LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
			}
			
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("更改密码报错。"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 绑定业务系统账号
	 * @param request
	 * @param resp
	 * @param timeStamp
	 * @param nonce
	 * @param ssoToken
	 * @param bizCode
	 * @param appCode
	 * @param method
	 * @param bizUserLoginName
	 * @param bizUserPassword
	 * @param sign
	 * @return
	 */
	@RequestMapping(value="/bindBizUser",method=RequestMethod.POST)
    @ResponseBody
    public ResultModel bindBizUser(
            HttpServletRequest request,
            HttpServletResponse resp,
            @RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
            @RequestParam(value="nonce",defaultValue="",required=true) String nonce,
            @RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
            @RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
            @RequestParam(value="appCode",defaultValue="",required=true) String appCode,
            @RequestParam(value="method",defaultValue="",required=true) String method,
            @RequestParam(value="bizUserLoginName",defaultValue="",required=true) String bizUserLoginName,
            @RequestParam(value="bizUserPassword",defaultValue="",required=true) String bizUserPassword,
            @RequestParam(value="sign",defaultValue="",required=true) String sign
            ){
        ResultModel result = new ResultModel();
        Map<String,Object> content = new HashMap<String,Object>();
        result.setContent(content);
        StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
        try{
            LOGGER.info(request.getRemoteAddr()+"绑定业务系统账号,参数：ssoToken="+ssoToken+",bizCode="
                    +bizCode+",appCode="+appCode+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",bizUserLoginName="+bizUserLoginName+",bizUserPassword="+bizUserPassword+",sign="+sign);
            long timeStampV = Long.valueOf(timeStamp);
            long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
            if(spec>0){
                result.setCode(ApiConstants.REQUEST_TIMED_OUT);
                result.setMsg("请求超时");
                LOGGER.error("请求超时,timeStamp="+timeStamp);
                return result;
            }
            if(StringUtils.isEmpty(sign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            if(StringUtils.isEmpty(bizCode)){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.error("bizCode错误,bizCode="+bizCode);
                return result;
            }
            if(StringUtils.isEmpty(appCode)){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("appCode错误");
                LOGGER.error("bizCode错误,bizCode="+appCode);
                return result;
            }
            if(StringUtils.isEmpty(ssoToken)){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            if(StringUtils.isEmpty(bizUserLoginName)){
                result.setCode(ApiConstants.BIZUSERLOGINNAME_ERROR);
                result.setMsg("bizUserLoginName 参数不能为空");
                LOGGER.error("bizUserLoginName 参数不能为空");
                return result;
            }
            if(StringUtils.isEmpty(bizUserPassword)){
                result.setCode(ApiConstants.BIZUSERLOGINNAME_ERROR);
                result.setMsg("bizUserPassword 参数不能为空");
                LOGGER.error("bizUserPassword 参数不能为空");
                return result;
            }
            
            Map<String,String> bodys = new HashMap<String, String>();
            bodys.put("timeStamp", timeStamp);
            bodys.put("nonce", nonce);
            bodys.put("ssoToken", ssoToken);
            bodys.put("bizCode", bizCode);
            bodys.put("bizUserLoginName", bizUserLoginName);
            bodys.put("bizUserPassword", bizUserPassword);
            bodys.put("appCode", appCode);
            bodys.put("method", method);
            //根据bizCode 取出 bizSecret
            BizConfigDTO bizConfig = this.getBizConfig(bizCode);
            if(bizConfig==null){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.info("bizCode错误");
                return result;
            }
            String bizSecret = bizConfig.getBizSecret();
            String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
            if(!sign.equals(vfSign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            //解密token
            ssoToken = AESUtil.decode(ssoToken, bizSecret);
            if(ssoToken==null){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            if(redisUtil.exists(ssoToken)){
                SsoToken token = (SsoToken) redisUtil.get(ssoToken);
                SsoUser ssoUser = token.getSsoUser();
                //从数据库取最新的数据
                ssoUser = ssoUserManager.findByPrimaryKey(ssoUser.getLoginName());
                String bizUser = null;
                if(StringUtils.isEmpty(appCode)) appCode = bizCode;
                String bizUserJS = ssoUser.getBizUser();
                JSONObject bizUserJO = JSONObject.parseObject(bizUserJS);
                if(bizUserJO.get(appCode)!=null){
                    //已经绑定该业务系统其他账号
                    bizUser = bizUserJO.getString(appCode);
                    result.setCode(ApiConstants.ALREADY_BIND_BIZ_USER);
                    result.setMsg("该账号已经绑定该业务系统的账号："+bizUser);
                    LOGGER.error("该账号已经绑定该业务系统的账号,bizUser="+bizUser);
                    return result;
                }
                
                //校验appCode
                BizConfigDTO appBizConfig = this.getBizConfig(appCode);
                if(appBizConfig==null){
                    result.setCode(ApiConstants.APPCODE_ERROR);
                    result.setMsg("appCode错误:appCode不存在");
                    LOGGER.info("appCode错误:appCode"+appCode+"不存在");
                    return result;
                }
                //解密密码
                String plaintextBizUserPassword = AESUtil.decode(bizUserPassword, bizSecret);
                if(StringUtils.isEmpty(plaintextBizUserPassword)){
                    result.setCode(ApiConstants.BIZUSERLOGINNAME_ERROR);
                    result.setMsg("bizUserPassword 参数不能为空");
                    LOGGER.error("bizUserPassword 参数不能为空");
                    return result;
                }
                //调用业务系统验证用户名密码接口验证用户是否存在
                Map<String, String> paramsMap = new HashMap<String, String>(7);
                paramsMap.put("loginName", bizUserLoginName);
                String encryptPwd = AESUtil.encode(plaintextBizUserPassword, appBizConfig.getBizSecret());
                paramsMap.put("password", encryptPwd);
                paramsMap.put("bizCode", appBizConfig.getBizCode());
                Map<String, Object> map = InvokeApiUtil.checkBizUserPwd(appBizConfig.getVerifyUserPwdUrl(), paramsMap, appBizConfig.getBizSecret());
                int code = (int) map.get("code");
                if (code != 1) {
                    result.setCode(ApiConstants.OTHER_ERROR);
                    result.setMsg("校验biz账户时出错:"+(String) map.get("msg"));
                    LOGGER.error("校验biz账户时出错:"+(String) map.get("msg"));
                    return result;
                }
                bizUserJO.put(appBizConfig.getBizCode(), bizUserLoginName);
                ssoUser.setBizUser(bizUserJO.toJSONString());
                SsoUniformUserDTO ssoUniformUserDTO = ssoUniformUserManager.findByLoginName(ssoUser.getLoginName());
                if(ssoUniformUserDTO==null){
                    result.setCode(ApiConstants.OTHER_ERROR);
                    result.setMsg("mysql中没有此用户信息");
                    LOGGER.error("mysql中没有此用户信息");
                    return result;
                }
                ssoUniformUserDTO.setBizUser(bizUserJO.toJSONString());
                ssoUniformUserManager.updateToMysqlAndLdap(ssoUniformUserDTO);
                token.setSsoUser(ssoUser);
                redisUtil.set(ssoToken, token);
                result.setCode(ApiConstants.SUCCESS);
                result.setMsg("绑定成功");
                LOGGER.info("绑定成功");
            }else{
                result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
                result.setMsg("token已失效");
                LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
            }
        }catch(Exception e){
            result.setCode(ApiConstants.OTHER_ERROR);
            result.setMsg(e.getMessage());
            LOGGER.error("绑定业务系统账号报错。"+e.getMessage());
        }
        return result;
    }
	
	/**
     * 解除绑定业务系统账号
     * @param request
     * @param resp
     * @param timeStamp
     * @param nonce
     * @param ssoToken
     * @param bizCode
     * @param appCode
     * @param method
     * @param sign
     * @return
     */
    @RequestMapping(value="/unBindBizUser",method=RequestMethod.POST)
    @ResponseBody
    public ResultModel unBindBizUser(
            HttpServletRequest request,
            HttpServletResponse resp,
            @RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
            @RequestParam(value="nonce",defaultValue="",required=true) String nonce,
            @RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
            @RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
            @RequestParam(value="appCode",defaultValue="",required=true) String appCode,
            @RequestParam(value="method",defaultValue="",required=true) String method,
            @RequestParam(value="sign",defaultValue="",required=true) String sign
            ){
        ResultModel result = new ResultModel();
        Map<String,Object> content = new HashMap<String,Object>();
        result.setContent(content);
        StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
        try{
            LOGGER.info(request.getRemoteAddr()+"解除绑定业务系统账号,参数：ssoToken="+ssoToken+",bizCode="
                    +bizCode+",appCode="+appCode+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",sign="+sign);
            long timeStampV = Long.valueOf(timeStamp);
            long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
            if(spec>0){
                result.setCode(ApiConstants.REQUEST_TIMED_OUT);
                result.setMsg("请求超时");
                LOGGER.error("请求超时,timeStamp="+timeStamp);
                return result;
            }
            if(StringUtils.isEmpty(sign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            if(StringUtils.isEmpty(bizCode)){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.error("bizCode错误,bizCode="+bizCode);
                return result;
            }
            if(StringUtils.isEmpty(appCode)){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("appCode错误");
                LOGGER.error("appCode错误,appCode="+appCode);
                return result;
            }
            if(StringUtils.isEmpty(ssoToken)){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            Map<String,String> bodys = new HashMap<String, String>();
            bodys.put("timeStamp", timeStamp);
            bodys.put("nonce", nonce);
            bodys.put("ssoToken", ssoToken);
            bodys.put("bizCode", bizCode);
            bodys.put("appCode", appCode);
            bodys.put("method", method);
            //根据bizCode 取出 bizSecret
            BizConfigDTO bizConfig = this.getBizConfig(bizCode);
            if(bizConfig==null){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.info("bizCode错误");
                return result;
            }
            String bizSecret = bizConfig.getBizSecret();
            String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
            if(!sign.equals(vfSign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            //解密token
            ssoToken = AESUtil.decode(ssoToken, bizSecret);
            if(ssoToken==null){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            if(redisUtil.exists(ssoToken)){
                SsoToken token = (SsoToken) redisUtil.get(ssoToken);
                SsoUser ssoUser = token.getSsoUser();
                //从数据库取最新的数据
                ssoUser = ssoUserManager.findByPrimaryKey(ssoUser.getLoginName());
                String bizUser = null;
                if(StringUtils.isEmpty(appCode)) appCode = bizCode;
                String bizUserJS = ssoUser.getBizUser();
                JSONObject bizUserJO = JSONObject.parseObject(bizUserJS);
                if(bizUserJO.get(appCode)!=null){
                    //已经绑定该业务系统其他账号
                    bizUserJO.remove(appCode);
                }else{
                    bizUser = bizUserJO.getString(appCode);
                    result.setCode(ApiConstants.ALREADY_BIND_BIZ_USER);
                    result.setMsg("该账号还未绑定该业务系统的账号");
                    LOGGER.error("该账号还未绑定该业务系统的账号");
                    return result;
                }
                ssoUser.setBizUser(bizUserJO.toJSONString());
                SsoUniformUserDTO ssoUniformUserDTO = ssoUniformUserManager.findByLoginName(ssoUser.getLoginName());
                if(ssoUniformUserDTO==null){
                    result.setCode(ApiConstants.OTHER_ERROR);
                    result.setMsg("mysql中没有此用户信息");
                    LOGGER.error("mysql中没有此用户信息");
                    return result;
                }
                ssoUniformUserDTO.setBizUser(bizUserJO.toJSONString());
                ssoUniformUserManager.updateToMysqlAndLdap(ssoUniformUserDTO);
                token.setSsoUser(ssoUser);
                redisUtil.set(ssoToken, token);
                result.setCode(ApiConstants.SUCCESS);
                result.setMsg("解绑成功");
                LOGGER.info("解绑成功");
            }else{
                result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
                result.setMsg("token已失效");
                LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
            }
        }catch(Exception e){
            result.setCode(ApiConstants.OTHER_ERROR);
            result.setMsg(e.getMessage());
            LOGGER.error("解绑报错。"+e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取业务系统列表
     * @param request
     * @param resp
     * @param timeStamp
     * @param nonce
     * @param ssoToken
     * @param bizCode
     * @param appCode
     * @param method
     * @param type 0：全部 ，1：已绑定，2：未绑定
     * @param sign
     * @return
     */
    @RequestMapping(value="/bizConfigList",method=RequestMethod.POST)
    @ResponseBody
    public ResultModel bizConfigList(
            HttpServletRequest request,
            HttpServletResponse resp,
            @RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
            @RequestParam(value="nonce",defaultValue="",required=true) String nonce,
            @RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
            @RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
            @RequestParam(value="method",defaultValue="POST",required=true) String method,
            @RequestParam(value="type",defaultValue="0",required=true) Integer type,
            @RequestParam(value="sign",defaultValue="",required=true) String sign
            ){
        ResultModel result = new ResultModel();
        StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
        Map<String,Object> content = new HashMap<String,Object>();
        result.setContent(content);
        try{
            LOGGER.info(request.getRemoteAddr()+"获取业务系统列表,参数：ssoToken="+ssoToken+",bizCode="
                    +bizCode+",type="+type+",nonce="+nonce+",timeStamp="+timeStamp+",method="+method+",sign="+sign);
            long timeStampV = Long.valueOf(timeStamp);
            long spec = System.currentTimeMillis()-timeStampV-10*60*1000;
            if(spec>0){
                result.setCode(ApiConstants.REQUEST_TIMED_OUT);
                result.setMsg("请求超时");
                LOGGER.error("请求超时,timeStamp="+timeStamp);
                return result;
            }
            if(StringUtils.isEmpty(sign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            if(StringUtils.isEmpty(bizCode)){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.error("bizCode错误,bizCode="+bizCode);
                return result;
            }
            if(StringUtils.isEmpty(ssoToken)){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            Map<String,String> bodys = new HashMap<String, String>();
            bodys.put("timeStamp", timeStamp);
            bodys.put("nonce", nonce);
            bodys.put("ssoToken", ssoToken);
            bodys.put("bizCode", bizCode);
            bodys.put("method", method);
            //根据bizCode 取出 bizSecret
            BizConfigDTO bizConfig = this.getBizConfig(bizCode);
            if(bizConfig==null){
                result.setCode(ApiConstants.BIZCODE_ERROR);
                result.setMsg("bizCode错误");
                LOGGER.info("bizCode错误");
                return result;
            }
            String bizSecret = bizConfig.getBizSecret();
            String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
            if(!sign.equals(vfSign)){
                result.setCode(ApiConstants.SIGNATURE_ERROR);
                result.setMsg("签名错误");
                LOGGER.error("签名错误,sign="+sign);
                return result;
            }
            
            Map<String,Object> params = new HashMap<String, Object>(3);
            params.put("joinUpEppFlag", 1);
            if(type==0){
                //查询全部bizCofing
                List<BizConfigVO> list = bizConfigManager.selectForApi(params);
                result.setContent(list);
                result.setCode(ApiConstants.SUCCESS);
                result.setMsg("获取业务系统列表成功");
                LOGGER.info("获取业务系统列表成功");
                return result;
            }
            
            //解密token
            ssoToken = AESUtil.decode(ssoToken, bizSecret);
            if(ssoToken==null){
                result.setCode(ApiConstants.SSOTOKEN_ERROR);
                result.setMsg("ssoToken 参数错误");
                LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
                return result;
            }
            if(redisUtil.exists(ssoToken)){
                SsoToken token = (SsoToken) redisUtil.get(ssoToken);
                SsoUser ssoUser = token.getSsoUser();
                //从数据库取最新的数据
                ssoUser = ssoUserManager.findByPrimaryKey(ssoUser.getLoginName());
                String bizUser = null;
                String bizUserJS = ssoUser.getBizUser();
                JSONObject bizUserJO = JSONObject.parseObject(bizUserJS);
                
                Set<String> keySet = bizUserJO.keySet();
                StringBuilder bindBizCodes = new StringBuilder(); 
                bindBizCodes.append(" (");
                for (String bizCodeKey : keySet) {
                    bindBizCodes.append("'").append(bizCodeKey).append("',");
                }
                bindBizCodes.append("'')");
                if(type==1){
                    //查询已绑定
                    params.put("inBizCodes", bindBizCodes.toString());
                    List<BizConfigVO> list = bizConfigManager.selectForApi(params);
                    result.setContent(list);
                    result.setCode(ApiConstants.SUCCESS);
                    result.setMsg("获取业务系统列表成功");
                    LOGGER.info("获取业务系统列表成功");
                    return result;
                }else if(type==2){
                    //查询未绑定
                    params.put("notInBizCodes", bindBizCodes.toString());
                    List<BizConfigVO> list = bizConfigManager.selectForApi(params);
                    result.setContent(list);
                    result.setCode(ApiConstants.SUCCESS);
                    result.setMsg("获取业务系统列表成功");
                    LOGGER.info("获取业务系统列表成功");
                    return result;
                }else{
                    result.setCode(ApiConstants.OTHER_ERROR);
                    result.setMsg("type参数有误");
                    LOGGER.error("type参数有误。");
                }
            }else{
                result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
                result.setMsg("token已失效");
                LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
            }
        }catch(Exception e){
            result.setCode(ApiConstants.OTHER_ERROR);
            result.setMsg(e.getMessage());
            LOGGER.error("解绑报错。"+e.getMessage());
        }
        return result;
    }
    
	@RequestMapping(value="/logout",method=RequestMethod.POST)
	@ResponseBody
	public ResultModel logout(
			HttpServletRequest request,
			HttpServletResponse resp,
			@RequestParam(value="timeStamp",defaultValue="",required=true) String timeStamp,
			@RequestParam(value="nonce",defaultValue="",required=true) String nonce,
			@RequestParam(value="ssoToken",defaultValue="",required=true) String ssoToken,
			@RequestParam(value="bizCode",defaultValue="",required=true) String bizCode,
			@RequestParam(value="method",defaultValue="post",required=true) String method,
			@RequestParam(value="sign",defaultValue="",required=true) String sign
			){
		
		ResultModel result = new ResultModel();
		Map<String,Object> content = new HashMap<String,Object>();
		result.setContent(content);
		StringBuffer requestURL = request.getRequestURL();
        String path = requestURL.substring(requestURL.lastIndexOf("/")+1);
		try{
			long timeStampV = Long.valueOf(timeStamp);
			long now = System.currentTimeMillis();
			long spec = now-timeStampV-10*60*1000;
			if(spec>0){
				result.setCode(ApiConstants.REQUEST_TIMED_OUT);
				result.setMsg("请求超时");
				LOGGER.error("请求超时,timeStamp="+timeStamp);
				return result;
			}
			if(StringUtils.isEmpty(sign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			if(StringUtils.isEmpty(bizCode)){
				result.setCode(ApiConstants.BIZCODE_ERROR);
				result.setMsg("bizCode错误");
				LOGGER.error("bizCode错误,bizCode="+bizCode);
				return result;
			}
			if(StringUtils.isEmpty(ssoToken)){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			Map<String,String> bodys = new HashMap<String, String>();
			bodys.put("timeStamp", timeStamp);
			bodys.put("nonce", nonce);
			bodys.put("ssoToken", ssoToken);
			bodys.put("bizCode", bizCode);
			bodys.put("method", method);
			
			//根据bizCode 取出 bizSecret
			String key = "bizConfig"+bizCode;
			BizConfigDTO bizConfig = bizConfigCache.get(key);
			if(bizConfig==null){
				BizConfigDTO model = new BizConfigDTO();
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("bizCode", bizCode);
				List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(model, p);
				if(bizConfigList==null || bizConfigList.size()==0){
					result.setCode(ApiConstants.BIZCODE_ERROR);
					result.setMsg("bizCode错误");
					LOGGER.info("bizCode错误");
					return result;
				}
				bizConfig = bizConfigList.get(0);
				bizConfigCache.put(key, bizConfig);
			}
			String bizSecret = bizConfig.getBizSecret();
			String vfSign = SignUtil.sign(bizSecret, method, path, bodys);
			
			if(!sign.equals(vfSign)){
				result.setCode(ApiConstants.SIGNATURE_ERROR);
				result.setMsg("签名错误");
				LOGGER.error("签名错误,sign="+sign);
				return result;
			}
			
			//解密token
			ssoToken = AESUtil.decode(ssoToken, bizSecret);
			if(ssoToken==null){
				result.setCode(ApiConstants.SSOTOKEN_ERROR);
				result.setMsg("ssoToken 参数错误");
				LOGGER.error("ssoToken 参数错误,ssoToken="+ssoToken);
				return result;
			}
			
			if(redisUtil.exists(ssoToken)){
				redisUtil.remove(ssoToken);
				result.setCode(ApiConstants.SUCCESS);
				LOGGER.info("登出成功");
			}else{
				result.setCode(ApiConstants.SSOTOKEN_OVERDUE);
				result.setMsg("token已失效");
				LOGGER.error("ssoToken已失效,ssoToken="+ssoToken);
			}
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("登出报错。"+e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value="/refreshBizConfig")
	@ResponseBody
	public ResultModel refreshBizConfig(){
		ResultModel result = new ResultModel();
		Map<String,Object> content = new HashMap<String,Object>();
		result.setContent(content);
		try{
			this.bizConfigCache.clear();
			result.setCode(ApiConstants.SUCCESS);
			LOGGER.info("刷新成功");
		}catch(Exception e){
			result.setCode(ApiConstants.OTHER_ERROR);
			result.setMsg(e.getMessage());
			LOGGER.error("请求报错。"+e.getMessage());
		}
		return result;
	}
	
	
	private BizConfigDTO getBizConfig(String bizCode){
        //根据bizCode 取出 bizSecret
        String key = "bizConfig"+bizCode;
        BizConfigDTO bizConfig = bizConfigCache.get(key);
        if(bizConfig==null){
            BizConfigDTO model = new BizConfigDTO();
            Map<String, Object> p = new HashMap<String, Object>();
            p.put("bizCode", bizCode);
            List<BizConfigDTO> bizConfigList = null;
            try {
                bizConfigList = bizConfigManager.findByBiz(model, p);
            } catch (ManagerException e) {
                e.printStackTrace();
            }
            if(bizConfigList==null || bizConfigList.size()==0){
                return null;
            }
            bizConfig = bizConfigList.get(0);
            bizConfigCache.put(key, bizConfig);
        }
        return bizConfig;
    }
	
}