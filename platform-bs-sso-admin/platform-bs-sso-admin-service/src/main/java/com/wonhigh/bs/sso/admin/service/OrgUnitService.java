package com.wonhigh.bs.sso.admin.service;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.vo.OrgUnitDTO;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午2:53:44
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface OrgUnitService extends BaseCrudService {
	/**
	 * 根据组织父级ID查出下级组织列表，包含组织属性等
	 * 
	 * @param unitId
	 * @param status -1 为全部 0 未生效 1 已生效
	 * @param parentId
	 * @return
	 * @throws ServiceException
	 */
	public List<OrgUnitDTO> getUnitListByParentId(int parentId, int status) throws ServiceException;
	
	/**
	 * 查询组织树 包括自己
	 * @param selfUnitId
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	public List<OrgUnitDTO> getUnitListBySelfId(int selfUnitId, int status) throws ServiceException;
	
	/**
	 * 根据组织ID查询组织信息，包含组织属性等
	 * @param unitId
	 * @return
	 * @throws ServiceException
	 */
	public OrgUnitDTO getUnitByUnitId(int unitId)throws ServiceException;
	
	/**
	 * 根据组织代码查询组织信息
	 * @param unitCode
	 * @return
	 * @throws ServiceException
	 */
	public List<OrgUnit> selectByUnitCode(String unitCode) throws ServiceException;
	
	/**
	 * 根据unitId查询父级组织
	 * @param unitId
	 * @return
	 * @throws ServiceException
	 */
	public Integer queryParentsUnit(String unitId) throws ServiceException;

}
