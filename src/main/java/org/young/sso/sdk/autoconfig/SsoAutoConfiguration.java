package org.young.sso.sdk.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.young.sso.sdk.autoprop.SsoProperties;
import org.young.sso.sdk.filter.WebSigninFilter;
import org.young.sso.sdk.filter.WebSignoutFilter;

@Configuration
@ServletComponentScan(basePackageClasses = {WebSigninFilter.class, WebSignoutFilter.class})
@ConditionalOnProperty(prefix=SsoProperties.PREFIX, value="enabled", havingValue="true")
public class SsoAutoConfiguration {
	
}
