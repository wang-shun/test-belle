package com.wonhigh.bs.sso.admin.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.model.VEasyUiTree;
import com.wonhigh.bs.sso.admin.common.util.CommonUtil;
import com.wonhigh.bs.sso.admin.common.util.RedisUtil;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleOrgUnitDTO;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleOrgUnitManager;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * 
 * TODO: 组织结构树图--控制层
 * 
 * @author xiao.fz
 * @date 2017年11月7日 下午2:15:15
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/org_unit")
public class OrgUnitController {
    protected static final XLogger LOGGER = XLoggerFactory.getXLogger(OrgUnitController.class);
    public static final String ROOT = "100";
    public static final String TOPORG = "0";
    @Resource
    private OrgUnitManager orgUnitManager;
    //@Resource
    //private RedisUtil redisUtil;
    @Resource
    private SsoAdminManager ssoAdminManager;
    @Resource
    private SsoAdminRoleManager ssoAdminRoleManager;
    @Resource
    private SsoAdminRoleOrgUnitManager ssoAdminRoleOrgUnitManager;

    /**
     * 查询有权限的组织(加载两级)
     * 
     * @param orgUnit
     * @param req
     * @return
     */
    @RequestMapping(value = "/tree_data_pre.json")
    @ResponseBody
    public List<VEasyUiTree> treeDataPre(HttpServletRequest req, Model model) {

        Integer adminType = (Integer) req.getSession().getAttribute("adminType");
        List<String> orgUnitList = null;
        if (adminType != 1) {
        	//只显示此节点下的机构
            orgUnitList = (List<String>) req.getSession().getAttribute("orgUnitLimitList"); 
        }

        Map<String, Object> params = builderParams(req, model);
        //查该节点下子节点
        Integer unitId = params.get("id") == null ? null : Integer.valueOf((String) params.get("id"));
        int level = CommonUtil.getIntValue(params.get("level"), 0);

        //是否刷新缓存
        //String refresh = CommonUtil.getStrValue(params.get("refresh"), null);

        // 查询出所有的节点
        List<VEasyUiTree> otree = new ArrayList<VEasyUiTree>();
        try {
        	//登录的管理员拥有的节点(只包含父节点)
            if (orgUnitList != null && unitId == null) { 
                for (String unitCode : orgUnitList) {
                    //String key2 = "orgUnit-" + "x" + level + "-" + unitCode;
                    //Object object2 = redisUtil.get(key2);
                    //if (object2 == null || refresh != null) {
                        //查找unitId
                        List<OrgUnit> list = orgUnitManager.findByUnitCode(unitCode);
                        if (list != null && list.size() > 0) {
                        	//包含根节点
                            List<VEasyUiTree> l = orgUnitManager.queryTreeNodesBySelfIdAndLevel(list.get(0).getUnitId(), level, 1); 
                            otree.addAll(l);
                            //redisUtil.set(key2, l, 24 * 60 * 60L);
                        }
                    /*} else {
                        List<VEasyUiTree> l = (List<VEasyUiTree>) object2;
                        otree.addAll(l);
                    }*/
                }
            } else if (unitId != null) { 
            	//查询子节点 不包括自己
                //String key = "orgUnit-" + unitId + "-" + level + "x";
                //Object object = redisUtil.get(key);
                //if (object == null || refresh != null) {
                	//不包含根节点
                    otree = orgUnitManager.queryTreeNodesBySelfIdAndLevel(unitId, level, 0);  
                /*} else {
                    otree = (List<VEasyUiTree>) object;
                }*/
            } else { //查询所有组织架构
                String key = ApiConstants.ORG_UNTI_TREE_ALL;
                //Object object = redisUtil.get(key);
                //if (object == null || refresh != null) {
                    otree = orgUnitManager.queryTreeNodesByLevel(0, level);
                    /*redisUtil.set(key, otree, 24 * 60 * 60L);
                } else {
                    otree = (List<VEasyUiTree>) object;
                }*/
            }

            // 角色已选择的节点
            String roleId = CommonUtil.getStrValue(params.get("roleId"), null);
            if (StringUtils.isNotEmpty(roleId)) {
                SsoAdminRoleOrgUnitDTO p = new SsoAdminRoleOrgUnitDTO();
                p.setSsoAdminRoleId(Integer.valueOf(roleId));
                Map<String, Object> ps = new HashMap<String, Object>(2);
                ps.put("ssoAdminRoleId", roleId);
                //查询角色所管理的组织机构
                List<SsoAdminRoleOrgUnitDTO> list = ssoAdminRoleOrgUnitManager.findByBiz(p, ps);
                List<String> limitList = new ArrayList<String>();
                if (list != null && list.size() > 0) {
                	for (SsoAdminRoleOrgUnitDTO roleOrgUnit : list) {
                		this.initSelectedOrgTree(roleOrgUnit.getUnitCode(), otree);
                		limitList.add(roleOrgUnit.getUnitCode());
					}
                }
                //this.initOrgTree(limitList, otree);
            }
            
            //ssoUser所属组织机构
            if (StringUtils.isEmpty(roleId)) {
                String organizationCode = CommonUtil.getStrValue(params.get("organizationCode"), null);
                if (organizationCode != null) {
                    List<String> limitList = new ArrayList<String>();
                    limitList.add(organizationCode);
                    this.initSelectedOrgTree(organizationCode, otree);
                    //this.initOrgTree(limitList, otree);
                }
            }

        } catch (ManagerException e) {
        	LOGGER.error(">>>>>>获取组织树出错.", e);
        }
        return otree;
    }
    
    /**
     * 加载指定数据
     * @param roleOrgUnitCode 指定加载到的节点
     * @param otree
     */
    private void initSelectedOrgTree(String roleOrgUnitCode, List<VEasyUiTree> otree){
    	if(StringUtils.isEmpty(roleOrgUnitCode)) {
    		return;
    	}
    	if(otree==null||otree.size()==0){
    		return;
    	}
    	//遍历otree 找到当前角色所选机构节点
    	for (VEasyUiTree uiTree : otree) {
			String orgUnitCode = uiTree.getUnitCode();
			//找到了
			if(StringUtils.equals(roleOrgUnitCode, orgUnitCode)){ 
				//修改状态为选中
				if (uiTree.getIsParent()) {
					uiTree.setChecked(true);
					uiTree.setLeaf(false);
					uiTree.setOpen(false);
					uiTree.setState("closed");
                } else {
					uiTree.setChecked(true);
					uiTree.setLeaf(true);
                    uiTree.setState("");
                }
				return;
			}else if(CommonUtil.isPrefix(orgUnitCode, roleOrgUnitCode)){  
				//找到roleOrgUnitCode的父节点，再查子节点
				List<VEasyUiTree> children = uiTree.getChildren();
				if(children==null || children.size()==0){
					//查数据库
					try {
						//不包含当前节点,只查询直接子节点
						children = orgUnitManager.queryTreeNodesBySelfIdAndLevel(uiTree.getId(), 1, 0);
						uiTree.setChildren(children);
					} catch (ManagerException e) {
						e.printStackTrace();
					} 
				}
				
				if (uiTree.getIsParent()) {
					//修改状态为子节点选中
					uiTree.setChecked(false);
					uiTree.setOpen(true);
					uiTree.setState("open");
					uiTree.setLeaf(false);
                } else {
					uiTree.setChecked(false);
					uiTree.setLeaf(true);
                    uiTree.setState("");
                }
				
				//递归查询
				this.initSelectedOrgTree(roleOrgUnitCode, children);
			}else if(CommonUtil.isPrefix(roleOrgUnitCode, orgUnitCode)) { 
				//被选中节点的子节点
				uiTree.setChecked(true);
				if (uiTree.getIsParent()) {
					uiTree.setState("closed");
					uiTree.setOpen(false);
					uiTree.setLeaf(false);
                } else {
                	uiTree.setLeaf(true);
                    uiTree.setState("");
                }
			}else {
				if (uiTree.getIsParent()) {
					uiTree.setState("closed");
					uiTree.setLeaf(false);
                } else {
                	uiTree.setLeaf(true);
                    uiTree.setState("");
                }
			}
		}
    }
    
    /**
     * 初始化组织树状态
     * @param limitList 已经选中的节点
     * @param otree 遍历树
     */
    private void initOrgTree(List<String> limitList, List<VEasyUiTree> otree) {
        for (VEasyUiTree tree : otree) {
            String orgcode = tree.getUnitCode();
            List<VEasyUiTree> children = tree.getChildren();
            boolean flag = false;
            for (String limit : limitList) {
                if (flag){
                	continue;
                }
                if (CommonUtil.isPrefix(orgcode, limit)) {
                    tree.setChecked(true);
                    tree.setOpen(false);
                    if (tree.getIsParent() && children != null && children.size() > 0) {
                        //tree.setState("open");
                        tree.setState("closed");
                    } else if (tree.getIsParent()) {
                        tree.setState("closed");
                    } else {
                        tree.setState("");
                        tree.setLeaf(true);
                    }
                    flag = true;
                } /*else if(CommonUtil.isPrefix(orgcode, limit)){
                  tree.setOpen(true);
                  tree.setState("open");
                  }*/else {
                    tree.setChecked(false);
                    tree.setOpen(false);
                    if (tree.getIsParent()) {
                        tree.setState("closed");
                    } else {
                        tree.setLeaf(true);
                        tree.setState("");
                    }
                }
            }
            if (children != null && children.size() > 0) {
                this.initOrgTree(limitList, children);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> builderParams(HttpServletRequest req, Model model) {
        Map<String, Object> retParams = new HashMap<String, Object>(req.getParameterMap().size());
        Map<String, String[]> params = req.getParameterMap();
        if (null != params && params.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Entry<String, String[]> p : params.entrySet()) {

                if (null == p.getValue() || p.getValue().length <= 0){
                    continue;
                }
                // 只转换一个参数，多个参数不转换
                String[] values = (String[]) p.getValue();
                String match = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
                if (values[0].matches(match)) {
                    try {
                        retParams.put(p.getKey(), sdf.parse(values[0]));
                    } catch (ParseException e) {
                        retParams.put(p.getKey(), values);
                        e.printStackTrace();
                    }
                } else if ("queryCondition".equals(p.getKey()) && model.asMap().containsKey("queryCondition")) {
                    retParams.put(p.getKey(), model.asMap().get("queryCondition"));
                } else {
                    retParams.put(p.getKey(), values[0]);
                }
            }
            String qm = "queryParams";
            if (model.asMap().containsKey(qm)) {
                Map<String, Object> queryParams = (Map<String, Object>) model.asMap().get("queryParams");
                for (Entry<String, Object> entry : queryParams.entrySet()) {
                    retParams.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return retParams;
    }

}
