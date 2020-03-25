package org.young.sso.sdk.listener;

/**
 * 刷新token
 * @author pengy
 * @date 2018年12月4日
 */
public interface SsoListener {
	
	/**
	 * 刷新token
	 * @param token 旧token值
	 * @param remoteAddr 客户端IP
	 * @return 返回有效的token，返回null时未登录
	 */
	String renewToken(String token, String remoteAddr);
	
}
