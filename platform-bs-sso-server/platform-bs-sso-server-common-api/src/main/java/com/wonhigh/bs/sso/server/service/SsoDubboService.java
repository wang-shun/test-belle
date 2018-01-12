package com.wonhigh.bs.sso.server.service;

import com.wonhigh.bs.sso.server.dto.SsoTokenEntityDto;

/**
 * sso提供的dubbo服务接口
 * @author user
 *
 */
public interface SsoDubboService {


	/**
	 * 解密token字符串为token对象
	 * @param token
	 * @param bizCode
	 * @return
	 */
	public SsoTokenEntityDto decodeToken(String token, String bizCode);

}