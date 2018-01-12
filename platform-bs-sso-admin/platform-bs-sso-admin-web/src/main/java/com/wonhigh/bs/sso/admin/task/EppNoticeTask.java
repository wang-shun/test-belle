package com.wonhigh.bs.sso.admin.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonhigh.bs.sso.admin.task.epp.EppNotification;

/**
 * 
 * 发送通知任务
 * 
 * @author aking
 * @date 2017年12月21日 上午11:52:01
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class EppNoticeTask {
	
	private static Logger LOGGER = LoggerFactory.getLogger(EppNoticeTask.class);
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	
	public static void sendNoticeToEpp(EppNotification notice){
		try{
            executor.submit(notice);
		}catch(Exception e){
			LOGGER.error("执行发送通知到EPP任务时出错，"+e.getMessage());
		}
	}
}
