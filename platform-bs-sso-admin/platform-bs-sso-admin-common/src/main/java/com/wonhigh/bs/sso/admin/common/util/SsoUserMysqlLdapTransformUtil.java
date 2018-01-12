package com.wonhigh.bs.sso.admin.common.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.model.SsoUser;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年12月18日 下午8:46:00
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SsoUserMysqlLdapTransformUtil {
    
    /**
     * 拷贝实体SsoUniformUserDTO  to  SsoUser
     * 
     * @param user
     * @param userLdap
     * @param isNeedCreat
     * @return
     */
    public static SsoUser ssoUserDtoToSsoUserLdap(SsoUniformUserDTO user, SsoUser userLdap) {
        userLdap.setLoginName(user.getLoginName());
        userLdap.setUid(user.getLoginName());
        userLdap.setSureName(user.getSureName());
        userLdap.setTelephoneNumber(StringUtils.isEmpty(user.getTelephoneNumber())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getTelephoneNumber());
        userLdap.setEmployeeNumber(StringUtils.isEmpty(user.getEmployeeNumber())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getEmployeeNumber());
        userLdap.setDescription(StringUtils.isEmpty(user.getDescription())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getDescription());
        userLdap.setEmail(StringUtils.isEmpty(user.getEmail())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getEmail());
        userLdap.setMobile(StringUtils.isEmpty(user.getMobile())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getMobile());
        userLdap.setSex(user.getSex());
        userLdap.setIdCard(StringUtils.isEmpty(user.getIdCard())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getIdCard());
        userLdap.setPositionName(StringUtils.isEmpty(user.getPositionName())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getPositionName());
        userLdap.setState(user.getState());
        userLdap.setDelFlag(user.getDelFlag());
        userLdap.setEmployeeType(StringUtils.isEmpty(user.getEmployeeType())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getEmployeeType());
        userLdap.setOrganizationalUnitName(StringUtils.isEmpty(user.getOrganizationalUnitName())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getOrganizationalUnitName());
        userLdap.setOrganizationCode(StringUtils.isEmpty(user.getOrganizationCode())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getOrganizationCode());
        userLdap.setOrganizationId(user.getUnitId()==null?0:user.getUnitId());
        String date = DateUtil.getDateFormat(user.getCreateTime(),DateUtil.TIME_DEFAULT_FORMAT);
        userLdap.setCreateTime(date);
        userLdap.setUpdateTime(date);
        userLdap.setCreater(StringUtils.isEmpty(user.getCreateUser())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getCreateUser());
        userLdap.setPassword(user.getPassword());
        userLdap.setBizUser(StringUtils.isEmpty(user.getBizUser())?ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE:user.getBizUser());
        return userLdap;
    }
    
    public static SsoUser ssoUserLdapToSsoUserDto(SsoUser userLdap, SsoUniformUserDTO user) {
        user.setLoginName(userLdap.getLoginName());
        user.setSureName(userLdap.getSureName());
        user.setTelephoneNumber(StringUtils.equals(userLdap.getTelephoneNumber(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getTelephoneNumber());
        user.setEmployeeNumber(StringUtils.equals(userLdap.getEmployeeNumber(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getEmployeeNumber());
        user.setDescription(StringUtils.equals(userLdap.getDescription(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getDescription());
        user.setEmail(StringUtils.equals(userLdap.getEmail(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getEmail());
        user.setMobile(StringUtils.equals(userLdap.getMobile(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getMobile());
        user.setSex(userLdap.getSex());
        user.setIdCard(StringUtils.equals(userLdap.getIdCard(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getIdCard());
        user.setPositionName(StringUtils.equals(userLdap.getPositionName(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getPositionName());
        user.setState(userLdap.getState());
        user.setDelFlag(userLdap.getDelFlag());
        user.setEmployeeType(StringUtils.equals(userLdap.getEmployeeType(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getEmployeeType());
        user.setOrganizationalUnitName(StringUtils.equals(userLdap.getOrganizationalUnitName(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getOrganizationalUnitName());
        user.setOrganizationCode(StringUtils.equals(userLdap.getOrganizationCode(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getOrganizationCode());
        user.setUnitId(Integer.valueOf(userLdap.getUid()));
        Date date = DateUtil.formatDate(userLdap.getCreateTime(), DateUtil.TIME_DEFAULT_FORMAT);
        user.setCreateTime(date);
        user.setUpdateTime(date);
        user.setCreateUser(StringUtils.equals(userLdap.getCreater(), ApiConstants.SSOUSER_COMMON_DEFAULT_VALUE)?null:userLdap.getCreater());
        user.setPassword(userLdap.getPassword());
        user.setBizUser(userLdap.getBizUser());
        return userLdap;
    }

}
