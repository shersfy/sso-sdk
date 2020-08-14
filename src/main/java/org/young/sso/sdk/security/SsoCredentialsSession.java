package org.young.sso.sdk.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.resource.LoginUser;

import com.alibaba.fastjson.JSON;

public class SsoCredentialsSession implements SsoCredentials {

	@Override
	public SsoPrincipal setUserPrincipal(HttpServletRequest req, LoginUser user) {
		String id = req.getSession().getId();
		id = DigestUtils.md5Hex(id);
		
		SsoPrincipal principal = new SsoPrincipal(id, user);
		req.getSession().setAttribute(ConstSso.SESSION_LOGIN_USER, principal.toString());
		return principal;
	}

	@Override
	public SsoPrincipal getUserPrincipal(HttpServletRequest req) {
		Object principal = req.getSession().getAttribute(ConstSso.SESSION_LOGIN_USER);
		return principal==null?null:JSON.parseObject(principal.toString(), SsoPrincipal.class);
	}

	@Override
	public void reletPrincipal(HttpServletRequest req, long tgcMaxAgeSeconds) {
		
	}

}
