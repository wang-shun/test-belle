package com.wonhigh.bs.sso.server.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.dal.database.BizConfigMapper;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.wonhigh.bs.sso.service.BizConfigService;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 请写出类的用途 
 * @author zhang.rq.com
 * @date  2017-03-22 19:04:03
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
@Service("bizConfigService")
class BizConfigServiceImpl extends BaseCrudServiceImpl implements BizConfigService {
    @Resource
    private BizConfigMapper bizConfigMapper;

    @Override
    public BaseCrudMapper init() {
        return bizConfigMapper;
    }

	@Override
	public List<BizConfigDTO> findAll() {
		return bizConfigMapper.findAll();
	}

	@Override
	public BizConfigDTO findbyCondition(Map<String,Object> map) {
		List<BizConfigDTO> dtoList=bizConfigMapper.findbyCondition(map);
		if(dtoList.size()>0)return dtoList.get(0);
		return null;
	}

    @Override
    public List<BizConfigVO> selectForApi(Map<String, Object> params) {
        return bizConfigMapper.selectForApi(params);
    }

}