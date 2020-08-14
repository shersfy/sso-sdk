package org.young.sso.sdk.autoconfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.young.sso.sdk.autoprop.SsoProperties;

/***
 * 开启SSO单点登录功能
 * @author pengy
 * @date 2018年11月10日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SsoAutoConfiguration.class)
@EnableConfigurationProperties(SsoProperties.class)
public @interface EnableSso {

}
