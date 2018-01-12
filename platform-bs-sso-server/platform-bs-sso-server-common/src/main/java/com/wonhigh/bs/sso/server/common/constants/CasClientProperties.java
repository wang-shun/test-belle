package com.wonhigh.bs.sso.server.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CasClientProperties {
	
	@Value("${cas.server.loginUrl}")
	private String casServerLoginUrl;
	
	@Value("${cas.server.urlPrefix}")
	private String casServerUrlPrefix;
	
	@Value("${cas.client.serverName}")
	private String clientServerName;

	@Value("${EPP_SOURCE_PARAMETER}")
	private String source;
	
	@Value("${sendSmsMsg.content}")
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String getCasServerLoginUrl() {
		return casServerLoginUrl;
	}

	public void setCasServerLoginUrl(String casServerLoginUrl) {
		this.casServerLoginUrl = casServerLoginUrl;
	}

	public String getCasServerUrlPrefix() {
		return casServerUrlPrefix;
	}

	public void setCasServerUrlPrefix(String casServerUrlPrefix) {
		this.casServerUrlPrefix = casServerUrlPrefix;
	}

	public String getClientServerName() {
		return clientServerName;
	}

	public void setClientServerName(String clientServerName) {
		this.clientServerName = clientServerName;
	}
	

}
