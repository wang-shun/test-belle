package com.wonhigh.bs.sso.admin.service;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午2:53:19
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoAdminRoleService extends BaseCrudService {
    /**
     * 查询所有管理员角色列表
     * @return 管理员角色列表
     */
    public List<SsoAdminRoleDTO> findAll();

}
