package org.young.sso.sdk.resource;

public abstract class BaseResource extends BaseBean{

	private static final long serialVersionUID = 1L;
	
	private Long createTimestamp;
	
	private Long updateTimestamp;
    
	/**
     * 是否被选中
     */
    private boolean checked;

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Long getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Long updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
