package org.young.sso.sdk.autoprop;

import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * 微信扫码登录配置
 * @author Young
 * @date 2020-08-12
 */
public class WeixinProperties {
	
	/**
	 * 服务商的corpid
	 */
	private String corpid;
	
	/**
	 * 服务商的secret，在服务商管理后台可见
	 */
	private String providerSecret;
	
	/**
	 * 扫码登录地址
	 */
	private String loginUri = "https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid={0}&redirect_uri={1}";

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getProviderSecret() {
		return providerSecret;
	}

	public void setProviderSecret(String providerSecret) {
		this.providerSecret = providerSecret;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}
	
	public String getLoginUri() {
		return loginUri;
	}
	
	public String getLoginUri(String callback) {
		try {
			callback = URLEncoder.encode(callback, "UTF-8");
		} catch (Exception e) {
		}
		return MessageFormat.format(this.loginUri, this.corpid, callback);
	}
	
}
