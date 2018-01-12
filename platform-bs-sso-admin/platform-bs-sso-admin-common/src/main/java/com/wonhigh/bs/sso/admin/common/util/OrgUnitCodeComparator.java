package com.wonhigh.bs.sso.admin.common.util;

import java.util.Comparator;

/**
 * 
 * 组织机构代码大小比较
 * 
 * @author user
 * @date 2017年11月12日 下午4:04:18
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class OrgUnitCodeComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if(o1.length()>o2.length()){
			return 1;
		}else if(o1.length()<o2.length()){
			return -1;
		}else if(o1.length()==o2.length()){
			return o1.compareTo(o2);
		}
		return 0;
	}

}
