package com.wonhigh.bs.sso.server.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wonhigh.bs.sso.server.common.constants.CustomConstants;
import com.wonhigh.bs.sso.server.manager.cas.CasClientConfigManager;

@Controller
@RequestMapping("/")
public class WebIndexController {

	private final static Logger LOGGER = LoggerFactory.getLogger(WebIndexController.class);
	
	@Resource
	private CasClientConfigManager casClientConfigManager;

	@Resource
	private WebIgnoreController webIgnoreController;
	
	/**
	 * sso用户首页
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public String index(HttpServletRequest request) {
		return "redirect:index";
	}

	@RequestMapping("/index")
	public String login(HttpServletRequest request) {
		
		LOGGER.info("--sso-server请求登录login---start---");
		
		// 获取登录用户信息
		AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();

		Map<String, Object> attributes = principal.getAttributes();

		if (attributes == null) {
			LOGGER.info("用户属性信息为null, userName: " + principal.getName());
			return "redirect:error";
		}

		if (attributes.get(CustomConstants.ACCOUNT_STATUS.SN_SURENAME) == null) {
			LOGGER.info("用户真实姓名为null, userName: " + principal.getName());
			return "redirect:error";
		}

		if (attributes.get(CustomConstants.ACCOUNT_STATUS.UID_USERNAME) == null) {
			LOGGER.info("用户名为null, userName: " + principal.getName());
			return "redirect:error";
		}

		String loginName_UID = (String) attributes.get(CustomConstants.ACCOUNT_STATUS.UID_USERNAME);

		String sureName_CHS = (String) attributes.get(CustomConstants.ACCOUNT_STATUS.SN_SURENAME);

		// 真实姓名
		request.getSession().setAttribute("sureName_CHS", sureName_CHS);

		// uid：后台所有的接口都是支持uid，需要把登录名转成uid
		request.getSession().setAttribute("loginName_UID", loginName_UID);

		// 登录名：cas登录名用户名
		request.getSession().setAttribute("userName_CAS", principal.getName());

		LOGGER.info("sso-server登录, 真实姓名sureName_CHS:" + sureName_CHS + ", LDAP_UID--loginName_UID:" + loginName_UID
				+ ", 登录名userName_CAS:" + principal.getName());

		LOGGER.info("--sso-server请求登录login---end---");

		return "index";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		//清除本地已定义的session
		return webIgnoreController.logout(request);
	}

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(HttpServletRequest request) {
		// 自定义错误页面
		return "error";
	}

}
