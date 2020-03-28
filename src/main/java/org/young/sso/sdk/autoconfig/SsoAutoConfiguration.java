package org.young.sso.sdk.autoconfig;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.young.sso.sdk.filter.WebSigninFilter;
import org.young.sso.sdk.filter.WebSignoutFilter;

@ImportAutoConfiguration({WebSigninFilter.class, WebSignoutFilter.class})
@Configuration
@ConditionalOnProperty(prefix=SsoProperties.PREFIX, value="enabled", havingValue="true")
public class SsoAutoConfiguration {

//	@Bean
//	public FilterRegistrationBean<WebSigninFilter> ssoFilter(@Autowired SsoProperties config,
//			@Autowired(required=false) SsoListener listener,
//			@Autowired(required=false) SessionSharedListener sessionSharedListener) throws Exception{
//
//		WebSigninFilter filter = new WebSigninFilter();
//		filter.setListener(listener);
//		filter.setSessionSharedListener(sessionSharedListener);
//
//		FilterRegistrationBean<WebSigninFilter> bean = new FilterRegistrationBean<>();
//		bean.setOrder(1);
//		bean.setFilter(filter);
//		bean.setName("ssoFilter");
//		bean.addUrlPatterns("/*");
//		addInitParameters(bean, config);
//		return bean;
//	}

//	@Bean
//	public FilterRegistrationBean<WebSignoutFilter> signoutFilter(@Autowired SsoProperties config,
//			@Autowired(required=false) SessionSharedListener sessionSharedListener) throws Exception{
//
//		WebSignoutFilter filter = new WebSignoutFilter();
//		filter.setSessionSharedListener(sessionSharedListener);
//
//		FilterRegistrationBean<WebSignoutFilter> bean = new FilterRegistrationBean<>();
//		bean.setOrder(2);
//		bean.setFilter(filter);
//		bean.setName("signoutFilter");
//		bean.addUrlPatterns(ConstSso.SIGN_OUT);
//		addInitParameters(bean, config);
//		return bean;
//	}

//	private void addInitParameters(FilterRegistrationBean<? extends Filter> bean, SsoProperties config) throws Exception {
//
//		Field[] fields = config.getClass().getDeclaredFields();
//		for (Field fd :fields) {
//			if ("PREFIX".equals(fd.getName())) {
//				continue;
//			}
//
//			boolean flg = fd.isAccessible();
//			if (!flg) {
//				fd.setAccessible(true);
//			}
//
//			String name = fd.getName();
//			Object value = fd.get(config);
//			if (value==null) {
//				continue;
//			}
//			if (value instanceof String) {
//				bean.addInitParameter(name, value.toString());
//			}
//			else if (value instanceof String[]) {
//				bean.addInitParameter(name, StringUtils.join((String[])value, ","));
//				continue;
//			}
//			else {
//				bean.addInitParameter(name, String.valueOf(value));	
//			}
//			fd.setAccessible(flg);
//		}
//	}
}
