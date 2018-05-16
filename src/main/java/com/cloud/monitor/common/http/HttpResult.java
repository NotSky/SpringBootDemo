package com.cloud.monitor.common.http;

import java.io.Serializable;

public class HttpResult implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer code;
    private String data;
    private String codeMessage;

    public HttpResult(Integer code, String codeMessage, String data) {
        this.code = code;

        this.codeMessage = codeMessage;

        this.data = data;
    }

    public HttpResult() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", data='" + data + '\'' +
                ", codeMessage='" + codeMessage + '\'' +
                '}';
    }
}
