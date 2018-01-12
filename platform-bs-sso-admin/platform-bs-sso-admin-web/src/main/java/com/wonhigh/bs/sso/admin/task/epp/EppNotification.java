package com.wonhigh.bs.sso.admin.task.epp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.EppConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.task.syncSsoUser.SyncSsoUserPerPage;


/**
 * 发送通知到epp
 * 
 * @author user
 * @date 2017年12月21日 上午10:18:41
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class EppNotification implements Runnable{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(EppNotification.class);
    
    //通知内容
    private SsoUniformUserDTO ssoUserDto;
    //通知类型
    private String msgType;
    //发送到epp什么环境
    private String eppSourceParameter;
    //解绑或者绑定的业务系统登录名
    private String bizLoginName;
    //解绑或者绑定的业务系统登录名
    private String bizCode;
    //调用的通知url
    private String noticeUrl;
    
    private EppNotification(){};
    public EppNotification(SsoUniformUserDTO ssoUserDto, String msgType, String noticeUrl, String eppSourceParameter, String bizCode, String bizLoginName){
        this.ssoUserDto = ssoUserDto;
        this.msgType = msgType;
        this.noticeUrl = noticeUrl;
        this.eppSourceParameter = eppSourceParameter; 
        this.bizCode = bizCode;
        this.bizLoginName = bizLoginName;
    }
    
    @Override
    public void run() {
        switch (msgType) {
        case EppConstants.UPDATE_SSO_USER_NOTICE:
            this.updateSsoUser();
            break;

        case EppConstants.ADD_SSO_USER_NOTICE:
            this.addSsoUser();
            break;
            
        case EppConstants.DELETE_SSO_USER_NOTICE:
            this.deleteSsoUser();
            break;
            
        case EppConstants.CHANGE_PSWD_NOTICE:
            this.updatePassword();
            break;
            
        case EppConstants.BIND_LOGINNAME_NOTICE:
            this.bindBizLoginName();
            break;
            
        case EppConstants.UNBIND_LOGINNAME_NOTICE:
            this.unBindBizLoginName();
            break;
        }
    }
    
    //修改sso账号
    private void updateSsoUser(){
        try {
            LOGGER.info("开始发送更新用户信息通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.UPDATE_SSO_USER_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            json.put("name_cn", ssoUserDto.getSureName()==null?"":ssoUserDto.getSureName());
            json.put("phone", ssoUserDto.getMobile()==null?"":ssoUserDto.getMobile());
            json.put("email", ssoUserDto.getEmail()==null?"":ssoUserDto.getEmail());
            json.put("employee_no", ssoUserDto.getEmployeeNumber()==null?"":ssoUserDto.getEmployeeNumber());
            json.put("department", ssoUserDto.getPositionName()==null?"":ssoUserDto.getPositionName());
            json.put("title", ssoUserDto.getOrganizationalUnitName()==null?"":ssoUserDto.getOrganizationalUnitName());
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送更新用户信息通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{ 
                LOGGER.info("发送更新用户信息通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用更新用户信息通知EPP接口失败"+e.getMessage());
        }
    }
    
    //添加sso账号
    private void addSsoUser(){
        try {
            LOGGER.info("开始发送新用户入职通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.ADD_SSO_USER_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            json.put("name_cn", ssoUserDto.getSureName());
            json.put("phone", ssoUserDto.getMobile()==null?"":ssoUserDto.getMobile());
            json.put("email", ssoUserDto.getEmail()==null?"":ssoUserDto.getEmail());
            json.put("employee_no", ssoUserDto.getEmployeeNumber()==null?"":ssoUserDto.getEmployeeNumber());
            json.put("department", ssoUserDto.getPositionName()==null?"":ssoUserDto.getPositionName());
            json.put("title", ssoUserDto.getOrganizationalUnitName()==null?"":ssoUserDto.getOrganizationalUnitName());
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送新用户入职通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{ 
                LOGGER.info("发送新用户入职通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用新用户入职通知EPP接口失败"+e.getMessage());
        }
    }
    
    //删除sso账号
    private void deleteSsoUser(){
        try {
            LOGGER.info("开始发送离职通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.DELETE_SSO_USER_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送离职通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{ 
                LOGGER.info("发送离职通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用离职通知EPP接口失败"+e.getMessage());
        }
    }
    
    //修改密码
    private void updatePassword(){
        try {
            LOGGER.info("开始发送修改密码通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.CHANGE_PSWD_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送修改密码通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{ 
                LOGGER.info("发送修改密码通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用修改密码通知EPP接口失败"+e.getMessage());
        }
    }
    
    //绑定
    private void bindBizLoginName(){
        try {
            LOGGER.info("开始发送绑定通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.BIND_LOGINNAME_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            json.put("appCode", bizCode);
            json.put("lapp_login_name", bizLoginName);
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送绑定通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{
                LOGGER.info("发送绑定通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用绑定通知EPP接口失败"+e.getMessage());
        }
    }

    //解绑
    private void unBindBizLoginName(){
        try {
            LOGGER.info("开始发送解绑通知到EPP...");
            Map<String, String> bodys = new HashMap<String, String>(7);
            bodys.put("msg_type", EppConstants.UNBIND_LOGINNAME_NOTICE);
            bodys.put("source", eppSourceParameter);
            JSONObject json = new JSONObject();
            json.put("sso_login_name", ssoUserDto.getLoginName());
            json.put("appCode", bizCode);
            json.put("lapp_login_name", bizLoginName);
            bodys.put("msg_info", json.toJSONString());
            Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(noticeUrl, bodys);
            int c = (int) m.get("code");
            if (c != 1) {
                LOGGER.error("发送解绑通知到EPP出错"+(String) m.get("msg")+bodys);
            }else{
                LOGGER.info("发送解绑通知到EPP成功"+bodys);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用解绑通知EPP接口失败"+e.getMessage());
        }
    }
}
