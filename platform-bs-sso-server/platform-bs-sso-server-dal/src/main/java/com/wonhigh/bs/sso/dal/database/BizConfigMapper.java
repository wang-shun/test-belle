package com.wonhigh.bs.sso.dal.database;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;

public interface BizConfigMapper extends BaseCrudMapper {

	List<BizConfigDTO> findAll();

	List<BizConfigDTO> findbyCondition(@Param("params") Map<String, Object> map);
	
	List<BizConfigVO> selectForApi(@Param("params") Map<String, Object> params);

}
