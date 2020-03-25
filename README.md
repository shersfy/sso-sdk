# sso-sdk
### SSO集成开发包
### 单点登录EDP认证中心服务器功能简介
1. RSA非对称前端加密，后端解密，支持单点登录，支持集群多节点分布式部署，包括租户管理、用户管理、角色管理、应用管理、授权等；
2. 实现原理与CAS类似，![查看序列图](http://10.32.15.89/edpglobal/edpglobal-commons-root/edit/master/edpglobal-sso-sdk/blob/master/EDP-SSO.png)
	

### 单点登录客户端应用集成
### 一、适用场景一，spring boot项目，集成较简单

#### 1. 添加依赖
```
	<dependency>
		<groupId>com.edpglobal</groupId>
		<artifactId>edpglobal-sso-sdk</artifactId>
		<version>${project.version}</version>
	</dependency>

```

#### 2. 添加注解
```
@EnableSso
public class Application

```

#### 3. 修改配置
1. edpauth-srever: SSO server地址
2. webapp-server: 应用服务器地址
3. ignoreUrls: 忽略不需要认证的请求地址

```
# sso
edpglobal:
  sso:
    enabled: true
    outer-edpauth-srever: https://preauth.edmpglobal.com
    inner-edpauth-srever: https://preauth.edmpglobal.com
    webapp-server: https://datahub.edmpglobal.com
    ignoreUrls: /sign/out,/actuator/*
    
```

#### 4. 应用session共享配置
拷贝../RedisHttpSessionConfig.java这个java文件到应用源码中
```
spring:
  session:
    store-type: redis
    timeout:
      seconds: 18000
    
```

#### 5. 获取当前登录用户
直接从session中获取
```
	@Autowired
	private SsoProperties ssoProperties;
	
	public LoginUser getLoginUser() {
		Object loginUser = getRequest().getSession().getAttribute("loginUser");
		if (loginUser==null) {
			String token = SsoUtil.getTokenFormStorage(getRequest(), true);
			loginUser = SsoUtil.requestLoginUser(ssoProperties, token);
			getRequest().getSession().setAttribute("loginUser", loginUser);
		}
		
		return loginUser==null?null:(LoginUser)loginUser;
	}
    
```

#### 6. 前端Ajax接口调用说明
注意：前端所有Ajax接口调用时，需要在请求头中统一添加参数标识'X-Requested-With'= 'XMLHttpRequest'，返回code=-2时，表示应用session失效，重新加载页面。
例如：
```
axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
axios.interceptors.response.use(data => {
	if(data.data.code == -2) {
		location.reload();
		return;
	}
	return data
}, error => {
	Message.error({
		message: '加载失败'
	})
	return Promise.reject(error)
})


```

**注意事项：**

- 使用了redis session共享机制，统一调用sdk里面的方法设值或取值，如果不使用可能会导致多个应用使用同一个session的key导致值被覆盖；
- com.gouuse.edpglobal.sso.utils.setSessionAttribute(HttpSession session, String serviceId, String name, Object value);
- com.gouuse.edpglobal.sso.utils.getSessionAttribute(HttpSession session, String serviceId, String name);


### 二、适用场景二，传统Java web项目，集成稍微复杂
参考案例： sso-client-demo
#### 1. 添加依赖或加载edpglobal-sso-sdk-xxx.jar到classpath
```
	<dependency>
		<groupId>com.edpglobal</groupId>
		<artifactId>edpglobal-sso-sdk</artifactId>
		<version>${project.version}</version>
	</dependency>

```

#### 2.1 web.xml方式配置Filter
说明：
1. 客户端应用单节点情况： sessionSharedListener默认配置com.gouuse.edpglobal.sso.listener.MemorySessionShared；
2. 客户端应用多节点情况：需要新建一个session共享处理类，实现com.gouuse.edpglobal.sso.listener.SessionSharedListener.invalidateSession(String sessionId)方法；

例：web.xml配置
```
	<filter>
		<filter-name>ssoFilter</filter-name>
		<filter-class>com.gouuse.edpglobal.sso.filter.WebSigninFilter</filter-class>
		
		<init-param>
			<param-name>enabled</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>outerEdpauthSrever</param-name>
			<param-value>https://preauth.edmpglobal.com</param-value>
		</init-param>
		<init-param>
			<param-name>webappServer</param-name>
			<param-value>https://datahub.edmpglobal.com</param-value>
		</init-param>
		<init-param>
			<param-name>ignoreUrls</param-name>
			<param-value>/sign/out,/actuator/*</param-value>
		</init-param>
		<init-param>
			<param-name>sessionSharedListener</param-name>
			<param-value>com.gouuse.edpglobal.sso.listener.MemorySessionShared</param-value>
		</init-param>
		
	</filter>
	<filter-mapping>
		<filter-name>ssoFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<filter>
		<filter-name>signoutFilter</filter-name>
		<filter-class>com.gouuse.edpglobal.sso.filter.WebSignoutFilter</filter-class>
		
		<init-param>
			<param-name>enabled</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>outerEdpauthSrever</param-name>
			<param-value>https://preauth.edmpglobal.com</param-value>
		</init-param>
		<init-param>
			<param-name>webappServer</param-name>
			<param-value>https://datahub.edmpglobal.com</param-value>
		</init-param>
		<init-param>
			<param-name>ignoreUrls</param-name>
			<param-value>/actuator/*</param-value>
		</init-param>
		<init-param>
			<param-name>sessionSharedListener</param-name>
			<param-value>com.gouuse.edpglobal.sso.listener.MemorySessionShared</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>signoutFilter</filter-name>
		<url-pattern>/sign/out</url-pattern>
	</filter-mapping>

```

#### 2.2 通过properties文件或其他方式配置
说明：
1. 客户端应用单节点情况： sessionSharedListener默认配置com.gouuse.edpglobal.sso.listener.MemorySessionShared；
2. 客户端应用多节点情况：需要新建一个session共享处理类，实现com.gouuse.edpglobal.sso.listener.SessionSharedListener.invalidateSession(String sessionId)方法；
3. 新建WebSigninFilter子类AppLoginFilter，覆写WebSigninFilter.resetSsoProperties()方法，返回配置；
4. 新建WebSignoutFilter子类AppLogoutFilter，覆写WebSigninFilter.resetSsoProperties()方法，返回配置；
5. 配置AppLoginFilter和AppLogoutFilter使之生效(web.xml或使用@WebFilter);
6. resetSsoProperties()方法和web.xml同时配置时，resetSsoProperties()配置生效，web.xml参数配置无效

例：
```
public class AppLoginFilter extends WebSigninFilter {

	@Override
	public SsoProperties resetSsoProperties() {
		SsoProperties ssoProperties = new SsoProperties(boolean enabled, String outerEdpauthSrever, String webappServer);
		ssoProperties.setIgnoreUrls(ignoreUrls);
		return ssoProperties;
	}
	

}

public class AppLogoutFilter extends WebSignoutFilter {

	@Override
	public SsoProperties resetSsoProperties() {
		SsoProperties ssoProperties = new SsoProperties(boolean enabled, String outerEdpauthSrever, String webappServer);
		ssoProperties.setIgnoreUrls(ignoreUrls);
		return ssoProperties;
	}
	

}

```

#### 3. 获取当前登录用户
参考：场景一步骤5



### 三、适用场景三，非Java语言项目需要按照以下流程实现集成，相比前面两种场景复杂些
* EDP服务器地址配置: var edpauthSrever=https://preauth.edmpglobal.com
* 应用服务器地址配置: var webappServer=https://datahub.edmpglobal.com

#### 1. 登录流程，未登录状态引导重定向或跳转到EDP单点登录服务器
###### 1. 跳转到：https://preauth.edmpglobal.com/login?`tenant`=NH00001&`webapp`=http%3A%2F%2F10.12.51.182%3A8880%2F
###### 2. webapp应用服务器地址进行URL编码 URLEncoder.encode(url, "UTF-8")
###### 3. 登录成功EDP服务器重定向到应用首页https://datahub.edmpglobal.com?`appId`=1&`tenant`=NH00001&`_t_`=xxx4324&`_k_`=3eqwedd
###### 4. 获取参数"`_t_`"和"`_k_`"以及appId, tenant租户（或公司）编号, 拦截获取到参数以后需要再次应用内重定向, 去掉浏览器地址栏中的各参数项
* "`_t_`"加密票据信息；
* "`_k_`"校验key，校验后失效；
* appId 应用ID，已在【EDP认证中心】注册的应用ID；
* tenant 租户（或公司）编号，EDP认证中心应用所属租户；

###### 5. 向EDP服务器发送http请求进行校验

###### 5.1. POST接口：edpauthSrever+"/login/validate"
###### 5.2. Content-Type: application/x-www-form-urlencoded
###### 5.3. 参数：data=字符串
```
// 说明：
// t=_t_(步骤4获取);
// k=_k_(步骤4获取); 
// appId=appId(步骤4获取); 
// webappServer=https://datahub.edmpglobal.com (注册应用服务器地址)
// webappSession=003dd093-0c43-4696-baea-d632e6d5f0c4 (注册应用服务登录sessionId)

// 1.序列化为json字符串(强调最终参数类型是字符串)
data={"k":"f3e786b669ee83478411f207f83d0db5","t":"3746c0a528cdcfc66effbd04","appId":1, "webappServer":"https://datahub.edmpglobal.com","webappSession":"003dd093-0c43-4696-baea-d632e6d5f0c4"}


```
###### 5.4. 返回值
```
// 校验通过：
{"code":0,"msg":""}

// 校验失败：
{"code":2000006,"msg":"登录错误: 权限不足, 没有权限访问'https://datahub.edmpglobal.com"}

// 说明：
// code表示校验结果，0校验成功，非0表示校验失败，msg提示错误信息

```
###### 5.5. 校验接口本身在没有正确响应的情况(如网络不稳定)，接口调用http状态非200情况可适当重试，间隔1秒重试次数5次左右

###### 6. 判定是否校验成功

###### 6.1. 校验成功，存储票据信息，重定向或跳转到应用首页
###### 6.1.1. "`_t_`"的值同时存储cookie和session, cookie失效时间设置60秒，cookie失效后取session中的"`_t_`"调用EDP服务器续租刷新"`_t_`"的值（参考步骤7），同时更新cookie和session中的值；
###### 6.1.2. cookie域为应用域名，path为"/"，生命值60秒；同时根据sessionId，缓存对应的session对象，退出时会根据sessionId找到对应的session进行销毁；
###### 6.1.3. 登录成功重定向或跳转到应用首页：https://datahub.edmpglobal.com

###### 6.2. 最终判定校验失败后，重定向或跳转到【EDP认证中心】服务器的错误页面
###### 6.2.1. 跳转地址：https://preauth.edmpglobal.com/error?code=%s&msg=%s
###### 6.2.2. 参数：code错误代码，msg提示错误信息（通过UTF8编码URLEncoder.encode(url, "UTF-8")）

###### 6.3. 登录流程完毕

###### 7. 续租"`_t_`"的值（可选非必需）
###### 7.1 POST接口：edpauthSrever+"/login/renew"
###### 7.2 Content-Type: application/x-www-form-urlencoded
###### 7.3 参数：data字符串
```
// 说明：
// t=_t_(从session获取"_t_")
// webappServer=APP服务配置
// remoteAddr=从request获取remoteAddress

// 1.序列化为json字符串(强调最终参数类型是字符串)
data={"k":"f3e786b669ee83478411f207f83d0db5","webappServer":"https://datahub.edmpglobal.com","remoteAddr":"127.0.0.1"}

```
###### 7.4 返回值
```
// 续租成功：
{"code":0,"model":"6155f4b89b68f5238de8cb6", "msg":""}


// 续租失败：
{"code":1,"msg":"授权续租失败, 授权已过期或不存在"}


// 说明：
// code表示续租结果，0续租成功，model表示新"_t_"的值；非0表示续租失败，msg提示错误信息

```
###### 7.5. 续租接口本身在没有正确响应的情况(如网络不稳定)，接口调用http状态非200情况可适当重试，间隔1秒重试次数5次左右
###### 7.6. 续租成功更新cookie和session中的值
###### 7.7. 续租失败重定向或跳转到【EDP认证中心】服务器登录页面：https://preauth.edmpglobal.com/login?webapp=http%3A%2F%2F10.12.51.182%3A8880%2F

#### 2. 退出登录流程

###### 1. 直接调用EDP服务器端退出操作，退出地址：edpauthSrever+"/sign/out"；
###### 2. EDP服务器将向所有登录的应用服务器异步发送http退出登录接口调用， 请求参数为应用客户端session id， 参数名"`_sid_`"；

###### 3. 应用服务器需要提供POST退出接口或拦截请求：webappServer+"/sign/out"；
###### 3.1. 应用服务器拦截到"/sign/out"请求后，从请求中获取session id参数request.getParam("`_sid_`")；
###### 3.2. 应用服务器根据"`_sid_`"的session id找到对应的session进行销毁；

###### 4. 退出流程完毕

### 四、适用场景四，纯前端页面项目
参考：http://10.32.15.89/edpglobal/haimihui-apps/tree/master/edpglobal-sso-sdk


#### 1. 登录流程
###### 1.1 认证中心添加APP应用
###### 1.2 从认证中心首页点击应用跳转到应用首页
例：
```
http://haimihui.apps-test.edmpglobal.com/e-contract/index.html#/organization?_t_=6affd16bf402c7ded93724430a661f4400f65f2f4691013e7956aecddd49c15e31da1fcbb0a518d5df9adf7e5512cf90ce7d1343a697b178cb972cd9471fe26350ed8b2bfb571d0a7ff6eaf885ee4df768380e46f1c74536c829e6aceee0d3255997c364f19cd7ce51641d52df8cb1d44824aea49ca16d4f24c4156664ea9c8de32baec2445c03572cc2b5fb2f52175fa47ee3a703051b7d7abbf882e8b36287&_k_=7325e470944edcf5cbb432f7c4661dde8273dd3e52cff1be5ec995cc736e2c6b2a83295f4863256390a6b57987e64c09&appId=130

```
###### 1.3 应用首页截取URL参数
 例：
```
// 截取参数：
_k_ = "7325e470944edcf5cbb432f7c4661dde8273dd3e52cff1be5ec995cc736e2c6b2a83295f4863256390a6b57987e64c09";
_t_ = "6affd16bf402c7ded93724430a661f4400f65f2f4691013e7956aecddd49c15e31da1fcbb0a518d5df9adf7e5512cf90ce7d1343a697b178cb972cd9471fe26350ed8b2bfb571d0a7ff6eaf885ee4df768380e46f1c74536c829e6aceee0d3255997c364f19cd7ce51641d52df8cb1d44824aea49ca16d4f24c4156664ea9c8de32baec2445c03572cc2b5fb2f52175fa47ee3a703051b7d7abbf882e8b36287";
appId = 130;
webappServer = "http://haimihui.apps-test.edmpglobal.com";
webappHome   = "http://haimihui.apps-test.edmpglobal.com/e-contract/index.html#/organization";

```

###### 1.4 调用登录认证API
```
// 网关服务
gatewayServer = "https://gateway2g.edmpglobal.com";

// POST请求
apiUrl = gatewayServer + "/openapi/login/validate";
params = {
	_k_: "上一步骤截取（注意：该参数使用过一次后失效）",
	_t_: "上一步骤截取",
	appId: "上一步骤截取",
	webappServer: "上一步骤截取"
}

// 返回结果。处理并做好缓存，之后业务接口请求需要用到以下结果
状态：第一层code=0且第二层code=0时，登录认证成功；其它情况均失败，执行步骤1.6;
业务接口授权appId: appId;
业务接口授权secretKey: secretKey;
登录用户信息: loginUser
认证中心地址：edpauthServer

例：
res = {
    "code": 0,
    "msg": "",
    "model": [
        {
            "result": {
                "code": 0,
                "model": {
                    "loginUser": {
                        "note": "系统内置数据",
                        "idCard": "00000000000000000X",
                        "roles": {
                            "0": {
                                "mine": false,
                                "note": "系统内置数据",
                                "code": "edp_admin",
                                "editable": false,
                                "name": "EDP全局管理员",
                                "checked": false,
                                "disabled": false,
                                "id": 1,
                                "updateTimestamp": 0,
                                "createTimestamp": 0
                            }
                        },
                        "globalId": "EDP0000001",
                        "creatorId": null,
                        "loginLog": null,
                        "userId": 1,
                        "realName": "EDP管理员",
                        "edpMenus": null,
                        "phone": "01000000001",
                        "tenantId": 1,
                        "rolesText": "EDP全局管理员",
                        "userType": "EDP_ADMIN",
                        "email": "edpadmin@xmi01.com",
                        "tenant": {
                            "code3": null,
                            "note": "系统内置数据",
                            "code2": null,
                            "code1": null,
                            "creatorId": 1,
                            "creatorName": null,
                            "type": "edpglobal",
                            "contactTel": "01000000001",
                            "homePage": "#",
                            "createTimestamp": 1535731200000,
                            "enName": "edpglobal",
                            "checked": false,
                            "logo": "/image/201812211454507182002575395ce4ac5ab2dabd895320842.jpg",
                            "id": 1,
                            "email": "edpadmin@xmi01.com",
                            "licence": "/image/201812211455177753d7c708631484e6fb3c5c33f0a5d5346.jpg",
                            "contactName": "edpadmin",
                            "fullName": "EDP系统",
                            "businessScope": "edpglobal",
                            "updateTimestamp": 1545375407000,
                            "parentId": 0,
                            "background": "/image/20181221145209615a0d09a11446a4eedb3354fcb4f3c2b5e.jpg",
                            "webapps": {},
                            "shortName": "EDP",
                            "account": "edpglobal",
                            "status": 0
                        },
                        "username": "edpadmin",
                        "status": 0
                    },
                    "secretKey": "kWIzF3H0uby9GcgVX8qle2iDdacsNXNfqWyXZ938X6JXd+MUceITDxWIbGmtusml",
                    "appId": "81346ee636b8e64d6a1d590fbb7e1396d3bb0811e18401dad329c5c8a986fcd7116f5d06738ce5f1a616d9a42b99eaf8",
                    "edpauthServer ": "http://10.32.15.101:31004"
                }
            }
        }
    ],
    "traceId": "438d29708669339f"
}

```

###### 1.5 调用业务API

```
// 请求header里面携带如下参数请求业务接口
header.appId = appId;
header.secretKey = secretKey;

```
###### 1.6 调用业务API授权失效的情况
前端页面重定向

```
// 1. 业务接口返回code==0，成功；
// 2.1 业务接口返回code==4001，secretKey失效，重定向到以下地址重新登录认证；
location.href = edpauthServer  + "/goto?webapp=" + webappHome;
// 2.2 业务接口返回code==其它，提示错误信息

```

#### 2. 退出登录流程
调用【认证中心】退出

```
// 步骤1.4返回结果
window.location = edpauthServer  + "/sign/out";

```

### 五、应用与EDP认证中心交互
###### 1. 获取已登录用户信息

- 1.1 POST接口：edpauthSrever+"/resource/webapp/user"
- 1.2 参数：t=从应用session中获取

```
// 返回结果：
// {code:0成功或非0失败, msg: 错误提示信息, model: 用户信息}
{
  "code": 0,
  "msg": "",
  "model": {
    "userId": 1,
    "tenantId": 1,
    "username": "edpadmin",
    "realName": "EDP管理员",
    "phone": "13191979257",
    "email": "edpadmin@gouuse.cn",
    "globalId": "edpadmin",
    "idCard": null,
    "note": null,
    "status": 0,
    "userType": "EDP_ADMIN",
    "loginLog": {
      "loginId": 84,
      "loginLang": "zh_CN",
      "loginUrl": "http://localhost:9090/sign/in",
      "loginTimestamp": 1545811517174
    },
    "tenant": {
      "id": 1,
      "parentId": 0,
      "account": "edpglobal",
      "enName": "edpglobal",
      "shortName": "EDP",
      "fullName": "EDP系统",
      "type": "edpglobal",
      "businessScope": "edpglobal",
      "contactName": "edpadmin",
      "contactTel": "13843838438",
      "email": "edpadmin@gouuse.cn",
      "homePage": "#",
      "licence": "https://dfs.edmpglobal.com/fs/image/201812211455177753d7c708631484e6fb3c5c33f0a5d5346.jpg",
      "background": "https://dfs.edmpglobal.com/fs/image/20181221145209615a0d09a11446a4eedb3354fcb4f3c2b5e.jpg",
      "logo": "https://dfs.edmpglobal.com/fs/image/201812211454507182002575395ce4ac5ab2dabd895320842.jpg",
      "code1": null,
      "code2": null,
      "code3": null,
      "note": null,
      "status": 0,
      "webapps": []
    },
    "roles": [
      {
        "id": 1,
        "name": "edp_admin",
        "note": "EDP全局管理员",
        "editable": false,
        "mine": true
      },
      {
        "id": 2,
        "name": "tenant_admin",
        "note": "租户管理员",
        "editable": false,
        "mine": false
      },
      {
        "id": 3,
        "name": "basic_user",
        "note": "普通用户",
        "editable": false,
        "mine": false
      }
    ],
    "edpMenus": [
      {
        "id": 1,
        "parentId": 0,
        "appName": null,
        "name": "用户中心",
        "enName": "Users",
        "url": "/system/userManage",
        "icon": "fa fa-user",
        "sortNum": 1,
        "disabled": false,
        "operation": "View",
        "note": null
      },
      {
        "id": 2,
        "parentId": 1,
        "appName": null,
        "name": "租户管理",
        "enName": "Tenants Management",
        "url": "/system/tenantManage",
        "icon": "fa fa-group",
        "sortNum": 1,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 3,
        "parentId": 1,
        "appName": null,
        "name": "用户管理",
        "enName": "Users Management",
        "url": "/system/userManage",
        "icon": "fa fa-user",
        "sortNum": 2,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 4,
        "parentId": 1,
        "appName": null,
        "name": "角色管理",
        "enName": "Roles Management",
        "url": "/system/roleManage",
        "icon": "fa fa-vcard",
        "sortNum": 3,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 5,
        "parentId": 1,
        "appName": null,
        "name": "合同管理",
        "enName": "Agreements Management",
        "url": "/system/agreements",
        "icon": "fa fa-vcard",
        "sortNum": 4,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 6,
        "parentId": 0,
        "appName": null,
        "name": "资源中心",
        "enName": "Resources",
        "url": null,
        "icon": "fa fa-cogs",
        "sortNum": 2,
        "disabled": false,
        "operation": "View",
        "note": null
      },
      {
        "id": 7,
        "parentId": 6,
        "appName": null,
        "name": "API服务",
        "enName": "API Services",
        "url": "/system/navbarManage",
        "icon": "fa fa-bars",
        "sortNum": 1,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 8,
        "parentId": 6,
        "appName": null,
        "name": "访问授权",
        "enName": "Access Authorization",
        "url": "/system/navbarManage",
        "icon": "fa fa-bars",
        "sortNum": 2,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 9,
        "parentId": 0,
        "appName": null,
        "name": "应用集成",
        "enName": "Applications",
        "url": null,
        "icon": "fa fa-cogs",
        "sortNum": 3,
        "disabled": false,
        "operation": "View",
        "note": null
      },
      {
        "id": 10,
        "parentId": 9,
        "appName": null,
        "name": "应用管理",
        "enName": "Applications Management",
        "url": "/system/navbarManage",
        "icon": "fa fa-bars",
        "sortNum": 1,
        "disabled": false,
        "operation": "View;Add;Delete;Update",
        "note": null
      },
      {
        "id": 11,
        "parentId": 9,
        "appName": null,
        "name": "菜单管理",
        "enName": "Menu Management",
        "url": "/system/navbarManage",
        "icon": "fa fa-bars",
        "sortNum": 2,
        "disabled": false,
        "operation": "View;Add;Delete;Update;EnableOrDisable",
        "note": null
      }
    ]
  }
}

```
