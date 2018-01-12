package com.wonhigh.bs.sso.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.server.common.constants.ApiConstants;
import com.wonhigh.bs.sso.server.common.constants.CustomConstants;
import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.util.AESUtil;
import com.wonhigh.bs.sso.server.common.util.CommonUtil;
import com.wonhigh.bs.sso.server.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.server.common.util.PasswordUtil;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.server.manager.BizConfigManager;
import com.wonhigh.bs.sso.server.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.server.manager.SsoUserManager;
import com.wonhigh.bs.sso.server.manager.cas.CasClientConfigManager;
import com.wonhigh.bs.sso.server.manager.epp.EPPNoticeManager;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.common.exception.ServiceException;

/**
 * @author 石涛
 * @date  2017-09-04 11:36:04
 * @version 1.0.0
 * @copyright (C) 2013 WonHigh Information Technology Co.,Ltd 
 * All Rights Reserved. 
 * 
 * The software for the WonHigh technology development, without the 
 * company's written consent, and any other individuals and 
 * organizations shall not be used, Copying, Modify or distribute 
 * the software.
 * 
 */
@Controller
@RequestMapping("/ssoUser")
public class SsoUserController {

	private final static Logger LOGGER = LoggerFactory.getLogger(SsoUserController.class);

	@Resource
	private CasClientConfigManager casClientConfigManager;

	@Resource
	private SsoUserManager ssoUserManager;

	@Resource
	private BizConfigManager bizConfigManager;

	@Resource
	private SsoUniformUserManager ssoUniformUserManager;

	@Resource
	private EPPNoticeManager eppNoticeManager;

	/**
	 * 修改密码接口
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/changepwd", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changePassword(final String userName, String oldPassword, String newPassword,
			HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>(2);
		LOGGER.info("--start--修改密码，用户名userName：" + userName + ", 原密码:" + oldPassword + ", 新密码:" + newPassword);
		
		try {
			// 入参合法性校验
			if (!paraLegalCheck(userName, oldPassword, newPassword, result)) {
				return result;
			}

			// 首先校验原始用户密码
			SsoUniformUserDTO ssoUserInfo = checkUserInformation(userName, oldPassword, result);
			if (ssoUserInfo == null) {
				return result;
			}

			// 校验成功后修改密码
			if (ssoUserInfo != null && StringUtils.isNotEmpty(newPassword)) {
				// 原始密码校验通过，再修改密码信息, 对修改的密码进行MD5加密
				ssoUserInfo.setPassword(PasswordUtil.createPassword(newPassword));
				ssoUniformUserManager.updateToMysqlAndLdap(ssoUserInfo);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "修改密码成功！", result);
				
				try {
					// 通知EPP
					eppNoticeManager.asyncSendNoticToEpp(userName, null, null, ApiConstants.CHANGE_PSWD_NOTICE);
				} catch (Exception e) {
					LOGGER.error("调用通知EPP接口异常" + e.getMessage());
				}
				LOGGER.info("--end--修改密码，用户名：" + userName + "，原密码:" + oldPassword + "，新密码:" + newPassword);
			}
			return result;
		} catch (Exception e) {
			LOGGER.error("修改密码失败，请检查用户名密码正确性：" + userName + e.getMessage());
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "修改密码异常，请检查用户名密码正确性！", result);
		}

		return result;
	}

	/**
	 * 查询业务系统信息
	 * @return
	 */
	@RequestMapping(value = "/queryBizInfo", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> queryBizInfo() {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		LOGGER.info("--start--查询bizConfig配置信息");

		Map<String, Object> bizMap = null;

		try {
			List<BizConfigDTO> BizConfigList = bizConfigManager.findAll();
			String bizName = null;
			String bizCode = null;
			String loginUrl = null;

			for (BizConfigDTO bizConfig : BizConfigList) {
				bizMap = new HashMap<String, Object>();
				bizName = bizConfig.getBizName();
				bizCode = bizConfig.getBizCode();
				loginUrl = bizConfig.getLoginUrl();
				bizMap.put("bizName", bizName);
				bizMap.put("bizCode", bizCode);
				bizMap.put("loginUrl", loginUrl);
				result.add(bizMap);
			}

			LOGGER.info("--end--查询bizConfig配置信息成功，result:" + result.toString());
		} catch (Exception e) {
			LOGGER.error("查询biz系统信息失败：" + e);
		}
		return result;
	}

	/**
	 * 查询业务系统登录跳转loginUrl
	 * @return
	 */
	@RequestMapping(value = "/queryLoginUrl", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryBizLoginUrl() {
		Map<String, Object> result = new HashMap<String, Object>();

		LOGGER.info("--start--查询bizLoginUrl配置信息");
		
		try {
			List<BizConfigDTO> BizConfigList = bizConfigManager.findAll();
			String bizCode = null;
			String loginUrl = null;

			for (BizConfigDTO bizConfig : BizConfigList) {
				bizCode = bizConfig.getBizCode();
				loginUrl = bizConfig.getLoginUrl();
				result.put(bizCode, loginUrl);
			}
			LOGGER.info("--end--查询bizLoginUrl配置信息成功，result:"+result.toString());
		} catch (Exception e) {
			LOGGER.error("查询biz系统登录url失败：" + e.getMessage() + e);
		}
		return result;
	}
	
	/**
	 * 用于判断业务系统是否绑定账户
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bindStatus", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryBizBindMap(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			// 校验用户session
			String userName = checkUserSession(request, result);
			if (StringUtils.isEmpty(userName)) {
				return result;
			}

			LOGGER.info("--start--查询sso账户绑定状态信息, userName:" + userName);

			// 从数据库中查询用户数据，校验用户是否存在，是否被锁定
			SsoUniformUserDTO ssoUser = checkUserStatus(userName, result);
			if (ssoUser == null) {
				return result;
			}

			String bizUser = ssoUser.getBizUser();

			if ("{}".equals(bizUser)) {
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "未绑定业务系统账号！", result);
				LOGGER.info("未绑定业务系统账号！, userName:" + userName);
				return result;
			}

			if (StringUtils.isNotEmpty(bizUser)) {
				JSONObject bizUJ = JSONObject.parseObject(bizUser);
				for (Entry<String, Object> entry : bizUJ.entrySet()) {
					String bizcode = entry.getKey();
					BizConfigDTO bizConfig = bizConfigManager.findByPrimaryKey(bizcode);
					if (bizConfig != null) {
						// bizCode业务系统唯一code
						result.put(bizcode, entry.getValue());
					}
				}
			}

			LOGGER.info("--end--查询sso账户绑定状态信息, userName:" + userName + ", result:" + result.toString());
		} catch (Exception e) {
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "查询绑定biz信息状态失败！", result);
			LOGGER.error("--查询绑定biz信息状态失败--：" + e.getMessage() + e);
		}

		return result;
	}

	/**
	 * 查询用户绑定信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/query_bindinfo", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> queryBindInfo(HttpServletRequest request) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> bindInfo = new HashMap<String, Object>();

		try {
			// 通过session:loginName_UID获取用户名
			String userName = checkUserSession(request, bindInfo);
			if (StringUtils.isEmpty(userName)) {
				return result;
			}
			LOGGER.info("--start--查询sso账户绑定信息, userName:" + userName);

			// 校验用户是否存在，是否锁定
			SsoUniformUserDTO ssoUser = checkUserStatus(userName, bindInfo);
			if (ssoUser == null) {
				return result;
			}

			String bizUser = ssoUser.getBizUser();

			if ("{}".equals(bizUser)) {
				LOGGER.info("未绑定业务系统账号！, SesseionKey:loginName_UID, userName:" + userName);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "未绑定业务系统账号！", bindInfo);
				return result;
			}

			if (StringUtils.isNotEmpty(bizUser)) {
				JSONObject bizUJ = JSONObject.parseObject(bizUser);
				for (Entry<String, Object> entry : bizUJ.entrySet()) {
					String bizcode = entry.getKey();
					BizConfigDTO bizConfig = bizConfigManager.findByPrimaryKey(bizcode);
					if (bizConfig != null) {
						JSONObject obj = new JSONObject();
						// 一账通账号
						obj.put("userName", userName);
						// 真实姓名
						obj.put("sureName", ssoUser.getSureName());
						// bizCode业务系统唯一code
						obj.put("bizCode", bizConfig.getBizCode());
						// 业务系统名称
						obj.put("bizName", bizConfig.getBizName());
						// 业务系统绑定账号
						String ln = (String) entry.getValue();
						obj.put("bizUserName", ln);
						result.add(obj);
					}
				}

				LOGGER.info("--end--查询sso账户绑定信息成功, userName:" + userName + ", result:" + result.toString());
			}
		} catch (Exception e) {
			LOGGER.error("查询绑定biz信息异常：" + e.getMessage());
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "查询绑定信息异常!", bindInfo);
		}

		return result;
	}

	/**
	 * 绑定业务系统账号
	 * 
	 * @param bizCode
	 * @param bizUserName
	 * @param bizPassword
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bind_bizuser", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> bindBizUser(String bizCode, final String bizUserName, String bizPassword,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>(3);

		try {
			// 从session中获取sso账户信息
			final String userName = checkUserSession(request, result);
			if (StringUtils.isEmpty(userName)) {
				return result;
			}
			LOGGER.info("绑定业务系统入参, userName:" + userName + ", bizCode:" + bizCode + ", bizUserName:" + bizUserName
					+ ", bizPassword" + bizPassword);

			//获取用户信息，判断用户是否存在，账号是否锁定
			SsoUniformUserDTO ssoUser = checkUserStatus(userName, result);
			if (ssoUser == null) {
				return result;
			}

			// 校验sso账户是否绑定bizCode对应业务账号
			JSONObject bizUJ = checkAccountBindStatus(ssoUser, bizCode, result);
			if (bizUJ == null) {
				return result;
			}

			// 查询bizConfig并做比较校验
			BizConfigDTO bizConfig = checkBizConfig(bizCode, bizUserName, result);
			if (bizConfig == null) {
				return result;
			}

			//调用业务系统验证用户名密码接口验证用户是否存在
			if (!checkBizSystemAccount(bizCode, bizUserName, bizPassword, result, bizConfig)) {
				return result;
			}

			// 修改绑定信息
			bizUJ.put(bizCode, bizUserName);
			ssoUser.setBizUser(bizUJ.toJSONString());
			LOGGER.info("绑定后, userName: " + ssoUser.getLoginName() + "bizUser：" + bizUJ.toString());
			
			// 更新数据数据
			ssoUser.setPassword(null);
			ssoUniformUserManager.updateToMysqlAndLdap(ssoUser);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "绑定成功", result);
			result.put("sureName", ssoUser.getSureName());

			LOGGER.info("绑定业务系统成功, userName:" + userName + ", bizCode:" + bizCode + ", bizUserName:" + bizUserName
					+ ", bizPassword" + bizPassword);

			// 异步通知EPP修改信息
			try {
				eppNoticeManager.asyncSendNoticToEpp(userName, bizConfig.getBizCode(), bizUserName,
						ApiConstants.BIND_LOGINNAME_NOTICE);
			} catch (Exception e) {
				LOGGER.error("调用通知EPP接口失败" + e.getMessage());
			}
		} catch (Exception e) {
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "绑定账号异常!", result);
			LOGGER.error("绑定账号异常!" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 解绑账号
	 * 
	 * @param bizCodes
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/unbind_bizuser", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unBindBizUser(String bizCodes, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>(2);

		try {
			// 从session中获取用户名, 校验session是否失效
			String userName = checkUserSession(request, result);
			if (StringUtils.isEmpty(userName)) {
				return result;
			}
			LOGGER.info("--start--解绑biz用户账号!, SesseionKey:loginName_UID, userName:" + userName);

			// 校验用户状态，用户名是否存在，是否被锁定
			SsoUniformUserDTO ssoUser = checkUserStatus(userName, result);
			if (ssoUser == null) {
				return result;
			}

			// 解绑业务系统账号，并做解绑前校验
			Map<String, String> bizCodeBizName = unBindBizAccount(bizCodes, userName, ssoUser, result);
			if (bizCodeBizName == null) {
				return result;
			}

			ssoUniformUserManager.updateToMysqlAndLdap(ssoUser);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "解绑成功！", result);
			LOGGER.info("解绑成功！" + "userName：" + userName + ", bizCodes:" + bizCodes);

			try {
				Set<String> keySet = bizCodeBizName.keySet();
				Iterator<String> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					String bizCode = iterator.next();
					eppNoticeManager.asyncSendNoticToEpp(userName, bizCode, bizCodeBizName.get(bizCode),
							ApiConstants.UNBIND_LOGINNAME_NOTICE);
				}
			} catch (Exception e) {
				LOGGER.error("调用通知EPP接口失败" + e.getMessage());
			}
			LOGGER.info("--end--解绑biz用户账号!" + "SesseionKey:loginName_UID, userName:" + userName);
			return result;
		} catch (Exception e) {
			LOGGER.error("解绑失败!" + bizCodes + e.getMessage(), e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "解绑异常！", result);
			return result;
		}
	}

	@RequestMapping(value = "/query_userinfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryUserInfo(String userName, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			SsoUser ssoUser = ssoUserManager.findByPrimaryKey(userName);
			if (ssoUser == null) {
				result.put("error", "查询结果为null");
				LOGGER.error("userName: " + userName + "查询用户信息为null");
				return result;
			}

			String employeeNumber = ssoUser.getEmployeeNumber();
			String idCard = ssoUser.getIdCard();
			int sex = ssoUser.getSex();
			String sureName = ssoUser.getSureName();
			String mobile = ssoUser.getMobile();
			String email = ssoUser.getEmail();
			result.put("employeeNumber", employeeNumber);
			result.put("idCard", idCard);
			result.put("mobile", mobile);
			result.put("sex", sex);
			result.put("sureName", sureName);
			result.put("email", email);

		} catch (Exception e) {
			result.put("error", e.getMessage() + e);
			LOGGER.error("userName: " + userName + e.getMessage() + e, e);
		}

		return result;
	}

	@RequestMapping(value = "/update_userinfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateUserInfo(String userName, String employeeNumber, String idCard, String sex,
			String sureName, String mobile, String email) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			SsoUser ssoUser = ssoUserManager.findByPrimaryKey(userName);
			ssoUser.setEmployeeNumber(employeeNumber);
			ssoUser.setIdCard(idCard);
			ssoUser.setSureName(sureName);

			int gender = "female".equals(sex) ? 0 : 1;
			ssoUser.setSex(gender);

			ssoUser.setMobile(mobile);
			ssoUser.setEmail(email);

			ssoUserManager.update(ssoUser);

		} catch (Exception e) {
			result.put(CustomConstants.RESULT_CODE.MSG, "更新用户信息失败");
			LOGGER.error("userName: " + userName + "更新用户信息失败" + e.getMessage(), e);
		}

		return result;
	}
	
	/**
	 * 修改密码校验用户密码信息
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param result
	 * @return
	 */
	private SsoUniformUserDTO checkUserInformation(final String userName, String oldPassword,
			Map<String, Object> result) {

		SsoUniformUserDTO ssoUserInfo = null;
		try {
			// 校验用户是否存在，是否锁定
			ssoUserInfo = checkUserStatus(userName, result);
			if (ssoUserInfo == null) {
				return null;
			}

			//验证密码
			oldPassword = PasswordUtil.createPassword(oldPassword);
			if (!StringUtils.equals(ssoUserInfo.getPassword(), oldPassword)) {
				LOGGER.info("原始密码错误:, userName:" + userName + "，原密码:" + oldPassword);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "原始密码错误，请重新输入！", result);
				return null;
			}
		} catch (Exception e) {
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取用户信息异常！", result);
			LOGGER.error("获取用户信息异常！ 用户名：" + userName);
			return null;
		}

		return ssoUserInfo;
	}

	/**
	 * 校验用户是否存在，是否锁定
	 * 
	 * @param userName
	 * @param result
	 * @return
	 * @throws DaoException
	 */
	private SsoUniformUserDTO checkUserStatus(final String userName, Map<String, Object> result) throws DaoException {

		// 首先校验原始用户密码
		SsoUniformUserDTO ssoUserInfo = ssoUniformUserManager.findByLoginName(userName);

		if (ssoUserInfo == null) {
			LOGGER.info("用户名不存在:,userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "用户名不存在!", result);
			return null;
		}

		if (ssoUserInfo.getState() == CustomConstants.ACCOUNT_STATUS.LOCKED_STATE) {
			LOGGER.info("账户被锁定:, userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "账户被锁定!", result);
			return null;
		}

		return ssoUserInfo;
	}

	/**
	 * 修改密码合法性校验
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @param result
	 */
	private boolean paraLegalCheck(final String userName, String oldPassword, String newPassword,
			Map<String, Object> result) {

		if (StringUtils.isEmpty(userName)) {
			LOGGER.error("用户名为空，请检查session是否失效，userName：" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "用户名为空，请检查session是否失效！", result);
			return false;
		}

		if (StringUtils.equals(oldPassword, newPassword)) {
			LOGGER.info("新密码和原密码不能一样，请重新输入！，userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码和原密码不能一样，请重新输入!", result);
			return false;
		}

		if (StringUtils.isEmpty(oldPassword)) {
			LOGGER.error("原密码不能为空，请输入！，userName：" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "原密码不能为空，请输入！", result);
			return false;
		}

		if (StringUtils.isEmpty(newPassword)) {
			LOGGER.error("新密码不能为空，请输入！，userName：" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码不能为空，请输入！", result);
			return false;
		}

		if (!CommonUtil.match(CustomConstants.REGEX.REGEX_PASSWORD, newPassword)) {
			LOGGER.info("新密码不符合规范，请重新输入！，userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码不符合规范，请重新输入！", result);
			return false;
		}
		return true;
	}
	
	/**
	 * 通过session:loginName_UID获取用户名
	 * 
	 * @param request
	 * @param result
	 * @return
	 */
	private String checkUserSession(HttpServletRequest request, Map<String, Object> result) {
		String userName = (String) request.getSession().getAttribute("loginName_UID");
		LOGGER.info("从seesion获取sso账户信息loginName_UID, userName:" + userName);

		if (StringUtils.isEmpty(userName)) {
			LOGGER.info("登录用户名Session失效!" + ", userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "登录用户名Session失效！", result);
			return userName;
		}
		return userName;
	}
	
	/**
	 * 校验账户是否绑定过该业务系统账号，每个业务系统限定绑定一个账号
	 * 
	 * @param ssoUser
	 * @return
	 */
	private JSONObject checkAccountBindStatus(SsoUniformUserDTO ssoUser, String bizCode, Map<String, Object> result) {

		String bizUser = ssoUser.getBizUser();
		LOGGER.info("绑定前, userName: " + ssoUser.getLoginName() + "bizUser：" + bizUser);
		JSONObject bizUJ = JSONObject.parseObject(bizUser);
		if (bizUJ != null && bizUJ.get(bizCode) != null) {
			LOGGER.info("已绑定该系统账户, 请先解绑再绑定, userName: " + ssoUser.getLoginName() + ", bizCode: " + bizCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "已绑定该系统账户，请先解绑再绑定", result);
			return null;
		}

		return bizUJ;
	}

	/**
	 * 查询bizConfig, 并做校验
	 * 
	 * @param bizCode
	 * @param bizUserName
	 * @param result
	 * @return
	 * @throws ServiceException
	 */
	private BizConfigDTO checkBizConfig(String bizCode, String bizUserName, Map<String, Object> result)
			throws ServiceException {

		final BizConfigDTO bizConfig = bizConfigManager.findByPrimaryKey(bizCode);

		if (bizConfig == null) {
			LOGGER.info("业务系统代码错误, 系统中不存在该业务系统, bizCode: " + bizCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "业务系统代码错误,系统中不存在该业务系统", result);
			return null;
		}

		//检查账号是否已经绑定过sso账户
		int total = ssoUniformUserManager.findByBizUser(bizConfig.getBizCode(), bizUserName);
		if (total > 0) {
			LOGGER.info("该业务账号已经绑定过其他sso账户,bizUserName:" + bizUserName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "业务系统账户已经绑定其它SSO账户", result);
			return null;
		}
		return bizConfig;
	}
	
	/**
	 * 校验业务系统账号密码
	 * 
	 * @param bizCode
	 * @param bizUserName
	 * @param bizPassword
	 * @param result
	 * @param bizConfig
	 */
	private boolean checkBizSystemAccount(String bizCode, final String bizUserName, String bizPassword,
			Map<String, Object> result, final BizConfigDTO bizConfig) {

		LOGGER.info(
				"绑定账号，校验业务系统账号密码, bizCode：" + bizCode + ", bizUserName：" + bizUserName + ", bizPassword" + bizPassword);

		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("loginName", bizUserName);
		String encryptPwd = AESUtil.encode(bizPassword, bizConfig.getBizSecret());
		paramsMap.put("password", encryptPwd);
		paramsMap.put("bizCode", bizConfig.getBizCode());

		if (StringUtils.isEmpty(bizConfig.getVerifyUserPwdUrl())) {
			LOGGER.info("校验用户密码url为空，请维护!, " + ", bizCode: " + bizCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "校验用户密码url为空，请维护！", result);
			return false;
		}

		Map<String, Object> map = InvokeApiUtil.checkBizUserPwd(bizConfig.getVerifyUserPwdUrl(), paramsMap,
				bizConfig.getBizSecret());
		int code = (int) map.get(CustomConstants.RESULT_CODE.CODE);
		if (code != CustomConstants.RESULT_CODE.SUCCESS) {
			LOGGER.error(map.get(CustomConstants.RESULT_CODE.MSG) == null ? "校验biz账户时出错!" + ", bizCode: " + bizCode
					: (String) map.get(CustomConstants.RESULT_CODE.MSG));
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR,
					map.get(CustomConstants.RESULT_CODE.MSG) == null ? "校验biz账户时出错"
							: (String) map.get(CustomConstants.RESULT_CODE.MSG),
					result);

			return false;
		}

		return true;
	}
	
	/**
	 * 解绑业务系统账号
	 * 
	 * @param bizCodes
	 * @param userName
	 * @param ssoUser
	 * @param result
	 * @return
	 */
	private Map<String, String> unBindBizAccount(String bizCodes, String userName, SsoUniformUserDTO ssoUser,
			Map<String, Object> result) {

		String[] bizCodeArr = null;
		if (!StringUtils.isEmpty(bizCodes)) {
			bizCodeArr = bizCodes.split(",");
		}

		if (bizCodeArr == null) {
			LOGGER.info("未选中解绑账号! bizCodeArr=null, bizCodes: " + bizCodes);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "未选中解绑账号, 请选择! ", result);
			return null;
		}

		String bizUser = ssoUser.getBizUser();
		if ("{}".equals(bizUser)) {
			LOGGER.info("未绑定业务系统账号！" + "SesseionKey:loginName_UID, userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "未绑定业务系统账号！", result);
			return null;
		}

		LOGGER.info("解绑前, userName:" + userName + ", bizUser: " + bizUser);
		Map<String, String> bizCodeBizName = new HashMap<String, String>(10);
		JSONObject bizUJ = JSONObject.parseObject(bizUser);
		for (String bizCode : bizCodeArr) {
			if (bizUJ != null && bizUJ.get(bizCode) != null) {
				bizCodeBizName.put(bizCode, (String) bizUJ.get(bizCode));
				bizUJ.remove(bizCode);
			}
		}
	
		if (bizUJ != null) {
			ssoUser.setBizUser(bizUJ.toJSONString());
			LOGGER.info("解绑后, userName:" + userName + ", bizUser: " + ssoUser.getBizUser());
			return bizCodeBizName;
		}
		return bizCodeBizName;
	}

}