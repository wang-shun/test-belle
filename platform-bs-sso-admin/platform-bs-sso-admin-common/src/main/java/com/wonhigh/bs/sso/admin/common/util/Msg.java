package com.wonhigh.bs.sso.admin.common.util;

/**
 * 
 * @ClassName: Msg  
 * @Description: 格式化Controller输出  
 * @author: user
 * @date: 2015年8月26日 下午3:39:04
 */
public class Msg  {
	
	/**
	 * 返回前端status 0为成功 1位失败
	 */
	final static public Integer SUCCESS = 0;
	final static public Integer FAILURE = 1;

	final static public Integer DUPLICATEKEYERROR = 201;
	final static public Integer NOTEXISTERROR = 202;
	final static public Integer EXISTERROR = 203;
	final static public Integer INSERTERROR = 204;
	final static public Integer DELETEERROR = 205;
	final static public Integer UPDATEERROR = 206;
	final static public Integer FINDBYIDERROR = 207;
	final static public Integer FINDALLERROR = 208;
	final static public Integer FILEUPLOADERROR = 301;
	final static public Integer COPYERROR = 209;
	final static public Integer APPROVEERROR = 302;
	final static public Integer REJECTERROR = 303;
	final static public Integer ONLINEERROR = 304;
	
	/**
	 * 默认为成功
	 */
	private Integer status = SUCCESS;
	/**
	 *  成功为返回值，失败为错误信息。
	 */
	private Object value;

	public Msg() {
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}