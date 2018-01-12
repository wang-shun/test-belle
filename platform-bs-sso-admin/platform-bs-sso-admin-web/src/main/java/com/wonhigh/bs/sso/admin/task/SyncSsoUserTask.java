package com.wonhigh.bs.sso.admin.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.util.RandomStringUtil;
import com.wonhigh.bs.sso.admin.common.util.RedisUtil;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.manager.BizConfigManager;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.admin.manager.SsoUserManager;
import com.wonhigh.bs.sso.admin.manager.sms.SmsEmailValidateService;
import com.wonhigh.bs.sso.admin.task.syncSsoUser.PageResult;
import com.wonhigh.bs.sso.admin.task.syncSsoUser.SyncSsoUserPerPage;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;

/**
 * 全量同步hr用户信息
 * 
 * @author user
 * @date 2017年11月16日 上午10:18:27
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Component
public class SyncSsoUserTask {
	
	@Resource
	private SsoUserManager ssoUserManager;
	@Resource
	private SsoUniformUserManager ssoUniformUserManager;
	@Resource 
	private BizConfigManager bizConfigManager;
	@Resource
	private OrgUnitManager orgUnitManager;
	@Resource
    private SmsEmailValidateService smsEmailValidateService;
	@Resource
    private Properties properties;
	@Resource
	private RedisUtil redisUtil;
	
	private Logger LOGGER = LoggerFactory.getLogger(SyncSsoUserTask.class);
	
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	
	public void syncSsoUserFromHr(){
		//先获得同步锁
		//redisUtil.remove(ApiConstants.SYNC_HR_USER_REDIS_KEY);
		boolean syncLock = redisUtil.setIfAbsent(ApiConstants.SYNC_HR_USER_REDIS_KEY, ApiConstants.SYNC_HR_USER_REDIS_KEY);
		if(syncLock==false){
			LOGGER.error("获取同步锁失败，全量同步启动失败...thread="+Thread.currentThread().getId()+"-"+Thread.currentThread().getName());
			return;
		}
		LOGGER.info("开始全量同步hr用户信息...thread="+Thread.currentThread().getId()+"-"+Thread.currentThread().getName());
		
		//将表中的逻辑删除记录移到删除表
		try{
			String query = " AND del_flag =  1 ";
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("queryCondition",query);
			int total = this.ssoUniformUserManager.findCount(condition);
			if(total>0){
		        SimplePage page = new SimplePage(1, total, total);
		        List<SsoUniformUserDTO> list = this.ssoUniformUserManager.findByPage(page, "id", "desc", condition);
		        for (SsoUniformUserDTO ssoUniformUserDTO : list) {
		        	this.ssoUniformUserManager.logicDelete(ssoUniformUserDTO);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LOGGER.error("全量同步前移除ssoUser delFlag=1的记录时报错，error="+e);
		}
		
		try{
			long begin = System.currentTimeMillis();
        	//调用hr的同步接口获取用户信息
        	String hrBizCode = ApiConstants.SSOADMIN_HR_BIZ_CODE;
        	BizConfigDTO config = new BizConfigDTO();
        	config.setBizCode(hrBizCode);
        	Map<String,Object> param = new HashMap<String, Object>();
        	param.put("bizCode", hrBizCode);
        	List<BizConfigDTO> list = bizConfigManager.findByBiz(config, param);
        	if(list==null || list.size()==0){
                LOGGER.error("HR系统的bizCode错误，bizCode="+hrBizCode);
                return;
        	}
        	BizConfigDTO bizConfig = list.get(0);
        	if(StringUtils.isEmpty(bizConfig.getSyncUserInfoUrl())){
        		LOGGER.error("HR系统的同步数据URL错误");
                return ;
        	}
        	config = list.get(0);
        	int currentPage = 1;
        	Page page = this.getPage();
        	page.setCurrentPage(currentPage);
        	int totalPage = 0;
        	try{
				totalPage = this.getTotalPage(page, bizConfig);//this.syncSsoUserPerPage(page, config);
				LOGGER.info("全量获取hr数据总条数="+page.getTotalSize()+",总页数="+totalPage);
				CountDownLatch countDownLatch=new CountDownLatch(totalPage);
				List<Future<PageResult>> resultList = new ArrayList<Future<PageResult>>(); 
				BizConfigDTO bizconfig = new BizConfigDTO();
                Map<String,Object> params = new HashMap<String, Object>(3);
                params.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, params);
                BizConfigDTO eppBizconfig = (bizList!=null&&bizList.size()>0)?bizList.get(0):null;
				for(int i=currentPage; i<=totalPage; i++){
					
					//单线程执行同步任务
					/*page.setCurrentPage(i);
					this.syncSsoUserPerPage(page, config);*/
					
					//多线程执行同步任务
					PageResult p = new PageResult();
					p.setCurrentPage(i);
					p.setTotalPage(totalPage);
					SyncSsoUserPerPage task = new SyncSsoUserPerPage(countDownLatch, p, bizConfig, eppBizconfig, ssoUniformUserManager, orgUnitManager, smsEmailValidateService, properties);
					Future<PageResult> future = executor.submit(task);
					resultList.add(future);
				}
				
				countDownLatch.await();
				
				//获得同步结果
				int successSize = 0;
				int failures = 0;
				//遍历任务的结果   
		        for (Future<PageResult> future : resultList){   
		                try{   
		                    while(!future.isDone());//Future返回如果没有完成，则一直循环等待，直到Future返回完成  
		                    PageResult pageResult = future.get();
							successSize += pageResult.getSuccessSize();
							failures += pageResult.getFailures();
		                }catch(InterruptedException e){   
		                    e.printStackTrace();   
		                }catch(ExecutionException e){   
		                    e.printStackTrace();   
		                }finally{   
		                    //启动一次顺序关闭，执行以前提交的任务，但不接受新任务  
		                	//executor.shutdown();   
		                }   
		        }   
				long end = System.currentTimeMillis();
				LOGGER.info("全量同步hr用户信息完成，成功：" + successSize +"，失败："+failures+"。总耗时"+(end-begin)/1000 +"秒。"+failures+"thread="+Thread.currentThread().getId()+"-"+Thread.currentThread().getName());
        	}catch(Exception e){
        		e.printStackTrace();
        		LOGGER.error("调用hr同步数据接口报错，e="+e);
        	}
		}catch(Exception e){
			LOGGER.error("全量同步hr用户信息出错，"+e.getMessage());
		}finally{
			//释放同步锁
        	redisUtil.remove(ApiConstants.SYNC_HR_USER_REDIS_KEY);
		}
	}
	
	private int getTotalPage(Page page, BizConfigDTO bizConfig){
		Map<String, String> paramsMap = new HashMap<String, String>(7);
        paramsMap.put("page", page.getCurrentPage()+"");
        paramsMap.put("bizCode", bizConfig.getBizCode());
        Map<String, Object> map = InvokeApiUtil.getEmployeeInfo(bizConfig.getSyncUserInfoUrl(), paramsMap, bizConfig.getBizSecret());
        int code = (int) map.get("code");
        if (code != 1) {
            LOGGER.error("同步HR用户信息时出错" + (String) map.get("msg"));
            return 0;
        }
        JSONObject jsonObj = (JSONObject) map.get("json");
        int totalPage = jsonObj.getIntValue("totalPage");
        page.setTotalPage(totalPage);
        int count = jsonObj.getIntValue("count");
        page.setTotalSize(count);
        return totalPage;
	}
	
	private int syncSsoUserPerPage(Page page, BizConfigDTO bizConfig){
		//调用业务系同步hr用户信息接口
		Map<String, String> paramsMap = new HashMap<String, String>(7);
        paramsMap.put("page", page.getCurrentPage()+"");
        paramsMap.put("bizCode", bizConfig.getBizCode());
        Map<String, Object> map = InvokeApiUtil.getEmployeeInfo(bizConfig.getSyncUserInfoUrl(), paramsMap, bizConfig.getBizSecret());
        int code = (int) map.get("code");
        if (code != 1) {
            LOGGER.error("同步HR用户信息时出错" + (String) map.get("msg"));
            return 0;
        }
        JSONObject jsonObj = (JSONObject) map.get("json");
        int totalPage = jsonObj.getIntValue("totalPage");
        page.setTotalPage(totalPage);
        int count = jsonObj.getIntValue("count");
        page.setTotalSize(count);
        int currentPage = jsonObj.getIntValue("currentPage");
        int successSize = 0;
        int failureSize = 0;
        JSONArray data = jsonObj.getJSONArray("data");
        for (Object object : data) {
          	try{
          		JSONObject jsonObject = (JSONObject) object;
          		if(StringUtils.equals("{}", jsonObject.toJSONString())){
          			LOGGER.error("同步失败，无效数据，"+jsonObject);
	              	failureSize++;
	                continue;
                }
            	String employeeCode = jsonObject.getString("employeeCode");
                //姓名
                String employeeName = jsonObject.getString("employeeName");
                //0:男 1:女
                int sex = jsonObject.getInteger("sex")==null?1:jsonObject.getIntValue("sex");
                String phoneNo = jsonObject.getString("phoneNo");
                String workMail = jsonObject.getString("workMail");
                Date entryDate = jsonObject.getDate("entryDate");
                String unitCode = jsonObject.getString("unitCode");
                String storeCode = jsonObject.getString("storeCode");
                //-1：临时 0: 预入职 1:试用 2:转正 3:放弃入职 4:离职 5:离职在途 6:退休 7:试工不合格 8:实习
                Integer employeeStatus = jsonObject.getInteger("employeeStatus");
                if(employeeStatus==3){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:放弃入职");
                	failureSize++;
                    continue;
                }
                if(employeeStatus==4){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:离职");
                	failureSize++;
                    continue;
                }
                if(employeeStatus==7){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:试工不合格");
                	failureSize++;
                    continue;
                }
                //1：辞职(主动) 2：辞职(被动) 3：辞退
                Integer leaveType = jsonObject.getInteger("leaveType");
                //登录账户
                String userNo = jsonObject.getString("userNo");
                String password = jsonObject.getString("password");
                List<OrgUnit> orgUnitList = null;
                
                SsoUniformUserDTO user = new SsoUniformUserDTO();
              	
              	user.setSureName(employeeName);
              	user.setTelephoneNumber("");
              	user.setSex(sex==0?1:0);
              	user.setState(ApiConstants.SSOUSER_NORMAL_STATE_VALUE);
                
            	if(StringUtils.isEmpty(employeeCode)){
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"为空");
              		failureSize++;
                    continue;
              	}else{
              		user.setLoginName(employeeCode);
              		user.setEmployeeNumber(employeeCode);
              	}
    			try {
    				orgUnitList = orgUnitManager.findByUnitCode(unitCode);
    			} catch (ManagerException e1) {
    				e1.printStackTrace();
    			}
    			OrgUnit org = null;
    			if(orgUnitList==null||orgUnitList.size()==0){
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：所在组织机构"+unitCode+"为空");
              		failureSize++;
                    continue;
              	}else{
              		org = orgUnitList.get(0);
              		user.setOrganizationalUnitName(org.getFullName());
                  	user.setOrganizationCode(org.getUnitCode());
              	}
    			SsoUniformUserDTO model = new SsoUniformUserDTO();
                Map<String,Object> param = new HashMap<String, Object>(2);
                param.put("loginName", employeeCode);
                List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：登录名"+employeeCode+"在sso系统中已经存在");
              		failureSize++;
                    continue;
              	}
                param.clear();
                param.put("employeeNumber", employeeCode);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"在sso系统中已经存在");
              		failureSize++;
                    continue;
                }
                if(StringUtils.isNotEmpty(phoneNo)){
                	param.clear();
                    param.put("mobile", phoneNo);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	user.setMobile(null);
                    	LOGGER.info("同步hr用户"+employeeName+"，添加统一登录账号时手机号重复，修改为空");
                    }else{
                    	user.setMobile(phoneNo);
                    }
              	}else{
              		user.setMobile(null);
              	}
                if(StringUtils.isNotEmpty(workMail)){
                	param.clear();
                    param.put("email", workMail);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	user.setEmail(null);
                    	LOGGER.info("同步hr用户"+employeeName+"，添加统一登录账号时邮箱重复，修改为空");
                    }else{
                    	user.setEmail(workMail);
                    }
              	}else{
              		user.setEmail(null);
              	}
                param.clear();
                param.put("mobile", employeeCode);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户名不能是其它用户的手机号码");
                	failureSize++;
                	continue;
                }
      			try{
                  	Date date = new Date();
                  	user.setCreateTime(date);
                  	user.setUpdateTime(date);
              		user.setCreateUser(ApiConstants.SSOADMIN_HR_BIZ_CODE);
              		String bizUser = "{\""+ApiConstants.SSOADMIN_HR_BIZ_CODE+"\":\""+user.getLoginName()+"\"}";
              		user.setBizUser(bizUser);
              		String pwd = RandomStringUtil.getRandomCode2(6, 1);
              		String plaintextPwd = pwd;
                    password = PasswordUtil.createPassword(pwd);
              		user.setPassword(password);
              		LOGGER.info("开始同步添加统一登录账号到sso系统。。。"+user);
              		ssoUniformUserManager.addToMysqlAndLdap(user);
              		LOGGER.info("同步添加统一登录账号到sso系统成功");
          			if(StringUtils.equalsIgnoreCase(properties.getSendMsgPhoneFromHrSyncSwitch(), "on")){
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
        				LOGGER.info("开始发送短信到用户手机，"+phoneNo);
        				boolean flag = smsEmailValidateService.sendSms(smsSend);
        				if(flag==false){
        					LOGGER.info("同步hr用户"+employeeName+"，添加统一登录账号成功，但是发送短信失败");
        				}
          			}
    				successSize++;
    				LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号成功，");
          		}catch(Exception e){ 
          			LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败"+e.getMessage());
          			failureSize++;
                    continue;
          		}
          	}catch(Exception e){
          		LOGGER.error("同步hr用户"+object+"，添加统一登录账号失败，原因："+e.getMessage());
          		failureSize++;
                continue;
          	}
        }
        page.setSuccessSize(page.getSuccessSize()+successSize);
    	page.setFailures(page.getFailures()+failureSize);
        return totalPage;
	}

	public Page getPage() { 
        return new Page(); 
    } 
	
	public class Page { 
        public int currentPage;
        public int pageSize;
        public int totalPage;
        public int totalSize;
        public int successSize;
        public int failures;
		public int getCurrentPage() {
			return currentPage;
		}
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		public int getTotalPage() {
			return totalPage;
		}
		public void setTotalPage(int totalPage) {
			this.totalPage = totalPage;
		}
		public int getTotalSize() {
			return totalSize;
		}
		public void setTotalSize(int totalSize) {
			this.totalSize = totalSize;
		}
		public int getSuccessSize() {
			return successSize;
		}
		public void setSuccessSize(int successSize) {
			this.successSize = successSize;
		}
		public int getFailures() {
			return failures;
		}
		public void setFailures(int failures) {
			this.failures = failures;
		}
		
    } 
	
}
