package com.wonhigh.bs.sso.admin.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.admin.sso.dal.database.SsoAdminRoleOrgUnitMapper;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 角色和 组织机构的关联
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
@Service("ssoAdminRoleOrgUnitService")
class SsoAdminRoleOrgUnitServiceImpl extends BaseCrudServiceImpl implements SsoAdminRoleOrgUnitService {
    @Resource
    private SsoAdminRoleOrgUnitMapper ssoAdminRoleOrgUnitMapper;

    @Override
    public BaseCrudMapper init() {
        return ssoAdminRoleOrgUnitMapper;
    }

    /**
     * 删除关联
     * 系统管理员角色ID 和 组织机构id 的关联
     */
    @Override
    public int deleteByRoleId(Integer roleId) throws ServiceException {
        return ssoAdminRoleOrgUnitMapper.deleteByRoleId(roleId);
    }

}