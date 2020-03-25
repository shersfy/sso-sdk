package org.young.sso.sdk.autoconfig;
/**
 * 无论使用什么模式，客户端与SSO服务必须保持一致
 * @author Young
 * 2019年10月23日
 */
public enum CookieSessionModel {
	/**
	 * SSO服务器与客户端应用session独立，完全级别较高，但是session过期互不影响
	 */
	security,
	/**
	 * SSO服务器与客户端应用session共享，安全级别较低
	 */
	sharing
}
