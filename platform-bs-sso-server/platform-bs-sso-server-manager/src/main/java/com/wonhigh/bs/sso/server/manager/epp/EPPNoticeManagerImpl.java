package com.wonhigh.bs.sso.server.manager.epp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wonhigh.bs.sso.server.common.constants.ApiConstants;
import com.wonhigh.bs.sso.server.common.util.InvokeApiUtil;
import com.wonhigh.bs.sso.server.common.vo.BizConfigDTO;
import com.wonhigh.bs.sso.server.manager.BizConfigManager;
import com.wonhigh.bs.sso.server.manager.cas.CasClientConfigManager;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月22日 下午3:57:31
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */

@Service
public class EPPNoticeManagerImpl implements EPPNoticeManager {

	@Value("${EPP_SOURCE_PARAMETER}")
	private String source;

	private final static Logger LOGGER = LoggerFactory.getLogger(EPPNoticeManagerImpl.class);

	@Resource
	private BizConfigManager bizConfigManager;

	@Resource
	private CasClientConfigManager casClientConfigManager;

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * 异步通知EPP
	 * @param userName
	 * @param bizUserName
	 * @param bizCode
	 * @param msgType
	 */
	public void asyncSendNoticToEpp(final String userName, final String bizCode, final String bizUserName,
			final String msgType) {

		executor.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				sendNoticToEpp(userName, bizCode, bizUserName, msgType);
				return null;
			}
		});
	}

	/**
	 * 同时EPP修改用户信息
	 * @param userName
	 * @param bizUserName
	 * @param bizConfig
	 */
	public void sendNoticToEpp(String userName, String bizCode, String bizUserName, String msgType) {

		try {

			LOGGER.info("开始通知EPP, userName: " + userName + ", bizCode:" + bizCode + ", bizUserName:" + bizUserName
					+ ", msgType: " + msgType);

			Map<String, String> bodys = new HashMap<String, String>(7);
			bodys.put("msg_type", msgType);
			bodys.put("source", casClientConfigManager.getSource());
			JSONObject json = new JSONObject();
			json.put("sso_login_name", userName);

			if (!StringUtils.isEmpty(bizCode)) {
				json.put("appCode", bizCode);
			}

			if (!StringUtils.isEmpty(bizUserName)) {
				json.put("lapp_login_name", bizUserName);
			}

			bodys.put("msg_info", json.toJSONString());
			BizConfigDTO eppConfig = new BizConfigDTO();
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("bizCode", ApiConstants.SSOADMIN_EPP_BIZ_CODE);
			List<BizConfigDTO> findByBiz;

			findByBiz = bizConfigManager.findByBiz(eppConfig, p);

			if (findByBiz != null && findByBiz.size() > 0) {
				eppConfig = findByBiz.get(0);
				Map<String, Object> m = InvokeApiUtil.sendNoticToEpp(eppConfig.getSyncUserInfoUrl(), bodys);
				int c = (int) m.get("code");
				if (c != 1) {
					LOGGER.error("发送通知到EPP出错" + (String) m.get("msg") + bodys);
				} else {
					LOGGER.info("发送通知到EPP成功" + bodys);
				}
			}

			LOGGER.info("结束通知EPP, userName: " + userName + ", bizCode:" + bizCode + ", bizUserName:" + bizUserName
					+ ", msgType: " + msgType);
		} catch (ManagerException e) {
			LOGGER.info("发送通知到EPP失败" + e.getMessage());
		}
	}

}
