package com.wonhigh.bs.sso.admin.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wonhigh.bs.sso.admin.common.model.SsoUser;
import com.wonhigh.bs.sso.admin.common.util.SsoUserMysqlLdapTransformUtil;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.admin.service.SsoUniformUserService;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月18日 下午1:52:40
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service("ssoUniformUserManager")
@Transactional
public class SsoUniformUserManagerImpl extends BaseCrudManagerImpl implements SsoUniformUserManager {
	
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
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
		queryCondition.append(" AND biz_user like \"%\\\""+bizCode+"\\\":\\\""+bizLoginName+"\\\"%\" ");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("queryCondition", queryCondition.toString());
		int total = this.ssoUniformUserService.findCount(map);
		return total;
	}

	@Override
	public int saveAsDeleted(SsoUniformUserDTO deletedUser) {
		return ssoUniformUserService.saveAsDeleted(deletedUser);
	}

	@Override
	public int logicDelete(SsoUniformUserDTO user) throws ManagerException {
		//备份到删除表
		try{
	        LOGGER.info("开始保存ssoUnitformUser到删除表(mysql):"+user);
	        this.saveAsDeleted(user);
	        LOGGER.info("保存到删除表(mysql)成功:"+user);
		}catch (Exception e){
			e.printStackTrace();
			LOGGER.error("保存ssoUnitformUser(mysql) 到删除表报错，e="+e);
			throw new ManagerException(e);
		}
        //物理删除
		try{
	        LOGGER.info("开始物理删除ssoUnitformUser(mysql):"+user);
	        this.deleteById(user);
	        LOGGER.info("物理删除ssoUnitformUser(mysql)成功:"+user);
		}catch (Exception e){
			e.printStackTrace();
			LOGGER.info("物理删除ssoUnitformUser(mysql)失败，e="+e);
			throw new ManagerException(e);
		}
        //删除ldap数据
        try{
            SsoUser userLdap = ssoUserManager.findByPrimaryKey(user.getLoginName());
            if (userLdap != null) {
            	LOGGER.info("开始删除ssoUnitformUser(mysql):"+userLdap);
            	ssoUserManager.delete(userLdap);
            	LOGGER.info("删除ssoUnitformUser(mysql)结束:"+userLdap);
            }
    	}catch (Exception e){
    		e.printStackTrace();
    		LOGGER.error("删除SsoUser(LDAP)失败，e=" + e.getMessage());
    		throw new ManagerException(e);
    	}
		return 1;
	}

	@Override
	public int addToMysqlAndLdap(SsoUniformUserDTO user) throws ManagerException {
		//添加到mysql
		Date now = new Date();
		try{
			LOGGER.info("开始添加SsoUniformUser到mysql:"+user);
			user.setCreateTime(now);
			user.setUpdateTime(now);
			this.add(user);
			LOGGER.info("添加SsoUniformUser到mysql成功:"+user);
		}catch (Exception e){
			e.printStackTrace();
            LOGGER.error("添加ssoUser到mysql失败,e="+e.getMessage());
            throw new ManagerException(e);
		}
		//添加到ldap
		try {
			SsoUser userLdap = ssoUserManager.findByPrimaryKey(user.getLoginName());
			if(userLdap==null){
				userLdap = new SsoUser();
				userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
	            LOGGER.info("开始添加SsoUser到LDAP："+userLdap.toString());
	            ssoUserManager.create(userLdap);
	            LOGGER.info("添加SsoUser到LDAP成功："+userLdap.toString());
			}else{
			    userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
	            LOGGER.info("开始更新SsoUser到LDAP：新数据："+userLdap);
	            ssoUserManager.updateInAdditionToPassword(userLdap);
	            LOGGER.info("更新SsoUser到LDAP成功："+userLdap);
			}
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("添加ssoUser到LDAP失败, e="+e.getMessage());
            throw new ManagerException(e);
        }
		return 1;
	}

	@Override
	public int updateToMysqlAndLdap(SsoUniformUserDTO user)
			throws ManagerException {
		Date now = new Date();
		//修改mysql
		try{
			LOGGER.info("开始修改ssoUnitformUser(mysql):"+user);
			user.setUpdateTime(now);
			this.modifyById(user);
	    	LOGGER.info("修改ssoUnitformUser(mysql)成功:"+user);
		}catch (Exception e){
			e.printStackTrace();
            LOGGER.error("修改ssoUnitformUser(mysql)失败,e="+e.getMessage());
            throw new ManagerException(e);
		}
		//ldap
		try {
			SsoUser userLdap = ssoUserManager.findByPrimaryKey(user.getLoginName());
        	if(userLdap==null){
        		LOGGER.error("mysql和ldap数据不一致,LDAP为空，mysql="+user);
        		//throw new ManagerException("mysql和ldap数据不一致");
        		//添加到LDAP
        		userLdap = new SsoUser();
        		userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
	            LOGGER.info("开始添加SsoUser到LDAP："+userLdap.toString());
	            ssoUserManager.create(userLdap);
	            LOGGER.info("添加SsoUser到LDAP成功："+userLdap.toString());
        	}else{
        	    userLdap = SsoUserMysqlLdapTransformUtil.ssoUserDtoToSsoUserLdap(user, userLdap);
        		LOGGER.info("开始修改ssoUser(LDAP):"+userLdap);
        		if(StringUtils.isNotEmpty(user.getPassword())){
    				userLdap.setPassword(user.getPassword());
    				ssoUserManager.update(userLdap);
            	}else{
    	    		ssoUserManager.updateInAdditionToPassword(userLdap);
            	}
                LOGGER.info("修改SsoUser成功(LDAP)："+userLdap.toString());
        	}
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("修改SsoUser(LDAP)失败, e="+e.getMessage()); 
            throw new ManagerException(e);
        }
		return 1;
	}

	/*@Override
	public int addSsoUserTmpBatch(List<SsoUniformUserDTO> userList) {
		return ssoUniformUserService.addSsoUserTmpBatch(userList);
	}

	@Override
	public int clearSsoUserTmp() {
		return ssoUniformUserService.clearSsoUserTmp();
	}

	@Override
	public List<SsoUniformUserDTO> getDelUserFromHr() {
		return ssoUniformUserService.getDelUserFromHr();
	}

	@Override
	public List<SsoUniformUserDTO> getAddUserFromHr() {
		return ssoUniformUserService.getAddUserFromHr();
	}*/	
	
}
