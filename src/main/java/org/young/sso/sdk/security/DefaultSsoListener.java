package org.young.sso.sdk.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoprop.SsoProperties;
import org.young.sso.sdk.utils.SsoUtil;

/**
 * 应用客户端使用
 * @author pengy
 * @date 2018年12月4日
 */
public class DefaultSsoListener implements SsoListener {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultSsoListener.class);
	
	private SsoProperties ssoProperties;
	
	public DefaultSsoListener(SsoProperties ssoProperties) {
		super();
		this.ssoProperties = ssoProperties;
	}
	
	@Override
	public String renewToken(String token, String remoteAddr) {
		token = SsoUtil.requestRenewToken(ssoProperties, token, remoteAddr, ssoProperties.getRequestRemoteRetry());
		return token;
	}
	
}
