package org.young.sso.sdk.resource;

import java.util.HashSet;
import java.util.Set;

import org.young.sso.sdk.resource.BaseBean;

public class LoginWebapp extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	private String appserver;
	
	private String applogout;
	
	private Set<String> sessions;
	
	public LoginWebapp() {
		super();
		this.sessions = new HashSet<>();
	}
	
	public LoginWebapp(String appserver, String applogout) {
		this();
		this.appserver = appserver;
		this.applogout = applogout;
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

	public Set<String> getSessions() {
		return sessions;
	}

	public void setSessions(Set<String> sessions) {
		this.sessions = sessions;
	}

}
