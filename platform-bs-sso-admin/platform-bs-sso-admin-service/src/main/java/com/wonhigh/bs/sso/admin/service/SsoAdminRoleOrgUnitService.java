package com.wonhigh.bs.sso.admin.service;

import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午2:53:27
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoAdminRoleOrgUnitService extends BaseCrudService {

    /**
     * 删除关联
     * 系统管理员角色ID 和 组织机构id 的关联
     * @param roleId
     * @return
     * @throws ServiceException
     */
    public int deleteByRoleId(Integer roleId) throws ServiceException;

}
