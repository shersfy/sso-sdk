package org.young.sso.sdk.autoconfig;

import javax.servlet.Filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.young.sso.sdk.filter.ChangeTenantFilter;
import org.young.sso.sdk.filter.WebSigninFilter;
import org.young.sso.sdk.filter.WebSignoutFilter;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.listener.SsoListener;

@Configuration
@ConditionalOnProperty(prefix=SsoProperties.PREFIX, value="enabled", havingValue="true")
public class SsoAutoConfiguration {
	
	@Bean
	public FilterRegistrationBean<WebSigninFilter> ssoFilter(@Autowired SsoProperties config,
			@Autowired(required=false) SsoListener listener,
			@Autowired(required=false) SessionSharedListener sessionSharedListener){
		
		WebSigninFilter filter = new WebSigninFilter();
		filter.setListener(listener);
		filter.setSessionSharedListener(sessionSharedListener);
		
		FilterRegistrationBean<WebSigninFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(1);
		bean.setFilter(filter);
		bean.setName("ssoFilter");
		bean.addUrlPatterns("/*");
		addInitParameters(bean, config);
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<WebSignoutFilter> signoutFilter(@Autowired SsoProperties config,
			@Autowired(required=false) SessionSharedListener sessionSharedListener){
		
		WebSignoutFilter filter = new WebSignoutFilter();
		filter.setSessionSharedListener(sessionSharedListener);
		
		FilterRegistrationBean<WebSignoutFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(2);
		bean.setFilter(filter);
		bean.setName("signoutFilter");
		bean.addUrlPatterns(ConstSso.SIGN_OUT);
		addInitParameters(bean, config);
		return bean;
	}
	
	@Bean
	@ConditionalOnProperty(prefix=SsoProperties.PREFIX, value="enabled-change-tenant", havingValue="true")
	public FilterRegistrationBean<ChangeTenantFilter> changeTenantFilter(@Autowired SsoProperties config){
		
		ChangeTenantFilter filter = new ChangeTenantFilter(config);
		FilterRegistrationBean<ChangeTenantFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(3);
		bean.setFilter(filter);
		bean.setName("changeTenantFilter");
		bean.addUrlPatterns("/*");
		
		return bean;
	}
	
	private void addInitParameters(FilterRegistrationBean<? extends Filter> bean, SsoProperties config) {
		bean.addInitParameter("enabled", String.valueOf(config.isEnabled()));
		bean.addInitParameter("outerEdpauthSrever", config.getOuterEdpauthSrever());
		bean.addInitParameter("innerEdpauthSrever", config.getInnerEdpauthSrever());
		bean.addInitParameter("webappServer", config.getWebappServer());
		
		bean.addInitParameter("enabledRsa", String.valueOf(config.isEnabledRsa()));
		bean.addInitParameter("asyncSupported", String.valueOf(config.isAsyncSupported()));
		bean.addInitParameter("autoRemoveWebappFromServer", String.valueOf(config.isAutoRemoveWebappFromServer()));
		bean.addInitParameter("cookieHttpOnly", String.valueOf(config.isCookieHttpOnly()));
		bean.addInitParameter("cookieSecure", String.valueOf(config.isCookieSecure()));
		bean.addInitParameter("tokenMaxAgeSeconds", String.valueOf(config.getTokenMaxAgeSeconds()));
		bean.addInitParameter("requestRemoteRetry", String.valueOf(config.getRequestRemoteRetry()));
		bean.addInitParameter("ignoreUrls", StringUtils.join(config.getIgnoreUrls(), ","));
		bean.addInitParameter("ignoreResources", StringUtils.join(config.getIgnoreResources(), ","));
	}
}
