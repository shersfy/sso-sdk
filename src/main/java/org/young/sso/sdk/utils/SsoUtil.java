package org.young.sso.sdk.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.exception.SsoException;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.sdk.utils.HttpUtil.HttpResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public final class SsoUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(SsoUtil.class);

	private SsoUtil() {}

	/**
	 * 过滤器初始化Sso
	 * @param filterConfig
	 * @return
	 * @throws ServletException
	 */
	public static SsoProperties initSso(FilterConfig filterConfig) throws ServletException {
		String enabled        = filterConfig.getInitParameter("enabled");
		String outerEdpauthSrever = filterConfig.getInitParameter("outerEdpauthSrever");
		String innerEdpauthSrever = filterConfig.getInitParameter("innerEdpauthSrever");
		String webappServer	  = filterConfig.getInitParameter("webappServer");

		// webapp
		if (StringUtils.isNotBlank(webappServer)) {
			if (StringUtils.isBlank(innerEdpauthSrever)) {
				innerEdpauthSrever = outerEdpauthSrever;
			}
		}

		SsoProperties ssoProperties = new SsoProperties(StringUtils.isBlank(enabled)?false:Boolean.valueOf(enabled), 
				outerEdpauthSrever, webappServer);

		ssoProperties.setInnerEdpauthSrever(innerEdpauthSrever);
		ssoProperties.setWebappLogout(filterConfig.getInitParameter("webappLogout"));

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("asyncSupported"))) {
			ssoProperties.setAsyncSupported(Boolean.valueOf(filterConfig.getInitParameter("asyncSupported")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("autoRemoveWebappFromServer"))) {
			ssoProperties.setAutoRemoveWebappFromServer(Boolean.valueOf(filterConfig.getInitParameter("autoRemoveWebappFromServer")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("cookieName"))) {
			ssoProperties.setCookieName(String.valueOf(filterConfig.getInitParameter("cookieName")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("cookieHttpOnly"))) {
			ssoProperties.setCookieHttpOnly(Boolean.valueOf(filterConfig.getInitParameter("cookieHttpOnly")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("cookieSecure"))) {
			ssoProperties.setCookieSecure(Boolean.valueOf(filterConfig.getInitParameter("cookieSecure")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("tgtMaxAgeSeconds"))) {
			ssoProperties.setTgtMaxAgeSeconds(Integer.valueOf(filterConfig.getInitParameter("tgtMaxAgeSeconds")));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("ignoreUrls"))) {
			ssoProperties.setIgnoreUrls(filterConfig.getInitParameter("ignoreUrls").split(","));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("ignoreResources"))) {
			ssoProperties.setIgnoreResources(filterConfig.getInitParameter("ignoreResources").split(","));
		}

		if (StringUtils.isNotBlank(filterConfig.getInitParameter("sessionSharedListener"))) {
			ssoProperties.setSessionSharedListener(filterConfig.getInitParameter("sessionSharedListener"));
		}

		return ssoProperties;
	}

	/**
	 * 是否为ajax请求
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
	}

	/**
	 * 清除session
	 * @param session
	 */
	public static void invalidateSession(HttpSession session) {
		if (session==null) {
			return;
		}
		try {
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				session.removeAttribute(name);
			}
			session.invalidate();
		} catch (IllegalStateException e) {
			LOGGER.info(e.getMessage());
		}
	}


	/**
	 * 重定向到登录页面
	 * @param req
	 * @param res
	 * @param ssoProperties
	 * @throws IOException
	 */
	public static void redirectLogin(HttpServletRequest req, HttpServletResponse res, SsoProperties ssoProperties) throws IOException {

		String basePath  = req.getAttribute(ConstSso.BASE_PATH).toString();
		String loginPath = StringUtils.isNotBlank(ssoProperties.getOuterEdpauthSrever())?ssoProperties.getOuterEdpauthSrever():basePath;

		String encodeUrl = req.getParameter("webapp");
		encodeUrl = encodeUrl==null ?ssoProperties.getWebappServer() :encodeUrl;
		encodeUrl = URLEncoder.encode(encodeUrl, "UTF-8");

		loginPath = HttpUtil.concatUrl(loginPath, "/login");
		loginPath = loginPath+(StringUtils.isNotBlank(encodeUrl)?"?webapp="+encodeUrl:"");

		// 异步方式, 未登录返回状态-1
		if (ssoProperties.isAsyncSupported() || SsoUtil.isAjaxRequest(req)) {
			SsoResult result = new SsoResult(loginPath);
			result.setCode(ResultCode.NOT_LOGIN);
			result.setMsg("not login");
			res.setContentType(ContentType.APPLICATION_JSON.toString());
			res.getWriter().write(result.toString());
			return;
		}

		// 同步方式, 未登录重定向到登录页面
		res.sendRedirect(loginPath);
	}

	/**
	 * 重定向到SSO服务器错误页面
	 * @param res
	 * @param ssoProperties
	 * @param error
	 * @throws IOException
	 */
	public static void redirectServerError(HttpServletResponse res, SsoProperties ssoProperties, SsoResult error) throws IOException {
		String errorPath = HttpUtil.concatUrl(ssoProperties.getOuterEdpauthSrever(), "/error");
		errorPath = errorPath+String.format("?code=%s&msg=%s", error.getCode(), URLEncoder.encode(error.getMsg(), "UTF-8"));
		LOGGER.error("redirect '{}', error:{}", errorPath, error);
		res.sendRedirect(errorPath);
	}

	/***
	 * 校验ST有效性
	 * @param req 请求
	 * @param res 响应
	 * @param ssoProperties sso配置
	 * @param token
	 */
	public static SsoResult requestValidate(HttpServletRequest req, SsoProperties ssoProperties, int retry) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "Access SSO server retry timeout");
		}

		String url = ssoProperties.getInnerEdpauthSrever();
		url = url.endsWith("/")?url.substring(0, url.length()-1):url;
		url = url+"/login/validate";

		String st = req.getParameter(ConstSso.LOGIN_TICKET_KEY);
		String rk = req.getParameter(ConstSso.LOGIN_REQUEST_KEY);
		try {
			String apphost = new URL(ssoProperties.getWebappServer()).getHost();
			rk = rk.split("-")[1];
			rk = SsoAESUtil.decryptHexStr(rk, apphost);
		} catch (Exception e) {
			LOGGER.error("decrypt error", e);
			return new SsoResult(520, "client webapp request key rk decrypt error:"+SsoException.getRootCauseMsg(e));
		}

		JSONObject json = new JSONObject();
		json.put("rk", rk);
		json.put("st", st);
		json.put("webappSession", req.getSession().getId());
		json.put("webappServer", ssoProperties.getWebappServer());
		json.put("webappLogout", ConstSso.SIGN_OUT[0]);

		String data = json.toString();
		if (ssoProperties.isEnabledRsa()) {
			try {
				data = new String(SsoRSAUtil.encrypt(data.getBytes()));
			} catch (Exception e) {
				LOGGER.error("rsa encrypt error", e);
				// 2000014=客户端应用加密异常
				SsoResult res = new SsoResult();
				res.setCode(2000014);
				return new SsoResult(520, "client webapp rsa encrypt error:"+SsoException.getRootCauseMsg(e));
			}
		}

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("data", data));

		LOGGER.info("request '{}'", url);
		HttpResult res = HttpUtil.send(url, "post", params, null);
		if (res.getCode()!=200) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			// 递归重试
			int seconds = RandomUtils.nextInt(5);
			LOGGER.info("await {} seconds retry ...", seconds);
			try {
				Thread.sleep(seconds*1000L);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return requestValidate(req, ssoProperties, retry-1);
		}

		try {
			SsoResult result = JSON.parseObject(res.getBody(), SsoResult.class);
			return result;
		} catch (Exception e) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			LOGGER.error("", e);
			return new SsoResult(520, "client webapp login validate error:"+SsoException.getRootCauseMsg(e));
		}

	}

	/**
	 * 刷新token
	 * @param ssoProperties
	 * @param oldToken 
	 * @param remoteAddr
	 * @param retry
	 * @return
	 */
	public static String requestRenewToken(SsoProperties ssoProperties, String oldToken, String remoteAddr, int retry) {

		if (retry<=0) {
			return null;
		}

		String url = HttpUtil.concatUrl(ssoProperties.getInnerEdpauthSrever(), "/login/renew");
		JSONObject json = new JSONObject();
		json.put("t", oldToken);
		json.put("webappServer", ssoProperties.getWebappServer());
		json.put("remoteAddr", remoteAddr);

		String data = json.toString();
		if (ssoProperties.isEnabledRsa()) {
			try {
				data = new String(SsoRSAUtil.encrypt(data.getBytes()));
			} catch (Exception e) {
				LOGGER.error("rsa encrypt error", e);
				return null;
			}
		}

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("data", data));

		LOGGER.info("request '{}'", url);
		HttpResult res = HttpUtil.send(url, "post", params, null);
		if (res.getCode()!=200) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			// 递归重试
			int seconds = RandomUtils.nextInt(5);
			LOGGER.info("await {} seconds retry ...", seconds);
			try {
				Thread.sleep(seconds*1000L);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return requestRenewToken(ssoProperties, oldToken, remoteAddr, retry-1);
		}

		String newToken = null;
		try {
			SsoResult result = JSON.parseObject(res.getBody(), SsoResult.class);
			if (result.getCode()==ResultCode.SUCESS) {
				newToken = result.getModel().toString();
			}
		} catch (Exception e) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			LOGGER.error("", e);
		}

		return newToken;
	}

	/**
	 * 获取角色列表
	 * @param ssoProperties
	 * @param retry
	 * @param token
	 * @param keyword
	 * @param editable
	 * @param disabled
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static SsoResult requestRoleList(SsoProperties ssoProperties, int retry,
			String token, String keyword, Boolean editable, Boolean disabled,
			Integer pageNo, Integer pageSize) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "数据加载失败");
		}

		String url = HttpUtil.concatUrl(ssoProperties.getInnerEdpauthSrever(), "/resource/webapp/role/list");

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("t", token));
		if (StringUtils.isNotBlank(keyword)) {
			params.add(new BasicNameValuePair("keyword", keyword));
		}
		if (editable!=null) {
			params.add(new BasicNameValuePair("editable", editable.toString()));
		}
		if (disabled!=null) {
			params.add(new BasicNameValuePair("disabled", disabled.toString()));
		}
		if (pageNo!=null) {
			params.add(new BasicNameValuePair("pageNo", pageNo.toString()));
		}
		if (pageSize!=null) {
			params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		}

		LOGGER.info("request '{}'", url);
		HttpResult res = HttpUtil.send(url, "post", params, null);
		if (res.getCode()!=200) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			// 递归重试
			int seconds = RandomUtils.nextInt(5);
			LOGGER.info("await {} seconds retry ...", seconds);
			try {
				Thread.sleep(seconds*1000L);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return requestRoleList(ssoProperties, retry-1, token, keyword, editable, disabled, pageNo, pageSize);
		}

		try {
			SsoResult result = JSON.parseObject(res.getBody(), SsoResult.class);
			return result;
		} catch (Exception e) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			LOGGER.error("", e);
			return new SsoResult(res.getCode(), res.getBody(), null);
		}

	}

	/**
	 * 查询全部角色
	 * @param ssoProperties
	 * @param retry
	 * @param token
	 * @return
	 */
	public static SsoResult requestRoleAll(SsoProperties ssoProperties, int retry, String token) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "数据加载失败");
		}

		String url = HttpUtil.concatUrl(ssoProperties.getInnerEdpauthSrever(), "/resource/webapp/role/all");

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("t", token));

		LOGGER.info("request '{}'", url);
		HttpResult res = HttpUtil.send(url, "post", params, null);
		if (res.getCode()!=200) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			// 递归重试
			int seconds = RandomUtils.nextInt(5);
			LOGGER.info("await {} seconds retry ...", seconds);
			try {
				Thread.sleep(seconds*1000L);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return requestRoleAll(ssoProperties, retry-1, token);
		}

		try {
			SsoResult result = JSON.parseObject(res.getBody(), SsoResult.class);
			return result;
		} catch (Exception e) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			LOGGER.error("", e);
			return new SsoResult(res.getCode(), res.getBody(), null);
		}

	}

	/***
	 * 获取登录用户信息
	 * @param request  请求对象
	 * @param ssoProperties SSO配置
	 * @return 登录用户
	 */
	public static LoginUser requestLoginUser(SsoProperties ssoProperties, String token) {
		return requestLoginUser(ssoProperties, token, ssoProperties.getRequestRemoteRetry());
	}

	/***
	 * 获取登录用户信息
	 * @param request  请求对象
	 * @param ssoProperties SSO配置
	 * @param retry 重试次数
	 * @return 登录用户
	 */
	public static LoginUser requestLoginUser(SsoProperties ssoProperties, String token, int retry) {
		if (StringUtils.isBlank(token) || retry<=0) {
			return null;
		}

		String url = HttpUtil.concatUrl(ssoProperties.getInnerEdpauthSrever(), "/resource/webapp/user");

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("t", token));

		LOGGER.info("request '{}'", url);
		HttpResult res = HttpUtil.send(url, "post", params, null);
		if (res.getCode()!=200) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			// 递归重试
			int seconds = RandomUtils.nextInt(5);
			LOGGER.info("await {} seconds retry ...", seconds);
			try {
				Thread.sleep(seconds*1000L);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return requestLoginUser(ssoProperties, token, retry-1);
		}

		LoginUser loginUser = null;
		try {
			SsoResult result = JSON.parseObject(res.getBody(), SsoResult.class);
			if (result.getCode() == ResultCode.SUCESS) {
				loginUser = JSON.parseObject(result.getModel().toString(), LoginUser.class);
			} else {
				LOGGER.error("request error, result: {}", result);
			}
		} catch (Exception e) {
			LOGGER.error("request error, code={}, url={}, body={}", 
					res.getCode(), res.getUrl(), res.getBody());
			LOGGER.error("", e);
		}

		return loginUser;
	}

	/***
	 * 从存储中获取token
	 * @param req 请求
	 * @param fromSession true从session获取否则从cookie获取
	 * @return
	 */
	public static String getTGCFormStorage(HttpServletRequest req, boolean fromSession) {
		// session模式存储
		if (fromSession) {
			Object token = req.getSession().getAttribute(ConstSso.LOGIN_TICKET_KEY);
			return token!=null?token.toString():null;
		}
		// cookie
		return CookieUtil.getCookieValue(req, ConstSso.LOGIN_TICKET_KEY);
	}

	public static void removeTokenFormStorage(HttpServletRequest req, HttpServletResponse res) {
		req.getSession().removeAttribute(ConstSso.LOGIN_TICKET_KEY);
		CookieUtil.clearCookie(req, res);
	}

	/***
	 * 从存储中获取语言
	 * @param req 请求
	 * @param fromSession true从session获取否则从cookie获取
	 * @return token
	 */
	public static String getLangFormStorage(HttpServletRequest req, boolean fromSession) {
		// session模式存储
		String lang = Locale.CHINA.toString();
		if (fromSession) {
			Object sessionLang = req.getSession().getAttribute(ConstSso.LOGIN_LANGUAGE);
			if (sessionLang !=null ) {
				lang = sessionLang.toString();
				return lang;
			}
		}
		// cookie
		String cookieLang = CookieUtil.getCookieValue(req, ConstSso.LOGIN_TICKET_KEY);
		if (StringUtils.isNotBlank(cookieLang)) {
			lang = cookieLang;
		}
		return lang;
	}

	public static void saveLoginUser(HttpServletRequest req, String user) {
		req.getSession().setAttribute(ConstSso.SESSION_LOGIN_USER, user);
	}

	/***
	 * 存储token
	 * @param req 请求
	 * @param res 响应
	 * @param ssoProperties sso配置
	 * @param token
	 */
	@Deprecated
	public static void saveTGC(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoProperties, String token) {

		saveCookie(req, res, ssoProperties, ConstSso.LOGIN_TICKET_KEY, token);
		req.getSession().setAttribute(ConstSso.LOGIN_TICKET_KEY, token);

	}

	/**
	 * 存储语言
	 * @param req
	 * @param res
	 * @param ssoProperties
	 * @param lang
	 */
	public static void saveLanguage(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoProperties, String lang) {
		saveCookie(req, res, ssoProperties, ConstSso.LOGIN_LANGUAGE, lang);
		req.getSession().setAttribute(ConstSso.LOGIN_LANGUAGE, lang);
	}


	/**
	 * 存储cookie
	 * @param req 请求
	 * @param res 响应
	 * @param ssoProperties sso配置
	 * @param key
	 * @param value
	 */
	private static void saveCookie(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoProperties, String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setDomain(req.getServerName());
		cookie.setMaxAge(req.getSession().getMaxInactiveInterval());
		cookie.setHttpOnly(ssoProperties.isCookieHttpOnly());
		cookie.setSecure(ssoProperties.isCookieSecure());
		CookieUtil.addCookie(res, cookie);
	}

	/**
	 * 隐藏截取
	 * @param ticket
	 * @return
	 */
	public static String hiddenTicket(String ticket) {
		return hiddenTicket(ticket, ConstSso.HIDDEN_REMAIN);
	}

	/**
	 * 隐藏截取
	 * @param ticket
	 * @param remain
	 * @return
	 */
	public static String hiddenTicket(String ticket, int remain) {
		if (ticket!=null) {
			if (ticket.length()<=remain) {
				return ticket;
			}
			ticket = ConstSso.HIDDEN_CODE+ticket.substring(ticket.length()-remain);
		}
		return ticket;
	}

	/**
	 * 存储session属性
	 * @param session
	 * @param name
	 * @param value
	 */
	public static void setSessionAttribute(HttpSession session, String serviceId, String name, Object value) {
		if (session==null || StringUtils.isBlank(name)) {
			return ;
		}
		if (StringUtils.isNotBlank(serviceId)) {
			name = String.format("%s.%s", serviceId, name);
		}
		session.setAttribute(name, value);
	};

	/**
	 * 获取session属性
	 * @param session
	 * @param name
	 * @param value
	 */
	public static Object getSessionAttribute(HttpSession session, String serviceId, String name) {
		if (session==null || StringUtils.isBlank(name)) {
			return null;
		}

		if (StringUtils.isNotBlank(serviceId)) {
			name = String.format("%s.%s", serviceId, name);
		}

		return session.getAttribute(name);
	};

}
