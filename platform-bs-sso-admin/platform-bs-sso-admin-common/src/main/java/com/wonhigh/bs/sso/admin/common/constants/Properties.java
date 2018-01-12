package com.wonhigh.bs.sso.admin.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * 加载配置变量信息
 * 
 * @author user
 * @date 2017年11月12日 下午4:10:49
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
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
	
	@Value( "${ssoadminrole.basedn}" )
	private String ssoAdminRoleBaseDn = "";
	
	@Value( "${ssoadminrole.basedn.ou}" )
	private String ssoAdminRoleBaseDnOu = "";
	
	@Value( "${EPP_SOURCE_PARAMETER}" )
	private String eppSourceParameter = "";
	
	@Value("${sms.business.system.code}")
    private String smsBusinessSystemCode = "22";
	
	@Value("${sms.create.new.account.msg}")
    private String smsCreateNewAccountMsg;
	
	@Value("${sms.update.password.msg}")
    private String smsUpdatePasswordMsg;
	
	@Value("${syncHR.cron}")
    private String syncHRCron;
	
	@Value("${sendMsg.phone.from.hr.mq.switch}")
	private String sendMsgPhoneFromHrMqSwitch = "on";
	
	@Value("${sendMsg.phone.from.hr.sync.switch}")
	private String sendMsgPhoneFromHrSyncSwitch = "on";
	
	/**
	 * 导出时能同时并发同步执行的请求数
	 */
	@Value("${concurrent.semaphore.number}")
	private Integer concurrentSemaphoreNumber = 5;
	
	/**
	 * 导出数量超过这个数时，对请求进行串行同步执行
	 */
	@Value("${concurrent.export.max.size.per.request}")
    private Integer concurrentExportMaxSizePerRequest = 10000;
	
	public Integer getConcurrentSemaphoreNumber() {
        return concurrentSemaphoreNumber;
    }

    public void setConcurrentSemaphoreNumber(Integer concurrentSemaphoreNumber) {
        this.concurrentSemaphoreNumber = concurrentSemaphoreNumber;
    }

    public Integer getConcurrentExportMaxSizePerRequest() {
        return concurrentExportMaxSizePerRequest;
    }

    public void setConcurrentExportMaxSizePerRequest(Integer concurrentExportMaxSizePerRequest) {
        this.concurrentExportMaxSizePerRequest = concurrentExportMaxSizePerRequest;
    }

    public String getSendMsgPhoneFromHrMqSwitch() {
		return sendMsgPhoneFromHrMqSwitch;
	}

	public void setSendMsgPhoneFromHrMqSwitch(String sendMsgPhoneFromHrMqSwitch) {
		this.sendMsgPhoneFromHrMqSwitch = sendMsgPhoneFromHrMqSwitch;
	}

	public String getSendMsgPhoneFromHrSyncSwitch() {
		return sendMsgPhoneFromHrSyncSwitch;
	}

	public void setSendMsgPhoneFromHrSyncSwitch(String sendMsgPhoneFromHrSyncSwitch) {
		this.sendMsgPhoneFromHrSyncSwitch = sendMsgPhoneFromHrSyncSwitch;
	}

	public String getSyncHRCron() {
		return syncHRCron;
	}

	public void setSyncHRCron(String syncHRCron) {
		this.syncHRCron = syncHRCron;
	}

	public String getSmsCreateNewAccountMsg() {
		return smsCreateNewAccountMsg;
	}

	public void setSmsCreateNewAccountMsg(String smsCreateNewAccountMsg) {
		this.smsCreateNewAccountMsg = smsCreateNewAccountMsg;
	}

	public String getSmsUpdatePasswordMsg() {
		return smsUpdatePasswordMsg;
	}

	public void setSmsUpdatePasswordMsg(String smsUpdatePasswordMsg) {
		this.smsUpdatePasswordMsg = smsUpdatePasswordMsg;
	}

	public String getSmsBusinessSystemCode() {
		return smsBusinessSystemCode;
	}

	public void setSmsBusinessSystemCode(String smsBusinessSystemCode) {
		this.smsBusinessSystemCode = smsBusinessSystemCode;
	}

	public String getEppSourceParameter() {
		return eppSourceParameter;
	}

	public void setEppSourceParameter(String eppSourceParameter) {
		this.eppSourceParameter = eppSourceParameter;
	}

	public String getSsoAdminRoleBaseDn() {
		return ssoAdminRoleBaseDn;
	}

	public void setSsoAdminRoleBaseDn(String ssoAdminRoleBaseDn) {
		this.ssoAdminRoleBaseDn = ssoAdminRoleBaseDn;
	}

	public String getSsoAdminRoleBaseDnOu() {
		return ssoAdminRoleBaseDnOu;
	}

	public void setSsoAdminRoleBaseDnOu(String ssoAdminRoleBaseDnOu) {
		this.ssoAdminRoleBaseDnOu = ssoAdminRoleBaseDnOu;
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
