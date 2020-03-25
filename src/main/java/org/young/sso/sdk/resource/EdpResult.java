package org.young.sso.sdk.resource;

import com.alibaba.fastjson.JSONObject;

public class EdpResult {
	
	/**
	 * 状态码
	 * 2018年9月18日
	 */
	public class ResultCode {

        public static final int SUCESS      = 0;
        public static final int FAIL        = 1;
        /**临时登录**/
        public static final int TMP_LOGIN   = -1;
        /**未登录**/
        public static final int NOT_LOGIN   = -2;
    }
	
	private int 	code 	= ResultCode.SUCESS;
	private String 	msg 	= "";
	private Object  model	= null;

	public EdpResult() {
	}
	
	public EdpResult(Object  model) {
		this.model = model;
	}

	public EdpResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public EdpResult(int code, String msg, Object  model) {
		this.code = code;
		this.msg = msg;
		this.model = model;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}
