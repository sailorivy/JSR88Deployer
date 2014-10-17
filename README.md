JSR88Deployer
=============

JSR-88是Java EE Application Deployment，定义了往Java EE应用服务器上部署应用的统一模型和API。利用JSR-88的部署工具可以采用统一的方式往不同的Java EE应用服务器上部署应用，而不用了解众多产品之间的诸多差异，从而简化部署过程。
这个Eclipse工程目前支持JBoss 7.1.1和GlassFish 3。代码包含如下文件：
    o  app.properties：指定应用存放路径（app.path）、部署计划存放路径（plan.path）、应用类型（app.type）。其中应用类型是小写的web、ejb、rar或ear
    o  dm.properties：指定应用服务器的DeploymentManager相关信息。格式为“filed.product”，filed可以是dm.jar（暴露DeploymentFactory接口的Jar包）、dm.uri（应用服务器指定的URI）、dm.userName（管理用户用户名）、dm.password（管理用户密码）；product用来区分不同的应用服务器产品，示例代码要求小写，代码运行时可以指定这里声明的产品名（不指定的话取jboss）
    o  AppInfo：app.properties的包装类
    o  DMProperty：dm.properties的包装类
    o  DeploymentHandler：获取DeploymentManager、封装DeploymentHandler的操作
    o  JSR88Deployer：测试主类
如果要支持其他Java EE应用服务器产品，根据dm.properties的要求进行修改，设置classpath，运行时指定产品名称就可以了。