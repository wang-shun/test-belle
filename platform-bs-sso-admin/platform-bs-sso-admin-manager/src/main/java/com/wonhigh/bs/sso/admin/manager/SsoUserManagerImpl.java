package com.wonhigh.bs.sso.admin.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapOperationsCallback;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.model.PageResult;
import com.wonhigh.bs.sso.admin.common.model.SsoUser;
import com.wonhigh.bs.sso.admin.common.model.SsoUserDTO;
import com.wonhigh.bs.sso.admin.common.util.DateUtil;
import com.wonhigh.bs.sso.admin.common.vo.BizConfigDTO;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.utils.SimplePage;

/**
 * @author user
 * @date 2017-09-04 11:36:04
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
@Service("ssoUserManager")
@Transactional
class SsoUserManagerImpl implements SsoUserManager {

	@Resource
	private LdapTemplate ldapTemplate;
	@Resource
	private LdapTemplate ldapTemplatePool;
	@Resource
	private Properties properties;
	@Resource
	private BizConfigManager bizConfigManager;

	@Override
	public void create(SsoUser user) {
		String dn2 = properties.getSsoUserBaseDnOu();
		user.setDn2(dn2);
		ldapTemplate.create(user);
	}

	@Override
	public void update(SsoUser user) {
		ldapTemplate.update(user);
	}

	@Override
	public void delete(SsoUser user) {
		// 先备份
		Name odn = user.getDn();
		String dn = odn.toString();
		String nUid = user.getUid() + "-del" + System.currentTimeMillis();
		String oUid = user.getUid();
		String dn2 = dn.replace(properties.getSsoUserBaseDnOu(),
				properties.getDeleteDataBaseDnOu());
		dn2 = dn2.replace(user.getUid(), nUid);
		Name ndn = null;
		try {
			ndn = new LdapName(dn2);
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}
		user.setDn(ndn);
		user.setDelFlag(1);
		user.setUid(nUid);
		user.setUpdateTime(DateUtil.getTimeFormat(new Date()));
		ldapTemplate.create(user);

		// 再删除
		user.setDn(odn);
		user.setUid(oUid);
		ldapTemplate.delete(user);
	}

	@Override
	public List<SsoUser> findAll() {
		ldapTemplate.setDefaultCountLimit(100);
		return ldapTemplate.findAll(SsoUser.class);
	}

	/**
	 * 此方法不能用連接池
	 */
	@Override
	public SsoUser getLoginDn(String uid, String password) {
		String basedn = properties.getSsoManagerBaseDn();
		ContainerCriteria containerCriteria = LdapQueryBuilder.query().base(basedn)
				.where("uid").is(uid);
		ldapTemplate.authenticate(containerCriteria, password);
		SsoUser ssoUser = ldapTemplate
				.findOne(containerCriteria, SsoUser.class);
		return ssoUser;
	}

	public boolean authenticate(String userDn, String credentials) {
		DirContext ctx = null;
		try {
			ctx = ldapTemplate.getContextSource().getContext(userDn,
					credentials);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			// ItisimperativethatthecreatedDirContextinstanceisalwaysclosed
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public List<SsoUser> findByCondition(SsoUser condition) {

		String baseDn = properties.getSsoUserBaseDn();
		ContainerCriteria attrQuery = null;

		if (StringUtils.isNotEmpty(condition.getEmail())
				&& !ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE.equals(condition.getEmail())) {
			attrQuery = LdapQueryBuilder.query().where("mail")
					.is(condition.getEmail());
		}
		if (StringUtils.isNotEmpty(condition.getLoginName())) {
			if (attrQuery != null) {
				attrQuery.or(LdapQueryBuilder.query().where("uid")
						.is(condition.getLoginName()));
			} else {
				attrQuery = LdapQueryBuilder.query().where("uid")
						.is(condition.getLoginName());
			}
		}
		if (StringUtils.isNotEmpty(condition.getMobile())
				&& !ApiConstants.SSOUSER_MOBILE_DEFAULT_VALUE.equals(condition.getMobile())) {
			if (attrQuery != null) {
				attrQuery.or(LdapQueryBuilder.query().where("mobile")
						.is(condition.getMobile()));
			} else {
				attrQuery = LdapQueryBuilder.query().where("mobile")
						.is(condition.getMobile());
			}
		}
		if (condition.getState() != null) {
			if (attrQuery != null) {
				attrQuery.or(LdapQueryBuilder.query().where("state")
						.is(condition.getState() + ""));
			} else {
				attrQuery = LdapQueryBuilder.query().where("state")
						.is(condition.getState() + "");
			}
		}

		LdapQueryBuilder queryBuilder = LdapQueryBuilder.query().searchScope(
				SearchScope.SUBTREE);
		if (!"".equals(baseDn)) {
			queryBuilder = LdapQueryBuilder.query().base(baseDn);
		}
		ContainerCriteria query = queryBuilder.where("objectclass").is(
				"SsoUser");
		if (attrQuery != null) {
			query.and(attrQuery);
		}

		List<SsoUser> list = ldapTemplate.find(query, SsoUser.class);

		return list;
	}

	@SuppressWarnings("rawtypes")
	public List<SsoUser> getAllPersonNames() {

		final AttributesMapper attributesMapper = new AttributesMapper() {
			@Override
			public Object mapFromAttributes(Attributes attributes)
					throws NamingException {
				SsoUser user = new SsoUser();
				user.setLoginName((String) attributes.get("uid").get());
				user.setEmail((String) attributes.get("mail").get());
				user.setTelephoneNumber((String) attributes.get(
						"telephoneNumber").get());
				return user;
			}
		};

		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		Integer currentPage = 0;
		int pageSize = 2;
		int pageNo = 2;

		final Map<String, Integer> pager = new HashMap<String, Integer>();
		pager.put("currentPage", currentPage);
		pager.put("pageSize", pageSize);
		pager.put("pageNo", pageNo);

		final PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(
				pageSize);

		List<SsoUser> result = new LinkedList<SsoUser>();
		boolean haveFindFlag = false;
		do {
			List<SsoUser> oneResult = ldapTemplatePool.search("o=wonhigh",
					"(&(objectclass=SsoUser))", searchControls,
					attributesMapper, processor);
			currentPage++;
			if (currentPage == pageNo) {
				result.addAll(oneResult);
				haveFindFlag = true;
			}

		} while (processor.hasMore() && !haveFindFlag);

		List<SsoUser> r = result;
		return r;
	}

	@Override
	public List<SsoUser> findByCondition(PageResult page) {

		SsoUser condition = (SsoUser) page.getCondition();
		final String basedn = properties.getSsoUserBaseDn();
		AndFilter andFilter = new AndFilter();
		if (StringUtils.isNotEmpty(condition.getLoginName())) {
			andFilter.append(new LikeFilter("uid", "*"
					+ condition.getLoginName() + "*"));
		}
		if (StringUtils.isNotEmpty(condition.getMobile())) {
			andFilter.append(new LikeFilter("mobile", "*"
					+ condition.getMobile() + "*"));
		}
		if (StringUtils.isNotEmpty(condition.getEmployeeNumber())) {
			andFilter.append(new LikeFilter("employeeNumber", "*"
					+ condition.getEmployeeNumber() + "*"));
		}
		if (StringUtils.isNotEmpty(condition.getEmail())) {
			andFilter.append(new LikeFilter("mail", "*" + condition.getEmail()
					+ "*"));
		}
		if (StringUtils.isNotEmpty(condition.getSureName())) {
			andFilter.append(new EqualsFilter("sn", condition.getSureName()));
		}
		if (StringUtils.isNotEmpty(condition.getOrganizationCodeCond())) {
			andFilter.append(new LikeFilter("organization-code", condition.getOrganizationCodeCond()+"*"));
		}

		andFilter.and(new EqualsFilter("objectclass", "SsoUser"));
		OrFilter orFilter2 = new OrFilter();
		if(StringUtils.isNotEmpty(condition.getOrganizationCode())){
			String limits = condition.getOrganizationCode();
			String[] limitArr = limits.split(",");
			for (String limit : limitArr) {
				orFilter2.append(new LikeFilter("organization-code", limit + "*"));
			}
		}
		andFilter.and(orFilter2);
		
		//绑定条件过滤
		String bindState = condition.getBindState();
		String bizConfigCode = condition.getBizConfigCode();
		if(bindState!=null){
			//TODO:查询所有业务系统
			Map<String, Object> params = new HashMap<String, Object>();
    		SimplePage p = new SimplePage(1, 50, 50);
    		List<BizConfigDTO> list = new ArrayList<BizConfigDTO>();
			try {
				list = bizConfigManager.findByPage(p, null, null, params);
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			 //已绑定
			if(StringUtils.equals(bindState, ApiConstants.SSOUSER_IS_BIND_BIZ)){
				//已绑定任意一个
				if(StringUtils.isEmpty(bizConfigCode)){ 
					OrFilter or = new OrFilter();
					for (BizConfigDTO b : list) {
						or.append(new LikeFilter("biz-user", "*\"" + b.getBizCode() + "\":*"));
					}
					andFilter.and(or);
				}else{ 
					//已绑定某一个
					andFilter.and(new LikeFilter("biz-user", "*\"" + bizConfigCode + "\":*"));
				}
			}else if(StringUtils.equals(bindState, ApiConstants.SSOUSER_NOT_BIND_BIZ)){ 
				//未绑定
				if(StringUtils.isEmpty(bizConfigCode)){ 
					//未绑定全部
					for (BizConfigDTO b : list) {
						andFilter.and(new NotFilter(new LikeFilter("biz-user", "*\"" + b.getBizCode() + "\":*")));
					}
				}else{ 
					//未绑定某一个
					andFilter.and(new NotFilter(new LikeFilter("biz-user", "*\"" + bizConfigCode + "\":*")));
				}
			}
		}

		if (StringUtils.isNotEmpty(condition.getEmployeeType())) {
			andFilter.and(new EqualsFilter("employeeType", condition
					.getEmployeeType()));
		}
		if (condition.getState() != null) {
			andFilter.and(new EqualsFilter("state", condition.getState()));
		}

		final AttributesMapper attributesMapper = new AttributesMapper() {
			@Override
			public Object mapFromAttributes(Attributes attributes)
					throws NamingException {
				SsoUser user = new SsoUser();
				user.setUid((String) attributes.get("uid").get());
				user.setLoginName((String) attributes.get("uid").get());
				user.setSureName((attributes.get("sn") == null ? null
						: ((String) attributes.get("sn").get())));
				user.setMobile((attributes.get("mobile") == null ? null
						: ((String) attributes.get("mobile").get())));
				user.setTelephoneNumber(attributes.get("telephoneNumber") == null ? null
						: ((String) attributes.get("telephoneNumber").get()));
				user.setEmployeeNumber(attributes.get("employeeNumber") == null ? null
						: ((String) attributes.get("employeeNumber").get()));
				user.setEmployeeType(attributes.get("employeeType") == null ? null
						: ((String) attributes.get("employeeType").get()));
				user.setEmail(attributes.get("mail") == null ? null
						: ((String) attributes.get("mail").get()));
				user.setCreateTime(attributes.get("create-time-str") == null ? null
						: ((String) attributes.get("create-time-str").get()));
				user.setOrganizationCode(attributes.get("organization-code")==null?null:((String)
				attributes.get("organization-code").get()));
				user.setOrganizationalUnitName(attributes.get("organizationalUnitName")==null?null:((String)
				attributes.get("organizationalUnitName").get()));
				user.setBizUser(attributes.get("biz-user") == null ? null
						: ((String) attributes.get("biz-user").get()));
				user.setUpdateTime(attributes.get("update-time-str") == null ? null
						: ((String) attributes.get("update-time-str").get()));
				user.setSex(attributes.get("sex") == null ? 0 : (Integer
						.valueOf((String) attributes.get("sex").get())));
				user.setState(attributes.get("state") == null ? null : (Integer
						.valueOf((String) attributes.get("state").get())));
				user.setDelFlag(attributes.get("del-flag") == null ? null
						: (Integer.valueOf((String) attributes.get("del-flag")
								.get())));
				user.setDescription(attributes.get("description") == null ? null
						: ((String) attributes.get("description").get()));
				user.setIdCard(attributes.get("id-card") == null ? null
						: ((String) attributes.get("id-card").get()));
				user.setCreater(attributes.get("creater") == null ? null
						: ((String) attributes.get("creater").get()));
				return user;
			}
		};

		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		final Map<String, Integer> pager = new HashMap<String, Integer>();
		pager.put("currentPage", 0);
		pager.put("pageSize", page.getPageSize());
		pager.put("pageNo", page.getPageNo());
		pager.put("totalRecord", 0);
		pager.put("totalPage", 0);
		final int totalSize = page.getTotal();

		final String fs = andFilter.encode();
		final PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(
				page.getPageSize());

		List<SsoUser> rs = SingleContextSource.doWithSingleContext(
				ldapTemplate.getContextSource(),
				new LdapOperationsCallback<List<SsoUser>>() {
					@Override
					public List<SsoUser> doWithLdapOperations(
							LdapOperations operations) {
						List<SsoUser> result = new ArrayList<SsoUser>();
						boolean goonflag = true;
						do {
							List<SsoUser> oneResult = operations.search(basedn,
									fs, searchControls, attributesMapper,
									processor);
							int currentPage = pager.get("currentPage");
							currentPage++;
							int pageNo = pager.get("pageNo");
							int totalRecord = pager.get("totalRecord");
							
							if(oneResult==null){
				        		  oneResult=new ArrayList<SsoUser>();
				        	  }
							
							totalRecord += oneResult.size();
							int totalPage = pager.get("totalPage");
							if (oneResult != null && oneResult.size() > 0) {
								totalPage++;
							}
							if (currentPage == pageNo) {
								result.addAll(oneResult);
							}
							pager.put("currentPage", currentPage);
							pager.put("totalRecord", totalRecord);
							pager.put("totalPage", totalPage);
							
							if(totalSize>0 && pageNo==currentPage){
								pager.put("totalRecord", totalSize);
								goonflag = false;
							}
						} while (processor.hasMore() && goonflag);

						return result;
					}
				});

		int totalPage = pager.get("totalPage");
		int totalRecord = pager.get("totalRecord");
		page.setTotalPage(totalPage);
		page.setTotal(totalRecord);
		page.setRows(rs);

		return rs;
	}

	@Override
	public SsoUser findByPrimaryKey(String loginName) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn)
				.where("uid").is(loginName).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public SsoUser findByEmployeeNumber(String employeeNumber) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn)
				.where("employeeNumber").is(employeeNumber).and("objectclass")
				.is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public SsoUser findByMobile(String mobile) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn)
				.where("mobile").is(mobile).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public SsoUser findByEmail(String email) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn)
				.where("email").is(email).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public void dimission(String employeeNumber) {
		SsoUser ssoUser = this.findByEmployeeNumber(employeeNumber);
		if(ssoUser==null){
			return;
		}
		// 删除sso账号
		this.delete(ssoUser);
		/*
		 * if(ssoUser!=null){ //调用各业务系统删除用户接口 String bizUserStr =
		 * ssoUser.getBizUser(); JSONObject bizUserJ =
		 * JSONObject.parseObject(bizUserStr); Set<Entry<String,Object>>
		 * entrySet = bizUserJ.entrySet(); for (Entry<String, Object> entry :
		 * entrySet) { String bizCode = entry.getKey(); BizConfig bizConfig =
		 * bizConfigManager.findByPrimaryKey(bizCode); if(bizConfig==null)
		 * continue; String bizLoginName = (String) entry.getValue(); try{
		 * Map<String,String> paramsMap = new HashMap<String, String>();
		 * paramsMap.put("ssoLoginName ", ssoUser.getLoginName());
		 * paramsMap.put("bizLoginName ", bizLoginName); String nonce = "123";
		 * String timestamp = System.currentTimeMillis()+"";
		 * paramsMap.put("nonce", nonce); paramsMap.put("timestamp", timestamp);
		 * paramsMap.put("bizCode", bizConfig.getBizCode()); String sign =
		 * SignUtil.sign(bizConfig.getBizSecret(), "POST", "checkUserPwd",
		 * paramsMap); paramsMap.put("sign", sign); Map<String, Object> map =
		 * HttpClientUtil.doPost(bizConfig.getDelUserUrl(), paramsMap); int code
		 * = (int) map.get("code"); if(code==1){ String result = (String)
		 * map.get("result"); JSONObject jsonObject =
		 * JSONObject.parseObject(result); String msg =
		 * jsonObject.getString("msg"); int code2 =
		 * jsonObject.getIntValue("code"); if(code2!=1){
		 * System.out.println("删除"+
		 * bizConfig.getBizName()+"用户"+bizLoginName+"失败："+msg); }else{
		 * System.out
		 * .println("删除"+bizConfig.getBizName()+"用户"+bizLoginName+"成功"); }
		 * }else{
		 * System.out.println("删除"+bizConfig.getBizName()+"用户"+bizLoginName
		 * +"失败：网络错误"); } }catch (Exception e){ e.printStackTrace();
		 * System.out.println
		 * ("删除"+bizConfig.getBizName()+"用户"+bizLoginName+"失败："+e.getMessage());
		 * } } }
		 */
	}

	@Override
	public SsoUser findByBizUser(String bizCode, String bizLoginName) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn)
				.where("biz-user")
				.like("*" + bizCode + "\":\"" + bizLoginName + "*")
				.and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public void updateInAdditionToPassword(SsoUser user) {
		SsoUserDTO updateUser = new SsoUserDTO();
		updateUser.setUid(user.getUid());
		updateUser.setDn(user.getDn());
		updateUser.setDn2(user.getDn2());
		updateUser.setLoginName(user.getLoginName());
		updateUser.setSureName(user.getSureName());
		updateUser.setMobile(user.getMobile());
		updateUser.setTelephoneNumber(user.getTelephoneNumber());
		updateUser.setEmployeeNumber(user.getEmployeeNumber());
		updateUser.setEmployeeType(user.getEmployeeType());
		updateUser.setEmail(user.getEmail());
		updateUser.setBizUser(user.getBizUser());
		updateUser.setOrganizationalUnitName(user.getOrganizationalUnitName());
		updateUser.setOrganizationCode(user.getOrganizationCode());
		updateUser.setOrganizationId(user.getOrganizationId());
		updateUser.setCreater(user.getCreater());
		updateUser.setUpdateTime(user.getUpdateTime());
		updateUser.setCreateTime(user.getCreateTime());
		updateUser.setSex(user.getSex());
		updateUser.setDelFlag(user.getDelFlag());
		updateUser.setDescription(user.getDescription());
		updateUser.setIdCard(user.getIdCard());
		updateUser.setState(user.getState());
		this.ldapTemplate.update(updateUser);
	}

}