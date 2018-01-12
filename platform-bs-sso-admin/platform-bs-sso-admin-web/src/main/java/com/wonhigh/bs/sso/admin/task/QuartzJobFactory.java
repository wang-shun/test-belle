package com.wonhigh.bs.sso.admin.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wonhigh.bs.sso.admin.common.model.ScheduleJob;
import com.wonhigh.bs.sso.admin.common.util.TaskUtils;

/**
 * 
 * 计划任务执行处 无状态
 * @author user
 * @date 2017年11月21日 下午3:49:01
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class QuartzJobFactory implements Job {

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
		TaskUtils.invokMethod(scheduleJob);
	}
}