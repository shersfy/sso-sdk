package org.young.sso.sdk.resource;

import java.util.HashSet;
import java.util.Set;

import org.young.sso.sdk.resource.BaseBean;

public class LoginWebapp extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	private Long appId;
	
	private String webappServer;
	
	private String webappSignout;
	
	private Set<String> sessions;
	
	public LoginWebapp() {
		super();
	}
	
	public LoginWebapp(Long appId, String webappServer, String webappSignout) {
		super();
		this.appId = appId;
		this.webappServer = webappServer;
		this.webappSignout = webappSignout;
		this.sessions = new HashSet<>();
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getWebappServer() {
		return webappServer;
	}

	public void setWebappServer(String webappServer) {
		this.webappServer = webappServer;
	}

	public Set<String> getSessions() {
		return sessions;
	}

	public void setSessions(Set<String> sessions) {
		this.sessions = sessions;
	}

	public String getWebappSignout() {
		return webappSignout;
	}

	public void setWebappSignout(String webappSignout) {
		this.webappSignout = webappSignout;
	}


}
