package com.wonhigh.bs.admin.sso.dal.database;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.vo.OrgUnitDTO;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:38:51
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public  interface OrgUnitMapper extends BaseCrudMapper {

	/**
	 * 条件查询
	 * @param params
	 * @return
	 */
	public List<OrgUnitDTO> findByParams(@Param("params")Map<String, Object> params);

	/**
	 * 根据组织id查询
	 * @param unitId
	 * @return
	 */
	public OrgUnitDTO findByKey(@Param("unitId")int unitId);
	
	/**
	 * 根据组织代码查询
	 * @param unitCode
	 * @return
	 */
	public List<OrgUnit> selectByUnitCode(String unitCode);
	
	/**
	 * 根据组织id查询父亲节点数
	 * @param unitId
	 * @return
	 * @throws DaoException
	 */
	public Integer queryParentsUnit(@Param("unitId")String unitId) throws DaoException;

}
