package com.wonhigh.bs.sso.server.manager.cas;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.server.common.constants.CasClientProperties;

@Service
public class CasCilentConfigManagerImpl implements CasClientConfigManager {

	@Resource
	private CasClientProperties casClientProperties;

	@Override
	public String getCasServerUrlPrefix() {
		return casClientProperties.getCasServerUrlPrefix();
	}

	@Override
	public String getClientServerName() {
		return casClientProperties.getClientServerName();
	}

	@Override
	public String getSource() {
		return casClientProperties.getSource();
	}

	@Override
	public String getContent() {
		return casClientProperties.getContent();
	}

}
