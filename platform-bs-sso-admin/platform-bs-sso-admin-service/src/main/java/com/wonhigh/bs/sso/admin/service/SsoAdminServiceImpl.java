package com.wonhigh.bs.sso.admin.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.admin.sso.dal.database.SsoAdminMapper;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 系统管理员
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
@Service("ssoAdminService")
class SsoAdminServiceImpl extends BaseCrudServiceImpl implements SsoAdminService {
    @Resource
    private SsoAdminMapper ssoAdminMapper;

    @Override
    public BaseCrudMapper init() {
        return ssoAdminMapper;
    }

}