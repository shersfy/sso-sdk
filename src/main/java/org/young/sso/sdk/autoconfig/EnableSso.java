package org.young.sso.sdk.autoconfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/***
 * 开启SSO单点登录功能
 * @author pengy
 * @date 2018年11月10日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SsoAutoConfiguration.class)
@Documented
@Inherited
@EnableConfigurationProperties(SsoProperties.class)
public @interface EnableSso {

}
