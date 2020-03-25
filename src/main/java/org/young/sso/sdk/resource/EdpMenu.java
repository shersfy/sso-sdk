package org.young.sso.sdk.resource;

import java.util.ArrayList;
import java.util.List;

public class EdpMenu extends BaseResource {
	
	private static final long serialVersionUID = 1L;

	/** 主键 **/
    private Long id;

    /** 父级ID **/
    private Long parentId;

    /** 应用名称 **/
    private String appName;
    
    /** 菜单名称 **/
    private String name;

    /** 英文名称 **/
    private String enName;

    /** 链接地址 **/
    private String url;

    /** 图标 **/
    private String icon;

    /** 菜单排序 **/
    private Integer sortNum;

    /** 状态(false:正常(默认)，true：失效) **/
    private Boolean disabled;

    /** 备注 **/
    private String note;
    
    /** 子菜单 **/
    private List<EdpMenu> children;
    
    /** 菜单操作 **/
    private List<EdpMenuOperation> operations;
    
    /** 下拉菜单级别 **/
    private List<Object> dropdown;
    
	public EdpMenu() {
		super();
		this.dropdown = new ArrayList<>();
		this.children = new ArrayList<>();
		this.operations = new ArrayList<>();
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

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<EdpMenu> getChildren() {
		return children;
	}

	public void setChildren(List<EdpMenu> children) {
		this.children = children;
	}

	public List<EdpMenuOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<EdpMenuOperation> operations) {
		this.operations = operations;
	}

	public List<Object> getDropdown() {
		return dropdown;
	}

	public void setDropdown(List<Object> dropdown) {
		this.dropdown = dropdown;
	}

}