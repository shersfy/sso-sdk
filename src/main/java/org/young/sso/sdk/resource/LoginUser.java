package org.young.sso.sdk.resource;

public class LoginUser extends BaseBean {

	private static final long serialVersionUID = 1L;

	/** 登录用户ID **/
	private Long userId;
	
	/** 登录用户名 **/
	private String username;
	
	/** 真实姓名 **/
    private String realName;
	
    /** 电话 **/
    private String phone;

    /** 邮箱 **/
    private String email;

    /** 状态志(0:正常(默认)，1：失效) **/
    private Integer status;
   
	
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
