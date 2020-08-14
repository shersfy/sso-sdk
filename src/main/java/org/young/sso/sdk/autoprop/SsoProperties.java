package org.young.sso.sdk.autoprop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.young.sso.sdk.security.MemorySessionShared;

@ConfigurationProperties(SsoProperties.PREFIX)
public class SsoProperties {

	public static final String PREFIX ="sso";

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
	 * SSO服务内部地址(默认空白)
	 */
	private String innerSrever = "";
	/**
	 * SSO服务外部地址(默认空白)
	 */
	private String outerSrever = "";
	
	/**
	 * 客户端应用服务注册地址
	 */
	private String webappServer = "";
	
	/**
	 * 客户端应用退出地址
	 */
	private String webappLogout = "/sign/out";
	
	/**
	 * 单点登录过滤器忽略的URL地址
	 */
	private String[] ignoreUrls = {"/sign/out"};
	
	/**
	 * 单点登录过滤器忽略的静态资源文件(扩展名,默认{".js", ".css", ".png", ".ico", ".html"})
	 */
	private String[] ignoreResources = {".js", ".css", ".png", ".ico", ".html"};
	
	/**
	 * ST最大有效时间(秒，默认10s)
	 */
	private int stMaxAgeSeconds = 10;
	
	/**
	 * TGC最大有效时间(秒，默认2h=2*60*60)
	 */
	private int tgcMaxAgeSeconds = 2*60*60;
	
	/**
	 * TGT最大有效时间(秒，默认8h=8*60*60)
	 */
	private int tgtMaxAgeSeconds = 8*60*60;
	
	/**
	 *  客户端cookie
	 */
	private CookieProperties cookie;
	
	/**
	 * 启用安全加密
	 */
	private boolean enabledRsa = false;
	
	/***
	 * 请求远端服务重试次数(默认5次)
	 */
	private int requestRemoteRetry = 5;
	
	/**
	 * session共享处理类，默认配置org.young.sso.sdk.listener.MemorySessionShared
	 */
	private String sessionSharedListener = MemorySessionShared.class.getName();
	
	/**
	 * 微信扫码登录配置
	 */
	private WeixinProperties weixin;
	
	/**
	 * SSO 客户端配置
	 */
	private ClientProperties client;

	public SsoProperties() {
		super();
		this.cookie = new CookieProperties();
		this.weixin = new WeixinProperties();
		this.client = new ClientProperties();
	}
	
	public SsoProperties(boolean enabled, String outerSsoSrever, String webappServer) {
		this();
		this.enabled = enabled;
		this.outerSrever = outerSsoSrever;
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

	public String getInnerSrever() {
		return innerSrever;
	}

	public void setInnerSrever(String innerSrever) {
		this.innerSrever = innerSrever;
	}

	public String getOuterSrever() {
		return outerSrever;
	}

	public void setOuterSrever(String outerSrever) {
		this.outerSrever = outerSrever;
	}

	public String getWebappServer() {
		return webappServer;
	}

	public void setWebappServer(String webappServer) {
		this.webappServer = webappServer;
	}

	public String getWebappLogout() {
		return webappLogout;
	}

	public void setWebappLogout(String webappLogout) {
		this.webappLogout = webappLogout;
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

	public int getStMaxAgeSeconds() {
		return stMaxAgeSeconds;
	}

	public void setStMaxAgeSeconds(int stMaxAgeSeconds) {
		this.stMaxAgeSeconds = stMaxAgeSeconds;
	}

	public int getTgcMaxAgeSeconds() {
		return tgcMaxAgeSeconds;
	}

	public void setTgcMaxAgeSeconds(int tgcMaxAgeSeconds) {
		this.tgcMaxAgeSeconds = tgcMaxAgeSeconds;
	}

	public int getTgtMaxAgeSeconds() {
		return tgtMaxAgeSeconds;
	}

	public void setTgtMaxAgeSeconds(int tgtMaxAgeSeconds) {
		this.tgtMaxAgeSeconds = tgtMaxAgeSeconds;
	}

	public CookieProperties getCookie() {
		return cookie;
	}

	public void setCookie(CookieProperties cookie) {
		this.cookie = cookie;
	}

	public boolean isEnabledRsa() {
		return enabledRsa;
	}

	public void setEnabledRsa(boolean enabledRsa) {
		this.enabledRsa = enabledRsa;
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

	public WeixinProperties getWeixin() {
		return weixin;
	}

	public void setWeixin(WeixinProperties weixin) {
		this.weixin = weixin;
	}

	public ClientProperties getClient() {
		return client;
	}

	public void setClient(ClientProperties client) {
		this.client = client;
	}

}
