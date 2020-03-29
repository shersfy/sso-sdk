package org.young.sso.sdk.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
		
		SsoProperties prop = new SsoProperties();
		// 设值
		Map<String, Method> methods = new HashMap<>();
		for (Method md :prop.getClass().getDeclaredMethods()) {
			if (md.getName().startsWith("set")) {
				methods.put(md.getName(), md);
			}
		}
		
		Enumeration<String> names = filterConfig.getInitParameterNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			String value = filterConfig.getInitParameter(name);
			String setter = "set"+StringUtils.capitalize(name);
			LOGGER.info("sso property {}={}", name, value);
			if (value==null) {
				continue;
			}
			try {
				Method method = methods.get(setter);
				Class<?> type = method.getParameters()[0].getType();
				if (type==boolean.class || type==Boolean.class) {
					method.invoke(prop, Boolean.valueOf(value));
				}
				else if (type==int.class || type==Integer.class) {
					method.invoke(prop, Integer.valueOf(value));
				}
				else if (type==long.class || type==Long.class) {
					method.invoke(prop, Long.valueOf(value));
				}
				else if (type==String[].class) {
					method.invoke(prop, (Object)value.split(","));
				}
				else {
					method.invoke(prop, value);
				}
				
			} catch (Exception e) {
				throw new ServletException(e);
			}
			
		}
		
		if (StringUtils.isBlank(prop.getInnerSrever())) {
			prop.setInnerSrever(prop.getOuterSrever());
		}

		return prop;
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
	 * @param ssoconf
	 * @throws IOException
	 */
	public static void redirectLogin(HttpServletRequest req, HttpServletResponse res, SsoProperties ssoconf) throws IOException {

		String basePath  = req.getAttribute(ConstSso.BASE_PATH).toString();
		String loginPath = StringUtils.isNotBlank(ssoconf.getOuterSrever())?ssoconf.getOuterSrever():basePath;

		String encodeUrl = req.getParameter("webapp");
		if (encodeUrl==null) {
			encodeUrl = ssoconf.getWebappServer();
			if (StringUtils.isNotBlank(ssoconf.getOuterSrever())) {
				encodeUrl = req.getRequestURL().toString();
			}
		}
		
		encodeUrl = StringUtils.isBlank(encodeUrl) ?"" :encodeUrl;
		encodeUrl = URLEncoder.encode(encodeUrl, "UTF-8");

		loginPath = HttpUtil.concatUrl(loginPath, "/login");
		loginPath = loginPath+(StringUtils.isNotBlank(encodeUrl)?"?webapp="+encodeUrl:"");

		// 异步方式, 未登录返回状态-1
		if (ssoconf.isAsyncSupported() || SsoUtil.isAjaxRequest(req)) {
			SsoResult result = new SsoResult(loginPath);
			result.setCode(ResultCode.NOT_LOGIN);
			result.setMsg("not login");
			res.setContentType(ContentType.APPLICATION_JSON.toString());
			res.getWriter().write(result.toString());
			return;
		}

		// 同步方式, 未登录重定向到登录页面
		LOGGER.info("redirect to {}", loginPath);
		res.sendRedirect(loginPath);
	}
	
	/**
	 * 重定向到退出
	 * @param req
	 * @param res
	 * @param ssoconf
	 * @throws IOException
	 */
	public static void redirectLogout(HttpServletRequest req, HttpServletResponse res, SsoProperties ssoconf) throws IOException {
		
		String basePath  = req.getAttribute(ConstSso.BASE_PATH).toString();
		String logoutPath = StringUtils.isNotBlank(ssoconf.getOuterSrever())?ssoconf.getOuterSrever():basePath;
		logoutPath = HttpUtil.concatUrl(logoutPath, ConstSso.SIGN_OUT[0]);
		// 重定向到登出
		LOGGER.info("redirect to {}", logoutPath);
		res.sendRedirect(logoutPath);
	}

	/**
	 * 重定向到SSO服务器错误页面
	 * @param res
	 * @param ssoconf
	 * @param error
	 * @throws IOException
	 */
	public static void redirectServerError(HttpServletResponse res, SsoProperties ssoconf, SsoResult error) throws IOException {
		String errorPath = HttpUtil.concatUrl(ssoconf.getOuterSrever(), "/error");
		errorPath = errorPath+String.format("?code=%s&msg=%s", error.getCode(), URLEncoder.encode(error.getMsg(), "UTF-8"));
		LOGGER.error("redirect to {}, error:{}", errorPath, error);
		res.sendRedirect(errorPath);
	}

	/***
	 * 校验ST有效性
	 * @param req 请求
	 * @param res 响应
	 * @param ssoconf sso配置
	 * @param token
	 */
	public static SsoResult requestValidate(HttpServletRequest req, SsoProperties ssoconf, int retry) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "Access SSO server retry timeout");
		}

		String url = ssoconf.getInnerSrever();
		url = url.endsWith("/")?url.substring(0, url.length()-1):url;
		url = url+"/login/validate";

		String st = req.getParameter(ConstSso.LOGIN_TICKET_KEY);
		String rk = req.getParameter(ConstSso.LOGIN_REQUEST_KEY);
		try {
			String apphost = new URL(ssoconf.getWebappServer()).getHost();
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
		json.put("webappServer", ssoconf.getWebappServer());
		json.put("webappLogout", ConstSso.SIGN_OUT[0]);

		String data = json.toString();
		if (ssoconf.isEnabledRsa()) {
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
			return requestValidate(req, ssoconf, retry-1);
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
	 * @param ssoconf
	 * @param oldToken 
	 * @param remoteAddr
	 * @param retry
	 * @return
	 */
	public static String requestRenewToken(SsoProperties ssoconf, String oldToken, String remoteAddr, int retry) {

		if (retry<=0) {
			return null;
		}

		String url = HttpUtil.concatUrl(ssoconf.getInnerSrever(), "/login/renew");
		JSONObject json = new JSONObject();
		json.put("t", oldToken);
		json.put("webappServer", ssoconf.getWebappServer());
		json.put("remoteAddr", remoteAddr);

		String data = json.toString();
		if (ssoconf.isEnabledRsa()) {
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
			return requestRenewToken(ssoconf, oldToken, remoteAddr, retry-1);
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
	 * @param ssoconf
	 * @param retry
	 * @param token
	 * @param keyword
	 * @param editable
	 * @param disabled
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static SsoResult requestRoleList(SsoProperties ssoconf, int retry,
			String token, String keyword, Boolean editable, Boolean disabled,
			Integer pageNo, Integer pageSize) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "数据加载失败");
		}

		String url = HttpUtil.concatUrl(ssoconf.getInnerSrever(), "/resource/webapp/role/list");

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
			return requestRoleList(ssoconf, retry-1, token, keyword, editable, disabled, pageNo, pageSize);
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
	 * @param ssoconf
	 * @param retry
	 * @param token
	 * @return
	 */
	public static SsoResult requestRoleAll(SsoProperties ssoconf, int retry, String token) {

		if (retry<=0) {
			return new SsoResult(SsoResult.ResultCode.FAIL, "数据加载失败");
		}

		String url = HttpUtil.concatUrl(ssoconf.getInnerSrever(), "/resource/webapp/role/all");

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
			return requestRoleAll(ssoconf, retry-1, token);
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
	 * @param ssoconf SSO配置
	 * @return 登录用户
	 */
	public static LoginUser requestLoginUser(SsoProperties ssoconf, String token) {
		return requestLoginUser(ssoconf, token, ssoconf.getRequestRemoteRetry());
	}

	/***
	 * 获取登录用户信息
	 * @param request  请求对象
	 * @param ssoconf SSO配置
	 * @param retry 重试次数
	 * @return 登录用户
	 */
	public static LoginUser requestLoginUser(SsoProperties ssoconf, String token, int retry) {
		if (StringUtils.isBlank(token) || retry<=0) {
			return null;
		}

		String url = HttpUtil.concatUrl(ssoconf.getInnerSrever(), "/resource/webapp/user");

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
			return requestLoginUser(ssoconf, token, retry-1);
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
	 * @param ssoconf sso配置
	 * @param token
	 */
	@Deprecated
	public static void saveTGC(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoconf, String token) {

		saveCookie(req, res, ssoconf, ConstSso.LOGIN_TICKET_KEY, token);
		req.getSession().setAttribute(ConstSso.LOGIN_TICKET_KEY, token);

	}

	/**
	 * 存储语言
	 * @param req
	 * @param res
	 * @param ssoconf
	 * @param lang
	 */
	public static void saveLanguage(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoconf, String lang) {
		saveCookie(req, res, ssoconf, ConstSso.LOGIN_LANGUAGE, lang);
		req.getSession().setAttribute(ConstSso.LOGIN_LANGUAGE, lang);
	}


	/**
	 * 存储cookie
	 * @param req 请求
	 * @param res 响应
	 * @param ssoconf sso配置
	 * @param key
	 * @param value
	 */
	private static void saveCookie(HttpServletRequest req, HttpServletResponse res, 
			SsoProperties ssoconf, String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		if (StringUtils.isNotBlank(ssoconf.getCookie().getDomain())) {
			cookie.setDomain(ssoconf.getCookie().getDomain());
		} else {
			cookie.setDomain(req.getServerName());
		}
		cookie.setMaxAge(req.getSession().getMaxInactiveInterval());
		cookie.setHttpOnly(ssoconf.getCookie().isHttpOnly());
		cookie.setSecure(ssoconf.getCookie().isSecure());
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
