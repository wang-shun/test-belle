package com.wonhigh.bs.sso.server.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.server.common.constants.Properties;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.common.vo.BizConfigVO;
import com.wonhigh.bs.sso.service.BizConfigService;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

@Service
public class BizConfigManagerImpl extends BaseCrudManagerImpl  implements BizConfigManager {
	
	@Resource
	private LdapTemplate ldapTemplatePool;
	
	@Resource
	private LdapTemplate ldapTemplate;
	
	@Resource
	private Properties properties;
	
	@Resource
    private BizConfigService bizConfigService;
	
	@Override
	protected BaseCrudService init() {
		return bizConfigService;
	}	

	@Override
	public List<BizConfigDTO> findAll() {
		return bizConfigService.findAll();
	}

	@Override
	public BizConfigDTO findByPrimaryKey(String bizCode) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("bizCode", bizCode);
		return bizConfigService.findbyCondition(map);
	}

	@Override
	public BizConfigDTO findByBizName(String bizName) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("bizName", bizName);
		return bizConfigService.findbyCondition(map);
	}

    @Override
    public List<BizConfigVO> selectForApi(Map<String, Object> params) {
        return bizConfigService.selectForApi(params);
    }

}
