package com.wonhigh.bs.sso.server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

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
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wonhigh.bs.sso.server.common.constants.Properties;
import com.wonhigh.bs.sso.server.common.model.PageResult;
import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.model.SsoUserDTO;

/**
 * @author user
 * @date  2017-09-04 11:36:04
 * @version 1.0.0
 * @copyright (C) 2013 WonHigh Information Technology Co.,Ltd 
 * All Rights Reserved. 
 * 
 * The software for the WonHigh technology development, without the 
 * company's written consent, and any other individuals and 
 * organizations shall not be used, Copying, Modify or distribute 
 * the software.
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
	public List<SsoUser> findAll() {
        return ldapTemplatePool.findAll(SsoUser.class);
	}

    
	@Override
	public SsoUser getLoginDn(String uid, String password) {
		String basedn = properties.getSsoUserBaseDn();
		ContainerCriteria containerCriteria = LdapQueryBuilder.query().base(basedn).where("uid").is(uid)
				.and("objectclass").is("SsoUser");
		String userDn = "uid="+uid+","+properties.getSsoUserBaseDn()+","+properties.getSampleLdapBase();
		try{
			boolean authenticate = this.authenticate(userDn, password);
			if(authenticate){
				SsoUser ssoUser = ldapTemplatePool.findOne(containerCriteria, SsoUser.class);
				return ssoUser;
			}
		}catch(Exception e){
			ldapTemplate.authenticate(containerCriteria, password);
			SsoUser SsoUser = ldapTemplatePool.findOne(containerCriteria, SsoUser.class);
			return SsoUser;
		}
		return null;
	}
	
	@Override
	public SsoUser getLoginDnByMobile(String mobile, String password) {
		String basedn = properties.getSsoUserBaseDn();
		ContainerCriteria containerCriteria = LdapQueryBuilder.query().base(basedn).where("mobile").is(mobile)
				.and("objectclass").is("SsoUser");
		ldapTemplate.authenticate(containerCriteria, password);
		SsoUser ssoUser = ldapTemplatePool.findOne(containerCriteria, SsoUser.class);
		return ssoUser;
	}

	public boolean authenticate(String userDn, String credentials) {    
	    DirContext ctx = null;    
	    try {    
	        ctx = ldapTemplate.getContextSource().getContext(userDn, credentials);  
	        return true;    
	    } catch (Exception e) {    
	        throw e;    
	    } finally {    
	        // ItisimperativethatthecreatedDirContextinstanceisalwaysclosed    
	        LdapUtils.closeContext(ctx);    
	    }    
	}

	@Override
	public List<SsoUser> findByCondition(SsoUser condition) {
		
		String baseDn = properties.getSsoUserBaseDn();
		ContainerCriteria attrQuery = null;
		
		if(StringUtils.isNotEmpty(condition.getEmail()) && !condition.getEmail().equals("NULL")){
			attrQuery = LdapQueryBuilder.query().where("mail").is(condition.getEmail());
		}
		if(StringUtils.isNotEmpty(condition.getLoginName())){
			if(attrQuery!=null){
				attrQuery.or(LdapQueryBuilder.query().where("uid").is(condition.getLoginName()));
			}else{
				attrQuery = LdapQueryBuilder.query().where("uid").is(condition.getLoginName());
			}
		}
		if(StringUtils.isNotEmpty(condition.getMobile()) && !condition.getMobile().equals("0")){
			if(attrQuery!=null){
				attrQuery.or(LdapQueryBuilder.query().where("mobile").is(condition.getMobile()));
			}else{
				attrQuery = LdapQueryBuilder.query().where("mobile").is(condition.getMobile());
			}
		}
		if(condition.getState()!=null){
			if(attrQuery!=null){
				attrQuery.or(LdapQueryBuilder.query().where("state").is(condition.getState()+""));
			}else{
				attrQuery = LdapQueryBuilder.query().where("state").is(condition.getState()+"");
			}
		}
		
		LdapQueryBuilder queryBuilder = LdapQueryBuilder.query().searchScope(SearchScope.SUBTREE);
		if(!"".equals(baseDn)){
			queryBuilder = LdapQueryBuilder.query().base(baseDn);
		}
		ContainerCriteria query = queryBuilder.where("objectclass").is("SsoUser");
		if(attrQuery!=null){
			query.and(attrQuery);
		}
		
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		
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
            	user.setTelephoneNumber((String) attributes.get("telephoneNumber").get());
                return user;
            }
        };
        
		  final SearchControls searchControls = new SearchControls();
		  searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		  Integer currentPage = 0;
		  int pageSize = 2;
		  int pageNo = 2;
		  
		  final Map<String,Integer> pager = new HashMap<String,Integer>();
		  pager.put("currentPage", currentPage);
		  pager.put("pageSize", pageSize);
		  pager.put("pageNo", pageNo);
		  
		  final PagedResultsDirContextProcessor processor =
		        new PagedResultsDirContextProcessor(pageSize);
		  
		  List<SsoUser> result = new LinkedList<SsoUser>();
	        boolean haveFindFlag = false;
	        do {
	          List<SsoUser> oneResult = ldapTemplatePool.search(
	            "o=wonhigh",
	            "(&(objectclass=SsoUser))",
	            searchControls,
	            attributesMapper,
	            processor);
	          currentPage++;
	      	  if(currentPage==pageNo){
	      		  result.addAll(oneResult);
	      		  haveFindFlag = true;
	      	  }
	          
	        } while(processor.hasMore() && !haveFindFlag);
	        
	        List<SsoUser> r  = result;
		  return r;
		}

	@Override
	public List<SsoUser> findByCondition(PageResult page) {
		
		SsoUser condition = (SsoUser) page.getCondition();
		final String basedn = properties.getSsoUserBaseDn();
		OrFilter orFilter = new OrFilter();
		if(StringUtils.isNotEmpty(condition.getLoginName())){
			orFilter.append(new LikeFilter("uid", "*"+condition.getLoginName()+"*"));
		}
		if(StringUtils.isNotEmpty(condition.getMobile())){
			orFilter.append(new LikeFilter("mobile", "*"+condition.getMobile()+"*"));
		}
		if(StringUtils.isNotEmpty(condition.getEmployeeNumber())){
			orFilter.append(new LikeFilter("employeeNumber", "*"+condition.getEmployeeNumber()+"*"));
		}
		if(StringUtils.isNotEmpty(condition.getEmail())){
			orFilter.append(new LikeFilter("mail", "*"+condition.getEmail()+"*"));
		}
		if(StringUtils.isNotEmpty(condition.getSureName())){
			orFilter.append(new EqualsFilter("sn", condition.getSureName()));
		}
		
		AndFilter andFlter = new AndFilter(); 
        andFlter.and(new EqualsFilter("objectclass", "SsoUser"));
        andFlter.and(orFilter);
		
		if(StringUtils.isNotEmpty(condition.getEmployeeType())){
			andFlter.and(new EqualsFilter("employeeType", condition.getEmployeeType()));
		}
		if(condition.getState()!=null){
			andFlter.and(new EqualsFilter("state",condition.getState()));
		}
		
		final AttributesMapper attributesMapper = new AttributesMapper() {
            @Override
            public Object mapFromAttributes(Attributes attributes)
                    throws NamingException {
            	SsoUser user = new SsoUser();
            	user.setUid((String) attributes.get("uid").get());
            	user.setLoginName((String) attributes.get("uid").get());
            	user.setSureName((attributes.get("sn")==null?null:((String) attributes.get("sn").get())));
            	user.setMobile((attributes.get("mobile")==null?null:((String) attributes.get("mobile").get())));
            	user.setTelephoneNumber(attributes.get("telephoneNumber")==null?null:((String) attributes.get("telephoneNumber").get()));
            	user.setEmployeeNumber(attributes.get("employeeNumber")==null?null:((String) attributes.get("employeeNumber").get()));
            	user.setEmployeeType(attributes.get("employeeType")==null?null:((String) attributes.get("employeeType").get()));
            	user.setEmail(attributes.get("mail")==null?null:((String) attributes.get("mail").get()));
            	user.setCreateTime(attributes.get("create-time-str")==null?null:((String) attributes.get("create-time-str").get()));
            	//user.setHrmsUser(attributes.get("hrms-user")==null?null:((String) attributes.get("hrms-user").get()));
            	//user.setIntegralUnionUser(attributes.get("integral-union-user")==null?null:((String) attributes.get("integral-union-user").get()));
            	//user.setMiuOrganUser(attributes.get("miu-organ-user")==null?null:((String) attributes.get("miu-organ-user").get()));
            	//user.setOaUser(attributes.get("oa-user")==null?null:((String) attributes.get("oa-user").get()));
            	//user.setRetailOcpUser(attributes.get("retail-ocp-user")==null?null:((String) attributes.get("retail-ocp-user").get()));
            	//user.setRetailSspUser(attributes.get("retail-ssp-user")==null?null:((String) attributes.get("retail-ssp-user").get()));
            	//user.setRetailEdiUser(attributes.get("retail-edi-user")==null?null:((String) attributes.get("retail-edi-user").get()));
            	//user.setYongyouucUser(attributes.get("yongyouuc-user")==null?null:((String) attributes.get("yongyouuc-user").get()));
            	user.setBizUser(attributes.get("biz-user")==null?null:((String) attributes.get("biz-user").get()));
            	user.setUpdateTime(attributes.get("update-time-str")==null?null:((String) attributes.get("update-time-str").get()));
            	
            	javax.naming.directory.Attribute sexObj=attributes.get("sex");
            	Integer sexInt=-1;
            	if(sexObj!=null){
            		Object obj=sexObj.get();
            		sexInt=(obj==null?-1:Integer.valueOf(obj+""));
            	}
            	
            	user.setSex(sexInt);
            	user.setState(attributes.get("state")==null?null:(Integer.valueOf((String)attributes.get("state").get())));
            	user.setDelFlag(attributes.get("del-flag")==null?null:(Integer.valueOf((String)attributes.get("del-flag").get())));
            	user.setDescription(attributes.get("description")==null?null:((String) attributes.get("description").get()));
            	user.setIdCard(attributes.get("id-card")==null?null:((String) attributes.get("id-card").get()));
                return user;
            }
        };
        
		  final SearchControls searchControls = new SearchControls();
		  searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		  final Map<String,Integer> pager = new HashMap<String,Integer>();
		  pager.put("currentPage", 0);
		  pager.put("pageSize", page.getPageSize());
		  pager.put("pageNo", page.getPageNo());
		  pager.put("totalRecord", 0);
		  pager.put("totalPage", 0);
		  
		  final String fs = andFlter.encode();
		  final PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(page.getPageSize());
		  
        List<SsoUser> rs = SingleContextSource.doWithSingleContext(
		        ldapTemplate.getContextSource(), new LdapOperationsCallback<List<SsoUser>>() {
			@Override
		      public List<SsoUser> doWithLdapOperations(LdapOperations operations) {
		        List<SsoUser> result = new ArrayList<SsoUser>();

		        do {
		          List<SsoUser> oneResult = operations.search(
	        		basedn,
	  	            fs,
	  	            searchControls,
	  	            attributesMapper,
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
	    		  if(oneResult!=null && oneResult.size()>0){
	    			  totalPage++;
	    		  }
	        	  if(currentPage==pageNo){
	        		  result.addAll(oneResult);
	        	  }
		          pager.put("currentPage", currentPage);
		          pager.put("totalRecord", totalRecord);
		          pager.put("totalPage", totalPage);
		          
		        } while(processor.hasMore());

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
		ContainerCriteria query = LdapQueryBuilder.query().base(dn).where("uid").is(loginName).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if(list!=null && list.size()>0){
        	return list.get(0);
        }
		return null;
	}

	@Override
	public SsoUser findByEmployeeNumber(String employeeNumber) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn).where("employeeNumber").is(employeeNumber).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if(list!=null && list.size()>0){
        	return list.get(0);
        }
		return null;
	}

	@Override
	public SsoUser findByMobile(String mobile) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn).where("mobile").is(mobile).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if(list!=null && list.size()>0){
        	return list.get(0);
        }
		return null;
	}

	@Override
	public SsoUser findByEmail(String email) {
		String dn = properties.getSsoUserBaseDn();
		ContainerCriteria query = LdapQueryBuilder.query().base(dn).where("email").is(email).and("objectclass").is("SsoUser");
		List<SsoUser> list = ldapTemplatePool.find(query, SsoUser.class);
		if(list!=null && list.size()>0){
        	return list.get(0);
        }
		return null;
	}

}