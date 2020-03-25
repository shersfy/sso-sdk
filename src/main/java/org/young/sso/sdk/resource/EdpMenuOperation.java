package org.young.sso.sdk.resource;

public class EdpMenuOperation extends BaseResource {
	
	private static final long serialVersionUID = 1L;

	/** 操作编码 **/
    private String id;
    
    /** 操作编码 **/
    private String code;

    /** 操作名称 **/
    private String name;

    /** 状态志(0:不可编辑，1：可编辑) **/
    private Boolean editable;
    
    public EdpMenuOperation() {
		super();
	}
    
    public EdpMenuOperation(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
    
	public EdpMenuOperation(String id, String code, String name) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	
}