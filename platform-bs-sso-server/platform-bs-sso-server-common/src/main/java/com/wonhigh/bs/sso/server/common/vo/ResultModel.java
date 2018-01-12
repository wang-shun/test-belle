package com.wonhigh.bs.sso.server.common.vo;

import java.io.Serializable;



/**
 * 自定义返回结果
 */
public class ResultModel implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
     * 返回码
     */
    private int code; //0失败 1成功

    /**
     * 返回结果描述
     */
    private String msg;

    /**
     * 返回内容
     */
    private Object content;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getContent() {
        return content;
    }
    public ResultModel(){}
    public ResultModel(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.content = "";
    }

    public ResultModel(int code, String msg, Object content) {
        this.code = code;
        this.msg = msg;
        this.content = content;
    }

	public void setCode(int code) {
		this.code = code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
