package com.wonhigh.bs.sso.server.manager;

import java.util.List;

import com.wonhigh.bs.sso.server.common.model.PageResult;
import com.wonhigh.bs.sso.server.common.model.SsoUser;

public interface SsoUserManager {

	/**
	 * 在ldap创建sso用户
	 * @param user
	 */
	void create(SsoUser user);

	/**
	 * 修改信息 除了密码字段
	 * @param user
	 */
	void updateInAdditionToPassword(SsoUser user);

	/**
	 * 更新ldap中sso用户信息
	 * @param user
	 */
	void update(SsoUser user);

	List<SsoUser> findByCondition(SsoUser condition);

	List<SsoUser> findAll();

	SsoUser getLoginDn(String uid, String password);

	SsoUser getLoginDnByMobile(String mobile, String password);

	List<SsoUser> findByCondition(PageResult page);

	SsoUser findByPrimaryKey(String loginName);

	SsoUser findByEmployeeNumber(String employeeNumber);

	SsoUser findByMobile(String mobile);

	SsoUser findByEmail(String email);

	SsoUser findByBizUser(String bizCode, String bizLoginName);
}