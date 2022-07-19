# gmall-0420
谷粒商城系统

**day02课堂笔记**

https://github.com/joedyli/gmall-0420.git

以pms为例运行工程：
	1.pom.xml
		以自己的父工程作为父工程（spring-boot-starter-parent）
		删除properties标签中定义的依赖版本号
		删除dependencyManagement（springcloud springcloud-alibaba）
		需要引入一些依赖：gmall-common mysql驱动 mybatis-plus依赖
	2.配置文件：
		bootstrap.yml
			spring.application.name
			spring.cloud.nacos.config.server-addr/namespace/group/file-extension
		application.yml
			server.port
			spring.cloud.nacos.discovery.server-addr
			spring.cloud.sentinel.transport.dashboard
			spring.cloud.sentinel.transport.port
			spring.zipkin.base-url
			spring.zipkin.discovery-client-enabled
			spring.zipkin.sender.type
			spring.sleuth.sampler.probability
			spring.datasource.driver-class-name/url/username/password
			spring.redis.url
			feign.sentinel.enabled
			mybatis-plus.mapper-locations/type-aliases-package/global-config.db-config.id-type=auto
		mybatis分页插件配置
			参照官方文档
	3.注解：
		启动类上：@EnableFeignClients @EnableSwagger2 @MapperScan() @RefreshScope
	4.逆向工程生成代码：
		gmall-generator：application.yml  generator.properties  

CORS
	跨域场景：http://api.gmall.com:80/pms/brand
		协议不同 一级域名不同 二级域名或者三级域名不同 端口号不同
	跨域不一定总是有跨域问题
	跨域问题：本质是浏览器对ajax的一种限制
	同源策略
	解决方案：
		1.jsonp:需要前后端开发人员协同，只能解决get请求的跨域问题
		2.nginx:反向代理不跨域；通过配置的方式添加跨域所需头信息(devops)
		3.cors:跨域w3c规范

	cors原理：发送两次请求
		预检请求：OPTIONS
			origin
			Access-Control-Request-Method
			Access-Control-Request-Headers

			Access-Control-Allow-origin
			Access-control-Allow-credentials
			Access-Control-Allow-Methods
			Access-control-Allow-Headers
		真正请求

	spring提供了一套过滤器，只需要把对应的过滤器配置到网关中


spring:
    cloud:
        nacos:
            discovery:
                server-addr: localhost:8848
        sentinel:
            transport:
                dashboard: localhost:8080
                port: 8719
    zipkin:
        base-url: http://localhost:9411
        discovery-client-enabled: false
        sender:
            type: web
    sleuth:
        sampler:
            probability: 1
    datasource:
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/guli_sms?useUnicode=true&characterEncoding=UTF-8&useSSL=false
        username: root
        password: root
    redis:
        host: 192.168.188.129









