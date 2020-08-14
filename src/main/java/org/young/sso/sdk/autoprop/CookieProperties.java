package org.young.sso.sdk.autoprop;

public class CookieProperties {

	/**
	 * SessionId cookie name
	 */
	private String sidname = "TGC";
	
	/**
	 * SessionId cookie domain
	 */
	private String domain;
	
	/**
	 * HttpOnly（默认false）
	 */
	private boolean httpOnly = true;
	
	/**
	 * 是否启用https（默认false）
	 */
	private boolean secure = true;

	public String getSidname() {
		return sidname;
	}

	public void setSidname(String sidname) {
		this.sidname = sidname;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}


}
