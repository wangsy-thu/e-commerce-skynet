# e-commerce-skynet

#### 介绍
基于SpringCloud/Alibaba微服务体系框架的电商应用

#### 软件架构
软件架构说明
该系统采用微服务架构，

#### 安装教程

1. 解压缩 e-commerce-middleware 压缩包
2. 导入 nacos_config 数据库
3. 以 standalone 模式启动 Nacos 组件
4. 按照启动脚本内容启动 zipkin jar包
5. 按照启动脚本内容启动 sentinel jar包
6. 运行 nginx.exe 
7. 启动服务中所有 springboot 工程

#### 使用说明

##### 监控网址
1. SpringBootAdmin监控地址：[SpringBootAdmin](http://localhost:7001/e-commerce-admin/applications) 账号：WangSongyao 密码：88888888
2. Sentinel资源限流管理平台：[Sentinel Dashboard](http://localhost:8333/#/dashboard) 账号：sentinel 密码：sentinel
3. Nacos注册中心控制台：[Nacos](http://localhost:8848/nacos) 账号：Nacos 密码：Nacos
4. Zipkin链路追踪可视化平台：[Zipkin](http://localhost:9411/zipkin/)
5. Elasic Search操作平台 Kibana：[Kibana](http://123.56.120.188:5601/app/home#/)
##### 基本功能
1. 后台管理系统：[Admin](http://localhost:8001)账号：admin 密码：123456
2. 电商基本功能: [Gulimall](http://gulimall.com) 账号:13998358698 密码：123456

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
