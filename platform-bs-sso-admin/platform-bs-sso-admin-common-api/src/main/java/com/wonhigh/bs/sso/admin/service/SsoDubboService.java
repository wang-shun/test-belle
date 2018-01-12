package com.wonhigh.bs.sso.admin.service;

import com.wonhigh.bs.sso.admin.dto.SsoTokenEntityDto;
import com.wonhigh.bs.sso.admin.dto.SsoUserEntityDto;
import com.yougou.logistics.base.common.exception.ServiceException;

/**
 * sso提供的dubbo服务接口
 * 
 * @author yang.wei
 * @since 2016-12-15
 */
public interface SsoDubboService {

	/**
	 * 注册一账通账户
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public void registerSsoUser(SsoUserEntityDto entity)
			throws ServiceException;

	/**
	 * 通过ssoUser查询账户资料
	 * 
	 * @param ssoUser
	 * @return
	 * @throws ServiceException
	 */
	public SsoUserEntityDto querySsoUser(String ssoUser)
			throws ServiceException;

	/**
	 * 通过业务系统编号与用户名查询账户资料
	 * 
	 * @param bizCode
	 * @param bizUser
	 * @return
	 * @throws ServiceException
	 */
	public SsoUserEntityDto querySsoUser(String bizCode, String bizUser)
			throws ServiceException;

	/**
	 * 更新一账通账户信息，但不更新密码
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public void updateSsoUser(SsoUserEntityDto entity) throws ServiceException;

	/**
	 * 根据原密码，修改一账通账户密码
	 * 
	 * @param ssoUser
	 * @param oldPwd
	 * @param newPwd
	 * @throws ServiceException
	 */
	public void modifySsoUserPassword(String ssoUser, String oldPwd,
			String newPwd) throws ServiceException;

	/**
	 * sso账户绑定业务系统账户
	 * 
	 * @param ssoUser
	 * @param ssoPassword
	 * @param bizCode
	 * @param bizUser
	 * @throws ServiceException
	 */
	public void bindBizUser(String ssoUser, String ssoPassword, String bizCode,
			String bizUser) throws ServiceException;

	/**
	 * sso账户解除对业务系统账户的绑定
	 * 
	 * @param ssoUser
	 * @param ssoPassword
	 * @param bizCode
	 * @throws ServiceException
	 */
	public void unbindBizUser(String ssoUser, String ssoPassword, String bizCode)
			throws ServiceException;

	/**
	 * 通过指定sso账户名锁定
	 * 
	 * @param ssoUser
	 * @throws ServiceException
	 */
	public void lockSsoUser(String ssoUser) throws ServiceException;

	/**
	 * 通过指定业务系统编号和用户名锁定
	 * 
	 * @param bizCode
	 * @param bizUser
	 * @throws ServiceException
	 */
	public void lockSsoUser(String bizCode, String bizUser)
			throws ServiceException;

	/**
	 * 通过指定sso账户名解锁
	 * 
	 * @param ssoUser
	 * @throws ServiceException
	 */
	public void unlockSsoUser(String ssoUser) throws ServiceException;

	/**
	 * 通过指定业务系统编号和用户名解锁
	 * 
	 * @param bizCode
	 * @param bizUser
	 * @throws ServiceException
	 */
	public void unlockSsoUser(String bizCode, String bizUser)
			throws ServiceException;

	/**
	 * 解密token字符串为token对象
	 * 
	 * @param token
	 * @return
	 * @throws ServiceException
	 */
	public SsoTokenEntityDto decodeToken(String token) throws ServiceException;

	/**
	 * 验证用户名密码是否正确，用于移动端登录
	 * 
	 * @param ssoUser
	 * @param ssoPassword
	 * @return
	 * @throws ServiceException
	 */
	public Boolean checkSsoUser(String ssoUser, String ssoPassword)
			throws ServiceException;
}