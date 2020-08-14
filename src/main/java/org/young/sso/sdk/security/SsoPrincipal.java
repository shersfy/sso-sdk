package org.young.sso.sdk.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

import org.young.sso.sdk.resource.LoginUser;

import com.alibaba.fastjson.JSON;

/**
 * 登录用户身份信息
 * @author Young
 * @date 2020-08-14
 */
public class SsoPrincipal implements Principal, Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 会话ID
	 */
	private String id;
	
	/**
	 * 登录用户
	 */
	private LoginUser user;
	
	/**
	 * 授信访问令牌
	 */
	private Map<String, Object> accesskey;
	
	public SsoPrincipal() {
		super();
	}
	
	public SsoPrincipal(String id, LoginUser user) {
		super();
		this.id = id;
		this.user = user;
	}

	@Override
	public String getName() {
		return user==null ?null :user.getUsername();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public LoginUser getUser() {
		return user;
	}

	public void setUser(LoginUser user) {
		this.user = user;
	}

	public Map<String, Object> getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(Map<String, Object> accesskey) {
		this.accesskey = accesskey;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	

}
