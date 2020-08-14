package org.young.sso.sdk.security;

import javax.servlet.http.HttpServletRequest;

import org.young.sso.sdk.resource.LoginUser;

public class SsoCredentialsAuth2 implements SsoCredentials {

	@Override
	public SsoPrincipal setUserPrincipal(HttpServletRequest req, LoginUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SsoPrincipal getUserPrincipal(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reletPrincipal(HttpServletRequest req, long tgcMaxAgeSeconds) {
		// TODO Auto-generated method stub

	}

}
