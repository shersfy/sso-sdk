package org.young.sso.sdk.resource;

import org.young.sso.sdk.resource.BaseBean;

public class ServiceTicket extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	/**
	 * st值
	 */
	private String st;
	
	/**
	 * 客户端应用服务器地址
	 */
	private String appserver;
	
	/**
	 * 客户端应用session ID
	 */
	private String appsession;
	
	/**
	 * 客户端应用退出地址(全路径)
	 */
	private String applogout;

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getAppserver() {
		return appserver;
	}

	public void setAppserver(String appserver) {
		this.appserver = appserver;
	}

	public String getAppsession() {
		return appsession;
	}

	public void setAppsession(String appsession) {
		this.appsession = appsession;
	}

	public String getApplogout() {
		return applogout;
	}

	public void setApplogout(String applogout) {
		this.applogout = applogout;
	}

}
