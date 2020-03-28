package org.young.sso.sdk.autoconfig;

public interface ConstSso {
	
	/** 请求基础地址key **/
	String BASE_PATH = "basePath";
	
	/** 应用服务器 **/
	String WEBAPP_SERVER   = "webappServer";
	/** EDP服务器地址 **/
	String EDPADMIN_SREVER = "edpadminSrever";
	
	/** 登录session **/
	String LOGIN_SESSION_ID    = "_sid_";
	/** 登录Ticket Key **/
	String LOGIN_TICKET_KEY = "_t_";
	/** 登录公钥 **/
	String LOGIN_PUBLIC_KEY	= "_p_";
	
	/** 登录语言key **/
	String LOGIN_LANGUAGE   = "lang";
	/** 登录请求 key **/
	String LOGIN_REQUEST_KEY	= "_k_";
	
	/**退出地址**/
	String[] SIGN_OUT = {"/sign/out", "/logout"};
	
	/** Session属性已登录用户 **/
	String SESSION_LOGIN_USER    = "loginUser";
	/** Session属性已登录应用 **/
	String SESSION_LOGIN_WEBAPPS = "loginWebapps";
	
	/** 隐藏保留长度 **/
	int HIDDEN_REMAIN = 10;
	/** 隐藏密码 **/
	String HIDDEN_CODE = "******";
	
}
