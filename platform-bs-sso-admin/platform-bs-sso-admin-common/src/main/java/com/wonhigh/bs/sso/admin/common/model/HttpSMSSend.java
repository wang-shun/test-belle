package com.wonhigh.bs.sso.admin.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信实体类
 * 
 * @author zhang.rq
 * @date 2016-9-13 上午10:30:30
 * @version 0.2.0 
 * @copyright yougou.com 
 */
public class HttpSMSSend implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1771732430691434955L;

	/**
     * 业务系统代码
     */
    private String businessSystemCode;
    
    /**
     * 优先级 0-低 10-中 20-高 默认为：0
     */
    private Integer priority = 0;
    
    /**
     * 按规定的时间发送
     */
    private Date scheduleSendTime;
    
    /**
     * 发送人编号
     */
    private String senderNo;
    
    /**
     * 发送人姓名
     */
    private String senderName;
    
    /**
     * 接收人(多人，以英文逗号分隔开)
     */
    private String receivePhones;
    
    /**
     * 短信内容
     */
    private String content;
    
    /**
     * 是否需要替换内容中的某些信息 使用 ${propertyName} 形式备注 0-不替换 1-需替换 默认为：0
     */
    private Integer isInstead = 0;    

	public String getBusinessSystemCode() {
		return businessSystemCode;
	}

	public void setBusinessSystemCode(String businessSystemCode) {
		this.businessSystemCode = businessSystemCode;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Date getScheduleSendTime() {
		return scheduleSendTime;
	}

	public void setScheduleSendTime(Date scheduleSendTime) {
		this.scheduleSendTime = scheduleSendTime;
	}

	public String getSenderNo() {
		return senderNo;
	}

	public void setSenderNo(String senderNo) {
		this.senderNo = senderNo;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceivePhones() {
		return receivePhones;
	}

	public void setReceivePhones(String receivePhones) {
		this.receivePhones = receivePhones;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getIsInstead() {
		return isInstead;
	}

	public void setIsInstead(Integer isInstead) {
		this.isInstead = isInstead;
	}
    
}
