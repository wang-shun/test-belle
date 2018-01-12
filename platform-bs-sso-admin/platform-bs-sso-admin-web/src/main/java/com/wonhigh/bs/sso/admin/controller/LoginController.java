package com.wonhigh.bs.sso.admin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.util.PasswordUtil;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleOrgUnitDTO;
import com.wonhigh.bs.sso.admin.manager.SsoAdminManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleManager;
import com.wonhigh.bs.sso.admin.manager.SsoAdminRoleOrgUnitManager;
import com.wonhigh.bs.sso.admin.manager.mq.JMSService;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * 
 * TODO: 登录实现--控制层
 * 
 * @author xiao.fz
 * @date 2017年11月7日 下午2:15:25
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Controller
@RequestMapping("/")
public class LoginController {

    @Resource
    private JMSService jmsService;
    @Resource
    private SsoAdminRoleManager ssoAdminRoleManager;
    @Resource
    private SsoAdminManager ssoAdminManager;
    @Resource
    private SsoAdminRoleOrgUnitManager ssoAdminRoleOrgUnitManager;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("home")
    public String home() {
        return "login";
    }

    @RequestMapping("login")
    public String loginSSOAdmin(ModelMap model, HttpServletRequest request, HttpServletResponse resp,
            @RequestParam(value = "loginName", defaultValue = "", required = true) String loginName,
            @RequestParam(value = "loginPassword", defaultValue = "", required = true) String password) {
        try {

            SsoAdminDTO admin = new SsoAdminDTO();
            Map<String, Object> p = new HashMap<String, Object>(2);
            p.put("loginName", loginName);
            List<SsoAdminDTO> userList = ssoAdminManager.findByBiz(admin, p);
            if (userList == null || userList.size() == 0) {
                model.addAttribute("error", "用户不存在");
                return "login";
            }
            SsoAdminDTO loginUser = userList.get(0);
            if (loginUser == null) {
                model.addAttribute("error", "用户不存在");
                return "login";
            }

            if (loginUser.getState() == ApiConstants.SSOADMIN_LOCK_STATE_VALUE) {
                model.addAttribute("error", "用户被锁定");
                return "login";
            }
            //验证密码
            password = PasswordUtil.createPassword(password);
            if (!StringUtils.equals(loginUser.getPassword(), password)) {
                model.addAttribute("error", "密码错误");
                return "login";
            }

            //保存登录用户
            request.getSession(true).setAttribute("loginUser", loginUser);
            request.getSession().setAttribute("session_user", loginUser.getLoginName());
            request.getSession().setAttribute("adminType", loginUser.getAdminType());
            request.getSession().setMaxInactiveInterval(1 * 24 * 60 * 60);

            //加载所管理的机构节点
            if (!(loginUser.getAdminType() == 1)) {
                SsoAdminRoleDTO param = new SsoAdminRoleDTO();
                param.setId(loginUser.getRoleId());
                SsoAdminRoleDTO role = ssoAdminRoleManager.findById(param);
                StringBuilder limits = new StringBuilder();
                List<String> orgUnitList = new ArrayList<String>();
                if (role != null) {
                    SsoAdminRoleOrgUnitDTO ar = new SsoAdminRoleOrgUnitDTO();
                    p.clear();
                    p.put("ssoAdminRoleId", role.getId());
                    List<SsoAdminRoleOrgUnitDTO> list = ssoAdminRoleOrgUnitManager.findByBiz(ar, p);
                    for (SsoAdminRoleOrgUnitDTO ssoAdminRoleOrgUnitDTO : list) {
                        limits.append(ssoAdminRoleOrgUnitDTO.getUnitCode()).append(",");
                        orgUnitList.add(ssoAdminRoleOrgUnitDTO.getUnitCode());
                    }
                }
                request.getSession().setAttribute("orgUnitLimits", limits.toString());
                request.getSession().setAttribute("orgUnitLimitList", orgUnitList);
            }

            if (loginUser != null) {
                model.addAttribute("userName", loginUser.getLoginName());
            }
            model.addAttribute("loginName", loginName);
            return "redirect:/index";
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            model.addAttribute("error", "用户不存在");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            model.addAttribute("error", "密码错误");
        } catch (ManagerException e) {
            e.printStackTrace();
            model.addAttribute("error", "登录错误");
        }
        return "login";
    }

    /**
     * 用户--退出登出
     * @param request
     * @param resp
     * @return
     */
    @RequestMapping("logout")
    public String logoutSSOAdmin(HttpServletRequest request, HttpServletResponse resp) {
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
        return "login";
    }

    @RequestMapping("/updateUserInfo")
    public String updateUserInfo(ModelMap model) throws Exception {
        /*List<SsoAdminRole> roleList = ssoAdminRoleManager.findAll();
        model.addAttribute("roleList", roleList);*/
        return "/update_user_info";
    }

    @RequestMapping("/update")
    @ResponseBody
    public Map<String, Object> update(HttpServletRequest request,
            @RequestParam(value = "sureName", defaultValue = "", required = true) String sureName,
            @RequestParam(value = "email", defaultValue = "", required = true) String email,
            @RequestParam(value = "phone", defaultValue = "", required = true) String phone) {
        Map<String, Object> r = new HashMap<String, Object>(3);
        SsoAdminDTO manager = (SsoAdminDTO) request.getSession().getAttribute("loginUser");
        if (manager == null) {
            r.put("code", 0);
            r.put("msg", "用户 未登录");
            return r;
        }
        //manager.setSureName(sureName);
        manager.setEmail(email);
        manager.setPhone(phone);
        manager.setUpdateUser(manager.getLoginName());
        manager.setUpdateTime(new Date());
        try {
            ssoAdminManager.modifyById(manager);
            r.put("msg", "修改成功");
            r.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "修改失败" + e.getMessage());
        }
        return r;
    }

    @RequestMapping("/updateUserPwd")
    public String updateUserPwd(ModelMap model) throws Exception {
        return "/update_user_pwd";
    }

    /**
     * 修改密码
     * @param request
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @RequestMapping("/updatePwd")
    @ResponseBody
    public Map<String, Object> updatePwd(HttpServletRequest request,
            @RequestParam(value = "oldPwd", defaultValue = "", required = true) String oldPwd,
            @RequestParam(value = "newPwd", defaultValue = "", required = true) String newPwd) {
        Map<String, Object> r = new HashMap<String, Object>(3);
        SsoAdminDTO manager = (SsoAdminDTO) request.getSession().getAttribute("loginUser");
        if (manager == null) {
            r.put("code", 0);
            r.put("msg", "用户 未登录");
            return r;
        }
        oldPwd = PasswordUtil.createPassword(oldPwd);
        if (!StringUtils.equals(oldPwd, manager.getPassword())) {
            r.put("code", 0);
            r.put("msg", "旧密码不正确");
            return r;
        }
        newPwd = PasswordUtil.createPassword(newPwd);
        if(StringUtils.equals(oldPwd, newPwd)){
        	r.put("code", 0);
            r.put("msg", "新密码不能和旧密码相同");
            return r;
        }
        try {
            manager.setPassword(newPwd);
            ssoAdminManager.modifyById(manager);
            r.put("msg", "修改成功");
            r.put("code", 1);
        } catch (Exception e) {
            e.printStackTrace();
            r.put("code", 0);
            r.put("msg", "修改失败" + e.getMessage());
        }
        return r;
    }

}