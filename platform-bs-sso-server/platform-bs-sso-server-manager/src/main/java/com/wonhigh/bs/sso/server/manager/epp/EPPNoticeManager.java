package com.wonhigh.bs.sso.server.manager.epp;

/**
 * EPP异步通知消息
 * 
 * @author user
 * @date 2017年11月22日 下午4:05:50
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface EPPNoticeManager {
	
	/**
	 * EPP异步通知消息
	 * @param userName
	 * @param bizUserName
	 * @param bizCode
	 * @param msgType
	 */
	public void asyncSendNoticToEpp(final String userName, final String bizUserName, final String bizCode,
			final String msgType);
}
