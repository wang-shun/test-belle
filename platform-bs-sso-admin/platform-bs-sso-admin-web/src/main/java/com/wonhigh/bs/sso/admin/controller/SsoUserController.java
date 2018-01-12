package com.wonhigh.bs.sso.admin.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.EppConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.model.HttpEmailSend;
import com.wonhigh.bs.sso.admin.common.model.HttpSMSSend;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.model.PageResult;
import com.wonhigh.bs.sso.admin.common.model.ScheduleJob;
import com.wonhigh.bs.sso.admin.common.model.SsoUser;
import com.wonhigh.bs.sso.admin.common.util.AESUtil;
import com.wonhigh.bs.sso.admin.common.util.CommonUtil;
import com.wonhigh.bs.sso.admin.common.util.DateUtil;
import com.wonhigh.bs.sso.admin.common.util.ExcelToLdifUtil;
import com.wonhigh.bs.sso.admin.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.admin.common.util.Msg;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.util.PoiExcelUtils;
import com.wonhigh.bs.sso.admin.common.util.RandomStringUtil;
import com.wonhigh.bs.sso.admin.common.util.RedisUtil;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.manager.BizConfigManager;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleManager;
import com.wonhigh.bs.sso.admin.manager.SsoUniformUserManager;
import com.wonhigh.bs.sso.admin.manager.SsoUserManager;
import com.wonhigh.bs.sso.admin.manager.sms.SmsEmailValidateService;
import com.wonhigh.bs.sso.admin.task.EppNoticeTask;
import com.wonhigh.bs.sso.admin.task.JobTaskService;
import com.wonhigh.bs.sso.admin.task.epp.EppNotification;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;
import com.yougou.logistics.base.web.controller.BaseCrud4RestfulController;

/**
 * 
 * TODO: sso用户--控制层
 * 
 * @author xiao.fz
 * @date 2017年11月7日 下午2:15:25
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/sso_user")
public class SsoUserController extends BaseCrud4RestfulController<SsoUser> {

    @Resource
    private SsoUserManager ssoUserManager;
    @Resource
    private SsoUniformUserManager ssoUniformUserManager;
    @Resource
    private BizConfigManager bizConfigManager;
    @Resource
    private SsoAdminRoleManager ssoAdminRoleManager;
    @Resource
    private SmsEmailValidateService smsEmailValidateService;
    @Resource
    private OrgUnitManager orgUnitManager;
    @Autowired
    private ServletContext context;
    @Resource
    private JobTaskService jobTaskService;
    @Resource
    private Properties properties;
    @Resource
    private RedisUtil redisUtil;
    
    // 只能5个线程同时访问 导出方法
    private static Semaphore exportSemp;
    
    /**
     * 2表示用户被锁定
     */
    private static final Integer LOCK_STATE = 2;
    /**
     * 1表示用户被解锁
     */
    private static final Integer UNLOCK_STATE =1;
    /**
     * 前端传来的锁定标志
     */
    private static final String LOCK_FLAG = "lock";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(SsoUserController.class);

    @Override
    public CrudInfo init() {
        return new CrudInfo("index/", bizConfigManager);
    }

    @RequestMapping("/index")
    public String index() throws Exception {
        return "index";
    }

    @RequestMapping("/userPage")
    public String index(ModelMap model) throws Exception {
        //查询所有bizConfig
        try {
            Map<String, Object> params = new HashMap<String, Object>(1);
            SimplePage page = new SimplePage(1, 50, 50);
            List<BizConfigDTO> list = bizConfigManager.findByPage(page, null, null, params);
            JSONArray ja = new JSONArray();
            for (BizConfigDTO b : list) {
                JSONObject jo = new JSONObject();
                jo.put("bizId", b.getId());
                jo.put("bizName", b.getBizName());
            }
            model.addAttribute("bizConfigListJson", ja.toJSONString());
            model.addAttribute("bizConfigList", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "sso_user";
    }

    @RequestMapping(value = "/ssoUserlist.json")
    @ResponseBody
    public Map<String, Object> ssoUserList(HttpServletRequest req, 
    		@RequestParam(value = "loginNameP", defaultValue = "", required = false) String loginName,
    		@RequestParam(value = "mobileP", defaultValue = "", required = false) String mobile,
    		@RequestParam(value = "idCardP", defaultValue = "", required = false) String idCard,
    		@RequestParam(value = "employeeNumberP", defaultValue = "", required = false) String employeeNumber,
    		@RequestParam(value = "emailP", defaultValue = "", required = false) String email,
    		@RequestParam(value = "sureNameP", defaultValue = "", required = false) String sureName,
    		@RequestParam(value = "employeeTypeP", defaultValue = "", required = false) String employeeType,
    		@RequestParam(value = "stateP", defaultValue = "", required = false) Integer state,
    		@RequestParam(value = "orgCond", defaultValue = "", required = false) String orgCond,
    		@RequestParam(value = "bindStateP", defaultValue = "-1", required = false) Integer bindState,
    		@RequestParam(value = "bizCodeP", defaultValue = "-1", required = false) String bizCode,
    		@RequestParam(value = "bizCodeN", defaultValue = "", required = false) String bizCodeN,
    		@RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
    		@RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
    		Model model) throws ManagerException {

    	Map<String, Object> obj = new HashMap<String, Object>(3);
    	try{
    		LOGGER.info("查询souser列表...");
	        
	        int pageNo = StringUtils.isEmpty(req.getParameter("page")) ? 1 : Integer.parseInt(req.getParameter("page"));
	        int pageSize = StringUtils.isEmpty(req.getParameter("rows")) ? 10 : Integer.parseInt(req.getParameter("rows"));
	
	        Map<String, Object> condition = new HashMap<String, Object>();
	        condition.put("loginName",loginName);
	        condition.put("mobile",mobile);
	        condition.put("employeeNumber",employeeNumber);
	        condition.put("email",email);
	        condition.put("sureName",sureName);
	        condition.put("employeeType",employeeType);
	        if(state!=null && state!=-1){
	        	condition.put("state",state);
	        }

			//			if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)
			//					&& DateUtil.getDateFormat(startTime).getTime() > DateUtil.getDateFormat(endTime).getTime()) {
			//				LOGGER.info("结束日期小于开始日期!");
			//				obj.put("code", 0);
			//				obj.put("msg", "结束日期小于开始日期!");
			//				return obj;
			//			}

			if (!StringUtils.isEmpty(startTime)) {
				condition.put("startTime", startTime + " 00:00:00");
			}
			if (!StringUtils.isEmpty(endTime)) {
				condition.put("endTime", endTime + " 23:59:59");
			}

	        StringBuilder query = new StringBuilder();
        	//查询全部绑定状态
        	if(bindState==1){
        		//绑定指定系统
        		if(!StringUtils.equals(bizCode, "-1")){
        			query.append(" AND sso_user.biz_user like \"%\\\"" + bizCode.trim() + "\\\":%\"");
        		}else{
        			//绑定任意系统
        			query.append(" AND sso_user.biz_user !=  \"{}\" ");
        		}
        	}else if(bindState==0){
        		//查询未绑定
        		//未绑定指定系统
        		if(!StringUtils.equals(bizCode, "-1")){
        			query.append(" AND sso_user.biz_user not like \"%\\\"" + bizCode.trim() + "\\\":%\"");
        		}else{
        			//未绑定任意系统
        			query.append(" AND sso_user.biz_user =  \"{}\" ");
        		}
        	}
        	if(!StringUtils.isEmpty(bizCodeN)){
        		query.append(" AND sso_user.biz_user like \"%:\\\"" + bizCodeN.trim() + "\\\"%\"");
        	}
        	
        	if(!StringUtils.isEmpty(idCard)) {
        		query.append(" AND sso_user.id_card like \"%" + idCard.trim() + "%\"");
        	}
        	
	        //过滤组织机构
	        SsoAdminDTO manager = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
	        if (!(manager.getAdminType() == ApiConstants.SSOADMIN_SYSTEM_ROLE)) {
	        	//当前管理员能查看的机构（包含子节点）
	            String orgUnitLimits = req.getSession().getAttribute("orgUnitLimits") == null ? null
	                    : (String) req.getSession().getAttribute("orgUnitLimits");
	            if(StringUtils.isEmpty(orgUnitLimits)){
	            	orgUnitLimits = "NULL";
	            	condition.put("organizationCode",orgUnitLimits);
	            }else if(StringUtils.isNotEmpty(orgCond)){
	            	//查询节点是否在权限内
	            	String[] orgCodeArr = orgUnitLimits.split(",");
	            	boolean flag = false;
	            	for (int i = 0; i < orgCodeArr.length; i++) {
		            	if(CommonUtil.isPrefix(orgCodeArr[i], orgCond.trim())){
		            		flag=true;
		            		break;
		            	}
					}
	            	if(flag){
	            		condition.put("organizationCode",orgCond.trim());
	            	}else{
	            		orgUnitLimits = "NULL";
		            	condition.put("organizationCode",orgUnitLimits);
	            	}
	            }else{
	            	query.append(" AND ( ");
	            	String[] orgCodeArr = orgUnitLimits.split(",");
	            	for (int i = 0; i < orgCodeArr.length; i++) {
						if(i==0){
							query.append(" sso_user.organization_code like \""+orgCodeArr[i].trim()+"%\" ");
						}else{
							query.append(" OR sso_user.organization_code like \""+orgCodeArr[i].trim()+"%\" ");
						}
					}
	            	query.append(" ) ");
	            }
	        } else {
	        	//系统管理员查询全部
	        	if(StringUtils.isEmpty(orgCond)){
	        		condition.put("organizationCode",null);
	        	}else{
	        		condition.put("organizationCode",orgCond.trim());
	        	}
	        }
	        
	        if(StringUtils.isNotEmpty(query.toString())){
	        	condition.put("queryCondition",query.toString());
	        }
	        int total = this.ssoUniformUserManager.findCount(condition);
	        SimplePage page = new SimplePage(pageNo, pageSize, total);
	        List<SsoUniformUserDTO> list = this.ssoUniformUserManager.findByPage(page, "id", "desc", condition);
	       
	        obj.put("total", total);
	        obj.put("rows", list);
    	}catch(Exception e){
    		e.printStackTrace();
    		LOGGER.error("查询ssoUser列表失败"+e.getMessage());
    	}
        return obj;
    }
    
	@RequestMapping(value = "/queryExportSsoUserlist")
    @ResponseBody
    public Map<String, Object> queryExportSsoUserlist(HttpServletRequest request, HttpServletResponse response, 
    		@RequestParam(value = "loginNameP", defaultValue = "", required = false) String loginName,
    		@RequestParam(value = "mobileP", defaultValue = "", required = false) String mobile,
    		@RequestParam(value = "employeeNumberP", defaultValue = "", required = false) String employeeNumber,
    		@RequestParam(value = "emailP", defaultValue = "", required = false) String email,
    		@RequestParam(value = "sureNameP", defaultValue = "", required = false) String sureName,
    		@RequestParam(value = "employeeTypeP", defaultValue = "", required = false) String employeeType,
    		@RequestParam(value = "stateP", defaultValue = "", required = false) Integer state,
    		@RequestParam(value = "orgCond", defaultValue = "", required = false) String orgCond,
    		@RequestParam(value = "bindStateP", defaultValue = "-1", required = false) Integer bindState,
    		@RequestParam(value = "bizCodeP", defaultValue = "-1", required = false) String bizCode,
    		Model model) throws ManagerException {

    	Map<String, Object> obj = new HashMap<String, Object>(3);
    	try{
    		LOGGER.info("查询souser列表...");
	        int pageNo = StringUtils.isEmpty(request.getParameter("page")) ? 1 : Integer.parseInt(request.getParameter("page"));
	        int pageSize = StringUtils.isEmpty(request.getParameter("rows")) ? 10 : Integer.parseInt(request.getParameter("rows"));
	        Map<String, Object> condition = new HashMap<String, Object>();
	        condition.put("loginName",loginName);
	        condition.put("mobile",mobile);
	        condition.put("employeeNumber",employeeNumber);
	        condition.put("email",email);
	        condition.put("sureName",sureName);
	        condition.put("employeeType",employeeType);
	        if(state!=null && state!=-1){
	        	condition.put("state",state);
	        }
	        StringBuilder query = new StringBuilder();
        	//查询全部绑定状态
        	if(bindState==1){
        		//绑定指定系统
        		if(!StringUtils.equals(bizCode, "-1")){
        			query.append(" AND biz_user like \"%\\\"" + bizCode.trim() + "\\\":%\"");
        		}else{
        			//绑定任意系统
        			query.append(" AND biz_user !=  \"{}\" ");
        		}
        	}else if(bindState==0){
        		//查询未绑定
        		//未绑定指定系统
        		if(!StringUtils.equals(bizCode, "-1")){
        			query.append(" AND biz_user not like \"%\\\"" + bizCode.trim() + "\\\":%\"");
        		}else{
        			//未绑定任意系统
        			query.append(" AND biz_user =  \"{}\" ");
        		}
        	}
	        //过滤组织机构
	        SsoAdminDTO manager = (SsoAdminDTO) request.getSession().getAttribute("loginUser");
	        if (!(manager.getAdminType() == ApiConstants.SSOADMIN_SYSTEM_ROLE)) {
	        	//当前管理员能查看的机构（包含子节点）
	            String orgUnitLimits = request.getSession().getAttribute("orgUnitLimits") == null ? null
	                    : (String) request.getSession().getAttribute("orgUnitLimits");
	            if(StringUtils.isEmpty(orgUnitLimits)){
	            	orgUnitLimits = "NULL";
	            	condition.put("organizationCode",orgUnitLimits);
	            }else if(StringUtils.isNotEmpty(orgCond)){
	            	//查询节点是否在权限内
	            	String[] orgCodeArr = orgUnitLimits.split(",");
	            	boolean flag = false;
	            	for (int i = 0; i < orgCodeArr.length; i++) {
		            	if(CommonUtil.isPrefix(orgCodeArr[i], orgCond.trim())){
		            		flag=true;
		            		break;
		            	}
					}
	            	if(flag){
	            		condition.put("organizationCode",orgCond.trim());
	            	}else{
	            		orgUnitLimits = "NULL";
		            	condition.put("organizationCode",orgUnitLimits);
	            	}
	            }else{
	            	query.append(" AND ( ");
	            	String[] orgCodeArr = orgUnitLimits.split(",");
	            	for (int i = 0; i < orgCodeArr.length; i++) {
						if(i==0){
							query.append(" organization_code like \""+orgCodeArr[i].trim()+"%\" ");
						}else{
							query.append(" OR organization_code like \""+orgCodeArr[i].trim()+"%\" ");
						}
					}
	            	query.append(" ) ");
	            }
	        } else {
	        	//系统管理员查询全部
	        	if(StringUtils.isEmpty(orgCond)){
	        		condition.put("organizationCode",null);
	        	}else{
	        		condition.put("organizationCode",orgCond.trim());
	        	}
	        }
	        if(StringUtils.isNotEmpty(query.toString())){
	        	condition.put("queryCondition",query.toString());
	        }
	        int total = this.ssoUniformUserManager.findCount(condition);
	        /*//保存到session
	        if(total>0){
	        	SimplePage page = new SimplePage(1, total, total);
		        List<SsoUniformUserDTO> list = this.ssoUniformUserManager.findByPage(page, "id", "desc", condition);
	        	redisUtil.set(request.getSession().getId(), list, 15L);
	        }*/
	        obj.put("total", total);
    	}catch(Exception e){
    		e.printStackTrace();
    		LOGGER.error("查询ssoUser列表失败"+e.getMessage());
    		obj.put("total", -1);
    	}
        return obj;
    }
    
    @RequestMapping(value = "/exportSsoUserlist.xmls")
    @ResponseBody
    public Map<String, Object> exportSsoUserlist(HttpServletRequest req, HttpServletResponse response, 
            @RequestParam(value = "loginNameP", defaultValue = "", required = false) String loginName,
            @RequestParam(value = "mobileP", defaultValue = "", required = false) String mobile,
            @RequestParam(value = "idCardP", defaultValue = "", required = false) String idCard,
            @RequestParam(value = "employeeNumberP", defaultValue = "", required = false) String employeeNumber,
            @RequestParam(value = "emailP", defaultValue = "", required = false) String email,
            @RequestParam(value = "sureNameP", defaultValue = "", required = false) String sureName,
            @RequestParam(value = "employeeTypeP", defaultValue = "", required = false) String employeeType,
            @RequestParam(value = "stateP", defaultValue = "", required = false) Integer state,
            @RequestParam(value = "orgCond", defaultValue = "", required = false) String orgCond,
            @RequestParam(value = "bindStateP", defaultValue = "-1", required = false) Integer bindState,
            @RequestParam(value = "bizCodeP", defaultValue = "-1", required = false) String bizCode,
            @RequestParam(value = "bizCodeN", defaultValue = "", required = false) String bizCodeN,
            @RequestParam(value = "startTime", defaultValue = "", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "", required = false) String endTime,
            Model model) throws ManagerException {

        Map<String, Object> obj = new HashMap<String, Object>(3);
        int total = 0;
        try{
            LOGGER.info("导出souser列表...");
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("loginName",loginName);
            condition.put("mobile",mobile);
            condition.put("employeeNumber",employeeNumber);
            condition.put("email",email);
            condition.put("sureName",sureName);
            condition.put("employeeType",employeeType);
            if(state!=null && state!=-1){
                condition.put("state",state);
            }

            if (!StringUtils.isEmpty(startTime)) {
                condition.put("startTime", startTime + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endTime)) {
                condition.put("endTime", endTime + " 23:59:59");
            }

            StringBuilder query = new StringBuilder();
            //查询全部绑定状态
            if(bindState==1){
                //绑定指定系统
                if(!StringUtils.equals(bizCode, "-1")){
                    query.append(" AND sso_user.biz_user like \"%\\\"" + bizCode.trim() + "\\\":%\"");
                }else{
                    //绑定任意系统
                    query.append(" AND sso_user.biz_user !=  \"{}\" ");
                }
            }else if(bindState==0){
                //查询未绑定
                //未绑定指定系统
                if(!StringUtils.equals(bizCode, "-1")){
                    query.append(" AND sso_user.biz_user not like \"%\\\"" + bizCode.trim() + "\\\":%\"");
                }else{
                    //未绑定任意系统
                    query.append(" AND sso_user.biz_user =  \"{}\" ");
                }
            }
            if(!StringUtils.isEmpty(bizCodeN)){
                query.append(" AND sso_user.biz_user like \"%:\\\"" + bizCodeN.trim() + "\\\"%\"");
            }
            
            if(!StringUtils.isEmpty(idCard)) {
                query.append(" AND sso_user.id_card like \"%" + idCard.trim() + "%\"");
            }
            
            //过滤组织机构
            SsoAdminDTO manager = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
            if (!(manager.getAdminType() == ApiConstants.SSOADMIN_SYSTEM_ROLE)) {
                //当前管理员能查看的机构（包含子节点）
                String orgUnitLimits = req.getSession().getAttribute("orgUnitLimits") == null ? null
                        : (String) req.getSession().getAttribute("orgUnitLimits");
                if(StringUtils.isEmpty(orgUnitLimits)){
                    orgUnitLimits = "NULL";
                    condition.put("organizationCode",orgUnitLimits);
                }else if(StringUtils.isNotEmpty(orgCond)){
                    //查询节点是否在权限内
                    String[] orgCodeArr = orgUnitLimits.split(",");
                    boolean flag = false;
                    for (int i = 0; i < orgCodeArr.length; i++) {
                        if(CommonUtil.isPrefix(orgCodeArr[i], orgCond.trim())){
                            flag=true;
                            break;
                        }
                    }
                    if(flag){
                        condition.put("organizationCode",orgCond.trim());
                    }else{
                        orgUnitLimits = "NULL";
                        condition.put("organizationCode",orgUnitLimits);
                    }
                }else{
                    query.append(" AND ( ");
                    String[] orgCodeArr = orgUnitLimits.split(",");
                    for (int i = 0; i < orgCodeArr.length; i++) {
                        if(i==0){
                            query.append(" sso_user.organization_code like \""+orgCodeArr[i].trim()+"%\" ");
                        }else{
                            query.append(" OR sso_user.organization_code like \""+orgCodeArr[i].trim()+"%\" ");
                        }
                    }
                    query.append(" ) ");
                }
            } else {
                //系统管理员查询全部
                if(StringUtils.isEmpty(orgCond)){
                    condition.put("organizationCode",null);
                }else{
                    condition.put("organizationCode",orgCond.trim());
                }
            }
            
            if(StringUtils.isNotEmpty(query.toString())){
                condition.put("queryCondition",query.toString());
            }
            total = this.ssoUniformUserManager.findCount(condition);
            if(total>properties.getConcurrentExportMaxSizePerRequest()){
                if(exportSemp==null){
                    synchronized(this){
                        if(exportSemp==null){
                            exportSemp = new Semaphore(properties.getConcurrentSemaphoreNumber());
                        }
                    }
                }
                try {
                    LOGGER.info(Thread.currentThread()+"开始获取导出许可...");
                    exportSemp.acquire();
                    LOGGER.info(Thread.currentThread()+"获取导出许可成功。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LOGGER.error("导出souser列表时获取许可时报错，e="+e);
                }
            }
            if(total>0){
                
                SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
                String filename = "sso_user_list_"+total+"_"+req.getSession().getAttribute("session_user")+"_"+System.currentTimeMillis()+".xlsx";
                String excelPath = "/resources/download/ssouser/";
                File excelFile = new File(req.getSession().getServletContext().getRealPath("/")+excelPath+filename);
                if(excelFile.exists()==false){
                    File path = new File(req.getSession().getServletContext().getRealPath("/")+excelPath);
                    if(path.exists()==false){
                        path.mkdirs();
                    }
                    excelFile.createNewFile();
                }
                
                String[] tilte = new String[]{"工号","姓名","手机号","已绑定系统","状态","组织机构代码","组织机构名称"};
                String[] fieldName = new String[]{"employeeNumber","sureName","mobile","bizUser","state","organizationCode","organizationalUnitName"};
                OutputStream os = null;
                try {
                    /*response.setContentType("APPLICATION/OCTET-STREAM");
                    response.setHeader("Content-Disposition",  "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));  
                    */
                    os = new FileOutputStream(excelFile); //response.getOutputStream();
                    Sheet sheet = workbook.createSheet("一账通用户信息");
                    CellStyle header = getHeader(workbook);
                    CellStyle style = getContext(workbook);
                    PoiExcelUtils.addTitle(sheet, tilte, "一账通用户信息", header, style);
                    int pageSize = 5000;
                    int pageCount = (total+pageSize-1)/pageSize;
                    SimplePage page = new SimplePage();
                    int startLine = 2;
                    for(int i=1; i<pageCount+1; i++){
                        page.setPageNo(i);
                        page.setPageSize(pageSize);
                        page.setTotalCount(total);
                        List<SsoUniformUserDTO> list = this.ssoUniformUserManager.findByPage(page, "id", "desc", condition);
                        PoiExcelUtils.addContextByList(sheet, list, fieldName, getContext(workbook), false, startLine);
                        startLine+=list.size();
                    }
                    workbook.write(os);
                } catch (Throwable e) {
                    LOGGER.error("一账通用户信息出错：" + e.getMessage(), e);
                } finally {
                    try {
                        os.flush();
                        os.close();
                    } catch (Throwable e) {
                        LOGGER.error("一账通用户信息Excel出错：" + e.getMessage(), e);
                    }
                }
                obj.put("excelPath", excelPath+filename);
            }
            obj.put("total", total);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("查询ssoUser列表失败"+e.getMessage());
            obj.put("total", -1);
        }finally{
            if(total>properties.getConcurrentExportMaxSizePerRequest()){
                exportSemp.release();
            }
        }
        return obj;
    }
    

    @RequestMapping("/add")
    @ResponseBody
    public Map<String, Object> add(HttpServletRequest request,
            @RequestParam(value = "loginName", defaultValue = "", required = true) String loginName,
            @RequestParam(value = "sureName", defaultValue = "", required = true) String sureName,
            @RequestParam(value = "telephoneNumber", defaultValue = "") String telephoneNumber,
            @RequestParam(value = "employeeType", defaultValue = "", required = false) String employeeType,
            @RequestParam(value = "employeeNumber", defaultValue = "", required = false) String employeeNumber,
            @RequestParam(value = "email", defaultValue = "", required = false) String email,
            @RequestParam(value = "departmentName", defaultValue = "", required = false) String departmentName,
            @RequestParam(value = "mobile", defaultValue = "", required = true) String mobile,
            @RequestParam(value = "password", defaultValue = "123456", required = false) String password,
            @RequestParam(value = "sex", defaultValue = "1", required = false) int sex,
            @RequestParam(value = "idCard", defaultValue = "", required = false) String idCard,
            @RequestParam(value = "positionName", defaultValue = "", required = false) String positionName,
            @RequestParam(value = "state", defaultValue = "1", required = true) int state,
            @RequestParam(value = "description", defaultValue = "", required = false) String description,
            @RequestParam(value = "organizationId", defaultValue = "", required = false) Integer organizationId,
            @RequestParam(value = "organizationCode", defaultValue = "", required = false) String organizationCode,
            @RequestParam(value = "organizationalUnitName", defaultValue = "", required = false) String organizationalUnitName,
            @RequestParam(value = "bizUsers", defaultValue = "{}", required = false) String bizUsers) {

        Map<String, Object> r = new HashMap<String, Object>(4);
        //首先添加到mysql数据库
        if (StringUtils.isEmpty(organizationCode)) {
            r.put("code", 0);
            r.put("msg", "请选择机构");
            r.put("isError", true);
            return r;
        }else{
        	try {
				List<OrgUnit> list = orgUnitManager.findByUnitCode(organizationCode);
				if(list==null || list.size()==0){
					r.put("code", 0);
		            r.put("msg", "机构代码错误，请重新选择");
		            r.put("isError", true);
		            return r;
				}
				organizationalUnitName = list.get(0).getFullName();
				organizationId = list.get(0).getUnitId();
			} catch (ManagerException e) {
				e.printStackTrace();
			}
        }
        try {
        	SsoUniformUserDTO model = new SsoUniformUserDTO();
            Map<String,Object> param = new HashMap<String, Object>(2);
            param.put("loginName", loginName);
            List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
            if (ssoUserList != null && ssoUserList.size()>0) {
                r.put("code", 0);
                r.put("msg", "用户名已经存在");
                r.put("isError", true);
                return r;
            }
            param.clear();
            param.put("mobile", loginName);
            ssoUserList = ssoUniformUserManager.findByBiz(model, param);
            if(ssoUserList != null && ssoUserList.size()>0) {
                r.put("code", 0);
                r.put("msg", "用户名不能是其它用户的手机号码 ");
                r.put("isError", true);
                return r;
            }
            if(StringUtils.isNotEmpty(mobile)) {
            	param.clear();
                param.put("mobile", mobile);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                    r.put("code", 0);
                    r.put("msg", "手机号码已经存在");
                    r.put("isError", true);
                    return r;
                }
                //检查手机号码是否和登录名重复
                param.clear();
                param.put("loginName", mobile);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	r.put("code", 0);
                    r.put("msg", "手机号码已经是其它用户的用户名");
                    r.put("isError", true);
                    return r;
                }
            }
            if(StringUtils.isNotEmpty(email)){
	            param.clear();
	            param.put("email", email);
	            ssoUserList = ssoUniformUserManager.findByBiz(model, param);
	            if (ssoUserList != null && ssoUserList.size()>0) {
	                r.put("code", 0);
	                r.put("msg", "邮箱已经存在");
	                r.put("isError", true);
	                return r;
	            }
            }
            if(StringUtils.isNotEmpty(employeeNumber)){
            	param.clear();
                param.put("employeeNumber", employeeNumber);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	r.put("code", 0);
                    r.put("msg", "工号已经存在");
                    r.put("isError", true);
                    return r;
                }
            }
            
            SsoUniformUserDTO user = new SsoUniformUserDTO();
            user.setLoginName(loginName);
            user.setSureName(sureName);
            user.setTelephoneNumber(telephoneNumber);
            user.setEmployeeNumber(employeeNumber);
            user.setEmail(email);
            user.setMobile(mobile);
            user.setSex(sex);
            user.setIdCard(idCard);
            user.setPositionName(positionName);
            user.setState(state);
            user.setEmployeeType(employeeType);
            user.setUnitId(organizationId);
            user.setOrganizationalUnitName(organizationalUnitName);
            user.setOrganizationCode(organizationCode);
            Date cud = new Date();
            user.setCreateTime(cud);
            user.setUpdateTime(cud);
            SsoAdminDTO manager = (SsoAdminDTO) request.getSession().getAttribute("loginUser");
            user.setCreateUserId(manager.getId());
            user.setCreateUser(manager.getSureName());
            String plaintextPwd = password;
            password = PasswordUtil.createPassword(password);
            user.setPassword(password);
            /*LOGGER.info("开始添加SsoUniformUser到mysql:"+user.toString());
            ssoUniformUserManager.add(user);
            LOGGER.info("添加SsoUniformUser到mysql成功:"+user.toString());*/
            LOGGER.info("开始添加SsoUniformUser:"+user.toString());
            try{
            	ssoUniformUserManager.addToMysqlAndLdap(user);
            	LOGGER.info("添加SsoUniformUser成功。"+user.toString());
            	
            	//发送消息到epp
            	try{
            	    BizConfigDTO bizconfig = new BizConfigDTO();
            	    Map<String,Object> p = new HashMap<String, Object>(3);
            	    p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
            	    List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
            	    if(bizList!=null && bizList.size()>0){
            	        String url = bizList.get(0).getSyncUserInfoUrl();
            	        if(StringUtils.isNotEmpty(url)){
            	            EppNotification notice = new EppNotification(user, EppConstants.ADD_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
                            EppNoticeTask.sendNoticeToEpp(notice);
            	        }
            	    }
            	}catch(Exception e){
            	    e.printStackTrace();
            	    LOGGER.error("调用通知EPP接口失败"+e.getMessage());
            	}
            }catch (ManagerException e){
            	LOGGER.error("添加SsoUniformUser失败。e="+e);
            	r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "添加失败");
                return r;
            }
            //发送短信给用户
            String content = properties.getSmsCreateNewAccountMsg();
			content = content.replace("{account}", user.getLoginName());
			content = content.replace("{password}", plaintextPwd);
            boolean f = this.sendPwd(content, mobile, "phone");
            if (f) {
                r.put("msg", "添加成功");
            } else {
                r.put("msg", "添加成功,但是短信发送失败");
            }
            r.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("isError", true);
            r.put("msg", "添加失败" + e.getMessage());
            LOGGER.error("添加ssoUser失败"+e.getMessage());
            return r;
        }
        
        return r;
    }

    @RequestMapping("/toUpdate")
    @ResponseBody
    public Map<String, Object> toUpdate(
            @RequestParam(value = "loginName", defaultValue = "", required = true) String loginName) {
        Map<String, Object> r = new HashMap<String, Object>(4);
        try {
            SsoUser user = ssoUserManager.findByPrimaryKey(loginName);
            if (user == null) {
                r.put("code", 0);
                r.put("msg", "loginName 不存在");
                r.put("isError", true);
                return r;
            }
            r.put("user", JSON.toJSONString(user));
            r.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "查找失败" + e.getMessage());
            LOGGER.error("查询ssoUser失败"+e.getMessage());
        }
        return r;
    }

    @RequestMapping("/update")
    @ResponseBody
	public Map<String,Object> update(
			@RequestParam(value="id",defaultValue="",required=true) Integer id,
			@RequestParam(value="loginName",defaultValue="",required=true) String loginName,
			@RequestParam(value="sureName",defaultValue="",required=true) String sureName,
			@RequestParam(value="employeeType",defaultValue="",required=false) String employeeType,
			@RequestParam(value="employeeNumber",defaultValue="",required=false) String employeeNumber,
			@RequestParam(value="email",defaultValue="",required=false) String email,
			@RequestParam(value="mobile",defaultValue="",required=false) String mobile,
			@RequestParam(value="password",defaultValue="",required=false) String password,
			@RequestParam(value="sex",defaultValue="",required=false) Integer sex,
			@RequestParam(value="idCard",defaultValue="",required=false) String idCard,
			@RequestParam(value = "positionName", defaultValue = "", required = false) String positionName,
			@RequestParam(value="state",defaultValue="",required=true) Integer state,
			@RequestParam(value="description",defaultValue="",required=false) String description,
			@RequestParam(value="organizationId",defaultValue="",required=false) Integer organizationId,
			@RequestParam(value="organizationCode",defaultValue="",required=false) String organizationCode,
			@RequestParam(value="organizationalUnitName",defaultValue="",required=false) String organizationalUnitName
			) {
    	Map<String,Object> r = new HashMap<String, Object>(4);
    	try{
	    	//修改mysql数据
	    	if(id==null){
	    		r.put("code", 0);
	    		r.put("msg", "ID 不能为空");
	    		r.put("isError", true);
	    		return r;
	    	}
	    	SsoUniformUserDTO model = new SsoUniformUserDTO();
	    	model.setId(id);
	    	SsoUniformUserDTO findById = ssoUniformUserManager.findById(model);
	    	if (findById==null) {
	    		r.put("code", 0);
	    		r.put("msg", "ID 不存在");
	    		r.put("isError", true);
	    		return r;
	    	}
	    	if(StringUtils.isEmpty(loginName)){
	    		r.put("code", 0);
	    		r.put("msg", "用户名不能为空");
	    		r.put("isError", true);
	    		return r;
	    	}
	    	Map<String,Object> param = new HashMap<String, Object>(2);
	        param.put("loginName", loginName);
	        List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
	        if (ssoUserList != null && ssoUserList.size()>0) {
	        	if(!StringUtils.equals(loginName, findById.getLoginName())){
		    		r.put("code", 0);
		    		r.put("msg", "用户名不允许修改");
		    		r.put("isError", true);
		    		return r;
	        	}
	    	}
	        if(StringUtils.isNotEmpty(employeeNumber)){
            	param.clear();
                param.put("employeeNumber", employeeNumber);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0 && ssoUserList.get(0).getId().intValue()!=findById.getId().intValue()) {
                	r.put("code", 0);
                    r.put("msg", "工号已经存在");
                    r.put("isError", true);
                    return r;
                }
            }
	        List<OrgUnit> list = null;
	        try {
				list = orgUnitManager.findByUnitCode(organizationCode);
				if(list==null || list.size()==0){
					r.put("code", 0);
		            r.put("msg", "机构代码错误，请重新选择");
		            r.put("isError", true);
		            return r;
				}
				organizationalUnitName = list.get(0).getFullName();
				organizationId = list.get(0).getUnitId();
			} catch (ManagerException e) {
				e.printStackTrace();
			}
	        SsoUniformUserDTO user = findById;
	    	if(StringUtils.isNotEmpty(sureName)){
	    		user.setSureName(sureName);
	    	}
	    	if(StringUtils.isNotEmpty(employeeNumber)){
	    		user.setEmployeeNumber(employeeNumber); 
	    	}
	    	if(StringUtils.isNotEmpty(description)){
	    		user.setDescription(description);
	    	}else{
	    		user.setDescription("");
	    	}
	    	user.setEmail(email);
	    	if(StringUtils.isNotEmpty(mobile)){
	    		if(!StringUtils.equals(user.getMobile(), mobile)){
	        		if (StringUtils.isNotEmpty(mobile)) {
	        			param.clear();
	                    param.put("mobile", mobile);
	                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
	                    if (ssoUserList != null && ssoUserList.size()>0) {
	                        r.put("code", 0);
	                        r.put("msg", "手机号码已经存在");
	                        r.put("isError", true);
	                        return r;
	                    }
	                    param.clear();
	                    param.put("mobile", loginName);
	                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
	                    if (ssoUserList != null && ssoUserList.size()>0 && !(ssoUserList.get(0).getId().intValue()==user.getId().intValue())) {
	                        r.put("code", 0);
	                        r.put("msg", "手机号码不能改成其它用户的用户名");
	                        r.put("isError", true);
	                        return r;
	                    }
	                    user.setMobile(mobile);
	                }
	    		}
	    	}else{
	    		 user.setMobile("");
	    	}
	    	if(StringUtils.isNotEmpty(employeeType)){
	    		user.setEmployeeType(employeeType);
	    	}else{
	    		user.setEmployeeType("");
	    	}
	    	if(sex!=null){
	    		user.setSex(sex);
	    	}
	    	if(StringUtils.isNotEmpty(idCard)){
	    		user.setIdCard(idCard);
	    	}else{
	    		user.setIdCard("");
	    	}
	    	if(StringUtils.isNotEmpty(positionName)) {
	    		user.setPositionName(positionName);
	    	}else {
	    		user.setPositionName("");
			}
	    	if(state!=null && state!=-1){
	    		user.setState(state);
	    	}
	    	if(StringUtils.isNotEmpty(organizationCode)){
	    		user.setOrganizationCode(organizationCode);
	    		organizationalUnitName = list.get(0).getFullName();
	    		user.setOrganizationalUnitName(organizationalUnitName);
	    		user.setUnitId(organizationId);
	    	}
	    	user.setUpdateTime(new Date());
	    	LOGGER.info("开始修改ssoUnitformUser:"+user);
	    	if(StringUtils.isEmpty(password)){
	    		ssoUniformUserManager.updateToMysqlAndLdap(user);
	    		r.put("msg", "修改成功");
	    		//发送消息到epp
                try{
                    BizConfigDTO bizconfig = new BizConfigDTO();
                    Map<String,Object> p = new HashMap<String, Object>(3);
                    p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                    List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
                    if(bizList!=null && bizList.size()>0){
                        String url = bizList.get(0).getSyncUserInfoUrl();
                        if(StringUtils.isNotEmpty(url)){
                            EppNotification notice = new EppNotification(user, EppConstants.UPDATE_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
                            EppNoticeTask.sendNoticeToEpp(notice);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    LOGGER.error("调用通知EPP接口失败"+e.getMessage());
                }
	    	}else{
	    		String content = properties.getSmsUpdatePasswordMsg();
  				content = content.replace("{account}", loginName);
  				content = content.replace("{password}", password);
				user.setPassword(PasswordUtil.createPassword(password));
				ssoUniformUserManager.updateToMysqlAndLdap(user);
				boolean sendResult = this.sendPwd(content, user.getMobile(), "phone");
				if (sendResult) {
					r.put("msg", "修改成功");
				} else {
					r.put("msg", "修改成功，但是短信发送失败。");
				}
				//发送消息到epp
                try{
                    BizConfigDTO bizconfig = new BizConfigDTO();
                    Map<String,Object> p = new HashMap<String, Object>(3);
                    p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                    List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
                    if(bizList!=null && bizList.size()>0){
                        String url = bizList.get(0).getSyncUserInfoUrl();
                        if(StringUtils.isNotEmpty(url)){
                            EppNotification notice = new EppNotification(user, EppConstants.UPDATE_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
                            EppNoticeTask.sendNoticeToEpp(notice);
                            EppNotification notice2 = new EppNotification(user, EppConstants.CHANGE_PSWD_NOTICE, url, properties.getEppSourceParameter(), null, null);
                            EppNoticeTask.sendNoticeToEpp(notice2);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    LOGGER.error("调用通知EPP接口失败"+e.getMessage());
                }
	    	}
	    	LOGGER.info("修改ssoUnitformUser成功:"+user);
    		r.put("code", 1);
    	}catch(Exception e){
    		e.printStackTrace();
    		r.put("code", 0);
    		r.put("isError", true);
    		r.put("msg","修改失败"+e.getMessage());
    		LOGGER.error("修改ssoUser失败"+e.getMessage());
    	}
		return r;
	}
    
    @RequestMapping("/del")
    @ResponseBody
    public Msg del(HttpServletRequest req) {
    	Msg msg = new Msg();
    	try{
	        String userIds = req.getParameter("ids");
	        String[] userIdList = userIds.split(",");
	        StringBuilder info = new StringBuilder();
	        LOGGER.info("开始删除统一登录账号，userIds="+userIds);
	        //循环对用户List进行操作
	        for (String userId : userIdList) {
	            try {
	            	SsoUniformUserDTO model = new SsoUniformUserDTO();
	    	    	model.setId(Integer.valueOf(userId.trim()));
	    	    	SsoUniformUserDTO findById = ssoUniformUserManager.findById(model);
	                if (findById == null) {
	                    msg.setStatus(Msg.EXISTERROR);
	                    info.append("删除" + userId + "失败，原因：用户不存在；");
	                    continue;
	                }
	    	        try{
	    	        	ssoUniformUserManager.logicDelete(findById);
	    	        	LOGGER.error("删除统一登录账号成功，userId="+userId);
	    	        	//发送消息到epp
	                    try{
	                        BizConfigDTO bizconfig = new BizConfigDTO();
	                        Map<String,Object> p = new HashMap<String, Object>(3);
	                        p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
	                        List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
	                        if(bizList!=null && bizList.size()>0){
	                            String url = bizList.get(0).getSyncUserInfoUrl();
	                            if(StringUtils.isNotEmpty(url)){
	                                EppNotification notice = new EppNotification(findById, EppConstants.DELETE_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
	                                EppNoticeTask.sendNoticeToEpp(notice);
	                            }
	                        }
	                    }catch(Exception e){
	                        e.printStackTrace();
	                        LOGGER.error("调用通知EPP接口失败"+e.getMessage());
	                    }
	    	        }catch (ManagerException e){
	    	        	LOGGER.error("删除统一登录账号失败，userId="+userId+",e="+e);
	    	        	info.append("删除" + userId + "失败。");
	    	        	msg.setStatus(Msg.FAILURE);
	    	        	continue;
	    	        }
	    	        //删除业务系统用户
	                String bizUserStr = findById.getBizUser();
	                JSONObject bizUser = (JSONObject) JSONObject.parse(bizUserStr);
	                Map<String, Object> params = new HashMap<String, Object>(1);
	                SimplePage page = new SimplePage(1, 50, 50);
	                List<BizConfigDTO> list = bizConfigManager.findByPage(page, null, null, params);
	                for (BizConfigDTO b : list) {
	                    String bizLoginName = bizUser.getString(b.getBizCode());
	                    if (StringUtils.isNotEmpty(bizLoginName)) {
	                        if (StringUtils.isNotEmpty(b.getDelUserUrl()) && b.getDelUserUrl().length()>"http://".length()) {
	                            Map<String, String> paramsMap = new HashMap<String, String>(7);
	                            paramsMap.put("ssoLoginName", findById.getLoginName());
	                            paramsMap.put("bizLoginName", bizLoginName);
	                            try {
	                            	LOGGER.info("开始调用删除业务系统账号api:"+b.getDelUserUrl());
	                                Map<String, Object> map = InvokeApiUtil.delBizUser(b.getDelUserUrl(), paramsMap,
	                                        b.getBizSecret());
	                                LOGGER.info("调用删除业务系统账号api结束:"+b.getDelUserUrl());
	                                int code = (int) map.get("code");
	                                if (code != 1) {
	                                    info.append("删除" + userId + "的业务系统" + b.getBizName() + "账号" + bizLoginName + "失败。");
	                                    LOGGER.error("删除" + userId + "的业务系统" + b.getBizName() + "账号" + bizLoginName + "失败。");
	                                }
	                            } catch (Exception e) {
	                                info.append("删除" + userId + "的业务系统" + b.getBizName() + "账号" + bizLoginName + "失败。");
	                                LOGGER.error("删除" + userId + "的业务系统" + b.getBizName() + "账号" + bizLoginName + "失败。");
	                            }
	                        }
	                    }
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                msg.setStatus(Msg.FAILURE);
	                info.append("删除" + userId + "失败，报错信息：" + e.getMessage());
	                LOGGER.error("删除" + userId + "失败，报错信息：" + e.getMessage());
	                continue;
	            }
	        }
	        if (Msg.SUCCESS.equals(msg.getStatus())) {
	            msg.setValue("统一账号删除成功。");
	        } else {
	            msg.setValue("删除出现失败，详情: " + info.toString());
	            LOGGER.info("删除出现失败，详情: " + info.toString());
	        }
    	}catch (Exception e){
    		e.printStackTrace();
    		LOGGER.error("批量删除SsoUser失败，报错信息：" + e.getMessage());
    	}
        return msg;
    }

    /**
     * 业务账号的列表--某用户绑定
     * @param loginName
     * @param pageSize
     * @param rows
     * @param pageNo
     * @return
     */
    @RequestMapping("/bindBizUserList")
    @ResponseBody
    public Map<String, Object> bindBizUserList(
    		@RequestParam(value="id",defaultValue="",required=true) Integer userId,
            @RequestParam(value = "loginName", defaultValue = "", required = true) String loginName,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "rows", defaultValue = "10", required = false) int rows,
            @RequestParam(value = "page", defaultValue = "1", required = false) int pageNo) {

        PageResult page = new PageResult();
        page.setPageNo(pageNo);
        page.setPageSize(rows);
        int total = 0;
        Map<String, Object> r = new HashMap<String, Object>(3);
        try {
        	SsoUniformUserDTO model = new SsoUniformUserDTO();
	    	model.setId(userId);
	    	SsoUniformUserDTO user = ssoUniformUserManager.findById(model);
            if (user == null) {
            	r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "用户不存在");
                return r;
            }
	    	
            List<Object> list = new ArrayList<Object>();
            String bizUser = user.getBizUser();
            if (StringUtils.isNotEmpty(bizUser)) {
                JSONObject bizUJ = JSONObject.parseObject(bizUser);
                int index = 0;
                int from = (pageNo-1)*rows + 1;
                int to = from + rows;
                for (Entry<String, Object> entry : bizUJ.entrySet()) {
                	index++;
                	if(index<from || index>=to){
                		total++;
                		continue;
                	}
                	total++;
                    String bizcode = entry.getKey();
                    BizConfigDTO p = new BizConfigDTO();
                    Map<String,Object> param = new HashMap<String, Object>(7);
                    param.put("bizCode", bizcode);
                    List<BizConfigDTO> list2 = bizConfigManager.findByBiz(p, param);
                    if (list2!=null && list2.size()>0) {
                    	BizConfigDTO bizConfig = list2.get(0);
                        JSONObject obj = new JSONObject();
                        obj.put("loginName", user.getLoginName());
                        obj.put("bizName", bizConfig.getBizName());
                        obj.put("bizCode", bizcode);
                        obj.put("sureName", user.getSureName());
                        String ln = (String) entry.getValue();
                        obj.put("bizLoginName", ln);
                        list.add(obj);
                    }
                }
            }
            //page.setRows(list);
            r.put("total", total);
            r.put("rows", list);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询ssoUser绑定列表失败"+e.getMessage());
        }
        return r;
    }

    /**
     * 绑定业务账号
     * @param userId
     * @param bizId
     * @param realName
     * @param bizLoginName
     * @param bizPwd
     * @return
     */
    @RequestMapping("/bindBizUser")
    @ResponseBody
    public Map<String, Object> bindBizUser(
            @RequestParam(value = "id", defaultValue = "", required = false) Integer userId,
            @RequestParam(value = "bizId", defaultValue = "", required = false) Integer bizId,
            @RequestParam(value = "bizLoginName", defaultValue = "", required = false) String bizLoginName,
            @RequestParam(value = "bizPwd", defaultValue = "", required = false) String bizPwd) {
        Map<String, Object> r = new HashMap<String, Object>(4);
        try {

            BizConfigDTO bizConfig = new BizConfigDTO();
            bizConfig.setId(bizId);
            bizConfig = bizConfigManager.findById(bizConfig);
            if (bizConfig == null) {
                r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "业务系统代码错误,系统中不存在该业务系统");
                return r;
            }

            SsoUniformUserDTO model = new SsoUniformUserDTO();
	    	model.setId(userId);
	    	SsoUniformUserDTO user = ssoUniformUserManager.findById(model);
            if (user == null) {
            	r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "用户不存在");
                return r;
            }
            String bizUser = user.getBizUser();
            JSONObject bizUJ = JSONObject.parseObject(bizUser);
            if (bizUJ != null && bizUJ.get(bizConfig.getBizCode()) != null) {
                r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "已绑定该系统账户，请先解绑再绑定");
                return r;
            }

            //检查账号是否已经绑定过sso账户
            int total = ssoUniformUserManager.findByBizUser(bizConfig.getBizCode(), bizLoginName);
            if (total>0) {
                r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "业务系统账户已经绑定其它SSO账户");
                return r;
            }

            //调用业务系统验证用户名密码接口验证用户是否存在
            Map<String, String> paramsMap = new HashMap<String, String>(7);
            paramsMap.put("loginName", bizLoginName);
            String encryptPwd = AESUtil.encode(bizPwd, bizConfig.getBizSecret());
            paramsMap.put("password", encryptPwd);
            paramsMap.put("bizCode", bizConfig.getBizCode());
            Map<String, Object> map = InvokeApiUtil.checkBizUserPwd(bizConfig.getVerifyUserPwdUrl(), paramsMap,
                    bizConfig.getBizSecret());
            int code = (int) map.get("code");
            if (code != 1) {
                r.put("code", 0);
                r.put("isError", true);
                r.put("msg", map.get("msg") == null ? "校验biz账户时出错" : (String) map.get("msg"));
                return r;
            }

            bizUJ.put(bizConfig.getBizCode(), bizLoginName);
            user.setBizUser(bizUJ.toJSONString());
            user.setUpdateTime(new Date());
            
            //修改绑定关系
            LOGGER.info("开始修改用户绑定关系..."+user);
            user.setPassword(null);
            ssoUniformUserManager.updateToMysqlAndLdap(user);
            LOGGER.info("修改用户绑定关系成功。");
            r.put("code", 1);
            r.put("msg", "绑定成功");
            
            //发送消息到epp
            try{
                BizConfigDTO bizconfig = new BizConfigDTO();
                Map<String,Object> p = new HashMap<String, Object>(3);
                p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
                if(bizList!=null && bizList.size()>0){
                    String url = bizList.get(0).getSyncUserInfoUrl();
                    if(StringUtils.isNotEmpty(url)){
                        EppNotification notice = new EppNotification(user, EppConstants.BIND_LOGINNAME_NOTICE, url, properties.getEppSourceParameter(), bizConfig.getBizCode(), bizLoginName);
                        EppNoticeTask.sendNoticeToEpp(notice);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("调用通知EPP接口失败"+e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("isError", true);
            r.put("msg", "绑定失败");
            LOGGER.error("ssoUser绑定业务系统失败"+e.getMessage());
        }
        return r;
    }

    /**
     * 解绑业务账号
     * @param loginName
     * @param bizCodes
     * @return
     */
    @RequestMapping("/unBindBizUser")
    @ResponseBody
    public Map<String, Object> unBindBizUser(
    		@RequestParam(value = "id", defaultValue = "", required = false) String userId,
            @RequestParam(value = "bizCodes", defaultValue = "", required = true) String bizCodes) {
        Map<String, Object> r = new HashMap<String, Object>(4);
        try {
        	SsoUniformUserDTO model = new SsoUniformUserDTO();
	    	model.setId(Integer.valueOf(userId.trim()));
	    	SsoUniformUserDTO user = ssoUniformUserManager.findById(model);
            if (user == null) {
            	r.put("code", 0);
                r.put("isError", true);
                r.put("msg", "用户不存在");
                return r;
            }
            Map<String,String> bizCodeBizName = new HashMap<String, String>(10);
            String bizUser = user.getBizUser();
            String[] bizCodeArr = bizCodes.split(",");
            JSONObject bizUJ = JSONObject.parseObject(bizUser);
            for (String bizCode : bizCodeArr) {
                if (bizUJ != null && bizUJ.get(bizCode) != null) {
                	bizCodeBizName.put(bizCode, (String) bizUJ.get(bizCode));
                    bizUJ.remove(bizCode);
                }
            }
            if (bizUJ != null) {
                user.setBizUser(bizUJ.toJSONString());
            }
            user.setUpdateTime(new Date());
            //修改绑定关系
            LOGGER.info("开始修改用户绑定关系..."+user);
            user.setPassword(null);
            ssoUniformUserManager.updateToMysqlAndLdap(user);
            LOGGER.info("修改用户绑定关系成功。");
            r.put("code", 1);
            r.put("msg", "解绑成功");
            
            //发送消息到epp
            try{
                BizConfigDTO bizconfig = new BizConfigDTO();
                Map<String,Object> p = new HashMap<String, Object>(3);
                p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
                if(bizList!=null && bizList.size()>0){
                    String url = bizList.get(0).getSyncUserInfoUrl();
                    if(StringUtils.isNotEmpty(url)){
                        bizUJ = JSONObject.parseObject(bizUser);
                        for (String bizCode : bizCodeArr) {
                            EppNotification notice = new EppNotification(user, EppConstants.UNBIND_LOGINNAME_NOTICE, url, properties.getEppSourceParameter(), bizCode, (String) bizUJ.get(bizCode));
                            EppNoticeTask.sendNoticeToEpp(notice);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("调用通知EPP接口失败"+e.getMessage());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("isError", true);
            r.put("msg", "解绑失败");
            LOGGER.error("解绑ssoUser时失败"+e.getMessage());
        }
        return r;
    }

    /**
     * 生成密码---界面按钮
     * @return
     */
    @RequestMapping("/getPassword")
    @ResponseBody
    public Map<String, Object> getPassword() {
        Map<String, Object> r = new HashMap<String, Object>(3);
        try {
            String pwd = RandomStringUtil.getRandomCode2(6, 1);
            r.put("code", 1);
            r.put("pwd", pwd);
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "生成失败");
        }
        return r;
    }

    /**
     * 发送验密码到手机或邮箱
     * @param loginName
     * @param pwd
     * @param phoneOrEmail
     * @param sendType 
     * @return
     */
    private boolean sendPwd(String content, String phoneOrEmail, String sendType) {
        boolean flag = false;
        try {
            switch (sendType) {
	            case "phone": {
	                HttpSMSSend smsSend = smsEmailValidateService.getSmsSend();
	                smsSend.setReceivePhones(phoneOrEmail);
	                smsSend.setBusinessSystemCode(properties.getSmsBusinessSystemCode());
	                smsSend.setSenderName("SSO-ADMIN");
	                smsSend.setPriority(0);
	                smsSend.setSenderName("一账通");
	                // 发送内容，随机码，时间限制
	                smsSend.setContent(content);
	                flag = smsEmailValidateService.sendSms(smsSend);
	                LOGGER.info("发送短信到"+phoneOrEmail+",内容："+content+",状态："+flag);
	                break;
	            }
	            case "email": {
	                HttpEmailSend emailSend = smsEmailValidateService.getEmailSend();
	                emailSend.setBusinessSystemCode(properties.getSmsBusinessSystemCode());
	                emailSend.setMainAddr(phoneOrEmail);
	                emailSend.setPriority(0);
	                emailSend.setEmailMsgType(41);
	                emailSend.setSubject("一账通");
	                emailSend.setContent(content);
	                flag = smsEmailValidateService.sendEmail(emailSend);
	                LOGGER.info("发送邮件到"+phoneOrEmail+",内容："+content+",状态："+flag);
	                break;
	            }
	            default:{
	                HttpSMSSend smsSend = smsEmailValidateService.getSmsSend();
	                smsSend.setReceivePhones(phoneOrEmail);
	                smsSend.setBusinessSystemCode(properties.getSmsBusinessSystemCode());
	                smsSend.setSenderName("SSO-ADMIN");
	                smsSend.setPriority(0);
	                smsSend.setSenderName("一账通");
	                // 发送内容，随机码，时间限制
	                smsSend.setContent(content);
	                flag = smsEmailValidateService.sendSms(smsSend);
	                LOGGER.info("发送短信到"+phoneOrEmail+",内容："+content+",状态："+flag);
	                break;
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("发送失败"+e.getMessage());
        }
        return flag;
    }
    
    @RequestMapping("/lockOrUnlock")
    @ResponseBody
    public Msg lockOrUnlock(
    		@RequestParam(value = "ids", defaultValue = "", required = false) String userIds,
            @RequestParam(value = "flag", defaultValue = "", required = true) String flag) {
        Msg msg = new Msg();
        String[] userIdList = userIds.split(",");
        StringBuilder info = new StringBuilder();
        String operation  = (LOCK_FLAG.equals(flag)?"锁定":"解锁");
        //循环对用户List进行操作
        for (String userId : userIdList) {
        	try{
        		SsoUniformUserDTO model = new SsoUniformUserDTO();
    	    	model.setId(Integer.valueOf(userId.trim()));
    	    	SsoUniformUserDTO user = ssoUniformUserManager.findById(model);
                if (user == null) {
                    msg.setStatus(Msg.EXISTERROR);
                    info.append(operation + userId + "失败，原因：用户不存在；");
                    continue;
                }
                user.setState(LOCK_FLAG.equals(flag)?LOCK_STATE:UNLOCK_STATE);
                user.setUpdateTime(new Date());
                LOGGER.info("开始修改ssoUnitformUser绑定状态:"+user);
                //修改状态
                user.setPassword(null);
                ssoUniformUserManager.updateToMysqlAndLdap(user);
                LOGGER.info("修改ssoUnitformUser绑定状态成功。");
        	}catch(Exception e){
        		msg.setStatus(Msg.FAILURE);
        		info.append(operation + userId + "失败；"+e.getMessage());
        		LOGGER.error(operation + userId + "失败；"+e.getMessage());
        	}
        }
        if (Msg.SUCCESS.equals(msg.getStatus())) {
            msg.setValue(operation+"用户成功。");
        } else {
            msg.setValue(operation+"用户出现失败，详情: " + info.toString());
            LOGGER.info(operation+"用户出现失败，详情: " + info.toString());
        }    	
        return msg;
    }

    /**
     * 同步hr用户信息
     * @param employeeNumber
     * @return
     */
    @RequestMapping("/syncHrUser")
    @ResponseBody
    public Map<String, Object> syncHrUser(
    		HttpServletRequest req,
    		@RequestParam(value = "employeeNumber", defaultValue = "", required = true) String employeeNumber
    		) {
        Map<String, Object> r = new HashMap<String, Object>(3);
        String key = employeeNumber+"_syncHrUser";
        try {
        	if(StringUtils.isEmpty(employeeNumber)){
        		r.put("code", 0);
                r.put("msg", "工号不能为空");
                r.put("isError", true);
                return r;
        	}
        	//保存此次同步请求
        	Object employeeNumberSession = redisUtil.get(key);
        	if(employeeNumberSession!=null){
        		r.put("code", 0);
                r.put("msg", "正在处理上一次同步请求,请勿重复提交");
                r.put("isError", true);
                return r;
        	}else{
        		redisUtil.set(key, employeeNumber, 5*60L);
        	}
        	LOGGER.info("开始同步hr用户信息...");
        	SsoUniformUserDTO model = new SsoUniformUserDTO();
            Map<String,Object> param = new HashMap<String, Object>(2);
            param.put("loginName", employeeNumber);
            List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
            if (ssoUserList != null && ssoUserList.size()>0) {
            	r.put("code", 0);
            	r.put("isError", true);
                r.put("msg", "工号已经存在");
                redisUtil.remove(key);
                LOGGER.info("同步hr用户信息失败，用户名已经存在");
                return r;
            }
            param.clear();
            param.put("employeeNumber", employeeNumber);
            ssoUserList = ssoUniformUserManager.findByBiz(model, param);
            if (ssoUserList != null && ssoUserList.size()>0) {
            	r.put("code", 0);
            	r.put("isError", true);
                r.put("msg", "工号已经存在");
                redisUtil.remove(key);
                LOGGER.info("同步hr用户信息失败，工号已经存在");
                return r;
          	}
        	//调用hr的同步接口获取用户信息
        	String hrBizCode = ApiConstants.SSOADMIN_HR_BIZ_CODE;
        	BizConfigDTO config = new BizConfigDTO();
        	config.setBizCode(hrBizCode);
        	param.clear();
        	param.put("bizCode", hrBizCode);
        	List<BizConfigDTO> list = bizConfigManager.findByBiz(config, param);
        	if(list==null || list.size()==0){
        		r.put("code", 0);
        		r.put("isError", true);
                r.put("msg", "HR系统的bizCode错误");
                redisUtil.remove(key);
                LOGGER.info("同步hr用户信息失败，HR系统的bizCode错误");
                return r;
        	}
        	BizConfigDTO bizConfig = list.get(0);
        	if(StringUtils.isEmpty(bizConfig.getSyncUserInfoUrl())){
        		r.put("code", 0);
        		r.put("isError", true);
                r.put("msg", "HR系统的同步数据URL错误");
                redisUtil.remove(key);
                LOGGER.info("同步hr用户信息失败，HR系统的同步数据URL错误");
                return r;
        	}
            
            Map<String, String> paramsMap = new HashMap<String, String>(7);
            paramsMap.put("employeeCode", employeeNumber);
            paramsMap.put("bizCode", bizConfig.getBizCode());
            Map<String, Object> map = InvokeApiUtil.getEmployeeInfo(bizConfig.getSyncUserInfoUrl(), paramsMap, bizConfig.getBizSecret());
            int code = (int) map.get("code");
            if (code != 1) {
                r.put("code", 0);
                r.put("isError", true);
                r.put("msg", map.get("msg") == null ? "同步HR用户信息时出错" : (String) map.get("msg"));
                redisUtil.remove(key);
                LOGGER.info("同步hr用户信息失败，同步HR用户信息时出错 :" + (String) map.get("msg"));
                return r;
            }
            JSONObject jsonObj = (JSONObject) map.get("json");
            JSONArray data = jsonObj.getJSONArray("data");
            if(data!=null && data.size()>0){
            	Object object = data.get(0);
            	JSONObject jsonObject = (JSONObject) object;
            	if(StringUtils.equals("{}", jsonObject.toJSONString())){
            		LOGGER.error("同步hr失败，原因：没有该用户数据");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr失败，原因：没有该用户数据");
                    redisUtil.remove(key);
                    return r;
                }
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
                
                //只能同步权限范围内的工号，如果查询工号在权限范围外，提示“暂未查询到该数据”
                int adminType = (int) req.getSession().getAttribute("adminType");
                if(adminType!=ApiConstants.SSOADMIN_SYSTEM_ROLE){
                	boolean flag = false;
                	List<String> orgUnitList = (List<String>) req.getSession().getAttribute("orgUnitLimitList");
                	for (String orgCode : orgUnitList) {
						if(CommonUtil.isPrefix(orgCode, unitCode)){
							flag = true;
							break;
						}
					}
                	if(!flag){
                		r.put("code", 0);
                		r.put("isError", true);
                        r.put("msg", "暂未查询到该数据");
                        redisUtil.remove(key);
                        LOGGER.info("同步hr用户信息失败，原因：暂未查询到该数据");
                        return r;
                	}
                }
                
                //-1：临时 0: 预入职 1:试用 2:转正 3:放弃入职 4:离职 5:离职在途 6:退休 7:试工不合格 8:实习
                Integer employeeStatus = jsonObject.getInteger("employeeStatus");
                if(employeeStatus==3){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:放弃入职");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:放弃入职");
                    redisUtil.remove(key);
                    return r;
                }
                if(employeeStatus==4){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:离职");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:离职");
                    redisUtil.remove(key);
                    return r;
                }
                if(employeeStatus==7){
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:试工不合格");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户状态为:试工不合格");
                    redisUtil.remove(key);
                    return r;
                }
                //1：辞职(主动) 2：辞职(被动) 3：辞退
                Integer leaveType = jsonObject.getInteger("leaveType");
                //登录账户
                String userNo = jsonObject.getString("userNo");
                String password = jsonObject.getString("password");
                List<OrgUnit> orgUnitList = null;
            	if(StringUtils.isEmpty(employeeCode)){
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"为空");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"为空");
                    redisUtil.remove(key);
                    return r;
              	}
				try {
					orgUnitList = orgUnitManager.findByUnitCode(unitCode);
				} catch (ManagerException e1) {
					e1.printStackTrace();
				}
				if(orgUnitList==null||orgUnitList.size()==0){
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：所在组织机构"+unitCode+"为空");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：所在组织机构"+unitCode+"为空");
                    redisUtil.remove(key);
                    return r;
              	}
				param.clear();
                param.put("loginName", employeeCode);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"在sso系统中已经存在");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"在sso系统中已经存在");
                    redisUtil.remove(key);
                    return r;
              	}
                param.clear();
                param.put("mobile", employeeCode);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户名不能是其它用户的手机号码");
                	r.put("code", 0);
                	r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：用户名不能是其它用户的手机号码");
                    redisUtil.remove(key);
              		return r;
                }
                param.clear();
                param.put("employeeNumber", employeeCode);
                ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                if (ssoUserList != null && ssoUserList.size()>0) {
                	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"在sso系统中已经存在");
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因：工号"+employeeCode+"在sso系统中已经存在");
                    redisUtil.remove(key);
                    return r;
                }
                
                if(StringUtils.isNotEmpty(phoneNo)){
                	param.clear();
                    param.put("mobile", phoneNo);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号时手机号码已经存在，修改为空");
                    	phoneNo = null;
                  	}
                }else{
                	phoneNo = null;
                }
                if(StringUtils.isNotEmpty(workMail)){
                	param.clear();
                    param.put("email", workMail);
                    ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                    if (ssoUserList != null && ssoUserList.size()>0) {
                    	LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号时邮箱已经存在，修改为空");
                    	workMail = null;
                  	}
                }else{
                	workMail = null;
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
              		OrgUnit org = orgUnitList.get(0);
              		SsoUniformUserDTO user = new SsoUniformUserDTO();
                  	user.setLoginName(employeeCode);
                  	user.setSureName(employeeName);
                  	user.setTelephoneNumber(phoneNo);
                  	user.setEmployeeNumber(employeeCode);
                  	user.setEmail(workMail);
                  	user.setMobile(phoneNo);
                  	user.setSex(sex==0?1:0);
                  	user.setState(ApiConstants.SSOUSER_NORMAL_STATE_VALUE);
                  	user.setOrganizationalUnitName(org.getFullName());
                  	user.setOrganizationCode(org.getUnitCode());
                  	user.setUnitId(org.getUnitId());
                  	user.setCreateTime(new Date());
                  	user.setUpdateTime(new Date());
              		user.setCreateUser(hrBizCode);
              		String bizUser = "{\""+hrBizCode+"\":\""+user.getLoginName()+"\"}";
              		user.setBizUser(bizUser);
              		user.setIdCard(idCard);
              		String plaintextPwd = "";
                    if(StringUtils.isEmpty(idCard)){
                      plaintextPwd = RandomStringUtil.getRandomCode2(6, 1);
                      password = PasswordUtil.createPassword(plaintextPwd);
                    }else{
                      plaintextPwd = idCard.substring(idCard.length()-6);
                      password = PasswordUtil.createPassword(plaintextPwd);
                    }
                    user.setPassword(password);
              		user.setPositionName(positionName);
              		
              		LOGGER.info("开始同步添加统一登录账号到sso系统。。。"+user);
              		ssoUniformUserManager.addToMysqlAndLdap(user);
              		LOGGER.info("同步添加统一登录账号到sso系统成功");
              		
          			r.put("code", 1);
                    r.put("msg", "同步成功");
          			try{
          				if(StringUtils.equalsIgnoreCase(properties.getSendMsgPhoneFromHrSyncSwitch(), "on")){
          					//发送短信给用户
                			String content = "您的一账通账号："+user.getLoginName()+"注册成功，初始密码是："+plaintextPwd;
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
              		}catch(Exception e){ 
              			LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号成功，但是发送短信失败"+e.getMessage());
              			r.put("code", 0);
              			r.put("isError", true);
                        r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号成功，但是发送短信失败");
                        redisUtil.remove(key);
                        return r;
              		}
          			//发送消息到epp
                    try{
                        BizConfigDTO bizconfig = new BizConfigDTO();
                        Map<String,Object> p = new HashMap<String, Object>(3);
                        p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
                        List<BizConfigDTO> bizList = bizConfigManager.findByBiz(bizconfig, p);
                        if(bizList!=null && bizList.size()>0){
                            String url = bizList.get(0).getSyncUserInfoUrl();
                            if(StringUtils.isNotEmpty(url)){
                                EppNotification notice = new EppNotification(user, EppConstants.ADD_SSO_USER_NOTICE, url, properties.getEppSourceParameter(), null, null);
                                EppNoticeTask.sendNoticeToEpp(notice);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("调用通知EPP接口失败"+e.getMessage());
                    }
              	}catch(Exception e){
              		LOGGER.error("同步hr用户"+employeeName+"，添加统一登录账号失败，原因："+e.getMessage());
              		r.put("code", 0);
              		r.put("isError", true);
                    r.put("msg", "同步hr用户"+employeeName+"，添加统一登录账号失败，原因："+e.getMessage());
                    redisUtil.remove(key);
                    return r;
              	}
              	r.put("isError", false);
              	LOGGER.info("同步hr用户"+employeeName+"，添加统一登录账号成功");
            }else{
            	LOGGER.error("同步hr失败，原因：没有该用户数据");
          		r.put("code", 0);
          		r.put("isError", true);
                r.put("msg", "同步hr失败，原因：没有该用户数据");
                redisUtil.remove(key);
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "同步hr用户信息失败");
        }finally{
            redisUtil.remove(key);
        }
        return r;
    }
    

    @RequestMapping("/syncAllHrUser")
    @ResponseBody
    public Map<String, Object> syncAllHrUser() {
    	LOGGER.info("开始调用全量同步hr用户信息任务...");
        Map<String, Object> r = new HashMap<String, Object>(3);
        try {
        	//先删除旧的定时任务
        	List<ScheduleJob> allJob = jobTaskService.getAllJob();
        	for (ScheduleJob scheduleJob : allJob) {
				if("syncHR".equals(scheduleJob.getJobGroup()) && "allHRDataNow".equals(scheduleJob.getJobName())){
					jobTaskService.deleteJob(scheduleJob);
				}
			}
        	//再添加新的定时任务
        	String f = "s m H d M ? yyyy";
        	//推迟一分钟执行
    		String corn = DateUtil.getDateFormat(new Date(System.currentTimeMillis()+60*1000), f);
        	ScheduleJob job = new ScheduleJob();
    		job.setJobGroup("syncHR");
    		job.setJobName("allHRDataNow");
    		job.setCronExpression(corn);
    		job.setSpringId("syncSsoUserTask");
    		job.setMethodName("syncSsoUserFromHr");
    		job.setIsConcurrent(ScheduleJob.CONCURRENT_NOT);
    		job.setJobStatus(ScheduleJob.STATUS_RUNNING);
    		job.setDescription("立即全量同步HR用户数据");
        	jobTaskService.addJob(job);
        	r.put("code", 1);
            r.put("msg", "调用全量同步hr用户信息任务成功");
        	LOGGER.info("调用全量同步hr用户信息任务成功");
        	
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "调用全量同步hr用户信息任务失败");
            LOGGER.info("调用全量同步hr用户信息任务失败,e="+e);
        }
        return r;
    }
    
    @RequestMapping("/upload")
    public String upload() throws Exception {
        return "upload_pwd";
    }
    
    @RequestMapping("/resetPwd")  
    @ResponseBody
    public Map<String, Object> resetPwd(HttpServletRequest request){
    	Map<String, Object> r = new HashMap<String, Object>();
    	int success = 0;
    	int failures = 0;
    	StringBuilder failureLoginNames = new StringBuilder();
    	CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getSession().getServletContext());  
        if (cmr.isMultipart(request)) {  
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) (request);  
            Iterator<String> filelist = mRequest.getFileNames();  
            while (filelist.hasNext()) {  
                MultipartFile mFile = mRequest.getFile(filelist.next());  
                /*if (mFile != null) {  
                    String fileName = UUID.randomUUID()  
                            + mFile.getOriginalFilename();  
                    String path = "d:/" + fileName;  
  
                    File localFile = new File(path);  
                    try {
						mFile.transferTo(localFile);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
                    request.setAttribute("fileUrl", path);  
                } */ 
                String loginName = "";
            	String idCard = "";
                try {  
                    //拿到上传文件的输入流  
                    //FileInputStream in = (FileInputStream) mFile.getInputStream();
                    XSSFWorkbook wb = new XSSFWorkbook(mFile.getInputStream());
                    XSSFSheet sheet = wb.getSheetAt(0);
                    int l = -1;
                    
                    for (Row row : sheet) {
                    	l++;
                    	if(l==0) {
                    		continue;
                    	}
                    	for (int j=0;j<row.getLastCellNum();j++) {
                    		Cell cell = row.getCell(j);
                        	String content = "";
                        	if(cell!=null){
            	            	cell.setCellType(Cell.CELL_TYPE_STRING);
            	            	content = cell.getRichStringCellValue().getString();
            	            	content = ExcelToLdifUtil.replaceBlank(content);
                        	}
                        	if(j==0){ 
                        		loginName = content;
                        	}
                        	if(j==1){
                        		idCard = content;
                        	}
                    	}
                    	if(!StringUtils.isEmpty(loginName)){
                    		Map<String,Object> param = new HashMap<String, Object>(2);
                            param.put("loginName", loginName);
                            SsoUniformUserDTO model = new SsoUniformUserDTO();
                            List<SsoUniformUserDTO> ssoUserList = ssoUniformUserManager.findByBiz(model, param);
                            if (ssoUserList != null && ssoUserList.size()>0) {
                               model = ssoUserList.get(0);
                               if(StringUtils.isEmpty(idCard)){
                            	   failureLoginNames.append(loginName+",");
                            	   failures++;
                               }else{
    	                           String pwd = PasswordUtil.createPassword(StringUtils.trim(idCard));
    	                           model.setPassword(pwd);
    	                           ssoUniformUserManager.updateToMysqlAndLdap(model);
    	                           success++;
                               }
                            }
                    	}
                        idCard = "";
                        loginName = "";
                    }
                } catch (Exception e) {  
                    e.printStackTrace();  
                    System.out.println("上传出错");  
                    failures++;
                    failureLoginNames.append(loginName+",");
                } 
            }  
        }  
        String msg = "修改成功"+success+"个，失败"+failures+"个。";
        if(failures>0){
        	msg += "修改失败用户为："+failureLoginNames.toString();
        }
    	r.put("msg", msg);
        return r;  
    }  
    
    //标题样式
    public static CellStyle getHeader(SXSSFWorkbook workbook){
        CellStyle format = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);  //加粗
        font.setFontName("黑体");
        font.setFontHeightInPoints((short)16);
        format.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        format.setAlignment(XSSFCellStyle.ALIGN_CENTER);    
        format.setFont(font);
        return format;
    }
    
    //内容样式
    public static CellStyle getContext(SXSSFWorkbook workbook){
        CellStyle format = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        format.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        format.setAlignment(XSSFCellStyle.ALIGN_CENTER);    
        format.setFont(font);
        return format;
    }
}