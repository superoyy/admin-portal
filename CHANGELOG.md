# 2019.10.12
依赖 springboot 2.1.19
调整application.yml,对mysql的配置调整
增加ApplicationConfig,用于定义BEAN
增加ApplicationInit,用于应用启动后完成初始动作

#  2019.10.14
退回到springboot 1.5.3,程序可以运行。貌似对Hibernate的支持有问题，构建不了Query,Parameter设置越界，暂时没有解决办法。
有可能是Application的包位置有问题，导致EntityBean 扫描不进去。

#  2019.10.15
Dependent on springboot 2.1.9 and dukla-base 1.0.1-SNAPSHOT
upgrade to 1.0.1-SNAPSHOT
the reason of can't startup,because of the Class Application locate at package com.dukla.portal.admin,must locate at com.dukla
the springframework can't scan and instance the entitybeans,even if set @ComponentScan.why?
