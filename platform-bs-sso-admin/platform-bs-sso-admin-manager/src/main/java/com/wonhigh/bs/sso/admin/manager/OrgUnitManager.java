package com.wonhigh.bs.sso.admin.manager;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.model.VEasyUiTree;
import com.wonhigh.bs.sso.admin.common.vo.OrgUnitDTO;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 请写出类的用途
 * 
 * @author user
 * @date 2016-05-26 14:35:07
 * @version 1.0.0
 * @copyright (C) 2013 WonHigh Information Technology Co.,Ltd All Rights
 *            Reserved.
 * 
 *            The software for the WonHigh technology development, without the
 *            company's written consent, and any other individuals and
 *            organizations shall not be used, Copying, Modify or distribute the
 *            software.
 * 
 */
public interface OrgUnitManager extends BaseCrudManager {
	
	/**
	 * 组织树结构缓存处理
	 * @param unitId 
	 * @param userNo
	 * @param level
	 * @return List<VEasyUiTree>
	 * @throws ManagerException
	 */
	public List<VEasyUiTree> queryTreeNodesByLevel(int unitId,int level)throws ManagerException;
	
	/**
	 * 查询组织树
	 * @param unitId
	 * @param level
	 * @param type 1 包含自己  0不包含自己
	 * @return
	 * @throws ManagerException
	 */
	public List<VEasyUiTree> queryTreeNodesBySelfIdAndLevel(int unitId, int level, int type)throws ManagerException;
	
	/**
	 * 根据组织编码获组织信息
	 * @param unitCode
	 * @return
	 * @throws ManagerException
	 */
	public List<OrgUnit> findByUnitCode(String unitCode) throws ManagerException;
	
	/**
	 * 根据Id查询父级组织
	 * @param unitId
	 * @return
	 * @throws ManagerException
	 */
	public Integer queryParentsUnit(String unitId)throws ManagerException;
	
	/**
	 * 查询下属所有组织
	 * @param unitId 组织ID
	 * @param flag 标识：0查所有 1查有效组织
	 * @return
	 * @throws ManagerException
	 */
	public List<OrgUnitDTO> findSubList(int unitId,int flag)throws ManagerException;
	
	/**
	 * 查出所有的组织
	 * @return
	 */
	List<OrgUnitDTO> findAllList();
	
	/**
	 * 根据组织ID查询组织信息，包含组织属性等
	 * @param unitId
	 * @return
	 * @throws ServiceException
	 */
	public OrgUnitDTO getUnitByUnitId(int unitId)throws ServiceException;

}