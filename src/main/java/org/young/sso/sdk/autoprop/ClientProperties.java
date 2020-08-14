package org.young.sso.sdk.autoprop;

import org.young.sso.sdk.resource.ClientAuthModel;

public class ClientProperties {
	
	/**
	 * SSO 客户端应用登录认证模式
	 */
	private ClientAuthModel authModel = ClientAuthModel.session;
	
	/**
	 * authModel=auth2时有效, 设置auth2模式下request传递token的header名
	 */
	private String auth2HeaderName    = "_edp_";

	public ClientAuthModel getAuthModel() {
		return authModel;
	}

	public void setAuthModel(ClientAuthModel authModel) {
		this.authModel = authModel;
	}

	public String getAuth2HeaderName() {
		return auth2HeaderName;
	}

	public void setAuth2HeaderName(String auth2HeaderName) {
		this.auth2HeaderName = auth2HeaderName;
	}
	
}
