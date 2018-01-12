package com.wonhigh.bs.sso.admin.freemark;


import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class ProductId implements TemplateMethodModel {
	
	private static char[] cs = new char[]{'a','b','c','d','e','f','g'
		,'h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

	private String get11Id(){
		SecureRandom sr = new SecureRandom();
		int size = 26;
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<11;i++){
			int s = sr.nextInt(size);
			sb.append(cs[s]);
		}
		return sb.toString();
	}
	
	@Override
	public Object exec(List args) throws TemplateModelException {
		// TODO Auto-generated method stub
		String id = null;
		if(args!=null&&args.size()>0){ 
			String s1=(String) args.get(0);
			id = s1;
			if(StringUtils.isEmpty(s1)){
				id = get11Id();
			}
			
		} else {
			id = get11Id();
		}
		
		return id;
	}

}
