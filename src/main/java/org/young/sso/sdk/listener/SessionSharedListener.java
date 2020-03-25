package org.young.sso.sdk.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 分布式共享session
 * @author pengy
 * @date 2018年12月4日
 */
public interface SessionSharedListener extends HttpSessionListener{
	
	/**
	 * 退出登录当前session的APP
	 * @param sessionId
	 */
	default void removeWebapps(String sessionId) {
		
	}
	
	/**
	 * 发布session销毁指令
	 * @param sessionId
	 */
	default void publishInvalidateSession(String sessionId) {
		
	}
	
	/**
	 * 销毁分布式共享session
	 * @param sessionId
	 */
	void invalidateSession(String sessionId);
	
	/**
	 * 添加session到共享存储
	 * @param session
	 */
	default void addSession(HttpSession session) {}
	
	
	@Override
	default void sessionCreated(HttpSessionEvent event) {
		
	}

	@Override
	default void sessionDestroyed(HttpSessionEvent event) {
		
	}

}
