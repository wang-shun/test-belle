package com.wonhigh.bs.sso.server.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wonhigh.bs.sso.server.common.model.SsoUser;
import com.wonhigh.bs.sso.server.common.util.DateUtil;
import com.wonhigh.bs.sso.server.common.util.SsoUserMysqlLdapTransformUtil;
import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.service.SsoUniformUserService;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * SsoUniformUserManagerImpl
 * 
 * @author user
 * @date 2017年11月18日 下午1:52:40
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service("ssoUniformUserManager")
@Transactional
public class SsoUniformUserManagerImpl extends BaseCrudManagerImpl implements SsoUniformUserManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(SsoUniformUserManagerImpl.class);

	@Resource
	private SsoUniformUserService ssoUniformUserService;

	@Resource
	private SsoUserManager ssoUserManager;

	@Override
	public BaseCrudService init() {
		return ssoUniformUserService;
	}

	@Override
	public int findByBizUser(String bizCode, String bizLoginName) throws ServiceException {
		StringBuilder queryCondition = new StringBuilder();
		queryCondition.append(" AND biz_user like \"%\\\"" + bizCode + "\\\":\\\"" + bizLoginName + "\\\"%\" ");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("queryCondition", queryCondition.toString());
		int total = this.ssoUniformUserService.findCount(map);
		return total;
	}

	@Override
	public SsoUniformUserDTO findByLoginName(String loginName) throws DaoException {
		Map<String, Object> queryMap = new HashMap<String, Object>(1);
		queryMap.put("loginName", loginName);
		return ssoUniformUserService.findbyCondition(queryMap);
	}

	@Override
	public SsoUniformUserDTO findByMobile(String mobile) throws DaoException {
		Map<String, Object> queryMap = new HashMap<String, Object>(1);
		queryMap.put("mobile", mobile);
		return ssoUniformUserService.findbyCondition(queryMap);
	}

	@Override
	public int updateToMysqlAndLdap(SsoUniformUserDTO user) throws ManagerException {
		//修改mysql
		try {
			user.setUpdateTime(new Date());
			LOGGER.info("开始修改ssoUnitformUser(mysql):" + user);
			this.modifyById(user);
			LOGGER.info("修改ssoUnitformUser(mysql)成功:" + user);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("修改ssoUnitformUser(mysql)失败,e=" + e.getMessage());
			throw new ManagerException(e);
		}

		//ldap
		try {
			SsoUser userLdap = ssoUserManager.findByPrimaryKey(user.getLoginName());
			if (userLdap == null) {
				LOGGER.error("mysql和ldap数据不一致, 创建新的Ldap数据, mysql=" + user);
				userLdap = new SsoUser();
				userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
				LOGGER.info("开始添加SsoUser到LDAP：" + userLdap.toString());
				ssoUserManager.create(userLdap);
				LOGGER.info("添加SsoUser到LDAP成功：" + userLdap.toString());
				return 1;
			}
			// LDAP数据存在，则拷贝数据库数据到LDAP
			userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
			LOGGER.info("开始修改ssoUser(LDAP):" + userLdap);
			if (StringUtils.isNotEmpty(user.getPassword())) {
				userLdap.setPassword(user.getPassword());
				ssoUserManager.update(userLdap);
			} else {
				ssoUserManager.updateInAdditionToPassword(userLdap);
			}
			LOGGER.info("修改SsoUser成功(LDAP)：" + userLdap.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("修改SsoUser(LDAP)失败, e=" + e.getMessage());
			throw new ManagerException(e);
		}
		return 1;
	}

	/**
	 * 拷贝实体SsoUniformUserDTO  to  SsoUser
	 * 
	 * @param user
	 * @param userLdap
	 * @param isNeedCreat
	 * @return
	 */
	private SsoUser setSsoUserDtoToSsoUserLdap(SsoUniformUserDTO user, SsoUser userLdap, boolean isNeedCreat) {
		userLdap.setLoginName(user.getLoginName());
		userLdap.setUid(user.getLoginName());
		userLdap.setSureName(user.getSureName());
		userLdap.setTelephoneNumber(user.getTelephoneNumber());
		userLdap.setEmployeeNumber(user.getEmployeeNumber());
		userLdap.setDescription(userLdap.getDescription());
		userLdap.setEmail(user.getEmail());
		userLdap.setMobile(user.getMobile());
		userLdap.setSex(user.getSex());
		userLdap.setIdCard(user.getIdCard());
		userLdap.setState(user.getState());
		userLdap.setDelFlag(user.getDelFlag());
		userLdap.setEmployeeType(user.getEmployeeType());
		userLdap.setOrganizationalUnitName(user.getOrganizationalUnitName());
		userLdap.setOrganizationCode(user.getOrganizationCode());
		String date = DateUtil.getDateTimeFormat(new Date());
		if (isNeedCreat) {
			userLdap.setCreateTime(date);
		}
		userLdap.setUpdateTime(date);
		userLdap.setCreater(user.getCreateUser());
		userLdap.setPassword(user.getPassword());
		userLdap.setBizUser(user.getBizUser());
		return userLdap;
	}

}
