# ford-search

ford-search demo整合高德地图搜索、OAuth2.0与github整合，redis zset内容排行排序等
--
# 项目启动问题：
项目启动后，通过github登录后，有时会出现登录失败sslException.偶发情况。可多尝试几次。,百度说是 jdk 环境问题。可尝试升级jdk 到1.8.0_151
,修改Java\jre\lib\security\java.security文件。
crypto.policy=unlimited
# 路径
###### http:localhost:8080/index
###### http:localhost:8080/login
###### http://localhost:8080/search?keywords=xxx
###### http://localhost:8080/showAll
