package com.wonhigh.bs.sso.admin.manager.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;

/**
 * @author zhang.rq
 * @version 0.2.0
 * @datetime 2016/3/11 18:03
 * @copyright wonhigh.cn
 */
@Service("smsEmailAlarmSerivceImpl")
public class SmsEmailValidateServiceImpl implements SmsEmailValidateService{

	/**
	 * 短信属性
	 */
    @Value("${sendSmsMsg.bussinessSysCode}")
    private String bussinessSysCode;

    @Value("${sendSmsMsg.priority}")
    private int smsProiorty;

    @Value("${sendSmsMsg.sendName}")
    private String smsSendName;

    @Value("${sendSmsMsg.sendNo}")
    private String smsSendNo;
    
    /**
     * 这个是接受验证码手机号，输入获取的
     */
    private String smsReceivePhone;

    @Value("${sendSmsMsg.emailMsgType}")
    private int emailMsgType;

    @Value("${sendMailMsg.priority}")
    private int emailPriority;

    @Value("${sendSmsMsg.emailSubject}")
    private String emailSubject;

	/**
	 * 邮件属性
	 */
    @Value("${mail.username}")
    private String emailSendAddr;

    @Value("${mail.password}")
    private String sendEmailPasswd;

    /**
     * 接受Email，输入获取的
     */
    private String reveiveEmailAddr;
    
    @Value("${sendSmsMsg.reveiveEmailAddrCC}")
    private String reveiveEmailAddrCC;    
    
    /**
     * 发送邮件端口是否关闭
     */
    @Value("${sendSmsMsg.email.switch}")
    private String sendEmailSwitch;
    
    /**
     * 发送短信端口是否关闭
     */
    @Value("${sendSmsMsg.sms.switch}")
    private String sendSmsSwitch;    
    
    
    
	/**
	 * 短信服务器地址
	 */
    @Value("${sms.send.url}")
	public String smsUrl;
    
	/**
	 * 邮件服务地址
	 */
    @Value("${mail.send.url}")
	public String mailUrl;
	
	

    private Logger logger = LoggerFactory.getLogger(SmsEmailValidateServiceImpl.class);

    @Override
    public HttpSMSSend getSmsSend() {
        HttpSMSSend smsSend = new HttpSMSSend();
        smsSend.setBusinessSystemCode(bussinessSysCode);
        smsSend.setPriority(smsProiorty);
        /*发送SMS用户名*/
        smsSend.setSenderName(smsSendName);
        smsSend.setSenderNo(smsSendNo);
        smsSend.setReceivePhones(smsReceivePhone);
        
        return smsSend;
    }

    @Override
    public HttpEmailSend getEmailSend() {
    	HttpEmailSend emailSend = new HttpEmailSend();
        emailSend.setPriority(emailPriority);
        emailSend.setEmailMsgType(emailMsgType);
        emailSend.setBusinessSystemCode(bussinessSysCode);
        emailSend.setSenderAddr(emailSendAddr);
        emailSend.setEmailPwd(sendEmailPasswd);
        emailSend.setSubject(emailSubject);
        emailSend.setMainAddr(reveiveEmailAddr);
        emailSend.setCcAddr(reveiveEmailAddrCC);
        
        return emailSend;
    }

    /**
     * @usage 是否发送邮件报警
     * @return
     *      true:表示发送,false:表示不发送
     */
    public boolean isSendEmailAram(){
        if(ApiConstants.SEND_EMAIL_SWITCH_ON.equalsIgnoreCase(this.sendEmailSwitch)){
            return true;
        }else if(ApiConstants.SEND_EMAIL_SWITCH_OFF.equalsIgnoreCase(this.sendEmailSwitch)){
            return false;
        }
        return false;
    }
    
    
    /**
     * @usage 是否发送短信报警
     * @return
     *      true:表示发送,false:表示不发送
     */
    public boolean isSendSmsAram(){
        if(ApiConstants.SEND_EMAIL_SWITCH_ON.equalsIgnoreCase(this.sendSmsSwitch)){
            return true;
        }else if(ApiConstants.SEND_EMAIL_SWITCH_ON.equalsIgnoreCase(this.sendSmsSwitch)){
            return false;
        }
        return false;
    }
    
    @Override
    public boolean sendSms(HttpSMSSend smsSend) {
    	if(isSendSmsAram()){
	        if (SendUtils.checkSMSSend(smsSend)) {
	        	Boolean bool=MailSMSHttpUtils.sendPost(smsUrl, smsSend);
	            return bool;
	        }
    	}else{
    		logger.info("[ SMS ] 短信关闭 ");
    	}
        logger.error(" [ SMS Error ] 短信消息体错误，请检查sms-email-config.properties参数");
        return false;
    }

    @Override
    public boolean sendEmail(HttpEmailSend emailSend){
    	if(isSendEmailAram()){
	        if (SendUtils.checkEmailSend(emailSend)) {
	        	Boolean bool=MailSMSHttpUtils.sendPost(mailUrl, emailSend);
	            return bool;
	        }
    	}else{
    		logger.info("[ Email ] 邮件关闭 ");
    	}
        
        logger.error(" [ Email Error ] 邮件消息体错误，请检查sms-email-config.properties参数");
        return false;
    }
    
    

}
