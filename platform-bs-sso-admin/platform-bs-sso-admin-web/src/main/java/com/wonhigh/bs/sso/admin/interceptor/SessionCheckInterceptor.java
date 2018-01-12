package com.wonhigh.bs.sso.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * TODO: session检查是否登录
 * 
 * @author user
 * @date 2017年11月12日 下午2:28:03
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SessionCheckInterceptor extends HandlerInterceptorAdapter {
	protected static final XLogger LOGGER = XLoggerFactory.getXLogger(SessionCheckInterceptor.class);

	private String sessionKey;
	private String redirectUrl;
	private String indexUrl;
	private String[] excludeUrl;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String ojectName = request.getContextPath();
		String reqURI = request.getRequestURI();
		String matchReqURI = reqURI.replace(ojectName, "");
		if(pathMatch(matchReqURI)){
			return true;
		}
		
		String path = redirectUrl;
		LOGGER.info(ojectName);
		LOGGER.info(path);
		
		String serverName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String serverPath = serverName  + reqURI;
		String ajaxFlag = request.getHeader("x-requested-with");
		HttpSession session = request.getSession();
		Object tempObj = null;
		if (session != null) {
			tempObj = session.getAttribute(sessionKey);
		}
		if (tempObj == null && !redirectUrl.equals(matchReqURI) ) {
			//如果是ajax请求头会有，x-requested-with
			String xml = "XMLHttpRequest";
			if (xml.equalsIgnoreCase(ajaxFlag)) {
				//设置标志状态
				response.setHeader("sessionTimeOutFlag", "true");
			} else {
				response.sendRedirect(serverName+ojectName+path);
			}
			response.flushBuffer();
			return false;
		}else if (tempObj != null && redirectUrl.equals(reqURI) ){
			response.sendRedirect(indexUrl);
		}
		return true;
	}

	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
	
	private boolean pathMatch(String reqURI) {
		for (String exUrl : excludeUrl){
			if (PATH_MATCHER.match(exUrl, reqURI)){
				return true;
			}
		}
		return false;
	}

	
	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}


	/**
	 * @return sessionKey
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * @param sessionKey 
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * @return redirectUrl
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * @param redirectUrl 
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public void setExcludeUrl(String[] excludeUrl) {
		this.excludeUrl = excludeUrl;
	}
}
