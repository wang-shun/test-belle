package com.wonhigh.bs.sso.server.manager;

import java.util.List;
import java.util.Map;

import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 业务系统 bizCode : bizSecret 管理
 * @author user
 *
 */
public interface BizConfigManager extends BaseCrudManager {
	List<BizConfigDTO> findAll();

	BizConfigDTO findByPrimaryKey(String bizCode);

	BizConfigDTO findByBizName(String bizName);
	
	List<BizConfigVO> selectForApi(Map<String, Object> params);
}
