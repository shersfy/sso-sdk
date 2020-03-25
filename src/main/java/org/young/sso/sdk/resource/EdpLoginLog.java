package org.young.sso.sdk.resource;

public class EdpLoginLog extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	/** 本次登录日志ID **/
	private Long loginId;
	
	/** 本次登录语言 **/
	private String loginLang;
	
	/**登录网址**/
	private String loginUrl;
	
	/** 本次登录时间 **/
	private long loginTimestamp;
	
	public EdpLoginLog() {}

	public EdpLoginLog(Long loginId, String loginLang) {
		super();
		this.loginId = loginId;
		this.loginLang = loginLang;
	}

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getLoginLang() {
		return loginLang;
	}

	public void setLoginLang(String loginLang) {
		this.loginLang = loginLang;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

}
