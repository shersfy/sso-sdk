package org.young.sso.sdk.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoprop.SsoProperties;
import org.young.sso.sdk.resource.ClientAuthModel;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.sdk.security.DefaultSsoListener;
import org.young.sso.sdk.security.MemorySessionShared;
import org.young.sso.sdk.security.SessionSharedListener;
import org.young.sso.sdk.security.SsoCredentials;
import org.young.sso.sdk.security.SsoCredentialsAuth2;
import org.young.sso.sdk.security.SsoCredentialsSession;
import org.young.sso.sdk.security.SsoListener;
import org.young.sso.sdk.security.SsoPrincipal;
import org.young.sso.sdk.utils.SsoUtil;

import com.alibaba.fastjson.JSON;


/**
 * 登录过滤器
 * @author pengy
 * @date 2018年11月8日
 */
@WebFilter(filterName = "signinFilter", urlPatterns = "/*")
public class WebSigninFilter implements Filter {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private SsoProperties ssoconf;

	@Autowired(required = false)
	private SsoListener listener;
	
	@Autowired(required = false)
	private SessionSharedListener sessionSharedListener;
	
	@Autowired(required = false)
	private SsoCredentials credentials;
	
	public WebSigninFilter() {}
	
	/**
	 * 配置SSO相关属性，将会在WebSigninFilter.init()中被调用
	 * @return
	 */
	public SsoProperties resetSsoProperties() {
		return ssoconf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ssoconf = resetSsoProperties();
		ssoconf = ssoconf==null?SsoUtil.initSso(filterConfig):ssoconf;
		
		listener = listener==null ?new DefaultSsoListener(ssoconf) :listener;
		
		String className = ssoconf.getSessionSharedListener();
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
		
		credentials = credentials==null? getDefaultCredentials():credentials;
		SsoUtil.registerFilter(this);
		
		LOGGER.info("{} initialized, sso server is '{}', and listener is {}", 
				WebSigninFilter.class.getSimpleName(), ssoconf.getOuterSrever(), listener.getClass().getSimpleName());
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
		LOGGER.debug("== outerSrever:{}", ssoconf.getOuterSrever());
		LOGGER.debug("== ticket:{}", req.getParameter(ConstSso.LOGIN_TICKET_KEY));
		
		SsoUtil.setBasePath(req);
		String url = req.getRequestURI();
		
		// 登录系统重定向过来的请求
		boolean isWebappLogin = StringUtils.isNotBlank(req.getParameter(ConstSso.LOGIN_TICKET_KEY));
		isWebappLogin = isWebappLogin && StringUtils.isNotBlank(req.getParameter(ConstSso.LOGIN_REQUEST_KEY));
		isWebappLogin = isWebappLogin && StringUtils.isNotBlank(ssoconf.getOuterSrever());
		if (isWebappLogin) {
			if (StringUtils.isBlank(ssoconf.getWebappServer())) {
				throw new ServletException("client webappServer cannot be blank");
			}
			
			if (isLogined(req, res)) {
				LOGGER.info("redirect to {}", req.getRequestURL());
				SsoUtil.redirectLogined(req, res, ssoconf, credentials.getUserPrincipal(req));
				return;
			}
			
			// webapp 执行: 校验ST有效性
			String webapp = ssoconf.getWebappServer();
			SsoResult validate = SsoUtil.requestValidate(req, ssoconf, ssoconf.getRequestRemoteRetry());
			if (validate.getCode()!=ResultCode.SUCESS) {
				LOGGER.error("webapp '{}' validate failed", webapp);
				SsoUtil.redirectServerError(res, ssoconf, validate);
				return;
			}
			
			// 存储校验通过的会话和登录用户信息
			// 废弃旧session, 创建新session
			SsoUtil.invalidateSession(req.getSession());
			sessionSharedListener.addSession(req);
			
			LoginUser user = JSON.parseObject(JSON.toJSONString(validate.getModel()), LoginUser.class);
			SsoPrincipal principal = credentials.setUserPrincipal(req, user);
			
			LOGGER.info("sign in successful. webapp={}, session={}, LoginUser={}", 
					webapp, req.getSession().getId(), user.toString());
			LOGGER.info("redirect to {}", req.getRequestURL());
			
			SsoUtil.redirectLogined(req, res, ssoconf, principal);
			return;
		}

		// 被忽略或已登录的请求通行
		if (isLogined(req, res)) {
			req.setAttribute(ConstSso.APP_SERVER, ssoconf.getWebappServer());
			req.setAttribute(ConstSso.SSO_SREVER, ssoconf.getOuterSrever());
			chain.doFilter(request, response);
			return;
		}
		
		if (ignore(url)) {
			chain.doFilter(request, response);
			return;
		}

		// 重定向到登录页面
		SsoUtil.redirectLogin(req, res, ssoconf);
	}

	/***
	 * 是否登录
	 * @param req
	 * @param res
	 * @return true已登录，false未登录
	 */
	private boolean isLogined(HttpServletRequest req, HttpServletResponse res) {
		
		if (credentials.getUserPrincipal(req)==null) {
			return false;
		}
		
		return true;
	}

	private boolean ignore(String url) {
		for (String ignore :ssoconf.getIgnoreUrls()) {
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
		for (String ignore :ssoconf.getIgnoreResources()) {
			if (url.endsWith(ignore)) {
				return true;
			}
		}
		return false;
	}

	public void setListener(SsoListener listener) {
		this.listener = listener;
	}

	@Override
	public void destroy() {}

	public void setSessionSharedListener(SessionSharedListener sessionSharedListener) {
		this.sessionSharedListener = sessionSharedListener;
	}
	
	private SsoCredentials getDefaultCredentials() {
		
		if (ClientAuthModel.auth2==ssoconf.getClient().getAuthModel()) {
			return new SsoCredentialsAuth2();
		}
		
		return new SsoCredentialsSession();
	}

	public SsoCredentials getCredentials() {
		return credentials;
	}

}
