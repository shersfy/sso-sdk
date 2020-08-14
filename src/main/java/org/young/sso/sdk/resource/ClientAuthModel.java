package org.young.sso.sdk.resource;

/**
 * SSO 客户端应用登录认证模式
 * @author Young
 * @date 2020-08-13
 */
public enum ClientAuthModel {
	/**
	 * session模式, 适用于前后端不分离部署
	 */
	session,
	/**
	 * auth模式, 适用于前后端分离部署
	 */
	auth2
}
