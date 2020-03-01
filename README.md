# ford-search
#v1.1（已与master同步）
##ford-search demo整合高德地图搜索、SpringSecurity与github第三方登录整合，使用H2内存数据库进行存储，配合使用thymleaf进行页面展示
### 项目启动问题：
项目启动后，通过github登录后，有时会出现登录失败sslException.偶发情况。原因是因为github服务器在授权认证期间有时存在关闭连接的情况。多尝试登录几次即可。
百度说是 更改jdk可能减少问题出现次数。可尝试升级jdk 到1.8.0_151,修改Java\jre\lib\security\java.security文件。
crypto.policy=unlimited

##v1.1路径
#### http://localhost:8080/h2-console（H2数据库控制台）
#### http://localhost:8080/index（首页）
#### http://localhost:8080/login（登录界面）
#### http://localhost:8080/ford/search?keywords=妙栏小区(浏览器输入此链接进行查询，返回查询结果界面，keywords替换为自己要查的内容)
#### http://localhost:8080/ford/searchHistory(浏览器输入此链接进行查询历史最频繁的查询记录，返回结果界面)



#v1.0（已弃用，细节可查看v1.0分支）
##ford-search demo整合高德地图搜索、OAuth2.0与github整合，redis zset内容排行排序等
##v1.0路径
#### http:localhost:8080/index
#### http:localhost:8080/login
#### http://localhost:8080/search?keywords=xxx
####http://localhost:8080/showAll