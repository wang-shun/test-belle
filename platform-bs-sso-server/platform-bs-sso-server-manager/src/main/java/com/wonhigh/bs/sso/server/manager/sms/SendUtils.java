package com.wonhigh.bs.sso.server.manager.sms;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonhigh.bs.sso.server.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.server.common.model.HttpSMSSend;
import com.wonhigh.o2o.ms.common.constants.PublicConstants;
import com.wonhigh.o2o.ms.common.utils.CommonUtil;

/**
 * @usage       调度服务工具类
 * @author      tong.cx
 * @version     0.0.1
 * @datetime    2016/3/10 11:10
 * @copyright   wonhigh.cn
 */
public class SendUtils {

    public static Logger logger = LoggerFactory.getLogger(SendUtils.class);

    /**
     * 校验EmailSend实体的必填项是否设置正确
     * 1.业务代码非空
     * 2.发件人地址非空且遵守邮件格式
     * 3.邮件类型的值范围：0<X<20
     * 4.优先级的值范围：0<X<30
     * 5.是否替换字段非空且值只能为0、1
     * 6.主题内容非空
     * 7.接收人列表非空
     * 8.模板内容替换--替换的属性值不能为空
     * 9.
     * @param entity
     * @return
     */
    public static boolean checkEmailSend(HttpEmailSend entity) {
        if (entity == null || StringUtils.isBlank(entity.getBusinessSystemCode())) {
        	if(entity!=null){
        		logger.info(String.format("=============邮件设置存在问题-->业务代码【emailSubject：%s】", entity.getSubject()));
        	}
            return false;
        }

        if (StringUtils.isBlank(entity.getSenderAddr()) || !CommonUtil.checkEmailAddr(entity.getSenderAddr())) {
            logger.info(String.format("=============邮件设置存在问题-->发送人邮件地址【emailSenderAddr：%s】",
                    entity.getSenderAddr()));
            return false;
        }

        if (entity.getEmailMsgType() == null || entity.getEmailMsgType() < 0 ) {
            logger.info(String.format("=============邮件设置存在问题-->邮件类型【emailMsgType：%d】", entity.getEmailMsgType()));
            return false;
        }

        if (entity.getPriority() == null || entity.getPriority() < 0 || entity.getPriority() > 30) {
            logger.info(String.format("=============邮件设置存在问题-->邮件级别【priority：%d】", entity.getPriority()));
            return false;
        }

        if (entity.getIsInstead() == null || entity.getIsInstead() < 0 || entity.getIsInstead() > 1) {
            logger.info(String.format("=============邮件设置存在问题-->邮件是否替换【isInstead：%d】", entity.getIsInstead()));
            return false;
        }

        if (StringUtils.isBlank(entity.getSubject())) {
            logger.info(String.format("=============邮件设置存在问题-->邮件主题【emailSubject：%s】", entity.getSubject()));
            return false;
        }

        if (StringUtils.isBlank(entity.getMainAddr())) {
            logger.info(String.format("=============邮件设置存在问题-->邮件接收人【getMainAddr：%s】", entity.getMainAddr()));
            return false;
        }else{
        	boolean boolAddr=true;
        	for(String addr:entity.getMainAddr().split(",")){
        		boolAddr=boolAddr&&CommonUtil.checkEmailAddr(addr);
        	}
        	if(!boolAddr){
        		 logger.info(String.format("=============邮件设置格式不正确->邮件接收人【getMainAddr：%s】", entity.getMainAddr()));
                 return false;
        	}
        }
        
        //* 1.接收人地址非空
        //* 2.接收人地址只能是邮件形式
        //* 3.邮件接收类型值只能是：1、2
        
        if (entity.getEmailMsgType()<10||entity.getEmailMsgType()>99) {
            logger.info(String.format("=============邮件设置存在问题-->邮件接收类型【emailReceiveType：%d】",
                    entity.getEmailMsgType()));
            return false;
        }
        

        if (StringUtils.isBlank(entity.getContent())) {
            logger.info(String.format("=============邮件设置存在问题-->邮件内容【emailContent：%s】", entity.getContent()));
            return false;
        }

        //模板替换校验值
        if (entity.getIsInstead().intValue() == PublicConstants.MODEL_INSTEAD_YES) {
            try {
                String msg = new String(entity.getContent().getBytes(), PublicConstants.CHARSET_UTF8);
                //遍历内容中所有的替换属性
                for (Field field : entity.getClass().getDeclaredFields()) {
                    String str = PublicConstants.CHARACTER_STR_0 + field.getName() + PublicConstants.CHARACTER_STR_1;
                    //证明存在替换属性并且需替换的属性值为空
                    field.setAccessible(true);
                    if (msg.indexOf(str) >= 0
                            && (field.get(entity) == null || field.get(entity).toString().trim().equals(""))) {
                        logger.info(String.format("=============邮件设置存在问题-->存在替换属性并且需替换的属性值为空【%s：%s】", field.getName(),
                                field.get(entity).toString()));
                        return false;
                    }
                    msg = msg.replace(str, "");
                }

                //证明有存在非属性字段
                if (msg.indexOf(PublicConstants.CHARACTER_STR_0) > 0) {
                    logger.info(String.format("=============邮件地址设置存在问题-->存在非属性字段【%s】", msg));
                    return false;
                }
            } catch (Exception e) {
                logger.error("===============校验EmailSend实体的必填项是否设置正确", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 校验SMSSend实体的必填项是否设置正确
     * 1.业务代码非空
     * 2.发送人手机号非空且手机号长度范围：6~20
     * 3.接收人列表非空
     * 4.模板属性替换--检验替换的属性值是否为空且值只能为0、1
     * 5.优先级别的值范围：0<X<30
     * @param entity
     * @return
     */
    public static boolean checkSMSSend(HttpSMSSend entity) {
        if (StringUtils.isBlank(entity.getBusinessSystemCode())) {
            logger.info(String.format("=============短信设置存在问题-->业务代码非空【businessSystemCode：%s】",
                    entity.getBusinessSystemCode()));
            return false;
        }

        if (entity.getIsInstead() == null || entity.getIsInstead() < 0 || entity.getIsInstead() > 1) {
            logger.info(String.format("=============短信设置存在问题-->内容是否替换【isInstead：%d】", entity.getIsInstead()));
            return false;
        }

        if (StringUtils.isBlank(entity.getReceivePhones())) {
            logger.info(String.format("=============短信设置存在问题-->接收人列表非空【smsReceiverPhoneList.size：%s】",entity.getReceivePhones()));
            return false;
        }

        if (entity.getPriority() == null || entity.getPriority() < 0 || entity.getPriority() > 30) {
            logger.info(String.format("=============短信设置存在问题-->优先级别的值范围【priority：%d】", entity.getPriority()));
            return false;
        }
        
        //* 1.接收人手机号非空
        //* 2.接收人手机号长度仅在6-20位
        if (StringUtils.isBlank(entity.getReceivePhones())) {
            logger.info(String.format("=============短信设置存在问题-->接收人手机号【receivePhone：%s】", entity.getReceivePhones()));
            return false;
        }else{
        	boolean boolPhone=true;
        	for(String phone:entity.getReceivePhones().split(",")){
        		boolPhone=boolPhone&&CommonUtil.checkMobilePhone(phone);
        	}
        	if(!boolPhone){
        		 logger.info(String.format("=============短信号码设置格式不正确->接收人手机号【receivePhone：%s】", entity.getReceivePhones()));
                 return false;
        	}
        }

        //模板替换校验值
        if (entity.getIsInstead().intValue() == PublicConstants.MODEL_INSTEAD_YES) {
            try {
                String msg = entity.getContent();
                //遍历内容中所有的替换属性
                for (Field field : entity.getClass().getDeclaredFields()) {
                    String str = PublicConstants.CHARACTER_STR_0 + field.getName() + PublicConstants.CHARACTER_STR_1;
                    //证明存在替换属性并且需替换的属性值为空
                    field.setAccessible(true);
                    if (msg.indexOf(str) >= 0
                            && (field.get(entity) == null || field.get(entity).toString().trim().equals(""))) {
                        logger.info(String.format("=============短信设置存在问题-->存在替换属性并且需替换的属性值为空【%s：%s】",
                                field.getName(), field.get(entity).toString()));
                        return false;
                    }
                    msg = msg.replace(str, "");
                }

                //证明有存在非属性字段
                if (msg.indexOf(PublicConstants.CHARACTER_STR_0) > 0) {
                    logger.info(String.format("=============短信设置存在问题-->存在非属性字段【%s】", msg));
                    return false;
                }
            } catch (Exception e) {
                logger.error("===============校验SMSSend实体的必填项是否设置正确", e);
                return false;
            }
        }
        return true;
    }

}
