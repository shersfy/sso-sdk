package org.young.sso.sdk.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.young.sso.sdk.listener.MemorySessionShared;

@ConfigurationProperties(SsoProperties.PREFIX)
public class SsoProperties {
	
	public static final String PREFIX ="edpglobal.sso";

	/**
	 * 是否开启单点登录(默认false关闭)
	 */
	private boolean enabled = false;
	
	/**
	 * 是否支持异步方式(默认false). 
	 * false(前后端不分离)未登录后台重定向到登录页面, 
	 * true (前后端分离)未登录返回状态为-1的json数据
	 */
	private boolean asyncSupported = false;
	
	/**
	 * SSO服务器session自动销毁时是否退出已登录的APP(默认false不退出)
	 */
	private boolean autoRemoveWebappFromServer = false;
	
	/**
	 * EDP认证中心服务内部地址(默认空白)
	 */
	private String innerEdpauthSrever = "";
	/**
	 * EDP认证中心服务内部地址(默认空白)
	 */
	private String outerEdpauthSrever = "";
	
	/**
	 * Web应用服务器注册地址
	 */
	private String webappServer = "";
	
	/**
	 * 单点登录过滤器忽略的URL地址
	 */
	private String[] ignoreUrls = {};
	
	/**
	 * 单点登录过滤器忽略的静态资源文件(扩展名,默认{".js", ".css", ".png", ".ico", ".html"})
	 */
	private String[] ignoreResources = {".js", ".css", ".png", ".ico", ".html"};
	
	/**
	 * token最大有效时间(秒，默认1min=60s)
	 */
	private long tokenMaxAgeSeconds = 1*60L;
	
	/**
	 * HttpOnly（默认false）
	 */
	private boolean cookieHttpOnly;
	/**
	 * 是否启用https（默认false）
	 */
	private boolean cookieSecure;
	
	/**
	 * 启用安全加密
	 */
	private boolean enabledRsa;
	
	/***
	 * 请求远端服务重试次数(默认5次)
	 */
	private int requestRemoteRetry = 5;
	
	/**
	 * session共享处理类，默认配置com.gouuse.edpglobal.sso.listener.MemorySessionShared
	 */
	private String sessionSharedListener = MemorySessionShared.class.getName();
	
	/**
	 * 是否启用租户切换
	 */
	private boolean enabledChangeTenant = false;
	
	/**
	 * 找不到租户允许访问地址
	 */
	private String[] noTenantignoreUrls = {"/appinfo", "/user/current", "/user/tenants"};
	
	/**
	 * Cookie域名
	 */
	private String cookieDomain = "edmpglobal.com";
	
	private CookieSessionModel cookieSessionModel = CookieSessionModel.sharing;
	
	public SsoProperties() {
		super();
	}
	
	public SsoProperties(boolean enabled, String outerEdpauthSrever, String webappServer) {
		super();
		this.enabled = enabled;
		this.outerEdpauthSrever = outerEdpauthSrever;
		this.innerEdpauthSrever = outerEdpauthSrever;
		this.webappServer = webappServer;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAsyncSupported() {
		return asyncSupported;
	}

	public void setAsyncSupported(boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}


	public boolean isAutoRemoveWebappFromServer() {
		return autoRemoveWebappFromServer;
	}

	public void setAutoRemoveWebappFromServer(boolean autoRemoveWebappFromServer) {
		this.autoRemoveWebappFromServer = autoRemoveWebappFromServer;
	}

	public String getInnerEdpauthSrever() {
		return innerEdpauthSrever;
	}

	public void setInnerEdpauthSrever(String innerEdpauthSrever) {
		this.innerEdpauthSrever = innerEdpauthSrever;
	}

	public String getOuterEdpauthSrever() {
		return outerEdpauthSrever;
	}

	public void setOuterEdpauthSrever(String outerEdpauthSrever) {
		this.outerEdpauthSrever = outerEdpauthSrever;
	}

	public String getWebappServer() {
		return webappServer;
	}

	public void setWebappServer(String webappServer) {
		this.webappServer = webappServer;
	}

	public String[] getIgnoreUrls() {
		return ignoreUrls;
	}

	public void setIgnoreUrls(String[] ignoreUrls) {
		this.ignoreUrls = ignoreUrls;
	}

	public String[] getIgnoreResources() {
		return ignoreResources;
	}

	public void setIgnoreResources(String[] ignoreResources) {
		this.ignoreResources = ignoreResources;
	}

	public long getTokenMaxAgeSeconds() {
		return tokenMaxAgeSeconds;
	}

	public void setTokenMaxAgeSeconds(long tokenMaxAgeSeconds) {
		this.tokenMaxAgeSeconds = tokenMaxAgeSeconds;
	}

	public boolean isCookieHttpOnly() {
		return cookieHttpOnly;
	}

	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
	}

	public boolean isCookieSecure() {
		return cookieSecure;
	}

	public void setCookieSecure(boolean cookieSecure) {
		this.cookieSecure = cookieSecure;
	}

	public int getRequestRemoteRetry() {
		return requestRemoteRetry;
	}

	public void setRequestRemoteRetry(int requestRemoteRetry) {
		this.requestRemoteRetry = requestRemoteRetry;
	}

	public String getSessionSharedListener() {
		return sessionSharedListener;
	}

	public void setSessionSharedListener(String sessionSharedListener) {
		this.sessionSharedListener = sessionSharedListener;
	}

	public boolean isEnabledRsa() {
		return enabledRsa;
	}

	public void setEnabledRsa(boolean enabledRsa) {
		this.enabledRsa = enabledRsa;
	}

	public boolean isEnabledChangeTenant() {
		return enabledChangeTenant;
	}

	public void setEnabledChangeTenant(boolean enabledChangeTenant) {
		this.enabledChangeTenant = enabledChangeTenant;
	}
	
	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public CookieSessionModel getCookieSessionModel() {
		return cookieSessionModel;
	}

	public void setCookieSessionModel(CookieSessionModel cookieSessionModel) {
		this.cookieSessionModel = cookieSessionModel;
	}

	public String[] getNoTenantignoreUrls() {
		return noTenantignoreUrls;
	}

	public void setNoTenantignoreUrls(String[] noTenantignoreUrls) {
		this.noTenantignoreUrls = noTenantignoreUrls;
	}

}
