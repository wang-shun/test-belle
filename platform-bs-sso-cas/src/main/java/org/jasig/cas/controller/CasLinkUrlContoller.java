package org.jasig.cas.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.common.constant.CasLinkUrlProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


public class CasLinkUrlContoller extends AbstractController{
	
	@NotNull
	private CasLinkUrlProperties casLinkUrlProperties;
	
	public CasLinkUrlProperties getCasLinkUrlProperties() {
		return casLinkUrlProperties;
	}

	public void setCasLinkUrlProperties(CasLinkUrlProperties casLinkUrlProperties) {
		this.casLinkUrlProperties = casLinkUrlProperties;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ModelAndView mav = new ModelAndView();
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		Map<String, Object> result = new HashMap<String, Object>();

		String forgetPwdUrl = casLinkUrlProperties.getForgetPwdUrl();

		String orgLoginUrl = casLinkUrlProperties.getOrgLoginUrl();

		if (!StringUtils.isEmpty(orgLoginUrl) && !StringUtils.isEmpty(forgetPwdUrl)) {
			result.put("forgetPwdUrl", forgetPwdUrl);
			result.put("orgLoginUrl", orgLoginUrl);
			result.put("code", 1);
			view.setAttributesMap(result);
			mav.setView(view);
		} else {
			result.put("code", 0);
			result.put("msg", "请查看配置文件cas-link.properties的url地址是否正确！");
			view.setAttributesMap(result);
			mav.setView(view);
		}

		return mav;
	}

}
