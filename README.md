# sso-sdk
### SSO集成开发包

### sso server

### sso client app

1. 前后端不分离部署(session模式)

   validate校验：从sso server重定向到sso client的url中获取ST和request key，异步调用sso server的validate API校验和注册sso client app，校验通过后返回用户信息给sso client；

   用户信息：用户登录校验通过后，用户信息存储在客户端服务器session，由后端服务管理session(如session共享、session有效期等)；

   登录判断：从session中获取用户信息，存在即为已登录，否则未登录，重定向到sso server登录页面；

2. 前后端分离部署(auth2模式)

   用户信息：用户登录校验通过后，用户信息存储在客户端服务器，由后端服务维护用户信息(如存储在redis)，并生成可信任的令牌token返回前端；

   登录判断：从sso client前端请求header中获取令牌token，sso client后端服务器校验token的有效性，有效即为已完成登录认证，且进行token续租；否则无效token，返回未登录状态码，由sso client前端完成重定向到sso server登录页面；