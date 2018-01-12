package com.wonhigh.bs.sso.admin.manager;

import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 统一用户账号 管理（mysql）
 * @author user
 *
 */
public interface SsoUniformUserManager extends BaseCrudManager {
	
	/**
	 * 查询绑定过业务系统账号
	 * @param bizCode
	 * @param bizLoginName
	 * @return
	 */
	public int findByBizUser(String bizCode, String bizLoginName)throws ServiceException;
	
	/**
	 * 保存到删除表
	 * @param deletedUser
	 * @return
	 */
	public int saveAsDeleted(SsoUniformUserDTO deletedUser);
	
	/**
	 * 逻辑删除
	 * @param user
	 * @return
	 * @throws ManagerException
	 */
	public int logicDelete(SsoUniformUserDTO user) throws ManagerException;
	
	/**
	 * 添加用户同时保存到mysql和ldap
	 * @return
	 * @throws ManagerException
	 */
	public int addToMysqlAndLdap(SsoUniformUserDTO user) throws ManagerException;
	
	/**
	 * 修改用户到mysql和ldap
	 * @param user
	 * @return
	 * @throws ManagerException
	 */
	public int updateToMysqlAndLdap(SsoUniformUserDTO user) throws ManagerException;
	
}
