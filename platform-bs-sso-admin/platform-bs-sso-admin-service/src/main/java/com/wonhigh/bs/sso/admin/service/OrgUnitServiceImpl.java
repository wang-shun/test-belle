package com.wonhigh.bs.sso.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wonhigh.bs.admin.sso.dal.database.OrgUnitMapper;
import com.wonhigh.bs.sso.admin.common.model.OrgUnit;
import com.wonhigh.bs.sso.admin.common.vo.OrgUnitDTO;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 
 * TODO: 组织机构
 * 
 * @author xiao.fz
 * @date 2017年11月7日 下午3:08:17
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service("orgUnitService")
public class OrgUnitServiceImpl extends BaseCrudServiceImpl implements OrgUnitService {
    @Autowired
    private OrgUnitMapper orgUnitMapper;

    @Override
    public BaseCrudMapper init() {
        return orgUnitMapper;
    }

    /**
     * 根据组织父级ID查出下级组织列表，包含组织属性等
     */
    @Override
    public List<OrgUnitDTO> getUnitListByParentId(int parentId, int status) throws ServiceException {

        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("parentId", parentId);
        if (status >= 0) {
            params.put("orgStatus", status);
        }
        List<OrgUnitDTO> list = orgUnitMapper.findByParams(params);

        return list;
    }

    /**
     *  根据组织ID查询组织信息，包含组织属性等
     */
    @Override
    public OrgUnitDTO getUnitByUnitId(int unitId) throws ServiceException {
        return orgUnitMapper.findByKey(unitId);
    }

    @Override
    public List<OrgUnit> selectByUnitCode(String unitCode) throws ServiceException {
        try {
            return orgUnitMapper.selectByUnitCode(unitCode);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Integer queryParentsUnit(String unitId) throws ServiceException {
        try {
            return orgUnitMapper.queryParentsUnit(unitId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrgUnitDTO> getUnitListBySelfId(int selfUnitId, int status) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("selfUnitId", selfUnitId);
        if (status >= 0) {
            params.put("orgStatus", status);
        }
        List<OrgUnitDTO> list = orgUnitMapper.findByParams(params);

        return list;
    }

}
