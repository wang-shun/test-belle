package com.wonhigh.bs.sso.admin.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.model.VEasyUiTree;
import com.wonhigh.bs.sso.admin.common.vo.OrgUnitDTO;
import com.wonhigh.bs.sso.admin.service.OrgUnitService;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

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
@Service("orgUnitManager")
public class OrgUnitManagerImpl extends BaseCrudManagerImpl implements OrgUnitManager {
	@Resource
	private OrgUnitService orgUnitService;
	private static final Logger logger = Logger.getLogger(OrgUnitManagerImpl.class);

	@Override
	public BaseCrudService init() {
		return orgUnitService;
	}

	@Override
	public List<VEasyUiTree> queryTreeNodesByLevel(int unitId, int level)
			throws ManagerException {

		List<VEasyUiTree> otree = new ArrayList<VEasyUiTree>();
		try {
			OrgUnitDTO ou = new OrgUnitDTO();
			ou = orgUnitService.getUnitByUnitId(unitId);
			return getNode(unitId,level,0);
		} catch (Exception ex) {

		}
		return otree;
	}
	
	/**
	 * 获取组织树单个节点信息
	 * 
	 * @param unitId
	 *            组织ID
	 * @param userNo
	 *            用户编码
	 * @param level
	 *            显示级次
	 * @param num
	 *            已循环次数
	 * @return
	 */
	private List<VEasyUiTree> getNode(int unitId, int level, int num) {
		List<VEasyUiTree> otree = new ArrayList<VEasyUiTree>();
		try {
			if (num < level) {
				num++;
				
				List<OrgUnitDTO> list = orgUnitService.getUnitListByParentId(unitId, 1);
				
				if (list!=null&&list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						OrgUnitDTO ou = list.get(i);

						VEasyUiTree tree = new VEasyUiTree();
						tree.setId(Integer.valueOf(ou.getUnitId()));
						tree.setText(ou.getFullName());
						tree.setPId(ou.getParentId());
						JSONObject attr = new JSONObject();
						attr.put("unitCode", ou.getUnitCode());
						tree.setAttributes(attr.toJSONString());
						tree.setUnitCode(ou.getUnitCode());
						
						List<VEasyUiTree> subtree = getNode(ou.getUnitId(),level,num);
						
						if (i == (list.size() - 1)) {
							if (num == 1){
								num = level;
							}

						}
						
//						otree.addAll(subtree);

						tree.setChildren(subtree);
						if (num == level) {
							tree.setState("closed");
							tree.setOpen(false);
							tree.setIsParent(true);
						}
						
						if(ou.getChildNodeCount()!=null && ou.getChildNodeCount()>0){
							tree.setIsParent(true);
						}else{
							tree.setIsParent(false);
							tree.setState("open");
							tree.setLeaf(true);
						}
						
						otree.add(tree);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return otree;
	}
	
	@Override
	public List<OrgUnit> findByUnitCode(String unitCode)
			throws ManagerException {
		try {
			return orgUnitService.selectByUnitCode(unitCode);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer queryParentsUnit(String unitId) throws ManagerException {
		try {
			return orgUnitService.queryParentsUnit(unitId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<OrgUnitDTO> findSubList(int unitId, int flag) throws ManagerException {
		List<OrgUnitDTO> list = new ArrayList<OrgUnitDTO>();
		try {
			OrgUnitDTO orgunit = new OrgUnitDTO();
			orgunit = orgUnitService.getUnitByUnitId(unitId);
			if (orgunit == null){
				return list;
			}
			// 添加当前组织
			list.add(orgunit);
			int pUnitId = unitId;
			List<OrgUnitDTO> listA = orgUnitService.getUnitListByParentId(pUnitId, flag);
			while (listA != null && listA.size() > 0) {
				List<OrgUnitDTO> listM = new ArrayList<OrgUnitDTO>();
				for (int i = 0; i < listA.size(); i++) {
					list.add(listA.get(i));
					List<OrgUnitDTO> listB = orgUnitService.getUnitListByParentId(listA.get(i).getUnitId(), flag);
					if (listB != null && listB.size() > 0) {
						listM.addAll(listB);
					}
					if (i == listA.size() - 1) {
						// 最后一个时候,将遍历集合赋给ListA
						listA.clear();
						listA.addAll(listM);
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return list;
	}

	@Override
	public List<OrgUnitDTO> findAllList() {
		try {
			Map<String, Object> params = new HashMap<String, Object>(2);
			params.put("orgStatus", 1);
			return orgUnitService.findByBiz(null, params);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OrgUnitDTO getUnitByUnitId(int unitId) throws ServiceException {
		try {
			return orgUnitService.getUnitByUnitId(unitId);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}
       

	@Override
	public List<VEasyUiTree> queryTreeNodesBySelfIdAndLevel(int unitId, int level, int type)throws ManagerException {

		List<VEasyUiTree> otree = new ArrayList<VEasyUiTree>();
		try {
			OrgUnitDTO ou = new OrgUnitDTO();
			ou = orgUnitService.getUnitByUnitId(unitId);
			return getNodeByUnitIdAndLevel(unitId,level,0,type);
		} catch (Exception ex) {

		}
		return otree;
	}
	
	/**
	 * 查询组织树
	 * @param unitId
	 * @param level
	 * @param type 1 包含自己  0不包含自己
	 * @return
	 */
	private List<VEasyUiTree> getNodeByUnitIdAndLevel(int unitId, int level, int num, int type) {
		List<VEasyUiTree> otree = new ArrayList<VEasyUiTree>();
		try {
			if (num < level) {
				num++;
				
				List<OrgUnitDTO> list = null;
				if(type==0){
					list = orgUnitService.getUnitListByParentId(unitId, 1);
				}else{
					//包含自己节点处理
					//1.先查询自己
					OrgUnitDTO orgUnitDTO = orgUnitService.getUnitByUnitId(unitId);
					//List<OrgUnitDTO> subList = orgUnitService.getUnitListByParentId(unitId, 1);
					list = new ArrayList<OrgUnitDTO>();
					list.add(orgUnitDTO);
				}
				
				if (list!=null&&list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						OrgUnitDTO ou = list.get(i);

						VEasyUiTree tree = new VEasyUiTree();
						tree.setId(Integer.valueOf(ou.getUnitId()));
						tree.setText(ou.getFullName());
						tree.setPId(ou.getParentId());
						JSONObject attr = new JSONObject();
						attr.put("unitCode", ou.getUnitCode());
						tree.setAttributes(attr.toJSONString());
						tree.setUnitCode(ou.getUnitCode());
						
						if(type==0){
						}else{
							//包含自己节点处理
							//2.遍历处理子节点
							num--;
						}
						List<VEasyUiTree> subtree = getNodeByUnitIdAndLevel(ou.getUnitId(),level,num, 0);
						
						if (i == (list.size() - 1)) {
							if (num == 1){
								num = level;
							}

						}
						
//						otree.addAll(subtree);

						tree.setChildren(subtree);
						if (num == level) {
							tree.setState("closed");
							tree.setOpen(false);
							tree.setIsParent(true);
						}
						
						if(ou.getChildNodeCount()!=null && ou.getChildNodeCount()>0){
							tree.setIsParent(true);
						}else{
							tree.setIsParent(false);
							tree.setState("open");
							tree.setLeaf(true);
						}
						
						otree.add(tree);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return otree;
	}
}