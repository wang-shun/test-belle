package com.wonhigh.bs.sso.admin.common.vo;

import java.io.Serializable;

/**
 * 
 * TODO: 自定义返回结果
 * 
 * @author user
 * @date 2017年11月7日 上午10:39:20
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class ResultModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回结果描述
     */
    private String message;

    /**
     * 返回内容
     */
    private Object content;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getContent() {
        return content;
    }

    public ResultModel() {
    }

    public ResultModel(int code, String message) {
        this.code = code;
        this.message = message;
        this.content = "";
    }

    public ResultModel(int code, String message, Object content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ResultModel [code=" + code + ", message=" + message + ", content=" + content + "]";
    }

}
