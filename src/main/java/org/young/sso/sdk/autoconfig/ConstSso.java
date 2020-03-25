package org.young.sso.sdk.autoconfig;

public interface ConstSso {
	
	/** 请求基础地址key **/
	String BASE_PATH = "basePath";
	/** 应用ID**/
	String WEBAPP_ID = "appId";
	/** 全部角色**/
	String ROLES_ALL = "allRoles";
	/** 应用服务器 **/
	String WEBAPP_SERVER   = "webappServer";
	/** EDP服务器地址 **/
	String EDPADMIN_SREVER = "edpadminSrever";
	/** 登录session **/
	String LOGIN_SESSION_ID    = "_sid_";
	/** 登录token key **/
	String LOGIN_TOKEN_KEY  = "_t_";
	/** 登录公钥 **/
	String LOGIN_PUBLIC_KEY	= "_p_";
	/** 登录语言key **/
	String LOGIN_LANGUAGE   = "lang";
	/** 登录请求 key **/
	String LOGIN_REQUEST_KEY	= "_k_";
	/** 重定向随机状态码(用于验证是重定向和登录认证是同一个session) **/
	String LOGIN_STATE  		= "_st_";
	/**token长度**/
	int LOGIN_TOKEN_LENGTH = 10;
	/**退出地址**/
	String[] SIGN_OUT = {"/sign/out", "/logout"};
	
	/** 当前租户Key **/
	String CURRENT_TENANT_KEY = "tenant";
}
