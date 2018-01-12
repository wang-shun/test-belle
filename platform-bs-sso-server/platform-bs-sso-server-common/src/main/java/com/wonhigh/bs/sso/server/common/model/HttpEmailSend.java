package com.wonhigh.bs.sso.server.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * mail实体
 * 
 * @author zhang.rq
 * @date 2016-9-13 上午10:26:10
 * @version 0.2.0
 * @copyright yougou.com 
 */
public class HttpEmailSend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3430742841284900621L;

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
	* 发送人名称
	*/
	private String senderName;	
	
	/**
	 * 邮件类型 1-系统运行日志 2-错误日志 3-监控日志 4-通告信息；第二位1为文本，2为HTML
	 */
	private Integer emailMsgType = 11;
	
	/**
     * 是否需要替换内容中的某些信息 使用 ${propertyName} 形式备注 0-不替换 1-需替换 默认为：0
     */
	private Integer isInstead = 0;
	
	/**
	* 邮件主题
	*/
	private String subject;	
	
	/**
	* 邮件内容
	*/
	private String content;	
	
	/**
	 * 邮件主送接收列
	 */
	private String mainAddr;
	
	/**
	 * 邮件抄送列表
	 */
	private String ccAddr;
	
	/**
	 * 邮件发送者地址(发送方邮件)
	 */
	private String senderAddr;
	
	/**
	 * 邮件发送者邮箱对应的密码
	 */
	private String emailPwd;

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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getEmailMsgType() {
		return emailMsgType;
	}

	public void setEmailMsgType(Integer emailMsgType) {
		this.emailMsgType = emailMsgType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMainAddr() {
		return mainAddr;
	}

	public void setMainAddr(String mainAddr) {
		this.mainAddr = mainAddr;
	}

	public String getCcAddr() {
		return ccAddr;
	}

	public void setCcAddr(String ccAddr) {
		this.ccAddr = ccAddr;
	}

	public String getSenderAddr() {
		return senderAddr;
	}

	public void setSenderAddr(String senderAddr) {
		this.senderAddr = senderAddr;
	}

	public String getEmailPwd() {
		return emailPwd;
	}

	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
	}

	public Integer getIsInstead() {
		return isInstead;
	}

	public void setIsInstead(Integer isInstead) {
		this.isInstead = isInstead;
	}
	
}
