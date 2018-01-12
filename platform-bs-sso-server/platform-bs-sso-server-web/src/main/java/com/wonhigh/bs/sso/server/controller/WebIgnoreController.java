package com.wonhigh.bs.sso.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.util.MyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.server.common.constants.ApiConstants;
import com.wonhigh.bs.sso.server.common.constants.CustomConstants;
import com.wonhigh.bs.sso.server.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.server.common.model.HttpSMSSend;
import com.wonhigh.bs.sso.server.common.util.AESUtil;
import com.wonhigh.bs.sso.server.common.util.CommonUtil;
import com.wonhigh.bs.sso.server.common.util.PasswordUtil;
import com.wonhigh.bs.sso.server.common.util.RandomStringUtil;
import com.wonhigh.bs.sso.server.common.util.RedisUtil;
import com.wonhigh.bs.sso.server.common.util.SignUtil;
import com.wonhigh.bs.sso.server.common.util.VerificationUtils;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.ResultModel;
import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.server.manager.BizConfigManager;
import com.wonhigh.bs.sso.server.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.server.manager.cas.CasClientConfigManager;
import com.wonhigh.bs.sso.server.manager.epp.EPPNoticeManager;
import com.wonhigh.bs.sso.server.manager.sms.SmsEmailValidateService;
import com.wonhigh.o2o.ms.common.utils.Base64Utils;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.common.exception.ManagerException;

@Controller
@RequestMapping("/ignore")
public class WebIgnoreController {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebIgnoreController.class);

	@Resource
	private EPPNoticeManager eppNoticeManager;
	
	@Resource
	private RedisUtil redisUtil;

	@Resource
	private BizConfigManager bizConfigManager;

	@Resource
	private SmsEmailValidateService smsEmailValidateService;

	@Resource
	private CasClientConfigManager casClientConfigManager;
	
	@Resource
	private SsoUniformUserManager ssoUniformUserManager;
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {

		//清除本地已定义的session
		HttpSession session = request.getSession();
		session.invalidate();

		// 清楚cas服务器session，TGT信息，并返回登录sso-server登录状态
		String casServerUrlPrefix = casClientConfigManager.getCasServerUrlPrefix();
		LOGGER.info("logout method's casServerUrlPrefix is :" + casServerUrlPrefix);

		String clientServerName = casClientConfigManager.getClientServerName();
		LOGGER.info("logout method's clientServerName is :" + clientServerName);

		String casServerLogoutUrl = casServerUrlPrefix + "/logout";
		LOGGER.info("logout method's casServerLogoutUrl is :" + casServerLogoutUrl);

		StringBuilder loginUrl = new StringBuilder();
		loginUrl.append(clientServerName);
		loginUrl.append(request.getContextPath());
		loginUrl.append("/login");
		loginUrl.append("?random=" + Math.random());

		LOGGER.info("logout method's loginUrl is :" + loginUrl);

		return "redirect:" + casServerLogoutUrl + "?service=" + loginUrl;
	}

	/**
	 * sso用户重置密码页面
	 * 
	 * @return
	 */
	@RequestMapping("/pwdindex_rest")
	public String restPassword(HttpServletRequest request) {
		return "/user/pwd_rest";
	}

	/**
	 * sso用户重置密码通讯方式
	 * 
	 * @return
	 */
	@RequestMapping(value = "/checkmethod_rest", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryRestPWDNoticeMethod(String userName, String verificationType,
			HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>(2);
		LOGGER.info("重置密码, 查询通讯方式, userName: " + userName + ", verificationType:" + verificationType);

		try {
			// 校验用户名
			if (!paraLegalCheck_UserName(userName, verificationType, result)) {
				return result;
			}

			// 通过用户名获取sso信息
			SsoUniformUserDTO ssoUser = obtainSsoUserByLoginName(userName);
			if (ssoUser == null) {
				LOGGER.info("不存在的用户名, userName:" + userName);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "不存在的用户名！", result);
				return result;
			}

			// 
			String encryptionCommuMehod = encryptionCommuMethod(userName, verificationType, ssoUser, result);
			if (StringUtils.isEmpty(encryptionCommuMehod)) {
				return result;
			}

			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "查询用户通讯方式成功！", result);
			result.put(CustomConstants.SMS_EMAIL_CODE.PHONE_NUMBER, encryptionCommuMehod);
			result.put(CustomConstants.SMS_EMAIL_CODE.EMAIL_ADDRESS, encryptionCommuMehod);
			return result;
		} catch (Exception e) {
			LOGGER.error("查询用户联系方式异常！" + e.getMessage(), e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "查询用户联系方式异常！", result);
		}
		return result;
	}

	/**
	 * 发送验证码
	 * 
	 * @param userName
	 * @param verificationType
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/verifcode_rest", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> sendVerificationCode(String userName, String verificationType,
			String communicationMethod, HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>(2);

		LOGGER.info("--start--发送邮件短信验证码 userName: " + userName + "verificationType:" + verificationType
				+ "communicationMethod:" + communicationMethod);

		try {
			// 校验入参的合法性
			if (!paraLegalCheck_RestCode(userName, verificationType, communicationMethod, result)) {
				return result;
			}

			// 通过用户名获取sso信息
			SsoUniformUserDTO ssoUser = obtainSsoUserByLoginName(userName);

			// 校验手机邮箱合法性
			if (!communicationLegalCheck(userName, verificationType, ssoUser, result)) {
				return result;
			}

			String randomCode = RandomStringUtil.getRandomCode(6, 0);
			// 邮件短信通知内容
			String noticeContent = casClientConfigManager.getContent();
			noticeContent = noticeContent.replace(CustomConstants.SMS_EMAIL_CODE.NOTICE_ACCOUNT, userName);
			noticeContent = noticeContent.replace(CustomConstants.SMS_EMAIL_CODE.NOTICE_CODE, randomCode);
			LOGGER.info("邮件短信提示内容：" + "userName：" + userName + ", notice:" + noticeContent);

			// 发送成功，将验证码存在Redis中用于校验
			switch (verificationType) {
			case CustomConstants.SMS_EMAIL_CODE.PHONE:
				// 手机
				return sendSmsVerifiCode(ssoUser.getMobile(), noticeContent, randomCode);
			case CustomConstants.SMS_EMAIL_CODE.EMAIL:
				// 邮件
				return sendEmailVerfiCode(ssoUser.getEmail(), noticeContent, randomCode);
			default:
				// 默认手机号
				return sendSmsVerifiCode(ssoUser.getMobile(), noticeContent, randomCode);
			}

		} catch (Exception e) {
			LOGGER.error("获取验证码异常！" + e.getMessage(), e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取验证码异常！", result);
			return result;
		}
	}

	/**
	 * 重置sso账户密码
	 * 
	 * @param userName
	 * @param verificationType
	 * @param verfiCode
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "/pwd_rest", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> restPassword(final String userName, String verificationType, String verfiCode,
			String newPassword) {
		Map<String, Object> result = new HashMap<String, Object>(2);
		try {
			LOGGER.info("--start--重置一账通密码！ 用户名 :" + userName + ", 验证码:" + verfiCode + ", 新密码:" + newPassword);

			// 根据用户名查询用户信息
			SsoUniformUserDTO ssoUser = obtainSsoUserByLoginName(userName);

			// 校验参数入参合法性
			if (!paraLegalCheck_RestPwd(userName, verfiCode, result, ssoUser)) {
				return result;
			}

			// 校验验证码正确性
			if (!checkVerfiCode(ssoUser, verificationType, verfiCode, result)) {
				return result;
			}

			// 校验通过，再修改密码信息
			if (StringUtils.isNotEmpty(newPassword)) {
				ssoUser.setPassword(PasswordUtil.createPassword(newPassword));
				ssoUniformUserManager.updateToMysqlAndLdap(ssoUser);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "重置一账通密码成功!", result);

				// 通知EPP
				try {
					eppNoticeManager.asyncSendNoticToEpp(userName, null, null, ApiConstants.CHANGE_PSWD_NOTICE);
				} catch (Exception e) {
					LOGGER.error("调用通知EPP接口异常！" + e.getMessage() + e);
				}
			}

			LOGGER.info("--end--重置一账通密码！ 用户名 :" + userName + ", 验证码:" + verfiCode + ", 新密码:" + newPassword);
			return result;
		} catch (Exception e) {
			LOGGER.error("重置一账通密码异常! " + userName + ":" + verificationType + ":" + verfiCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "重置一账通密码异常!", result);
			return result;
		}
	}

	/**
	 * 强制修改原始密码
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/pwd_org")
	public String forceChangeOrgPwdIndex(HttpServletRequest request) {

		String editPwdUserName = (String) request
				.getParameter(Base64Utils.encrytor(MyConstants.NEED_FORCE_EDIT_PWD_USERNAME));
		request.getSession().setAttribute(MyConstants.NEED_FORCE_EDIT_PWD_USERNAME,
				Base64Utils.decryptor(editPwdUserName));

		String uidUserName = (String) request.getParameter(Base64Utils.encrytor(MyConstants.UID_USERNAME));
		request.getSession().setAttribute(MyConstants.UID_USERNAME, Base64Utils.decryptor(uidUserName));
		return "/user/pwd_org";
	}

	/**
	 * 用户使用原始密码登录，强制必须修改密码才能登录
	 * 
	 * @param userName
	 * @param newPassword
	 * @param request
	 * @return
	 */
	@RequestMapping("/orgpwd_rest")
	@ResponseBody
	public Map<String, Object> forceChangeOrgPwd(String userName, String newPassword, HttpServletRequest request,
			String oldPassword) {
		Map<String, Object> result = new HashMap<String, Object>(2);

		try {
			LOGGER.info("--start--强行修改密码，用户名:" + userName + ", 原密码:" + oldPassword + ", 新密码:" + newPassword);
			// 校验入参合法性
			if (!paraLegalCheck_ForceChangeOrgPwd(userName, newPassword, oldPassword, result)) {
				return result;
			}
			// 首先校验原始用户
			SsoUniformUserDTO ssoUserInfo = ssoUniformUserManager.findByLoginName(userName);

			//校验用户信息合法性
			if (!checkUserInfo_ForceChangeOrgPwd(userName, oldPassword, ssoUserInfo, result)) {
				return result;
			}
			LOGGER.info("修改前账户状态state:, 用户名:" + userName + ", state:" + ssoUserInfo.getState());

			// 只有在原始密码状态下，才进行强制密码修改
			if (ssoUserInfo.getState() == CustomConstants.ACCOUNT_STATUS.ORIGINAL_STATE
					&& StringUtils.isNotEmpty(newPassword)) {
				// 对新密码进行MD5加密
				ssoUserInfo.setState(CustomConstants.ACCOUNT_STATUS.ACTIVATED_STATE);
				ssoUserInfo.setPassword(PasswordUtil.createPassword(newPassword));
				ssoUniformUserManager.updateToMysqlAndLdap(ssoUserInfo);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "强制修改一账通密码成功!", result);

				// 通知EPP
				try {
					eppNoticeManager.asyncSendNoticToEpp(userName, null, null, ApiConstants.CHANGE_PSWD_NOTICE);
				} catch (Exception e) {
					LOGGER.error("调用通知EPP接口异常！" + e.getMessage(), e);
				}
				LOGGER.info("修改后账户状态state:, 用户名:" + userName + ", 账号状态state:" + ssoUserInfo.getState());
			} else {
				LOGGER.info("强制修改一账通密码失败, 用户名:" + userName + ", state:" + ssoUserInfo.getState());
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "账户不是初始状态!", result);
			}
			LOGGER.info("--end--强行修改密码, 用户名:" + userName + ", 原密码:" + oldPassword + ", 新密码:" + newPassword);
			return result;
		} catch (Exception e) {
			LOGGER.error("强制修改一账通密码异常!" + e.getMessage(), e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "强制修改一账通密码异常！", result);
			return result;
		}
	}

	/**
	 * 为了绑定和解绑实时生效, 提供获取业务系统绑定账号接口
	 * 
	 * @param ssoLoginName
	 * @param bizCode
	 * @param method
	 * @param sign
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getBizLoginName", method = RequestMethod.POST)
	@ResponseBody
	public ResultModel getBizLoginName(String ssoLoginName, String bizCode, String method, String sign,
			HttpServletRequest request) {

		LOGGER.info(request.getRemoteAddr() + "获取业务系统登录名,参数：, ssoLoginName=" + ssoLoginName + ",bizCode=" + bizCode
				+ ", method=" + method + ",sign=" + sign);

		ResultModel result = new ResultModel();
		Map<String, Object> content = new HashMap<String, Object>();

		try {
			// 获取bizConfig
			BizConfigDTO bizConfig = obtainBizConfigByBizCode(bizCode, result);
			if (bizConfig == null) {
				return result;
			}

			String bizSecret = bizConfig.getBizSecret();

			Map<String, String> bodys = new HashMap<String, String>();
			bodys.put("bizCode", bizCode);
			bodys.put("method", method);
			String vfSign = SignUtil.sign(bizSecret, method, "getBizLoginName", bodys);
			if (!sign.equals(vfSign)) {
				result.setCode(0);
				result.setMsg("签名错误!");
				LOGGER.info("签名错误! ,sign=" + sign + ", vfSign" + vfSign);
				return result;
			}

			SsoUniformUserDTO ssoUser = ssoUniformUserManager.findByLoginName(ssoLoginName);
			if (ssoUser == null) {
				result.setCode(0);
				result.setMsg("sso用户不存在！");
				LOGGER.info("sso用户不存在！ ssoLoginName=" + ssoLoginName);
				return result;
			}

			String bizUser = ssoUser.getBizUser();
			if (StringUtils.isEmpty(bizUser) || "{}".equals(bizUser)) {
				result.setCode(0);
				result.setMsg("sso账号未绑定业务系统！");
				result.setContent(null);
				LOGGER.info("sso账号未绑定业务系统！ ssoLoginName=" + ssoLoginName + ", bizUser=" + bizUser);
				return result;
			}
			JSONObject bizUserJO = JSONObject.parseObject(bizUser);
			if (bizUserJO.get(bizCode) != null) {
				bizUser = bizUserJO.getString(bizCode);
			}

			if (bizUser != null) {
				//加密业务系统的登录账号
				bizUser = AESUtil.encode(bizUser, bizSecret);
				result.setCode(ApiConstants.SUCCESS);
				content.put("loginName", bizUser);
				result.setContent(content);
				LOGGER.info("SSO账号：" + ssoUser.getLoginName() + "获取业务系统账号成功。bizUser=" + bizUser);
				return result;
			} else {
				result.setCode(ApiConstants.NOT_FOUND_BIZ_USER);
				result.setMsg("SSO账号：" + ssoUser.getLoginName() + "未绑定业务系统账号");
				LOGGER.info("SSO账号：" + ssoUser.getLoginName() + "未绑定业务系统账号");
			}
			LOGGER.info(request.getRemoteAddr() + "获取登录名成功, 参数：, ssoLoginName=" + ssoLoginName + ",bizCode=" + bizCode
					+ ", method=" + method + ",sign=" + sign);
		} catch (Exception e) {
			LOGGER.error("获取业务系统账号异常！ ssoLoginName: " + ssoLoginName);
			result.setCode(ApiConstants.NOT_FOUND_BIZ_USER);
			result.setMsg("获取业务系统账号异常!");
		}

		return result;
	}

	/**
	 * 获取bizConfig
	 * 
	 * @param bizCode
	 * @param result
	 * @return
	 * @throws ManagerException
	 */
	private BizConfigDTO obtainBizConfigByBizCode(String bizCode, ResultModel result) throws ManagerException {
		//根据bizCode 取出 bizSecret
		String redisKey = CustomConstants.CACHE_KEY.BIZCONFIG + '_' + bizCode;
		BizConfigDTO bizConfig = (BizConfigDTO) redisUtil.get(redisKey);
		if (bizConfig != null) {
			LOGGER.info("从Redis查询bizConfig结果:" + bizConfig);
			return bizConfig;
		}
		BizConfigDTO bizconfigModel = new BizConfigDTO();
		Map<String, Object> parameter = new HashMap<String, Object>(1);
		parameter.put("bizCode", bizCode);
		List<BizConfigDTO> bizConfigList = bizConfigManager.findByBiz(bizconfigModel, parameter);
		if (CollectionUtils.isEmpty(bizConfigList)) {
			result.setCode(ApiConstants.BIZCODE_ERROR);
			result.setMsg("bizCode错误");
			LOGGER.info("bizCode错误");
			return null;
		}
		bizConfig = bizConfigList.get(0);
		if (bizConfig != null) {
			LOGGER.info("将数据库中查询bizConfig放入Redis:" + bizConfig.toString());
			redisUtil.set(redisKey, bizConfig);
			return bizConfig;
		}
		return bizConfig;
	}

	/**
	 * 校验用户信息合法性
	 * 
	 * @param userName
	 * @param oldPassword
	 * @param ssoUserInfo
	 * @param result
	 * @return
	 */
	private boolean checkUserInfo_ForceChangeOrgPwd(String userName, String oldPassword, SsoUniformUserDTO ssoUserInfo,
			Map<String, Object> result) {
		if (ssoUserInfo == null) {
			LOGGER.error("用户名不存在, 用户名:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "用户名不存在!", result);
			return false;
		}

		//验证密码
		oldPassword = PasswordUtil.createPassword(oldPassword);
		if (!StringUtils.equals(ssoUserInfo.getPassword(), oldPassword)) {
			LOGGER.info("原密码错误, 用户名:" + userName + ", 原密码错误:" + oldPassword);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "原始密码错误，请重新输入!", result);
			return false;
		}
		return true;
	}

	/**
	 * 初次登陆, 强行修改原始密码
	 * 
	 * @param userName
	 * @param newPassword
	 * @param oldPassword
	 * @return
	 */
	private boolean paraLegalCheck_ForceChangeOrgPwd(String userName, String newPassword, String oldPassword,
			Map<String, Object> result) {

		if (StringUtils.isEmpty(userName)) {
			LOGGER.info("获取用户名为空，请检查session是否失效, userName:" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取用户名为空, 请检查session是否失效！", result);
			return false;
		}

		if (StringUtils.equals(oldPassword, newPassword)) {
			LOGGER.info("原密码和新密码不能一致！ 用户名:" + userName + ", 新密码：" + newPassword + ", 原密码:" + oldPassword);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码和原始密码不能一致, 请重新输入！", result);
			return false;
		}

		if (StringUtils.isEmpty(oldPassword)) {
			LOGGER.info("原始密码为空，请输入！ 用户名:" + userName + ", 原密码:" + oldPassword);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "原始密码为空, 请输入！", result);
			return false;
		}

		if (StringUtils.isEmpty(newPassword)) {
			LOGGER.info("新密码为空，请输入！ 用户名:" + userName + ", 新密码：" + newPassword);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码为空，请输入！", result);
			return false;
		}

		if (!CommonUtil.match(CustomConstants.REGEX.REGEX_PASSWORD, newPassword)) {
			LOGGER.info("新密码不符合规范，请重新输入！  用户名:" + userName + ", 新密码：" + newPassword);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "新密码6-16位，必须包含字母和数字，请重新输入！", result);
			return false;
		}
		return true;
	}

	/**
	 * 校验参数合法性
	 * 
	 * @param userName
	 * @param verificationType
	 * @param communicationMethod
	 * @param result
	 * @return
	 */
	private boolean paraLegalCheck_RestCode(String userName, String verificationType, String communicationMethod,
			Map<String, Object> result) {

		if (!paraLegalCheck_UserName(userName, verificationType, result)) {
			return false;
		}

		if (StringUtils.isEmpty(communicationMethod)) {
			LOGGER.info("手机号或者邮箱不存在, userName: " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "手机号或者邮箱不存在！", result);
			return false;
		}
		return true;
	}

	/**
	 * 校验用户名和通讯方式
	 * 
	 * @param userName
	 * @param verificationType
	 * @param result
	 * @return
	 */
	private boolean paraLegalCheck_UserName(String userName, String verificationType, Map<String, Object> result) {

		if (StringUtils.isEmpty(userName)) {
			LOGGER.info("用户名为空, 请输入用户名, userName: " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "用户名为空, 请输入用户名！", result);
			return false;
		}

		if (StringUtils.isEmpty(verificationType)) {
			LOGGER.info("请选择密码重置方式，手机号 or 邮箱, userName: " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "请选择密码重置方式，手机号 or 邮箱！", result);
			return false;
		}
		return true;
	}
	
	/**
	 * 发送邮件验证码
	 * 
	 * @param ssoUser
	 * @param noticeContent
	 * @param randomCode
	 */
	private Map<String, Object> sendEmailVerfiCode(String emailAddress, String noticeContent, String randomCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(emailAddress)) {
				LOGGER.info("邮箱地址为空！ emailAddress：" + emailAddress);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "邮箱地址为空！", result);
				return result;
			}

			if (StringUtils.isEmpty(noticeContent)) {
				LOGGER.info("验证码信息为空！notice: " + noticeContent);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "验证码信息为空！", result);
				return result;
			}

			HttpEmailSend emailSend = smsEmailValidateService.getEmailSend();
			emailSend.setMainAddr(emailAddress);
			emailSend.setSubject("重置一账通密码");
			emailSend.setContent(noticeContent);
			boolean sendEmail_Status = smsEmailValidateService.sendEmail(emailSend);
			LOGGER.info("sendEmail_Status: " + String.valueOf(sendEmail_Status) + noticeContent + randomCode);

			// 发送成功后储存于redis，用于下次校验验证码
			if (sendEmail_Status) {
				// 验证码有效时间15min
				redisUtil.set(CustomConstants.SMS_EMAIL_CODE.EMAIL_VERIF_CODE + emailAddress, randomCode, 60 * 15L);
				LOGGER.info("email_VerifCode: " + noticeContent + randomCode);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "获取验证码成功！", result);
				return result;
			}
			LOGGER.info("获取验证码失败！ emailAddress： " + emailAddress + ", sendStatus:" + sendEmail_Status, result);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取验证码失败！", result);
			return result;
		} catch (Exception e) {
			LOGGER.error("发送邮件验证码异常！" + e.getMessage(), e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取验证码异常！", result);
			return result;
		}
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param verificationMethod
	 * @param noticeContent
	 * @param randomCode
	 */
	private Map<String, Object> sendSmsVerifiCode(String phoneNumber, String noticeContent, String randomCode) {

		Map<String, Object> result = new HashMap<String, Object>(2);
		try {
			if (StringUtils.isEmpty(phoneNumber)) {
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "手机号码为空！", result);
				return result;
			}

			if (StringUtils.isEmpty(noticeContent)) {
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "验证码信息为空！", result);
				return result;
			}

			HttpSMSSend smsSend = smsEmailValidateService.getSmsSend();
			smsSend.setReceivePhones(phoneNumber);
			smsSend.setSenderName("重置一账通密码");

			// 发送内容，随机码，时间限制
			smsSend.setContent(noticeContent);

			boolean sendSms_Status = smsEmailValidateService.sendSms(smsSend);
			LOGGER.info("sendSms_Status: " + String.valueOf(sendSms_Status) + noticeContent);
			
			// 发送成功后储存于redis，用于下次校验验证码
			if (sendSms_Status) {
				// 验证码有效时间15min
				redisUtil.set(CustomConstants.SMS_EMAIL_CODE.SMS_VERIF_CODE + phoneNumber, randomCode, 60 * 15L);
				LOGGER.info("sms_VerifCode: " + noticeContent);
				CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.SUCCESS, "获取验证码成功！", result);
				return result;
			}
			LOGGER.info("获取验证码失败！，phoneNumber： " + phoneNumber + ", sendStatus:" + sendSms_Status);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "获取验证码失败！", result);
			return result;
		} catch (Exception e) {
			LOGGER.error("发送短信验证码异常！" + e.getMessage() + e, e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "发送短信验证码异常！", result);
			return result;
		}
	}
	
	/**
	 * 校验手机邮箱合法性
	 * 
	 * @param userName
	 * @param verificationType
	 * @param ssoUser
	 * @return
	 */
	private boolean communicationLegalCheck(String userName, String verificationType, SsoUniformUserDTO ssoUser,
			Map<String, Object> result) throws DaoException {

		if (ssoUser == null) {
			LOGGER.info("对不起，用户名不存在，请重新输入, userName: " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "用户名不存在，请重新输入！", result);
			return false;
		}

		if (CustomConstants.SMS_EMAIL_CODE.PHONE.equals(verificationType)
				&& ("0".equals(ssoUser.getMobile()) || StringUtils.isEmpty(ssoUser.getMobile()))) {
			LOGGER.info("手机号为空, userName：" + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "手机号未绑定！", result);
			return false;
		}

		if (CustomConstants.SMS_EMAIL_CODE.EMAIL.equals(verificationType)
				&& ("NULL".equalsIgnoreCase(ssoUser.getEmail()) || StringUtils.isEmpty(ssoUser.getEmail()))) {
			LOGGER.info("邮箱地址为空, userName： " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "邮箱地址未绑定！", result);
			return false;
		}
		return true;
	}

	/**
	 * 通过用户名获取sso信息
	 * 
	 * @param userName
	 * @return
	 * @throws DaoException
	 */
	private SsoUniformUserDTO obtainSsoUserByLoginName(String userName) throws DaoException {
		
		SsoUniformUserDTO ssoUser = null;
		
		// 这则表达式判断是否是手机号，如果满足手机号则手机号获取用户信息
		if (CommonUtil.match(CustomConstants.REGEX.REGEX_PHONE, userName)) {
			ssoUser = ssoUniformUserManager.findByMobile(userName);
			LOGGER.info("发送验证码用户名 Match REGEX_PHONE, userName：" + userName);
		} else if (ssoUser == null) {
			ssoUser = ssoUniformUserManager.findByLoginName(userName);
			LOGGER.info("发送验证码用户名Not Match REGEX_PHONE, userName：" + userName);
		}
		return ssoUser;
	}

	/**
	 * 加密通讯方式,  手机号 ： ******1234, 邮箱：*****@wonhigh.cn
	 * 
	 * @param userName
	 * @param verificationType
	 * @param ssoUser
	 * @param result
	 * @return
	 */
	private String encryptionCommuMethod(String userName, String verificationType, SsoUniformUserDTO ssoUser,
			Map<String, Object> result) {
		String encryptionCommuMethod = null;
		try {

			if (CustomConstants.SMS_EMAIL_CODE.PHONE.equals(verificationType)) {
				String phoneNumber = ssoUser.getMobile();
				LOGGER.info("手机号码, userName:" + userName + ", phoneNumber:" + phoneNumber);
				// LDAP中mobile为空是0
				if ("0".equals(phoneNumber) || StringUtils.isEmpty(phoneNumber)) {
					LOGGER.info("手机号码未绑定, userName: " + userName + "phoneNumber:" + phoneNumber);
					CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "手机号码未绑定！", result);
					return null;
				}

				if (!StringUtils.isEmpty(phoneNumber)) {
					// 校验后台手机号正确性
					if (!CommonUtil.match(CustomConstants.REGEX.REGEX_PHONE, phoneNumber)) {
						LOGGER.info("手机号码不符合规范: " + "userName:" + userName + ":" + phoneNumber);
						CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "手机号码不符合规范！", result);
						return null;
					}
					encryptionCommuMethod = VerificationUtils.noticeEncryption(phoneNumber,
							CustomConstants.SMS_EMAIL_CODE.PHONE);
				}
			}

			if (CustomConstants.SMS_EMAIL_CODE.EMAIL.equals(verificationType)) {
				String emailAddress = ssoUser.getEmail();
				LOGGER.info("邮箱地址, userName: " + userName + ", emailAddress" + emailAddress);
				if ("NULL".equalsIgnoreCase(emailAddress) || StringUtils.isEmpty(emailAddress)) {
					LOGGER.info("邮箱地址未绑定, userName: " + userName + ", emailAddress" + emailAddress);
					CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "邮箱地址未绑定！", result);
					return null;
				}

				if (!StringUtils.isEmpty(emailAddress)) {
					LOGGER.info("email:" + emailAddress);
					// 校验后台邮箱地址正确性
					if (!CommonUtil.match(CustomConstants.REGEX.REGEX_EMAIL, emailAddress)) {
						LOGGER.info("邮箱地址不符合规范: " + "userName:" + userName + ", emailAddress:" + emailAddress);
						CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "邮箱地址不符合规范！", result);
						return null;
					}
					encryptionCommuMethod = VerificationUtils.noticeEncryption(emailAddress,
							CustomConstants.SMS_EMAIL_CODE.EMAIL);
				}
			}

		} catch (Exception e) {
			LOGGER.error("加密通讯方式异常！" + e.getMessage() + e);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "加密通讯方式异常！", result);
			return null;
		}

		return encryptionCommuMethod;
	}
	
	/**
	 * 校验入参合法性
	 * 
	 * @param userName
	 * @param verfiCode
	 * @param result
	 * @param ssoUser
	 * @return
	 */
	private boolean paraLegalCheck_RestPwd(final String userName, String verfiCode, Map<String, Object> result,
			SsoUniformUserDTO ssoUser) {

		if (ssoUser == null) {
			LOGGER.info("用户不存在，请检查用户信息！用户名userName： " + userName);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "不存在的用户名, 请重新输入！", result);
			return false;
		}

		if (StringUtils.isEmpty(verfiCode)) {
			LOGGER.info("验证码不存在！ 用户名userName：" + userName + ", 验证码:" + verfiCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "验证码为空，请重新输入！", result);
			return false;
		}
		return true;
	}

	
	/**
	 * 校验验证码正确性
	 * 
	 * @param userName
	 * @param verificationType
	 * @param verfiCode
	 * @param result
	 * @return
	 */
	private boolean checkVerfiCode(final SsoUniformUserDTO ssoUser, String verificationType, String verfiCode,
			Map<String, Object> result) {

		String redisVerfiCode = null;
		if (CustomConstants.SMS_EMAIL_CODE.EMAIL.equals(verificationType)) {
			// 获取redis中的邮箱验证码
			redisVerfiCode = redisUtil.get(CustomConstants.SMS_EMAIL_CODE.EMAIL_VERIF_CODE + ssoUser.getEmail()) == null
					? null
					: (String) redisUtil.get(CustomConstants.SMS_EMAIL_CODE.EMAIL_VERIF_CODE + ssoUser.getEmail());
		} else if (redisVerfiCode == null) {
			// 默认是手机验证方式
			redisVerfiCode = redisUtil.get(CustomConstants.SMS_EMAIL_CODE.SMS_VERIF_CODE + ssoUser.getMobile()) == null
					? null
					: (String) redisUtil.get(CustomConstants.SMS_EMAIL_CODE.SMS_VERIF_CODE + ssoUser.getMobile());
		}

		if (StringUtils.isEmpty(redisVerfiCode)) {
			LOGGER.info("验证码不存在或者已经失效, 请重新获取！用户名： " + ssoUser.getLoginName() + ", 通讯方式:" + verificationType + ", 验证码:"
					+ verfiCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "验证码不存在或者已经失效, 请重新获取！", result);
			return false;
		}

		if (!redisVerfiCode.equals(verfiCode)) {
			LOGGER.info("验证码不正确！ 用户名：" + ssoUser.getLoginName() + ", 通讯方式:" + verificationType + ", 验证码:" + verfiCode);
			CommonUtil.setResultMessage(CustomConstants.RESULT_CODE.ERROR, "验证码不正确！", result);
			return false;
		}
		return true;
	}
}
