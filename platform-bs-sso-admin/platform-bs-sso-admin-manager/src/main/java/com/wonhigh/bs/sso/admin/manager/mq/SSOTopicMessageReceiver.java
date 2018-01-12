package com.wonhigh.bs.sso.admin.manager.mq;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.util.RandomStringUtil;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.admin.manager.SsoUserManager;
import com.wonhigh.bs.sso.admin.manager.sms.SmsEmailValidateService;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * 接收来自TOPIC VirtualTopic.com.retail.hrms.topic的消息
 */
@Component("topicMessageReceiver")
public class SSOTopicMessageReceiver implements MessageListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(SSOTopicMessageReceiver.class);
	
	@Resource
	private SsoUserManager ssoUserManager;
	@Resource
	private SsoUniformUserManager ssoUniformUserManager;
	@Resource
	private OrgUnitManager orgUnitManager;
	@Resource
	private SmsEmailValidateService smsEmailValidateService;
	@Resource
    private Properties properties;
	
    @Override
    public void onMessage(Message message) {
        if(message==null || "".equals(message.toString())){
            return ;
        }
        
        if (!(message instanceof TextMessage)) {
            if(message instanceof Object){
              ObjectMessage objMessage = (ObjectMessage) message;

              try {
            	  Object obj = objMessage.getObject();
            	  JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            	  LOGGER.info("Topic类型的消费的消息为 : "+jsonObject.toJSONString());
                  if(StringUtils.equals("{}", jsonObject.toJSONString())){
                	  LOGGER.error("同步失败，无效数据");
                	  return;
                  }
                  
                  //1:新增 2:修改 3:离职全人员 4:店铺离职人员
                  /**
              	 * {"employeeCode":"120983954","employeeName":"古燕霞",
              	 * "employeeStatus":2,"entryDate":1346688000000,
              	 * "modelName":"修改人员","modelType":2,"password":"c4ca4238a0b923820dcc509a6f75849b", 
              	 * "phoneNo":"18126149692","sex":1,"storeCode":"K301BL",
              	 * "unitCode":"100010201011201001",
              	 * "userNo":"120983954","workMail":""}
              	 */
                  Integer modelType = jsonObject.getInteger("modelType");
                  String employeeCode = jsonObject.getString("employeeCode");
                  //姓名
                  String employeeName = jsonObject.getString("employeeName");
                  //0:男 1:女
                  int sex = jsonObject.getString("sex") == null ? 1 : Integer.valueOf(jsonObject.getString("sex").trim()).intValue();
                  String phoneNo = jsonObject.getString("phoneNo");
                  String workMail = jsonObject.getString("workMail");
                  Date entryDate = jsonObject.getDate("entryDate");
                  String unitCode = jsonObject.getString("unitCode");
                  String storeCode = jsonObject.getString("storeCode");
                  //-1：临时 0: 预入职 1:试用 2:转正 3:放弃入职 4:离职 5:离职在途 6:退休 7:试工不合格 8:实习
                  Integer employeeStatus = jsonObject.getInteger("employeeStatus");
                  //1：辞职(主动) 2：辞职(被动) 3：辞退
                  Integer leaveType = jsonObject.getInteger("leaveType");
                  //登录账户
                  String userNo = jsonObject.getString("userNo");
                  String password = jsonObject.getString("password");
                  String idCard = jsonObject.getString("cardId");
                  String positionName = jsonObject.getString("positionName");
                  
                  if(modelType==null){
                	  return;
                  }
                  
                  List<OrgUnit> list = null;
                  if(StringUtils.isNotEmpty(unitCode)){
  					try {
  						list = orgUnitManager.findByUnitCode(unitCode);
  					} catch (ManagerException e1) {
  						e1.printStackTrace();
  					}
                  }
                  
                  if(modelType==ApiConstants.SSOUSER_HRMQ_ADD){
                	LOGGER.info("HR MQ消息：开始添加员工"+employeeName+"统一登录账号...");
            	    SsoUniformUserDTO userdto = new SsoUniformUserDTO();
                    userdto.setSureName(employeeName);
                    userdto.setTelephoneNumber("");
                    userdto.setSex(sex==0?1:0);
                    userdto.setState(ApiConstants.SSOUSER_NORMAL_STATE_VALUE);
                    OrgUnit org = null;
            	    if(list==null||list.size()==0){
                	    LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因：所在组织机构为空");
            		    return ;
            	    }else{
            	    	org = list.get(0);
            	    	userdto.setOrganizationalUnitName(org.getFullName());
                      	userdto.setOrganizationCode(org.getUnitCode());
                      	userdto.setUnitId(org.getUnitId());
            	    }
                  	if(StringUtils.isEmpty(employeeCode)){
                  		LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因：工号为空");
                  		return;
                  	}else{
                  		userdto.setLoginName(employeeCode);
                  	}
                  	SsoUniformUserDTO model = new SsoUniformUserDTO();
                    Map<String,Object> param = new HashMap<String, Object>(2);
                    param.put("loginName", employeeCode);
                    List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因：登录名"+employeeCode+"已经存在");
                  		return ;
                    }
                    param.clear();
                    param.put("employeeNumber", employeeCode);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因：工号"+employeeCode+"已经存在");
                  		return ;
                    }else{
                    	userdto.setEmployeeNumber(employeeCode);
                    }
                    //检查手机号是否重复
                    if(StringUtils.isNotEmpty(phoneNo)){
                    	param.clear();
                        param.put("mobile", phoneNo);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                        	userdto.setMobile(null);
                        	LOGGER.info("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号时手机号重复，修改手机号为空");
                      	}else{
                      	  userdto.setMobile(phoneNo);
                      	}
                    }else{
                    	userdto.setMobile(null);
                    }
                    //检查手机号码是否和登录名重复
                  	if(StringUtils.isNotEmpty(userdto.getMobile())){
                    	param.clear();
                        param.put("loginName", phoneNo);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                        	if(!(userdto.getId().intValue()==ssoUserList.get(0).getId().intValue())){
                        		LOGGER.error("HR MQ消息：员工"+employeeName+"修改信息时手机号和其它用户的登录名重复，修改为空");
                        		userdto.setMobile(null);
                        	}
                        }
                    }
                    if(StringUtils.isNotEmpty(workMail)){
                    	param.clear();
                        param.put("email", workMail);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                        	userdto.setEmail(null);
                        	LOGGER.info("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号时邮箱重复，修改邮箱为空");
                      	}else{
                      		userdto.setEmail(workMail);
                      	}
                    }else{
                    	userdto.setEmail(null);
                    }
                    param.clear();
                    param.put("mobile", employeeCode);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因：用户名不能是其它用户的手机号码");
                  		return ;
                    }
                    if(StringUtils.isNotEmpty(idCard)){
                        param.clear();
                        param.put("idCard", idCard);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                            LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号时身份证号已经存在，修改为空");
                            idCard = null;
                        }
                    }
                  	try{
                  		Date aud = new Date();
                      	userdto.setCreateTime(aud);
                      	userdto.setUpdateTime(aud);
                  		userdto.setCreateUser(ApiConstants.SSOADMIN_HR_BIZ_CODE);
                  		String bizUser = "{\""+ApiConstants.SSOADMIN_HR_BIZ_CODE+"\":\""+userdto.getLoginName()+"\"}";
                  		userdto.setBizUser(bizUser);
                  		userdto.setIdCard(idCard);
                  		userdto.setPositionName(positionName);
                  		String plaintextPwd = "";
                  		if(StringUtils.isEmpty(idCard)){
                  		  plaintextPwd = RandomStringUtil.getRandomCode2(6, 1);
                          password = PasswordUtil.createPassword(plaintextPwd);
                  		}else{
                  		  plaintextPwd = idCard.substring(idCard.length()-6);
                  		  password = PasswordUtil.createPassword(plaintextPwd);
                  		}
                        userdto.setPassword(password);
                  		ssoUniformUserManager.addToMysqlAndLdap(userdto);
              			if(StringUtils.equalsIgnoreCase(properties.getSendMsgPhoneFromHrMqSwitch(), "on")){
              				try{
      	            			//发送短信给用户 您的一账通账号注册成功，账号：xxxx，初始密码：xxxxx
                  				String content = properties.getSmsCreateNewAccountMsg();
                  				content = content.replace("{account}", userdto.getLoginName());
                  				content = content.replace("{password}", plaintextPwd);
      	            			HttpSMSSend smsSend = smsEmailValidateService.getSmsSend();
      	        				smsSend.setReceivePhones(phoneNo);
      	        				smsSend.setBusinessSystemCode(properties.getSmsBusinessSystemCode());
      	        				smsSend.setSenderName("SSO-ADMIN");
      	        				smsSend.setPriority(0);
      	        				smsSend.setSenderName("一账通");
      	        				// 发送内容，随机码，时间限制
      	        				smsSend.setContent(content);
      	        				LOGGER.info("开始发送短信到用户手机，"+phoneNo);
      	        				boolean flag = smsEmailValidateService.sendSms(smsSend);
      	        				if(flag==false){
      	        					LOGGER.info("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号成功，但是发送短信失败");
      	        				}
                      		}catch(Exception e){
                      			e.printStackTrace();
                      			LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号成功，但是发送短信失败");
                      			return;
                      		}
              			}
              			LOGGER.info("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号成功。");
                  	}catch(Exception e){
                  		LOGGER.error("HR MQ消息：员工"+employeeName+"入职，添加统一登录账号失败，原因："+e);
                  	}
                  }
                  if(modelType==ApiConstants.SSOUSER_HRMQ_UPDATE){
                	LOGGER.info("HR MQ消息：开始修改员工"+employeeName+"统一登录账号信息...");
                	//修改信息
                	if(list==null||list.size()==0){
                		LOGGER.error("HR MQ消息：员工"+employeeName+"修改统一登录账号信息失败，原因：所在组织机构不存在");
                		return ;
                	}
                	SsoUniformUserDTO userdto = new SsoUniformUserDTO();
	            	SsoUniformUserDTO model = new SsoUniformUserDTO();
	                Map<String,Object> param = new HashMap<String, Object>(2);
	                param.put("loginName", employeeCode);
	                List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
	                if (ssoUserList == null || ssoUserList.size()==0) {
	              		LOGGER.error("HR MQ消息：员工"+employeeName+"修改信息失败，原因:工号"+employeeCode+"不存在");
	              		return ;
                  	}else{
                  		userdto = ssoUserList.get(0);
                  		model = ssoUserList.get(0);
                  	}
                  	if(StringUtils.isNotEmpty(employeeName)){
                  		userdto.setSureName(employeeName);
                  	}
                  	if(StringUtils.isNotEmpty(workMail)){
                  		userdto.setEmail(workMail);
                  	}else{
                  		userdto.setEmail(null);
                  	}
                  	
                  	//检查手机号码是否重复
                  	if(StringUtils.isNotEmpty(phoneNo)){
                  		param.clear();
                        param.put("mobile", phoneNo);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                        	if(userdto.getId().intValue()!=ssoUserList.get(0).getId().intValue()){
                        		LOGGER.error("HR MQ消息：员工"+employeeName+"修改信息时手机号码重复，修改为空");
                        		userdto.setMobile(null);
                        	}else{
                        		userdto.setMobile(phoneNo);
                        	}
                        }else{
                            userdto.setMobile(phoneNo);
                        }
                  	}else{
                  		userdto.setMobile(null);
                  	}
                  	//检查手机号码是否和登录名重复
                  	if(StringUtils.isNotEmpty(userdto.getMobile())){
                    	param.clear();
                        param.put("loginName", phoneNo);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                        	if(!(userdto.getId().intValue()==ssoUserList.get(0).getId().intValue())){
                        		LOGGER.error("HR MQ消息：员工"+employeeName+"修改信息时手机号和其它用户的登录名重复，修改为空");
                        		userdto.setMobile(null);
                        	}
                        }
                    }
                  	//检查身份证是否重复
                  	if(StringUtils.isNotEmpty(idCard)){
                        param.clear();
                        param.put("idCard", idCard);
                        ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                        if (ssoUserList != null && ssoUserList.size()>0) {
                            if(ssoUserList.get(0).getId().intValue()!=userdto.getId().intValue()){
                                LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号时身份证号已经存在，修改为空");
                                idCard = null;
                            }
                        }
                    }
                  	userdto.setSex(sex==0?1:0);
                  	if(StringUtils.isNotEmpty(unitCode)){
                  		userdto.setOrganizationCode(unitCode);
                  		userdto.setOrganizationalUnitName(list.get(0).getFullName());
                  		userdto.setUnitId(list.get(0).getUnitId());
                  	}
                  	userdto.setUpdateTime(new Date());
                  	userdto.setPositionName(positionName);
                  	userdto.setIdCard(idCard);
                  	try{
                  		//修改
                  		userdto.setPassword(null);
                        ssoUniformUserManager.updateToMysqlAndLdap(userdto);
                  	}catch(Exception e){
                  		e.printStackTrace();
                  		LOGGER.error("HR MQ消息：员工"+employeeName+"修改统一登录账号信息失败，原因："+e);
                  		return;
                  	}
                  	LOGGER.info("HR MQ消息：员工"+employeeName+"修改统一登录账号成功。");
                  }
                  if(modelType==ApiConstants.SSOUSER_HRMQ_DELETE){
  	                //离职
                	LOGGER.info("HR MQ消息：开始删除员工"+employeeName+"统一登录账号...");
                  	/**
                  	 * {"employeeCode":"161155454","employeeName":"罗灿波","employeeStatus":4,
                  	 * "entryDate":1479916800000,"leaveDate":1493481600000,"leaveType":1,
                  	 * "modelName":"离职全人员","modelType":3,"password":"08c865680dea70ed176b45d60c3ed477",
                  	 * "phoneNo":"13714179829","sex":0,"storeCode":"ADSZ16",
                  	 * "unitCode":"1000101020108012","userNo":"161155454","workMail":""}
                  	 */
  	                String employeeNumber = jsonObject.getString("employeeCode");
  	                if(StringUtils.isNotEmpty(employeeNumber)){
  		            	SsoUniformUserDTO model = new SsoUniformUserDTO();
  		                Map<String,Object> param = new HashMap<String, Object>(2);
  		                param.put("loginName", employeeCode);
  		                List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
  		                if (ssoUserList == null || ssoUserList.size()==0) {
  		              		LOGGER.error("HR MQ消息：员工"+employeeName+"离职,删除统一登录用户失败，原因:工号"+employeeCode+"不存在");
  		              		return ;
  	                  	}
  		                try{
  		                	ssoUniformUserManager.logicDelete(ssoUserList.get(0));
  		                }catch (Exception e){
  		                	e.printStackTrace();
  		                	LOGGER.error("HR MQ消息：删除员工"+employeeName+"统一登录账号失败，e="+e);
  		                	return;
  		                }
  	                }else{
  	                	LOGGER.info("HR MQ消息：员工"+employeeName+"离职。删除统一登录用户失败，原因：工号为空");
  	                	return;
  	                }
  	                LOGGER.info("HR MQ消息：删除员工"+employeeName+"统一登录账号成功");
                  }
              } catch (JMSException e) {
                  e.printStackTrace();
                  LOGGER.error("同步失败。error:"+e);
              } catch (Exception e) {
                  e.printStackTrace();
                  LOGGER.error("同步失败。error:"+e);
              } finally {
  			}
          
            }
        }else{
        	
        	
        }
    }

}
