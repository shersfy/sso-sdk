package org.young.sso.sdk.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.utils.SsoUtil;

/**
 * 缓存登录有效session
 * @author pengy
 * @date 2018年12月4日
 */
public class MemorySessionShared implements SessionSharedListener {
	
	private Logger logger = LoggerFactory.getLogger(MemorySessionShared.class);
	
	public static Map<String, HttpSession> cacheSessions = new ConcurrentHashMap<>();

	@Override
	public void invalidateSession(String sessionId) {
		if (StringUtils.isBlank(sessionId) || !cacheSessions.containsKey(sessionId)) {
			return;
		}
		HttpSession session = cacheSessions.get(sessionId);
		// 第1步，销毁session
		SsoUtil.invalidateSession(session);
	}
	
	@Override
	public void publishInvalidateSession(String sessionId) {
		this.invalidateSession(sessionId);
	}
	
	@Override
	public void addSession(HttpSession session) {
		if (session==null) {
			return;
		}
		cacheSessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		// 第2步，删除缓存
		String sessionId = event.getSession().getId();
		if (cacheSessions.containsKey(sessionId)) {
			cacheSessions.remove(sessionId);
			logger.info("invalidate session '{}' successful", sessionId);
		}
	}

}
