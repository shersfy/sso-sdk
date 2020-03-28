package org.young.sso.sdk.resource;

public class LoginWebapp extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	private String appserver;
	
	private String applogout;
	
	private String session;
	
	public LoginWebapp() {
		super();
	}
	
	public LoginWebapp(String appserver, String applogout) {
		super();
		this.appserver = appserver;
		this.applogout = applogout;
	}
	
	public LoginWebapp(String appserver, String applogout, String session) {
		super();
		this.appserver = appserver;
		this.applogout = applogout;
		this.session = session;
	}

	public String getAppserver() {
		return appserver;
	}

	public void setAppserver(String appserver) {
		this.appserver = appserver;
	}

	public String getApplogout() {
		return applogout;
	}

	public void setApplogout(String applogout) {
		this.applogout = applogout;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}


}
