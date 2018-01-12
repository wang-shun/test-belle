package com.wonhigh.bs.sso.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.util.BizSecretGenerator;
import com.wonhigh.bs.sso.admin.common.util.Msg;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.admin.manager.BizConfigManager;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;
import com.yougou.logistics.base.web.controller.BaseCrud4RestfulController;

/**
 * 
 * TODO: 业务系统配置--控制层
 * 
 * @author zhang.rq
 * @date 2017年11月7日 下午2:15:58
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/biz_config")
public class BizConfigController extends BaseCrud4RestfulController<BizConfigDTO> {

    @Resource
    private BizConfigManager bizConfigManager;
    
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public CrudInfo init() {
        return new CrudInfo("bizConfig/", bizConfigManager);
    }

    @RequestMapping("/index")
    public String index(ModelMap model) throws Exception {
        return "/bizConfig/index";
    }

    @Override
    @RequestMapping(value = "/list.json")
    @ResponseBody
    public Map<String, Object> queryList(HttpServletRequest req, Model model) throws ManagerException {
        int pageNo = StringUtils.isEmpty(req.getParameter("page")) ? 1 : Integer.parseInt(req.getParameter("page"));
        int pageSize = StringUtils.isEmpty(req.getParameter("rows")) ? 10 : Integer.parseInt(req.getParameter("rows"));
        String sortColumn = StringUtils.isEmpty(req.getParameter("sort")) ? ""
                : String.valueOf(req.getParameter("sort"));
        String sortOrder = StringUtils.isEmpty(req.getParameter("order")) ? ""
                : String.valueOf(req.getParameter("order"));
        Map<String, Object> params = builderParams(req, model);

        int total = this.bizConfigManager.findCount(params);
        SimplePage page = new SimplePage(pageNo, pageSize, (int) total);
        List<BizConfigDTO> list = this.bizConfigManager.findByPage(page, sortColumn, sortOrder, params);
        Map<String, Object> obj = new HashMap<String, Object>(3);
        obj.put("total", total);
        obj.put("rows", list);
        return obj;
    }
    
    @RequestMapping("/all.json")
    @ResponseBody
    public String allBizConfig(
    		) throws Exception {
    	JSONArray json = new JSONArray();
        try {
            Map<String, Object> params = new HashMap<String, Object>(3);
            SimplePage page = new SimplePage(1, 100, 100);
            List<BizConfigDTO> list = bizConfigManager.findByPage(page, null, null, params);
            for (BizConfigDTO biz : list) {
				JSONObject obj = new JSONObject();
				obj.put("id", biz.getId());
				obj.put("bizName", biz.getBizName());
				json.add(obj);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toJSONString();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Msg add(@ModelAttribute BizConfigDTO bizdto) throws ManagerException {
        Msg msg = new Msg();
        try{
	        Map<String, Object> param = new HashMap<String, Object>(3);
	        param.put("bizCode", bizdto.getBizCode());
	
	        int total = this.bizConfigManager.findCount(param);
	        if (total > 0) {
	            msg.setStatus(Msg.EXISTERROR);
	            msg.setValue("【bizCode】 编码code已经存在");
	            return msg;
	        }
	
	        param.clear();
	        param.put("bizName", bizdto.getBizName());
	        total = this.bizConfigManager.findCount(param);
	        if (total > 0) {
	            msg.setStatus(Msg.EXISTERROR);
	            msg.setValue("【bizName】 编码名已经存在");
	            return msg;
	        }
	
	        bizdto.setBizSecret(BizSecretGenerator.generateKey());
	        bizConfigManager.add(bizdto);
	        msg.setStatus(Msg.SUCCESS);
	        msg.setValue("添加成功");
	        
        }catch (Exception e){
        	e.printStackTrace();
        	LOGGER.error("添加业务系统配置报错，e="+e);
        }
        return msg;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Msg update(@ModelAttribute BizConfigDTO bizdto) throws ManagerException {
        Msg msg = new Msg();
        try{
	        BizConfigDTO p = new BizConfigDTO();
	        p.setId(bizdto.getId());
	        BizConfigDTO configDTO = this.bizConfigManager.findById(p);
	        BizConfigDTO old = configDTO;
	        if (configDTO == null) {
	            msg.setStatus(Msg.NOTEXISTERROR);
	            msg.setValue("【bizCode】 不存在");
	            return msg;
	        }
	
	        if (StringUtils.isNotEmpty(bizdto.getBizName())) {
	            configDTO.setBizName(bizdto.getBizName());
	        }
	        configDTO.setEmail(bizdto.getEmail());
	        if (StringUtils.isNotEmpty(bizdto.getVerifyUserPwdUrl())) {
	            configDTO.setVerifyUserPwdUrl(bizdto.getVerifyUserPwdUrl());
	        }
	        configDTO.setDelUserUrl(bizdto.getDelUserUrl());
	        configDTO.setLoginUrl(bizdto.getLoginUrl());
	        configDTO.setDescription(bizdto.getDescription());
	        configDTO.setSyncUserInfoUrl(bizdto.getSyncUserInfoUrl());
	        bizConfigManager.modifyById(configDTO);
	        msg.setStatus(Msg.SUCCESS);
	        msg.setValue("修改成功");
        }catch (Exception e){
        	e.printStackTrace();
        }
        return msg;
    }

    @RequestMapping("/del")
    @ResponseBody
    public Msg del(@RequestParam String ids) throws ManagerException {
        Msg msg = new Msg();
        try{
	        String[] deleteIds = ids.split(",");
	        for (String id : deleteIds) {
	            BizConfigDTO bizdto = new BizConfigDTO();
	            bizdto.setId(Integer.parseInt(id));
	            try{
		            bizConfigManager.deleteById(bizdto);
	            }catch (Exception e){
	            	e.printStackTrace();
	            }
	        }
	        msg.setStatus(Msg.SUCCESS);
	        msg.setValue("删除成功");
        }catch(Exception e){
        	e.printStackTrace();
        }
        return msg;
    }

}