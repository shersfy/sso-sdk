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
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.listener.MemorySessionShared;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.utils.SsoUtil;

@WebFilter(filterName = "signoutFilter", urlPatterns = {"/sign/out", "/logout"})
public class WebSignoutFilter implements Filter {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private SsoProperties ssoconf;

	@Autowired(required = false)
	private SessionSharedListener sessionSharedListener;

	/**
	 * 配置SSO相关属性，将会在WebSignoutFilter.init()中被调用
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
		
		LOGGER.info("{} initialized, sso server is '{}'", 
				WebSignoutFilter.class.getSimpleName(), ssoconf.getOuterSrever());

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!ssoconf.isEnabled()) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest req  = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		LOGGER.info("method:{}, port:{}, url:{}", req.getMethod(), req.getServerPort(), req.getRequestURL());

		// webapp get 请求
		if (StringUtils.isNotBlank(ssoconf.getOuterSrever()) 
				&& "get".equalsIgnoreCase(req.getMethod())) {
			destroyWebappSession(req, res);
			SsoUtil.redirectLogout(req, res, ssoconf);
			return;
		}

		// server执行
		if (StringUtils.isBlank(ssoconf.getOuterSrever())) {
			destroyServerSession(req, res);
			SsoUtil.redirectLogin(req, res, ssoconf);
			return;
		}

		destroyWebappSession(req, res);
	}

	@Override
	public void destroy() {

	}

	public void setSessionSharedListener(SessionSharedListener sessionSharedListener) {
		this.sessionSharedListener = sessionSharedListener;
	}

	private void destroyServerSession(HttpServletRequest req, HttpServletResponse res) {
		String sessionId = req.getSession().getId();
		// 退出已登录的APP
		if (!ssoconf.isAutoRemoveWebappFromServer()) {
			sessionSharedListener.removeWebapps(sessionId);
		}
		// 销毁session
		SsoUtil.invalidateSession(req.getSession());

		LOGGER.info("sign out successful. session={}", sessionId);
	}

	private void destroyWebappSession(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String sessionId = req.getParameter(ConstSso.LOGIN_SESSION_ID);
		// webapp执行，不能直接销毁session，需要根据session id找到对应的session销毁
		if (StringUtils.isBlank(sessionId)) {
			sessionId = req.getSession().getId();
		}
		// 广播发布指令执行销毁session
		sessionSharedListener.publishInvalidateSession(sessionId);
		LOGGER.debug("sessionId=getParameter('{}')", sessionId);

		SsoResult succ = new SsoResult(sessionId);
		succ.setMsg("sign out successful");
		succ.setModel(sessionId);

		res.setContentType(ContentType.APPLICATION_JSON.toString());
		res.getWriter().write(succ.toString());

		LOGGER.info("sign out successful. session={}", sessionId);
	}

}
