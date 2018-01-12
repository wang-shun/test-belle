package com.wonhigh.bs.sso.admin.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.admin.sso.dal.database.SsoAdminRoleMapper;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 管理员角色
 * @author zhang.rq
 * @date  2017-10-24 19:04:03
 * @version 0.1.0
 * @copyright (C) 2013 YouGou Information Technology Co.,Ltd 
 * All Rights Reserved. 
 * 
 * The software for the YouGou technology development, without the 
 * company's written consent, and any other individuals and 
 * organizations shall not be used, Copying, Modify or distribute 
 * the software.
 * 
 */
@Service("ssoAdminRoleService")
class SsoAdminRoleServiceImpl extends BaseCrudServiceImpl implements SsoAdminRoleService {
    @Resource
    private SsoAdminRoleMapper ssoAdminRoleMapper;

    @Override
    public BaseCrudMapper init() {
        return ssoAdminRoleMapper;
    }

    /**
     * 查询所有管理员角色列表
     * @return 管理员角色列表
     */
    @Override
    public List<SsoAdminRoleDTO> findAll() {
        return ssoAdminRoleMapper.findAll();
    }

}