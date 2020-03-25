package org.young.sso.sdk.resource;

public class EdpRole extends BaseResource {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 **/
    private Long id;

    /** 角色代码 **/
    private String code;
    
    /** 角色名称 **/
    private String name;

    /** 备注 **/
    private String note;

    /** 状态志(0:不可编辑，1：可编辑) **/
    private Boolean editable;
    
    /** 状态(false:0正常(默认)，true：1失效) **/
    private Boolean disabled;
    
    /** 是否当前用户的角色 **/
	private boolean mine;
    
    public EdpRole() {
		super();
	}

	public EdpRole(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isMine() {
		return mine;
	}

	public void setMine(boolean mine) {
		this.mine = mine;
	}
	

	
}