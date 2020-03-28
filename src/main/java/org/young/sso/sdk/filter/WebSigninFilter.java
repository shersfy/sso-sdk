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
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.sdk.utils.SsoUtil;

import com.alibaba.fastjson.JSON;


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

		LOGGER.debug("== method:{}", req.getMethod());
		LOGGER.debug("== port:{}", req.getServerPort());
		LOGGER.debug("== url:{}", req.getRequestURL());
		LOGGER.debug("== outerEdpauthSrever:{}", ssoProperties.getOuterEdpauthSrever());
		LOGGER.debug("== ticket:{}", req.getParameter(ConstSso.LOGIN_TICKET_KEY));
		
		this.setBasePath(req);
		String url = req.getRequestURI();
		
		// 登录系统重定向过来的请求
		boolean isWebappLogin = StringUtils.isNotBlank(req.getParameter(ConstSso.LOGIN_TICKET_KEY));
		isWebappLogin = isWebappLogin && StringUtils.isNotBlank(req.getParameter(ConstSso.LOGIN_REQUEST_KEY));
		isWebappLogin = isWebappLogin && StringUtils.isNotBlank(ssoProperties.getOuterEdpauthSrever());
		if (isWebappLogin) {
			if (StringUtils.isBlank(ssoProperties.getWebappServer())) {
				throw new ServletException("client webappServer cannot be blank");
			}
			
			if (isLogined(req, res)) {
				LOGGER.info("redirect to {}", req.getRequestURL());
				res.sendRedirect(url);
				return;
			}
			
			// webapp 执行: 校验ST有效性
			String webapp = ssoProperties.getWebappServer();
			SsoResult validate = SsoUtil.requestValidate(req, ssoProperties, ssoProperties.getRequestRemoteRetry());
			if (validate.getCode()!=ResultCode.SUCESS) {
				LOGGER.error("webapp '{}' validate failed", webapp);
				SsoUtil.redirectServerError(res, ssoProperties, validate);
				return;
			}
			
			String loginUser = JSON.toJSONString(validate.getModel());
			SsoUtil.saveLoginUser(req, loginUser);
			sessionSharedListener.addSession(req.getSession());
			LOGGER.info("sign in successful. webapp={}, session={}, loginUser={}", 
					webapp, req.getSession().getId(), loginUser);
			
			LOGGER.info("redirect to {}", req.getRequestURL());
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
		
		Object user = req.getSession().getAttribute(ConstSso.SESSION_LOGIN_USER);
		if (user!=null) {
			return true;
		}
		return false;
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
