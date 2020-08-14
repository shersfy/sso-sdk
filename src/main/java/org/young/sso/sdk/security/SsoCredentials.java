package org.young.sso.sdk.security;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.auth.Credentials;
import org.young.sso.sdk.resource.LoginUser;

/**
 * 用户登录认证资格
 * @author Young
 * @date 2020-08-14
 */
public interface SsoCredentials extends Credentials {
	
	/**
	 * 存储登录用户信息，返回一个租赁周期的登录用户信息，租赁到期后存储失效
	 * @param req 请求对象
	 * @param user 登录用户
	 * @return 返回登录用户身份
	 */
	SsoPrincipal setUserPrincipal(HttpServletRequest req, LoginUser user);
	
	/**
	 * 获取登录用户信息
	 * @param req 请求
	 * @return
	 */
	SsoPrincipal getUserPrincipal(HttpServletRequest req);
	
	/**
	 * 续租(一个TGC时间)登录用户身份信息
	 * @param req 请求
	 * @param tgcMaxAgeSeconds
	 */
	void reletPrincipal(HttpServletRequest req, long tgcMaxAgeSeconds);
	
	@Override
	default Principal getUserPrincipal() {
		return null;
	}
	
	@Override
	default String getPassword() {
		return null;
	}

}
