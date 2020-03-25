package org.young.sso.sdk.resource;

import java.util.ArrayList;
import java.util.List;

public class EdpWebapp extends BaseResource {

	private static final long serialVersionUID = 1L;
	
	/** 主键 **/
    private Long id;

    /** 租户ID **/
    private Long tenantId;
    
    /** 租户名称 **/
    private String tenantName;

    /** 应用名称 **/
    private String name;

    /** 英文名称 **/
    private String enName;
    
    /** 版本 **/
    private String version;

    /** 应用服务器地址 **/
    private String webappServer;
    
    /** 应用首页地址 **/
    private String webappHome;
    
    /** 应用退出接口 **/
    private String webappSignout;

    /** 图标 **/
    private String icon;

    /** 备注 **/
    private String note;

    /** 状态(false:0正常(默认)，true：1失效) **/
    private Boolean disabled;
    
    /** 需要登录(false:0(不需要)，true：1需要登录) **/
    private Boolean needLogin;
    
    /** 创建者ID **/
    private Long creatorId;
    
    /** 创建者名称 **/
    private String creatorName;
    
    /** 拥有菜单 **/
    private List<EdpMenu> menus;
    
	public EdpWebapp() {
		super();
		this.menus = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getWebappServer() {
		return webappServer;
	}

	public void setWebappServer(String webappServer) {
		this.webappServer = webappServer;
	}

	public String getWebappHome() {
		return webappHome;
	}

	public void setWebappHome(String webappHome) {
		this.webappHome = webappHome;
	}

	public String getWebappSignout() {
		return webappSignout;
	}

	public void setWebappSignout(String webappSignout) {
		this.webappSignout = webappSignout;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(Boolean needLogin) {
		this.needLogin = needLogin;
	}

	public List<EdpMenu> getMenus() {
		return menus;
	}

	public void setMenus(List<EdpMenu> menus) {
		this.menus = menus;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

}
