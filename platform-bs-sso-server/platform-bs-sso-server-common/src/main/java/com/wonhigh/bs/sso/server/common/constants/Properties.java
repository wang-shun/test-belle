package com.wonhigh.bs.sso.server.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Properties {
	
	@Value( "${ssouser.basedn}" )
	private String ssoUserBaseDn = "";
	
	@Value( "${bizconfig.basedn}" )
	private String bizConfigBaseDn = "";
	
	@Value( "${ssomanager.basedn}" )
	private String ssoManagerBaseDn = "";
	
	@Value( "${ssouser.basedn.ou}" )
	private String ssoUserBaseDnOu = "";
	
	@Value( "${bizconfig.basedn.ou}" )
	private String bizConfigBaseDnOu = "";
	
	@Value( "${ssomanager.basedn.ou}" )
	private String ssoManagerBaseDnOu = "";
	
	@Value( "${deletedata.basedn}" )
	private String deleteDataBaseDn = "";
	
	@Value( "${deletedata.basedn.ou}" )
	private String deleteDataBaseDnOu = "";
	
	@Value( "${sample.ldap.base}")
	private String sampleLdapBase = "";

	public String getSampleLdapBase() {
		return sampleLdapBase;
	}

	public void setSampleLdapBase(String sampleLdapBase) {
		this.sampleLdapBase = sampleLdapBase;
	}

	public String getSsoUserBaseDn() {
		return ssoUserBaseDn;
	}

	private void setSsoUserBaseDn(String ssoUserBaseDn) {
		this.ssoUserBaseDn = ssoUserBaseDn;
	}

	public String getBizConfigBaseDn() {
		return bizConfigBaseDn;
	}

	private void setBizConfigBaseDn(String bizConfigBaseDn) {
		this.bizConfigBaseDn = bizConfigBaseDn;
	}

	public String getSsoManagerBaseDn() {
		return ssoManagerBaseDn;
	}

	private void setSsoManagerBaseDn(String ssoManagerBaseDn) {
		this.ssoManagerBaseDn = ssoManagerBaseDn;
	}

	public String getSsoUserBaseDnOu() {
		return ssoUserBaseDnOu;
	}

	public String getBizConfigBaseDnOu() {
		return bizConfigBaseDnOu;
	}

	public String getSsoManagerBaseDnOu() {
		return ssoManagerBaseDnOu;
	}

	private void setSsoUserBaseDnOu(String ssoUserBaseDnOu) {
		this.ssoUserBaseDnOu = ssoUserBaseDnOu;
	}

	private void setBizConfigBaseDnOu(String bizConfigBaseDnOu) {
		this.bizConfigBaseDnOu = bizConfigBaseDnOu;
	}

	private void setSsoManagerBaseDnOu(String ssoManagerBaseDnOu) {
		this.ssoManagerBaseDnOu = ssoManagerBaseDnOu;
	}

	public String getDeleteDataBaseDn() {
		return deleteDataBaseDn;
	}

	private void setDeleteDataBaseDn(String deleteDataBaseDn) {
		this.deleteDataBaseDn = deleteDataBaseDn;
	}

	public String getDeleteDataBaseDnOu() {
		return deleteDataBaseDnOu;
	}

	private void setDeleteDataBaseDnOu(String deleteDataBaseDnOu) {
		this.deleteDataBaseDnOu = deleteDataBaseDnOu;
	}

	
}
