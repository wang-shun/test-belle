package com.wonhigh.bs.sso.admin.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonhigh.bs.sso.admin.common.constants.ApiConstants;
import com.wonhigh.bs.sso.admin.common.model.VEasyUiTree;
import com.wonhigh.bs.sso.admin.common.util.RedisUtil;
import com.wonhigh.bs.sso.admin.manager.OrgUnitManager;
import com.yougou.logistics.base.common.exception.ManagerException;

/**
 * 组织树从db中查询并缓存到redis
 * 作为spring定时器的任务
 * @author user
 * @date 2017年11月6日 上午11:41:31
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class OrgUnitTreeInitUtil {
	
	@Resource
	private RedisUtil redisUtil;
	@Resource
	private OrgUnitManager orgUnitManager;
	
	private Logger log = LoggerFactory.getLogger(OrgUnitTreeInitUtil.class);

	public void initOrgUnitTree(){
		try {
			//TODO:查询top 根节点id
			long b = System.currentTimeMillis();
			Integer topUnitId = 1;
			//包含根节点
			List<VEasyUiTree> top = orgUnitManager.queryTreeNodesBySelfIdAndLevel(topUnitId, 1, 1); 
			for (VEasyUiTree sub : top) {
				findChildTree(sub);
			}
			long e = System.currentTimeMillis();
			System.out.println("查询组织树耗时：" + (e-b)/1000 + " 秒.");
			log.info("查询组织树耗时：" + (e-b)/1000 + " 秒.");
			redisUtil.set(ApiConstants.ORG_UNTI_TREE_ALL, top);
		} catch (ManagerException e) {
			e.printStackTrace();
			log.error("获取组织树出错。" + e);
		}
	}
	
	private void findChildTree(VEasyUiTree parent){
		try {
			//不包含根节点
			List<VEasyUiTree> sub = orgUnitManager.queryTreeNodesBySelfIdAndLevel(parent.getId(), 1, 0);
			if(sub==null||sub.size()==0) {
				return;
			}
			parent.setChildren(sub);
			for (VEasyUiTree ssub : sub) {
				this.findChildTree(ssub);
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		} 
	}
	
}
