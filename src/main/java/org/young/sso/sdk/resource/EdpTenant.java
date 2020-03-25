package org.young.sso.sdk.resource;

import java.util.ArrayList;
import java.util.List;

public class EdpTenant extends BaseResource {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 租户ID **/
    private Long id;

    /** 父级租户ID **/
    private Long parentId;

    /** 注册账户 **/
    private String account;

    /** 英文名称 **/
    private String enName;

    /** 租户名称(简称) **/
    private String shortName;

    /** 租户名称(全称) **/
    private String fullName;

    /** 租户类型 **/
    private String type;

    /** 经营范围 **/
    private String businessScope;

    /** 联系人 **/
    private String contactName;

    /** 联系电话 **/
    private String contactTel;

    /** 邮箱 **/
    private String email;

    /** 首页地址 **/
    private String homePage;
    
    /** 营业执照 **/
    private String licence;

    /** 背景图片 **/
    private String background;

    /** 商标存储路径(大中小) **/
    private String logo;

    /** 代码1 **/
    private String code1;

    /** 代码2 **/
    private String code2;

    /** 代码3 **/
    private String code3;

    /** 备注 **/
    private String note;

    /** 状态志(0:正常(默认)，1：失效) **/
    private Integer status;
    
    /** 创建者ID **/
    private Long creatorId;
    
    /** 创建者名称 **/
    private String creatorName;
    
    /** 拥有应用 **/
    private List<EdpWebapp> webapps;
    
	public EdpTenant() {
		super();
		this.webapps = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusinessScope() {
		return businessScope;
	}

	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public String getCode3() {
		return code3;
	}

	public void setCode3(String code3) {
		this.code3 = code3;
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

	public List<EdpWebapp> getWebapps() {
		return webapps;
	}

	public void setWebapps(List<EdpWebapp> webapps) {
		this.webapps = webapps;
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