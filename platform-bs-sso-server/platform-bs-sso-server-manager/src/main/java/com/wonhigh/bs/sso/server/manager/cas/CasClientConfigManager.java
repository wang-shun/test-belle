package com.wonhigh.bs.sso.server.manager.cas;

public interface CasClientConfigManager {
	
	String getCasServerUrlPrefix();
	
	String getClientServerName();
	
	String getSource();
	
	String getContent();
}
