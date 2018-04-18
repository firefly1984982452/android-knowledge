(This document is encoded with UTF-8)
功能说明
本项目是云片短信发送接口的Java演示项目,基于Apache HttpClient v4.3.

目录结构
1. /src 示例代码目录
2. /pom.xml maven配置
3. /lib 依赖jar包 （Maven项目不需要）

使用提示
1. 可执行代码在/中
2. 需将apikey和电话号码修改成自己的。
3. 非maven用户需自行将/lib目录中的jar包加入依赖。

常见问题提示
1. 代码打开是乱码
    项目代码格式为UTF-8，请注意您的开发环境设置。
2. 发送返回“非法的apikey”
    请用您注册的账号登录云片官网(http://www.yunpian.com)，在账户中心首页查看您自己的apikey，检查是否和代码中设置的一致。
3. 发送返回“IP没有权限”
    请用您的注册的账号登录云片官网(http://www.yunpian.com)，在“账户设置”的“IP白名单”设置中将自己测试机的外网IP加进去。
    如不清楚是哪个IP，可以用错误提示信息里显示的IP，或访问http://www.ip138.com/查看自己的IP




