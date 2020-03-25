package org.young.sso.sdk.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.EdpResult;
import org.young.sso.sdk.resource.EdpTenant;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.EdpResult.ResultCode;
import org.young.sso.sdk.utils.SsoUtil;
/**
 * 支持切换租户
 * @author Young
 * 2019年9月19日
 */
public class ChangeTenantFilter implements Filter {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private SsoProperties ssoProperties;
	
	public ChangeTenantFilter(SsoProperties ssoProperties) {
		super();
		this.ssoProperties = ssoProperties;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info(" == ChangeTenantFilter initialized ==");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req  = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String url = req.getRequestURI();
		Object obj = req.getSession().getAttribute("loginUser");
		String tenant = req.getHeader(ConstSso.CURRENT_TENANT_KEY);
		if (obj==null 
				|| StringUtils.isBlank(tenant)
				|| Arrays.asList(ssoProperties.getNoTenantignoreUrls()).contains(url)) {
			chain.doFilter(request, response);
			return;
		}
		
		// 校验该租户是否存在于租户列表
		List<EdpTenant> list = getLoginUserTenants(req, (LoginUser)obj);
		for (EdpTenant e :list) {
			if (tenant.equals(e.getAccount())) {
				chain.doFilter(request, response);
				return;
			}
		}
		
		// 租户不存在或无权限访问
		EdpResult result = new EdpResult(tenant);
		result.setCode(ResultCode.FAIL);
		result.setMsg(String.format("'%s'租户不存在或无权限访问", tenant));
		if (SsoUtil.isAjaxRequest(req)) {
			res.setContentType(ContentType.APPLICATION_JSON.toString());
			res.getWriter().write(result.toString());
			return;
		}

		// 重定向到error页面
		SsoUtil.redirectServerError(res, ssoProperties, result);
		
	}

	@Override
	public void destroy() {

	}
	
	@SuppressWarnings("unchecked")
	public List<EdpTenant> getLoginUserTenants(HttpServletRequest req, LoginUser loginUser){
		Object tenants = req.getSession().getAttribute("loginUserTenants");
		if (tenants==null) {
			String token = SsoUtil.getTokenFormStorage(req, true);
			tenants = SsoUtil.requestLoginUserTenants(ssoProperties, token, ssoProperties.getRequestRemoteRetry());
			req.getSession().setAttribute("loginUserTenants", (ArrayList<EdpTenant>)tenants);
		}
		
		List<EdpTenant> list = tenants==null?new ArrayList<>():(ArrayList<EdpTenant>) tenants;
		if (list.isEmpty()) {
			list.add(loginUser.getTenant());
		}
		return list;
	}
	
}
