package org.jasig.cas.common.constant;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CasLinkUrlProperties implements Serializable{

	private static final long serialVersionUID = 4395284072890344138L;

	@Value("${cas.forgetPwd.url}")
	private String forgetPwdUrl;

	@Value("${cas.orgLogin.url}")
	private String orgLoginUrl;

	public String getForgetPwdUrl() {
		return forgetPwdUrl;
	}

	public void setForgetPwdUrl(String forgetPwdUrl) {
		this.forgetPwdUrl = forgetPwdUrl;
	}

	public String getOrgLoginUrl() {
		return orgLoginUrl;
	}

	public void setOrgLoginUrl(String orgLoginUrl) {
		this.orgLoginUrl = orgLoginUrl;
	}
		
}
