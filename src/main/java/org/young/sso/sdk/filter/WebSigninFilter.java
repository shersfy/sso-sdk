package org.young.sso.sdk.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.listener.DefaultSsoListener;
import org.young.sso.sdk.listener.MemorySessionShared;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.listener.SsoListener;
import org.young.sso.sdk.resource.EdpResult;
import org.young.sso.sdk.resource.EdpResult.ResultCode;
import org.young.sso.sdk.utils.CookieUtil;
import org.young.sso.sdk.utils.SsoUtil;


/**
 * 登录过滤器
 * @author pengy
 * @date 2018年11月8日
 */
public class WebSigninFilter implements Filter {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private SsoProperties ssoProperties;

	private SsoListener listener;
	
	private SessionSharedListener sessionSharedListener;
	
	public WebSigninFilter() {}
	
	/**
	 * 配置SSO相关属性，将会在WebSigninFilter.init()中被调用
	 * @return
	 */
	public SsoProperties resetSsoProperties() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ssoProperties = resetSsoProperties();
		ssoProperties = ssoProperties==null?SsoUtil.initSso(filterConfig):ssoProperties;
		
		listener = listener==null ?new DefaultSsoListener(ssoProperties) :listener;
		
		String className = ssoProperties.getSessionSharedListener();
		className = StringUtils.isBlank(className)?MemorySessionShared.class.getName():className;
		
		Class<? extends SessionSharedListener> clazz = null;
		try {
			clazz = (Class<? extends SessionSharedListener>) Class.forName(className);
			if (sessionSharedListener==null) {
				sessionSharedListener = clazz.newInstance();
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		LOGGER.info("SSO filter has enabled, edpauth server is '{}', and listener is {}", 
				ssoProperties.getOuterEdpauthSrever(), listener);
	}
	

	/**
	 * 不需要覆写
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req  = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Credentials", "true");
		res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		res.setHeader("Access-Control-Allow-Headers", "*");

		LOGGER.info("method:{}, port:{}, url:{}, tenant={}, t={}", 
				req.getMethod(), req.getServerPort(), req.getRequestURL(),
				req.getParameter("tenant"), req.getParameter(ConstSso.LOGIN_TOKEN_KEY));
		
		this.setBasePath(req);
		String url = req.getRequestURI();
		
		// 登录系统重定向过来的请求
		boolean isWebappLogin = StringUtils.isNotBlank(req.getParameter(ConstSso.LOGIN_TOKEN_KEY));
		isWebappLogin = isWebappLogin && StringUtils.isNotBlank(ssoProperties.getOuterEdpauthSrever());
		if (isWebappLogin) {
			String tenant = req.getParameter("tenant");
			if (tenant!=null) {
				tenant = String.format("?tenant=%s&", tenant);
				url = url.contains("?")?url.replaceFirst("?", tenant):url+tenant.substring(0, tenant.length()-1);
			}
			
			LOGGER.debug("===== outerEdpauthSrever:{}", ssoProperties.getOuterEdpauthSrever());
			LOGGER.debug("===== _t_:{}", req.getParameter(ConstSso.LOGIN_TOKEN_KEY));
			if (isLogined(req, res)) {
				res.sendRedirect(url);
				return;
			}
			// 验证未登录重定向和登录认证是否来自同一个session
//			String state1 = req.getParameter(ConstSso.LOGIN_STATE);
//			Object state2 = req.getSession().getAttribute(ConstSso.LOGIN_STATE);
//			if (state1==null || state2==null || !state1.equals(state2.toString())) {
//				// 重定向到登录页面
//				SsoUtil.redirectLogin(req, res, ssoProperties);
//				return;
//			}
			req.getSession().removeAttribute(ConstSso.LOGIN_STATE);
			// webapp 执行: 校验token有效性
			String token = req.getParameter(ConstSso.LOGIN_TOKEN_KEY);
			EdpResult validate = SsoUtil.requestValidateToken(req, ssoProperties, ssoProperties.getRequestRemoteRetry());
			if (validate.getCode()!=ResultCode.SUCESS) {
				LOGGER.error("validate failed");
				SsoUtil.redirectServerError(res, ssoProperties, validate);
				return;
			}
			
			SsoUtil.saveToken(req, res, ssoProperties, token);
			sessionSharedListener.addSession(req.getSession());
			LOGGER.info("sign in successful. session={}, t={}", req.getSession().getId(), SsoUtil.hiddenToken(token));
			res.sendRedirect(url);
			return;
		}

		// 被忽略或已登录的请求通行
		if (isLogined(req, res)) {
			req.setAttribute(ConstSso.WEBAPP_SERVER, ssoProperties.getWebappServer());
			req.setAttribute(ConstSso.EDPADMIN_SREVER, ssoProperties.getOuterEdpauthSrever());
			chain.doFilter(request, response);
			return;
		}
		
		if (ignore(url)) {
			chain.doFilter(request, response);
			return;
		}

		// 重定向到登录页面
		SsoUtil.redirectLogin(req, res, ssoProperties);
	}

	/***
	 * 是否登录
	 * @param req
	 * @param res
	 * @return true已登录，false未登录
	 */
	private boolean isLogined(HttpServletRequest req, HttpServletResponse res) {

		String sessionToken = SsoUtil.getTokenFormStorage(req, true);
		String cookieToken  = SsoUtil.getTokenFormStorage(req, false);
		// session token无效
		if (StringUtils.isBlank(sessionToken)) {
			return false;
		}
		// cookie token无效, 刷新token
		if (StringUtils.isBlank(cookieToken)) {
			String newToken = listener.renewToken(sessionToken, req.getRemoteAddr());
			if (StringUtils.isBlank(newToken)) {
				SsoUtil.removeTokenFormStorage(req, res);
				return false;
			}
			SsoUtil.saveToken(req, res, ssoProperties, newToken);
			return true;
		}
		
		
		// 前后端token是否一致
		boolean equals = sessionToken.equals(cookieToken);
		if (!equals) {
			CookieUtil.clearCookie(req, res);
		}
		return equals;
	}

	private boolean ignore(String url) {
		for (String ignore :ssoProperties.getIgnoreUrls()) {
			if (ignore.contains("*")) {
				ignore = ignore.replace("*", "");
				if ((url+"/").startsWith(ignore)) {
					return true;
				}
			}
			if (url.equals(ignore)) {
				return true;
			}
		}
		for (String ignore :ssoProperties.getIgnoreResources()) {
			if (url.endsWith(ignore)) {
				return true;
			}
		}
		return false;
	}

	private void setBasePath(HttpServletRequest request) {
		StringBuilder basePath = new StringBuilder(0);
		basePath.append(request.getScheme()).append("://");
		basePath.append(request.getServerName());
		if(request.getServerPort() != 80 && request.getServerPort() != 443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(request.getContextPath());
		request.setAttribute(ConstSso.BASE_PATH, basePath.toString());
	}


	public void setListener(SsoListener listener) {
		this.listener = listener;
	}

	@Override
	public void destroy() {}

	public void setSessionSharedListener(SessionSharedListener sessionSharedListener) {
		this.sessionSharedListener = sessionSharedListener;
	}


}
