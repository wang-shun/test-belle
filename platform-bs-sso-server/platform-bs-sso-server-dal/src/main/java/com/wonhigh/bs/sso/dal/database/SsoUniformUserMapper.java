package com.wonhigh.bs.sso.dal.database;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;

/**
 * 
 * findByCondition
 * 
 * @author shitao
 * @date 2017年11月22日 下午8:23:05
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoUniformUserMapper extends BaseCrudMapper {
	public List<SsoUniformUserDTO> findByCondition(@Param("params") Map<String, Object> map) throws DaoException;
}
