package com.wonhigh.bs.sso.service;

import java.util.List;
import java.util.Map;

import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.yougou.logistics.base.service.BaseCrudService;

public interface BizConfigService extends BaseCrudService {

	List<BizConfigDTO> findAll();

	BizConfigDTO findbyCondition(Map<String,Object> map);
	
	List<BizConfigVO> selectForApi(Map<String, Object> params);

}
