package com.wonhigh.bs.sso.admin.manager.mq;

/**
 * 发送消息服务
 */
public interface JMSService {
	/**
	 * 发送
	 * @param model
	 * @return
	 */
	boolean send(Object model);
}
