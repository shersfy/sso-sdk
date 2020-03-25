package org.young.sso.sdk.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class CookieUtil {

	public static String getCookieValue(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static void addCookie(HttpServletResponse response, Cookie cookie) {
		response.addCookie(cookie);
	}

	public static void clearCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null) {
			return;
		}
		for (Cookie cookie : cookies) {
			cookie.setMaxAge(0);
			cookie.setValue(null);
			cookie.setPath(getCookiePath(request));
			response.addCookie(cookie);
		}
	}
	/**
	 * 获取Cookie保存路径
	 * 
	 * @return String
	 */
	public static String getCookiePath(HttpServletRequest request){
		String path = request.getContextPath();
		return StringUtils.isBlank(path) ? "/" : path;
	}
}
