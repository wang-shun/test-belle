package com.wonhigh.bs.sso.admin.manager;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.model.PageResult;
import com.wonhigh.bs.sso.admin.common.model.SsoUser;
import com.wonhigh.bs.sso.admin.common.model.SsoUserDTO;
import com.yougou.logistics.base.common.exception.ServiceException;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:33:06
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoUserManager  {
	/**
	 * 创建新用户
	 * @param user
	 */
	public void create(SsoUser user);
	
	/**
	 * 修改用户信息
	 * @param user
	 */
	public void update(SsoUser user) ;
	
	/**
	 * 修改信息 除了密码字段
	 * @param user
	 */
	public void updateInAdditionToPassword(SsoUser user) ;
	
	/**
	 * 删除用户
	 * @param user
	 */
	public void delete(SsoUser user) ;
	
	/**
	 * 条件查询
	 * @param condition
	 * @return
	 */
	public List<SsoUser> findByCondition(SsoUser condition) ;
	
	/**
	 * 查询所有用户
	 * @return
	 */
	public List<SsoUser> findAll() ;
	
	/**
	 * 登录验证
	 * @param uid
	 * @param password
	 * @return
	 */
	public SsoUser getLoginDn(String uid, String password);
	
	/**
	 * 分页查询
	 * @param page
	 * @return
	 */
	public List<SsoUser> findByCondition(PageResult page);
	
	/**
	 * 根据主键查询
	 * @param loginName
	 * @return
	 */
	public SsoUser findByPrimaryKey(String loginName);
	
	/**
	 * 根据工号查询
	 * @param employeeNumber
	 * @return
	 */
	public SsoUser findByEmployeeNumber(String employeeNumber);
	
	/**
	 * 根据手机号查询
	 * @param mobile
	 * @return
	 */
	public SsoUser findByMobile(String mobile);
	
	/**
	 * 根据邮箱查询
	 * @param email
	 * @return
	 */
	public SsoUser findByEmail(String email);
	
	/**
	 * 根据业务系统查询
	 * @param bizCode
	 * @param bizLoginName
	 * @return
	 */
	public SsoUser findByBizUser(String bizCode, String bizLoginName);
	
	
	/**
	 * 员工离职
	 * @param employeeNumber
	 */
	public void dimission(String employeeNumber);
	
}