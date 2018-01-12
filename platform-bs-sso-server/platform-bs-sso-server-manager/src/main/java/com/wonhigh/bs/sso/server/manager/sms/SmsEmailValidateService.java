package com.wonhigh.bs.sso.server.manager.sms;

import com.wonhigh.bs.sso.server.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.server.common.model.HttpSMSSend;

/**
 * @usage       邮件短信报警接口
 * @author      zhang.rq
 * @version     0.2.0
 * @datetime    2016/9/13 17:52
 * @copyright   wonhigh.cn
 */
public interface SmsEmailValidateService {
	
    /**
     * @usage 获取短信配置信息
     * @return
     */	
    public HttpSMSSend getSmsSend();
    
    /**
     * @usage 获取邮件配置信息
     * @return
     */
    public HttpEmailSend getEmailSend();

    /**
     * @usage 发送短信报警信息
     * @param smsSend
     *          短信信息
     * @return
     */
    public boolean sendSms(HttpSMSSend smsSend);

    /**
     * @usage 发送邮件信息
     * @param emailSend
     *          邮件信息
     * @return
     */
    public boolean sendEmail(HttpEmailSend emailSend);

}
