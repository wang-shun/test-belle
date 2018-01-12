package com.wonhigh.bs.sso.admin.controller;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.util.CommonUtil;
import com.wonhigh.bs.sso.admin.common.util.Msg;
import com.wonhigh.bs.sso.admin.common.util.OrgUnitCodeComparator;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleOrgUnitDTO;
import com.wonhigh.bs.sso.admin.manager.SsoAdminManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleOrgUnitManager;
import com.yougou.logistics.base.common.enums.CommonOperatorEnum;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;
import com.yougou.logistics.base.web.controller.BaseCrud4RestfulController;

/**
 * 
 * TODO: 管理员角色--控制层
 * 
 * @author xiao.fz
 * @date 2017年11月7日 下午2:14:27
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/admin_role")
public class SsoAdminRoleController extends BaseCrud4RestfulController<SsoAdminRoleDTO> {

    @Resource
    private SsoAdminRoleManager ssoAdminRoleManager;
    @Resource
    private SsoAdminRoleOrgUnitManager ssoAdminRoleOrgUnitManager;
    @Resource
    private SsoAdminManager ssoAdminManager;

    @Override
    public CrudInfo init() {
        return new CrudInfo("ssoAdminRole/", ssoAdminRoleManager);
    }

    @RequestMapping("/index")
    public String index(ModelMap model) throws Exception {
        return "/adminrole/index";
    }

    @Override
    @RequestMapping("/list.json")
    @ResponseBody
    public Map<String, Object> queryList(HttpServletRequest req, Model model) throws ManagerException {
        try {
            int pageNo = StringUtils.isEmpty(req.getParameter("page")) ? 1 : Integer.parseInt(req.getParameter("page"));
            int pageSize = StringUtils.isEmpty(req.getParameter("rows")) ? 10
                    : Integer.parseInt(req.getParameter("rows"));
            String sortColumn = StringUtils.isEmpty(req.getParameter("sort")) ? ""
                    : String.valueOf(req.getParameter("sort"));
            String sortOrder = StringUtils.isEmpty(req.getParameter("order")) ? ""
                    : String.valueOf(req.getParameter("order"));
            String descriptionP = StringUtils.isEmpty(req.getParameter("descriptionP")) ? ""
                    : String.valueOf(req.getParameter("descriptionP"));

            //除系统管理员 其它只能看到这些角色： 角色管理的机构属于  当前登录的超级管理员的角色所管理的机构
            Map<String, Object> params = builderParams(req, model);
            
            SsoAdminDTO loginUser = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
            if (StringUtils.isNotEmpty(descriptionP)) {
                params.put("description", descriptionP);
            }
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
            int total = this.ssoAdminRoleManager.findCount(params);
            SimplePage page = new SimplePage(pageNo, pageSize, (int) total);
            List<SsoAdminRoleDTO> list = this.ssoAdminRoleManager.findByPage(page, sortColumn, sortOrder, params);
            Map<String, Object> obj = new HashMap<String, Object>(3);
            obj.put("total", total);
            obj.put("rows", list);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/add")
    @ResponseBody
    public Msg add(HttpServletRequest req, @ModelAttribute SsoAdminRoleDTO roleDto,
            @RequestParam(value = "orgcodes", defaultValue = "", required = true) String orgcodes,
            @RequestParam(value = "orgIds", defaultValue = "", required = true) String orgIds) {
        Msg msg = new Msg();
        try {
            Map<String, Object> param = new HashMap<String, Object>(2);
            param.put("roleName", roleDto.getRoleName());

            int total = this.ssoAdminRoleManager.findCount(param);
            if (total > 0) {
                msg.setStatus(Msg.EXISTERROR);
                msg.setValue("【roleName】 角色名称已经存在");
                return msg;
            }
            SsoAdminDTO loginUser = (SsoAdminDTO) req.getSession().getAttribute("loginUser");
            roleDto.setCreateUserId(loginUser.getId());
            roleDto.setCreateUser(loginUser.getSureName());
            roleDto.setRoleCode(System.currentTimeMillis() + "");
            this.ssoAdminRoleManager.add(roleDto);
            msg.setStatus(Msg.SUCCESS);
            msg.setValue("添加成功");

            //对资源进行处理：只保存父节点
            String[] split = orgcodes.split(",");
            if (split.length > 1) {
                List<String> sortLimits = new ArrayList<String>();
                for (String limit : split) {
                    sortLimits.add(limit);
                }
                Collections.sort(sortLimits, new OrgUnitCodeComparator());
                String tmp = sortLimits.get(0);
                String limitStr = "";
                for (int i = 0; i < sortLimits.size(); i++) {
                    int j = i + 1;
                    if (j < sortLimits.size()) {
                        String sub = sortLimits.get(j);
                        if (CommonUtil.isPrefix(tmp, sub)) {
                            //去掉子节点
                        } else {
                            limitStr += tmp + ",";
                            tmp = sub;
                        }
                    } else {
                        limitStr += tmp + ",";
                    }
                }
                orgcodes = limitStr;
            }

            //查询机构id
            String[] orgcodeArr = orgcodes.split(",");
            String[] orgIdArr = orgIds.split(",");
            List<SsoAdminRoleOrgUnitDTO> list = new ArrayList<SsoAdminRoleOrgUnitDTO>();
            for (String orgId : orgIdArr) {
                for (String orgcode : orgcodeArr) {
                    if (CommonUtil.isPrefix(orgcode + ":", orgId)) {
                        orgId = orgId.replace(orgcode + ":", "");
                        //添加角色关联的机构
                        SsoAdminRoleOrgUnitDTO roleOrg = new SsoAdminRoleOrgUnitDTO();
                        roleOrg.setOrgUnitId(Integer.valueOf(orgId));
                        roleOrg.setUnitCode(orgcode);
                        roleOrg.setSsoAdminRoleId(roleDto.getId());
                        roleDto.setCreateUser(loginUser.getLoginName());
                        list.add(roleOrg);
                        continue;
                    } else {
                    }
                }
            }
            Map<CommonOperatorEnum, List<SsoAdminRoleOrgUnitDTO>> map = new HashMap<CommonOperatorEnum, List<SsoAdminRoleOrgUnitDTO>>(1);
            map.put(CommonOperatorEnum.INSERTED, list);
            ssoAdminRoleOrgUnitManager.save(map);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setStatus(Msg.FAILURE);
            msg.setValue("添加失败");
        }

        return msg;
    }

    @RequestMapping("/update")
    @ResponseBody
    public Msg update(@ModelAttribute SsoAdminRoleDTO roleDto,
            @RequestParam(value = "orgcodes", defaultValue = "", required = true) String orgcodes,
            @RequestParam(value = "orgIds", defaultValue = "", required = true) String orgIds) {
        Msg msg = new Msg();
        try {
            SsoAdminRoleDTO dto = this.ssoAdminRoleManager.findById(roleDto);
            if (dto == null) {
                msg.setStatus(Msg.EXISTERROR);
                msg.setValue("【roleName】 角色不存在");
                return msg;
            } else {
                roleDto.setRoleName(dto.getRoleName());
            }

            roleDto.setRoleCode(System.currentTimeMillis() + "");
            this.ssoAdminRoleManager.modifyById(roleDto);
            msg.setStatus(Msg.SUCCESS);
            msg.setValue("修改成功");

            //删除原有关联节点
            this.ssoAdminRoleOrgUnitManager.deleteByRoleId(roleDto.getId());

            //对资源进行处理：只保存父节点
            String[] split = orgcodes.split(",");
            List<String> limtList = new ArrayList<String>();
            if (split.length > 1) {
                String limitStr = "";
                String unLimitStr = ""; 
            	for (int i = 0; i < split.length; i++) {
            		limtList.add(split[i]);
            		String tmp = split[i];
            		if((i+1)==split.length){
            		    break;
            		}
            		for(int j=(i+1); j< split.length; j++){
            			String sub = split[j];
                        if (CommonUtil.isPrefix(tmp, sub)) {
                        	//如果tmp是sub的子节点 去掉子节点
                        	unLimitStr += sub + ",";
                        }else if (CommonUtil.isPrefix(sub, tmp)){
                        	//如果sub是tmp的子节点 去掉子节点
                        	unLimitStr += tmp + ",";
                        }
            		}
                }
            	if(StringUtils.isNotEmpty(unLimitStr)){
            		String[] unLimtArr = unLimitStr.split(",");
            		for (String unLimt : unLimtArr) {
            			limtList.remove(unLimt);
					}
            	}
            	orgcodes = "";
            	for (String limt : limtList) {
            		orgcodes += limt+",";
				}
            }

            //查询机构id
            String[] orgcodeArr = orgcodes.split(",");
            String[] orgIdArr = orgIds.split(",");
            List<SsoAdminRoleOrgUnitDTO> list = new ArrayList<SsoAdminRoleOrgUnitDTO>();
            for (String orgId : orgIdArr) {
                for (String orgcode : orgcodeArr) {
                    if (CommonUtil.isPrefix(orgcode + ":", orgId)) {
                        orgId = orgId.replace(orgcode + ":", "");
                        //添加角色关联的机构
                        SsoAdminRoleOrgUnitDTO roleOrg = new SsoAdminRoleOrgUnitDTO();
                        roleOrg.setOrgUnitId(Integer.valueOf(orgId));
                        roleOrg.setUnitCode(orgcode);
                        roleOrg.setSsoAdminRoleId(roleDto.getId());
                        roleDto.setCreateUser("");
                        list.add(roleOrg);
                        continue;
                    } else {
                    }
                }
            }
            Map<CommonOperatorEnum, List<SsoAdminRoleOrgUnitDTO>> map = new HashMap<CommonOperatorEnum, List<SsoAdminRoleOrgUnitDTO>>(1);
            map.put(CommonOperatorEnum.INSERTED, list);
            ssoAdminRoleOrgUnitManager.save(map);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setStatus(Msg.FAILURE);
            msg.setValue("修改失败");
        }

        return msg;
    }

    @RequestMapping("/del")
    @ResponseBody
    public Msg del(@RequestParam String ids) throws ManagerException {
        Msg msg = new Msg();
        String[] deleteIds = ids.split(",");
        for (String id : deleteIds) {
            SsoAdminRoleDTO roledto = new SsoAdminRoleDTO();
            roledto.setId(Integer.parseInt(id));

            //删除原有关联节点
            this.ssoAdminRoleOrgUnitManager.deleteByRoleId(Integer.parseInt(id));

            ssoAdminRoleManager.deleteById(roledto);
        }

        msg.setStatus(Msg.SUCCESS);
        msg.setValue("删除成功");
        return msg;
    }

    @RequestMapping("/all.json")
    @ResponseBody
    public String allAdminRole(
    		HttpServletRequest req,
    		@RequestParam(value = "adminId", defaultValue = "", required = false) Integer adminId
    		) throws Exception {
    	JSONArray json = new JSONArray();
        try {
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
            
            SsoAdminDTO adminDTO = null;
            if(adminId!=null){
            	SsoAdminDTO admin = new SsoAdminDTO();
            	admin.setId(adminId);
            	adminDTO = ssoAdminManager.findById(admin);
            }
            for (SsoAdminRoleDTO role : list) {
				JSONObject obj = new JSONObject();
				obj.put("id", role.getId());
				obj.put("roleName", role.getRoleName());
				if(adminDTO!=null){
					if(adminDTO.getRoleId().equals(role.getId())){
						obj.put("selected", true);
					}
				}
				json.add(obj);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toJSONString();
    }

}