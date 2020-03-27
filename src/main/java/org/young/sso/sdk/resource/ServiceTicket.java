package org.young.sso.sdk.resource;

import org.young.sso.sdk.resource.BaseBean;

public class ServiceTicket extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 明文RequestKey
	 */
	private String rk;
	
	/**
	 * 密文TicketGrantingCookie值
	 */
	private String tgc;
	
	/**
	 * 客户端应用host
	 */
	private String apphost;
	

	public ServiceTicket() {
		super();
	}
	

	public ServiceTicket(String rk, String tgc, String apphost) {
		super();
		this.rk = rk;
		this.tgc = tgc;
		this.apphost = apphost;
	}




	public String getRk() {
		return rk;
	}


	public void setRk(String rk) {
		this.rk = rk;
	}


	public String getTgc() {
		return tgc;
	}


	public void setTgc(String tgc) {
		this.tgc = tgc;
	}


	public String getApphost() {
		return apphost;
	}


	public void setApphost(String apphost) {
		this.apphost = apphost;
	}

}
