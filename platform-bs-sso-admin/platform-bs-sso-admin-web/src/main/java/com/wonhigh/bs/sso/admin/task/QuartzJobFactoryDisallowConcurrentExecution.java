package com.wonhigh.bs.sso.admin.task;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wonhigh.bs.sso.admin.common.model.ScheduleJob;
import com.wonhigh.bs.sso.admin.common.util.TaskUtils;

/**
 * 
 * 若一个方法一次执行不完下次轮转时则等待改方法执行完后才执行下一次操作
 * @author user
 * @date 2017年11月21日 下午3:48:48
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
		TaskUtils.invokMethod(scheduleJob);
	}
}