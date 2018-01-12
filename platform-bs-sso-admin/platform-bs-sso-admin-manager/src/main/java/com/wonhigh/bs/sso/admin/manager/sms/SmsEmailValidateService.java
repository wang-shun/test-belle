package com.wonhigh.bs.sso.admin.manager.sms;

import com.wonhigh.bs.sso.admin.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;


/**
 * @usage       邮件短信报警接口
 * @author      zhang.rq
 * @version     0.2.0
 * @datetime    2016/9/13 17:52
 * @copyright   wonhigh.cn
 */
public interface SmsEmailValidateService {
	
    /**
     * 获取短信配置信息
     * @return
     */
    public HttpSMSSend getSmsSend();
    
    /**
     * 获取邮件配置信息
     * @return
     */
    public HttpEmailSend getEmailSend();

    /**
     * 发送短信报警信息
     * @param smsSend 短信信息
     * @return
     */
    public boolean sendSms(HttpSMSSend smsSend);

    /**
     * 发送邮件信息
     * @param emailSend 邮件信息
     * @return
     */
    public boolean sendEmail(HttpEmailSend emailSend);

}
