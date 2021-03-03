## 手写一个简易的spring框架
### IOC
- BeanDefinition：定义基本的类信息
- BeanDefinitionReader：读取配置信息
- BeanDefinitionRegistry：bean容器、切面管理、bean管理
- BeanFactory：初始化、实例化bean
- BeanInitializer：
    1. 读取配置
    2. 加载bean
    3. 解析切面
    4. 扫描注解并加载bean
    
### AOP
- AspectPointcut AspectDefinition:定义基本的切点、切面信息
- JdkProxyFactory JdkInvocationHandler：JDK动态代理
- CglibProxyFactory CglibMethodInterceptor：Cglib动态代理


