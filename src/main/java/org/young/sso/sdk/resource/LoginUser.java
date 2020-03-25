package org.young.sso.sdk.resource;

import java.util.List;

public class LoginUser extends BaseBean {

	private static final long serialVersionUID = 1L;

	/** 登录用户ID **/
	private Long userId;
	
    /** 租户ID **/
    private Long tenantId;
	
	/** 登录用户名 **/
	private String username;
	
	/** 真实姓名 **/
    private String realName;

    /** 电话 **/
    private String phone;

    /** 邮箱 **/
    private String email;
    
    /** 员工编号 **/
    private String empCode;

    /** 企业微信号 **/
    private String wechatCom;
    
    /** 全局ID **/
    private String globalId;

    /** 身份证号 **/
    private String idCard;

    /** 备注 **/
    private String note;

    /** 状态志(0:正常(默认)，1：失效) **/
    private Integer status;
    
    /** 创建者ID **/
    private Long creatorId;
    
    /** 用户类型 **/
    private UserType userType;
    
    /** 登录日志 **/
    private EdpLoginLog loginLog;
	
	/** 所属租户 **/
	private EdpTenant tenant;
	
	/** 角色 **/
	private String rolesText;
	
	/** 拥有角色 **/
	private List<EdpRole> roles;
	
	/** EDP admin菜单 **/
	private List<EdpMenu> edpMenus;
	
	public LoginUser() {
		super();
	}

	public LoginUser(Long userId, String username) {
		super();
		this.userId = userId;
		this.username = username;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getWechatCom() {
		return wechatCom;
	}

	public void setWechatCom(String wechatCom) {
		this.wechatCom = wechatCom;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public EdpLoginLog getLoginLog() {
		return loginLog;
	}

	public void setLoginLog(EdpLoginLog loginLog) {
		this.loginLog = loginLog;
	}

	public EdpTenant getTenant() {
		return tenant;
	}

	public void setTenant(EdpTenant tenant) {
		this.tenant = tenant;
	}

	public String getRolesText() {
		return rolesText;
	}

	public void setRolesText(String rolesText) {
		this.rolesText = rolesText;
	}

	public List<EdpRole> getRoles() {
		return roles;
	}

	public void setRoles(List<EdpRole> roles) {
		this.roles = roles;
	}

	public List<EdpMenu> getEdpMenus() {
		return edpMenus;
	}

	public void setEdpMenus(List<EdpMenu> edpMenus) {
		this.edpMenus = edpMenus;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	
}
