package com.wonhigh.bs.sso.admin.task.syncSsoUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.EppConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.util.RandomStringUtil;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.admin.manager.sms.SmsEmailValidateService;
import com.wonhigh.bs.sso.admin.task.EppNoticeTask;
import com.wonhigh.bs.sso.admin.task.epp.EppNotification;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * 同步hr用户数据任务
 * 
 * @author user
 * @date 2017年12月7日 上午11:24:34
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SyncSsoUserPerPage implements Callable<PageResult> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SyncSsoUserPerPage.class);

	private PageResult page;
	private BizConfigDTO hrBizConfig;
	private BizConfigDTO eppBizConfig;
	private SsoUniformUserManager ssoUniformUserManager;
	private OrgUnitManager orgUnitManager;
	private SmsEmailValidateService smsEmailValidateService;
	private Properties properties;
	private CountDownLatch countDownLatch;
	
	public static Map<String,OrgUnit> orgUnitCache = new HashMap<String, OrgUnit>(3100);

	private SyncSsoUserPerPage(){};
	
	public SyncSsoUserPerPage(CountDownLatch countDownLatch, PageResult page, BizConfigDTO hrBizConfig, BizConfigDTO eppBizConfig, SsoUniformUserManager ssoUniformUserManager, OrgUnitManager orgUnitManager, SmsEmailValidateService smsEmailValidateService, Properties properties) {
		this.countDownLatch = countDownLatch;
		this.page = page;
		this.hrBizConfig = hrBizConfig;
		this.eppBizConfig = eppBizConfig;
		this.ssoUniformUserManager = ssoUniformUserManager;
		this.orgUnitManager = orgUnitManager;
		this.smsEmailValidateService = smsEmailValidateService;
		this.properties = properties;
	}
	
	@Override
	public PageResult call() throws Exception {
		LOGGER.info("开始同步第"+page.getCurrentPage()+"页HR用户..."+Thread.currentThread().getId()+"-"+Thread.currentThread().getName());
		long begin = System.currentTimeMillis();
		try{
			//调用业务系同步hr用户信息接口
			Map<String, Object> map = this.getHrUserInfo();
			if(map==null){
				LOGGER.info("同步第"+page.getCurrentPage()+"页HR用户结束。成功：0");
				return page;
			}
			JSONObject jsonObj = (JSONObject) map.get("json");
			int totalPage = jsonObj.getIntValue("totalPage");
			page.setTotalPage(totalPage);
			int count = jsonObj.getIntValue("count");
			page.setTotalSize(count);
			
			JSONArray data = jsonObj.getJSONArray("data");
			for (Object object : data) {
				JSONObject jsonObject = (JSONObject) object;
				if (StringUtils.equals("{}", jsonObject.toJSONString())) {
					LOGGER.error("同步失败，无效数据，" + jsonObject);
					page.setFailures(page.getFailures()+1);
					continue;
				}
				this.addSsoUserToSso(jsonObject);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			countDownLatch.countDown();
		}
		long end = System.currentTimeMillis();
		LOGGER.info("同步第"+page.getCurrentPage()+"页HR用户结束。成功："+page.getSuccessSize()+",失败："+page.getFailures()+",耗时："+(end-begin)/1000+"秒。");
		return page;
	}

	/**
	 * 调用hr接口获取数据
	 * @return
	 */
	private Map<String,Object> getHrUserInfo(){
		Map<String, String> paramsMap = new HashMap<String, String>(7);
		paramsMap.put("page", page.getCurrentPage() + "");
		paramsMap.put("bizCode", hrBizConfig.getBizCode());
		Map<String, Object> map = InvokeApiUtil.getEmployeeInfo(hrBizConfig.getSyncUserInfoUrl(), paramsMap,
				hrBizConfig.getBizSecret());
		int code = (int) map.get("code");
		if (code != 1) {
			LOGGER.error("调用获取HR用户信息接口时出错" + (String) map.get("msg"));
			return null;
		}
		return map;
	}
	
	/**
	 * 同步数据到sso系统
	 * @param jsonObject
	 * @return
	 */
	private int addSsoUserToSso(JSONObject jsonObject){
		try {
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
			String idCard = jsonObject.getString("cardId");
            String positionName = jsonObject.getString("positionName");
			//-1：临时 0: 预入职 1:试用 2:转正 3:放弃入职 4:离职 5:离职在途 6:退休 7:试工不合格 8:实习
			Integer employeeStatus = jsonObject.getInteger("employeeStatus");
			int delFlag = 0;
			if (employeeStatus == 3) {
				//LOGGER.error("同步hr用户" + employeeName + "，添加数据到临时表失败，原因：用户状态为:放弃入职");
				delFlag = 1;
			}
			if (employeeStatus == 4) {
				//LOGGER.error("同步hr用户" + employeeName + "，添加数据到临时表失败，原因：用户状态为:离职");
				delFlag = 1;
			}
			if (employeeStatus == 7) {
				//LOGGER.error("同步hr用户" + employeeName + "，添加数据到临时表失败，原因：用户状态为:试工不合格");
				delFlag = 1;
			}
			
			SsoUniformUserDTO model = new SsoUniformUserDTO();
			if(delFlag==1){
				//删除sso账号
				Map<String, Object> param = new HashMap<String, Object>(2);
				param.put("loginName", employeeCode);
				List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
				if (ssoUserList == null || ssoUserList.size() == 0) {
					LOGGER.error("同步hr用户" + employeeName + "，删除统一登录账号失败，原因：登录名" + employeeCode + "在sso系统中不存在");
					page.setFailures(page.getFailures()+1);
					return 0;
				}else {
					try{
						LOGGER.info("开始同步删除sso系统账号，"+ssoUserList.get(0));
						ssoUniformUserManager.logicDelete(ssoUserList.get(0));
						LOGGER.info("开始同步删除sso系统账号完成，"+ssoUserList.get(0));
						page.setSuccessSize(page.getSuccessSize()+1);
						
						//发送消息到epp
		                try{
		                    if(eppBizConfig!=null){
    		                    String url = eppBizConfig.getSyncUserInfoUrl();
    		                    if(StringUtils.isNotEmpty(url)){
    		                        EppNotification notice = new EppNotification(ssoUserList.get(0), EppConstants.DELETE_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
    		                        EppNoticeTask.sendNoticeToEpp(notice);
    		                    }
		                    }
		                } catch (Exception e) {
		                    e.printStackTrace();
		                    LOGGER.error("调用通知EPP接口失败"+e.getMessage());
		                }
		                
						return 1;
					}catch (Exception e){
						e.printStackTrace();
						LOGGER.error("删除sso系统账号报错，e="+e);
						page.setFailures(page.getFailures()+1);
					}
					return 0;
				}
			}
			
			//1：辞职(主动) 2：辞职(被动) 3：辞退
			Integer leaveType = jsonObject.getInteger("leaveType");
			//登录账户
			String userNo = jsonObject.getString("userNo");
			String password = jsonObject.getString("password");
			SsoUniformUserDTO user = new SsoUniformUserDTO();
			user.setSureName(employeeName);
			user.setTelephoneNumber("");
			user.setSex(sex == 0 ? 1 : 0);
			user.setState(ApiConstants.SSOUSER_NORMAL_STATE_VALUE);
			if (StringUtils.isEmpty(employeeCode)) {
				LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败，原因：工号" + employeeCode + "为空");
				page.setFailures(page.getFailures()+1);
				return 0;
			} else {
				user.setLoginName(employeeCode);
				user.setEmployeeNumber(employeeCode);
			}
			OrgUnit org = null;
			try {
				org = this.orgUnitCache.get(unitCode);
				if(org==null){
					List<OrgUnit> orgUnitList = orgUnitManager.findByUnitCode(unitCode);
					if(orgUnitList!=null && orgUnitList.size()>0){
						org = orgUnitList.get(0);
						this.orgUnitCache.put(unitCode, org);
					}else{
						LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败，原因：所在组织机构" + unitCode + "为空");
						page.setFailures(page.getFailures()+1);
						return 0;
					}
				}
			} catch (ManagerException e1) {
				e1.printStackTrace();
			}
			user.setOrganizationalUnitName(org.getFullName());
			user.setOrganizationCode(org.getUnitCode());
			user.setUnitId(org.getUnitId());
			Map<String, Object> param = new HashMap<String, Object>(2);
			param.put("loginName", employeeCode);
			List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
			if (ssoUserList != null && ssoUserList.size() > 0) {
				LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败，原因：登录名" + employeeCode + "在sso系统中已经存在");
				page.setFailures(page.getFailures()+1);
				return 0;
			}
			param.clear();
			param.put("employeeNumber", employeeCode);
			ssoUserList = ssoUniformUserManager.findByBiz(model, param);
			if(ssoUserList != null && ssoUserList.size() > 0) {
				LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败，原因：工号" + employeeCode + "在sso系统中已经存在");
				page.setFailures(page.getFailures()+1);
				return 0;
			}
			if(StringUtils.isNotEmpty(phoneNo)) {
				param.clear();
				param.put("mobile", phoneNo);
				ssoUserList = ssoUniformUserManager.findByBiz(model, param);
				if (ssoUserList != null && ssoUserList.size() > 0) {
					user.setMobile(null);
					LOGGER.info("同步hr用户" + employeeName + "，添加统一登录账号时手机号重复，修改为空");
				} else {
					user.setMobile(phoneNo);
				}
			} else {
				user.setMobile(null);
			}
			//检查手机号码是否和登录名重复
          	if(StringUtils.isNotEmpty(user.getMobile())){
            	param.clear();
                param.put("loginName", phoneNo);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	if(!(user.getId().intValue()==ssoUserList.get(0).getId().intValue())){
                		LOGGER.error("HR MQ消息：员工"+employeeName+"修改信息时手机号和其它用户的登录名重复，修改为空");
                		user.setMobile(null);
                	}
                }
            }
			if (StringUtils.isNotEmpty(workMail)) {
				param.clear();
				param.put("email", workMail);
				ssoUserList = ssoUniformUserManager.findByBiz(model, param);
				if (ssoUserList != null && ssoUserList.size() > 0) {
					user.setEmail(null);
					LOGGER.info("同步hr用户" + employeeName + "，添加统一登录账号时邮箱重复，修改为空");
				} else {
					user.setEmail(workMail);
				}
			} else {
				user.setEmail(null);
			}
			param.clear();
			param.put("mobile", employeeCode);
			ssoUserList = ssoUniformUserManager.findByBiz(model, param);
			if (ssoUserList != null && ssoUserList.size() > 0) {
				LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败，原因：用户名不能是其它用户的手机号码");
				page.setFailures(page.getFailures()+1);
				return 0;
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
			try {
				Date date = new Date();
				user.setCreateTime(date);
				user.setUpdateTime(date);
				user.setCreateUser(ApiConstants.SSOADMIN_HR_BIZ_CODE);
				String bizUser = "{\"" + ApiConstants.SSOADMIN_HR_BIZ_CODE + "\":\"" + user.getLoginName() + "\"}";
				user.setBizUser(bizUser);
				user.setIdCard(idCard);
				user.setPositionName(positionName);
				String plaintextPwd = "";
                if(StringUtils.isEmpty(idCard)){
                  plaintextPwd = RandomStringUtil.getRandomCode2(6, 1);
                  password = PasswordUtil.createPassword(plaintextPwd);
                }else{
                  plaintextPwd = idCard.substring(idCard.length()-6);
                  password = PasswordUtil.createPassword(plaintextPwd);
                }
                user.setPassword(password);
				LOGGER.info("开始同步添加统一登录账号到sso系统。。。" + user);
				ssoUniformUserManager.addToMysqlAndLdap(user);
				LOGGER.info("同步添加统一登录账号到sso系统成功" + user);
				if (StringUtils.equalsIgnoreCase(properties.getSendMsgPhoneFromHrSyncSwitch(), "on")) {
					//发送短信给用户
					String content = properties.getSmsCreateNewAccountMsg();
					content = content.replace("{account}", user.getLoginName());
					content = content.replace("{password}", plaintextPwd);
					HttpSMSSend smsSend = smsEmailValidateService.getSmsSend();
					smsSend.setReceivePhones(phoneNo);
					smsSend.setBusinessSystemCode(properties.getSmsBusinessSystemCode());
					smsSend.setSenderName("SSO-ADMIN");
					smsSend.setPriority(0);
					smsSend.setSenderName("一账通");
					// 发送内容，随机码，时间限制
					smsSend.setContent(content);
					LOGGER.info("开始发送短信到用户手机，" + phoneNo);
					boolean flag = smsEmailValidateService.sendSms(smsSend);
					if (flag == false) {
						LOGGER.info("同步hr用户" + employeeName + "，添加统一登录账号成功，但是发送短信失败");
					}
				}
				//发送消息到epp
                try{
                    if(eppBizConfig!=null){
                        String url = eppBizConfig.getSyncUserInfoUrl();
                        if(StringUtils.isNotEmpty(url)){
                            EppNotification notice = new EppNotification(user, EppConstants.ADD_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
                            EppNoticeTask.sendNoticeToEpp(notice);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("调用通知EPP接口失败"+e.getMessage());
                }
				page.setSuccessSize(page.getSuccessSize()+1);
			} catch (Exception e) {
				LOGGER.error("同步hr用户" + employeeName + "，添加统一登录账号失败" + e);
				page.setFailures(page.getFailures()+1);
				return 0;
			}
		} catch (Exception e) {
			LOGGER.error("同步hr用户" + jsonObject + "，添加统一登录账号失败，原因：" + e);
			page.setFailures(page.getFailures()+1);
			return 0;
		}
		return  1;
	}
	
	
}
