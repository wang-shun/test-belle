package com.wonhigh.bs.sso.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wonhigh.bs.sso.admin.common.util.Msg;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.wonhigh.bs.sso.admin.manager.SsoAdminManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleManager;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;
import com.yougou.logistics.base.web.controller.BaseCrud4RestfulController;

/**
 * 
 * TODO: 管理员用户--控制层
 * 
 * @author zhang.rq
 * @date 2017年11月7日 下午2:15:00
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/sso_admin")
public class SsoAdminController extends BaseCrud4RestfulController<SsoAdminDTO> {

    @Resource
    private SsoAdminManager ssoAdminManager;
    @Resource
    private SsoAdminRoleManager ssoAdminRoleManager;

    @Override
    public CrudInfo init() {
        return new CrudInfo("ssoAdmin/", ssoAdminManager);
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest req, ModelMap model) throws Exception {
        //加载角色列表
        List<SsoAdminRoleDTO> roleList = new ArrayList<SsoAdminRoleDTO>();
        //除系统管理员 其它只能看到这些角色： 角色管理的机构属于  当前登录的超级管理员的角色所管理的机构
        Map<String, Object> params = new HashMap<String, Object>();
        SsoAdminDTO loginUser = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
        if (loginUser.getAdminType() != 1) {
            //params.put("createUserId", loginUser.getId());
            List<String> orgUnitList = (List<String>) req.getSession().getAttribute("orgUnitLimitList");
            if(orgUnitList!=null && orgUnitList.size()>0){
            	StringBuilder queryCondition = new StringBuilder();
            	queryCondition.append(" AND id NOT IN ( SELECT sso_admin_role_id FROM sso_admin_role_org_unit  WHERE ");
            	for (int i = 0; i < orgUnitList.size(); i++) {
					if(i==0){
						queryCondition.append(" unit_code NOT LIKE '"+StringUtils.trimToEmpty(orgUnitList.get(i)+"%' "));
					}else {
						queryCondition.append(" AND unit_code NOT LIKE '"+StringUtils.trimToEmpty(orgUnitList.get(i)+"%' "));
					}
				}
            	queryCondition.append(" )");
            	params.put("queryCondition", queryCondition.toString());
            }
        }
        SimplePage page = new SimplePage(1, 100, 100);
        List<SsoAdminRoleDTO> list = this.ssoAdminRoleManager.findByPage(page, null, null, params);
        
        model.addAttribute("roleList", list);
        return "/ssoAdmin/index";
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
        String roleId = StringUtils.isEmpty(req.getParameter("roleId")) ? ""
                : String.valueOf(req.getParameter("roleId"));
        Map<String, Object> params = builderParams(req, model);

        //除系统管理员 其它只能看到这些管理员： 管理员角色管理的机构属于  当前登录的超级管理员的角色所管理的机构
        SsoAdminDTO loginUser = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
        if (loginUser.getAdminType() != 1) {
            //params.put("createUserId", loginUser.getId());
        	List<String> orgUnitList = (List<String>) req.getSession().getAttribute("orgUnitLimitList");
            if(orgUnitList!=null && orgUnitList.size()>0){
            	StringBuilder queryCondition = new StringBuilder();
            	queryCondition.append(" AND role_id in( SELECT id FROM sso_admin_role WHERE id NOT IN ( SELECT sso_admin_role_id FROM sso_admin_role_org_unit WHERE ");
            	for (int i = 0; i < orgUnitList.size(); i++) {
					if(i==0){
						queryCondition.append(" unit_code NOT LIKE '"+StringUtils.trimToEmpty(orgUnitList.get(i)+"%' "));
					}else {
						queryCondition.append(" AND unit_code NOT LIKE '"+StringUtils.trimToEmpty(orgUnitList.get(i)+"%' "));
					}
				}
            	queryCondition.append(" group by sso_admin_role_id ) )");
            	params.put("queryCondition", queryCondition.toString());
            }else{
            	params.put("queryCondition", " AND role_id = -1 ");
            }
        } 
        params.put("excludeId", loginUser.getId());
        if (StringUtils.isEmpty(roleId)) {
            params.put("roleId", roleId);
        }
        int total = this.ssoAdminManager.findCount(params);
        SimplePage page = new SimplePage(pageNo, pageSize, (int) total);
        List<SsoAdminDTO> list = this.ssoAdminManager.findByPage(page, sortColumn, sortOrder, params);
        Map<String, Object> obj = new HashMap<String, Object>(3);
        obj.put("total", total);
        obj.put("rows", list);
        return obj;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Msg add(HttpServletRequest req, @ModelAttribute SsoAdminDTO admindto) throws ManagerException {
        Msg msg = new Msg();
        Map<String, Object> param = new HashMap<String, Object>(3);
        param.put("loginName", admindto.getLoginName());

        int total = this.ssoAdminManager.findCount(param);
        if (total > 0) {
            msg.setStatus(Msg.EXISTERROR);
            msg.setValue("【loginName】 登录名已经存在");
            return msg;
        }

        //密码加密
        String password = PasswordUtil.createPassword(admindto.getPassword());
        admindto.setPassword(password);

        SsoAdminDTO loginUser = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
        admindto.setCreateUserId(loginUser.getId());
        admindto.setCreateUser(loginUser.getLoginName());
        ssoAdminManager.add(admindto);
        msg.setStatus(Msg.SUCCESS);
        msg.setValue("添加成功");
        return msg;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Msg update(@ModelAttribute SsoAdminDTO admindto) throws ManagerException {
        Msg msg = new Msg();

        SsoAdminDTO old = ssoAdminManager.findById(admindto);
        if (old == null) {
            msg.setStatus(Msg.NOTEXISTERROR);
            msg.setValue("【loginName】 用户不存在");
            return msg;
        }
        String oldPwd = old.getPassword();
        String newPwd = admindto.getPassword();
        if (!StringUtils.equals(oldPwd, newPwd)) {
            newPwd = PasswordUtil.createPassword(newPwd);
            if(StringUtils.equals(oldPwd, newPwd)){
            	msg.setStatus(Msg.FAILURE);
                msg.setValue("新密码不能和旧密码相同");
                return msg;
            }
            admindto.setPassword(newPwd);
        }/*else{
        	 msg.setStatus(Msg.FAILURE);
             msg.setValue("新密码不能和旧密码相同");
             return msg;
        }*/
        ssoAdminManager.modifyById(admindto);
        msg.setStatus(Msg.SUCCESS);
        msg.setValue("修改成功");
        return msg;
    }

    @RequestMapping("/del")
    @ResponseBody
    public Msg del(@RequestParam String ids) throws ManagerException {
        Msg msg = new Msg();
        String[] deleteIds = ids.split(",");
        for (String id : deleteIds) {
            SsoAdminDTO bizdto = new SsoAdminDTO();
            bizdto.setId(Integer.parseInt(id));
            ssoAdminManager.deleteById(bizdto);
        }

        msg.setStatus(Msg.SUCCESS);
        msg.setValue("删除成功");
        return msg;
    }

}